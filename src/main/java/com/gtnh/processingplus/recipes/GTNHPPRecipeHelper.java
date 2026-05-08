package com.gtnh.processingplus.recipes;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * Central helper for all GTNHPP recipe definitions.
 * Supports:
 * - GregTech Materials fluids
 * - GT++ / modded registry fluids (string lookup)
 * - consistent OreDict + circuits
 */
public class GTNHPPRecipeHelper {

    // =========================
    // ITEMS
    // =========================

    public static ItemStack dust(Materials m, int amount) {
        return GTOreDictUnificator.get(OrePrefixes.dust, m, amount);
    }

    public static ItemStack dustSmall(Materials m, int amount) {
        return GTOreDictUnificator.get(OrePrefixes.dustSmall, m, amount);
    }

    public static ItemStack ingot(Materials m, int amount) {
        return GTOreDictUnificator.get(OrePrefixes.ingot, m, amount);
    }

    public static ItemStack plate(Materials m, int amount) {
        return GTOreDictUnificator.get(OrePrefixes.plate, m, amount);
    }

    public static ItemStack wireFine(Materials m, int amount) {
        return GTOreDictUnificator.get(OrePrefixes.wireFine, m, amount);
    }

    // =========================
    // CIRCUITS
    // =========================

    public static ItemStack circuit(int tier) {
        return GTUtility.getIntegratedCircuit(tier);
    }

    // =========================================================
    // FLUID SYSTEM (HYBRID SAFE)
    // =========================================================

    /**
     * GT CORE fluids (safe, typed, no strings)
     */
    public static FluidStack fluid(Materials m, int amount) {
        if (m == null || m.getFluid(amount) == null) {
            throw new IllegalArgumentException("Invalid GT material fluid: " + m);
        }
        return m.getFluid(amount);
    }

    /**
     * GT++ / modded / unknown registry fluids (safe string lookup)
     *
     * Example:
     * fluid("hydrogenperoxide", 1000)
     */
    public static FluidStack fluid(String name, int amount) {
        Fluid f = FluidRegistry.getFluid(name);

        if (f == null) {
            throw new IllegalStateException(
                "Missing fluid in FluidRegistry: '" + name + "'"
            );
        }

        return new FluidStack(f, amount);
    }

    // =========================
    // GTNH MATERIAL SHORTCUTS
    // =========================

    public static Materials mat(String name) {
        return (Materials) Materials.get(name);
    }

    // =========================
    // OPTIONAL SAFETY DEBUG
    // =========================

    public static void assertNotNull(Object o, String msg) {
        if (o == null) {
            throw new IllegalStateException("GTNHPP Helper error: " + msg);
        }
    }
}
