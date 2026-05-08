package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.GTNHPPRecipeHelper.*;

import com.gtnh.processingplus.items.GTNHPPItemList;
import com.gtnh.processingplus.materials.GTNHPPMaterials;
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
            .itemInputs(
                circuit(1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, "catalystCobaltTitanium", 0)
            )
            .fluidInputs(
                fluid(Materials.Phenol, 1000),
                fluid(Materials.Hydrogen, 3000)
            )
            .fluidOutputs(
                GTNHPPMaterials.Cyclohexanol.getFluid(1000)
            )
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
            .fluidInputs(
                GTNHPPMaterials.Cyclohexanol.getFluid(1000),
                fluid(Materials.Oxygen, 1000)
            )
            .fluidOutputs(
                fluid("cyclohexanone", 500),
                GTNHPPMaterials.Cyclohexanol.getFluid(500)
            )
            .duration(120)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 3. Adipic Acid synthesis
    // =========================================================
    private static void step3_AdipicAcidSynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(
                circuit(24),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, "catalystCobaltTitanium", 0)
            )
            .fluidInputs(
                GTNHPPMaterials.Cyclohexanol.getFluid(1000),
                fluid(Materials.NitricAcid, 4000)
            )
            .fluidOutputs(
                GTNHPPMaterials.AdipicAcid.getFluid(2000),
                fluid(Materials.NitrousOxide, 2000),
                fluid(Materials.Water, 2000)
            )
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
            .fluidInputs(
                GTNHPPMaterials.Cyclohexene.getFluid(1000),
                fluid("fluid.hydrogenperoxide", 3000)
            )
            .fluidOutputs(
                GTNHPPMaterials.AdipicAcid.getFluid(1000),
                fluid(Materials.Water, 3000)
            )
            .duration(300)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 5. Butadiene route
    // =========================================================
    private static void step5_ButadieneRoute() {

        GTValues.RA.stdBuilder()
            .itemInputs(
                circuit(5),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, "catalystCobaltTitanium", 0)
            )
            .fluidInputs(
                fluid(Materials.Butene, 2000),
                fluid(Materials.CarbonMonoxide, 2000),
                fluid(Materials.Water, 2000)
            )
            .fluidOutputs(
                GTNHPPMaterials.AdipicAcid.getFluid(1000)
            )
            .duration(500)
            .eut(TierEU.RECIPE_UV)
            .metadata(GTRecipeConstants.CHEMPLANT_CASING_TIER, 7)
            .addTo(GTPPRecipeMaps.chemicalPlantRecipes);
    }

    // =========================================================
    // 6. Nylon polymerization
    // =========================================================
    private static void step6_PolymerChain() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPItemList.Adiponitrile.get(2))
            .fluidInputs(
                fluid(Materials.Ammonia, 2000),
                GTNHPPMaterials.AdipicAcid.getFluid(4000)
            )
            .fluidOutputs(
                fluid(Materials.Water, 2000)
            )
            .duration(400)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sAARRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPItemList.Adiponitrile.get(1))
            .fluidInputs(fluid(Materials.Hydrogen, 4000))
            .itemOutputs(GTNHPPItemList.HMD.get(1))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTRecipeConstants.UniversalChemical);

        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPItemList.HMD.get(4))
            .fluidInputs(GTNHPPMaterials.AdipicAcid.getFluid(4000))
            .fluidOutputs(fluid(Materials.Water, 4000))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.ingot, GTNHPPMaterials.Nylon66, 8)
            )
            .duration(1600)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);
    }
}
