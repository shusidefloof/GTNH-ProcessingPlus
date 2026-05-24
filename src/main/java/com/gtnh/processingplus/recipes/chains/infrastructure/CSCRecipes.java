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
    }

    // =========================================================
    // Casing — stainless frame backbone, invar plates, PTFE liner, copper heat exchange
    // =========================================================
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

    // =========================================================
    // ASU mode — circuit(1)
    // Air is cooled in stages by Freon refrigeration cycle.
    // N₂ boils off first (-196°C), then Ar (-186°C), then O₂ (-183°C).
    // Freon partially recovers — ~500 mB lost per cycle to leakage.
    // =========================================================
    private static void asuMode() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(Materials.Air, 50000), fluid(PrPMaterials.FreonR12, 1000))
            .fluidOutputs(
                fluid(Materials.Nitrogen, 75000),
                fluid(Materials.Oxygen, 10000),
                fluid(PrPMaterials.LiquidArgon, 5000),
                fluid(PrPMaterials.FreonR12, 4500)) // 500 mB consumed per cycle
            .duration(4000)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);
    }

    // =========================================================
    // CO₂ liquefaction mode — circuit(2)
    // CO₂ compressed and cooled through Freon heat exchangers.
    // Less deep-cold than ASU — lower EU, less Freon loss.
    // =========================================================
    private static void co2LiquefactionMode() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(Materials.CarbonDioxide, 10000), fluid(PrPMaterials.FreonR12, 1000))
            .fluidOutputs(fluid(PrPMaterials.LiquidCO2, 9000), fluid(PrPMaterials.FreonR12, 850)) // 150 mB consumed per
                                                                                                  // cycle
            .duration(600)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);
    }

    // =========================================================
    // Liquid gas → gas (Fluid Heater — passive warming, no reagents)
    // =========================================================
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
