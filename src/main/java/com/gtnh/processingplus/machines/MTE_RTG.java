package com.gtnh.processingplus.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.Dynamo;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
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
import com.gtnh.processingplus.items.GTNHPPItems;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.tooltip.TooltipHelper;

/**
 * Radioisotope Thermoelectric Generator (RTG) — a UV-tier multiblock power source.
 * Burns Promethium Betavoltaic Cells: each cell's beta decay drives a steady EU output through the
 * dynamo hatches, and the more cells loaded into the input bus, the higher the sustained output
 * (up to {@link #MAX_CELLS} burned per cycle). The cells deplete over time, so it's a real Promethium
 * sink rather than free power.
 */
public class MTE_RTG extends MTEExtendedPowerMultiBlockBase<MTE_RTG> implements ISurvivalConstructable {

    private static final int CASING_INDEX = 11;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int OFFSET_X = 2;
    private static final int OFFSET_Y = 2;
    private static final int OFFSET_Z = 0;

    /** EU/t produced per active betavoltaic cell. */
    private static final int EU_PER_CELL = 32768;
    /** How long (ticks) one charge of cells burns before the next batch is drawn. */
    private static final int BURN_TIME = 600;

    private static IStructureDefinition<MTE_RTG> STRUCTURE_DEFINITION = null;

    public MTE_RTG(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected MTE_RTG(MTE_RTG prototype) {
        super(prototype.mName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTE_RTG(this);
    }

    @Override
    public IStructureDefinition<MTE_RTG> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_RTG>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    // shape[z][y][x] — 5×5×5, 'C' = Radioisotope casing or hatch, ' ' = shielded core void
                    new String[][] {
                        { "CCCCC", "CCCCC", "CC~CC", "CCCCC", "CCCCC" },
                        { "CCCCC", "C   C", "C   C", "C   C", "CCCCC" },
                        { "CCCCC", "C   C", "C   C", "C   C", "CCCCC" },
                        { "CCCCC", "C   C", "C   C", "C   C", "CCCCC" },
                        { "CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC" }, })
                .addElement(
                    'C',
                    buildHatchAdder(MTE_RTG.class)
                        .atLeast(Dynamo, InputBus, Maintenance)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.RADIOISOTOPE_CASING))
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
        if (mDynamoHatches.isEmpty() && mExoticDynamoHatches.isEmpty())
            errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
        if (mInputBusses.isEmpty()) errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
    }

    /**
     * Generator loop. Each cell is worth EU_PER_CELL, but the output is clamped to what the installed
     * dynamo hatches can actually emit — generating more than that makes GT explode the multiblock. So
     * it only burns as many cells as the dynamos can carry (the rest wait in the bus); you scale the
     * machine up by adding more / higher-tier dynamo hatches.
     */
    @Override
    public CheckRecipeResult checkProcessing() {
        // Total EU/t the dynamo hatches can emit. The output must never exceed this or the base explodes.
        long dynamoCap = 0;
        for (MTEHatchDynamo d : mDynamoHatches) {
            if (d == null) continue;
            dynamoCap += d.maxAmperesOut() * d.maxEUOutput();
        }
        for (MTEHatch d : mExoticDynamoHatches) {
            if (d == null) continue;
            dynamoCap += d.maxAmperesOut() * d.maxEUOutput();
        }
        if (dynamoCap <= 0) {
            lEUt = 0;
            mEUt = 0;
            mEfficiency = 0;
            return CheckRecipeResultRegistry.NO_FUEL_FOUND;
        }

        ItemStack cellProbe = GTNHPPItems.betavoltaicCell(1);
        List<ItemStack> cellStacks = new ArrayList<>();
        int available = 0;
        for (ItemStack s : getStoredInputs()) {
            if (s != null && s.stackSize > 0 && GTUtility.areStacksEqual(s, cellProbe)) {
                cellStacks.add(s);
                available += s.stackSize;
            }
        }
        if (available <= 0) {
            lEUt = 0;
            mEUt = 0;
            mEfficiency = 0;
            return CheckRecipeResultRegistry.NO_FUEL_FOUND;
        }

        // Only burn as many cells as the dynamos can output (at least one), so we never over-produce.
        int toBurn = (int) Math.max(1L, Math.min((long) available, dynamoCap / EU_PER_CELL));

        // depleteInput() only pulls from a single slot at a time, so draw from each stack (largest first)
        // until we've taken toBurn cells.
        cellStacks.sort((a, b) -> Integer.compare(b.stackSize, a.stackSize));
        int burned = 0;
        for (ItemStack s : cellStacks) {
            if (burned >= toBurn) break;
            int take = Math.min(s.stackSize, toBurn - burned);
            ItemStack req = s.copy();
            req.stackSize = take;
            if (depleteInput(req)) burned += take;
        }
        if (burned <= 0) {
            lEUt = 0;
            mEUt = 0;
            mEfficiency = 0;
            return CheckRecipeResultRegistry.NO_FUEL_FOUND;
        }

        // lEUt (positive = generation) clamped to dynamo capacity so the base never explodes the machine.
        long output = Math.min((long) EU_PER_CELL * burned, dynamoCap);
        lEUt = output;
        mEUt = (int) Math.min(output, Integer.MAX_VALUE);
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = BURN_TIME;
        return CheckRecipeResultRegistry.GENERATING;
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    @Override
    public int getPollutionPerTick(ItemStack aStack) {
        return 0;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    @Override
    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return false;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNHPPRecipeMaps.sRTGRecipes;
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
        tt.addMachineType("Radioisotope Thermoelectric Generator, RTG")
            .addInfo(EnumChatFormatting.GRAY + "Generates power by burning " + EnumChatFormatting.YELLOW
                + "fuel cells" + EnumChatFormatting.GRAY + ", scaling with how fast you feed them.")
            .addInfo(EnumChatFormatting.GRAY + "Output is capped by your " + EnumChatFormatting.YELLOW
                + "Dynamo hatch capacity" + EnumChatFormatting.GRAY + ".")
            .beginStructureBlock(5, 5, 5, false)
            .addController("Front face, center")
            .addCasingInfoMin("Radioisotope Thermoelectric Casing", 90, false)
            .addInputBus("Any casing — load betavoltaic cells", 1)
            .addDynamoHatch("Any casing", 1)
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
            + " s",
            StatCollector.translateToLocal("GT5U.multiblock.energy") + ": " + EnumChatFormatting.GREEN + mEUt
                + EnumChatFormatting.RESET + " EU/t" };
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
    public boolean supportsVoidProtection() {
        return false;
    }
}
