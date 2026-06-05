package com.gtnh.processingplus.machines;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;
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
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.tooltip.TooltipHelper;
import gregtech.common.misc.GTStructureChannels;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import javax.annotation.Nonnull;

import static gregtech.api.util.GTStructureUtility.chainAllGlasses;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.Muffler;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;

public class MTE_HTRF  extends MTEExtendedPowerMultiBlockBase<MTE_HTRF> implements ISurvivalConstructable {
    private static final int CASING_INDEX = 11;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int OFFSET_X = 3;
    private static final int OFFSET_Y = 4;
    private static final int OFFSET_Z = 0;
    private int mGlassTier = 0;

    private static IStructureDefinition<MTE_HTRF> STRUCTURE_DEFINITION = null;

    private HeatingCoilLevel mCoilLevel = HeatingCoilLevel.None;
    private int mHeatingCapacity = 0;

    public MTE_HTRF(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected MTE_HTRF(MTE_HTRF prototype) {
        super(prototype.mName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTE_HTRF(this);
    }

    @Override
    public IStructureDefinition<MTE_HTRF> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_HTRF>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    new String[][]{{
                        "       ",
                        "       ",
                        "  FFF  ",
                        "   B   ",
                        "   ~   ",
                        "   B   ",
                        " CFFFC ",
                        "CC   CC",
                        "CB   BC"
                    },{
                        "       ",
                        "  EFE  ",
                        " FADAF ",
                        " BDDDB ",
                        " BDDDB ",
                        " BDDDB ",
                        "CFADAFC",
                        "CFEFEFC",
                        "BB   BB"
                    },{
                        "  EEE  ",
                        " EAEAE ",
                        "FA   AF",
                        " D   D ",
                        " D   D ",
                        " D   D ",
                        "FA   AF",
                        " EAEAE ",
                        "  EEE  "
                    },{
                        "  EEE  ",
                        " FEAEF ",
                        "FD   DF",
                        "BD   DB",
                        "BD   DB",
                        "BD   DB",
                        "FD   DF",
                        " FEAEF ",
                        "  EEE  "
                    },{
                        "  EEE  ",
                        " EAEAE ",
                        "FA   AF",
                        " D   D ",
                        " D   D ",
                        " D   D ",
                        "FA   AF",
                        " EAEAE ",
                        "  EEE  "
                    },{
                        "       ",
                        "  EEE  ",
                        " FADAF ",
                        " BDDDB ",
                        " BDDDB ",
                        " BDDDB ",
                        "CFADAFC",
                        "CFEFEFC",
                        "BB   BB"
                    },{
                        "       ",
                        "       ",
                        "  FFF  ",
                        "   B   ",
                        "   B   ",
                        "   B   ",
                        " CFFFC ",
                        "CC   CC",
                        "CB   BC"
                    }}
                )
                .addElement('A', GTStructureChannels.HEATING_COIL
                    .use(activeCoils(ofCoil(MTE_HTRF::setCoilLevel, MTE_HTRF::getCoilLevel))))
                .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 0))
                .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockFrames, 360))
                .addElement('D', chainAllGlasses(-1, (t, tier) -> t.mGlassTier = tier, t -> t.mGlassTier))
                .addElement('E', buildHatchAdder(MTE_HTRF.class)
                    .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance, Muffler)
                    .casingIndex(CASING_INDEX)
                    .hint(1)
                    .buildAndChain(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.HTRF_CASING))
                .addElement('F', StructureUtility.ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.HTRF_REINFORCED_CASING))
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mHeatingCapacity = 0;
        mGlassTier = -1;
        setCoilLevel(HeatingCoilLevel.None);
        if (!checkPiece(STRUCTURE_PIECE_MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z)) return false;
        if (getCoilLevel() == HeatingCoilLevel.None) return false;
        if (mMaintenanceHatches.size() != 1) return false;
        mHeatingCapacity = (int) getCoilLevel().getHeat() + 100 * (GTUtility.getTier(getMaxInputVoltage()) - 2);
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNHPPRecipeMaps.sHTRFRecipes;
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

    /** 4 base parallels, +2 per heating-coil tier. */
    @Override
    public int getMaxParallelRecipes() {
        int coilTier = mCoilLevel != null ? Math.max(0, mCoilLevel.getTier()) : 0;
        return 4 + 2 * coilTier;
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
        tt.addMachineType("High Temperature Reaction Furnace, HTRF")
            .addInfo("Coil-heated ceramic furnace for reactions requiring temperatures above 2000K.")
            .addInfo("The only machine capable of SiC sintering, refractory carbide, and boride synthesis.")
            .addSeparator()
            .addInfo(
                "Heat capacity: "
                    + TooltipHelper.coloredText("coil tier heat", EnumChatFormatting.RED)
                    + EnumChatFormatting.GRAY
                    + " + "
                    + TooltipHelper.coloredText("100K", EnumChatFormatting.YELLOW)
                    + EnumChatFormatting.GRAY
                    + " per voltage tier above LV.")
            .addInfo(
                "Every "
                    + TooltipHelper.coloredText("1800K", EnumChatFormatting.RED)
                    + EnumChatFormatting.GRAY
                    + " above the recipe requirement grants 1 "
                    + TooltipHelper.coloredText("perfect overclock", EnumChatFormatting.LIGHT_PURPLE)
                    + EnumChatFormatting.GRAY
                    + ".")
            .addInfo(
                TooltipHelper.effText("-5% EU")
                    + EnumChatFormatting.GRAY
                    + " per "
                    + TooltipHelper.coloredText("900K", EnumChatFormatting.RED)
                    + EnumChatFormatting.GRAY
                    + " above the recipe requirement.")
            .addInfo(
                "Parallels: "
                    + TooltipHelper.coloredText("4", EnumChatFormatting.YELLOW)
                    + EnumChatFormatting.GRAY
                    + " base + "
                    + TooltipHelper.coloredText("2", EnumChatFormatting.YELLOW)
                    + EnumChatFormatting.GRAY
                    + " per heating coil tier.")
            .addSeparator()
            .addInfo(
                EnumChatFormatting.GOLD + "Glass tier: "
                    + EnumChatFormatting.GRAY
                    + "BW Glass tier limits maximum energy hatch voltage.")
            .beginStructureBlock(9, 9, 7, true)
            .addController("Center of the front face")
            .addCasingInfoMin("Silicon Carbide Ceramic Casing", 10, false)
            .addCasingInfoMin("Rebolted Silicon Carbide Casing", 1, false)
            .addOtherStructurePart("Heating Coils", "Inner ring across all layers")
            .addOtherStructurePart("BW Glass (any tier)", "Viewport windows")
            .addOtherStructurePart("GT Frames", "Structural support")
            .addInputBus("Any Silicon Carbide Ceramic Casing", 1)
            .addInputHatch("Any Silicon Carbide Ceramic Casing", 1)
            .addOutputBus("Any Silicon Carbide Ceramic Casing", 1)
            .addOutputHatch("Any Silicon Carbide Ceramic Casing", 1)
            .addEnergyHatch("Any Silicon Carbide Ceramic Casing", 1)
            .addMufflerHatch("Any Silicon Carbide Ceramic Casing", 1)
            .addMaintenanceHatch("Any Silicon Carbide Ceramic Casing", 1)
            .toolTipFinisher();
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
                + EnumChatFormatting.RESET,
            "Max Parallels: " + EnumChatFormatting.AQUA + getMaxParallelRecipes() + EnumChatFormatting.RESET };
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

    @Override
    public long getMaxInputVoltage() {
        long fromHatches = super.getMaxInputVoltage();
        if (mGlassTier > 0 && mGlassTier < gregtech.api.enums.GTValues.V.length) {
            return Math.min(fromHatches, gregtech.api.enums.GTValues.V[mGlassTier]);
        }
        return fromHatches;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("htrfGlassTier", mGlassTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mGlassTier = aNBT.hasKey("htrfGlassTier") ? aNBT.getInteger("htrfGlassTier") : 0;
    }
}
