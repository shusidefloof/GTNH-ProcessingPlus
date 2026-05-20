package com.gtnh.processingplus.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

public class GTNHPPItems {

    /** Waste output produced when the SPC station sequence or tier requirement is not met. */
    public static Item SCORCHED_CIRCUIT_BOARD;

    public static void register() {
        SCORCHED_CIRCUIT_BOARD = new Item().setUnlocalizedName("gtnhpp.scorched_circuit_board")
            .setTextureName("minecraft:paper") // placeholder — replace with custom art
            .setCreativeTab(CreativeTabs.tabMaterials);
        GameRegistry.registerItem(SCORCHED_CIRCUIT_BOARD, "scorched_circuit_board");
    }

    public static ItemStack scorchedBoard(int amount) {
        return new ItemStack(SCORCHED_CIRCUIT_BOARD, amount);
    }
}
