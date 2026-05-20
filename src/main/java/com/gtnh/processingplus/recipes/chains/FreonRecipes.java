package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;

public class FreonRecipes {

    public static void init() {
        step1_CarbonTetrachloride();
        step2_FreonSynthesis();
    }

    // =========================================================
    // 1. CHCl₃ + Cl₂ → CCl₄ + HCl (photochlorination of chloroform)
    // =========================================================
    private static void step1_CarbonTetrachloride() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(Materials.Chloroform, 1000), fluid(Materials.Chlorine, 1000))
            .fluidOutputs(
                fluid(PrPMaterials.CarbonTetrachloride, 1000),
                fluid(Materials.HydrochloricAcid, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 2. CCl₄ + 2 HF → CF₂Cl₂ + 2 HCl (Swarts fluorination)
    // Requires HV Large Chemical Reactor — HF at pressure
    // =========================================================
    private static void step2_FreonSynthesis() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(
                fluid(PrPMaterials.CarbonTetrachloride, 1000),
                fluid(Materials.HydrofluoricAcid, 2000))
            .fluidOutputs(
                fluid(PrPMaterials.FreonR12, 1000),
                fluid(Materials.HydrochloricAcid, 2000))
            .duration(400)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }
}
