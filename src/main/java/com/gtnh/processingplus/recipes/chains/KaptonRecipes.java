package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

public class KaptonRecipes {

    public static void init() {
        step1_PMDASynthesis();
        step2_ODASynthesis();
        step3_PolyamicAcidSolution();
        step4_PAAConcentration();
        step5_FilmCasting();
        step6_ImidizationToKapton();
    }

    // =========================================================
    // 1. PMDA synthesis (Phthalic acid oxidation)
    // =========================================================
    private static void step1_PMDASynthesis() {

        // GTValues.RA.stdBuilder()
        // .itemInputs(
        // circuit(3),
        // GTOreDictUnificator.get(OrePrefixes.dust, Materials.PhthalicAcid, 2)
        // )
        // .fluidInputs(
        // fluid(Materials.Oxygen, 6000)
        // )
        // .fluidOutputs(
        // fluid(Materials.CarbonDioxide, 2000),
        // fluid(Materials.Water, 1000)
        // )
        // .itemOutputs(GTNHPPItemList.PMDA.get(2))
        // .duration(600)
        // .eut(TierEU.RECIPE_LuV)
        // .metadata(GTRecipeConstants.CHEMPLANT_CASING_TIER, 5)
        // .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================
    // 2. ODA synthesis (benzene nitration route)
    // =========================================================
    private static void step2_ODASynthesis() {

        // GTValues.RA.stdBuilder()
        // .itemInputs(
        // circuit(4)
        // )
        // .fluidInputs(
        // fluid(Materials.Benzene, 2000),
        // fluid(Materials.NitricAcid, 1000)
        // )
        // .fluidOutputs(
        // fluid(Materials.Water, 1000)
        // )
        // .itemOutputs(GTNHPPItemList.ODA.get(1))
        // .duration(600)
        // .eut(TierEU.RECIPE_UV)
        // .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 3. Polyamic Acid solution formation (PMDA + ODA)
    // =========================================================
    private static void step3_PolyamicAcidSolution() {

        // GTValues.RA.stdBuilder()
        // .itemInputs(
        // GTNHPPItemList.PMDA.get(2),
        // GTNHPPItemList.ODA.get(2)
        // )
        // .fluidInputs(
        // fluid(Materials.NMethylIIPyrrolidone, 1000)
        // )
        // .fluidOutputs(
        // fluid(PPMaterials.PAASolution, 2000)
        // )
        // .duration(800)
        // .eut(TierEU.RECIPE_UV)
        // .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================
    // 4. Concentration / solvent recovery
    // =========================================================
    private static void step4_PAAConcentration() {

        // GTValues.RA.stdBuilder()
        // .fluidInputs(
        // fluid(PPMaterials.PAASolution, 2000)
        // )
        // .itemOutputs(GTNHPPItemList.ConcentratedPAA.get(2))
        // .fluidOutputs(
        // fluid(Materials.NMethylIIPyrrolidone, 800)
        // )
        // .duration(600)
        // .eut(TierEU.RECIPE_UV)
        // .addTo(RecipeMaps.distillationTowerRecipes);
    }

    // =========================================================
    // 5. Film casting (polyamic acid film formation)
    // =========================================================
    private static void step5_FilmCasting() {

        // GTValues.RA.stdBuilder()
        // .itemInputs(
        // GTNHPPItemList.ConcentratedPAA.get(2)
        // )
        // .fluidInputs(
        // fluid(Materials.Ethanol, 200)
        // )
        // .itemOutputs(GTNHPPItemList.PolyamicAcidFilm.get(4))
        // .duration(800)
        // .eut(TierEU.RECIPE_UV)
        // .addTo(GTNHPPRecipeMaps.sPFCCastingRecipes);
    }

    // =========================================================
    // 6. Final imidization → Kapton
    // =========================================================
    private static void step6_ImidizationToKapton() {

        // GTValues.RA.stdBuilder()
        // .itemInputs(
        // GTNHPPItemList.PolyamicAcidFilm.get(2)
        // )
        // .fluidOutputs(
        // fluid(Materials.Water, 1000)
        // )
        // .itemOutputs(plate(PPMaterials.Kapton, 2))
        // .duration(1200)
        // .eut(TierEU.RECIPE_UV)
        // .addTo(GTNHPPRecipeMaps.sPFCImidizationRecipes);
    }
}
