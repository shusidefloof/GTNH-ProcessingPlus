package com.gtnh.processingplus.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

public class GTNHPPItems {

    /** Waste output produced when the SPC station sequence or tier requirement is not met. */
    public static Item SCORCHED_CIRCUIT_BOARD;

    /** Raw UEV-tier optical circuit board substrate, precursor to Circuit_Board_Optical. */
    public static Item OPTICAL_CIRCUIT_BOARD_RAW;

    // INTERMEDIATES MetaItem: see the Intermediate enum.

    /** Promethium Betavoltaic Cell: fuel for the RTG generator multiblock. */
    public static Item PROMETHIUM_BETAVOLTAIC_CELL;

    /** Shared MetaItem holding every single-use chain intermediate ({@link Intermediate}). */
    public static Item INTERMEDIATES;

    /** Consumed by the HPR to retune its resonance field when jumping between frequencies (1 per step). */
    public static Item PHASE_SYNCHRONIZER;

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
        SCORCHED_CIRCUIT_BOARD = simpleItem("scorched_circuit_board", "Scorched Circuit Board", "minecraft:paper");

        OPTICAL_CIRCUIT_BOARD_RAW = simpleItem(
            "optical_circuit_board_raw",
            "Raw Optical Circuit Board",
            "minecraft:paper");

        PROMETHIUM_BETAVOLTAIC_CELL = simpleItem(
            "promethium_betavoltaic_cell",
            "Promethium Betavoltaic Cell",
            "minecraft:glowstone_dust");

        PHASE_SYNCHRONIZER = simpleItem("phase_synchronizer", "Phase Synchronizer", "minecraft:blaze_powder");

        // Single shared MetaItem for all demoted dust-only chain intermediates (see Intermediate enum).
        INTERMEDIATES = new ItemGTNHPPIntermediates();
        GameRegistry.registerItem(INTERMEDIATES, "intermediates");
    }

    public static ItemStack scorchedBoard(int amount) {
        return new ItemStack(SCORCHED_CIRCUIT_BOARD, amount);
    }

    public static ItemStack opticalBoardRaw(int amount) {
        return new ItemStack(OPTICAL_CIRCUIT_BOARD_RAW, amount);
    }

    public static ItemStack betavoltaicCell(int amount) {
        return new ItemStack(PROMETHIUM_BETAVOLTAIC_CELL, amount);
    }

    public static ItemStack phaseSynchronizer(int amount) {
        return new ItemStack(PHASE_SYNCHRONIZER, amount);
    }
}
