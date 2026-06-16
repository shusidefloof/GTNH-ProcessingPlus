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

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
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
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;

/**
 * Supercritical Dryer — 5×5×5 high-pressure autoclave.
 * Brings liquid CO₂ above its critical point (31 °C, 73.8 bar) to extract pore-filling
 * solvents without surface tension collapsing the gel network, yielding aerogels and
 * enabling supercritical CO₂ extraction of resins and pharmaceuticals.
 */
public class MTE_SCD extends MTEExtendedPowerMultiBlockBase<MTE_SCD> implements ISurvivalConstructable {

    private static final int CASING_INDEX = 11;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int OFFSET_X = 2;
    private static final int OFFSET_Y = 2;
    private static final int OFFSET_Z = 0;

    private static IStructureDefinition<MTE_SCD> STRUCTURE_DEFINITION = null;

    public MTE_SCD(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected MTE_SCD(MTE_SCD prototype) {
        super(prototype.mName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTE_SCD(this);
    }

    @Override
    public IStructureDefinition<MTE_SCD> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_SCD>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    // shape[z][y][x] — 5 z-slices, 5 y-rows each, 5 x-chars
                    // z=0 is the front face (controller position); z=4 is the back face.
                    new String[][] {
                        // z=0: front face — controller at center
                        { "CCCCC", "CCCCC", "CC~CC", "CCCCC", "CCCCC" },
                        // z=1–3: solid pressure-vessel walls
                        { "CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC" },
                        { "CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC" },
                        { "CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC" },
                        // z=4: back face
                        { "CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC" } })
                .addElement(
                    'C',
                    buildHatchAdder(MTE_SCD.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance, Muffler)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.SCD_CASING))
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
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack,
        List<StructureError> errors) {
        checkPiece(STRUCTURE_PIECE_MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z, errors);
        if (mMaintenanceHatches.size() != 1) errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNHPPRecipeMaps.sSCDRecipes;
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
        tt.addMachineType("Supercritical Dryer, SCD")
            .addInfo(EnumChatFormatting.GRAY + "Brings liquid CO" + EnumChatFormatting.GRAY
                + "₂ above its critical point " + EnumChatFormatting.AQUA
                + "(31 °C, 73.8 bar)" + EnumChatFormatting.GRAY + " inside the pressure vessel.")
            .addInfo(EnumChatFormatting.GRAY + "Eliminates surface tension during gel drying — preserving the")
            .addInfo(EnumChatFormatting.GRAY + "nanoporous network to produce " + EnumChatFormatting.AQUA
                + "aerogels" + EnumChatFormatting.GRAY + " and performing " + EnumChatFormatting.AQUA
                + "scCO₂ extraction" + EnumChatFormatting.GRAY + ".")
            .addSeparator()
            .addStaticParallelInfo(8)
            .addSeparator()
            .addInfo(EnumChatFormatting.YELLOW + "Processes:" + EnumChatFormatting.GRAY
                + " gel aging, solvent exchange, supercritical CO₂ drying,")
            .addInfo(EnumChatFormatting.GRAY + "resin/pharmaceutical extraction, PAN aerogel gelation.")
            .addInfo(EnumChatFormatting.GOLD + "Recovered CO₂ output loops back into the CSC.")
            .beginStructureBlock(5, 5, 5, true)
            .addController("Front face, center")
            .addCasingInfoMin("High-Pressure Containment Casing", 115, false)
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
