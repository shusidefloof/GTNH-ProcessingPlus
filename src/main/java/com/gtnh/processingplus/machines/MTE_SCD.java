package com.gtnh.processingplus.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.Muffler;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_MULTI_BREWERY;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_MULTI_BREWERY_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_MULTI_BREWERY_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_MULTI_BREWERY_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;

/**
 * Supercritical Dryer — high-pressure autoclave for scCO₂ gel drying and extraction.
 *
 * <p>
 * Runs a 3-stage process that the player must actively manage via fluid inputs:
 * <ul>
 * <li><b>Stage 1 — Solvent Purge</b>: drain a recipe-specific solvent per tick,
 * or keep the hatch empty (recipe-dependent). Stalls if the wrong fluid is present.</li>
 * <li><b>Stage 2 — Supercritical Infusion</b>: inject a registered supercritical fluid.
 * Higher-tier fluids (Freon R-12) advance the stage faster. Stalls if no valid fluid.</li>
 * <li><b>Stage 3 — Depressurization</b>: hatch must be empty. Any fluid present at
 * any point during stage 3 marks the batch as contaminated → degraded output.</li>
 * </ul>
 * Energy is consumed every tick regardless of stall state.
 */
public class MTE_SCD extends MTEExtendedPowerMultiBlockBase<MTE_SCD> implements ISurvivalConstructable {

    // -------------------------------------------------------------------------
    // Stage mechanic — static registries
    // -------------------------------------------------------------------------

    /**
     * Stage 1 fluid IDs for mSpecialValue encoding.
     * {@code STAGE1_EMPTY} (0) means stage 1 requires an empty hatch rather than a specific fluid.
     */
    public static final int STAGE1_EMPTY = 0;
    public static final int STAGE1_ACETONE = 1;
    public static final int STAGE1_ETHANOL = 2;
    public static final int STAGE1_IPA = 3;

    /** Stage 1 fluid ID → Fluid. Populated from SCDRecipes.init(). */
    private static final Map<Integer, Fluid> STAGE1_FLUIDS = new HashMap<>();

    /**
     * Valid stage 2 fluids → extra effective ticks per real tick.
     * <ul>
     * <li>0 = baseline 1× speed (Liquid CO₂)</li>
     * <li>1 = 2× faster (Freon R-12)</li>
     * </ul>
     * A LinkedHashMap so iteration order is deterministic when scanning hatches.
     * Populated from SCDRecipes.init().
     */
    private static final Map<Fluid, Integer> STAGE2_TIERS = new LinkedHashMap<>();

    public static void registerStage1Fluid(int id, Fluid fluid) {
        STAGE1_FLUIDS.put(id, fluid);
    }

    public static void registerStage2Fluid(Fluid fluid, int extraTicksPerTick) {
        STAGE2_TIERS.put(fluid, extraTicksPerTick);
    }

    /**
     * Encodes stage fluid parameters into a mSpecialValue int for recipe registration.
     *
     * <pre>
     *   bits  0-3  : stage 1 fluid ID (0-15)
     *   bits  4-11 : stage 1 per-tick drain in mB (0-255; 0 when stage1 is empty check)
     *   bits 12-19 : stage 2 per-tick drain in mB (0-255)
     * </pre>
     */
    public static int encodeStageData(int stage1FluidId, int stage1PerTick, int stage2PerTick) {
        return (stage1FluidId & 0xF) | ((stage1PerTick & 0xFF) << 4) | ((stage2PerTick & 0xFF) << 12);
    }

    private static int decodeStage1FluidId(GTRecipe r) {
        return r.mSpecialValue & 0xF;
    }

    private static int decodeStage1PerTick(GTRecipe r) {
        return (r.mSpecialValue >> 4) & 0xFF;
    }

    private static int decodeStage2PerTick(GTRecipe r) {
        return (r.mSpecialValue >> 12) & 0xFF;
    }

    // -------------------------------------------------------------------------
    // Stage state fields
    // -------------------------------------------------------------------------

    /** 0 = idle, 1/2/3 = active drying stage. */
    private int mDryingStage = 0;
    /** Effective tick counter within the current stage (advances faster with better stage-2 fluids). */
    private int mStageTicks = 0;
    private int mStage1MaxTicks = 0;
    private int mStage2MaxTicks = 0;
    private int mStage3MaxTicks = 0;
    /** Set during onRunningTick when the stage is waiting for the correct fluid condition. */
    private boolean mStalling = false;
    /** True once any fluid is detected in an input hatch during stage 3. */
    private boolean mStage3HasFluid = false;
    /** Stage data decoded from the active recipe's mSpecialValue. */
    private int mStage1FluidId = STAGE1_EMPTY;
    private int mStage1PerTick = 0;
    private int mStage2PerTick = 0;
    /** Outputs captured at recipe start; pushed at stage-3 completion. */
    private ItemStack mPerfectOutput = null;
    private ItemStack mDegradedOutput = null;
    private FluidStack[] mSavedFluidOutputs = new FluidStack[0];

    // -------------------------------------------------------------------------
    // Structure constants
    // -------------------------------------------------------------------------

    // StableTitaniumMachineCasing (sBlockCasings4 meta2) — matches SCD_CASING's borrowed texture.
    private static final int CASING_INDEX = 50;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int OFFSET_X = 7;
    private static final int OFFSET_Y = 5;
    private static final int OFFSET_Z = 0;

    private static IStructureDefinition<MTE_SCD> STRUCTURE_DEFINITION = null;

    private static net.minecraft.block.Block gtBlock(String name) {
        net.minecraft.block.Block b = GameRegistry.findBlock("gregtech", name);
        if (b == null) throw new RuntimeException("SCD requires GregTech block: " + name);
        return b;
    }

    private static net.minecraft.block.Block bwBlock(String name) {
        net.minecraft.block.Block b = GameRegistry.findBlock("bartworks", name);
        if (b == null) throw new RuntimeException("SCD requires Bartworks block: " + name);
        return b;
    }

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

    // -------------------------------------------------------------------------
    // Structure
    // -------------------------------------------------------------------------

    @Override
    public IStructureDefinition<MTE_SCD> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_SCD>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    new String[][] { { // z=0 — front face; controller (~) at row 5 col 7
                        "            ", "            ", "       C    ", "      ECE   ", "HFHFFEBDBE  ", "HGH CCD~DCC ",
                        "HAH  EBDBE  ", "HAH   ECE   ", "HGH    C    ", "H H         " },
                        { // z=1
                            "            ", "            ", "      ICI   ", " FFFFIB BI  ", "FBBBBB   BI ",
                            "GBGFBB    C ", "A A IB   BI ", "A A HIB BIH ", "GGG H ICIHH ", "    H     H " },
                        { // z=2
                            "            ", "      EEE   ", "     I C I  ", "    I     I ", "HFHE       E",
                            "HGHEC     CE", "HAHE       E", "HAH I     I ", "HGH HI C IH ", "H H   EEE   " },
                        { // z=3
                            "            ", "      EAE   ", "    II   II ", "    I     I ", "   E       E",
                            "   A       A", "   E       E", "    I     I ", "    II   II ", "      EAE   " },
                        { // z=4
                            "            ", "      EAE   ", "    II   II ", "    I     I ", "   E       E",
                            "   A       A", "   E       E", "    I     I ", "    II   II ", "      EAE   " },
                        { // z=5
                            "            ", "      EAE   ", "    II   II ", "    I     I ", "   E       E",
                            "   A       A", "   E       E", "    I     I ", "    II   II ", "      EAE   " },
                        { // z=6
                            "            ", "      EEE   ", "     I C I  ", "    I     I ", "HFHE       E",
                            "HGHEC     CE", "HAHE       E", "HAH I     I ", "HGH HI C IH ", "H H   EEE   " },
                        { // z=7
                            "            ", "            ", "      ICI   ", " FFFFIB BI  ", "FBBBBB   BI ",
                            "GBGFB     C ", "A A IB   BI ", "A A HIB BIH ", "GGG HHICIHH ", "    H     H " },
                        { // z=8 — back face
                            "            ", "            ", "       C    ", "      ECE   ", "HFHFFEBDBE  ",
                            "HGH CCDCDCC ", "HAH  EBDBE  ", "HAH   ECE   ", "HGH    C    ", "H H         " } })
                .addElement('A', ofBlock(bwBlock("BW_GlasBlocks"), 0))
                .addElement('B', ofBlock(gtBlock("gt.blockcasings10"), 9))
                .addElement('C', ofBlock(gtBlock("gt.blockcasings2"), 1))
                .addElement('D', ofBlock(gtBlock("gt.blockcasings2"), 15))
                .addElement('E', ofBlock(gtBlock("gt.blockcasings8"), 5))
                .addElement('F', ofBlock(gtBlock("gt.blockframes"), 404))
                .addElement('G', ofBlock(gtBlock("gt.blockmetal3"), 3))
                .addElement('H', ofBlock(gtBlock("gt.sheetmetal"), 404))
                .addElement(
                    'I',
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
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        checkPiece(STRUCTURE_PIECE_MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z, errors);
        if (mMaintenanceHatches.size() != 1) errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
        // Stage fluids are not declared as recipe inputs (they're consumed per-tick).
        // Disable the hatch filter so the player can insert any fluid during a recipe run.
        for (MTEHatchInput hatch : mInputHatches) {
            hatch.disableFilter = true;
        }
    }

    // -------------------------------------------------------------------------
    // Processing — stage machine
    // -------------------------------------------------------------------------

    private class SCDProcessingLogic extends ProcessingLogic {

        GTRecipe getLastRecipe() {
            return lastRecipe;
        }
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new SCDProcessingLogic();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNHPPRecipeMaps.sSCDRecipes;
    }

    /**
     * Finds the recipe via the standard GT path (consumes item inputs), then hijacks the timing:
     * the GT auto-completion sentinel is set to Integer.MAX_VALUE/2 so the machine never completes
     * by itself — stage logic drives termination instead.
     */
    @Override
    public CheckRecipeResult checkProcessing() {
        mDryingStage = 0;
        mStageTicks = 0;
        mStalling = false;
        mStage3HasFluid = false;

        CheckRecipeResult result = super.checkProcessing();
        if (!result.wasSuccessful()) return result;

        GTRecipe recipe = ((SCDProcessingLogic) processingLogic).getLastRecipe();
        if (recipe == null) return result;

        mStage1FluidId = decodeStage1FluidId(recipe);
        mStage1PerTick = decodeStage1PerTick(recipe);
        mStage2PerTick = decodeStage2PerTick(recipe);

        // Capture outputs before clearing them; we push the correct one at stage-3 completion.
        mPerfectOutput = (mOutputItems != null && mOutputItems.length > 0) ? mOutputItems[0].copy() : null;
        mDegradedOutput = (mOutputItems != null && mOutputItems.length > 1) ? mOutputItems[1].copy() : null;
        if (mOutputFluids != null && mOutputFluids.length > 0) {
            mSavedFluidOutputs = new FluidStack[mOutputFluids.length];
            for (int i = 0; i < mOutputFluids.length; i++) {
                mSavedFluidOutputs[i] = mOutputFluids[i] != null ? mOutputFluids[i].copy() : null;
            }
        } else {
            mSavedFluidOutputs = new FluidStack[0];
        }
        mOutputItems = new ItemStack[0];
        mOutputFluids = new FluidStack[0];

        int total = mMaxProgresstime;
        mStage1MaxTicks = Math.max(1, total / 3);
        mStage2MaxTicks = Math.max(1, total / 3);
        mStage3MaxTicks = Math.max(1, total - 2 * (total / 3));

        // Sentinel prevents GT from auto-completing. Stage 3 sets mMaxProgresstime=1 when done.
        mMaxProgresstime = Integer.MAX_VALUE / 2;
        mProgresstime = 0;
        mDryingStage = 1;

        return result;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        mStalling = false;

        switch (mDryingStage) {
            case 1:
                tickStage1();
                break;
            case 2:
                tickStage2();
                break;
            case 3:
                tickStage3();
                break;
            default:
                break;
        }

        // Energy drains every tick — maintaining pressure costs power even while stalled.
        if (!super.onRunningTick(aStack)) return false;
        return true;
    }

    private void tickStage1() {
        Fluid needed = STAGE1_FLUIDS.get(mStage1FluidId);

        if (needed == null) {
            // Stage 1 is an empty-hatch check (STAGE1_EMPTY)
            if (hasAnyFluidInHatch()) {
                mStalling = true;
                return;
            }
        } else {
            if (mStage1PerTick > 0 && !depleteInput(new FluidStack(needed, mStage1PerTick))) {
                mStalling = true;
                return;
            }
        }

        if (++mStageTicks >= mStage1MaxTicks) {
            mDryingStage = 2;
            mStageTicks = 0;
        }
    }

    private void tickStage2() {
        Fluid detected = detectStage2Fluid();
        if (detected == null) {
            mStalling = true;
            return;
        }

        int extra = STAGE2_TIERS.get(detected);
        if (mStage2PerTick > 0 && !depleteInput(new FluidStack(detected, mStage2PerTick))) {
            mStalling = true;
            return;
        }

        // Better fluids advance stage 2 by multiple effective ticks per real tick.
        mStageTicks += (1 + extra);
        if (mStageTicks >= mStage2MaxTicks) {
            mDryingStage = 3;
            mStageTicks = 0;
        }
    }

    // 1-second grace window at the start of stage 3 — gives the player time to drain any remaining
    // stage-2 fluid before contamination tracking begins.
    private static final int STAGE3_GRACE_TICKS = 20;

    private void tickStage3() {
        // Skip contamination check during the grace window so the player can empty the hatch.
        if (mStageTicks >= STAGE3_GRACE_TICKS && hasAnyFluidInHatch()) {
            mStage3HasFluid = true;
        }

        if (++mStageTicks >= mStage3MaxTicks) {
            completeRecipe();
        }
    }

    private void completeRecipe() {
        // Restore outputs so GT's doPostProcessing() pushes them to output hatches/buses.
        ItemStack chosenItem = mStage3HasFluid ? mDegradedOutput : mPerfectOutput;
        mOutputItems = (chosenItem != null) ? new ItemStack[] { chosenItem } : new ItemStack[0];
        mOutputFluids = (mSavedFluidOutputs != null) ? mSavedFluidOutputs : new FluidStack[0];

        mPerfectOutput = null;
        mDegradedOutput = null;
        mSavedFluidOutputs = new FluidStack[0];
        mDryingStage = 0;
        mStageTicks = 0;
        mStalling = false;
        mStage3HasFluid = false;

        // Set mMaxProgresstime=1, mProgresstime=0 so GT's tick loop increments to 1==1
        // and fires doPostProcessing() this same tick, outputting mOutputItems/mOutputFluids.
        mMaxProgresstime = 1;
        mProgresstime = 0;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBase, long aTick) {
        super.onPostTick(aBase, aTick);
        // Undo the mProgresstime++ that super just did to keep the sentinel frozen while stalling.
        // Guard against mMaxProgresstime==1 (completion signal) so we don't interfere with that path.
        if (aBase.isServerSide() && mStalling && mMaxProgresstime > 1 && mProgresstime > 0) {
            mProgresstime--;
        }
    }

    // -------------------------------------------------------------------------
    // Fluid hatch helpers
    // -------------------------------------------------------------------------

    /** True if any input hatch contains fluid. Uses the public {@code mFluid} field on MTEBasicTank. */
    private boolean hasAnyFluidInHatch() {
        for (MTEHatchInput hatch : mInputHatches) {
            if (hatch.mFluid != null && hatch.mFluid.amount > 0) return true;
        }
        return false;
    }

    /** Returns the first stage-2-eligible fluid found in any input hatch, or null if none. */
    private Fluid detectStage2Fluid() {
        for (MTEHatchInput hatch : mInputHatches) {
            if (hatch.mFluid != null && hatch.mFluid.amount > 0 && STAGE2_TIERS.containsKey(hatch.mFluid.getFluid())) {
                return hatch.mFluid.getFluid();
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Info data
    // -------------------------------------------------------------------------

    @Override
    public String[] getInfoData() {
        String[] base = super.getInfoData();
        List<String> info = new ArrayList<>(Arrays.asList(base));

        switch (mDryingStage) {
            case 0:
                info.add(EnumChatFormatting.GRAY + "Stage: " + EnumChatFormatting.WHITE + "Idle");
                break;
            case 1: {
                info.add(EnumChatFormatting.GRAY + "Stage: " + EnumChatFormatting.WHITE + "1 — Solvent Purge");
                info.add(EnumChatFormatting.GRAY + "Progress: " + mStageTicks + " / " + mStage1MaxTicks + " t");
                if (mStalling) {
                    Fluid f = STAGE1_FLUIDS.get(mStage1FluidId);
                    String what = (f == null) ? "empty hatch" : f.getLocalizedName(null);
                    info.add(EnumChatFormatting.RED + "STALLED — waiting for " + what);
                } else {
                    info.add(EnumChatFormatting.YELLOW + "Running");
                }
                break;
            }
            case 2: {
                info.add(EnumChatFormatting.GRAY + "Stage: " + EnumChatFormatting.WHITE + "2 — Supercritical Infusion");
                info.add(
                    EnumChatFormatting.GRAY + "Progress: " + mStageTicks + " / " + mStage2MaxTicks + " t (effective)");
                if (mStalling) {
                    info.add(EnumChatFormatting.RED + "STALLED — waiting for valid supercritical fluid");
                } else {
                    info.add(EnumChatFormatting.AQUA + "Pressurizing");
                }
                break;
            }
            case 3: {
                info.add(EnumChatFormatting.GRAY + "Stage: " + EnumChatFormatting.WHITE + "3 — Depressurization");
                info.add(EnumChatFormatting.GRAY + "Progress: " + mStageTicks + " / " + mStage3MaxTicks + " t");
                if (mStage3HasFluid) {
                    info.add(EnumChatFormatting.RED + "CONTAMINATED — degraded output");
                } else {
                    info.add(EnumChatFormatting.GREEN + "Clean — perfect output");
                }
                break;
            }
            default:
                break;
        }

        return info.toArray(new String[0]);
    }

    // -------------------------------------------------------------------------
    // NBT
    // -------------------------------------------------------------------------

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mDryingStage", mDryingStage);
        aNBT.setInteger("mStageTicks", mStageTicks);
        aNBT.setInteger("mStage1MaxTicks", mStage1MaxTicks);
        aNBT.setInteger("mStage2MaxTicks", mStage2MaxTicks);
        aNBT.setInteger("mStage3MaxTicks", mStage3MaxTicks);
        aNBT.setBoolean("mStage3HasFluid", mStage3HasFluid);
        aNBT.setInteger("mStage1FluidId", mStage1FluidId);
        aNBT.setInteger("mStage1PerTick", mStage1PerTick);
        aNBT.setInteger("mStage2PerTick", mStage2PerTick);
        if (mPerfectOutput != null) {
            NBTTagCompound t = new NBTTagCompound();
            mPerfectOutput.writeToNBT(t);
            aNBT.setTag("mPerfectOutput", t);
        }
        if (mDegradedOutput != null) {
            NBTTagCompound t = new NBTTagCompound();
            mDegradedOutput.writeToNBT(t);
            aNBT.setTag("mDegradedOutput", t);
        }
        if (mSavedFluidOutputs != null && mSavedFluidOutputs.length > 0) {
            aNBT.setInteger("mFluidOutCount", mSavedFluidOutputs.length);
            for (int i = 0; i < mSavedFluidOutputs.length; i++) {
                if (mSavedFluidOutputs[i] != null) {
                    NBTTagCompound t = new NBTTagCompound();
                    mSavedFluidOutputs[i].writeToNBT(t);
                    aNBT.setTag("mFluidOut" + i, t);
                }
            }
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mDryingStage = aNBT.getInteger("mDryingStage");
        mStageTicks = aNBT.getInteger("mStageTicks");
        mStage1MaxTicks = aNBT.getInteger("mStage1MaxTicks");
        mStage2MaxTicks = aNBT.getInteger("mStage2MaxTicks");
        mStage3MaxTicks = aNBT.getInteger("mStage3MaxTicks");
        mStage3HasFluid = aNBT.getBoolean("mStage3HasFluid");
        mStage1FluidId = aNBT.getInteger("mStage1FluidId");
        mStage1PerTick = aNBT.getInteger("mStage1PerTick");
        mStage2PerTick = aNBT.getInteger("mStage2PerTick");
        if (aNBT.hasKey("mPerfectOutput")) {
            mPerfectOutput = ItemStack.loadItemStackFromNBT(aNBT.getCompoundTag("mPerfectOutput"));
        }
        if (aNBT.hasKey("mDegradedOutput")) {
            mDegradedOutput = ItemStack.loadItemStackFromNBT(aNBT.getCompoundTag("mDegradedOutput"));
        }
        int fluidCount = aNBT.getInteger("mFluidOutCount");
        mSavedFluidOutputs = new FluidStack[fluidCount];
        for (int i = 0; i < fluidCount; i++) {
            if (aNBT.hasKey("mFluidOut" + i)) {
                mSavedFluidOutputs[i] = FluidStack.loadFluidStackFromNBT(aNBT.getCompoundTag("mFluidOut" + i));
            }
        }
        // Restore the sentinel so the stage machine resumes correctly after a server restart.
        if (mDryingStage > 0) {
            mMaxProgresstime = Integer.MAX_VALUE / 2;
        }
    }

    // -------------------------------------------------------------------------
    // Rendering, sound, tooltip
    // -------------------------------------------------------------------------

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { casingTexturePages[0][CASING_INDEX], TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_MULTI_BREWERY_ACTIVE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_MULTI_BREWERY_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { casingTexturePages[0][CASING_INDEX], TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_MULTI_BREWERY)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_MULTI_BREWERY_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { casingTexturePages[0][CASING_INDEX] };
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_EBF_LOOP;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Supercritical Dryer, SCD")
            .addInfo(
                EnumChatFormatting.GRAY + "Pressurizes gel samples through "
                    + EnumChatFormatting.AQUA
                    + "three active drying stages"
                    + EnumChatFormatting.GRAY
                    + " that you must manage via the input hatch.")
            .addSeparator()
            .addInfo(
                EnumChatFormatting.AQUA + "Stage 1 — Solvent Purge"
                    + EnumChatFormatting.GRAY
                    + ": fill the hatch with the recipe's required solvent, or keep it empty.")
            .addInfo(
                EnumChatFormatting.AQUA + "Stage 2 — Supercritical Infusion"
                    + EnumChatFormatting.GRAY
                    + ": inject a supercritical fluid (Liquid CO₂, Freon R-12, …).")
            .addInfo(
                EnumChatFormatting.GRAY + "  "
                    + EnumChatFormatting.WHITE
                    + "Better fluids"
                    + EnumChatFormatting.GRAY
                    + " reduce stage‑2 time: Freon R-12 is "
                    + EnumChatFormatting.WHITE
                    + "2× faster"
                    + EnumChatFormatting.GRAY
                    + " than Liquid CO₂.")
            .addInfo(
                EnumChatFormatting.AQUA + "Stage 3 — Depressurization"
                    + EnumChatFormatting.GRAY
                    + ": hatch must be "
                    + EnumChatFormatting.WHITE
                    + "empty"
                    + EnumChatFormatting.GRAY
                    + ". Any fluid present collapses the gel → "
                    + EnumChatFormatting.RED
                    + "degraded output"
                    + EnumChatFormatting.GRAY
                    + ".")
            .addSeparator()
            .addInfo(
                EnumChatFormatting.YELLOW + "Machine stalls"
                    + EnumChatFormatting.GRAY
                    + " (no progress) when waiting for the correct fluid condition.")
            .addInfo(EnumChatFormatting.YELLOW + "Energy is consumed even while stalled.")
            .addInfo(
                EnumChatFormatting.GRAY + "Right-click the controller face with a "
                    + EnumChatFormatting.WHITE
                    + "scanner"
                    + EnumChatFormatting.GRAY
                    + " to read current stage and status.")
            .beginStructureBlock(12, 10, 9, true)
            .addController("Front face, center (row 5, col 7)")
            .addCasingInfoMin("High-Pressure Containment Casing", 1, false)
            .addInputBus("Any High-Pressure Containment Casing (I)", 1)
            .addInputHatch("Any High-Pressure Containment Casing (I)", 1)
            .addOutputBus("Any High-Pressure Containment Casing (I)", 1)
            .addOutputHatch("Any High-Pressure Containment Casing (I)", 1)
            .addEnergyHatch("Any High-Pressure Containment Casing (I)", 1)
            .addMufflerHatch("Any High-Pressure Containment Casing (I)", 1)
            .addMaintenanceHatch("Any High-Pressure Containment Casing (I)", 1)
            .toolTipFinisher("_Shusi_");
        return tt;
    }

    // -------------------------------------------------------------------------
    // Capabilities
    // -------------------------------------------------------------------------

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
