package com.gtnh.processingplus.machines;

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
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

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
import gregtech.api.GregTechAPI;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.tooltip.TooltipHelper;

public class MTE_BOF extends MTEExtendedPowerMultiBlockBase<MTE_BOF> implements ISurvivalConstructable {

    private static final int CASING_INDEX = 1; // solid steel casing
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int OFFSET_X = 2, OFFSET_Y = 2, OFFSET_Z = 0;

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
                    new String[][] {
                        // z=0 — controller face
                        { "     ", "  C  ", " A~A ", " ACA " },
                        // z=1
                        { "  C  ", " C C ", "C   C", "CCBCC" },
                        // z=2
                        { " C C ", "C   C", "A   A", "ABBBA" },
                        // z=3
                        { "  C  ", " C C ", "C   C", "CCBCC" },
                        // z=4 — back face
                        { "     ", "  C  ", " CAC ", " CAC " },
                    })
                .addElement('A', ofBlock(GregTechAPI.sBlockCasings3, 10))
                .addElement('B', ofBlock(GregTechAPI.sBlockCasings3, 14))
                .addElement(
                    'C',
                    buildHatchAdder(MTE_BOF.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance, Muffler)
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
        checkPiece(STRUCTURE_PIECE_MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z, errors);
        if (mMaintenanceHatches.size() != 1) errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 4;
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
        tt.addMachineType("Basic Oxygen Furnace, BOF")
            .addInfo(EnumChatFormatting.GRAY + "Refines metal with a high-purity " + EnumChatFormatting.GOLD
                + "oxygen blast" + EnumChatFormatting.GRAY + ".")
            .addSeparator()
            .addStaticParallelInfo(4)
            .addInfo(
                EnumChatFormatting.RED + "Requires Liquid Oxygen"
                    + EnumChatFormatting.GRAY
                    + " — produced by the Cryogenic Separation Column.")
            .addSeparator()
            .addInfo(
                TooltipHelper.coloredText("circuit(1)", EnumChatFormatting.AQUA) + EnumChatFormatting.GRAY
                    + "  Iron ×8 + LOX 2,000 mB → Steel ×8 + CO₂")
            .addInfo(
                TooltipHelper.coloredText("circuit(2)", EnumChatFormatting.AQUA) + EnumChatFormatting.GRAY
                    + "  Iron ×8 + LOX 2,000 mB + Calcium ×2 → "
                    + TooltipHelper.coloredText("Steel ×10", EnumChatFormatting.GREEN)
                    + EnumChatFormatting.GRAY
                    + " | "
                    + TooltipHelper.effText("+25%")
                    + EnumChatFormatting.GRAY
                    + " yield")
            .addInfo(
                TooltipHelper.coloredText("circuit(3)", EnumChatFormatting.AQUA) + EnumChatFormatting.GRAY
                    + "  Iron ×16 + LOX 4,000 mB → Steel ×16 | double batch")
            .beginStructureBlock(5, 4, 5, true)
            .addController("Front face, center")
            .addCasingInfoMin("Basic Oxygen Furnace Casing", 44, false)
            .addInputBus("Any casing", 1)
            .addInputHatch("Any casing", 1)
            .addOutputBus("Any casing", 1)
            .addOutputHatch("Any casing", 1)
            .addEnergyHatch("Any casing", 1)
            .addMufflerHatch("Any casing", 1)
            .addMaintenanceHatch("Any casing", 1)
            .toolTipFinisher("_Shusi_");
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
