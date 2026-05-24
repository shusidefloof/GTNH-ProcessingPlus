package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;

public class BOFRecipes {

    public static void init() {
        casingRecipe();
        limedConversion();
        limestoneConversion();
        dolomiteConversion();
    }

    // =========================================================
    // Casing — steel frame, stainless shell, borosilicate viewing windows
    // =========================================================
    private static void casingRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1),
                plate(Materials.StainlessSteel, 4),
                plate(Materials.BorosilicateGlass, 2),
                plate(Materials.Copper, 1),
                circuit(11))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 4, BlockGTNHPPCasings.BOF_CASING))
            .duration(400)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // =========================================================
    // Mode 2 — Calcium-fluxed blowing (circuit 2)
    // Pure calcium flux. Most efficient — no carbonate overhead.
    // =========================================================
    private static void limedConversion() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Iron, 8), dust(Materials.Calcium, 2), circuit(2))
            .fluidInputs(fluid(Materials.Oxygen, 2000))
            .itemOutputs(ingot(Materials.Steel, 10))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 800))
            .duration(280)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sBOFRecipes);
    }

    // =========================================================
    // Mode 2 — Limestone-fluxed blowing (circuit 2)
    // Calcite (CaCO3) decomposes in the furnace to CaO + CO2.
    // Needs more flux than pure calcium; extra CO2 from decomposition.
    // =========================================================
    private static void limestoneConversion() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Iron, 8), dust(Materials.Calcite, 4), circuit(2))
            .fluidInputs(fluid(Materials.Oxygen, 2000))
            .itemOutputs(ingot(Materials.Steel, 10))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 1600))
            .duration(320)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sBOFRecipes);
    }

    // =========================================================
    // Mode 2 — Dolomite-fluxed blowing (circuit 2)
    // Dolomite (CaMg(CO3)2) is half calcium by mole — needs twice as much.
    // The MgO residue protects the furnace lining in real BOF practice.
    // Most input, most CO2, but dolomite is the cheapest source.
    // =========================================================
    private static void dolomiteConversion() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Iron, 8), dust(Materials.Dolomite, 8), circuit(2))
            .fluidInputs(fluid(Materials.Oxygen, 2000))
            .itemOutputs(ingot(Materials.Steel, 10))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 1600), fluid(Materials.CarbonMonoxide, 400))
            .duration(360)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sBOFRecipes);
    }
}
