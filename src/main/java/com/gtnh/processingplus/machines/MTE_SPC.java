package com.gtnh.processingplus.machines;

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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
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
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

/**
 * Spectral Photolithography Chamber.
 *
 * Variable-length multiblock: front cap + 2–6 station sections + back cap.
 * Each station section has one machine bay on the left wall (x=0, y=1).
 * The back cap has an optional support machine bay on the right wall (x=2, y=1).
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

    private static final String PIECE_FRONT = "spc_front";
    private static final String PIECE_STATION = "spc_station";
    private static final String PIECE_BACK = "spc_back";

    // Controller is at the center of the front face: x=1, y=1, z=0 in piece space.
    private static final int OFFSET_X = 1;
    private static final int OFFSET_Y = 1;

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

    // Rebuilt on every checkMachine — ordered list of what's in each station bay.
    final List<StationEntry> mDetectedStations = new ArrayList<>();

    // Rebuilt on every checkMachine — what's in the back-cap support bay (may be EMPTY).
    StationEntry mSupportStation = StationEntry.EMPTY;

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
    // Structure definition
    //
    // Top-down view (one station section shown):
    // [C][~][C] ← front cap: controller at center
    // [M][C][C] ← station: machine bay on left wall (x=0, y=1)
    // [C][C][S] ← back cap: support bay on right wall (x=2, y=1)
    //
    // Full per-layer cross-section (looking along depth axis):
    // y=2: C C C
    // y=1: M C C (station) / C C S (back) / C ~ C (front)
    // y=0: C C C
    // -------------------------------------------------------------------------

    @Override
    public IStructureDefinition<MTE_SPC> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_SPC>builder()
                // shape[z][y] = x-string. z increases going into the structure.
                .addShape(PIECE_FRONT, new String[][] { { "CCC", "C~C", "CCC" } })
                .addShape(PIECE_STATION, new String[][] { { "CCC", "MCC", "CCC" } })
                .addShape(PIECE_BACK, new String[][] { { "CCC", "CCS", "CCC" } })
                .addElement(
                    'C',
                    buildHatchAdder(MTE_SPC.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance, Muffler)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.SPC_CASING))
                .addElement('M', stationBayElement())
                .addElement('S', supportBayElement())
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // -------------------------------------------------------------------------
    // Structure check
    // -------------------------------------------------------------------------

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        for (int n = MIN_STATIONS; n <= MAX_STATIONS; n++) {
            mDetectedStations.clear();
            mSupportStation = StationEntry.EMPTY;
            mMaintenanceHatches.clear();
            mEnergyHatches.clear();
            mInputHatches.clear();
            mOutputHatches.clear();
            mInputBusses.clear();
            mOutputBusses.clear();
            mMufflerHatches.clear();
            if (checkSPCStructure(n)) {
                mStationCount = n;
                return mMaintenanceHatches.size() == 1 && !mEnergyHatches.isEmpty();
            }
        }
        return false;
    }

    /**
     * Checks front + n station sections + back cap.
     * Pieces are placed at increasing depth; z-offset encodes depth relative to controller.
     * checkPiece(name, ox, oy, oz) positions the piece so that piece-local (ox,oy,oz) = controller world pos.
     * A station at depth d has piece z=0 at world depth d, so oz = -d.
     */
    private boolean checkSPCStructure(int stations) {
        if (!checkPiece(PIECE_FRONT, OFFSET_X, OFFSET_Y, 0)) return false;
        for (int i = 0; i < stations; i++) {
            if (!checkPiece(PIECE_STATION, OFFSET_X, OFFSET_Y, -(i + 1))) return false;
        }
        return checkPiece(PIECE_BACK, OFFSET_X, OFFSET_Y, -(stations + 1));
    }

    // -------------------------------------------------------------------------
    // Construction (hint / survival build)
    // -------------------------------------------------------------------------

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(PIECE_FRONT, stackSize, hintsOnly, OFFSET_X, OFFSET_Y, 0);
        for (int i = 0; i < mStationCount; i++) {
            buildPiece(PIECE_STATION, stackSize, hintsOnly, OFFSET_X, OFFSET_Y, -(i + 1));
        }
        buildPiece(PIECE_BACK, stackSize, hintsOnly, OFFSET_X, OFFSET_Y, -(mStationCount + 1));
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        int built;
        built = survivalBuildPiece(PIECE_FRONT, stackSize, OFFSET_X, OFFSET_Y, 0, elementBudget, env, false, true);
        if (built > 0) return built;
        for (int i = 0; i < mStationCount; i++) {
            built = survivalBuildPiece(
                PIECE_STATION,
                stackSize,
                OFFSET_X,
                OFFSET_Y,
                -(i + 1),
                elementBudget,
                env,
                false,
                true);
            if (built > 0) return built;
        }
        return survivalBuildPiece(
            PIECE_BACK,
            stackSize,
            OFFSET_X,
            OFFSET_Y,
            -(mStationCount + 1),
            elementBudget,
            env,
            false,
            true);
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
     * Same as stationBayElement but writes to mSupportStation instead of appending to the list.
     */
    private static IStructureElement<MTE_SPC> supportBayElement() {
        return new IStructureElement<MTE_SPC>() {

            @Override
            public boolean check(MTE_SPC t, World world, int x, int y, int z) {
                StationEntry entry = detectMachine(world, x, y, z);
                if (entry == null) return false;
                t.mSupportStation = entry;
                return true;
            }

            @Override
            public boolean spawnHint(MTE_SPC t, World world, int x, int y, int z, ItemStack trigger) {
                return true;
            }

            @Override
            public boolean placeBlock(MTE_SPC t, World world, int x, int y, int z, ItemStack trigger) {
                return false;
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
     * Returns true if the support bay has the required machine type at or above the minimum tier.
     * Pass MachineType.NONE and tier 0 if no support machine is needed.
     */
    public boolean matchesSupport(MachineType requiredType, int minTier) {
        if (requiredType == MachineType.NONE) return true;
        return mSupportStation != null && mSupportStation.matches(requiredType, minTier);
    }

    // -------------------------------------------------------------------------
    // NBT — persist station count across chunk load / world restart
    // -------------------------------------------------------------------------

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("spcStationCount", mStationCount);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mStationCount = aNBT.hasKey("spcStationCount")
            ? Math.max(MIN_STATIONS, Math.min(MAX_STATIONS, aNBT.getInteger("spcStationCount")))
            : MIN_STATIONS;
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

        // Support bay check — scorched board if required machine is absent/wrong
        if (data.requiresSupport() && !matchesSupport(data.supportType, data.supportMinTier)) {
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
        tt.addMachineType("Spectral Photolithography Chamber")
            .addInfo("Etches circuit boards through a configurable assembly line of GT machines.")
            .addInfo(
                "Build " + EnumChatFormatting.YELLOW
                    + MIN_STATIONS
                    + EnumChatFormatting.GRAY
                    + "–"
                    + EnumChatFormatting.YELLOW
                    + MAX_STATIONS
                    + EnumChatFormatting.GRAY
                    + " station sections between front and back caps.")
            .addInfo("Place GT single-block machines in the left-wall bay of each station.")
            .addInfo("Machine type and tier are read in order — wrong sequence produces waste.")
            .addInfo("Optional: place a support machine in the right-wall bay of the back cap.")
            .beginStructureBlock(3, 3, MIN_STATIONS + 2, true)
            .addController("Front face, center")
            .addCasingInfoMin("Spectral Isolation Casing", 26, false)
            .addInputBus("Any casing", 1)
            .addInputHatch("Any casing", 1)
            .addOutputBus("Any casing", 1)
            .addOutputHatch("Any casing", 1)
            .addEnergyHatch("Any casing", 1)
            .addMufflerHatch("Any casing", 1)
            .addMaintenanceHatch("Any casing", 1)
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

        String[] lines = new String[2 + mDetectedStations.size() + 1];
        lines[0] = progress;
        lines[1] = EnumChatFormatting.AQUA + "Stations: " + EnumChatFormatting.WHITE + mStationCount;
        for (int i = 0; i < mDetectedStations.size(); i++) {
            lines[2 + i] = EnumChatFormatting.GRAY + "  ["
                + (i + 1)
                + "] "
                + EnumChatFormatting.WHITE
                + mDetectedStations.get(i)
                    .toString();
        }
        lines[2 + mDetectedStations.size()] = EnumChatFormatting.GRAY + "  [S] "
            + EnumChatFormatting.WHITE
            + mSupportStation.toString();
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
