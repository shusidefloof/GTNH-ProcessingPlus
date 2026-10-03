package com.gtnh.processingplus.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.MACHINE_CASING_SOLID_STEEL;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_HEARTH;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_HEARTH_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_HEARTH_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_HEARTH_GLOW;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;
import java.util.List;

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
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.tooltip.TooltipHelper;

/**
 * Basic Oxygen Furnace (BOF) — a 5×4×5 steelmaking converter that refines iron into steel
 * using a high-purity oxygen blast. Accepts flux additives (calcium, calcite, dolomite)
 * to increase yield and produce recoverable BOF slag (lime flux can be fully recycled).
 */
public class MTE_BOF extends MTEExtendedPowerMultiBlockBase<MTE_BOF> implements ISurvivalConstructable {

    // Page 100 slot 1 — page 100 itself is allocated once by CommonProxy.registerExternalCasingTextures(),
    // which also registers this exact index to MACHINE_CASING_SOLID_STEEL so hatches match the controller
    // face and the BOF_CASING structure block (see that method for why: no existing GT5 casingTexturePages
    // slot renders as Solid Steel, so it has to be registered by hand into an otherwise-unused page).
    public static final int SOLID_STEEL_MACHINE_CASING_INDEX = 12801;
    private static final int CASING_INDEX = SOLID_STEEL_MACHINE_CASING_INDEX;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int OFFSET_X = 2, OFFSET_Y = 2, OFFSET_Z = 0;

    private final static int PARALLELS = 4;

    private static IStructureDefinition<MTE_BOF> STRUCTURE_DEFINITION = null;

    public MTE_BOF(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected MTE_BOF(MTE_BOF prototype) {
        super(prototype.mName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTE_BOF(this);
    }

    @Override
    public IStructureDefinition<MTE_BOF> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_BOF>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    /*
                     * Block legend:
                     * A -> sBlockCasings3:10 — Solid Steel Machine Casing (structural shell)
                     * B -> sBlockCasings3:14 — Steel Turbine Casing (inner liner / tuyere zone)
                     * C -> BOF Casing (hatch-capable outer shell)
                     */
                    new String[][] { { "     ", "  C  ", " A~A ", " ACA " }, { "  C  ", " C C ", "C   C", "CCBCC" },
                        { " C C ", "C   C", "A   A", "ABBBA" }, { "  C  ", " C C ", "C   C", "CCBCC" },
                        { "     ", "  C  ", " CAC ", " CAC " }, })
                .addElement('A', ofBlock(GregTechAPI.sBlockCasings3, 10))
                .addElement('B', ofBlock(GregTechAPI.sBlockCasings3, 14))
                .addElement(
                    'C',
                    buildHatchAdder(MTE_BOF.class)
                        .atLeast(
                            Energy.or(ExoticEnergy),
                            InputBus,
                            InputHatch,
                            OutputBus,
                            OutputHatch,
                            Maintenance,
                            Muffler)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.BOF_CASING))
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z, errors)) return;
        checkOneMaintenanceHatch(errors);
        checkHasEnergyHatch(errors);
        checkHasMufflerHatch(errors);
        checkHasAnyInput(errors);
        checkHasAnyOutput(errors);
    }

    @Override
    public int getMaxParallelRecipes() {
        return PARALLELS;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNHPPRecipeMaps.sBOFRecipes;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { TextureFactory.of(MACHINE_CASING_SOLID_STEEL), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_HEARTH_ACTIVE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_HEARTH_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { TextureFactory.of(MACHINE_CASING_SOLID_STEEL), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_HEARTH)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_HEARTH_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { TextureFactory.of(MACHINE_CASING_SOLID_STEEL) };
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Basic Oxygen Furnace, BOF")
            .addInfo(
                TooltipHelper.coloredText(
                    TooltipHelper.italicText("Refining metal with high purity oxygen blasts"),
                    EnumChatFormatting.DARK_GRAY))
            .addStaticParallelInfo(PARALLELS)
            .addSeparator()
            .addTecTechHatchInfo()
            .beginStructureBlock(5, 4, 5, true)
            .addController("Front face, center")
            .addCasing("44+", "Basic Oxygen Furnace Casing", false)
            .addEnergyHatch("1+", "Any casing", 1)
            .addMaintenanceHatch("1", "Any casing", 1)
            .addMufflerHatch("1", "Any casing", 1)
            .addInputBus("1+", "Any casing", 1)
            .addInputHatch("1+", "Any casing", 1)
            .addOutputBus("1+", "Any casing", 1)
            .addOutputHatch("1+", "Any casing", 1)
            .toolTipFinisher("_Shusi_");
        return tt;
    }

    @Override
    public String[] getInfoData() {
        List<String> lines = new ArrayList<>();
        lines.add(
            StatCollector.translateToLocal("GT5U.multiblock.Progress") + ": "
                + EnumChatFormatting.GREEN
                + mProgresstime / 20
                + EnumChatFormatting.RESET
                + " s / "
                + EnumChatFormatting.YELLOW
                + mMaxProgresstime / 20
                + EnumChatFormatting.RESET
                + " s");
        return lines.toArray(new String[0]);
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
