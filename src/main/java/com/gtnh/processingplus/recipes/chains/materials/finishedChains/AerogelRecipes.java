package com.gtnh.processingplus.recipes.chains.materials.finishedChains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.machines.MTE_SCD;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GTRecipeConstants;

public class AerogelRecipes {

    public static void init() {
        step0_TrimethylsilaneSynthesis();
        stepAlt0_CatalyzedTrimethylsilane();
        stepAlt_TrimethylchlorosilaneSynthesis();
        step1_TEOSSynthesis();
        step2a_AcidHydrolysis();
        step2b_BaseCondensation();
        step3_Aging();
        step4_SolventExchange();
        step4b_AcetoneExchange();
        step5_SupercriticalDrying();
        step6_HydrophobicTreatment();
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
            .metadata(GTRecipeConstants.COIL_HEAT, 9900)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // ALT 0: Pt/Pd-catalysed Trimethylsilane
    // =========================================================
    private static void stepAlt0_CatalyzedTrimethylsilane() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1), dust(PrPMaterials.LoadedAerogelCatalystSupport, 1))
            .fluidInputs(
                fluid(Materials.SiliconTetrachloride, 1000),
                fluid(Materials.Methane, 3000),
                fluid(Materials.Hydrogen, 1000))
            .fluidOutputs(fluid(PrPMaterials.Trimethylsilane, 1500), fluid(Materials.HydrochloricAcid, 4000))
            .duration(300)
            .eut(TierEU.RECIPE_UV)
            .metadata(GTRecipeConstants.COIL_HEAT, 9900)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // ALT: Trimethylchlorosilane synthesis — (CH3)3SiH + Cl2 → TMCS + HCl (CSTR)
    // =========================================================
    private static void stepAlt_TrimethylchlorosilaneSynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(5))
            .fluidInputs(fluid(PrPMaterials.Trimethylsilane, 1000), fluid(Materials.Chlorine, 1000))
            .fluidOutputs(fluid(PrPMaterials.Trimethylchlorosilane, 1000), fluid(Materials.HydrochloricAcid, 1000))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
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
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // =========================================================
    // 2a. Acid hydrolysis — TEOS + water, HF-catalysed → hydrolysed Silica Sol (+ ethanol released).
    // =========================================================
    private static void step2a_AcidHydrolysis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(7))
            .fluidInputs(
                fluid(PrPMaterials.TEOS, 1000),
                fluid(Materials.Water, 5000),
                fluid(Materials.HydrofluoricAcid, 100))
            .fluidOutputs(fluid(PrPMaterials.SilicaSol, 1000), fluid(Materials.Ethanol, 4000))
            .duration(600)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // =========================================================
    // 2b. Base condensation — Silica Sol + ammonia → Wet Silica Gel (+ water released).
    // =========================================================
    private static void step2b_BaseCondensation() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(8))
            .fluidInputs(fluid(PrPMaterials.SilicaSol, 1000), fluid(Materials.Ammonia, 500))
            .itemOutputs(dust(PrPMaterials.WetSilicaGel, 2))
            .fluidOutputs(fluid(Materials.Water, 1000))
            .duration(800)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // =========================================================
    // 3. Aging — WetSilicaGel is held in water to mature the silica network (CSTR).
    // Continuous fluid contact with the gel over time; no reaction, just structural consolidation.
    // =========================================================
    private static void step3_Aging() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.WetSilicaGel, 4))
            .fluidInputs(fluid(Materials.Water, 1000))
            .itemOutputs(dust(PrPMaterials.AgedSilicaGel, 4))
            .duration(3200)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // =========================================================
    // 4. Solvent Exchange — water displaced by ethanol under continuous flow (CSTR).
    // Ethanol percolates through the gel matrix, carrying water out; classic CSTR operation.
    // =========================================================
    private static void step4_SolventExchange() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.AgedSilicaGel, 4))
            .fluidInputs(fluid(Materials.Ethanol, 4000))
            .fluidOutputs(fluid(Materials.Water, 3000))
            .itemOutputs(dust(PrPMaterials.EthanolSaturatedGel, 4))
            .duration(800)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // =========================================================
    // 4b. Acetone exchange — ethanol displaced by acetone under continuous flow (CSTR).
    // Acetone is fully miscible with scCO₂; a small LiquidCO₂ co-feed strips residual water.
    // =========================================================
    private static void step4b_AcetoneExchange() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.EthanolSaturatedGel, 4))
            .fluidInputs(fluid(Materials.Acetone, 4000), fluid(PrPMaterials.LiquidCO2, 1000))
            .fluidOutputs(fluid(Materials.Ethanol, 4000), fluid(Materials.CarbonDioxide, 1000))
            .itemOutputs(dust(PrPMaterials.AcetoneSaturatedGel, 4))
            .duration(800)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // =========================================================
    // 5. Supercritical CO₂ drying (SCD, 3-stage mechanic).
    //
    // The acetone-saturated gel enters the pressure vessel. Liquid CO₂ is pumped in until it crosses
    // the critical point (31 °C, 73.8 bar), becoming supercritical and flushing the pore acetone out
    // without any liquid-vapour interface. Both the recovered CO₂ (loops to CSC) and acetone are
    // vented during controlled depressurisation.
    //
    //   Stage 1 — Acetone flush (~10 mB/t): acetone is circulated through the gel to ensure full
    //             pore saturation and displace any remaining water before CO₂ is introduced.
    //   Stage 2 — scCO₂ infusion: Liquid CO₂ from the CSC is pumped in (~24 mB/t baseline).
    //             Freon R-12 lowers the critical temperature and completes stage 2 in half the time.
    //   Stage 3 — Controlled depressurisation (empty hatch): CO₂ vented slowly. Any fluid present
    //             at this point contaminates the pore structure → degraded (collapsed) output.
    //
    // Perfect output:  SilicaAerogel plate × 2 + CO₂ gas 8 000 mB + Acetone 3 500 mB (recovered).
    // Degraded output: AcetoneSaturatedGel dust × 2 (collapsed gel; re-submittable for another attempt).
    // =========================================================
    private static void step5_SupercriticalDrying() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.AcetoneSaturatedGel, 4))
            .itemOutputs(
                plate(PrPMaterials.SilicaAerogel, 2),              // [0] perfect
                dust(PrPMaterials.AcetoneSaturatedGel, 2))          // [1] degraded
            .fluidOutputs(
                fluid(Materials.CarbonDioxide, 8000),               // CO₂ recovery loop
                fluid(Materials.Acetone, 3500))                     // acetone recovery
            .duration(1000)
            .eut(TierEU.RECIPE_HV)
            .metadata(GTRecipeConstants.COIL_HEAT,
                MTE_SCD.encodeStageData(MTE_SCD.STAGE1_ACETONE, 10, 24))
            .addTo(GTNHPPRecipeMaps.sSCDRecipes);
    }

    // =========================================================
    // 6. Hydrophobic surface modification — TMCS caps surface silanol groups (CSTR).
    // =========================================================
    private static void step6_HydrophobicTreatment() {

        GTValues.RA.stdBuilder()
            .itemInputs(plate(PrPMaterials.SilicaAerogel, 2))
            .fluidInputs(fluid(PrPMaterials.Trimethylchlorosilane, 2000))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 2000))
            .itemOutputs(plate(PrPMaterials.HydrophobicSilicaAerogel, 2))
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }
}
