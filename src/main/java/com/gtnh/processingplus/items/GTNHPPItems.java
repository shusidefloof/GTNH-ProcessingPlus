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

    public static void register() {
        SCORCHED_CIRCUIT_BOARD = new Item().setUnlocalizedName("gtnhpp.scorched_circuit_board")
            .setTextureName("minecraft:paper")
            .setCreativeTab(CreativeTabs.tabMaterials);
        GameRegistry.registerItem(SCORCHED_CIRCUIT_BOARD, "scorched_circuit_board");

        OPTICAL_CIRCUIT_BOARD_RAW = new Item().setUnlocalizedName("gtnhpp.optical_circuit_board_raw")
            .setTextureName("minecraft:paper")
            .setCreativeTab(CreativeTabs.tabMaterials);
        GameRegistry.registerItem(OPTICAL_CIRCUIT_BOARD_RAW, "optical_circuit_board_raw");
    }

    public static ItemStack scorchedBoard(int amount) {
        return new ItemStack(SCORCHED_CIRCUIT_BOARD, amount);
    }

    public static ItemStack opticalBoardRaw(int amount) {
        return new ItemStack(OPTICAL_CIRCUIT_BOARD_RAW, amount);
    }
}
