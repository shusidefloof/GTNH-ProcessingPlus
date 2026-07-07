package com.gtnh.processingplus.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
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
import static gregtech.api.util.GTStructureUtility.activeCoils;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofCoil;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HeatingCoilLevel;
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
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.tooltip.TooltipHelper;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.core.material.MaterialsElements;

public class MTE_AAR extends MTEExtendedPowerMultiBlockBase<MTE_AAR> implements ISurvivalConstructable {

    private static final int CASING_INDEX = 11;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    // Controller marker '~' sits at slice z=0, row y=2, char x=2 in the exported shape.
    private static final int OFFSET_X = 2;
    private static final int OFFSET_Y = 2;
    private static final int OFFSET_Z = 0;

    private static IStructureDefinition<MTE_AAR> STRUCTURE_DEFINITION = null;

    // Iodine frame box has no fixed meta in GT++'s material system, so resolve it from the actual
    // ItemStack the material produces rather than hardcoding a magic damage value.
    private static final Block IODINE_FRAME_BLOCK;
    private static final int IODINE_FRAME_META;

    static {
        ItemStack frame = MaterialsElements.getInstance().IODINE.getFrameBox(1);
        IODINE_FRAME_BLOCK = Block.getBlockFromItem(frame.getItem());
        IODINE_FRAME_META = frame.getItemDamage();
    }

    private static Block miscutilsBlock(String name) {
        Block b = GameRegistry.findBlock("miscutils", name);
        if (b == null) throw new RuntimeException("AAR requires GT++ block: " + name);
        return b;
    }

    private HeatingCoilLevel mCoilLevel = HeatingCoilLevel.None;
    private int mHeatingCapacity = 0;

    public MTE_AAR(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected MTE_AAR(MTE_AAR prototype) {
        super(prototype.mName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTE_AAR(this);
    }

    @Override
    public IStructureDefinition<MTE_AAR> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_AAR>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    /*
                     * Block legend (from the in-game structure export):
                     * A -> Iodine Frame Box (GT++) — structural shell
                     * B -> gt.blockcasings5 — any tier Heating Coil (drives mCoilLevel via activeCoils)
                     * C -> gt.blockcasings8:0 — Chemically Inert Machine Casing (hatch-capable shell)
                     * D -> gt.blockcasings9:0 — PBI Pipe Casing (internal piping)
                     * E -> miscutils.blockcasings:14 — Coil (Blast Smelter) Casing, reused as outer shell
                     */
                    new String[][] { { "CEEEC", "ACCCA", "CC~CC", "ACCCA", "CEEEC" },
                        { "ECCCE", "CD DC", "CD DC", "CD DC", "ECCCE" },
                        { "ECCCE", "C D C", "B D B", "C D C", "ECCCE" },
                        { "ECCCE", "CD DC", "CD DC", "CD DC", "ECCCE" },
                        { "CEEEC", "ACCCA", "CCBCC", "ACCCA", "CEEEC" } })
                .addElement('A', ofBlock(IODINE_FRAME_BLOCK, IODINE_FRAME_META))
                .addElement(
                    'B',
                    GTStructureChannels.HEATING_COIL
                        .use(activeCoils(ofCoil(MTE_AAR::setCoilLevel, MTE_AAR::getCoilLevel))))
                .addElement(
                    'C',
                    buildHatchAdder(MTE_AAR.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance, Muffler)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GregTechAPI.sBlockCasings8, 0))
                .addElement('D', ofBlock(GregTechAPI.sBlockCasings9, 0))
                .addElement('E', ofBlock(miscutilsBlock("miscutils.blockcasings"), 14))
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
        mHeatingCapacity = 0;
        setCoilLevel(HeatingCoilLevel.None);
        if (!checkPiece(STRUCTURE_PIECE_MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z, errors)) return;
        if (getCoilLevel() == HeatingCoilLevel.None) errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
        if (mMaintenanceHatches.size() != 1) errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
        if (!errors.isEmpty()) return;
        mHeatingCapacity = (int) getCoilLevel().getHeat() + 100 * (GTUtility.getTier(getMaxInputVoltage()) - 2);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNHPPRecipeMaps.sAARRecipes;
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
        };
    }

    public HeatingCoilLevel getCoilLevel() {
        return mCoilLevel;
    }

    public void setCoilLevel(HeatingCoilLevel level) {
        mCoilLevel = level;
    }

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

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Ammonia Atmosphere Reactor, AAR")
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
            .beginStructureBlock(5, 5, 5, true)
            .addController("Front face, center")
            .addCasingInfoMin("Chemically Inert Machine Casing", 55, false)
            .addOtherStructurePart("Iodine Frame Box", "Outer corner posts")
            .addOtherStructurePart("PBI Pipe Casing", "Inner cross-section piping")
            .addOtherStructurePart("Coil (Blast Smelter) Casing", "Outer shell edges")
            .addOtherStructurePart("Heating Coils (any tier)", "4 blocks, top/bottom center + middle layer flanks")
            .addInputBus("Any Chemically Inert Machine Casing", 1)
            .addInputHatch("Any Chemically Inert Machine Casing", 1)
            .addOutputBus("Any Chemically Inert Machine Casing", 1)
            .addOutputHatch("Any Chemically Inert Machine Casing", 1)
            .addEnergyHatch("Any Chemically Inert Machine Casing", 1)
            .addMufflerHatch("Any Chemically Inert Machine Casing", 1)
            .addMaintenanceHatch("Any Chemically Inert Machine Casing", 1)
            .toolTipFinisher("_Shusi_");
        return tt;
    }

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
                + EnumChatFormatting.RESET };
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
