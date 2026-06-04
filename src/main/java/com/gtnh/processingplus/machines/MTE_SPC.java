package com.gtnh.processingplus.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.Muffler;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.items.GTNHPPItems;
import com.gtnh.processingplus.machines.spc.MachineType;
import com.gtnh.processingplus.machines.spc.SPCModuleType;
import com.gtnh.processingplus.machines.spc.SPCRecipeData;
import com.gtnh.processingplus.machines.spc.StationEntry;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.tooltip.TooltipHelper;

/**
 * Spectral Photolithography Chamber.
 *
 * Fixed 5×5×8 sealed process line: front cap (controller) + 6 station sections + back cap.
 * Each station section has a machine bay on both side walls (x=0 and x=4, process level).
 *
 * On structure check the SPC reads what GT single-block machine occupies each
 * station bay (type + voltage tier) and builds an ordered station list.
 * Recipes declare a required station sequence; mismatches produce waste output.
 *
 * Shape is intentionally isolated in getStructureDefinition() so it can be
 * redesigned without touching detection or recipe-matching logic.
 */
public class MTE_SPC extends MTEExtendedPowerMultiBlockBase<MTE_SPC> implements ISurvivalConstructable {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    private static final int CASING_INDEX = 49;
    public static final int MIN_STATIONS = 2;
    public static final int MAX_STATIONS = 6;

    private static final String PIECE_MAIN = "main";

    // Controller is at x=2, y=2, z=0 in piece space (center of front face).
    private static final int OFFSET_X = 2;
    private static final int OFFSET_Y = 2;

    private static ItemStack getScorchedBoard() {
        return GTNHPPItems.scorchedBoard(1);
    }

    // -------------------------------------------------------------------------
    // Custom ProcessingLogic — exposes lastRecipe so checkProcessing can read it
    // -------------------------------------------------------------------------

    private static class SPCProcessingLogic extends ProcessingLogic {

        GTRecipe getLastRecipe() {
            return lastRecipe;
        }
    }

    // -------------------------------------------------------------------------
    // Structure definition (shape only — change freely)
    // -------------------------------------------------------------------------

    private static IStructureDefinition<MTE_SPC> STRUCTURE_DEFINITION = null;

    // -------------------------------------------------------------------------
    // Per-instance state
    // -------------------------------------------------------------------------

    private int mStationCount = MIN_STATIONS;

    // Minimum BW glass tier found across all A-element positions during structure check.
    // BW glass meta == voltage tier index (0=ULV … 14=MAX).
    // Caps max energy hatch tier and caps the tier-excess parallel bonus.
    // TODO: extend glassElement() to detect a special glass variant that enables
    // perfect overclocking or another large bonus (e.g. Neodymium glass → +OC).
    private int mGlassTier = 0;

    // Rebuilt on every checkMachine — ordered list of what's in each main station bay.
    final List<StationEntry> mDetectedStations = new ArrayList<>();

    // Rebuilt on every checkMachine — which upgrade adapters are routed into the support bays.
    final EnumSet<SPCModuleType> mInstalledAdapters = EnumSet.noneOf(SPCModuleType.class);

    // Transient — external upgrade modules currently data-stick linked to this SPC, by type.
    // Modules re-register themselves periodically; entries are validated in hasModule().
    private final Map<SPCModuleType, MTE_SPCModuleBase<?>> mLinkedModules = new EnumMap<>(SPCModuleType.class);

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public MTE_SPC(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected MTE_SPC(MTE_SPC prototype) {
        super(prototype.mName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTE_SPC(this);
    }

    // -------------------------------------------------------------------------
    // Structure definition — fixed 5×5×8 sealed process line
    //
    // Station-layer cross-section (z=1..6):
    // I G D G I outer pillars (frame casing) + SPC casing (hatches) + D
    // B A A A B upper band: gt casing sides, BW glass middle
    // M F H F x process level: main bay (M, left) | frames (F) | beam (H) | right wall (x)
    // E A A A E lower band: frame box sides, BW glass middle
    // I G D G I
    //
    // Left wall (x=0) = 6 main station bays (M), z=1..6.
    // Right wall (x=4) = 2 support bays (N) at z=1 and z=6; SPC casing (G) at z=2..5.
    // Caps (z=0 front / z=7 back): front center = controller (~), back center = I.
    //
    // Block legend:
    // A = BW Glass (any tier, viewport + tier mechanic)
    // B = gt.blockcasings:3 C = gt.blockcasings2:13 D = gt.blockcasings3:10
    // E = gt.blockframes:306 F = gt.blockframes:334
    // G = Spectral Isolation Casing (hatch-capable)
    // H = Photonic Alignment Casing I = Spectral Frame Casing
    // M = main station bay N = support machine bay
    // -------------------------------------------------------------------------

    @Override
    public IStructureDefinition<MTE_SPC> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_SPC>builder()
                .addShape(
                    PIECE_MAIN,
                    new String[][] { { "IGDGI", "CAAAC", "CF~FC", "CAAAC", "IGDGI" },
                        { "IGDGI", "BAAAB", "MFHFN", "EAAAE", "IGDGI" },
                        { "IGDGI", "BAAAB", "MFHFG", "EAAAE", "IGDGI" },
                        { "IGDGI", "BAAAB", "MFHFG", "EAAAE", "IGDGI" },
                        { "IGDGI", "BAAAB", "MFHFG", "EAAAE", "IGDGI" },
                        { "IGDGI", "BAAAB", "MFHFG", "EAAAE", "IGDGI" },
                        { "IGDGI", "BAAAB", "MFHFN", "EAAAE", "IGDGI" },
                        { "IGDGI", "CAAAC", "CFIFC", "CAAAC", "IGDGI" }, })
                // G = Spectral Isolation Casing — hatch positions (energy / maintenance / I-O)
                .addElement(
                    'G',
                    buildHatchAdder(MTE_SPC.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance, Muffler)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.SPC_CASING))
                // A = any tiered glass — tier tracked for the energy cap + bonus cap
                .addElement('A', chainAllGlasses(-1, (t, tier) -> t.mGlassTier = tier, t -> t.mGlassTier))
                // B = Reinforced Glass Machine Casing (gt.blockcasings:3)
                .addElement('B', ofBlock(GameRegistry.findBlock("gregtech", "gt.blockcasings"), 3))
                // C = Tungstensteel Machine Casing (gt.blockcasings2:13)
                .addElement('C', ofBlock(GameRegistry.findBlock("gregtech", "gt.blockcasings2"), 13))
                // D = Europium-Reinforced Radiation Proof Casing (gt.blockcasings3:10)
                .addElement('D', ofBlock(GameRegistry.findBlock("gregtech", "gt.blockcasings3"), 10))
                // E = Americium Frame Box (gt.blockframes:306)
                .addElement('E', ofBlock(GameRegistry.findBlock("gregtech", "gt.blockframes"), 306))
                // F = TungstenSteel Frame Box (gt.blockframes:334)
                .addElement('F', ofBlock(GameRegistry.findBlock("gregtech", "gt.blockframes"), 334))
                // H = Photonic Alignment Casing — central beam column
                .addElement('H', ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.SPC_BEAM_CASING))
                // I = Spectral Frame Casing — outer corner pillars + back-cap core
                .addElement('I', ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.SPC_FRAME_CASING))
                // M = main station bay — left wall, z=1-6, populates mDetectedStations
                .addElement('M', stationBayElement())
                // N = support adapter bay — right wall ends, routes upgrade modules into the SPC
                .addElement('N', adapterBayElement())
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // -------------------------------------------------------------------------
    // Structure check
    // -------------------------------------------------------------------------

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mDetectedStations.clear();
        mInstalledAdapters.clear();
        mGlassTier = -1;
        if (!checkPiece(PIECE_MAIN, OFFSET_X, OFFSET_Y, 0)) return false;
        mStationCount = mDetectedStations.size();
        return mMaintenanceHatches.size() == 1 && !mEnergyHatches.isEmpty();
    }

    @Override
    public long getMaxInputVoltage() {
        long fromHatches = super.getMaxInputVoltage();
        if (mGlassTier > 0 && mGlassTier < GTValues.V.length) {
            return Math.min(fromHatches, GTValues.V[mGlassTier]);
        }
        return fromHatches;
    }

    // -------------------------------------------------------------------------
    // Construction (hint / survival build)
    // -------------------------------------------------------------------------

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(PIECE_MAIN, stackSize, hintsOnly, OFFSET_X, OFFSET_Y, 0);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(PIECE_MAIN, stackSize, OFFSET_X, OFFSET_Y, 0, elementBudget, env, false, true);
    }

    // -------------------------------------------------------------------------
    // Machine bay structure elements
    // -------------------------------------------------------------------------

    /**
     * Accepts any GT single-block machine or an empty/casing slot.
     * Appends a StationEntry to mDetectedStations when called during checkPiece.
     */
    private static IStructureElement<MTE_SPC> stationBayElement() {
        return new IStructureElement<MTE_SPC>() {

            @Override
            public boolean check(MTE_SPC t, World world, int x, int y, int z) {
                StationEntry entry = detectMachine(world, x, y, z);
                if (entry == null) return false;
                t.mDetectedStations.add(entry);
                return true;
            }

            @Override
            public boolean spawnHint(MTE_SPC t, World world, int x, int y, int z, ItemStack trigger) {
                return true;
            }

            @Override
            public boolean placeBlock(MTE_SPC t, World world, int x, int y, int z, ItemStack trigger) {
                return false; // player places their own machine
            }
        };
    }

    /**
     * Support-bay slot: accepts an upgrade adapter block (recorded into mInstalledAdapters), or an
     * empty/casing slot (no adapter). Any other block fails the structure check.
     */
    private static IStructureElement<MTE_SPC> adapterBayElement() {
        return new IStructureElement<MTE_SPC>() {

            @Override
            public boolean check(MTE_SPC t, World world, int x, int y, int z) {
                Block block = world.getBlock(x, y, z);
                if (block == GTNHPPBlocks.CASINGS) {
                    int meta = world.getBlockMetadata(x, y, z);
                    if (meta == BlockGTNHPPCasings.BIO_ADAPTER) {
                        t.mInstalledAdapters.add(SPCModuleType.BIO);
                    } else if (meta == BlockGTNHPPCasings.CRYO_ADAPTER) {
                        t.mInstalledAdapters.add(SPCModuleType.CRYO);
                    } else if (meta == BlockGTNHPPCasings.QUANTUM_ADAPTER) {
                        t.mInstalledAdapters.add(SPCModuleType.QUANTUM);
                    }
                    return true;
                }
                return world.isAirBlock(x, y, z);
            }

            @Override
            public boolean spawnHint(MTE_SPC t, World world, int x, int y, int z, ItemStack trigger) {
                return true;
            }

            @Override
            public boolean placeBlock(MTE_SPC t, World world, int x, int y, int z, ItemStack trigger) {
                return false; // player installs their own adapter (or leaves it empty)
            }
        };
    }

    /**
     * Reads what is at (x, y, z) and returns a StationEntry, or null if the block is invalid.
     * GT single-block machines → type + tier detected.
     * Air or SPC casing → EMPTY entry (slot unfilled).
     * Any other block → null (structure check fails).
     */
    private static StationEntry detectMachine(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof IGregTechTileEntity) {
            IGregTechTileEntity igte = (IGregTechTileEntity) te;
            IMetaTileEntity mte = igte.getMetaTileEntity();
            if (mte instanceof MTEBasicMachine) {
                MachineType type = MachineType.detect(mte);
                int tier = (int) ((MTEBasicMachine) mte).getInputTier();
                return new StationEntry(type, tier);
            }
        }
        Block block = world.getBlock(x, y, z);
        if (world.isAirBlock(x, y, z) || block == GTNHPPBlocks.CASINGS) {
            return StationEntry.EMPTY;
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Recipe sequence matching (called by recipe registration / checkRecipe)
    // -------------------------------------------------------------------------

    /**
     * Returns true if the current station configuration satisfies the given ordered sequence.
     * requiredTypes and requiredMinTiers must be the same length.
     * The SPC must have at least as many stations as the sequence length.
     * Each station is checked in order; the first mismatch returns false.
     */
    public boolean matchesSequence(MachineType[] requiredTypes, int[] requiredMinTiers) {
        if (requiredTypes.length != requiredMinTiers.length) return false;
        if (mDetectedStations.size() < requiredTypes.length) return false;
        for (int i = 0; i < requiredTypes.length; i++) {
            if (!mDetectedStations.get(i)
                .matches(requiredTypes[i], requiredMinTiers[i])) return false;
        }
        return true;
    }

    /**
     * Returns true if the given upgrade module is satisfied: its adapter is routed into a support
     * bay AND a module of that type is data-stick linked and currently running.
     */
    public boolean isModuleSatisfied(SPCModuleType type) {
        if (type == null) return true;
        return mInstalledAdapters.contains(type) && hasModule(type);
    }

    // -------------------------------------------------------------------------
    // Upgrade-module linking (data-stick) — see MTE_SPCModuleBase
    // -------------------------------------------------------------------------

    /** Called by a module when it (re)links to this SPC. */
    public void registerLinkedModule(MTE_SPCModuleBase<?> module) {
        mLinkedModules.put(module.getModuleType(), module);
    }

    /** Called by a module when its block is destroyed. */
    public void unregisterLinkedModule(MTE_SPCModuleBase<?> module) {
        mLinkedModules.remove(module.getModuleType(), module);
    }

    /** True if a module of the given type is linked and currently formed + running. */
    public boolean hasModule(SPCModuleType type) {
        MTE_SPCModuleBase<?> module = mLinkedModules.get(type);
        return module != null && module.isModuleActive();
    }

    /** Left-click with a data stick copies this controller's coordinates onto the stick for module linking. */
    @Override
    public void onLeftclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aPlayer instanceof EntityPlayerMP) {
            ItemStack stick = aPlayer.inventory.getCurrentItem();
            if (ItemList.Tool_DataStick.isStackEqual(stick, false, true)) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("type", "SPCUpgrade");
                tag.setInteger("x", aBaseMetaTileEntity.getXCoord());
                tag.setInteger("y", aBaseMetaTileEntity.getYCoord());
                tag.setInteger("z", aBaseMetaTileEntity.getZCoord());
                stick.stackTagCompound = tag;
                stick.setStackDisplayName("SPC Link Data");
                return;
            }
        }
        super.onLeftclick(aBaseMetaTileEntity, aPlayer);
    }

    // -------------------------------------------------------------------------
    // NBT — persist station count across chunk load / world restart
    // -------------------------------------------------------------------------

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("spcStationCount", mStationCount);
        aNBT.setInteger("spcGlassTier", mGlassTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mStationCount = aNBT.hasKey("spcStationCount")
            ? Math.max(MIN_STATIONS, Math.min(MAX_STATIONS, aNBT.getInteger("spcStationCount")))
            : MIN_STATIONS;
        mGlassTier = aNBT.hasKey("spcGlassTier") ? aNBT.getInteger("spcGlassTier") : 0;
    }

    // -------------------------------------------------------------------------
    // Recipe map
    // -------------------------------------------------------------------------

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNHPPRecipeMaps.sSPCRecipes;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new SPCProcessingLogic();
    }

    /**
     * After the base logic finds a recipe and sets outputs, we validate the station sequence
     * and either replace outputs with a scorched board (wrong sequence / tier too low)
     * or scale outputs + reduce duration based on tier excess (correct sequence with better machines).
     *
     * parallels = 2 ^ minExcessTier, capped at 64
     * flat +1 added to every output stack when minExcess >= 1
     */
    @Override
    public CheckRecipeResult checkProcessing() {
        CheckRecipeResult result = super.checkProcessing();
        if (!result.wasSuccessful()) return result;

        GTRecipe recipe = ((SPCProcessingLogic) processingLogic).getLastRecipe();
        if (recipe == null) return result;

        SPCRecipeData data = SPCRecipeData.get(recipe);
        if (data == null) return result;

        // Upgrade-module gate — scorched board if the required module isn't routed in + linked + running.
        if (!isModuleSatisfied(data.requiredModule)) {
            mOutputItems = new ItemStack[] { getScorchedBoard() };
            mOutputFluids = new net.minecraftforge.fluids.FluidStack[0];
            return result;
        }

        int minExcess = data.validateAndGetMinExcess(mDetectedStations);

        if (minExcess < 0) {
            // Wrong machine type or tier too low → scorched board
            mOutputItems = new ItemStack[] { getScorchedBoard() };
            mOutputFluids = new net.minecraftforge.fluids.FluidStack[0];
            return result;
        }

        // Glass tier cap: re-compute excess treating each machine tier as min(actual, mGlassTier).
        // If any glass-capped tier falls below the recipe minimum, produce a scorched board —
        // the glass is too low-tier for this recipe regardless of the machines installed.
        if (mGlassTier > 0) {
            int glassCappedExcess = Integer.MAX_VALUE;
            for (int i = 0; i < data.stationTypes.length; i++) {
                int effectiveTier = Math.min(mDetectedStations.get(i).tier, mGlassTier);
                int excess = effectiveTier - data.stationMinTiers[i];
                if (excess < 0) {
                    mOutputItems = new ItemStack[] { getScorchedBoard() };
                    mOutputFluids = new net.minecraftforge.fluids.FluidStack[0];
                    return result;
                }
                glassCappedExcess = Math.min(glassCappedExcess, excess);
            }
            if (glassCappedExcess != Integer.MAX_VALUE) {
                minExcess = glassCappedExcess;
            }
        }

        if (minExcess == 0) return result; // exactly at minimum, no bonus

        // Apply parallel scaling — 2^minExcess, capped at 64
        int parallels = Math.min(1 << minExcess, 64);

        if (mOutputItems != null) {
            for (ItemStack s : mOutputItems) {
                if (s == null) continue;
                s.stackSize = Math.min(s.stackSize * parallels + 1, s.getMaxStackSize());
            }
        }
        if (mOutputFluids != null) {
            for (net.minecraftforge.fluids.FluidStack f : mOutputFluids) {
                if (f != null) f.amount *= parallels;
            }
        }
        mMaxProgresstime = Math.max(20, mMaxProgresstime / parallels);

        return result;
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { casingTexturePages[0][CASING_INDEX], TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { casingTexturePages[0][CASING_INDEX], TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { casingTexturePages[0][CASING_INDEX] };
    }

    // -------------------------------------------------------------------------
    // Tooltip
    // -------------------------------------------------------------------------

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Spectral Photolithography Chamber, SPC")
            .addInfo("Etches circuit boards through an in-line sequence of GT machines.")
            .addInfo(
                TooltipHelper.coloredText("6", EnumChatFormatting.YELLOW) + EnumChatFormatting.GRAY
                    + " main station bays (left wall) + "
                    + TooltipHelper.coloredText("2", EnumChatFormatting.YELLOW)
                    + EnumChatFormatting.GRAY
                    + " support bays (right wall).")
            .addSeparator()
            .addInfo(
                EnumChatFormatting.GOLD + "Station sequence: "
                    + EnumChatFormatting.GRAY
                    + "place GT singleblock machines in the left-wall bays.")
            .addInfo("Machine type and minimum tier are read front-to-back and matched to the recipe.")
            .addInfo(
                EnumChatFormatting.GREEN + "Tier bonus: "
                    + EnumChatFormatting.GRAY
                    + "machines "
                    + TooltipHelper.coloredText("n", EnumChatFormatting.YELLOW)
                    + EnumChatFormatting.GRAY
                    + " tiers above the minimum grant "
                    + TooltipHelper.coloredText("2^n", EnumChatFormatting.YELLOW)
                    + EnumChatFormatting.GRAY
                    + " parallels (cap: "
                    + TooltipHelper.coloredText("64", EnumChatFormatting.YELLOW)
                    + EnumChatFormatting.GRAY
                    + ").")
            .addInfo(
                EnumChatFormatting.RED + "Wrong sequence"
                    + EnumChatFormatting.GRAY
                    + " — produces "
                    + TooltipHelper.coloredText("Scorched Circuit Boards", EnumChatFormatting.DARK_RED)
                    + EnumChatFormatting.GRAY
                    + " instead of the recipe output.")
            .addInfo(
                EnumChatFormatting.GOLD + "Upgrade modules: "
                    + EnumChatFormatting.GRAY
                    + "high-tier boards need an external module (data-stick linked)")
            .addInfo(
                "  routed in via its " + TooltipHelper.coloredText("Adapter", EnumChatFormatting.AQUA)
                    + EnumChatFormatting.GRAY
                    + " in one of the 2 support bays.")
            .addSeparator()
            .addInfo(
                EnumChatFormatting.GOLD + "Glass tier limits: "
                    + EnumChatFormatting.GRAY
                    + "BW Glass meta = voltage tier index.")
            .addInfo(
                "  " + EnumChatFormatting.RED
                    + "Energy cap: "
                    + EnumChatFormatting.GRAY
                    + "hatches above glass tier contribute no extra voltage.")
            .addInfo(
                "  " + EnumChatFormatting.RED
                    + "Bonus cap: "
                    + EnumChatFormatting.GRAY
                    + "tier-excess parallel bonus capped at glass tier.")
            .addInfo(
                "  " + EnumChatFormatting.DARK_RED
                    + "Glass too low: "
                    + EnumChatFormatting.GRAY
                    + "machines above glass tier produce Scorched Boards.")
            .beginStructureBlock(5, 5, 8, true)
            .addController("Front face, center")
            .addCasingInfoMin("Spectral Isolation Casing", 26, false)
            .addCasingInfoMin("Spectral Frame Casing", 33, false)
            .addCasingInfoExactly("Photonic Alignment Casing", 6, false)
            .addOtherStructurePart("BW Glass (any tier)", "Viewport bands, both levels")
            .addOtherStructurePart("Reinforced/Tungstensteel Casings", "Upper/lower bands and pillars")
            .addOtherStructurePart("Americium & TungstenSteel Frames", "Process-level uprights")
            .addOtherStructurePart("GT Machines (main)", "Left-wall bays × 6 (process sequence)")
            .addOtherStructurePart("Upgrade Adapters", "Right-wall bays × 2 (route in linked modules)")
            .addInputBus("Any Spectral Isolation Casing", 1)
            .addInputHatch("Any Spectral Isolation Casing", 1)
            .addOutputBus("Any Spectral Isolation Casing", 1)
            .addOutputHatch("Any Spectral Isolation Casing", 1)
            .addEnergyHatch("Any Spectral Isolation Casing", 1)
            .addMufflerHatch("Any Spectral Isolation Casing", 1)
            .addMaintenanceHatch("Any Spectral Isolation Casing", 1)
            .toolTipFinisher();
        return tt;
    }

    // -------------------------------------------------------------------------
    // Info panel (shows detected station configuration)
    // -------------------------------------------------------------------------

    @Override
    public String[] getInfoData() {
        String progress = StatCollector.translateToLocal("GT5U.multiblock.Progress") + ": "
            + EnumChatFormatting.GREEN
            + mProgresstime / 20
            + EnumChatFormatting.RESET
            + " s / "
            + EnumChatFormatting.YELLOW
            + mMaxProgresstime / 20
            + EnumChatFormatting.RESET
            + " s";

        String glassTierName = mGlassTier > 0 && mGlassTier < GTValues.VN.length ? GTValues.VN[mGlassTier] : "none";
        String[] lines = new String[3 + mDetectedStations.size() + 1];
        lines[0] = progress;
        lines[1] = EnumChatFormatting.AQUA + "Stations: " + EnumChatFormatting.WHITE + mStationCount;
        lines[2] = EnumChatFormatting.AQUA + "Glass tier: "
            + EnumChatFormatting.WHITE
            + glassTierName
            + EnumChatFormatting.GRAY
            + " (energy cap: "
            + (mGlassTier > 0 && mGlassTier < GTValues.V.length ? GTValues.V[mGlassTier] + " EU/t" : "none")
            + ")";
        for (int i = 0; i < mDetectedStations.size(); i++) {
            lines[3 + i] = EnumChatFormatting.GRAY + "  ["
                + (i + 1)
                + "] "
                + EnumChatFormatting.WHITE
                + mDetectedStations.get(i)
                    .toString();
        }
        StringBuilder adapters = new StringBuilder(EnumChatFormatting.GRAY + "  Adapters: " + EnumChatFormatting.WHITE);
        if (mInstalledAdapters.isEmpty()) {
            adapters.append("none");
        } else {
            boolean first = true;
            for (SPCModuleType type : mInstalledAdapters) {
                if (!first) adapters.append(EnumChatFormatting.GRAY + ", " + EnumChatFormatting.WHITE);
                adapters.append(type.name())
                    .append(
                        hasModule(type) ? EnumChatFormatting.GREEN + " (linked)"
                            : EnumChatFormatting.RED + " (no module)");
                first = false;
            }
        }
        lines[3 + mDetectedStations.size()] = adapters.toString();
        return lines;
    }

    // -------------------------------------------------------------------------
    // Audio / quality-of-life flags
    // -------------------------------------------------------------------------

    @Override
    @SideOnly(Side.CLIENT)
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_EBF_LOOP;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return true;
    }

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }
}
