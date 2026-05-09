package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PPMaterials;

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
            .fluidOutputs(fluid(PPMaterials.Cyclohexanol, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .metadata(GTRecipeConstants.CHEMPLANT_CASING_TIER, 3)
            .addTo(GTPPRecipeMaps.chemicalPlantRecipes);
    }

    // =========================================================
    // 2. Cyclohexanol → Cyclohexanone (equilibrium)
    // =========================================================
    private static void step2_CyclohexanolEquilibrium() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(PPMaterials.Cyclohexanol, 1000), fluid(Materials.Oxygen, 1000))
            .fluidOutputs(fluid("cyclohexanone", 500), fluid(PPMaterials.Cyclohexanol, 500))
            .duration(120)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 3. Adipic Acid synthesis
    // =========================================================
    private static void step3_AdipicAcidSynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(24), GTOreDictUnificator.get(OrePrefixes.dustSmall, "catalystCobaltTitanium", 0))
            .fluidInputs(fluid(PPMaterials.Cyclohexanol, 1000), fluid(Materials.NitricAcid, 4000))
            .fluidOutputs(
                fluid(PPMaterials.AdipicAcid, 2000),
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
            .fluidInputs(fluid(PPMaterials.Cyclohexene, 1000), fluid("fluid.hydrogenperoxide", 3000))
            .fluidOutputs(fluid(PPMaterials.AdipicAcid, 1000), fluid(Materials.Water, 3000))
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
            .fluidOutputs(fluid(PPMaterials.AdipicAcid, 1000))
            .duration(500)
            .eut(TierEU.RECIPE_UV)
            .metadata(GTRecipeConstants.CHEMPLANT_CASING_TIER, 7)
            .addTo(GTPPRecipeMaps.chemicalPlantRecipes);
    }

    // =========================================================
    // 6. Nylon polymerization
    // =========================================================
    private static void step6_PolymerChain() {

        // GTValues.RA.stdBuilder()
        // .itemInputs(GTNHPPItemList.Adiponitrile.get(2))
        // .fluidInputs(
        // fluid(Materials.Ammonia, 2000),
        // fluid(PPMaterials.AdipicAcid, 4000)
        // )
        // .fluidOutputs(
        // fluid(Materials.Water, 2000)
        // )
        // .duration(400)
        // .eut(TierEU.RECIPE_ZPM)
        // .addTo(GTNHPPRecipeMaps.sAARRecipes);
        //
        // GTValues.RA.stdBuilder()
        // .itemInputs(GTNHPPItemList.Adiponitrile.get(1))
        // .fluidInputs(fluid(Materials.Hydrogen, 3000))
        // .itemOutputs(GTNHPPItemList.HMD.get(1))
        // .duration(300)
        // .eut(TierEU.RECIPE_ZPM)
        // .addTo(GTRecipeConstants.UniversalChemical);
        //
        // GTValues.RA.stdBuilder()
        // .itemInputs(GTNHPPItemList.HMD.get(4))
        // .fluidInputs(fluid(PPMaterials.AdipicAcid, 4000))
        // .fluidOutputs(fluid(Materials.Water, 4000))
        // .itemOutputs(ingot(PPMaterials.Nylon66, 8))
        // .duration(1600)
        // .eut(TierEU.RECIPE_ZPM)
        // .addTo(GTNHPPRecipeMaps.sPCVRecipes);
    }
}
