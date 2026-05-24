package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;

public class CSCRecipes {

    public static void init() {
        casingRecipe();
        asuMode();
        co2LiquefactionMode();
        liquidGasVaporization();
        try {
            nobleGasVaporization();
        } catch (IllegalStateException e) {
            System.err.println("[GTNHPP] Skipping CSC noble gas recipes — missing fluid: " + e.getMessage());
        }
    }

    private static void casingRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.StainlessSteel, 1),
                plate(Materials.StainlessSteel, 4),
                plate(Materials.Invar, 2),
                plate(Materials.Polytetrafluoroethylene, 2),
                plate(Materials.Copper, 1),
                circuit(10))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.CSC_CASING))
            .duration(400)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // circuit(1): Air → N₂ + O₂ + Ar via Freon refrigeration. ~500 mB Freon lost per cycle.
    private static void asuMode() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(Materials.Air, 50000), fluid(PrPMaterials.FreonR12, 5000))
            .fluidOutputs(
                fluid(Materials.Nitrogen, 35000),
                fluid(Materials.Oxygen, 10000),
                fluid(PrPMaterials.LiquidArgon, 5000),
                fluid(PrPMaterials.FreonR12, 4500))
            .duration(4000)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);
    }

    // circuit(2): CO₂ → Liquid CO₂ via Freon heat exchangers. ~150 mB Freon lost per cycle.
    private static void co2LiquefactionMode() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(Materials.CarbonDioxide, 10000), fluid(PrPMaterials.FreonR12, 1000))
            .fluidOutputs(fluid(PrPMaterials.LiquidCO2, 9000), fluid(PrPMaterials.FreonR12, 850))
            .duration(600)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);
    }

    // circuits(3-5): Noble gas extraction — tiered air volumes, escalating Freon cost
    private static void nobleGasVaporization() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(Materials.Air, 500000), fluid(PrPMaterials.FreonR12, 5000))
            .fluidOutputs(fluid("neon", 700), fluid(PrPMaterials.FreonR12, 4500))
            .duration(8000)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(fluid(Materials.Air, 2000000), fluid(PrPMaterials.FreonR12, 10000))
            .fluidOutputs(fluid("krypton", 425), fluid(PrPMaterials.FreonR12, 9500))
            .duration(20000)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(5))
            .fluidInputs(fluid(Materials.Air, 10000000), fluid(PrPMaterials.FreonR12, 40000))
            .fluidOutputs(fluid("xenon", 250), fluid(PrPMaterials.FreonR12, 38000))
            .duration(80000)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);
    }

    // Liquid gas → gas in Fluid Heater (passive warming, no reagents)
    private static void liquidGasVaporization() {
        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.LiquidArgon, 1000))
            .fluidOutputs(fluid(Materials.Argon, 1000))
            .duration(40)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.fluidHeaterRecipes);

        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.LiquidCO2, 1000))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 1000))
            .duration(40)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.fluidHeaterRecipes);
    }
}
