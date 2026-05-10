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
            .duration(350)
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
                fluid(Materials.Butene, 2000),
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
            .duration(1600)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);
    }
}
