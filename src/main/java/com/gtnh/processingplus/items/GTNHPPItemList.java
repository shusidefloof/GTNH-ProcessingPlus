package com.gtnh.processingplus.items;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import gregtech.api.interfaces.IItemContainer;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

/**
 * Named handles for every synthesis intermediate registered by this mod.
 * Material-form items (dust, plate, rod, etc.) come from GT's MetaGeneratedItem01
 * via GTNHPPMaterials and are accessed through GTNHPPMaterials.Xxx.getDust(n) or
 * GTOreDictUnificator.get(OrePrefixes.yyy, GTNHPPMaterials.Xxx, n).
 */
public enum GTNHPPItemList implements IItemContainer {

    // --- Nylon-6,6 synthesis intermediates ---
    AdipicAcid, HMD, Adiponitrile,

    // --- PLA synthesis intermediates ---
    LacticAcid, Lactide,

    // --- Carbon fiber synthesis intermediates ---
    Acrylonitrile, PANFiber, StabilizedPANFiber, CarbonFiberTow,

    // --- Kapton synthesis intermediates ---
    PMDA, ODA, PolyamicAcidFilm,

    // --- hBN synthesis intermediates ---
    BoronTrioxide, BoronCarbide, CrudeHBN,

    // --- Aerogel synthesis intermediate ---
    WetSilicaGel;

    // @formatter:off
    public static final GTNHPPItemList[] INTER_MAP = {
        AdipicAcid, HMD, Adiponitrile,
        LacticAcid, Lactide,
        Acrylonitrile, PANFiber, StabilizedPANFiber, CarbonFiberTow,
        PMDA, ODA, PolyamicAcidFilm,
        BoronTrioxide, BoronCarbide, CrudeHBN,
        WetSilicaGel,
    };
    // @formatter:on

    // -------------------------------------------------------------------------
    // IItemContainer implementation

    private ItemStack mStack;
    private boolean mHasNotBeenSet = true;

    @Override
    public IItemContainer set(Item aItem) {
        mHasNotBeenSet = false;
        if (aItem == null) return this;
        mStack = GTUtility.copyAmount(1, new ItemStack(aItem, 1, 0));
        return this;
    }

    @Override
    public IItemContainer set(ItemStack aStack) {
        mHasNotBeenSet = false;
        mStack = GTUtility.copyAmount(1, aStack);
        return this;
    }

    @Override
    public Item getItem() {
        checkSet();
        if (GTUtility.isStackInvalid(mStack)) return null;
        return mStack.getItem();
    }

    @Override
    public Block getBlock() {
        checkSet();
        return GTUtility.getBlockFromItem(getItem());
    }

    @Override
    public boolean hasBeenSet() {
        return !mHasNotBeenSet;
    }

    @Override
    public boolean isStackEqual(Object aStack) {
        return isStackEqual(aStack, false, false);
    }

    @Override
    public boolean isStackEqual(Object aStack, boolean aWildcard, boolean aIgnoreNBT) {
        if (GTUtility.isStackInvalid(aStack)) return false;
        return GTUtility.areUnificationsEqual((ItemStack) aStack, aWildcard ? getWildcard(1) : get(1), aIgnoreNBT);
    }

    @Override
    public ItemStack get(long aAmount, Object... aReplacements) {
        checkSet();
        if (GTUtility.isStackInvalid(mStack)) return GTUtility.copyAmount(aAmount, aReplacements);
        return GTUtility.copyAmount(aAmount, GTOreDictUnificator.get(mStack));
    }

    @Override
    public ItemStack getWildcard(long aAmount, Object... aReplacements) {
        checkSet();
        if (GTUtility.isStackInvalid(mStack)) return GTUtility.copyAmount(aAmount, aReplacements);
        return GTUtility.copyAmountAndMetaData(aAmount, 32767, GTOreDictUnificator.get(mStack));
    }

    @Override
    public ItemStack getUndamaged(long aAmount, Object... aReplacements) {
        checkSet();
        if (GTUtility.isStackInvalid(mStack)) return GTUtility.copyAmount(aAmount, aReplacements);
        return GTUtility.copyAmountAndMetaData(aAmount, 0, GTOreDictUnificator.get(mStack));
    }

    @Override
    public ItemStack getAlmostBroken(long aAmount, Object... aReplacements) {
        checkSet();
        if (GTUtility.isStackInvalid(mStack)) return GTUtility.copyAmount(aAmount, aReplacements);
        return GTUtility.copyAmountAndMetaData(aAmount, mStack.getMaxDamage() - 1, GTOreDictUnificator.get(mStack));
    }

    @Override
    public ItemStack getWithDamage(long aAmount, long aMetaValue, Object... aReplacements) {
        checkSet();
        if (GTUtility.isStackInvalid(mStack)) return GTUtility.copyAmount(aAmount, aReplacements);
        return GTUtility.copyAmountAndMetaData(aAmount, aMetaValue, GTOreDictUnificator.get(mStack));
    }

    @Override
    public ItemStack getWithCharge(long aAmount, int aEnergy, Object... aReplacements) {
        return get(aAmount, aReplacements);
    }

    @Override
    public ItemStack getWithName(long aAmount, String aDisplayName, Object... aReplacements) {
        ItemStack rStack = get(1, aReplacements);
        if (GTUtility.isStackInvalid(rStack)) return null;
        rStack.setStackDisplayName(aDisplayName);
        return GTUtility.copyAmount(aAmount, rStack);
    }

    @Override
    public IItemContainer registerOre(Object... aOreNames) {
        checkSet();
        for (Object tOreName : aOreNames) GTOreDictUnificator.registerOre(tOreName, get(1));
        return this;
    }

    @Override
    public IItemContainer registerWildcardAsOre(Object... aOreNames) {
        checkSet();
        for (Object tOreName : aOreNames) GTOreDictUnificator.registerOre(tOreName, getWildcard(1));
        return this;
    }

    private void checkSet() {
        if (mHasNotBeenSet)
            throw new IllegalAccessError("GTNHPPItemList." + name() + " has not been initialized yet!");
    }
}
