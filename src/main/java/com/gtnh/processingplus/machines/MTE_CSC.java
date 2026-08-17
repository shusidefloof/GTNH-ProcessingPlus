package com.gtnh.processingplus.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
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
import static gregtech.api.util.GTUtility.validMTEList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;

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
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.IToolStats;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.items.MetaGeneratedTool;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.TurbineStatCalculator;
import gregtech.api.util.shutdown.ShutDownReason;
import gregtech.api.util.tooltip.TooltipHelper;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchTurbine;

/**
 * Cryogenic Separation Column (CSC) — a 15×6×7 fractional-distillation tower, structure designed by
 * Lord of Turtle.
 */
public class MTE_CSC extends MTEExtendedPowerMultiBlockBase<MTE_CSC> implements ISurvivalConstructable {

    // FrostProofMachineCasing (sBlockCasings2 meta1) — matches CSC's actual wall (sBlockCasings2, meta1).
    private static final int CASING_INDEX = 17;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    // "Transposed Scan" export: outer/row axes are swapped vs. a normal scan, so the true shape is
    // [z=7][y=6][x=15] with the controller marker '~' at z=0, y=3, x=7 (front face, mid-height).
    private static final int OFFSET_X = 7, OFFSET_Y = 3, OFFSET_Z = 0;

    private static IStructureDefinition<MTE_CSC> STRUCTURE_DEFINITION = null;

    // Persisted: Freon shortfall from the previous run that must be paid this run
    private int mFreonDeficit = 0;
    private String mDeficitFluidName = "";

    // Transient: primary fluid captured before inputs are consumed each run
    private String mCurrentPrimaryFluid = "";

    // Rotor Assembly hatches (MTEHatchTurbine) — each holds a real turbine rotor item (right-click to
    // insert, same as GT5's Large Turbines/Spinmatron). Summed rotor optimal flow gives a speed bonus.
    public ArrayList<MTEHatchTurbine> mRotorAssemblies = new ArrayList<>();

    // Speed-bonus tuning: higher combined rotor optimal flow -> lower (faster) duration multiplier,
    // floored so a rotor can never make the recipe instant.
    private static final double ROTOR_SPEED_DIVISOR = 500.0;
    private static final double ROTOR_SPEED_FLOOR = 0.001;

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
                    /*
                     * Block legend (from the in-game structure export, credit: Lord of Turtle):
                     * A -> gt.blockcasings2:1 — Frost Proof Machine Casing (hatch-capable shell)
                     * B -> gt.blockcasings8:4 — Extreme Engine Intake Casing (top/bottom cap accent)
                     * C -> gt.blockframes:407 — Energetic Silver Frame Box
                     * F -> Rotor Assembly (MTEHatchTurbine, "hatch.turbine" — the real placeable GT++
                     * rotor-assembly block/hatch, same class GT5's MTESpinmatron uses; insert a turbine
                     * rotor tool and its optimal gas flow stat speeds up recipes, see getRotorSpeedBonus)
                     * G -> gt.blockcasings8:4 — Air Intake (hatch-capable, Muffler only)
                     */
                    new String[][] {
                        { "               ", "     CAAAC     ", "     AAAAA     ", "    CAA~AAC    ", "     AAAAA     ",
                            "     CAAAC     " },
                        { "     BABAB     ", "    CA   AC    ", "    A     A    ", "   CA     AC   ", "    A     A    ",
                            "    CAAAAAC    " },
                        { "    ABABABA    ", "   CA     AC   ", "AAAA       AAAA", "GGGA       AGGG", "AAAA       AAAA",
                            "C  CAAAAAAAC  C" },
                        { "    ABABABA    ", "   CA     AC   ", "DDDA       ADDD", "F          A  F", "DDDA       ADDD",
                            "   CAAAAAAAC   " },
                        { "    ABABABA    ", "   CA     AC   ", "AAAA       AAAA", "GGGA       AGGG", "AAAA       AAAA",
                            "C  CAAAAAAAC  C" },
                        { "     BABAB     ", "    CA   AC    ", "    A     A    ", "   CA     AC   ", "    A     A    ",
                            "    CAAAAAC    " },
                        { "               ", "     CAAAC     ", "     AAAAA     ", "    CAAAAAC    ", "     AAAAA     ",
                            "     CAAAC     " } })
                .addElement(
                    'A',
                    buildHatchAdder(MTE_CSC.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance, Muffler)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GregTechAPI.sBlockCasings2, 1))
                .addElement('B', ofBlock(GregTechAPI.sBlockCasings8, 4))
                .addElement('C', ofBlock(GregTechAPI.sBlockFrames, 407))
                .addElement(
                    'D',
                    buildHatchAdder(MTE_CSC.class)
                        .atLeast(Energy, InputBus, OutputBus, OutputHatch, Maintenance, Muffler)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GregTechAPI.sBlockCasings2, 1))
                .addElement('F', CSCHatchElement.ROTOR_ASSEMBLY.newAny(CASING_INDEX, 2))
                .addElement(
                    'G',
                    buildHatchAdder(MTE_CSC.class).atLeast(InputHatch)
                        .casingIndex(CASING_INDEX)
                        .hint(2)
                        .buildAndChain(GregTechAPI.sBlockCasings2, 1))
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
        mRotorAssemblies.clear();
        checkPiece(STRUCTURE_PIECE_MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z, errors);
        if (mMaintenanceHatches.size() != 1) errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
    }

    public boolean addRotorAssembly(final IGregTechTileEntity aTileEntity, final int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        final IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity instanceof MTEHatchTurbine turbine) {
            turbine.updateTexture(aBaseCasingIndex);
            mRotorAssemblies.add(turbine);
            return true;
        }
        return false;
    }

    private enum CSCHatchElement implements IHatchElement<MTE_CSC> {

        ROTOR_ASSEMBLY(MTE_CSC::addRotorAssembly, MTEHatchTurbine.class) {

            @Override
            public long count(MTE_CSC t) {
                return t.mRotorAssemblies.size();
            }
        };

        private final List<Class<? extends IMetaTileEntity>> mteClasses;
        private final IGTHatchAdder<MTE_CSC> adder;

        @SafeVarargs
        CSCHatchElement(IGTHatchAdder<MTE_CSC> adder, Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        @Override
        public IGTHatchAdder<? super MTE_CSC> adder() {
            return adder;
        }
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

    // Sums the stored amount of a fluid by registry name, for the ±2% deficit-repayment check.
    private int getStoredAmount(String fluidName) {
        int total = 0;
        for (FluidStack fs : getStoredFluids()) {
            if (fs != null && fs.getFluid() != null && fs.getFluid()
                .getName()
                .equals(fluidName)) {
                total += fs.amount;
            }
        }
        return total;
    }

    // Called after recipe succeeds and inputs are consumed — enforce deficit, randomize Freon
    @Override
    protected CheckRecipeResult postCheckRecipe(CheckRecipeResult result, ProcessingLogic logic) {
        result = super.postCheckRecipe(result, logic);
        if (!result.wasSuccessful()) return result;

        // Enforce deficit from previous run. Repayment must land within ±2% of the exact deficit —
        // too little (can't cover the debt) OR too much (overpressuring the loop) both explode.
        if (mFreonDeficit > 0 && !mDeficitFluidName.isEmpty()) {
            int stored = getStoredAmount(mDeficitFluidName);
            double lowerBound = mFreonDeficit * 0.98;
            double upperBound = mFreonDeficit * 1.02;
            if (stored < lowerBound || stored > upperBound) {
                getBaseMetaTileEntity().doExplosion(GTValues.V[4] * 16);
                mFreonDeficit = 0;
                mDeficitFluidName = "";
                return SimpleCheckRecipeResult.ofFailure("freon_debt_explosion");
            }
            int toDeplete = Math.min(stored, mFreonDeficit);
            if (toDeplete > 0) {
                depleteInput(FluidRegistry.getFluidStack(mDeficitFluidName, toDeplete));
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
        return new ProcessingLogic() {

            @Override
            protected CheckRecipeResult onRecipeStart(@Nonnull GTRecipe recipe) {
                setTurbineActive();
                return super.onRecipeStart(recipe);
            }
        }.setSpeedBonusSupplier(this::getRotorSpeedBonus);
    }

    @Override
    public void stopMachine(@Nonnull ShutDownReason reason) {
        setTurbineInactive();
        super.stopMachine(reason);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aTick % 100 == 0) {
            if (!getBaseMetaTileEntity().isActive() && !this.mRotorAssemblies.isEmpty()) {
                setTurbineInactive();
            }
        }
    }

    private void setTurbineActive() {
        for (MTEHatchTurbine hatch : validMTEList(mRotorAssemblies)) {
            hatch.setActive(true);
            hatch.onTextureUpdate();
        }
    }

    private void setTurbineInactive() {
        for (MTEHatchTurbine hatch : validMTEList(mRotorAssemblies)) {
            hatch.setActive(false);
            hatch.onTextureUpdate();
        }
    }

    // Sums TurbineStatCalculator.getOptimalGasFlow() across every valid rotor item inserted into the
    // Rotor Assembly hatches, then converts it into a duration multiplier (< 1 = faster). Gas flow (not
    // the base/steam/plasma variants) since the CSC's whole job is cryogenic gas separation (air, CO2).
    // Mirrors how real GT5 turbines derive optimal flow from the rotor's speed multiplier, material tool
    // speed, and material gas multiplier, without the flow-matching/loose-efficiency curve those use for
    // EU generation — CSC recipes are fixed-amount, not a tunable fluid stream, so only the raw rotor
    // stat carries over.
    private double getRotorSpeedBonus() {
        double totalFlow = 0;
        for (MTEHatchTurbine hatch : validMTEList(mRotorAssemblies)) {
            ItemStack rotor = hatch.getTurbine();
            if (!isValidRotor(rotor)) continue;
            totalFlow += new TurbineStatCalculator((MetaGeneratedTool) rotor.getItem(), rotor).getOptimalGasFlow();
        }
        if (totalFlow <= 0) return 1.0;
        return Math.max(ROTOR_SPEED_FLOOR, 1.0 / (1.0 + totalFlow / ROTOR_SPEED_DIVISOR));
    }

    private static boolean isValidRotor(ItemStack aStack) {
        if (aStack == null || !(aStack.getItem() instanceof MetaGeneratedTool tool)) return false;
        if (aStack.getItemDamage() < 170 || aStack.getItemDamage() > 179) return false;
        IToolStats stats = tool.getToolStats(aStack);
        if (stats == null || stats.getSpeedMultiplier() <= 0) return false;
        Materials material = MetaGeneratedTool.getPrimaryMaterial(aStack);
        return material != null && material.mToolSpeed > 0;
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
                EnumChatFormatting.GRAY + "Each rotor's "
                    + TooltipHelper.coloredText("Optimal Gas Flow", EnumChatFormatting.YELLOW)
                    + EnumChatFormatting.GRAY
                    + " stat is added together across both slots.")
            .addInfo(
                "" + EnumChatFormatting.DARK_GRAY
                    + EnumChatFormatting.ITALIC
                    + "Purely a speed buff rotors don't consume fluid or take damage here.")
            .addInfo(
                EnumChatFormatting.WHITE + "Duration: "
                    + EnumChatFormatting.GRAY
                    + "×= max(0.001, 1 / (1 + ΣOptimalGasFlow / 500))")
            .beginStructureBlock(15, 6, 7, true)
            .addController("See NEI structure preview")
            .addCasingInfoMin("Frost Proof Machine Casing", 100, false)
            .addOtherStructurePart("Extreme Engine Intake Casing", "Top/bottom cap accent")
            .addOtherStructurePart("Energetic Silver Frame Box", "Structural framing")
            .addOtherStructurePart("Rotor Assembly", "Center slice, front-facing (2 required)")
            .addMufflerHatch("Air Intake (Extreme Engine Intake Casing only)", 2)
            .addInputBus("Any Frost Proof Machine Casing", 1)
            .addInputHatch("Any Frost Proof Machine Casing", 1)
            .addOutputBus("Any Frost Proof Machine Casing", 1)
            .addOutputHatch("Any Frost Proof Machine Casing", 1)
            .addEnergyHatch("Any Frost Proof Machine Casing", 1)
            .addMufflerHatch("Any Frost Proof Machine Casing", 1)
            .addMaintenanceHatch("Any Frost Proof Machine Casing", 1)
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
        String deficitLine = mFreonDeficit > 0
            ? EnumChatFormatting.RED + "Freon debt: +"
                + mFreonDeficit
                + " mB "
                + mDeficitFluidName
                + " required next run"
            : EnumChatFormatting.GREEN + "Freon: stable";
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
        lines.add(deficitLine);
        lines.addAll(getRotorDebugLines());
        return lines.toArray(new String[0]);
    }

    // Diagnostic breakdown of the Rotor Assembly speed bonus — shows exactly what each hatch sees so we
    // can tell "no item in slot" from "item rejected by isValidRotor" from "item accepted, flow computed".
    private List<String> getRotorDebugLines() {
        List<String> lines = new ArrayList<>();
        int i = 0;
        for (MTEHatchTurbine hatch : validMTEList(mRotorAssemblies)) {
            i++;
            ItemStack rotor = hatch.getTurbine();
            if (rotor == null) {
                lines.add(
                    EnumChatFormatting.GRAY + "Rotor Assembly "
                        + i
                        + ": "
                        + EnumChatFormatting.RED
                        + "no rotor detected (getTurbine() == null)");
                continue;
            }
            if (!isValidRotor(rotor)) {
                lines.add(
                    EnumChatFormatting.GRAY + "Rotor Assembly "
                        + i
                        + ": "
                        + EnumChatFormatting.RED
                        + "rejected — "
                        + rotor.getDisplayName());
                continue;
            }
            double flow = new TurbineStatCalculator((MetaGeneratedTool) rotor.getItem(), rotor).getOptimalGasFlow();
            lines.add(
                EnumChatFormatting.GRAY + "Rotor Assembly "
                    + i
                    + ": "
                    + EnumChatFormatting.GREEN
                    + rotor.getDisplayName()
                    + EnumChatFormatting.GRAY
                    + " — OptimalGasFlow="
                    + String.format("%.1f", flow));
        }
        lines.add(
            EnumChatFormatting.GOLD + "Rotor speed multiplier: "
                + EnumChatFormatting.YELLOW
                + String.format("%.3f", getRotorSpeedBonus())
                + EnumChatFormatting.GRAY
                + " (lower = faster, 1.0 = no rotors)");
        return lines;
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
