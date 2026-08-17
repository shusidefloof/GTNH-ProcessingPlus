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
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import bartworks.system.material.WerkstoffLoader;
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
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;
import tectech.thing.block.BlockQuantumGlass;
import tectech.thing.casing.TTCasingsContainer;

/**
 * Subatomic Patterning Unit (SPU) — UIV-tier multiblock that imprints a quantum lattice onto the
 * photoresist substrate. Runs the three UIV photoresist steps (stabilized QGP matrix → transcendent
 * lattice → quantum-field imprint), so it gates UIV circuit boards.
 */
public class MTE_SPU extends MTEExtendedPowerMultiBlockBase<MTE_SPU> implements ISurvivalConstructable {

    // CleanStainlessSteelMachineCasing (sBlockCasings4 meta1) — matches SPU's actual wall (sBlockCasings4, meta1).
    private static final int CASING_INDEX = 49;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    // Controller marker '~' sits at slice z=2, row y=14, char x=7 in the exported shape.
    private static final int OFFSET_X = 7;
    private static final int OFFSET_Y = 14;
    private static final int OFFSET_Z = 2;

    private static IStructureDefinition<MTE_SPU> STRUCTURE_DEFINITION = null;

    public MTE_SPU(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected MTE_SPU(MTE_SPU prototype) {
        super(prototype.mName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTE_SPU(this);
    }

    @Override
    public IStructureDefinition<MTE_SPU> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_SPU>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    /*
                     * Block legend (from the in-game structure export):
                     * A -> bw.sheetmetal — Jiritsu sheet metal (PrPMaterials, pale blue-white UIV alloy)
                     * B -> gt.blockcasings4:1 — Clean Stainless Steel Machine Casing (hatch-capable shell)
                     * C -> gt.blockcasingsSE:1 — Space Elevator Support Structure
                     * D -> gt.blockcasingsTT:12 — Ultimate Molecular Casing (TecTech)
                     * E -> gt.blockcasingsTT:13 — Ultimate Advanced Molecular Casing (TecTech)
                     * F -> gt.blockframes:405 — Stellar Alloy Frame Box
                     * G -> tile.quantumGlass:0 — Quantum Glass (TecTech)
                     * H -> BWBlockCasingsAdvanced (bolted) — Aerogel Insulation Panel (PrPMaterials)
                     * I -> BWBlockCasings (plain) — Aerogel Insulation Panel (PrPMaterials)
                     * K -> single legacy Subatomic Patterning Casing block, kept at local (0,0,0)
                     */
                    new String[][] {
                        { "K              ", "               ", "               ", "               ", "       B       ",
                            "      BBB      ", "     B   B     ", "    B     B    ", "   BB     BB   ",
                            "    B     B    ", "     B   B     ", "      BBB      ", "       B       ",
                            "               ", "               ", "               " },
                        { "               ", "               ", "               ", "       B       ", "               ",
                            "               ", "    A BFB A    ", "     BF FB     ", "  B FF   FF B  ",
                            "     BF FB     ", "    A BFB A    ", "               ", "       B       ",
                            "       B       ", "               ", "               " },
                        { "               ", "               ", "       B       ", "               ", "               ",
                            "   AA F F AA   ", "   AA     AA   ", "               ", " B F       F B ",
                            "               ", "   AA     AA   ", "   AA F F AA   ", "               ",
                            "      BBB      ", "      B~B      ", "      BBB      " },
                        { "               ", "       B       ", "               ", "               ", "   AAF   FAA   ",
                            "  AAA     AAA  ", "  AA       AA  ", "               ", "B F         F B",
                            "               ", "  AA       AA  ", "  AAA     AAA  ", "   AAF   FAA   ",
                            "      BBB      ", "      BBB      ", "      BBB      " },
                        { "               ", "      BBB      ", "     BB BB     ", "    A F F A    ", "   AA     AA   ",
                            "  AAC     CAA  ", " AA C     C AA ", "B   C     C   B", "BF  C     C  FB",
                            "B   C     C   B", " AA C     C AA ", "  AAC     CAA  ", "   AA     AA   ",
                            "    A F F A    ", "     B   B     ", "      BBB      " },
                        { "       B       ", "     BBIBB     ", "    BB F BB    ", "               ", "   F       F   ",
                            "               ", "B      D      B", " B    EGE    B ", " F   DGGGD   F ",
                            " B    EGE    B ", "B      D      B", "               ", "   F       F   ",
                            "               ", "    BB F BB    ", "     BBIBB     " },
                        { "      BBB      ", "    BBHHHBB    ", "    B     B    ", "    F     F    ", "               ",
                            "B F         F B", " B    EGE    B ", " F   E   E   F ", "     G   G     ",
                            " F   E   E   F ", " B    EGE    B ", "B F         F B", "               ",
                            "  BBF     FBB  ", "  BBB     BBB  ", "  BBBBHHHBBBB  " },
                        { "     BBBBB     ", "   BBIHHHIBB   ", "  B  F   F  B  ", " B           B ", "B             B",
                            "B             B", " F   DGGGD   F ", "     G   G     ", "     G   G     ",
                            "     G   G     ", " F   DGGGD   F ", "B             B", "BB           BB",
                            " BBB       BBB ", "  BB F   F BB  ", "  BBBIHHHIBBB  " },
                        { "      BBB      ", "    BBHHHBB    ", "    B     B    ", "    F     F    ", "               ",
                            "B F         F B", " B    EGE    B ", " F   E   E   F ", "     G   G     ",
                            " F   E   E   F ", " B    EGE    B ", "B F         F B", "               ",
                            "  BBF     FBB  ", "  BBB     BBB  ", "  BBBBHHHBBBB  " },
                        { "       B       ", "     BBIBB     ", "    BB F BB    ", "               ", "   F       F   ",
                            "               ", "B      D      B", " B    EGE    B ", " F   DGGGD   F ",
                            " B    EGE    B ", "B      D      B", "               ", "   F       F   ",
                            "               ", "    BB F BB    ", "     BBIBB     " },
                        { "               ", "      BBB      ", "     BB BB     ", "    A F F A    ", "   AA     AA   ",
                            "  AAC     CAA  ", " AA C     C AA ", "B   C     C   B", "BF  C     C  FB",
                            "B   C     C   B", " AA C     C AA ", "  AAC     CAA  ", "   AA     AA   ",
                            "    A F F A    ", "     BB BB     ", "      BBB      " },
                        { "               ", "       B       ", "               ", "               ", "   AAF   FAA   ",
                            "  AAA     AAA  ", "  AA       AA  ", "               ", "B F         F B",
                            "               ", "  AA       AA  ", "  AAA     AAA  ", "   AAF   FAA   ",
                            "      BBB      ", "      BBB      ", "      BBB      " },
                        { "               ", "               ", "       B       ", "               ", "               ",
                            "   AA F F AA   ", "   AA     AA   ", "               ", " B F       F B ",
                            "               ", "   AA     AA   ", "   AA F F AA   ", "               ",
                            "      BBB      ", "      BBB      ", "      BBB      " },
                        { "               ", "               ", "               ", "       B       ", "               ",
                            "               ", "    A BFB A    ", "     BF FB     ", "  B FF   FF B  ",
                            "     BF FB     ", "    A BFB A    ", "               ", "       B       ",
                            "       B       ", "               ", "               " },
                        { "               ", "               ", "               ", "               ", "       B       ",
                            "      BBB      ", "     B   B     ", "    B     B    ", "   BB     BB   ",
                            "    B     B    ", "     B   B     ", "      BBB      ", "       B       ",
                            "               ", "               ", "               " } })
                .addElement('K', ofBlock(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.SUBATOMIC_PATTERNING_CASING))
                .addElement('A', ofBlock(GregTechAPI.sBlockSheetmetalBW, PrPMaterials.Jiritsu.getId()))
                .addElement(
                    'B',
                    buildHatchAdder(MTE_SPU.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GregTechAPI.sBlockCasings4, 1))
                .addElement('C', ofBlock(GregTechAPI.sBlockCasingsSE, 1))
                .addElement('D', ofBlock(TTCasingsContainer.sBlockCasingsTT, 12))
                .addElement('E', ofBlock(TTCasingsContainer.sBlockCasingsTT, 13))
                .addElement('F', ofBlock(GregTechAPI.sBlockFrames, 405))
                .addElement('G', ofBlock(BlockQuantumGlass.INSTANCE, 0))
                .addElement(
                    'H',
                    ofBlock(WerkstoffLoader.BWBlockCasingsAdvanced, PrPMaterials.AerogelInsulationPanel.getId()))
                .addElement('I', ofBlock(WerkstoffLoader.BWBlockCasings, PrPMaterials.AerogelInsulationPanel.getId()))
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

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNHPPRecipeMaps.sSPURecipes;
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
        tt.addMachineType("Subatomic Patterning Unit, SPU")
            .addInfo(
                EnumChatFormatting.GRAY + "Patterns matter at the "
                    + EnumChatFormatting.LIGHT_PURPLE
                    + "subatomic scale"
                    + EnumChatFormatting.GRAY
                    + ".")
            .addSeparator()
            .beginStructureBlock(15, 16, 15, true)
            .addController("See NEI structure preview")
            .addCasingInfoMin("Clean Stainless Steel Machine Casing", 90, false)
            .addOtherStructurePart("Jiritsu Sheet Metal", "Diagonal support ribs")
            .addOtherStructurePart("Space Elevator Support Structure", "Equatorial ring")
            .addOtherStructurePart("Ultimate Molecular Casing", "Inner core (TecTech)")
            .addOtherStructurePart("Ultimate Advanced Molecular Casing", "Inner core (TecTech)")
            .addOtherStructurePart("Stellar Alloy Frame Box", "Structural framing")
            .addOtherStructurePart("Quantum Glass", "Core viewport")
            .addOtherStructurePart("Aerogel Insulation Panel Casing (bolted + plain)", "Equatorial paneling")
            .addOtherStructurePart("Subatomic Patterning Casing", "Single legacy block, local (0,0,0)")
            .addInputBus("Any Clean Stainless Steel Machine Casing", 1)
            .addInputHatch("Any Clean Stainless Steel Machine Casing", 1)
            .addOutputBus("Any Clean Stainless Steel Machine Casing", 1)
            .addOutputHatch("Any Clean Stainless Steel Machine Casing", 1)
            .addEnergyHatch("Any Clean Stainless Steel Machine Casing", 1)
            .addMaintenanceHatch("Any Clean Stainless Steel Machine Casing", 1)
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
