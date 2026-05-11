package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeConstants;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class KaptonRecipes {

    public static void init() {
        step1_PMDASynthesis();
        step2_DiphenylEtherSynthesis();
        step3_ODASynthesis();
        step4_PolyamicAcidSolution();
        step5_PAAConcentration();
        step6_FilmCasting();
        step7_ImidizationToKapton();
        stepPrereq_TriethylamineSynthesis();
        stepPrereq_KeteneSynthesis();
        stepAlt_AceticAnhydrideSynthesis();
        stepAlt_ChemicalImidization();
    }

    // =========================================================
    // 1. PMDA synthesis — naphthalene catalytic oxidation (LuV Chemical Plant)
    // 2 C10H8 + 9 O2 → C10H2O6 + 4 CO2 + 2 H2O
    // =========================================================
    private static void step1_PMDASynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3), GTOreDictUnificator.get(OrePrefixes.dustSmall, "catalystVanadiumPalladium", 0))
            .fluidInputs(fluid("fluid.naphthalene", 2000), fluid(Materials.Oxygen, 9000))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 4000), fluid(Materials.Water, 2000))
            .itemOutputs(dust(PrPMaterials.PMDA, 2))
            .duration(600)
            .eut(TierEU.RECIPE_LuV)
            .metadata(GTRecipeConstants.CHEMPLANT_CASING_TIER, 5)
            .addTo(GTPPRecipeMaps.chemicalPlantRecipes);
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
            .duration(400)
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
            .duration(400)
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
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sPFCImidizationRecipes);
    }

    // =========================================================
    // PREREQ: Triethylamine synthesis — Leuckart ethylation (UV LCR)
    // 3 Ethanol + NH3 → N(C2H5)3 + 3 H2O
    // =========================================================
    private static void stepPrereq_TriethylamineSynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(Materials.Ethanol, 3000), fluid(Materials.Ammonia, 1000))
            .fluidOutputs(fluid(PrPMaterials.Triethylamine, 1000), fluid(Materials.Water, 3000))
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================
    // PREREQ: Ketene synthesis — acetic acid pyrolysis (UV HTRF)
    // CH3COOH → CH2=C=O + H2O
    // =========================================================
    private static void stepPrereq_KeteneSynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(Materials.AceticAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.Ketene, 1000), fluid(Materials.Water, 1000))
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // ALT: Acetic Anhydride synthesis (LuV LCR prerequisite)
    // AceticAcid + Ketene → AceticAnhydride + Water
    // =========================================================
    private static void stepAlt_AceticAnhydrideSynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(5))
            .fluidInputs(fluid(Materials.AceticAcid, 2000), fluid(PrPMaterials.Ketene, 1000))
            .fluidOutputs(fluid(PrPMaterials.AceticAnhydride, 1000), fluid(Materials.Water, 500))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================
    // ALT: Chemical imidization → Kapton plate (UV LCR)
    // Faster than thermal; consumes AceticAnhydride + Triethylamine
    // =========================================================
    private static void stepAlt_ChemicalImidization() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.PolyamicAcidFilm, 2))
            .fluidInputs(fluid(PrPMaterials.AceticAnhydride, 500), fluid(PrPMaterials.Triethylamine, 200))
            .fluidOutputs(fluid(Materials.AceticAcid, 500))
            .itemOutputs(plate(PrPMaterials.Kapton, 2))
            .duration(800)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }
}
