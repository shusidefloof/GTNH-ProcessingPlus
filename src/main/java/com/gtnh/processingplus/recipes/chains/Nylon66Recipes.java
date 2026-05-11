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

public class Nylon66Recipes {

    public static void init() {
        step1_PhenolToCyclohexanol();
        step2_CyclohexanolEquilibrium();
        step3_AdipicAcidSynthesis();
        step4_GreenRoute();
        step5_ButadieneRoute();
        step6_PolymerChain();
        step0_HydroxylammoniumSulfateSynthesis();
        step7_CaprolactamRoute();
    }

    // =========================================================
    // 1. Phenol → Cyclohexanol
    // =========================================================
    private static void step1_PhenolToCyclohexanol() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1), GTOreDictUnificator.get(OrePrefixes.dustSmall, "catalystCobaltTitanium", 0))
            .fluidInputs(fluid(Materials.Phenol, 1000), fluid(Materials.Hydrogen, 3000))
            .fluidOutputs(fluid(PrPMaterials.Cyclohexanol, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .metadata(GTRecipeConstants.CHEMPLANT_CASING_TIER, 3)
            .addTo(GTPPRecipeMaps.chemicalPlantRecipes);
    }

    // =========================================================
    // 2. Cyclohexanol → Cyclohexanone (oxidation)
    // =========================================================
    private static void step2_CyclohexanolEquilibrium() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(PrPMaterials.Cyclohexanol, 1000), fluid(Materials.Oxygen, 500))
            .fluidOutputs(fluid("cyclohexanone", 1000), fluid(Materials.Water, 500))
            .duration(160)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 3. Adipic Acid synthesis
    // =========================================================
    private static void step3_AdipicAcidSynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(24), GTOreDictUnificator.get(OrePrefixes.dustSmall, "catalystCobaltTitanium", 0))
            .fluidInputs(fluid("cyclohexanone", 1000), fluid(Materials.NitricAcid, 4000))
            .fluidOutputs(
                fluid(PrPMaterials.AdipicAcid, 2000),
                fluid(Materials.NitrousOxide, 2000),
                fluid(Materials.Water, 2000))
            .duration(150)
            .eut(TierEU.RECIPE_UV)
            .metadata(GTRecipeConstants.CHEMPLANT_CASING_TIER, 6)
            .addTo(GTPPRecipeMaps.chemicalPlantRecipes);
    }

    // =========================================================
    // 4. Green route
    // =========================================================
    private static void step4_GreenRoute() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(fluid(PrPMaterials.Cyclohexene, 1000), fluid("fluid.hydrogenperoxide", 3000))
            .fluidOutputs(fluid(PrPMaterials.AdipicAcid, 1000), fluid(Materials.Water, 3000))
            .duration(300)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 5. Butadiene route
    // =========================================================
    private static void step5_ButadieneRoute() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(5), GTOreDictUnificator.get(OrePrefixes.dustSmall, "catalystCobaltTitanium", 0))
            .fluidInputs(
                fluid(Materials.Butadiene, 2000),
                fluid(Materials.CarbonMonoxide, 2000),
                fluid(Materials.Water, 2000))
            .fluidOutputs(fluid(PrPMaterials.AdipicAcid, 1000))
            .duration(500)
            .eut(TierEU.RECIPE_UV)
            .metadata(GTRecipeConstants.CHEMPLANT_CASING_TIER, 7)
            .addTo(GTPPRecipeMaps.chemicalPlantRecipes);
    }

    // =========================================================
    // 6. Nylon polymerization
    // =========================================================
    private static void step6_PolymerChain() {

        // Adipic Acid + NH3 → Adiponitrile + Water  (ZPM LCR)
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(6))
            .fluidInputs(fluid(PrPMaterials.AdipicAcid, 2000), fluid(Materials.Ammonia, 2000))
            .fluidOutputs(fluid(Materials.Water, 2000))
            .itemOutputs(dust(PrPMaterials.Adiponitrile, 1))
            .duration(350)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTRecipeConstants.UniversalChemical);

        // Adiponitrile + 4 H2 → HMD  (ZPM LCR)
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.Adiponitrile, 1))
            .fluidInputs(fluid(Materials.Hydrogen, 4000))
            .itemOutputs(dust(PrPMaterials.HMD, 1))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTRecipeConstants.UniversalChemical);

        // Adipic Acid + HMD → Nylon-6,6 + Water  (ZPM PCV, 1:1 molar)
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.HMD, 4))
            .fluidInputs(fluid(PrPMaterials.AdipicAcid, 4000))
            .fluidOutputs(fluid(Materials.Water, 4000))
            .itemOutputs(dust(PrPMaterials.Nylon66, 8))
            .duration(600)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);
    }

    // =========================================================
    // 0. Hydroxylammonium Sulfate synthesis (UV LCR)
    // 2 NH3 + H2O2 + H2SO4 → (NH3OH)2SO4 + H2O
    // =========================================================
    private static void step0_HydroxylammoniumSulfateSynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(10))
            .fluidInputs(
                fluid(Materials.Ammonia, 2000),
                fluid("fluid.hydrogenperoxide", 1000),
                fluid(Materials.SulfuricAcid, 1000))
            .itemOutputs(dust(PrPMaterials.HydroxylammoniumSulfate, 1))
            .fluidOutputs(fluid(Materials.Water, 2000))
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================
    // 7. Caprolactam route — third path branching from cyclohexanone
    // =========================================================
    private static void step7_CaprolactamRoute() {

        // A: Cyclohexanone + Hydroxylammonium Sulfate → Cyclohexanone Oxime + H2SO4
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(7), dust(PrPMaterials.HydroxylammoniumSulfate, 2))
            .fluidInputs(fluid("cyclohexanone", 2000))
            .fluidOutputs(fluid(Materials.SulfuricAcid, 1000))
            .itemOutputs(dust(PrPMaterials.CyclohexanoneOxime, 2))
            .duration(300)
            .eut(TierEU.RECIPE_UV)
            .metadata(GTRecipeConstants.CHEMPLANT_CASING_TIER, 5)
            .addTo(GTPPRecipeMaps.chemicalPlantRecipes);

        // B: Cyclohexanone Oxime + H2SO4 → Caprolactam (Beckmann rearrangement)
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(8), dust(PrPMaterials.CyclohexanoneOxime, 1))
            .fluidInputs(fluid(Materials.SulfuricAcid, 500))
            .fluidOutputs(fluid(PrPMaterials.Caprolactam, 1000))
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .metadata(GTRecipeConstants.CHEMPLANT_CASING_TIER, 5)
            .addTo(GTPPRecipeMaps.chemicalPlantRecipes);

        // C: Caprolactam + H2O → Nylon-6,6 (ring-opening polymerization, Chemical Plant)
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(9))
            .fluidInputs(fluid(PrPMaterials.Caprolactam, 4000), fluid(Materials.Water, 500))
            .itemOutputs(dust(PrPMaterials.Nylon66, 4))
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .metadata(GTRecipeConstants.CHEMPLANT_CASING_TIER, 5)
            .addTo(GTPPRecipeMaps.chemicalPlantRecipes);
    }
}
