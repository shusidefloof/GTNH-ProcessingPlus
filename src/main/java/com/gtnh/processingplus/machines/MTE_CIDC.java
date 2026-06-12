package com.gtnh.processingplus.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
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
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;

import cpw.mods.fml.common.registry.GameRegistry;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnh.processingplus.materials.PrPMaterials;
import bartworks.system.material.WerkstoffLoader;
import gtnhlanth.common.register.LanthItemList;
import gregtech.api.GregTechAPI;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
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
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;
import static gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase.*;

/**
 * Controlled Isotopic Doping Chamber (CIDC) — UV-tier multiblock that assembles the RE-Doped
 * Photoresist Matrix (four rare-earth dopants + tin-oxo cluster, doped under a light-isolated beam
 * column). That matrix is required for UV Photoresist → UV/Elite circuit boards, so the CIDC is the
 * gate that makes the entire UV+ tier reachable.
 */
public class MTE_CIDC extends MTEExtendedPowerMultiBlockBase<MTE_CIDC> implements ISurvivalConstructable {

    private static final int CASING_INDEX = 11;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    // Controller marker '~' sits at slice z=2, row y=14, char x=7 in the exported shape.
    private static final int OFFSET_X = 7;
    private static final int OFFSET_Y = 14;
    private static final int OFFSET_Z = 2;

    private static IStructureDefinition<MTE_CIDC> STRUCTURE_DEFINITION = null;

    public MTE_CIDC(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected MTE_CIDC(MTE_CIDC prototype) {
        super(prototype.mName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTE_CIDC(this);
    }

    @Override
    public IStructureDefinition<MTE_CIDC> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_CIDC>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    /*




                    Structure:

                    Blocks:
                    A -> ofBlock...(BW_GlasBlocks, 0, ...);
                    B -> ofBlock...(bw.frames, 26021, ...);
                    C -> ofBlock...(casing.electrode, 0, ...);
                    D -> ofBlock...(gt.blockcasings, 15, ...);
                    E -> ofBlock...(gt.blockcasings11, 3, ...);
                    F -> ofBlock...(gt.blockcasings13, 1, ...);
                    G -> ofBlock...(gt.blockcasings4, 1, ...);
                    H -> ofBlock...(gt.blockcasings9, 0, ...);
                    I -> ofBlock...(gt.blockcasings9, 1, ...);
                    J -> ofBlock...(gt.blockframes, 129, ...);
                    K -> ofBlock...(gtplusplus.blockcasings.2, 11, ...);
                    L -> ofBlock...(tile.gtnhpp.casings, 0, ...);
                    M -> ofBlock...(tile.gtnhpp.casings, 9, ...);
                    N -> ofBlock...(tile.gtnhpp.casings, 25, ...);

                    Tiles:

                    Special Tiles:
                    O -> ofSpecialTileAdder(bartworks.system.material.BWTileEntityMetaGeneratedBlocksCasingAdvanced, ...); // You will probably want to change it to something else
                    P -> ofSpecialTileAdder(gregtech.api.metatileentity.BaseMetaTileEntity, ...); // You will probably want to change it to something else

                    Offsets:
                    -1 -1 -1
                     */




                    // shape[z][y][x] — 15 slices (z) × 16 rows (y) × 15 chars (x).
                    // See the block legend below for what each letter maps to; ' ' = empty.
                    new String[][]{{
                        "               ",
                        "LLL         LLL",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "LLLLN     NLLLL"
                    },{
                        "               ",
                        "LNL         LNL",
                        " J AN     NA J ",
                        " J ANN   NNA J ",
                        " J ANN   NNA J ",
                        " J AAN   NAA J ",
                        " J AAN   NAA J ",
                        " J AAN   NAA J ",
                        " J AAN   NAA J ",
                        " J AAN   NAA J ",
                        " J AAN   NAA J ",
                        " J AAN   NAA J ",
                        " J AAN   NAA J ",
                        " J ANN   NNA J ",
                        " J ANN   NNA J ",
                        "LJLNNNL LNNNLJL"
                    },{
                        "               ",
                        "LLLLL     LLLLL",
                        "K L  LL LL  L K",
                        "K L   NLN   L K",
                        "K L   AAA   L K",
                        "K L   AAA   L K",
                        "K L   AAA   L K",
                        "K L   AAA   L K",
                        "K L   AAA   L K",
                        "K L   AAA   L K",
                        "K L  NAAAN  L K",
                        "K L  NAAAN  L K",
                        "K L  NAAAN  L K",
                        "K L  NNNNN  L K",
                        "K L  NN~NN  L K",
                        "LLLLNNNNNNNLLLL"
                    },{
                        "               ",
                        "  LN       NL  ",
                        " A NNNLLLNNN A ",
                        " A  MM   MM  A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        "LNLLLGGNGGLLLNL"
                    },{
                        "               ",
                        "  L   PPP   L  ",
                        " N NNN   NNN N ",
                        " N MMMGGGMMM N ",
                        " N   M G M   N ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " N           N ",
                        " N           N ",
                        "NNNLLLGGGLLLNNN"
                    },{
                        "       P       ",
                        "     PPPPP     ",
                        "  LNNN   NNNL  ",
                        " N MMMHHHMMM N ",
                        " N  MMNNNMM  N ",
                        " N   MNNNM   N ",
                        " N    N N    N ",
                        " N           N ",
                        " N           N ",
                        " N           N ",
                        " NN         NN ",
                        " NN         NN ",
                        " NN  B   B  NN ",
                        " NN  OKKKO  NN ",
                        " NN  OJ JO  NN ",
                        " NNGLLLGLLLGNN "
                    },{
                        "      PPP      ",
                        "    PPPPPPP    ",
                        "  LL       LL  ",
                        "  N GHIHIHG N  ",
                        "  A  NI IN  A  ",
                        "  A  NDDDN  A  ",
                        "  A  NININ  A  ",
                        "  A   FEF   A  ",
                        "  A   FEF   A  ",
                        "  A    E    A  ",
                        "  A         A  ",
                        "  A         A  ",
                        "  A         A  ",
                        "  A  KKKKK  A  ",
                        "  N  J   J  N  ",
                        " LNGGLLLLLGGNL "
                    },{
                        "     PPPPP     ",
                        "    PPPPPPP    ",
                        "   L       L   ",
                        "  L GHHHHHG L  ",
                        "  A GN C NG A  ",
                        "  A  NDCDN  A  ",
                        "  A   NCN   A  ",
                        "  A   ECE   A  ",
                        "  A   EME   A  ",
                        "  A   EME   A  ",
                        "  A    M    A  ",
                        "  A         A  ",
                        "  A         A  ",
                        "  A  KKKKK  A  ",
                        "  L    M    L  ",
                        "  NNGGLLLGGNN  "
                    },{
                        "      PPP      ",
                        "    PPPPPPP    ",
                        "  LL       LL  ",
                        "  N GHIHIHG N  ",
                        "  A  NI IN  A  ",
                        "  A  NDDDN  A  ",
                        "  A  NININ  A  ",
                        "  A   FEF   A  ",
                        "  A   FEF   A  ",
                        "  A    E    A  ",
                        "  A         A  ",
                        "  A         A  ",
                        "  A         A  ",
                        "  A  KKKKK  A  ",
                        "  N  J   J  N  ",
                        " LNGGLLLLLGGNL "
                    },{
                        "       P       ",
                        "     PPPPP     ",
                        "  LNNN   NNNL  ",
                        " N MMMHHHMMM N ",
                        " N  MMNNNMM  N ",
                        " N   MNNNM   N ",
                        " N    N N    N ",
                        " N           N ",
                        " N           N ",
                        " N           N ",
                        " NN         NN ",
                        " NN         NN ",
                        " NN  B   B  NN ",
                        " NN  OKKKO  NN ",
                        " NN  OJ JO  NN ",
                        " NNGLLLGLLLGNN "
                    },{
                        "               ",
                        "  L   PPP   L  ",
                        " N NNN   NNN N ",
                        " N MMMGGGMMM N ",
                        " N   M G M   N ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " N           N ",
                        " N           N ",
                        "NNNLLLGGGLLLNNN"
                    },{
                        "               ",
                        "  LN       NL  ",
                        " A NNNLLLNNN A ",
                        " A  MM   MM  A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        " A           A ",
                        "LNLLLGGNGGLLLNL"
                    },{
                        "               ",
                        "LLLLL     LLLLL",
                        "K L NLL LLN L K",
                        "K L   NLN   L K",
                        "K L   AAA   L K",
                        "K L   AAA   L K",
                        "K L   AAA   L K",
                        "K L   AAA   L K",
                        "K L   AAA   L K",
                        "K L  NAAAN  L K",
                        "K L  NAAAN  L K",
                        "K L  NAAAN  L K",
                        "K L  NAAAN  L K",
                        "K L  NAAAN  L K",
                        "K L  NNLNN  L K",
                        "LLLLNNNNNNNLLLL"
                    },{
                        "               ",
                        "LNL         LNL",
                        " J AN     NA J ",
                        " J ANN   NNA J ",
                        " J ANN   NNA J ",
                        " J AAN   NAA J ",
                        " J AAN   NAA J ",
                        " J AAN   NAA J ",
                        " J AAN   NAA J ",
                        " J AAN   NAA J ",
                        " J AAN   NAA J ",
                        " J AAN   NAA J ",
                        " J AAN   NAA J ",
                        " J ANN   NNA J ",
                        " J ANN   NNA J ",
                        "LJLNNNL LNNNLJL"
                    },{
                        "               ",
                        "LLL         LLL",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "L K         K L",
                        "LLLL       LLLL"
                    }})

                /*
                    Block legend (from the in-game structure export):
                    A -> any tiered glass (BW glass etc.)        — chainAllGlasses()
                    B -> Sintered Silicon Carbide bolted casing  — BWBlockCasingsAdvanced (meta = Werkstoff id)
                    C -> gtnhlanth electrode casing              — LanthItemList.ELECTRODE_CASING
                    D -> gt.blockcasings:15                      — sBlockCasings1
                    E -> gt.blockcasings11:3                     — sBlockCasings11
                    F -> gt.blockcasings13:1                     — sBlockCasings13
                    G -> gt.blockcasings4:1                      — sBlockCasings4
                    H -> gt.blockcasings9:0                      — sBlockCasings9
                    I -> gt.blockcasings9:1                      — sBlockCasings9
                    J -> gt.blockframes:129                      — sBlockFrames
                    K -> gtplusplus.blockcasings.2:11 (miscutils)
                    L -> tile.gtnhpp.casings:0  (Silicon Carbide Ceramic Casing)
                    M -> tile.gtnhpp.casings:9  (Spectral Isolation Casing — plain, lower dome cells)
                    N -> tile.gtnhpp.casings:25 (Isotopic Doping Casing — chamber shell, no hatches)
                    O -> Sintered Silicon Carbide plain casing   — BWBlockCasings (meta = Werkstoff id)
                    P -> top-dome M cells (Spectral Isolation Casing) — ONLY hatch-capable cells
                 */
                .addElement('A', chainAllGlasses())
                .addElement('B', ofBlock(WerkstoffLoader.BWBlockCasingsAdvanced, PrPMaterials.SinteredSiliconCarbide.getId()))
                .addElement('C', ofBlock(LanthItemList.ELECTRODE_CASING, 0))
                .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 15))
                .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings11, 3))
                .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings13, 1))
                .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 1))
                .addElement('H', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 0))
                .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 1))
                .addElement('J', StructureUtility.ofBlock(GregTechAPI.sBlockFrames, 129))
                .addElement('K', ofBlock(GameRegistry.findBlock("miscutils", "gtplusplus.blockcasings.2"), 11))
                .addElement('L', ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.HTRF_CASING))
                // M = Spectral Isolation Casing — plain casing (the lower/deeper dome cells).
                .addElement('M', ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.SPC_CASING))
                .addElement('O', ofBlock(WerkstoffLoader.BWBlockCasings, PrPMaterials.SinteredSiliconCarbide.getId()))
                // N = Isotopic Doping Casing — the chamber shell (no hatches).
                .addElement('N', ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.ISOTOPIC_DOPING_CASING))
                // P = the top dome M cells (Spectral Isolation Casing) — the ONLY hatch-capable spots.
                .addElement(
                    'P',
                    buildHatchAdder(MTE_CIDC.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.SPC_CASING))
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
            true,
            true);
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z, errors)) return;

        if (mMaintenanceHatches.size() != 1) {
            errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
        }
        if (mEnergyHatches.isEmpty()) {
            errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
        }
        if (mInputHatches.isEmpty()) {
            errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
        }
        if (mOutputHatches.isEmpty()) {
            errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
        }
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNHPPRecipeMaps.sCIDCRecipes;
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
        tt.addMachineType("Controlled Isotopic Doping Chamber, CIDC")
            .addInfo(EnumChatFormatting.GRAY + "Performs " + EnumChatFormatting.LIGHT_PURPLE + "precision doping"
                + EnumChatFormatting.GRAY + " under controlled isolation.")
            .addSeparator()
            .beginStructureBlock(15, 16, 15, true)
            .addController("Front face, center")
            .addCasingInfoMin("Isotopic Doping Casing", 90, false)
            .addInputBus("Any shell casing", 1)
            .addInputHatch("Any shell casing", 1)
            .addOutputBus("Any shell casing", 1)
            .addEnergyHatch("Any shell casing", 1)
            .addMaintenanceHatch("Any shell casing", 1)
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
