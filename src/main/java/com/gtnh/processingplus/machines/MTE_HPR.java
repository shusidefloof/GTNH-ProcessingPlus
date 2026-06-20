package com.gtnh.processingplus.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlocksTiered;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

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
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;

public class MTE_HPR extends MTEExtendedPowerMultiBlockBase<MTE_HPR> implements ISurvivalConstructable {

    private static final int CASING_INDEX = 11;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    // Controller (~) is at slice z=1, row y=14, col x=14
    private static final int OFFSET_X = 14;
    private static final int OFFSET_Y = 14;
    private static final int OFFSET_Z = 1;

    private static final String[] COIL_NAMES = { "FRF_Coil_1", "FRF_Coil_2", "FRF_Coil_3", "FRF_Coil_4" };
    private static final String[] TIER_DISPLAY = { "Tier I", "Tier II", "Tier III", "Tier IV" };

    /** 0-indexed coil tier (-1 = not formed / no coils placed). */
    private int mCoilTier = -1;

    private static IStructureDefinition<MTE_HPR> STRUCTURE_DEFINITION = null;

    private static Block ggBlock(String name) {
        Block b = GameRegistry.findBlock("GoodGenerator", name);
        if (b == null) throw new RuntimeException("HPR requires GoodGenerator block: " + name);
        return b;
    }

    public MTE_HPR(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected MTE_HPR(MTE_HPR prototype) {
        super(prototype.mName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTE_HPR(this);
    }

    @Override
    public IStructureDefinition<MTE_HPR> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            // Build coil block list once (safe here — called after mod init)
            Block[] coilBlocks = new Block[COIL_NAMES.length];
            for (int i = 0; i < COIL_NAMES.length; i++) {
                coilBlocks[i] = ggBlock(COIL_NAMES[i]);
            }
            List<Pair<Block, Integer>> coilTiers = new ArrayList<>();
            for (Block b : coilBlocks) {
                coilTiers.add(Pair.of(b, 0));
            }

            STRUCTURE_DEFINITION = StructureDefinition.<MTE_HPR>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    new String[][] { { // z=0 — outer frame ring (pressure walls only)
                        "                             ", "              D              ",
                        "             D D             ", "            D   D            ",
                        "             D D             ", "              D              ",
                        "                             ", "                             ",
                        "                             ", "                             ",
                        "                             ", "                             ",
                        "   D          D          D   ", "  D D        DCD        D D  ",
                        " D   D      DC CD      D   D ", "  D D        DCD        D D  ",
                        "   D          D          D   ", "                             ",
                        "                             ", "                             ",
                        "                             ", "                             ",
                        "                             ", "              D              ",
                        "             D D             ", "            D   D            ",
                        "             D D             ", "              D              ",
                        "                             " },
                        { // z=1 — front vessel faces; controller (~) at row 14 col 14
                            "              D              ", "             BFB             ",
                            "            BFFFB            ", "           DFFFFFD           ",
                            "            BFFFB            ", "             BFB             ",
                            "              D              ", "                             ",
                            "                             ", "                             ",
                            "                             ", "   D          D          D   ",
                            "  BFB        BFB        BFB  ", " BFFFB      BFFFB      BFFFB ",
                            "DFFFFFD    DFF~FFD    DFFFFFD", " BFFFB      BFFFB      BFFFB ",
                            "  BFB        BFB        BFB  ", "   D          D          D   ",
                            "                             ", "                             ",
                            "                             ", "                             ",
                            "              D              ", "             BFB             ",
                            "            BFFFB            ", "           DFFFFFD           ",
                            "            BFFFB            ", "             BFB             ",
                            "              D              " },
                        { // z=2 — glass conduit connections
                            "             D D             ", "            BFFFB            ",
                            "           DF   FD           ", "        DCCCF   FCCCD        ",
                            "           DF   FD           ", "            BFFFB            ",
                            "             D D             ", "              C              ",
                            "   D          C          D   ", "   C          C          C   ",
                            "   C          C          C   ", "  DCD        DCD        DCD  ",
                            " BFFFB      BFFFB      BFFFB ", "DF   FD    DF   FD    DF   FD",
                            " F   F CCCCCF   FCCCCC F   F ", "DF   FD    DF   FD    DF   FD",
                            " BFFFB      BFFFB      BFFFB ", "  DCD        DCD        DCD  ",
                            "   C          C          C   ", "   C          C          C   ",
                            "   D          C          D   ", "              C              ",
                            "             D D             ", "            BFFFB            ",
                            "           DF   FD           ", "        DCCCF   FCCCD        ",
                            "           DF   FD           ", "            BFFFB            ",
                            "             D D             " },
                        { // z=3 — central reaction core with FRF coils and radiation frames
                            "            D   D            ", "           DFFFFFD           ",
                            "        DCCCF   FCCCD        ", "      BBBAAAF   FAAABBB      ",
                            "     BB DCCCF   FCCCD BB     ", "    BB     DFFFFFD     BB    ",
                            "   BB       D   D       BB   ", "   B         CEC         B   ",
                            "  DBD        CEC        DBD  ", "  CAC        CEC        CAC  ",
                            "  CAC        CEC        CAC  ", " DCACD      DCECD      DCACD ",
                            "DFFFFFD    DFFFFFD    DFFFFFD", " F   F CCCCCF   FCCCCC F   F ",
                            " F   F EEEEEF   FEEEEE F   F ", " F   F CCCCCF   FCCCCC F   F ",
                            "DFFFFFD    DFFFFFD    DFFFFFD", " DCACD      DCECD      DCACD ",
                            "  CAC        CEC        CAC  ", "  CAC        CEC        CAC  ",
                            "  DBD        CEC        DBD  ", "   B         CEC         B   ",
                            "   BB       D   D       BB   ", "    BB     DFFFFFD     BB    ",
                            "     BB DCCCF   FCCCD BB     ", "      BBBAAAF   FAAABBB      ",
                            "        DCCCF   FCCCD        ", "           DFFFFFD           ",
                            "            D   D            " },
                        { // z=4 — mirror of z=2
                            "             D D             ", "            BFFFB            ",
                            "           DF   FD           ", "        DCCCF   FCCCD        ",
                            "           DF   FD           ", "            BFFFB            ",
                            "             D D             ", "              C              ",
                            "   D          C          D   ", "   C          C          C   ",
                            "   C          C          C   ", "  DCD        DCD        DCD  ",
                            " BFFFB      BFFFB      BFFFB ", "DF   FD    DF   FD    DF   FD",
                            " F   F CCCCCF   FCCCCC F   F ", "DF   FD    DF   FD    DF   FD",
                            " BFFFB      BFFFB      BFFFB ", "  DCD        DCD        DCD  ",
                            "   C          C          C   ", "   C          C          C   ",
                            "   D          C          D   ", "              C              ",
                            "             D D             ", "            BFFFB            ",
                            "           DF   FD           ", "        DCCCF   FCCCD        ",
                            "           DF   FD           ", "            BFFFB            ",
                            "             D D             " },
                        { // z=5 — mirror of z=1 (back vessel faces, no controller)
                            "              D              ", "             BFB             ",
                            "            BFFFB            ", "           DFFFFFD           ",
                            "            BFFFB            ", "             BFB             ",
                            "              D              ", "                             ",
                            "                             ", "                             ",
                            "                             ", "   D          D          D   ",
                            "  BFB        BFB        BFB  ", " BFFFB      BFFFB      BFFFB ",
                            "DFFFFFD    DFFFFFD    DFFFFFD", " BFFFB      BFFFB      BFFFB ",
                            "  BFB        BFB        BFB  ", "   D          D          D   ",
                            "                             ", "                             ",
                            "                             ", "                             ",
                            "              D              ", "             BFB             ",
                            "            BFFFB            ", "           DFFFFFD           ",
                            "            BFFFB            ", "             BFB             ",
                            "              D              " },
                        { // z=6 — back outer frame ring
                            "                             ", "              D              ",
                            "             D D             ", "            D   D            ",
                            "             D D             ", "              D              ",
                            "                             ", "                             ",
                            "                             ", "                             ",
                            "                             ", "                             ",
                            "   D          D          D   ", "  D D        DCD        D D  ",
                            " D   D      DCCCD      D   D ", "  D D        DCD        D D  ",
                            "   D          D          D   ", "                             ",
                            "                             ", "                             ",
                            "                             ", "                             ",
                            "                             ", "              D              ",
                            "             D D             ", "            D   D            ",
                            "             D D             ", "              D              ",
                            "                             " } })
                .addElement('A', ofBlocksTiered((block, meta) -> {
                    for (int i = 0; i < coilBlocks.length; i++) {
                        if (block == coilBlocks[i] && meta == 0) return i;
                    }
                    return null;
                }, coilTiers, -1, (h, t) -> h.mCoilTier = t, h -> h.mCoilTier))
                .addElement('B', ofBlock(ggBlock("MAR_Casing"), 0))
                .addElement('C', ofBlock(ggBlock("fieldRestrictingGlass"), 0))
                .addElement(
                    'D',
                    buildHatchAdder(MTE_HPR.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(ggBlock("pressureResistantWalls"), 0))
                .addElement('E', ofBlock(ggBlock("radiationProtectionSteelFrame"), 0))
                .addElement('F', ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.HYBRID_PHASE_CASING))
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
        mCoilTier = -1;
        checkPiece(STRUCTURE_PIECE_MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z, errors);
        if (mCoilTier < 0) errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
        if (mMaintenanceHatches.size() != 1) errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                // mSpecialValue = minimum FRF coil tier (1-indexed). 0 or negative = any tier.
                int required = recipe.mSpecialValue;
                if (required > 0 && mCoilTier + 1 < required) {
                    return SimpleCheckRecipeResult.ofFailure("coil_tier_too_low");
                }
                return super.validateRecipe(recipe);
            }
        };
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNHPPRecipeMaps.sHPRRecipes;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mCoilTier", mCoilTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mCoilTier = aNBT.getInteger("mCoilTier");
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
        tt.addMachineType("Hybrid Phase Reactor, HPR")
            .addInfo(
                EnumChatFormatting.GRAY + "Runs simultaneous "
                    + EnumChatFormatting.RED
                    + "liquid and plasma phase"
                    + EnumChatFormatting.GRAY
                    + " reactions.")
            .addInfo(
                EnumChatFormatting.GRAY + "Three phase-separated reaction vessels connected by "
                    + EnumChatFormatting.AQUA
                    + "field-restricting conduits"
                    + EnumChatFormatting.GRAY
                    + ".")
            .addSeparator()
            .addInfo(EnumChatFormatting.GOLD + "FRF Coil tier (A blocks) determines available recipes:")
            .addInfo(
                EnumChatFormatting.GRAY + "  "
                    + EnumChatFormatting.WHITE
                    + "Tier I"
                    + EnumChatFormatting.GRAY
                    + " — FRF_Coil_1 | "
                    + EnumChatFormatting.WHITE
                    + "Tier II"
                    + EnumChatFormatting.GRAY
                    + " — FRF_Coil_2")
            .addInfo(
                EnumChatFormatting.GRAY + "  "
                    + EnumChatFormatting.WHITE
                    + "Tier III"
                    + EnumChatFormatting.GRAY
                    + " — FRF_Coil_3 | "
                    + EnumChatFormatting.WHITE
                    + "Tier IV"
                    + EnumChatFormatting.GRAY
                    + " — FRF_Coil_4")
            .beginStructureBlock(29, 29, 7, true)
            .addController("Second layer from front, center vessel center")
            .addOtherStructurePart("FRF Coil (any tier)", "Core reaction rings (A)", 1)
            .addCasingInfoMin("Pressure Resistant Wall", 1, false)
            .addInputBus("Any Pressure Resistant Wall (D)", 1)
            .addInputHatch("Any Pressure Resistant Wall (D)", 1)
            .addOutputBus("Any Pressure Resistant Wall (D)", 1)
            .addOutputHatch("Any Pressure Resistant Wall (D)", 1)
            .addEnergyHatch("Any Pressure Resistant Wall (D)", 1)
            .addMaintenanceHatch("Any Pressure Resistant Wall (D)", 1)
            .toolTipFinisher("_Shusi_");
        return tt;
    }

    @Override
    public String[] getInfoData() {
        String tierLine = mCoilTier >= 0 && mCoilTier < TIER_DISPLAY.length
            ? EnumChatFormatting.AQUA + "FRF Coil: " + EnumChatFormatting.WHITE + TIER_DISPLAY[mCoilTier]
            : EnumChatFormatting.RED + "FRF Coil: not formed";
        return new String[] { StatCollector.translateToLocal("GT5U.multiblock.Progress") + ": "
            + EnumChatFormatting.GREEN
            + mProgresstime / 20
            + EnumChatFormatting.RESET
            + " s / "
            + EnumChatFormatting.YELLOW
            + mMaxProgresstime / 20
            + EnumChatFormatting.RESET
            + " s", tierLine };
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
