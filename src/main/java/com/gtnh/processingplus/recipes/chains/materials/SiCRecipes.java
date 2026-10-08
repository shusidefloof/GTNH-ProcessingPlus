package com.gtnh.processingplus.recipes.chains.materials;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeConstants;

public class SiCRecipes {

    public static void init() {
        step1_CarbothermalReduction();
        stepAlt_CVDRoute();
        step2_AcidPurification();
        step3_Sintering();
        step4_Machining();
    }

    // =========================================================
    // 1. SiO2 + 3C → SiC + 2CO (Acheson process, ~2500 K, a heat recipe, not a chemical-reactor one)
    // =========================================================
    private static void step1_CarbothermalReduction() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.SiliconDioxide, 2), dust(Materials.Carbon, 6))
            .circuit(1)
            .fluidInputs(fluid(Materials.Argon, 1000))
            .fluidOutputs(fluid(Materials.CarbonMonoxide, 4000))
            .itemOutputs(dust(PrPMaterials.CrudeSiCPowder, 2))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .metadata(GTRecipeConstants.COIL_HEAT, 2700)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // ALT: CVD route: SiCl4 + CH4 → PurifiedSiC + HCl (HTRF, UV)
    // Higher purity than Acheson; skips crushing and acid wash
    // =========================================================
    private static void stepAlt_CVDRoute() {

        GTValues.RA.stdBuilder()
            .circuit(2)
            .fluidInputs(fluid(Materials.SiliconTetrachloride, 1000), fluid(Materials.Methane, 1000))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 4000))
            .itemOutputs(dust(PrPMaterials.PurifiedSiCPowder, 2))
            .duration(40 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .metadata(GTRecipeConstants.COIL_HEAT, 1800)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // 2. HF purification → Purified SiC
    // =========================================================
    private static void step2_AcidPurification() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.CrudeSiCPowder, 4))
            .fluidInputs(fluid(Materials.HydrofluoricAcid, 500), fluid(Materials.SulfuricAcid, 500))
            .fluidOutputs(fluid(Materials.DilutedSulfuricAcid, 500), fluid(Materials.Water, 500))
            .itemOutputs(dust(PrPMaterials.PurifiedSiCPowder, 4))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // =========================================================
    // 3. Hot pressing (B additive, Ar atmosphere) → Dense SiC ceramic
    // =========================================================
    private static void step3_Sintering() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.PurifiedSiCPowder, 4), dust(Materials.Boron, 1))
            .fluidInputs(fluid(Materials.Argon, 500))
            .itemOutputs(dust(PrPMaterials.DenseSiCCompact, 4))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .metadata(GTRecipeConstants.COIL_HEAT, 2400)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // =========================================================
    // 4. Cutting / machining → plates
    // =========================================================
    private static void step4_Machining() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.DenseSiCCompact, 1), ItemList.Shape_Mold_Plate.get(0))
            .itemOutputs(plate(PrPMaterials.SinteredSiliconCarbide, 2))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.formingPressRecipes);
    }
}
