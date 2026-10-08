package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.GTNHProcessingPlus;
import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;

public class CSCRecipes {

    public static void init() {
        casingRecipe();
        controllerRecipe();
        asuMode();
        co2LiquefactionMode();
        naturalGasFractionation();
        lpgFractionation();
        liquidGasVaporization();
        try {
            nobleGasVaporization();
        } catch (IllegalStateException e) {
            GTNHProcessingPlus.LOG.warn("Skipping CSC noble gas recipes: missing fluid: {}", e.getMessage());
        }
    }

    // TEMP: placeholder hand-craft so the CSC can be built until its real recipe is designed.
    private static void controllerRecipe() {
        GTModHandler.addCraftingRecipe(
            GTNHPPBlocks.CSC.getStackForm(1),
            GTModHandler.RecipeBits.BITS | GTModHandler.RecipeBits.DO_NOT_CHECK_FOR_COLLISIONS,
            new Object[] { "PCP", "MHM", "PEP", 'P', OrePrefixes.plate.get(Materials.StainlessSteel), 'C',
                OrePrefixes.circuit.get(Materials.HV), 'M', ItemList.Electric_Motor_HV, 'H', ItemList.Hull_HV, 'E',
                ItemList.Electric_Pump_HV });
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
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // circuit(1): Air → N₂ + O₂ + Ar via Freon refrigeration. ~500 mB Freon lost per cycle.
    private static void asuMode() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(Materials.Air, 50000), fluid(PrPMaterials.FreonR12, 5000))
            .fluidOutputs(
                fluid(Materials.Nitrogen, 70000),
                fluid(Materials.Oxygen, 20000),
                fluid(PrPMaterials.LiquidArgon, 10000),
                fluid(PrPMaterials.FreonR12, 4500))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);
    }

    // circuit(2): CO₂ → Liquid CO₂ via Freon heat exchangers. ~150 mB Freon lost per cycle.
    private static void co2LiquefactionMode() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(Materials.CarbonDioxide, 10000), fluid(PrPMaterials.FreonR12, 1000))
            .fluidOutputs(fluid(PrPMaterials.LiquidCO2, 10000), fluid(PrPMaterials.FreonR12, 850))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(Materials.Argon, 10000), fluid(PrPMaterials.FreonR12, 1000))
            .fluidOutputs(fluid(PrPMaterials.LiquidArgon, 10000), fluid(PrPMaterials.FreonR12, 850))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);
    }

    // circuit(6): Natural Gas fractional distillation → Methane/Ethane/Propane cuts, roughly by real-world
    // yield (mostly methane, smaller ethane/propane fractions). Heavier cost/duration than the ASU modes
    // since it's separating three products from one feed instead of one.
    private static void naturalGasFractionation() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(6))
            .fluidInputs(fluid(Materials.NaturalGas, 10000), fluid(PrPMaterials.FreonR12, 2000))
            .fluidOutputs(
                fluid(Materials.Methane, 14000),
                fluid(Materials.Ethane, 4000),
                fluid(Materials.Propane, 2000),
                fluid(PrPMaterials.FreonR12, 1700))
            .duration(6 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);
    }

    // circuit(7): LPG fractional distillation → Propane + Butane, the two cuts vanilla GT5's plastics/fuel
    // chains actually consume (unlike LOX/LIN, which nothing downstream needed).
    private static void lpgFractionation() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(7))
            .fluidInputs(fluid(Materials.LPG, 10000), fluid(PrPMaterials.FreonR12, 1500))
            .fluidOutputs(
                fluid(Materials.Propane, 12000),
                fluid(Materials.Butane, 8000),
                fluid(PrPMaterials.FreonR12, 1300))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);
    }

    // circuits(3-5): Noble gas extraction: tiered air volumes, escalating Freon cost
    private static void nobleGasVaporization() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(Materials.Air, 125000), fluid(PrPMaterials.FreonR12, 5000))
            .fluidOutputs(fluid(Materials.Argon, 8400), fluid(PrPMaterials.FreonR12, 4500))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(Materials.Air, 250000), fluid(PrPMaterials.FreonR12, 5000))
            .fluidOutputs(fluid("neon", 4200), fluid(PrPMaterials.FreonR12, 4500))
            .duration(40 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(fluid(Materials.Air, 500000), fluid(PrPMaterials.FreonR12, 5000))
            .fluidOutputs(fluid(Materials.Helium, 2100), fluid(PrPMaterials.FreonR12, 4500))
            .duration(40 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(5))
            .fluidInputs(fluid(Materials.Air, 2000000), fluid(PrPMaterials.FreonR12, 10000))
            .fluidOutputs(fluid("krypton", 1275), fluid(PrPMaterials.FreonR12, 9500))
            .duration(100 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(6))
            .fluidInputs(fluid(Materials.Air, 10000000), fluid(PrPMaterials.FreonR12, 40000))
            .fluidOutputs(fluid("xenon", 750), fluid(PrPMaterials.FreonR12, 38000))
            .duration(400 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);
    }

    // Liquid gas → gas in Fluid Heater (passive warming, no reagents)
    private static void liquidGasVaporization() {
        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.LiquidArgon, 1000))
            .fluidOutputs(fluid(Materials.Argon, 1000))
            .duration(2 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.fluidHeaterRecipes);

        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.LiquidCO2, 1000))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 1000))
            .duration(2 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.fluidHeaterRecipes);
    }
}
