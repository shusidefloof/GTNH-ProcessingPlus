package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;

public class AerogelRecipes {

    public static void init() {
        step0_TrimethylsilaneSynthesis();
        stepAlt_TrimethylchlorosilaneSynthesis();
        step1_TEOSSynthesis();
        step2_SolGelHydrolysis();
        step3_Aging();
        step4_SolventExchange();
        step5_SupercriticalDrying();
        stepAlt_AmbientPressureDrying();
    }

    // =========================================================
    // 0. Trimethylsilane synthesis — SiCl4 + 3 CH4 + H2 → (CH3)3SiH + 4 HCl (UV HTRF)
    // High-temp hydrocarbon substitution on silicon tetrachloride
    // =========================================================
    private static void step0_TrimethylsilaneSynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(
                fluid(Materials.SiliconTetrachloride, 1000),
                fluid(Materials.Methane, 3000),
                fluid(Materials.Hydrogen, 1000))
            .fluidOutputs(fluid(PrPMaterials.Trimethylsilane, 1000), fluid(Materials.HydrochloricAcid, 4000))
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // ALT: Trimethylchlorosilane synthesis — (CH3)3SiH + Cl2 → TMCS + HCl (UV LCR)
    // =========================================================
    private static void stepAlt_TrimethylchlorosilaneSynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(5))
            .fluidInputs(fluid(PrPMaterials.Trimethylsilane, 1000), fluid(Materials.Chlorine, 1000))
            .fluidOutputs(fluid(PrPMaterials.Trimethylchlorosilane, 1000), fluid(Materials.HydrochloricAcid, 1000))
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 1. SiCl4 + Ethanol → TEOS
    // =========================================================
    private static void step1_TEOSSynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(6))
            .fluidInputs(fluid(Materials.SiliconTetrachloride, 1000), fluid(Materials.Ethanol, 4000))
            .fluidOutputs(fluid(PrPMaterials.TEOS, 1000), fluid(Materials.HydrochloricAcid, 4000))
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 2. TEOS → Wet Silica Gel (Sol-Gel)
    // =========================================================
    private static void step2_SolGelHydrolysis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(7))
            .fluidInputs(
                fluid(PrPMaterials.TEOS, 1000),
                fluid(Materials.Water, 5000),
                fluid(Materials.HydrofluoricAcid, 100))
            .itemOutputs(dust(PrPMaterials.WetSilicaGel, 2))
            .fluidOutputs(fluid(Materials.Ethanol, 4000))
            .duration(800)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 3. Aging
    // =========================================================
    private static void step3_Aging() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.WetSilicaGel, 4))
            .fluidInputs(fluid(Materials.Water, 1000))
            .itemOutputs(dust(PrPMaterials.AgedSilicaGel, 4))
            .duration(1600)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sSCDRecipes);
    }

    // =========================================================
    // 4. Solvent Exchange
    // =========================================================
    private static void step4_SolventExchange() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.AgedSilicaGel, 4))
            .fluidInputs(fluid(Materials.Ethanol, 4000))
            .fluidOutputs(fluid(Materials.Water, 3000))
            .itemOutputs(dust(PrPMaterials.EthanolSaturatedGel, 4))
            .duration(2400)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sSCDRecipes);
    }

    // =========================================================
    // 5. Supercritical Drying → Aerogel
    // =========================================================
    private static void step5_SupercriticalDrying() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.EthanolSaturatedGel, 4))
            .fluidInputs(fluid(Materials.Ethanol, 2000))
            .fluidOutputs(fluid(Materials.Ethanol, 1800))
            .itemOutputs(plate(PrPMaterials.SilicaAerogel, 2))
            .duration(3200)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sSCDRecipes);
    }

    // =========================================================
    // ALT: Ambient pressure drying via TMCS surface passivation (UHV LCR)
    // No Supercritical Dryer needed; half aerogel yield
    // =========================================================
    private static void stepAlt_AmbientPressureDrying() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.EthanolSaturatedGel, 4))
            .fluidInputs(fluid(PrPMaterials.Trimethylchlorosilane, 500), fluid(Materials.Nitrogen, 1000))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 500))
            .itemOutputs(plate(PrPMaterials.SilicaAerogel, 1))
            .duration(2000)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }
}
