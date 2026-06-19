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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;


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
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;

/**
 * Supercritical Dryer — high-pressure autoclave for scCO₂ gel drying and extraction.
 * Brings liquid CO₂ above its critical point (31 °C, 73.8 bar) to extract pore-filling
 * solvents without surface tension collapsing the gel network, yielding aerogels and
 * enabling supercritical CO₂ extraction of resins and pharmaceuticals.
 */
public class MTE_SCD extends MTEExtendedPowerMultiBlockBase<MTE_SCD> implements ISurvivalConstructable {

    private static final int CASING_INDEX = 11;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    // Controller (~) is at slice z=0, row y=5, col x=7
    private static final int OFFSET_X = 7;
    private static final int OFFSET_Y = 5;
    private static final int OFFSET_Z = 0;

    private static IStructureDefinition<MTE_SCD> STRUCTURE_DEFINITION = null;

    private static Block gtBlock(String name) {
        Block b = GameRegistry.findBlock("gregtech", name);
        if (b == null) throw new RuntimeException("SCD requires GregTech block: " + name);
        return b;
    }

    private static Block bwBlock(String name) {
        Block b = GameRegistry.findBlock("bartworks", name);
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

    @Override
    public IStructureDefinition<MTE_SCD> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_SCD>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    new String[][] {
                        { // z=0 — front face; controller (~) at row 5 col 7
                            "            ",
                            "            ",
                            "       C    ",
                            "      ECE   ",
                            "HFHFFEBDBE  ",
                            "HGH CCD~DCC ",
                            "HAH  EBDBE  ",
                            "HAH   ECE   ",
                            "HGH    C    ",
                            "H H         "
                        },
                        { // z=1
                            "            ",
                            "            ",
                            "      ICI   ",
                            " FFFFIB BI  ",
                            "FBBBBB   BI ",
                            "GBGFBB    C ",
                            "A A IB   BI ",
                            "A A HIB BIH ",
                            "GGG H ICIHH ",
                            "    H     H "
                        },
                        { // z=2
                            "            ",
                            "      EEE   ",
                            "     I C I  ",
                            "    I     I ",
                            "HFHE       E",
                            "HGHEC     CE",
                            "HAHE       E",
                            "HAH I     I ",
                            "HGH HI C IH ",
                            "H H   EEE   "
                        },
                        { // z=3
                            "            ",
                            "      EAE   ",
                            "    II   II ",
                            "    I     I ",
                            "   E       E",
                            "   A       A",
                            "   E       E",
                            "    I     I ",
                            "    II   II ",
                            "      EAE   "
                        },
                        { // z=4
                            "            ",
                            "      EAE   ",
                            "    II   II ",
                            "    I     I ",
                            "   E       E",
                            "   A       A",
                            "   E       E",
                            "    I     I ",
                            "    II   II ",
                            "      EAE   "
                        },
                        { // z=5
                            "            ",
                            "      EAE   ",
                            "    II   II ",
                            "    I     I ",
                            "   E       E",
                            "   A       A",
                            "   E       E",
                            "    I     I ",
                            "    II   II ",
                            "      EAE   "
                        },
                        { // z=6
                            "            ",
                            "      EEE   ",
                            "     I C I  ",
                            "    I     I ",
                            "HFHE       E",
                            "HGHEC     CE",
                            "HAHE       E",
                            "HAH I     I ",
                            "HGH HI C IH ",
                            "H H   EEE   "
                        },
                        { // z=7
                            "            ",
                            "            ",
                            "      ICI   ",
                            " FFFFIB BI  ",
                            "FBBBBB   BI ",
                            "GBGFB     C ",
                            "A A IB   BI ",
                            "A A HIB BIH ",
                            "GGG HHICIHH ",
                            "    H     H "
                        },
                        { // z=8 — back face
                            "            ",
                            "            ",
                            "       C    ",
                            "      ECE   ",
                            "HFHFFEBDBE  ",
                            "HGH CCDCDCC ",
                            "HAH  EBDBE  ",
                            "HAH   ECE   ",
                            "HGH    C    ",
                            "H H         "
                        }
                    })
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
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack,
        List<StructureError> errors) {
        checkPiece(STRUCTURE_PIECE_MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z, errors);
        if (mMaintenanceHatches.size() != 1) errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNHPPRecipeMaps.sSCDRecipes;
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
        tt.addMachineType("Supercritical Dryer, SCD")
            .addInfo(EnumChatFormatting.GRAY + "Brings liquid CO" + EnumChatFormatting.GRAY
                + "₂ above its critical point " + EnumChatFormatting.AQUA
                + "(31 °C, 73.8 bar)" + EnumChatFormatting.GRAY + " inside the pressure vessel.")
            .addInfo(EnumChatFormatting.GRAY + "Eliminates surface tension during gel drying — preserving the")
            .addInfo(EnumChatFormatting.GRAY + "nanoporous network to produce " + EnumChatFormatting.AQUA
                + "aerogels" + EnumChatFormatting.GRAY + " and performing " + EnumChatFormatting.AQUA
                + "scCO₂ extraction" + EnumChatFormatting.GRAY + ".")
            .addSeparator()
            .addStaticParallelInfo(8)
            .addSeparator()
            .addInfo(EnumChatFormatting.YELLOW + "Processes:" + EnumChatFormatting.GRAY
                + " gel aging, solvent exchange, supercritical CO₂ drying,")
            .addInfo(EnumChatFormatting.GRAY + "resin/pharmaceutical extraction, PAN aerogel gelation.")
            .addInfo(EnumChatFormatting.GOLD + "Recovered CO₂ output loops back into the CSC.")
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
