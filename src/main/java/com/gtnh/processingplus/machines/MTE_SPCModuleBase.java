package com.gtnh.processingplus.machines;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

import com.gtnewhorizon.structurelib.util.Vec3Impl;
import com.gtnh.processingplus.machines.spc.SPCModuleType;

import gregtech.api.enums.ItemList;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;

/**
 * Base class for the SPC's external upgrade modules (Bio / Cryo / Quantum).
 *
 * Linking mirrors the PCB Factory: left-click an SPC controller with a data stick to copy its
 * coordinates onto the stick, then right-click this module's controller to link. The link is
 * persisted as a coordinate set and re-established periodically (e.g. on world load). A module may
 * serve several SPCs. The module itself runs no recipes — it only advertises its presence.
 */
public abstract class MTE_SPCModuleBase<T extends MTEEnhancedMultiBlockBase<T>> extends MTEEnhancedMultiBlockBase<T> {

    /** Max per-axis distance between a module and an SPC it can link to. */
    public static final int LINK_RANGE = 16;

    /** Coordinates of every SPC controller this module is linked to. Persisted to NBT. */
    protected final Set<Vec3Impl> controllerCoords = new HashSet<>();

    protected MTE_SPCModuleBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public MTE_SPCModuleBase(String aName) {
        super(aName);
    }

    /** Which module category this is — used by the SPC to satisfy recipe gates. */
    public abstract SPCModuleType getModuleType();

    /** True when the module structure is formed and allowed to work. */
    public boolean isModuleActive() {
        IGregTechTileEntity te = getBaseMetaTileEntity();
        return te != null && mMachine && te.isAllowedToWork();
    }

    // Modules carry no maintenance, power, or recipe processing.
    @Override
    public boolean doRandomMaintenanceDamage() {
        return true;
    }

    @Override
    public boolean supportsPowerPanel() {
        return false;
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    protected void setHatchRecipeMap(MTEHatchInput hatch) {
        // no hatches / no recipe map
    }

    @Override
    public CheckRecipeResult checkProcessing() {
        return CheckRecipeResultRegistry.NONE;
    }

    // -------------------------------------------------------------------------
    // Linking
    // -------------------------------------------------------------------------

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        super.onPostTick(aBaseMetaTileEntity, aTimer);
        // Re-establish links periodically (controllers may load after this module).
        if (aTimer % 100 == 5 && !controllerCoords.isEmpty()) {
            for (Vec3Impl c : controllerCoords) {
                trySetControllerFromCoord(c.get(0), c.get(1), c.get(2));
            }
        }
    }

    private boolean trySetControllerFromCoord(int x, int y, int z) {
        IGregTechTileEntity our = getBaseMetaTileEntity();
        if (our == null) return false;
        if (Math.abs(our.getXCoord() - x) > LINK_RANGE) return false;
        if (Math.abs(our.getYCoord() - y) > LINK_RANGE) return false;
        if (Math.abs(our.getZCoord() - z) > LINK_RANGE) return false;
        TileEntity te = our.getWorld()
            .getTileEntity(x, y, z);
        if (!(te instanceof IGregTechTileEntity)) return false;
        IMetaTileEntity mte = ((IGregTechTileEntity) te).getMetaTileEntity();
        if (!(mte instanceof MTE_SPC)) return false;
        ((MTE_SPC) mte).registerLinkedModule(this);
        return true;
    }

    private boolean tryLinkDataStick(EntityPlayer aPlayer) {
        ItemStack stick = aPlayer.inventory.getCurrentItem();
        if (!ItemList.Tool_DataStick.isStackEqual(stick, false, true)) return false;
        NBTTagCompound tag = stick.stackTagCompound;
        if (tag == null || !"SPCUpgrade".equals(tag.getString("type"))) return false;

        int x = tag.getInteger("x");
        int y = tag.getInteger("y");
        int z = tag.getInteger("z");
        IGregTechTileEntity our = getBaseMetaTileEntity();
        if (our != null && (Math.abs(our.getXCoord() - x) > LINK_RANGE || Math.abs(our.getYCoord() - y) > LINK_RANGE
            || Math.abs(our.getZCoord() - z) > LINK_RANGE)) {
            aPlayer.addChatMessage(new ChatComponentText("Link failed: SPC out of range."));
            return true;
        }
        if (trySetControllerFromCoord(x, y, z)) {
            controllerCoords.add(new Vec3Impl(x, y, z));
            aPlayer.addChatMessage(new ChatComponentText("Linked to Spectral Photolithography Chamber."));
        } else {
            aPlayer.addChatMessage(new ChatComponentText("Link failed: no SPC controller at that location."));
        }
        return true;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (tryLinkDataStick(aPlayer)) return true;
        return super.onRightclick(aBaseMetaTileEntity, aPlayer);
    }

    @Override
    public void onBlockDestroyed() {
        for (Vec3Impl c : controllerCoords) {
            TileEntity te = getBaseMetaTileEntity().getWorld()
                .getTileEntity(c.get(0), c.get(1), c.get(2));
            if (te instanceof IGregTechTileEntity) {
                IMetaTileEntity mte = ((IGregTechTileEntity) te).getMetaTileEntity();
                if (mte instanceof MTE_SPC) ((MTE_SPC) mte).unregisterLinkedModule(this);
            }
        }
        super.onBlockDestroyed();
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        if (!controllerCoords.isEmpty()) {
            int[] arr = new int[controllerCoords.size() * 3];
            int i = 0;
            for (Vec3Impl c : controllerCoords) {
                arr[i++] = c.get(0);
                arr[i++] = c.get(1);
                arr[i++] = c.get(2);
            }
            aNBT.setIntArray("spcLinks", arr);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (aNBT.hasKey("spcLinks")) {
            int[] arr = aNBT.getIntArray("spcLinks");
            controllerCoords.clear();
            if (arr.length % 3 != 0) return;
            for (int i = 0; i < arr.length; i += 3) {
                controllerCoords.add(new Vec3Impl(arr[i], arr[i + 1], arr[i + 2]));
            }
        }
    }
}
