package com.gtnh.processingplus.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

public class GTNHPPItems {

    /** Waste output produced when the SPC station sequence or tier requirement is not met. */
    public static Item SCORCHED_CIRCUIT_BOARD;

    /** Raw UEV-tier optical circuit board substrate — precursor to Circuit_Board_Optical. */
    public static Item OPTICAL_CIRCUIT_BOARD_RAW;

    // ── LuV exotics: single-use chain intermediates (Vibranium + Unobtanium) ──
    public static Item RED_HOT_ADAMANTIUM;
    public static Item RED_HOT_VIBRANT_ADAMANTIUM;
    public static Item UNOBTANIUM_ORE_CONCENTRATE;
    public static Item UNOBTANIUM_CRYSTAL_FRAGMENT;
    public static Item PURIFIED_UNOBTANIUM_CRYSTAL;
    public static Item PURIFIED_UNOBTANIUM_SHARD;
    public static Item UNOBTANIUM_CLUMP;
    public static Item IRON_SLAG;

    /**
     * Register a plain single-meta item. The display name is hard-coded via getItemStackDisplayName
     * (matching how the casings name themselves) so it shows correctly regardless of lang/domain.
     */
    private static Item simpleItem(String reg, String displayName, String texture) {
        Item it = new Item() {

            @Override
            public String getItemStackDisplayName(ItemStack stack) {
                return displayName;
            }
        };
        it.setUnlocalizedName("gtnhpp." + reg)
            .setTextureName(texture)
            .setCreativeTab(CreativeTabs.tabMaterials);
        GameRegistry.registerItem(it, reg);
        return it;
    }

    public static void register() {
        SCORCHED_CIRCUIT_BOARD = new Item().setUnlocalizedName("gtnhpp.scorched_circuit_board")
            .setTextureName("minecraft:paper")
            .setCreativeTab(CreativeTabs.tabMaterials);
        GameRegistry.registerItem(SCORCHED_CIRCUIT_BOARD, "scorched_circuit_board");

        OPTICAL_CIRCUIT_BOARD_RAW = new Item().setUnlocalizedName("gtnhpp.optical_circuit_board_raw")
            .setTextureName("minecraft:paper")
            .setCreativeTab(CreativeTabs.tabMaterials);
        GameRegistry.registerItem(OPTICAL_CIRCUIT_BOARD_RAW, "optical_circuit_board_raw");

        // LuV exotics — single-use intermediates (textures are vanilla placeholders for now)
        RED_HOT_ADAMANTIUM = simpleItem("red_hot_adamantium", "Red Hot Adamantium", "minecraft:gold_ingot");
        RED_HOT_VIBRANT_ADAMANTIUM = simpleItem(
            "red_hot_vibrant_adamantium",
            "Red Hot Vibrant-Adamantium Ingot",
            "minecraft:gold_ingot");
        UNOBTANIUM_ORE_CONCENTRATE = simpleItem(
            "unobtanium_ore_concentrate",
            "Unobtanium Ore Concentrate",
            "minecraft:redstone");
        UNOBTANIUM_CRYSTAL_FRAGMENT = simpleItem(
            "unobtanium_crystal_fragment",
            "Unobtanium Crystal Fragment",
            "minecraft:quartz");
        PURIFIED_UNOBTANIUM_CRYSTAL = simpleItem(
            "purified_unobtanium_crystal",
            "Purified Unobtanium Crystal",
            "minecraft:diamond");
        PURIFIED_UNOBTANIUM_SHARD = simpleItem(
            "purified_unobtanium_shard",
            "Purified Unobtanium Crystal Shard",
            "minecraft:emerald");
        UNOBTANIUM_CLUMP = simpleItem("unobtanium_clump", "Unobtanium Clump", "minecraft:gunpowder");
        IRON_SLAG = simpleItem("iron_slag", "Iron Slag", "minecraft:gunpowder");
    }

    public static ItemStack stack(Item item, int amount) {
        return new ItemStack(item, amount);
    }

    public static ItemStack scorchedBoard(int amount) {
        return new ItemStack(SCORCHED_CIRCUIT_BOARD, amount);
    }

    public static ItemStack opticalBoardRaw(int amount) {
        return new ItemStack(OPTICAL_CIRCUIT_BOARD_RAW, amount);
    }
}
