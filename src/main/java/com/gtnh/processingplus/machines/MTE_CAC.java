package com.gtnh.processingplus.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;
import static gregtech.api.util.GTStructureUtility.ofSolenoidCoil;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.tooltip.TooltipHelper;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.core.block.ModBlocks;

/**
 * Cryogenic Annealing Cryostat (CAC) — a 15×13×7 vacuum cryostat tower, structure designed by
 * Lord of Turtle. The superconductor wire is cryo-annealed inside a hollow glass vacuum core, wrapped
 * in a Solenoid Superconductor Coil column (tier scales parallels and EU discount, MTELatex-style) and
 * a shell of GT/GT++ endgame casings. Takes over every UHV-tier-and-above superconductor anneal recipe.
 */
public class MTE_CAC extends MTEExtendedPowerMultiBlockBase<MTE_CAC> implements ISurvivalConstructable {

    // GT++ blockCustomMachineCasings meta3 = gtpp(3,4) = 116, per gregtech.api.casing.Casings.
    private static final int CASING_INDEX = 116;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    // "Transposed Scan" export: outer/row axes are swapped vs. a normal scan, so the true shape is
    // [z=7][y=13][x=15] with the controller marker '~' at z=0, y=11, x=7 (near the top of the front face).
    private static final int OFFSET_X = 7;
    private static final int OFFSET_Y = 11;
    private static final int OFFSET_Z = 0;

    private static IStructureDefinition<MTE_CAC> STRUCTURE_DEFINITION = null;

    // Base parallel multiplier before the solenoid tier and voltage tier scale it (see getMaxParallelRecipes).
    private static final int BASE_PARALLEL = 2;
    // EU discount per solenoid voltage tier, same formula MTELatex uses for its item-pipe-tier fluid discount.
    private static final float EU_DISCOUNT_PER_TIER = 0.0625F;

    // Detected solenoid voltage tier (MV=2 .. UMV=12), null until the structure is checked.
    private Byte mSolenoidTier = null;

    public MTE_CAC(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected MTE_CAC(MTE_CAC prototype) {
        super(prototype.mName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTE_CAC(this);
    }

    @Override
    public IStructureDefinition<MTE_CAC> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_CAC>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    /*
                     * Block legend (from the in-game structure export, credit: Lord of Turtle):
                     * A -> BW_GlasBlocks — any tiered BartWorks glass (hollow vacuum core)
                     * B -> gt.blockcasings.cyclotron_coils — any tier Solenoid Superconductor Coil (core
                     * column; tier scales parallels and EU discount, see getMaxParallelRecipes)
                     * C -> gt.blockcasings10:14 — Solidifier Radiator
                     * D -> gt.blockcasings11:5 — Quantium Item Pipe Casing
                     * E -> gt.blockcasings12:7 — Ultimately Static Machine Casing
                     * F -> gt.blockframes:966 — Orichalcum Frame Box
                     * G -> gtplusplus.blockcasings.3:3 — Bedrock Miner Casing (GT++)
                     * H -> gtplusplus.blockspecialcasings.2:3 — Custom Machine Casing 4 (GT++, hatch-capable shell)
                     */
                    new String[][] {
                        { "    HHHHHHH    ", "    F HHH F    ", "    F HHH F    ", "    F     F    ", "    F     F    ",
                            "    F     F    ", "    F     F    ", "    F     F    ", "    F     F    ",
                            "    F     F    ", "    F HHH F    ", "    F H~H F    ", "    HHHHHHH    " },
                        { "    HHHHHHH    ", "     AAAAA     ", "    HAAAAAH    ", "     AAAAA     ", "     AAAAA     ",
                            "     AAAAA     ", "     AAAAA     ", "     AAAAA     ", "     AAAAA     ",
                            "     AAAAA     ", "    HAAAAAH    ", "     AAAAA     ", "    HHHHHHH    " },
                        { "C   HHHHHHH   C", " C  HAG GAH  C ", "  C HA   AH C  ", "     AG GA     ", "     A   A     ",
                            "C C  AG GA  C C", " C   A   A   C ", "C C  AG GA  C C", "     A   A     ",
                            "     AG GA     ", "  C HA   AH C  ", " C  HAG GAH  C ", "C   HHHHHHH   C" },
                        { "C   HHHHHHH   C", " EEEHA B AHEEE ", " EC HA B AH CE ", " E   A B A   E ", " E   A B A   E ",
                            "CEC  A B A  CEC", " EEEDA B ADEEE ", "CEC  A B A  CEC", " E   A B A   E ",
                            " E   A B A   E ", " EC HA B AH CE ", " EEEHA B AHEEE ", "C   HHHHHHH   C" },
                        { "C   HHHHHHH   C", " C  HAG GAH  C ", "  C HA   AH C  ", "     AG GA     ", "     A   A     ",
                            "C C  AG GA  C C", " C   A   A   C ", "C C  AG GA  C C", "     A   A     ",
                            "     AG GA     ", "  C HA   AH C  ", " C  HAG GAH  C ", "C   HHHHHHH   C" },
                        { "    HHHHHHH    ", "     AAAAA     ", "    HAAAAAH    ", "     AAAAA     ", "     AAAAA     ",
                            "     AAAAA     ", "     AAAAA     ", "     AAAAA     ", "     AAAAA     ",
                            "     AAAAA     ", "    HAAAAAH    ", "     AAAAA     ", "    HHHHHHH    " },
                        { "    HHHHHHH    ", "    F HHH F    ", "    F HHH F    ", "    F     F    ", "    F     F    ",
                            "    F     F    ", "    F     F    ", "    F     F    ", "    F     F    ",
                            "    F     F    ", "    F HHH F    ", "    F HHH F    ", "    HHHHHHH    " } })
                .addElement('A', chainAllGlasses())
                .addElement(
                    'B',
                    GTStructureChannels.SOLENOID
                        .use(ofSolenoidCoil(MTE_CAC::setSolenoidTier, MTE_CAC::getSolenoidTier)))
                .addElement('C', ofBlock(GregTechAPI.sBlockCasings10, 14))
                .addElement('D', ofBlock(GregTechAPI.sBlockCasings11, 5))
                .addElement('E', ofBlock(GregTechAPI.sBlockCasings12, 7))
                .addElement('F', ofBlock(GregTechAPI.sBlockFrames, 966))
                .addElement('G', ofBlock(ModBlocks.blockCasings3Misc, 3))
                .addElement(
                    'H',
                    buildHatchAdder(MTE_CAC.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(ModBlocks.blockCustomMachineCasings, 3))
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

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        mSolenoidTier = null;
        checkPiece(STRUCTURE_PIECE_MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z, errors);
        if (mMaintenanceHatches.size() != 1) errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic().setMaxParallelSupplier(this::getTrueParallel)
            .setEuModifier(1.0F - getSolenoidDiscount());
    }

    // Parallels scale with both the solenoid coil's voltage tier and the machine's input voltage tier —
    // same shape as MTEIndustrialForgeHammer's solenoid-scaled parallel count.
    @Override
    public int getMaxParallelRecipes() {
        return BASE_PARALLEL * (mSolenoidTier == null ? 1 : mSolenoidTier)
            * GTUtility.getTier(this.getMaxInputVoltage());
    }

    // EU discount per solenoid tier — same 0.0625-per-tier formula MTELatex uses for its item-pipe-tier
    // fluid discount, just applied to EU cost instead of a fluid input amount.
    private float getSolenoidDiscount() {
        return EU_DISCOUNT_PER_TIER * (mSolenoidTier == null ? 0 : mSolenoidTier);
    }

    private Byte getSolenoidTier() {
        return mSolenoidTier;
    }

    private void setSolenoidTier(byte tier) {
        mSolenoidTier = tier;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNHPPRecipeMaps.sCACRecipes;
    }

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

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Cryogenic Annealing Cryostat, CAC")
            .addInfo(
                EnumChatFormatting.GRAY + "Anneals materials at "
                    + EnumChatFormatting.AQUA
                    + "cryogenic temperatures"
                    + EnumChatFormatting.GRAY
                    + " in a vacuum core.")
            .addSeparator()
            .addInfo(
                EnumChatFormatting.GOLD + "Core column: "
                    + EnumChatFormatting.GRAY
                    + "a Solenoid Superconductor Coil (any tier) down the centre, wrapped in glass.")
            .addInfo(
                EnumChatFormatting.GOLD + "Higher solenoid tiers"
                    + EnumChatFormatting.GRAY
                    + " grant more parallels and a bigger EU discount ("
                    + TooltipHelper.coloredText("6.25% per tier", EnumChatFormatting.YELLOW)
                    + EnumChatFormatting.GRAY
                    + ").")
            .beginStructureBlock(15, 13, 7, true)
            .addController("See NEI structure preview")
            .addCasingInfoMin("Custom Machine Casing 4 (GT++)", 145, false)
            .addOtherStructurePart("BartWorks Glass (any tier)", "Hollow vacuum core")
            .addOtherStructurePart("Solenoid Superconductor Coil (any tier)", "Central column")
            .addOtherStructurePart("Solidifier Radiator / Quantium Item Pipe Casing", "Corner + edge accents")
            .addOtherStructurePart("Ultimately Static Machine Casing", "Side accents")
            .addOtherStructurePart("Orichalcum Frame Box", "Structural corner posts")
            .addOtherStructurePart("Bedrock Miner Casing (GT++)", "Ring accents")
            .addInputBus("Any Custom Machine Casing 4", 1)
            .addInputHatch("Any Custom Machine Casing 4", 1)
            .addOutputBus("Any Custom Machine Casing 4", 1)
            .addOutputHatch("Any Custom Machine Casing 4", 1)
            .addEnergyHatch("Any Custom Machine Casing 4", 1)
            .addMaintenanceHatch("Any Custom Machine Casing 4", 1)
            .addSubChannelUsage(GTStructureChannels.SOLENOID)
            .toolTipFinisher(
                "_Shusi_",
                EnumChatFormatting.GREEN + ""
                    + EnumChatFormatting.BOLD
                    + "Lord_"
                    + EnumChatFormatting.AQUA
                    + EnumChatFormatting.BOLD
                    + "of_"
                    + EnumChatFormatting.DARK_GREEN
                    + EnumChatFormatting.BOLD
                    + "Turtle27");
        return tt;
    }

    @Override
    public String[] getInfoData() {
        return new String[] { StatCollector.translateToLocal("GT5U.multiblock.Progress") + ": "
            + EnumChatFormatting.GREEN
            + mProgresstime / 20
            + EnumChatFormatting.RESET
            + " s / "
            + EnumChatFormatting.YELLOW
            + mMaxProgresstime / 20
            + EnumChatFormatting.RESET
            + " s" };
    }

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
