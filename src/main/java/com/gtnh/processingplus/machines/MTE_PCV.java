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
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnh.processingplus.GTNHProcessingPlus;
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
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;

/**
 * Polycondensation Vessel — a tall hollow reaction column (5×5×7) rather than a cube. The shell is
 * Chemically Inert Reaction Vessel casing; a stainless-steel frame "spine" runs up the hollow core
 * as the stirring/condenser internals. Runs the step-growth polymer condensations (Nylon-6,6, PLA)
 * on {@code sPCVRecipes}.
 */
public class MTE_PCV extends MTEExtendedPowerMultiBlockBase<MTE_PCV> implements ISurvivalConstructable {

    private static final int CASING_INDEX = 11;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    // Controller marker (~) sits at slice 0, row 8, col 4 — the front face of the main column.
    private static final int OFFSET_X = 4;
    private static final int OFFSET_Y = 8;
    private static final int OFFSET_Z = 0;

    private static IStructureDefinition<MTE_PCV> STRUCTURE_DEFINITION = null;

    public MTE_PCV(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected MTE_PCV(MTE_PCV prototype) {
        super(prototype.mName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTE_PCV(this);
    }

    @Override
    public IStructureDefinition<MTE_PCV> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_PCV>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    // shape[z][y][x] — 9 depth slices (front→back), each 12 rows (top→bottom) × 16 cols.
                    // Controller (~) at slice 0, row 8, col 4. 'P' = PCV casing (hatches), legend below.
                    new String[][] {
                        { "                ", "                ", "                ", "                ",
                            "                ", "                ", "  DJ JD         ", "  DEEED         ",
                            "  DE~ED         ", "  DEEED         ", "  DJ JD         ", "  D   D         " },
                        { "                ", "   NNN          ", "   GNG          ", "   G G          ",
                            "   G G          ", "  DJ JD         ", " Q RQR Q        ", " Q RRR Q        ",
                            " Q RRR Q        ", " Q RRR Q        ", " Q RQR Q        ", "  QJ JQ         " },
                        { "   NNN          ", "  DG GD         ", "  DG GD         ", "  DKQKD         ",
                            "  DIBID         ", " DDRQRDD        ", "D P   P D   QQQ ", "D P   P D   IAI ",
                            "D P   P D   IAI ", "D P   P D  DIAID", "D P   P D  DQQQD", "DQQPQPQQD  DQQQD" },
                        { "  NSOSN         ", " NGC CGN        ", " GGC CGG        ", " GKS SKG        ",
                            " GIBMBIG        ", " JRRLRRJ    QQQ ", "JR     RJ  QQJQQ", "JR     RJ  IB BI",
                            "JR     RJNNIH HI", "JR     RJ  IH HI", "JR     RJ  QB BQ", " JPPPPPJ   QQJQQ" },
                        { "  NOOON         ", " N  F  N        ", " N  F  N        ", "  Q F Q         ",
                            "  BMFMB         ", "  QLLLQ     QQQ ", " Q     Q   QJJJQ", " R     RBNNQ   A",
                            " R     RBOOO   A", " R     R NNQ   A", " Q     Q   Q   Q", "  QPPPQ    QJJJQ" },
                        { "  NSOSN         ", " NGC CGN        ", " GGC CGG        ", " GKS SKG        ",
                            " GIBMBIG        ", " JRRLRRJ    QQQ ", "JR     RJ  QQJQQ", "JR     RJ  IB BI",
                            "JR     RJNNIH HI", "JR     RJ  IH HI", "JR     RJ  QB BQ", " JPPPPPJ   QQJQQ" },
                        { "   NNN          ", "  DG GD         ", "  DG GD         ", "  DKQKD         ",
                            "  DIBID         ", " DDRQRDD        ", "D P   P D   QQQ ", "D P   P D   IAI ",
                            "D P   P D   IAI ", "D P   P D  DIAID", "D P   P D  DQQQD", "DQQPQPQQD  DQQQD" },
                        { "                ", "   NNN          ", "   GNG          ", "   G G          ",
                            "   G G          ", "  DJ JD         ", " Q RQR Q        ", " Q RRR Q        ",
                            " Q RRR Q        ", " Q RRR Q        ", " Q RQR Q        ", "  QJ JQ         " },
                        { "                ", "                ", "                ", "                ",
                            "                ", "                ", "  DJ JD         ", "  DJ JD         ",
                            "  DJ JD         ", "  DJ JD         ", "  DJ JD         ", "  D   D         " }, })
                // --- PCV casing (solid structural; hatches go on the Carbon Fiber Composite casing instead) ---
                .addElement('P', ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.PCV_CASING))
                // --- GregTech blocks (registry name + meta straight from the export) ---
                .addElement('E', fb("gregtech", "gt.blockcasings", 11))
                .addElement('G', fb("gregtech", "gt.blockcasings11", 5))
                .addElement('H', fb("gregtech", "gt.blockframes", 71))
                .addElement('I', fb("gregtech", "gt.blockmetal2", 14))
                .addElement('J', fb("gregtech", "gt.blockmetal8", 3))
                .addElement('K', fb("gregtech", "gt.blockmetal8", 10))
                .addElement('L', fb("gregtech", "gt.sheetmetal", 71))
                .addElement('M', fb("gregtech", "gt.sheetmetal", 73))
                // gregtech also registers the bartworks-material sheetmetal and the solenoid/cyclotron coils
                .addElement('D', fb("gregtech", "bw.sheetmetal", 26166))
                .addElement('F', fb("gregtech", "gt.blockcasings.cyclotron_coils", 2))
                // 'C' = bartworks-material frame box; modid uncertain in this GT version, null-safe handles it
                .addElement('C', fb("gregtech", "bw.frames", 26021))
                // --- GT++ (miscutils) blocks ---
                .addElement('N', fb("miscutils", "gtplusplus.blockcasings.5", 0))
                .addElement('O', fb("miscutils", "gtplusplus.blockcasings.5", 1))
                .addElement('B', fb("miscutils", "block.Inconel792.frame", 0))
                // --- Bartworks blocks ---
                .addElement('A', fb("bartworks", "BW_GlasBlocks", 0))
                // --- Carbon Fiber Composite casing = the ONLY hatch-bearing element (energy, I/O, maint, muffler) ---
                .addElement(
                    'Q',
                    buildHatchAdder(MTE_PCV.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance, Muffler)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.CF_COMPOSITE_CASING))
                // --- TODO: R and S were undefined bartworks tile placeholders in the export.
                //     Temporarily mapped to PCV casing so the file forms — replace with the real blocks. ---
                .addElement('R', ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.PCV_CASING))
                .addElement('S', ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.PCV_CASING))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    /**
     * Null-safe block element: resolves {modid:name} at definition-build time. If the block can't be
     * found (wrong modid/name for this GT version) it logs a warning and falls back to PCV casing
     * instead of throwing IllegalArgumentException (which would hard-crash the client on auto-place).
     */
    private static IStructureElement<MTE_PCV> fb(String modid, String name, int meta) {
        net.minecraft.block.Block b = GameRegistry.findBlock(modid, name);
        if (b == null) {
            GTNHProcessingPlus.LOG.warn("PCV structure: block {}:{} not found — using PCV casing placeholder.", modid,
                name);
            return ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.PCV_CASING);
        }
        return ofBlock(b, meta);
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
        return GTNHPPRecipeMaps.sPCVRecipes;
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
        tt.addMachineType("Polycondensation Vessel, PCV")
            .addInfo(EnumChatFormatting.GRAY + "Runs " + EnumChatFormatting.AQUA + "vacuum-assisted polymerization"
                + EnumChatFormatting.GRAY + " reactions.")
            .beginStructureBlock(16, 12, 9, true)
            .addController("Front face, main column")
            .addCasingInfoMin("Chemically Inert Reaction Vessel (PCV casing)", 20, false)
            .addOtherStructurePart("Carbon Fiber Composite, GT casings & frames", "Per structure hologram")
            .addInputBus("Any Carbon Fiber Composite casing", 1)
            .addInputHatch("Any Carbon Fiber Composite casing", 1)
            .addOutputBus("Any Carbon Fiber Composite casing", 1)
            .addOutputHatch("Any Carbon Fiber Composite casing", 1)
            .addEnergyHatch("Any Carbon Fiber Composite casing", 1)
            .addMufflerHatch("Any Carbon Fiber Composite casing", 1)
            .addMaintenanceHatch("Any Carbon Fiber Composite casing", 1)
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
