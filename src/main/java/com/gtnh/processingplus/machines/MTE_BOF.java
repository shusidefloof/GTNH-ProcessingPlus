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
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

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
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;

/**
 * Basic Oxygen Furnace 3×5×3
 * Blows liquid oxygen through molten iron to rapidly decarburize and convert it to steel.
 * Three modes via integrated circuit:
 * Requires Liquid Oxygen
 */
public class MTE_BOF extends MTEExtendedPowerMultiBlockBase<MTE_BOF> implements ISurvivalConstructable {

    // Solid steel casing texture (GT page 0, index 1)
    private static final int CASING_INDEX = 1;
    private static final String STRUCTURE_PIECE_MAIN = "main";

    private static final int OFFSET_X = 1;
    private static final int OFFSET_Y = 2;
    private static final int OFFSET_Z = 0;

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

    // -------------------------------------------------------------------------
    // Structure — 3 wide × 5 tall × 3 deep
    // Controller at z=0, y=2 (centre height), x=1 (centre width).
    // -------------------------------------------------------------------------

    @Override
    public IStructureDefinition<MTE_BOF> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_BOF>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    new String[][] { { "CCC", "CCC", "C~C", "CCC", "CCC" }, // z=0 front
                        { "CCC", "CCC", "CCC", "CCC", "CCC" }, // z=1 body
                        { "CCC", "CCC", "CCC", "CCC", "CCC" } // z=2 back
                    })
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z)) return false;
        return mMaintenanceHatches.size() == 1;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNHPPRecipeMaps.sBOFRecipes;
    }

    // -------------------------------------------------------------------------
    // Rendering — EBF overlay fits the furnace aesthetic
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Tooltip
    // -------------------------------------------------------------------------

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Basic Oxygen Furnace")
            .addInfo("Converts iron to steel using liquid oxygen injection.")
            .addInfo(EnumChatFormatting.YELLOW + "circuit(1)" + EnumChatFormatting.GRAY + " — Standard")
            .addInfo("  Iron ×8 + LOX 2000 → Steel ×8 + CO₂ 1000")
            .addInfo(EnumChatFormatting.YELLOW + "circuit(2)" + EnumChatFormatting.GRAY + " — Calcium Flux")
            .addInfo("  Iron ×8 + LOX 2000 + Calcium ×2 → Steel ×10 | +25% yield")
            .addInfo(EnumChatFormatting.YELLOW + "circuit(3)" + EnumChatFormatting.GRAY + " — Bulk")
            .addInfo("  Iron ×16 + LOX 4000 → Steel ×16 | double batch")
            .addInfo(EnumChatFormatting.RED + "Requires Liquid Oxygen from the Cryogenic Separation Column.")
            .beginStructureBlock(3, 5, 3, true)
            .addController("Front face, center")
            .addCasingInfoMin("Basic Oxygen Furnace Casing", 44, false)
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
    // Info panel
    // -------------------------------------------------------------------------

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
