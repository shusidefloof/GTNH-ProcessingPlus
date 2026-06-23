package com.gtnh.processingplus.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlocksTiered;
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
import static gregtech.api.util.GTStructureUtility.chainItemPipeCasings;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.ITierConverter;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;

/**
 * Dual Atmosphere Furnace — a sealed multi-chamber reactor switchable between
 * oxidizing (O₂) and inert (N₂/Ar) atmospheres. Use a screwdriver to toggle mode.
 *
 * The machine tier is determined by the atmosphere casing (G), glass (A), and item
 * pipe casing (C) used in the structure:
 *   Tier 1 (IV)  — Dual-Sealed Atmosphere Casing + IV glass + Tin pipe
 *   Tier 2 (LuV) — Advanced Atmosphere Casing + LuV glass + Electrum pipe
 *   Tier 3 (UV)  — Pristine Atmosphere Casing + UV glass + Osmium pipe
 *   Tier 4 (UEV) — Absolute Atmosphere Casing + UEV glass + Quantium pipe
 *
 * Consumes 1 mB/t of the active atmosphere gas while running.
 */
public class MTE_DAF extends MTEExtendedPowerMultiBlockBase<MTE_DAF> implements ISurvivalConstructable {

    private static final int CASING_INDEX = 11;
    private static final String STRUCTURE_PIECE_MAIN = "main";

    // Controller ~ is at layer=1, row=3, col=9
    private static final int OFFSET_X = 9;
    private static final int OFFSET_Y = 3;
    private static final int OFFSET_Z = 1;


    private static final String[] TIER_NAMES = { "Invalid", "IV", "LuV", "UV", "UEV" };

    // ── Structure shape (7 layers × 7 rows × 18 cols) ───────────────────────────
    // A=BW glass  B=GT casing:11  C=item pipe  D=NHCM casing  E=GT frame
    // F=sheet metal  G=DAF atmo casing (tiered)  ~=controller
    private static final String[][] SHAPE_MAIN = {
        { " E   E       E   E",
          " F   FBBBBBBBF   F",
          " FD DF B   B FD DF",
          " FD DF       FD DF",
          " FD DF B   B FD DF",
          " F   FBBBBBBBF   F",
          " E   E       E   E" },
        { " F   FBBBBBBBF   F",
          "  GGGGB     BGGGG ",
          " BDCD AGGGGGA DCDB",
          " BDCD AGA~AGA DCDB",
          " BDCD AGGGGGA DCDB",
          "  GGGGB     BGGGG ",
          " F   FBBBBBBBF   F" },
        { " FD DFBB   B FD DF",
          " BDCD AGGGGGA DCDB",
          "B                 ",
          "B                 ",
          "B                 ",
          " BDCD AGGGGGA DCDB",
          " FD DFBB   BBFD DF" },
        { " FD DFB      FD DF",
          " BDCD AGAAAGA DCDB",
          "B                 ",
          "B                 ",
          "B                 ",
          " BDCD AGAAAGA DCDB",
          " FD DFB     BFD DF" },
        { " FD DFBB   B FD DF",
          " BDCD AGGGGGA DCDB",
          "B                 ",
          "B                 ",
          "B                 ",
          " BDCD AGGGGGA DCDB",
          " FD DFBB   BBFD DF" },
        { " F   FBBBBBBBF   F",
          "  GGGGB     BGGGG ",
          " BDCD AGGGGGA DCDB",
          " BDCD AGAAAGA DCDB",
          " BDCD AGGGGGA DCDB",
          "  GGGGB     BGGGG ",
          " F   FBBBBBBBF   F" },
        { " E   E       E   E",
          " F   FBBBBBBBF   F",
          " FD DF B   B FD DF",
          " FD DF       FD DF",
          " FD DF B   B FD DF",
          " F   FBBBBBBBF   F",
          " E   E       E   E" }
    };

    private boolean mIsOxidizing = true;
    private int mGlassTier = 0;
    private int mPipeCasingTier = -1;
    private byte mAtmoCasingTier = -1;
    /** 0 = structure invalid; 1 = IV; 2 = LuV; 3 = UV; 4 = UEV */
    private byte mSpecialTier = 0;

    private static IStructureDefinition<MTE_DAF> STRUCTURE_DEFINITION = null;

    public MTE_DAF(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected MTE_DAF(MTE_DAF prototype) {
        super(prototype.mName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTE_DAF(this);
    }

    // ── Structure ────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    @Override
    public IStructureDefinition<MTE_DAF> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            // Atmosphere casing: accepts all 4 DAF casing tiers, records which one
            ITierConverter<Byte> atmoCasingConverter = (block, meta) -> {
                if (block != GTNHPPBlocks.CASINGS) return null;
                switch (meta) {
                    case BlockGTNHPPCasings.DAF_CASING:     return (byte) 1;
                    case BlockGTNHPPCasings.DAF_CASING_LUV: return (byte) 2;
                    case BlockGTNHPPCasings.DAF_CASING_UV:  return (byte) 3;
                    case BlockGTNHPPCasings.DAF_CASING_UEV: return (byte) 4;
                    default: return null;
                }
            };
            List<Pair<net.minecraft.block.Block, Integer>> atmoCasingReps = new ArrayList<>();
            atmoCasingReps.add(Pair.<net.minecraft.block.Block, Integer>of(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.DAF_CASING));
            atmoCasingReps.add(Pair.<net.minecraft.block.Block, Integer>of(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.DAF_CASING_LUV));
            atmoCasingReps.add(Pair.<net.minecraft.block.Block, Integer>of(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.DAF_CASING_UV));
            atmoCasingReps.add(Pair.<net.minecraft.block.Block, Integer>of(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.DAF_CASING_UEV));

            STRUCTURE_DEFINITION = StructureDefinition.<MTE_DAF>builder()
                .addShape(STRUCTURE_PIECE_MAIN, SHAPE_MAIN)
                // A = BW glass — tiered; records the glass tier found
                .addElement('A', chainAllGlasses(-1, (mte, tier) -> mte.mGlassTier = tier, mte -> mte.mGlassTier))
                // B = GT clean machine casing (meta 11) — hatch-capable outer shell
                .addElement(
                    'B',
                    buildHatchAdder(MTE_DAF.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance, Muffler)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GregTechAPI.sBlockCasings1, 11))
                // C = item pipe casing — tiered; records which tier
                .addElement(
                    'C',
                    chainItemPipeCasings(-1, (mte, tier) -> mte.mPipeCasingTier = tier, mte -> mte.mPipeCasingTier))
                // D = NHCM casing (meta 0)
                .addElement('D', ofBlock(GregTechAPI.sBlockCasingsNH, 0))
                // E = GT frame (meta 312)
                .addElement('E', ofBlock(GregTechAPI.sBlockFrames, 312))
                // F = GT sheet metal (meta 306)
                .addElement('F', ofBlock(GameRegistry.findBlock("gregtech", "gt.sheetmetal"), 306))
                // G = atmosphere casing — tiered DAF casing; records which tier was built
                .addElement('G', ofBlocksTiered(
                    atmoCasingConverter,
                    atmoCasingReps,
                    (byte) -1,
                    (mte, tier) -> mte.mAtmoCasingTier = tier,
                    mte -> mte.mAtmoCasingTier))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, OFFSET_X, OFFSET_Y, OFFSET_Z);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            OFFSET_X,
            OFFSET_Y,
            OFFSET_Z,
            elementBudget,
            env,
            false,
            true);
    }

    // ── Validation ───────────────────────────────────────────────────────────────

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack,
        List<StructureError> errors) {
        mGlassTier = 0;
        mPipeCasingTier = -1;
        mAtmoCasingTier = -1;
        mSpecialTier = 0;

        if (!checkPiece(STRUCTURE_PIECE_MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z, errors)) return;

        // Atmosphere casing must be one of the 4 DAF tiers
        if (mAtmoCasingTier < 1 || mAtmoCasingTier > 4) {
            errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
            return;
        }

        // Glass must be present (any BW glass tier accepted)
        if (mGlassTier <= 0) {
            errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
            return;
        }

        // Item pipe casing must be present
        if (mPipeCasingTier <= 0) {
            errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
            return;
        }

        mSpecialTier = mAtmoCasingTier;

        if (mMaintenanceHatches.size() != 1) errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
    }

    // ── Recipe logic ─────────────────────────────────────────────────────────────

    private class DAFProcessingLogic extends ProcessingLogic {

        GTRecipe getLastRecipe() {
            return lastRecipe;
        }

        @Nonnull
        @Override
        protected CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
            return recipe.mSpecialValue <= mSpecialTier
                ? CheckRecipeResultRegistry.SUCCESSFUL
                : CheckRecipeResultRegistry.NO_RECIPE;
        }
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return mIsOxidizing ? GTNHPPRecipeMaps.sDAFOxidizingRecipes : GTNHPPRecipeMaps.sDAFInertRecipes;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new DAFProcessingLogic();
    }

    // ── Continuous atmosphere + recipe fluid drain ────────────────────────────────

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (!super.onRunningTick(aStack)) return false;

        // Drain 1 mB/t of the active atmosphere gas
        FluidStack atmo = mIsOxidizing
            ? Materials.Oxygen.getFluid(1)
            : Materials.Nitrogen.getFluid(1);
        if (!depleteInput(atmo)) {
            stopMachine(ShutDownReasonRegistry.POWER_LOSS);
            return false;
        }

        // Drain recipe-specified fluids per tick (amounts in recipe = mB/t)
        GTRecipe recipe = ((DAFProcessingLogic) processingLogic).getLastRecipe();
        if (recipe != null && recipe.mFluidInputs != null) {
            for (FluidStack fs : recipe.mFluidInputs) {
                if (fs == null || fs.amount <= 0) continue;
                if (!depleteInput(new FluidStack(fs.getFluid(), fs.amount))) {
                    stopMachine(ShutDownReasonRegistry.POWER_LOSS);
                    return false;
                }
            }
        }

        return true;
    }

    // ── Mode toggle ──────────────────────────────────────────────────────────────

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aStack) {
        mIsOxidizing = !mIsOxidizing;
        GTUtility.sendChatToPlayer(
            aPlayer,
            "DAF atmosphere: " + (mIsOxidizing ? "Oxidizing (O₂)" : "Inert (N₂/Ar)"));
    }

    // ── NBT persistence ──────────────────────────────────────────────────────────

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("IsOxidizing", mIsOxidizing);
        aNBT.setByte("mSpecialTier", mSpecialTier);
        aNBT.setByte("mAtmoCasingTier", mAtmoCasingTier);
        aNBT.setInteger("mGlassTier", mGlassTier);
        aNBT.setInteger("mPipeCasingTier", mPipeCasingTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mIsOxidizing = aNBT.getBoolean("IsOxidizing");
        mSpecialTier = aNBT.getByte("mSpecialTier");
        mAtmoCasingTier = aNBT.getByte("mAtmoCasingTier");
        mGlassTier = aNBT.getInteger("mGlassTier");
        mPipeCasingTier = aNBT.getInteger("mPipeCasingTier");
    }

    // ── Textures ─────────────────────────────────────────────────────────────────

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

    // ── Tooltip ──────────────────────────────────────────────────────────────────

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Dual Atmosphere Furnace, DAF")
            .addInfo(EnumChatFormatting.GRAY + "Sealed reaction chamber with switchable atmosphere.")
            .addInfo(
                EnumChatFormatting.AQUA + "Screwdriver"
                    + EnumChatFormatting.GRAY
                    + " toggles "
                    + EnumChatFormatting.RED
                    + "Oxidizing (O₂)"
                    + EnumChatFormatting.GRAY
                    + " / "
                    + EnumChatFormatting.AQUA
                    + "Inert (N₂/Ar)"
                    + EnumChatFormatting.GRAY
                    + " mode.")
            .addInfo(EnumChatFormatting.YELLOW + "Consumes 1 mB/t atmosphere gas while running.")
            .addInfo(EnumChatFormatting.YELLOW + "Recipe fluid inputs are consumed continuously (mB/t) while running.")
            .addSeparator()
            .addInfo("Tier is determined by the atmosphere casing (G), glass (A), and pipe casing (C):")
            .addInfo(
                EnumChatFormatting.WHITE + "  IV  "
                    + EnumChatFormatting.GRAY
                    + "— Dual-Sealed Atmosphere Casing, IV glass, Tin pipe")
            .addInfo(
                EnumChatFormatting.GREEN + "  LuV "
                    + EnumChatFormatting.GRAY
                    + "— Advanced Atmosphere Casing, LuV glass, Electrum pipe")
            .addInfo(
                EnumChatFormatting.AQUA + "  UV  "
                    + EnumChatFormatting.GRAY
                    + "— Pristine Atmosphere Casing, UV glass, Osmium pipe")
            .addInfo(
                EnumChatFormatting.LIGHT_PURPLE + "  UEV "
                    + EnumChatFormatting.GRAY
                    + "— Absolute Atmosphere Casing, UEV glass, Quantium pipe")
            .beginStructureBlock(18, 7, 7, false)
            .addController("Front face center, layer 2 of 7")
            .addCasingInfoMin("GT Clean Machine Casing (B) — hatch positions", 1, false)
            .addCasingInfoMin("Atmosphere Casing (G) — determines tier", 1, false)
            .addOtherStructurePart("BW Glass (A) — tiered glass panels", "Inner chamber face")
            .addOtherStructurePart("Item Pipe Casing (C) — tiered", "Inner column connectors")
            .addOtherStructurePart("NHCM Casing (D)", "Column filler")
            .addOtherStructurePart("GT Frame (meta 312) (E)", "Corner pillars")
            .addOtherStructurePart("GT Sheet Metal (meta 306) (F)", "Side panels")
            .addInputBus("Any B casing position", 1)
            .addInputHatch("Any B casing position", 1)
            .addOutputBus("Any B casing position", 1)
            .addOutputHatch("Any B casing position", 1)
            .addEnergyHatch("Any B casing position", 1)
            .addMufflerHatch("Any B casing position", 1)
            .addMaintenanceHatch("Any B casing position", 1)
            .toolTipFinisher("_Shusi_");
        return tt;
    }

    // ── Info display ─────────────────────────────────────────────────────────────

    @Override
    public String[] getInfoData() {
        String tierName = mSpecialTier >= 0 && mSpecialTier < TIER_NAMES.length ? TIER_NAMES[mSpecialTier] : "?";
        return new String[] {
            StatCollector.translateToLocal("GT5U.multiblock.Progress") + ": "
                + EnumChatFormatting.GREEN
                + mProgresstime / 20
                + EnumChatFormatting.RESET
                + " s / "
                + EnumChatFormatting.YELLOW
                + mMaxProgresstime / 20
                + EnumChatFormatting.RESET
                + " s",
            "Atmosphere: "
                + (mIsOxidizing
                    ? EnumChatFormatting.RED + "Oxidizing (O₂)"
                    : EnumChatFormatting.AQUA + "Inert (N₂/Ar)"),
            "DAF Tier: " + EnumChatFormatting.YELLOW + tierName + EnumChatFormatting.RESET,
            "Glass tier: " + EnumChatFormatting.AQUA + mGlassTier + EnumChatFormatting.RESET
                + " / Pipe tier: "
                + EnumChatFormatting.AQUA
                + mPipeCasingTier
                + EnumChatFormatting.RESET };
    }

    // ── Sound / flags ────────────────────────────────────────────────────────────

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
