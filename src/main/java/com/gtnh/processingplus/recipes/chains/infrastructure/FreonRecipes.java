package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeConstants;

public class FreonRecipes {

    public static void init() {
        step1_CarbonTetrachloride();
        step2_FreonSynthesis();
    }

    private static void step1_CarbonTetrachloride() {
        GTValues.RA.stdBuilder()
            // Chlorine + HCl as cells so it fits the single-block CR; UniversalChemical makes the
            // all-fluid LCR copy automatically. (CHCl3 + Cl2 -> CCl4 + HCl; cell carries Cl2 -> HCl.)
            .itemInputs(Materials.Chlorine.getCells(1), circuit(1))
            .fluidInputs(fluid(Materials.Chloroform, 1000))
            .itemOutputs(Materials.HydrochloricAcid.getCells(1))
            .fluidOutputs(fluid(PrPMaterials.CarbonTetrachloride, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    private static void step2_FreonSynthesis() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(PrPMaterials.CarbonTetrachloride, 1000), fluid(Materials.HydrofluoricAcid, 2000))
            .fluidOutputs(fluid(PrPMaterials.FreonR12, 1000), fluid(Materials.HydrochloricAcid, 2000))
            .duration(400)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }
}
