package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeConstants;

public class KaptonRecipes {

    public static void init() {
        step1_PMDASynthesis();
        step2_DiphenylEtherSynthesis();
        step3_ODASynthesis();
        step4_PolyamicAcidSolution();
        step5_PAAConcentration();
        step6_FilmCasting();
        step7_ImidizationToKapton();
    }

    // =========================================================
    // 1. PMDA synthesis — phthalic acid oxidative dehydration (LuV hook)
    // PhthalicAcid + O2 → PMDA + CO2 + H2O
    // =========================================================
    private static void step1_PMDASynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.PhthalicAcid, 2), circuit(3))
            .fluidInputs(fluid(Materials.Oxygen, 6000))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 2000), fluid(Materials.Water, 1000))
            .itemOutputs(dust(PrPMaterials.PMDA, 2))
            .duration(600)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================
    // 2. Diphenyl Ether synthesis — phenol dehydration (LuV)
    // 2 Phenol → Diphenyl Ether + H2O
    // Gives the aryl ether backbone needed for ODA
    // =========================================================
    private static void step2_DiphenylEtherSynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(5))
            .fluidInputs(fluid(Materials.Phenol, 2000), fluid(Materials.SulfuricAcid, 200))
            .fluidOutputs(fluid(PrPMaterials.DiphenylEther, 1000), fluid(Materials.Water, 1000))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================
    // 3. ODA synthesis — nitration + reduction (UV)
    // DiphenylEther + 2 HNO3 + 4 H2 → ODA + 4 H2O
    // Nitration of the ether, then H2 reduction of both nitro groups to amines
    // =========================================================
    private static void step3_ODASynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(6))
            .fluidInputs(
                fluid(PrPMaterials.DiphenylEther, 1000),
                fluid(Materials.NitricAcid, 2000),
                fluid(Materials.Hydrogen, 4000))
            .fluidOutputs(fluid(Materials.Water, 4000))
            .itemOutputs(dust(PrPMaterials.ODA, 1))
            .duration(800)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================
    // 4. Polyamic Acid solution — PMDA + ODA in NMP (UV)
    // Nitrogen blanket prevents oxidation during polycondensation
    // =========================================================
    private static void step4_PolyamicAcidSolution() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.PMDA, 2), dust(PrPMaterials.ODA, 2))
            .fluidInputs(fluid(Materials.NMethylIIPyrrolidone, 1000), fluid(Materials.Nitrogen, 1000))
            .fluidOutputs(fluid(PrPMaterials.PAASolution, 2000))
            .duration(800)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================
    // 5. Solvent recovery — distillation strips NMP from PAA (UV)
    // Recovered NMP loops back to step 4
    // =========================================================
    private static void step5_PAAConcentration() {

        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.PAASolution, 2000))
            .itemOutputs(dust(PrPMaterials.ConcentratedPAA, 2))
            .fluidOutputs(fluid(Materials.NMethylIIPyrrolidone, 800))
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.distillationTowerRecipes);
    }

    // =========================================================
    // 6. Film casting — PAA cast under nitrogen blanket (UV, PFC)
    // Produces the uncured polyamic acid film before imidization
    // =========================================================
    private static void step6_FilmCasting() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.ConcentratedPAA, 2))
            .fluidInputs(fluid(Materials.Nitrogen, 500))
            .itemOutputs(dust(PrPMaterials.PolyamicAcidFilm, 4))
            .duration(800)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sPFCCastingRecipes);
    }

    // =========================================================
    // 7. Thermal imidization → Kapton plate (UV, PFC imidize)
    // Water released as the imide rings close at 300°C+
    // =========================================================
    private static void step7_ImidizationToKapton() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.PolyamicAcidFilm, 2))
            .fluidOutputs(fluid(Materials.Water, 1000))
            .itemOutputs(plate(PrPMaterials.Kapton, 2))
            .duration(1200)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sPFCImidizationRecipes);
    }
}
