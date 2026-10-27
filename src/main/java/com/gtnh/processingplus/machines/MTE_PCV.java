package com.gtnh.processingplus.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_MULTI_LATEX;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_MULTI_LATEX_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_MULTI_LATEX_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_MULTI_LATEX_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.List;

import net.minecraft.block.Block;
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
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;
import com.gtnh.processingplus.recipes.PPRecipeHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.material.MaterialsAlloy;

public class MTE_PCV extends MTEExtendedPowerMultiBlockBase<MTE_PCV> implements ISurvivalConstructable {

    private static final int CASING_INDEX = 11;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int OFFSET_X = 4;
    private static final int OFFSET_Y = 9;
    private static final int OFFSET_Z = 0;

    private static IStructureDefinition<MTE_PCV> STRUCTURE_DEFINITION = null;

    // Promethium Betavoltaic Alloy rebolted casing (OrePrefixes.blockCasingAdvanced), resolved from the
    // actual ItemStack the Werkstoff produces rather than hardcoding a damage value.
    private static final Block PROMETHIUM_BETAVOLTAIC_BLOCK;
    private static final int PROMETHIUM_BETAVOLTAIC_BLOCK_META;

    static {
        ItemStack block = PrPMaterials.PromethiumBetavoltaicAlloy.get(OrePrefixes.blockCasingAdvanced, 1);
        PROMETHIUM_BETAVOLTAIC_BLOCK = Block.getBlockFromItem(block.getItem());
        PROMETHIUM_BETAVOLTAIC_BLOCK_META = block.getItemDamage();
    }

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
                    new String[][] {
                        { "                ", "                ", "                ", "                ",
                            "                ", "                ", "                ", "  DJ JD         ",
                            "  DEEED         ", "  DE~ED         ", "  DEEED         ", "  DJ JD         ",
                            "  D   D         " },
                        { "                ", "                ", "   NNN          ", "   GNG          ",
                            "   G G          ", "   G G          ", "  DJ JD         ", " Q RQR Q        ",
                            " Q RRR Q        ", " Q RRR Q        ", " Q RRR Q        ", " Q RQR Q        ",
                            "  QJ JQ         " },
                        { "                ", "   NNN          ", "  DG GD         ", "  DG GD         ",
                            "  DKQKD         ", "  DIBID         ", " DDRQRDD        ", "D P   P D   QQQ ",
                            "D P   P D   IAI ", "D P   P D   IAI ", "D P   P D  DIAID", "D P   P D  DQQQD",
                            "DQQPQPQQD  DQQQD" },
                        { "   DND          ", "  NSOSN         ", " NGC CGN        ", " GGC CGG        ",
                            " GKS SKG        ", " GIBMBIG        ", " JRRLRRJ    QQQ ", "JR     RJ  QQJQQ",
                            "JR     RJ  IB BI", "JR     RJNNIH HI", "JR     RJ  IH HI", "JR     RJ  QB BQ",
                            " JPPPPPJ   QQJQQ" },
                        { "   NNN          ", "  NOOON         ", " N  F  N        ", " N  F  N        ",
                            "  Q F Q         ", "  BMFMB         ", "  QLLLQ     QQQ ", " Q     Q   QJJJQ",
                            " R     RBNNQ   A", " R     RBOOO   A", " R     R NNQ   A", " Q     Q   Q   Q",
                            "  QPPPQ    QJJJQ" },
                        { "   DND          ", "  NSOSN         ", " NGC CGN        ", " GGC CGG        ",
                            " GKS SKG        ", " GIBMBIG        ", " JRRLRRJ    QQQ ", "JR     RJ  QQJQQ",
                            "JR     RJ  IB BI", "JR     RJNNIH HI", "JR     RJ  IH HI", "JR     RJ  QB BQ",
                            " JPPPPPJ   QQJQQ" },
                        { "                ", "   NNN          ", "  DG GD         ", "  DG GD         ",
                            "  DKQKD         ", "  DIBID         ", " DDRQRDD        ", "D P   P D   QQQ ",
                            "D P   P D   IAI ", "D P   P D   IAI ", "D P   P D  DIAID", "D P   P D  DQQQD",
                            "DQQPQPQQD  DQQQD" },
                        { "                ", "                ", "   NNN          ", "   GNG          ",
                            "   G G          ", "   G G          ", "  DJ JD         ", " Q RQR Q        ",
                            " Q RRR Q        ", " Q RRR Q        ", " Q RRR Q        ", " Q RQR Q        ",
                            "  QJ JQ         " },
                        { "                ", "                ", "                ", "                ",
                            "                ", "                ", "                ", "  DJ JD         ",
                            "  DJ JD         ", "  DJ JD         ", "  DJ JD         ", "  DJ JD         ",
                            "  D   D         " }, })
                .addElement('P', ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.PCV_CASING))
                .addElement('E', fb("gregtech", "gt.blockcasings", 11))
                .addElement('G', fb("gregtech", "gt.blockcasings11", 5))
                .addElement('H', fb("gregtech", "gt.blockframes", 71))
                .addElement('I', fb("gregtech", "gt.blockmetal2", 14))
                .addElement('J', fb("gregtech", "gt.blockmetal8", 3))
                .addElement('K', fb("gregtech", "gt.blockmetal8", 10))
                .addElement('L', fb("gregtech", "gt.sheetmetal", 71))
                .addElement('M', fb("gregtech", "gt.sheetmetal", 73))
                .addElement('D', fb("gregtech", "bw.sheetmetal", 26166))
                .addElement('F', fb("gregtech", "gt.blockcasings.cyclotron_coils", 2))
                .addElement('C', fb("gregtech", "bw.frames", 26021))
                .addElement('N', fb("miscutils", "gtplusplus.blockcasings.5", 0))
                .addElement('O', fb("miscutils", "gtplusplus.blockcasings.5", 1))
                .addElement('B', ofFrame(MaterialsAlloy.INCONEL_792))
                .addElement('S', ofFrame(MaterialsAlloy.INCONEL_792))
                .addElement('A', fb("bartworks", "BW_GlasBlocks", 0))
                .addElement('R', ofBlock(PROMETHIUM_BETAVOLTAIC_BLOCK, PROMETHIUM_BETAVOLTAIC_BLOCK_META))
                .addElement(
                    'Q',
                    buildHatchAdder(MTE_PCV.class)
                        .atLeast(
                            Energy.or(ExoticEnergy),
                            InputBus,
                            InputHatch,
                            OutputBus,
                            OutputHatch,
                            Maintenance,
                            Muffler)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.CF_COMPOSITE_CASING))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    /**
     * Null-safe block element: resolves {modid:name} at definition-build time. Falls back to PCV
     * casing on failure to prevent hard crashes from wrong modids.
     */
    private static IStructureElement<MTE_PCV> fb(String modid, String name, int meta) {
        Block b = GameRegistry.findBlock(modid, name);
        if (b == null) {
            GTNHProcessingPlus.LOG
                .warn("PCV structure: block {}:{} not found, using PCV casing placeholder.", modid, name);
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
    public boolean supportsBatchMode() {
        return true;
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
                .addIcon(OVERLAY_FRONT_MULTI_LATEX_ACTIVE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_MULTI_LATEX_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { casingTexturePages[0][CASING_INDEX], TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_MULTI_LATEX)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_MULTI_LATEX_GLOW)
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
            .addInfo("Runs vacuum-assisted condensation and ring-opening polymerization")
            .addSupportAny()
            .beginStructureBlock(16, 13, 9, true)
            .addController("Front, 5th from the left, 4th layer")
            .addCasing("20+", "Chemically Inert Reaction Vessel", false)
            .addOtherStructurePart(
                "Borosilicate Glass, Promethium Betavoltaic Alloy Rebolted Casing, Inconel-792 Frame Box, GT Casings and Frames",
                "See the structure hologram")
            .addEnergyHatch("1+", "Any chemically inert reaction vessel", 1)
            .addMaintenanceHatch("1", "Any chemically inert reaction vessel", 1)
            .addMufflerHatch("1", "Any chemically inert reaction vessel", 1)
            .addInputAny("1+", "Any chemically inert reaction vessel", 1)
            .addOutputAny("1+", "Any chemically inert reaction vessel", 1)
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
