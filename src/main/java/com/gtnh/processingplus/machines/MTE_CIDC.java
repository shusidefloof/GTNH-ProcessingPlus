package com.gtnh.processingplus.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
import static gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import bartworks.system.material.WerkstoffLoader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
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
import gtnhlanth.common.register.LanthItemList;

/**
 * Controlled Isotopic Doping Chamber (CIDC): UV-tier multiblock that assembles the RE-Doped
 * Photoresist Matrix. Required for UV Photoresist -> UV/Elite circuit boards
 */
public class MTE_CIDC extends MTEExtendedPowerMultiBlockBase<MTE_CIDC> implements ISurvivalConstructable {

    private static final int CASING_INDEX = 50;
    private static final String STRUCTURE_PIECE_MAIN = "main";
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
                    new String[][] {
                        { "               ", "LLL         LLL", "L K         K L", "L K         K L", "L K         K L",
                            "L K         K L", "L K         K L", "L K         K L", "L K         K L",
                            "L K         K L", "L K         K L", "L K         K L", "L K         K L",
                            "L K         K L", "L K         K L", "LLLLN     NLLLL" },
                        { "               ", "LNL         LNL", " J AN     NA J ", " J ANN   NNA J ", " J ANN   NNA J ",
                            " J AAN   NAA J ", " J AAN   NAA J ", " J AAN   NAA J ", " J AAN   NAA J ",
                            " J AAN   NAA J ", " J AAN   NAA J ", " J AAN   NAA J ", " J AAN   NAA J ",
                            " J ANN   NNA J ", " J ANN   NNA J ", "LJLNNNL LNNNLJL" },
                        { "               ", "LLLLL     LLLLL", "K L  LL LL  L K", "K L   NLN   L K", "K L   AAA   L K",
                            "K L   AAA   L K", "K L   AAA   L K", "K L   AAA   L K", "K L   AAA   L K",
                            "K L   AAA   L K", "K L  NAAAN  L K", "K L  NAAAN  L K", "K L  NAAAN  L K",
                            "K L  NNNNN  L K", "K L  NN~NN  L K", "LLLLNNNNNNNLLLL" },
                        { "               ", "  LN       NL  ", " A NNNLLLNNN A ", " A  MM   MM  A ", " A           A ",
                            " A           A ", " A           A ", " A           A ", " A           A ",
                            " A           A ", " A           A ", " A           A ", " A           A ",
                            " A           A ", " A           A ", "LNLLLGGNGGLLLNL" },
                        { "               ", "  L   PPP   L  ", " N NNN   NNN N ", " N MMMGGGMMM N ", " N   M G M   N ",
                            " A           A ", " A           A ", " A           A ", " A           A ",
                            " A           A ", " A           A ", " A           A ", " A           A ",
                            " N           N ", " N           N ", "NNNLLLGGGLLLNNN" },
                        { "       P       ", "     PPPPP     ", "  LNNN   NNNL  ", " N MMMHHHMMM N ", " N  MMNNNMM  N ",
                            " N   MNNNM   N ", " N    N N    N ", " N           N ", " N           N ",
                            " N           N ", " NN         NN ", " NN         NN ", " NN  B   B  NN ",
                            " NN  OKKKO  NN ", " NN  OJ JO  NN ", " NNGLLLGLLLGNN " },
                        { "      PPP      ", "    PPPPPPP    ", "  LL       LL  ", "  N GHIHIHG N  ", "  A  NI IN  A  ",
                            "  A  NDDDN  A  ", "  A  NININ  A  ", "  A   FEF   A  ", "  A   FEF   A  ",
                            "  A    E    A  ", "  A         A  ", "  A         A  ", "  A         A  ",
                            "  A  KKKKK  A  ", "  N  J   J  N  ", " LNGGLLLLLGGNL " },
                        { "     PPPPP     ", "    PPPPPPP    ", "   L       L   ", "  L GHHHHHG L  ", "  A GN C NG A  ",
                            "  A  NDCDN  A  ", "  A   NCN   A  ", "  A   ECE   A  ", "  A   EME   A  ",
                            "  A   EME   A  ", "  A    M    A  ", "  A         A  ", "  A         A  ",
                            "  A  KKKKK  A  ", "  L    M    L  ", "  NNGGLLLGGNN  " },
                        { "      PPP      ", "    PPPPPPP    ", "  LL       LL  ", "  N GHIHIHG N  ", "  A  NI IN  A  ",
                            "  A  NDDDN  A  ", "  A  NININ  A  ", "  A   FEF   A  ", "  A   FEF   A  ",
                            "  A    E    A  ", "  A         A  ", "  A         A  ", "  A         A  ",
                            "  A  KKKKK  A  ", "  N  J   J  N  ", " LNGGLLLLLGGNL " },
                        { "       P       ", "     PPPPP     ", "  LNNN   NNNL  ", " N MMMHHHMMM N ", " N  MMNNNMM  N ",
                            " N   MNNNM   N ", " N    N N    N ", " N           N ", " N           N ",
                            " N           N ", " NN         NN ", " NN         NN ", " NN  B   B  NN ",
                            " NN  OKKKO  NN ", " NN  OJ JO  NN ", " NNGLLLGLLLGNN " },
                        { "               ", "  L   PPP   L  ", " N NNN   NNN N ", " N MMMGGGMMM N ", " N   M G M   N ",
                            " A           A ", " A           A ", " A           A ", " A           A ",
                            " A           A ", " A           A ", " A           A ", " A           A ",
                            " N           N ", " N           N ", "NNNLLLGGGLLLNNN" },
                        { "               ", "  LN       NL  ", " A NNNLLLNNN A ", " A  MM   MM  A ", " A           A ",
                            " A           A ", " A           A ", " A           A ", " A           A ",
                            " A           A ", " A           A ", " A           A ", " A           A ",
                            " A           A ", " A           A ", "LNLLLGGNGGLLLNL" },
                        { "               ", "LLLLL     LLLLL", "K L NLL LLN L K", "K L   NLN   L K", "K L   AAA   L K",
                            "K L   AAA   L K", "K L   AAA   L K", "K L   AAA   L K", "K L   AAA   L K",
                            "K L  NAAAN  L K", "K L  NAAAN  L K", "K L  NAAAN  L K", "K L  NAAAN  L K",
                            "K L  NAAAN  L K", "K L  NNLNN  L K", "LLLLNNNNNNNLLLL" },
                        { "               ", "LNL         LNL", " J AN     NA J ", " J ANN   NNA J ", " J ANN   NNA J ",
                            " J AAN   NAA J ", " J AAN   NAA J ", " J AAN   NAA J ", " J AAN   NAA J ",
                            " J AAN   NAA J ", " J AAN   NAA J ", " J AAN   NAA J ", " J AAN   NAA J ",
                            " J ANN   NNA J ", " J ANN   NNA J ", "LJLNNNL LNNNLJL" },
                        { "               ", "LLL         LLL", "L K         K L", "L K         K L", "L K         K L",
                            "L K         K L", "L K         K L", "L K         K L", "L K         K L",
                            "L K         K L", "L K         K L", "L K         K L", "L K         K L",
                            "L K         K L", "L K         K L", "LLLL       LLLL" } })

                /*
                 * TODO: UPDATE BLOCK LEGEND
                 */
                .addElement('A', chainAllGlasses())
                .addElement(
                    'B',
                    ofBlock(WerkstoffLoader.BWBlockCasingsAdvanced, PrPMaterials.SinteredSiliconCarbide.getId()))
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
                .addElement('M', ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.SPC_CASING))
                .addElement('O', ofBlock(WerkstoffLoader.BWBlockCasings, PrPMaterials.SinteredSiliconCarbide.getId()))
                .addElement('N', ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.ISOTOPIC_DOPING_CASING))
                .addElement(
                    'P',
                    buildHatchAdder(MTE_CIDC.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, InputHatch, OutputBus, OutputHatch, Maintenance)
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

        checkOneMaintenanceHatch(errors);
        checkHasEnergyHatch(errors);
        checkHasAnyInput(errors);
        checkHasAnyOutput(errors);
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
        tt.addMachineType("Doping Chamber, CIDC")
            .addInfo("Assembles rare-earth dopant matrices for UV photoresist")
            .addSupportAny()
            .beginStructureBlock(15, 16, 15, true)
            .addController("Center, 2nd layer, 3rd slice from the front")
            .addCasing("90+", "Isotopic Doping Casing", false)
            .addEnergyHatch("1+", "Any isotopic doping casing", 1)
            .addMaintenanceHatch("1", "Any isotopic doping casing", 1)
            .addInputBus("1+", "Any isotopic doping casing", 1)
            .addInputHatch("1+", "Any isotopic doping casing", 1)
            .addOutputBus("1+", "Any isotopic doping casing", 1)
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
