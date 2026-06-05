package com.gtnh.processingplus.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.Muffler;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
import static gregtech.api.util.GTStructureUtility.*;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.HeatingCoilLevel;
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
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.tooltip.TooltipHelper;
import gregtech.common.misc.GTStructureChannels;

public class MTE_HPSF extends MTEExtendedPowerMultiBlockBase<MTE_HPSF> implements ISurvivalConstructable {

    private static final int CASING_INDEX = 11;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int OFFSET_X = 15;
    private static final int OFFSET_Y = 5;
    private static final int OFFSET_Z = 4;
    private int mGlassTier = 0;

    private static IStructureDefinition<MTE_HPSF> STRUCTURE_DEFINITION = null;

    private HeatingCoilLevel mCoilLevel = HeatingCoilLevel.None;
    private int mHeatingCapacity = 0;
    private int mPipeCasingTier = -1;

    public MTE_HPSF(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected MTE_HPSF(MTE_HPSF prototype) {
        super(prototype.mName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTE_HPSF(this);
    }

    // ── Structure ───────────────────────────────────────────────────────────────

    @Override
    public IStructureDefinition<MTE_HPSF> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_HPSF>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    new String[][] {
                        // Layer 0 — front end cap
                        { "                   ", "    CCC            ", "   CAAAC           ", "   CAAAC           ",
                            "   CAAAC           ", "    CCC            ", "                   ",
                            " BBBBBBBBB         " },
                        // Layer 1 — main vessel ring + TungstenSteel frame
                        { "    III            ", "   I   I           ", "  I     I          ", " GI     IG         ",
                            " GI     IG         ", " GGI   IGG         ", " G  III  G         ",
                            "BBBBBBBBBBB        " },
                        // Layer 2 — transition (HPSF casing ring)
                        { "    JIJ            ", "   H   H           ", "  J     J          ", "  I     I          ",
                            "  J     J          ", "   H   H           ", "    JIJ            ",
                            "BBBBBBBBBBB        " },
                        // Layer 3 — inner lining begins, control stub starts
                        { "    JIJ            ", "   HE EH           ", "  JE   EJ          ", "  I     I          ",
                            "  JE   EJ          ", "   HE EH           ", "    JIJ            ",
                            "BBBBBBBBBBB   BBB  " },
                        // Layer 4 — full inner lining, control chamber base
                        { "     I             ", "   IEEEI           ", "   E   E           ", "  IE   EI     FFF  ",
                            "  GE   EG     CCC  ", "  GIEEEIG     C~C  ", "  G  I  G     CCC  ",
                            "BBBBBBBBBBB  BBBBB " },
                        // Layer 5 — inner lining + control chamber mid
                        { "     I             ", "    EGE            ", "   E   E      FFF  ", "  IG   GI    F   F ",
                            "   E   E     A   A ", "    EGE      A   A ", "     I       C   C ",
                            "BBBBBBBBBBB BBBBBBB" },
                        // Layer 6 — control chamber core (pipe casings)
                        { "        DDDDDDD    ", "    IIIDDD   DDD   ", "   I   ID     FDF  ", "   I   I     F D F ",
                            "   I   I     C D C ", "    III      C D C ", "             C D C ",
                            "BBBBBBBBBBB BBBBBBB" },
                        // Layer 7 — mirror of layer 5
                        { "     I             ", "    EGE            ", "   E   E      FFF  ", "  IG   GI    F   F ",
                            "   E   E     A   A ", "    EGE      A   A ", "     I       C   C ",
                            "BBBBBBBBBBB BBBBBBB" },
                        // Layer 8 — mirror of layer 4
                        { "     I             ", "   IEEEI           ", "   E   E           ", "  IE   EI     FFF  ",
                            "  GE   EG     CCC  ", "  GIEEEIG     CCC  ", "  G  IH G     CCC  ",
                            "BBBBBBBBBBB  BBBBB " },
                        // Layer 9 — mirror of layer 3
                        { "    JIJ            ", "   HE EH           ", "  JE   EJ          ", "  I     I          ",
                            "  JE   EJ          ", "   HE EH           ", "    JIJ            ",
                            "BBBBBBBBBBB   BBB  " },
                        // Layer 10 — mirror of layer 2
                        { "    JIJ            ", "   H   H           ", "  J     J          ", "  I     I          ",
                            "  J     J          ", "   H   H           ", "    JIJ            ",
                            "BBBBBBBBBBB        " },
                        // Layer 11 — mirror of layer 1
                        { "    III            ", "   I   I           ", "  I     I          ", " GI     IG         ",
                            " GI     IG         ", " GGI   IGG         ", " G  III  G         ",
                            "BBBBBBBBBBB        " },
                        // Layer 12 — rear end cap
                        { "                   ", "    CCC            ", "   CAAAC           ", "   CAAAC           ",
                            "   CAAAC           ", "    CCC            ", "                   ",
                            " BBBBBBBBB         " }, })
                // BW glass — tiered viewport (any meta accepted)
                .addElement('A', chainAllGlasses(-1, (t, tier) -> t.mGlassTier = tier, t -> t.mGlassTier))
                // IC2 reinforced stone — base
                .addElement('B', ofBlock(GameRegistry.findBlock("IC2", "blockAlloy"), 0))
                // SCD casing — end caps + control chamber outer + hatch positions
                .addElement(
                    'C',
                    buildHatchAdder(MTE_HPSF.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance, Muffler)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.SCD_CASING))
                // Item pipe casings — tier (1-8) sets parallel count as 2^(tier-1)
                // All D positions must be the same tier.
                .addElement(
                    'D',
                    chainItemPipeCasings(
                        -1,
                        (machine, tier) -> machine.mPipeCasingTier = tier,
                        machine -> machine.mPipeCasingTier))
                // Heating coils — tier sets heat capacity
                .addElement(
                    'E',
                    GTStructureChannels.HEATING_COIL
                        .use(activeCoils(ofCoil(MTE_HPSF::setCoilLevel, MTE_HPSF::getCoilLevel))))
                // HPSF casing — control chamber outer shell + hatch positions
                .addElement(
                    'F',
                    buildHatchAdder(MTE_HPSF.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance, Muffler)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.HPSF_CASING))
                // TungstenSteel frames — structural ring support
                .addElement('G', ofBlock(GameRegistry.findBlock("gregtech", "gt.blockframes"), 334))
                // HPSF casing — transition zones (structural only)
                .addElement('H', ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.HPSF_CASING))
                // HPSF casing — main pressure vessel ring + hatch positions
                .addElement(
                    'I',
                    buildHatchAdder(MTE_HPSF.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance, Muffler)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.HPSF_CASING))
                .addElement('J', ofBlock(GameRegistry.findBlock("gregtech", "gt.blockcasings3"), 10))
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

    // ── Validation ──────────────────────────────────────────────────────────────

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mHeatingCapacity = 0;
        mPipeCasingTier = -1;
        setCoilLevel(HeatingCoilLevel.None);
        if (!checkPiece(STRUCTURE_PIECE_MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z)) return false;
        if (getCoilLevel() == HeatingCoilLevel.None) return false;
        if (mPipeCasingTier == -1) return false;
        if (mMaintenanceHatches.size() != 1) return false;
        mHeatingCapacity = (int) getCoilLevel().getHeat() + 100 * (GTUtility.getTier(getMaxInputVoltage()) - 2);
        return true;
    }

    // ── Recipe logic ────────────────────────────────────────────────────────────

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNHPPRecipeMaps.sHPSFRecipes;
    }

    @Override
    public int getMaxParallelRecipes() {
        return mPipeCasingTier > 0 ? (int) Math.pow(2, mPipeCasingTier - 1) : 1;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Nonnull
            @Override
            protected OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setRecipeHeat(recipe.mSpecialValue)
                    .setMachineHeat(mHeatingCapacity)
                    .setHeatOC(true)
                    .setHeatDiscount(true);
            }

            @Override
            protected @Nonnull CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                return recipe.mSpecialValue <= mHeatingCapacity ? CheckRecipeResultRegistry.SUCCESSFUL
                    : CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    // ── Continuous inert atmosphere ───────────────────────────────────────────────

    /** Inert gas (N₂ or Argon) drained from an input hatch each tick while sintering. */
    private static final int INERT_UPKEEP = 2;

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (!super.onRunningTick(aStack)) return false;
        // The sintering chamber must stay under an inert blanket the whole run; if the gas supply
        // runs dry mid-operation the process halts (matches the "continuous N₂" requirement).
        return depleteInput(Materials.Nitrogen.getGas(INERT_UPKEEP))
            || depleteInput(Materials.Argon.getGas(INERT_UPKEEP));
    }

    // ── Coil accessors ──────────────────────────────────────────────────────────

    public HeatingCoilLevel getCoilLevel() {
        return mCoilLevel;
    }

    public void setCoilLevel(HeatingCoilLevel level) {
        mCoilLevel = level;
    }

    // ── Textures ────────────────────────────────────────────────────────────────

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { casingTexturePages[0][CASING_INDEX], TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { casingTexturePages[0][CASING_INDEX], TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { casingTexturePages[0][CASING_INDEX] };
    }

    // ── Tooltip ─────────────────────────────────────────────────────────────────

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("High Pressure Sintering Furnace, HPSF")
            .addInfo("Simultaneously applies heat and mechanical pressure under inert atmosphere.")
            .addInfo("Required for RHEA alloys, alumina ceramics, and tungsten carbide compacts.")
            .addSeparator()
            .addInfo(
                "Heat capacity: " + TooltipHelper.coloredText("coil tier heat", EnumChatFormatting.RED)
                    + EnumChatFormatting.GRAY
                    + " + "
                    + TooltipHelper.coloredText("100 K", EnumChatFormatting.YELLOW)
                    + EnumChatFormatting.GRAY
                    + " per "
                    + TooltipHelper.tierText("Voltage")
                    + EnumChatFormatting.GRAY
                    + " tier above LV.")
            .addInfo(
                TooltipHelper.effText("-5% EU") + EnumChatFormatting.GRAY
                    + " per "
                    + TooltipHelper.coloredText("900 K", EnumChatFormatting.RED)
                    + EnumChatFormatting.GRAY
                    + " above the recipe requirement.")
            .addInfo(
                "Every " + TooltipHelper.coloredText("1800 K", EnumChatFormatting.RED)
                    + EnumChatFormatting.GRAY
                    + " above the recipe requirement grants 1 "
                    + TooltipHelper.coloredText("perfect overclock", EnumChatFormatting.LIGHT_PURPLE)
                    + EnumChatFormatting.GRAY
                    + ".")
            .addInfo(
                "Parallels: " + TooltipHelper.coloredText("2^(pipe tier − 1)", EnumChatFormatting.YELLOW)
                    + EnumChatFormatting.GRAY
                    + " — set by the item pipe casing tier in the control chamber.")
            .addInfo(
                EnumChatFormatting.RED + "Requires continuous N₂ supply"
                    + EnumChatFormatting.GRAY
                    + " — consumed as a recipe fluid each cycle.")
            .beginStructureBlock(11, 8, 13, true)
            .addController("Center of the 3×3 face on the control chamber")
            .addCasingInfoMin("Hardened Pressure Vessel Casing", 1, false)
            .addCasingInfoMin("High-Pressure Containment Casing", 1, false)
            .addCasingInfoMin("Dual-Sealed Atmosphere Pipe Casing", 1, false)
            .addOtherStructurePart("Heating Coils", "Inner sintering chamber lining")
            .addOtherStructurePart("TungstenSteel Frames", "Structural ring support")
            .addOtherStructurePart("BW Glass Blocks (any tier)", "Viewport end caps")
            .addOtherStructurePart("IC2 Reinforced Stone", "Base layer")
            .addInputBus("Any casing position", 1)
            .addInputHatch("Any casing position", 1)
            .addOutputBus("Any casing position", 1)
            .addOutputHatch("Any casing position", 1)
            .addEnergyHatch("Any casing position", 1)
            .addMufflerHatch("Any casing position", 1)
            .addMaintenanceHatch("Any casing position", 1)
            .toolTipFinisher();
        return tt;
    }

    // ── Info display ────────────────────────────────────────────────────────────

    @Override
    public String[] getInfoData() {
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
            "Heat Capacity: " + EnumChatFormatting.RED + mHeatingCapacity + EnumChatFormatting.RESET + " K",
            "Coil Tier: " + EnumChatFormatting.YELLOW
                + (mCoilLevel != null ? mCoilLevel.name() : "None")
                + EnumChatFormatting.RESET,
            "Pipe Casing Tier: " + EnumChatFormatting.AQUA
                + (mPipeCasingTier > 0 ? mPipeCasingTier : "None")
                + EnumChatFormatting.RESET,
            "Max Parallels: " + EnumChatFormatting.AQUA + getMaxParallelRecipes() + EnumChatFormatting.RESET };
    }

    // ── Sound / flags ───────────────────────────────────────────────────────────

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
