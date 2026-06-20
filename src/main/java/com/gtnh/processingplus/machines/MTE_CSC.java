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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.tooltip.TooltipHelper;

public class MTE_CSC extends MTEExtendedPowerMultiBlockBase<MTE_CSC> implements ISurvivalConstructable {

    private static final int CASING_INDEX = 11;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int OFFSET_X = 1, OFFSET_Y = 2, OFFSET_Z = 0;

    private static IStructureDefinition<MTE_CSC> STRUCTURE_DEFINITION = null;

    // Persisted: Freon shortfall from the previous run that must be paid this run
    private int mFreonDeficit = 0;
    private String mDeficitFluidName = "";

    // Transient: primary fluid captured before inputs are consumed each run
    private String mCurrentPrimaryFluid = "";

    public MTE_CSC(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected MTE_CSC(MTE_CSC prototype) {
        super(prototype.mName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTE_CSC(this);
    }

    @Override
    public IStructureDefinition<MTE_CSC> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_CSC>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    new String[][] { { "CCC", "CCC", "C~C", "CCC", "CCC" }, { "CCC", "CCC", "CCC", "CCC", "CCC" },
                        { "CCC", "CCC", "CCC", "CCC", "CCC" } })
                .addElement(
                    'C',
                    buildHatchAdder(MTE_CSC.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance, Muffler)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.CSC_CASING))
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

    // Called before inputs are consumed — capture primary (non-Freon) fluid for deficit tracking
    @Override
    protected void setupProcessingLogic(ProcessingLogic logic) {
        super.setupProcessingLogic(logic);
        Fluid freon = PrPMaterials.FreonR12.getFluidOrGas(1)
            .getFluid();
        mCurrentPrimaryFluid = "";
        for (FluidStack fs : getStoredFluids()) {
            if (fs != null && fs.getFluid() != freon && fs.amount > 0) {
                mCurrentPrimaryFluid = fs.getFluid()
                    .getName();
                break;
            }
        }
    }

    // Called after recipe succeeds and inputs are consumed — enforce deficit, randomize Freon
    @Override
    protected CheckRecipeResult postCheckRecipe(CheckRecipeResult result, ProcessingLogic logic) {
        result = super.postCheckRecipe(result, logic);
        if (!result.wasSuccessful()) return result;

        // Enforce deficit from previous run. If unpayable the machine explodes and inputs are forfeit.
        if (mFreonDeficit > 0 && !mDeficitFluidName.isEmpty()) {
            FluidStack debt = FluidRegistry.getFluidStack(mDeficitFluidName, mFreonDeficit);
            if (debt == null || !depleteInput(debt)) {
                getBaseMetaTileEntity().doExplosion(GTValues.V[4] * 16);
                mFreonDeficit = 0;
                mDeficitFluidName = "";
                return SimpleCheckRecipeResult.ofFailure("freon_debt_explosion");
            }
            mFreonDeficit = 0;
            mDeficitFluidName = "";
        }

        // Randomize Freon output: 0–10% extra loss beyond what the recipe planned to return
        Fluid freon = PrPMaterials.FreonR12.getFluidOrGas(1)
            .getFluid();
        FluidStack[] outputs = logic.getOutputFluids();
        if (outputs != null) {
            FluidStack[] modified = Arrays.copyOf(outputs, outputs.length);
            for (int i = 0; i < modified.length; i++) {
                FluidStack fs = modified[i];
                if (fs == null || fs.getFluid() != freon) continue;
                int expected = fs.amount;
                int variance = Math.max(1, expected / 10);
                int actual = expected - ThreadLocalRandom.current()
                    .nextInt(variance + 1);
                modified[i] = new FluidStack(fs.getFluid(), actual);
                int deficit = expected - actual;
                if (deficit > 0 && !mCurrentPrimaryFluid.isEmpty()) {
                    mDeficitFluidName = mCurrentPrimaryFluid;
                    mFreonDeficit = deficit;
                }
                break;
            }
            logic.overwriteOutputFluids(modified);
        }

        return result;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mFreonDeficit", mFreonDeficit);
        aNBT.setString("mDeficitFluidName", mDeficitFluidName);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mFreonDeficit = aNBT.getInteger("mFreonDeficit");
        mDeficitFluidName = aNBT.getString("mDeficitFluidName");
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNHPPRecipeMaps.sCSCRecipes;
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
        tt.addMachineType("Cryogenic Separation Column, CSC")
            .addInfo(
                EnumChatFormatting.GRAY + "Separates fluids by "
                    + EnumChatFormatting.AQUA
                    + "cryogenic distillation"
                    + EnumChatFormatting.GRAY
                    + ".")
            .addSeparator()
            .addStaticParallelInfo(8)
            .addInfo(
                EnumChatFormatting.RED + "Requires continuous Freon R-12 input."
                    + EnumChatFormatting.GRAY
                    + " Freon partially recovers each cycle.")
            .addInfo(
                EnumChatFormatting.GOLD + "Freon debt: "
                    + EnumChatFormatting.GRAY
                    + "random loss each cycle creates a deficit repaid on the next run.")
            .addInfo(EnumChatFormatting.DARK_RED + "Unpaid debt causes an explosion on the next run!")
            .addSeparator()
            .addInfo(
                TooltipHelper.coloredText("circuit(1)", EnumChatFormatting.AQUA) + EnumChatFormatting.GRAY
                    + "  50,000 mB Air → N₂ + O₂ + LiquidAr | ~"
                    + TooltipHelper.coloredText("500 mB", EnumChatFormatting.RED)
                    + EnumChatFormatting.GRAY
                    + " Freon lost")
            .addInfo(
                TooltipHelper.coloredText("circuit(2)", EnumChatFormatting.AQUA) + EnumChatFormatting.GRAY
                    + "  10,000 mB CO₂ → 9,000 mB LiquidCO₂ | ~"
                    + TooltipHelper.coloredText("150 mB", EnumChatFormatting.RED)
                    + EnumChatFormatting.GRAY
                    + " Freon lost")
            .beginStructureBlock(3, 5, 3, true)
            .addController("Front face, center")
            .addCasingInfoMin("Cryogenic Column Casing", 44, false)
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
        String deficitLine = mFreonDeficit > 0
            ? EnumChatFormatting.RED + "Freon debt: +"
                + mFreonDeficit
                + " mB "
                + mDeficitFluidName
                + " required next run"
            : EnumChatFormatting.GREEN + "Freon: stable";
        return new String[] { StatCollector.translateToLocal("GT5U.multiblock.Progress") + ": "
            + EnumChatFormatting.GREEN
            + mProgresstime / 20
            + EnumChatFormatting.RESET
            + " s / "
            + EnumChatFormatting.YELLOW
            + mMaxProgresstime / 20
            + EnumChatFormatting.RESET
            + " s", deficitLine };
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
