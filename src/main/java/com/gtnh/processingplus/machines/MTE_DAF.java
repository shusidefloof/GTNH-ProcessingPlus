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
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

/**
 * Dual Atmosphere Furnace — a sealed 5×5×5 reaction chamber that can be switched between
 * an oxidizing atmosphere (air/O₂) and an inert atmosphere (N₂/Ar). Use a screwdriver
 * to toggle the mode. Runs {@code sDAFOxidizingRecipes} or {@code sDAFInertRecipes}.
 */
public class MTE_DAF extends MTEExtendedPowerMultiBlockBase<MTE_DAF> implements ISurvivalConstructable {

    private static final int CASING_INDEX = 11;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    // Controller (~) at slice 0, row 2, col 2 — centre of the front face.
    private static final int OFFSET_X = 2;
    private static final int OFFSET_Y = 2;
    private static final int OFFSET_Z = 0;

    private boolean mIsOxidizing = true;

    private static IStructureDefinition<MTE_DAF> STRUCTURE_DEFINITION = null;

    public MTE_DAF(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected MTE_DAF(MTE_DAF prototype) {
        super(prototype.mName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTE_DAF(this);
    }

    // ── Structure ────────────────────────────────────────────────────────────────

    @Override
    public IStructureDefinition<MTE_DAF> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTE_DAF>builder()
                .addShape(
                    STRUCTURE_PIECE_MAIN,
                    // 5 × 5 × 5 sealed chamber. All outer faces are DAF casing; interior is hollow.
                    // Controller (~) at the centre of the front face.
                    new String[][] {
                        // z=0 — front face (controller)
                        { "CCCCC", "CCCCC", "CC~CC", "CCCCC", "CCCCC" },
                        // z=1..3 — hollow interior ring
                        { "CCCCC", "C   C", "C   C", "C   C", "CCCCC" },
                        { "CCCCC", "C   C", "C   C", "C   C", "CCCCC" },
                        { "CCCCC", "C   C", "C   C", "C   C", "CCCCC" },
                        // z=4 — back face
                        { "CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC" }, })
                // C = Dual-Sealed Atmosphere Casing — the only hatch-bearing element
                .addElement(
                    'C',
                    buildHatchAdder(MTE_DAF.class)
                        .atLeast(Energy, InputBus, InputHatch, OutputBus, OutputHatch, Maintenance, Muffler)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(GTNHPPBlocks.CASINGS, BlockGTNHPPCasings.DAF_CASING))
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

    // ── Validation ───────────────────────────────────────────────────────────────

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        checkPiece(STRUCTURE_PIECE_MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z, errors);
        if (mMaintenanceHatches.size() != 1) errors.add(StructureErrorRegistry.UNKNOWN_STRUCTURE_ERROR);
    }

    // ── Recipe logic ─────────────────────────────────────────────────────────────

    @Override
    public RecipeMap<?> getRecipeMap() {
        return mIsOxidizing ? GTNHPPRecipeMaps.sDAFOxidizingRecipes : GTNHPPRecipeMaps.sDAFInertRecipes;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic();
    }

    // ── Mode toggle ──────────────────────────────────────────────────────────────

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aStack) {
        mIsOxidizing = !mIsOxidizing;
        GTUtility.sendChatToPlayer(aPlayer, "DAF atmosphere: " + (mIsOxidizing ? "Oxidizing (O2)" : "Inert (N2/Ar)"));
    }

    // ── NBT persistence ──────────────────────────────────────────────────────────

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("IsOxidizing", mIsOxidizing);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mIsOxidizing = aNBT.getBoolean("IsOxidizing");
    }

    // ── Textures ─────────────────────────────────────────────────────────────────

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

    // ── Tooltip ──────────────────────────────────────────────────────────────────

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Dual Atmosphere Furnace, DAF")
            .addInfo(EnumChatFormatting.GRAY + "Sealed reaction chamber with switchable atmosphere.")
            .addInfo(
                EnumChatFormatting.AQUA + "Screwdriver"
                    + EnumChatFormatting.GRAY
                    + " toggles between "
                    + EnumChatFormatting.RED
                    + "Oxidizing (O₂)"
                    + EnumChatFormatting.GRAY
                    + " and "
                    + EnumChatFormatting.AQUA
                    + "Inert (N₂/Ar)"
                    + EnumChatFormatting.GRAY
                    + " mode.")
            .beginStructureBlock(5, 5, 5, true)
            .addController("Centre of the front face")
            .addCasingInfoMin("Dual-Sealed Atmosphere Casing", 20, false)
            .addInputBus("Any casing", 1)
            .addInputHatch("Any casing", 1)
            .addOutputBus("Any casing", 1)
            .addOutputHatch("Any casing", 1)
            .addEnergyHatch("Any casing", 1)
            .addMufflerHatch("Any casing", 1)
            .addMaintenanceHatch("Any casing", 1)
            .toolTipFinisher("_Shusi_");
        return tt;
    }

    // ── Info display ─────────────────────────────────────────────────────────────

    @Override
    public String[] getInfoData() {
        return new String[] {
            StatCollector.translateToLocal("GT5U.multiblock.Progress") + ": "
                + EnumChatFormatting.GREEN
                + mProgresstime / 20
                + EnumChatFormatting.RESET
                + " s / "
                + EnumChatFormatting.YELLOW
                + mMaxProgresstime / 20
                + EnumChatFormatting.RESET
                + " s",
            "Atmosphere: "
                + (mIsOxidizing ? EnumChatFormatting.RED + "Oxidizing" : EnumChatFormatting.AQUA + "Inert") };
    }

    // ── Sound / flags ────────────────────────────────────────────────────────────

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
