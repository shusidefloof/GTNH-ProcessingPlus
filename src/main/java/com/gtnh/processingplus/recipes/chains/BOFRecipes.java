package com.gtnh.processingplus.recipes.chains;

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
        standardConversion();
        limedConversion();
        bulkConversion();
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
    // Mode 1 — Standard oxygen blowing (circuit 1)
    // Iron + LOX → Steel + CO₂
    // Same yield as EBF but uses liquid oxygen; faster cycle time.
    // =========================================================
    private static void standardConversion() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Iron, 8), circuit(1))
            .fluidInputs(fluid(Materials.LiquidOxygen, 2000))
            .itemOutputs(ingot(Materials.Steel, 8))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 1000))
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sBOFRecipes);
    }

    // =========================================================
    // Mode 2 — Calcium-fluxed blowing (circuit 2)
    // Calcium reacts with sulfur/phosphorus impurities in the melt,
    // allowing a cleaner high-carbon conversion — +2 extra steel per batch.
    // =========================================================
    private static void limedConversion() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Iron, 8), dust(Materials.Calcium, 2), circuit(2))
            .fluidInputs(fluid(Materials.LiquidOxygen, 2000))
            .itemOutputs(ingot(Materials.Steel, 10))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 800))
            .duration(280)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sBOFRecipes);
    }

    // =========================================================
    // Mode 3 — Bulk throughput (circuit 3)
    // Double-batch mode: scales up without additional overhead.
    // Useful for mass-producing steel late in HV.
    // =========================================================
    private static void bulkConversion() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Iron, 16), circuit(3))
            .fluidInputs(fluid(Materials.LiquidOxygen, 4000))
            .itemOutputs(ingot(Materials.Steel, 16))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 2000))
            .duration(500)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sBOFRecipes);
    }
}
