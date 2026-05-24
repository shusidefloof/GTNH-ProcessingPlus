package com.gtnh.processingplus.recipes.chains.materials;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;

public class HBNRecipes {

    public static void init() {
        step2_CarbothermalReduction();
        step3_Nitriding();
        stepAlt_BCl3Shortcut();
        step4_PowderBlending();
        step5_Sintering();
        step6_WasteRecovery();
    }

    // =========================================================
    // 2. B2O3 → Boron Carbide (HTRF)
    // =========================================================
    private static void step2_CarbothermalReduction() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get("dustBoronTrioxide", 4), dust(Materials.Carbon, 9))
            .fluidOutputs(fluid(Materials.CarbonMonoxide, 2000), fluid(Materials.CarbonDioxide, 5000))
            .itemOutputs(dust(PrPMaterials.BoronCarbide, 2))
            .duration(600)
            .eut((int) (TierEU.RECIPE_LuV * 0.75))
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // 3. Boron Carbide → Crude hBN (nitriding)
    // =========================================================
    private static void step3_Nitriding() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.BoronCarbide, 4))
            .fluidInputs(fluid(Materials.Ammonia, 3000))
            .fluidOutputs(fluid(Materials.Methane, 1000), fluid(Materials.CarbonMonoxide, 500))
            .itemOutputs(dust(PrPMaterials.CrudeHBN, 2), dust(PrPMaterials.BNitrideWaste, 4))
            .duration(240)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sAARRecipes);
    }

    // =========================================================
    // ALT: BCl3 shortcut — B2O3 + Cl2 + HF + NH3 → CrudeHBN + HCl (UV LCR)
    // Skips BoronCarbide intermediate; HF flux gates behind fluorine infrastructure.
    // Lower B2O3 efficiency than main route (1:1 vs 2:4) — sidepath, not shortcut.
    // =========================================================
    private static void stepAlt_BCl3Shortcut() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get("dustBoronTrioxide", 2))
            .fluidInputs(
                fluid(Materials.Chlorine, 3000),
                fluid(Materials.HydrofluoricAcid, 2000),
                fluid(Materials.Ammonia, 2000))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 3000), fluid(Materials.Water, 1000))
            .itemOutputs(dust(PrPMaterials.CrudeHBN, 1))
            .duration(1200)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 4. Powder blending (dopants / sinter aid)
    // =========================================================
    private static void step4_PowderBlending() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.CrudeHBN, 4), dust(Materials.Yttrium, 16))
            .fluidInputs(fluid(Materials.Nitrogen, 16000), fluid(Materials.Argon, 8000))
            .itemOutputs(dust(PrPMaterials.HBNPowderBlend, 8))
            .fluidOutputs(fluid(Materials.NitricOxide, 3000), fluid(Materials.Oxygen, 1500))
            .duration(480)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 5. Hot pressing → Dense hBN ceramic
    // =========================================================
    private static void step5_Sintering() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.HBNPowderBlend, 4 * 8))
            .fluidInputs(
                fluid(Materials.Nitrogen, 16000 * 8),
                fluid(Materials.Argon, 4000 * 8),
                fluid("oganesson", 288))
            .itemOutputs(dust(PrPMaterials.HexagonalBoronNitride, 2 * 8))
            .duration(600)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // =========================================================
    // 6. BN Nitride Waste recovery (HTRF)
    // Impure BN byproduct from nitriding re-annealed under N₂.
    // 4 waste → 1 CrudeHBN; low yield reflects residual C/O contamination.
    // =========================================================
    private static void step6_WasteRecovery() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.BNitrideWaste, 4), circuit(1))
            .fluidInputs(fluid(Materials.Nitrogen, 2000))
            .itemOutputs(dust(PrPMaterials.CrudeHBN, 1))
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }
}
