package com.gtnh.processingplus.items;

import net.minecraft.item.ItemStack;

/**
 * LuV-exotic solid intermediates, held on one shared MetaItem ({@link ItemGTNHPPIntermediates}).
 *
 * <p>These are the non-chemical "physical state" steps of the Vibranium/Unobtanium chains (red-hot
 * ingots, crystal fragments/shards, clumps, slag, ore concentrate) — they aren't real compounds, so
 * they live as plain items rather than Werkstoffe. Each constant's {@code ordinal()} is its item
 * damage value, so only append, never reorder.
 *
 * <p>{@code reg} is the texture stem: art goes at
 * {@code assets/gtnhprp/textures/items/intermediates/<reg>.png}.
 */
public enum Intermediate {

    RED_HOT_ADAMANTIUM("red_hot_adamantium", "Red Hot Adamantium"),
    RED_HOT_VIBRANT_ADAMANTIUM("red_hot_vibrant_adamantium", "Red Hot Vibrant-Adamantium Ingot"),
    UNOBTANIUM_ORE_CONCENTRATE("unobtanium_ore_concentrate", "Unobtanium Ore Concentrate"),
    UNOBTANIUM_CRYSTAL_FRAGMENT("unobtanium_crystal_fragment", "Unobtanium Crystal Fragment"),
    PURIFIED_UNOBTANIUM_CRYSTAL("purified_unobtanium_crystal", "Purified Unobtanium Crystal"),
    PURIFIED_UNOBTANIUM_SHARD("purified_unobtanium_shard", "Purified Unobtanium Crystal Shard"),
    UNOBTANIUM_CLUMP("unobtanium_clump", "Unobtanium Clump"),
    IRON_SLAG("iron_slag", "Iron Slag");

    public final String reg;
    public final String display;

    Intermediate(String reg, String display) {
        this.reg = reg;
        this.display = display;
    }

    /** ItemStack for this intermediate ({@code meta == ordinal()}). */
    public ItemStack get(int amount) {
        return new ItemStack(GTNHPPItems.INTERMEDIATES, amount, ordinal());
    }
}
