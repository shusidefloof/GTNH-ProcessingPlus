package com.gtnh.processingplus.recipes.chains.infrastructure;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;

import com.gtnh.processingplus.GTNHProcessingPlus;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

/**
 * Ammonia Atmosphere Reactor recipes.
 *
 * <p>The AAR maintains its own reactive NH3 atmosphere, so any Large Chemical Reactor recipe that
 * consumes ammonia can run inside it without the player supplying ammonia. Rather than hand-author
 * each one, we mirror every LCR recipe that takes ammonia as a fluid input into the AAR map with the
 * ammonia stripped out. This runs in loadComplete (last in {@link com.gtnh.processingplus.recipes.PrPlusRecipes}),
 * after all GregTech and addon LCR recipes are registered.
 */
public class AARRecipes {

    public static void init() {
        migrateAmmoniaRecipesToAAR();
    }

    // -------------------------------------------------------------------------
    // Mirror every LCR recipe consuming ammonia into the AAR map, ammonia removed.
    // Ammonia in GT5U is a GAS (Materials.Ammonia.getGas), so the probe must use getGas;
    // getFluid() returns null for a gas-only material and would match nothing.
    // -------------------------------------------------------------------------
    private static void migrateAmmoniaRecipesToAAR() {
        FluidStack ammoniaProbe = Materials.Ammonia.getGas(1);
        if (ammoniaProbe == null) ammoniaProbe = Materials.Ammonia.getFluid(1); // fallback if registered as fluid
        if (ammoniaProbe == null) {
            GTNHProcessingPlus.LOG.warn("AAR ammonia migration: Materials.Ammonia fluid/gas unavailable — skipped.");
            return;
        }

        List<GTRecipe> toAdd = new ArrayList<>();
        for (GTRecipe r : RecipeMaps.multiblockChemicalReactorRecipes.getAllRecipes()) {
            if (r.mFluidInputs == null) continue;
            boolean hasAmmonia = false;
            for (FluidStack fs : r.mFluidInputs) {
                if (fs != null && fs.isFluidEqual(ammoniaProbe)) {
                    hasAmmonia = true;
                    break;
                }
            }
            if (!hasAmmonia) continue;

            GTRecipe copy = r.copy();
            // Strip the ammonia input; keep every other fluid input.
            List<FluidStack> kept = new ArrayList<>();
            for (FluidStack fs : copy.mFluidInputs) {
                if (fs != null && !fs.isFluidEqual(ammoniaProbe)) kept.add(fs);
            }
            copy.mFluidInputs = kept.toArray(new FluidStack[0]);
            toAdd.add(copy);
        }

        for (GTRecipe c : toAdd) {
            GTNHPPRecipeMaps.sAARRecipes.addRecipe(c);
        }
        GTNHProcessingPlus.LOG.info("AAR ammonia migration: added {} recipe(s) from the LCR.", toAdd.size());
    }
}
