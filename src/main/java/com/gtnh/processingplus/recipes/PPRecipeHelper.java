package com.gtnh.processingplus.recipes;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import bartworks.system.material.Werkstoff;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.Material;

/**
 * Central helper for all GTNHPP recipe definitions.
 * Supports:
 * - GregTech Materials items + fluids
 * - Werkstoff items + fluids
 * - GT++ / modded registry fluids (string lookup)
 * - consistent OreDict + circuits
 */
public class PPRecipeHelper {

    // =========================
    // ITEMS — GT Materials
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

    public static ItemStack foil(Materials m, int amount) {
        return GTOreDictUnificator.get(OrePrefixes.foil, m, amount);
    }

    public static ItemStack wireFine(Materials m, int amount) {
        return GTOreDictUnificator.get(OrePrefixes.wireFine, m, amount);
    }

    // =========================
    // ITEMS — Werkstoff
    // =========================

    public static ItemStack dust(Werkstoff w, int amount) {
        return w.get(OrePrefixes.dust, amount);
    }

    public static ItemStack dustSmall(Werkstoff w, int amount) {
        return w.get(OrePrefixes.dustSmall, amount);
    }

    public static ItemStack ingot(Werkstoff w, int amount) {
        return w.get(OrePrefixes.ingot, amount);
    }

    public static ItemStack plate(Werkstoff w, int amount) {
        return w.get(OrePrefixes.plate, amount);
    }

    public static ItemStack gem(Werkstoff w, int amount) {
        return w.get(OrePrefixes.gem, amount);
    }

    public static ItemStack rod(Werkstoff w, int amount) {
        return w.get(OrePrefixes.stick, amount);
    }

    // =========================
    // ITEMS — GT++ Material
    // =========================

    /** GT++ Material objects (e.g. ELEMENT.getInstance().FERMIUM). */
    public static ItemStack dust(Material m, int amount) {
        ItemStack is = m.getDust(amount);
        if (is == null) throw new IllegalStateException("No dust for GT++ material: " + m);
        return is;
    }

    // =========================
    // ITEMS — OreDict
    // =========================

    /** OreDict lookup — for materials not accessible at compile time (e.g. GoodGenerator Werkstoffe). */
    public static ItemStack item(String oreDictEntry, int amount) {
        List<ItemStack> ores = OreDictionary.getOres(oreDictEntry);
        if (ores.isEmpty()) throw new IllegalStateException("No OreDict entry: '" + oreDictEntry + "'");
        ItemStack copy = ores.get(0)
            .copy();
        copy.stackSize = amount;
        return copy;
    }

    // =========================
    // CIRCUITS
    // =========================

    public static ItemStack circuit(int tier) {
        return GTUtility.getIntegratedCircuit(tier);
    }

    // =========================
    // FLUIDS — GT Materials
    // =========================

    public static FluidStack fluid(Materials m, int amount) {
        if (m == null) throw new IllegalArgumentException("Null material");

        FluidStack fs = m.getFluid(amount);
        if (fs != null) return fs;

        fs = m.getGas(amount);
        if (fs != null) return fs;

        throw new IllegalArgumentException("Invalid GT material fluid/gas: " + m.name());
    }

    /** For molten metals — calls getMolten() rather than getFluid(). */
    public static FluidStack molten(Materials m, int amount) {
        FluidStack fs = m.getMolten(amount);
        if (fs == null) throw new IllegalArgumentException("No molten form for material: " + m.name());
        return fs;
    }

    /** For fusion-produced plasmas — calls getPlasma(). */
    public static FluidStack plasma(Materials m, int amount) {
        FluidStack fs = m.getPlasma(amount);
        if (fs == null) throw new IllegalArgumentException("No plasma form for material: " + m.name());
        return fs;
    }

    // =========================
    // FLUIDS — Werkstoff
    // =========================

    /** For fluid/gas Werkstoffe registered with addCells(). */
    public static FluidStack fluid(Werkstoff w, int amount) {
        return w.getFluidOrGas(amount);
    }

    /** For Werkstoffe registered with addMolten(). */
    public static FluidStack molten(Werkstoff w, int amount) {
        return w.getMolten(amount);
    }

    // =========================
    // FLUIDS — String registry
    // =========================

    /** GT++ Material objects (handles SOLID/LIQUID/GAS forms via getFluidStack). */
    public static FluidStack fluid(Material m, int amount) {
        FluidStack fs = m.getFluidStack(amount);
        if (fs == null) throw new IllegalStateException("No fluid for GT++ material: " + m);
        return fs;
    }

    /** GT++ / modded / unknown registry fluids (safe string lookup). */
    public static FluidStack fluid(String name, int amount) {
        Fluid f = FluidRegistry.getFluid(name);
        if (f == null) throw new IllegalStateException("Missing fluid in FluidRegistry: '" + name + "'");
        return new FluidStack(f, amount);
    }

    // =========================
    // MISC
    // =========================

    public static Materials mat(String name) {
        return (Materials) Materials.get(name);
    }

    public static void assertNotNull(Object o, String msg) {
        if (o == null) throw new IllegalStateException("GTNHPP Helper error: " + msg);
    }
}
