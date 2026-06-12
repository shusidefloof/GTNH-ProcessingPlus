package com.gtnh.processingplus.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_VACUUM_FREEZER;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_VACUUM_FREEZER_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_VACUUM_FREEZER_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_VACUUM_FREEZER_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
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

/**
 * Cryogenic Annealing Cryostat (CAC) — a 5×7×5 vacuum cryostat tower.
 * The superconductor wire is cryo-annealed inside a hollow vacuum core, wrapped in a load-bearing
 * Aerogel Insulation Block lining (36 blocks) that suppresses heat ingress so the coolant load stays
 * tractable at UHV+. Takes over every UHV-tier-and-above superconductor anneal recipe.
 */
public class MTE_CAC extends MTEExtendedPowerMultiBlockBase<MTE_CAC> implements ISurvivalConstructable {

    private static final int CASING_INDEX = 11;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int OFFSET_X = 2;
    private static final int OFFSET_Y = 3;
    private static final int OFFSET_Z = 0;

    private static IStructureDefinition<MTE_CAC> STRUCTURE_DEFINITION = null;

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
                    // shape[z][y][x] — 5 z-layers, 7 y-rows each, 5 x-chars
                    // 'C' = Cryostat Vacuum Casing or hatch, 'A' = Aerogel Insulation Block (no hatches), ' ' = vacuum core
                    new String[][] {
                        // z=0: front face — all casing, controller center
                        { "CCCCC", "CCCCC", "CCCCC", "CC~CC", "CCCCC", "CCCCC", "CCCCC" },
                        // z=1: casing ring + aerogel lining + hollow vacuum core
                        { "CCCCC", "CAAAC", "CA AC", "CA AC", "CA AC", "CAAAC", "CCCCC" },
                        // z=2: middle layer (identical to z=1)
                        { "CCCCC", "CAAAC", "CA AC", "CA AC", "CA AC", "CAAAC", "CCCCC" },
                        // z=3: inner layer (identical to z=1)
                        { "CCCCC", "CAAAC", "CA AC", "CA AC", "CA AC", "CAAAC", "CCCCC" },
                        // z=4: back face — all casing
                        { "CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC" }, })
                .addElement(
                    'C',
                    buildHatchAdder(MTE_CAC.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.CRYOSTAT_VACUUM_CASING))
                .addElement('A', ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.AEROGEL_INSULATION_BLOCK))
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
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic();
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
                .addIcon(OVERLAY_FRONT_VACUUM_FREEZER_ACTIVE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_VACUUM_FREEZER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { casingTexturePages[0][CASING_INDEX], TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_VACUUM_FREEZER)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_VACUUM_FREEZER_GLOW)
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
            .addInfo(EnumChatFormatting.GRAY + "Anneals materials at " + EnumChatFormatting.AQUA
                + "cryogenic temperatures" + EnumChatFormatting.GRAY + " in a vacuum core.")
            .addSeparator()
            .addInfo(
                EnumChatFormatting.GOLD + "Inner lining: "
                    + EnumChatFormatting.GRAY
                    + "exactly "
                    + TooltipHelper.coloredText("36", EnumChatFormatting.YELLOW)
                    + EnumChatFormatting.GRAY
                    + " Aerogel Insulation Blocks required.")
            .addInfo("The aerogel lining is load-bearing — the machine will not form without it.")
            .beginStructureBlock(5, 7, 5, true)
            .addController("Front face, center")
            .addCasingInfoMin("Cryostat Vacuum Casing", 78, false)
            .addCasingInfoExactly("Aerogel Insulation Block", 36, true)
            .addInputBus("Any outer casing", 1)
            .addInputHatch("Any outer casing", 1)
            .addOutputBus("Any outer casing", 1)
            .addOutputHatch("Any outer casing", 1)
            .addEnergyHatch("Any outer casing", 1)
            .addMaintenanceHatch("Any outer casing", 1)
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
