package com.gtnh.processingplus.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.Muffler;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_MEGA_CHEMICAL_REACTOR;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_MEGA_CHEMICAL_REACTOR_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_MEGA_CHEMICAL_REACTOR_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_MEGA_CHEMICAL_REACTOR_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;
import com.gtnh.processingplus.recipes.PPRecipeHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.SoundResource;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.tooltip.TooltipHelper;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

/**
 * Ceramic Reaction Vessel: 5×5×5 structure with hBN ceramic inner lining.
 * The 24 hBN blocks are structurally load-bearing: checkMachine fails without them.
 * Only multiblock that can safely contain exotic molten alloy mixtures at LuV/ZPM.
 */
public class MTE_CRV extends MTEExtendedPowerMultiBlockBase<MTE_CRV> implements ISurvivalConstructable {

    // RobustTungstenSteelMachineCasing (sBlockCasings4 meta0): matches CRV_CASING's borrowed texture.
    private static final int CASING_INDEX = 48;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int OFFSET_X = 2;
    private static final int OFFSET_Y = 2;
    private static final int OFFSET_Z = 0;

    // Machine modes: native CRV recipes, or the shared GT++ Alloy Blast Smelter recipe pool.
    private static final int MACHINEMODE_CRV = 0;
    private static final int MACHINEMODE_ABS = 1;

    private static IStructureDefinition<MTE_CRV> STRUCTURE_DEFINITION = null;

    public MTE_CRV(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected MTE_CRV(MTE_CRV prototype) {
        super(prototype.mName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTE_CRV(this);
    }

    @Override
    public IStructureDefinition<MTE_CRV> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_CRV>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    // shape[z][y][x]: 5 z-layers, 5 y-rows each, 5 x-chars
                    // 'C' = CRV casing or hatch, 'B' = hBN ceramic block (no hatches allowed), ' ' = air
                    new String[][] {
                        // z=0: front face: all casing, controller center
                        { "CCCCC", "CCCCC", "CC~CC", "CCCCC", "CCCCC" },
                        // z=1: outer casing ring + hBN inner lining
                        { "CCCCC", "CBBBC", "CB BC", "CBBBC", "CCCCC" },
                        // z=2: middle layer (identical to z=1)
                        { "CCCCC", "CBBBC", "CB BC", "CBBBC", "CCCCC" },
                        // z=3: inner layer (identical to z=1)
                        { "CCCCC", "CBBBC", "CB BC", "CBBBC", "CCCCC" },
                        // z=4: back face: all casing
                        { "CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC" }, })
                .addElement(
                    'C',
                    buildHatchAdder(MTE_CRV.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance, Muffler)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.CRV_CASING))
                .addElement('B', ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.HBN_CERAMIC_BLOCK))
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
        checkHasInputBus(errors);
        checkHasOutputBus(errors);
        if (PPRecipeHelper.recipeMapHasFluidInputs(getRecipeMap())) checkHasInputHatch(errors);
        if (PPRecipeHelper.recipeMapHasFluidOutputs(getRecipeMap())) checkHasOutputHatch(errors);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        if (machineMode == MACHINEMODE_ABS) {
            // 400% speed (1/4 duration) and up to 8 parallels, ABS-mode only.
            return new ProcessingLogic().setSpeedBonus(1F / 4F)
                .setMaxParallelSupplier(this::getTrueParallel);
        }
        return new ProcessingLogic();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        if (machineMode == MACHINEMODE_ABS) {
            return RecipeMaps.alloyBlastSmelterRecipes;
        }
        return GTNHPPRecipeMaps.sCRVRecipes;
    }

    // getRecipeMap() only reflects whichever mode is currently selected, so NEI's default catalyst
    // lookup (which reads getRecipeMap() once, on a fresh MACHINEMODE_CRV-by-default instance) never
    // discovers the ABS-mode recipes. Overriding this exposes both maps to NEI regardless of mode.
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(GTNHPPRecipeMaps.sCRVRecipes, RecipeMaps.alloyBlastSmelterRecipes);
    }

    @Override
    public int getMaxParallelRecipes() {
        return machineMode == MACHINEMODE_ABS ? 8 : super.getMaxParallelRecipes();
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    // Default getMachineModeKey() just returns "GT5U.MULTI_MACHINE_MODE.unknown" ("Unknown Mode")
    // same MTEOreWashingPlant pattern as the GUI button fix above.
    @Override
    public String getMachineModeKey() {
        return "GT5U.GTNHPP_CRV.mode." + machineMode;
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_COMPRESSING);
    }

    // setMachineModeIcons() alone no longer surfaces a GUI button, MTEMultiBlockBaseGui (the actual
    // rendered GUI since the ModularUI2 migration) only adds the mode-switch widget when its OWN,
    // separate machineModeIcons list (populated here) is non-empty. See MTEOreWashingPlant for the
    // upstream reference implementation of this exact pattern. Note this needs the ModularUI2
    // GTGuiTextures icons specifically (withMachineModeIcons takes com.cleanroommc's UITexture, not
    // GTUITextures' ModularUI1 one used by setMachineModeIcons() above).
    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new MTEMultiBlockBaseGui<>(this).withMachineModeIcons(
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_COMPRESSING);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { casingTexturePages[0][CASING_INDEX], TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_MEGA_CHEMICAL_REACTOR_ACTIVE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_MEGA_CHEMICAL_REACTOR_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { casingTexturePages[0][CASING_INDEX], TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_MEGA_CHEMICAL_REACTOR)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_MEGA_CHEMICAL_REACTOR_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { casingTexturePages[0][CASING_INDEX] };
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Reaction Vessel, Alloy Blast Smelter, CRV")
            .addInfo(
                "Accepts up to " + TooltipHelper.coloredText("6", EnumChatFormatting.YELLOW)
                    + " molten metal fluid inputs at once")
            .addInfo(
                "Requires exactly " + TooltipHelper.coloredText("24", EnumChatFormatting.YELLOW)
                    + " Hexagonal Boron Nitride Ceramic Blocks as the inner lining")
            .addInfo(
                "Switch between " + EnumChatFormatting.GOLD
                    + "CRV"
                    + EnumChatFormatting.GRAY
                    + " and "
                    + EnumChatFormatting.GOLD
                    + "Alloy Blast Smelter"
                    + EnumChatFormatting.GRAY
                    + " recipes with the mode button in the GUI")
            .addInfo(
                "Alloy Blast Smelter mode: " + TooltipHelper.speedText(4f)
                    + " speed and "
                    + TooltipHelper.parallelText(8)
                    + " Parallels")
            .beginStructureBlock(5, 5, 5, true)
            .addController("Front center, 3rd layer")
            .addCasing("74+", "Iridium-Reinforced Reactor Casing", false)
            .addCasing("24", "Hexagonal Boron Nitride Ceramic Block", true)
            .addEnergyHatch("1+", "Any iridium-reinforced reactor casing", 1)
            .addMaintenanceHatch("1", "Any iridium-reinforced reactor casing", 1)
            .addMufflerHatch("1", "Any iridium-reinforced reactor casing", 1)
            .addInputAny("1+", "Any iridium-reinforced reactor casing", 1)
            .addOutputAny("1+", "Any iridium-reinforced reactor casing", 1)
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
