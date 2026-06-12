package com.gtnh.processingplus.recipes.chains.materials.finishedChains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import static com.gtnh.processingplus.items.Intermediate.*;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
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
    // ALT 0: Pt/Pd-catalysed Trimethylsilane — the Loaded Aerogel Catalyst (PGM nanoparticles on an
    // aerogel support) catalyses the Si-C coupling, so the same inputs run faster and at higher yield.
    // The catalyst is consumed per batch. The base step0 stays available, so the chain still bootstraps
    // before any aerogel (and hence any catalyst) exists — this is purely the late-game efficiency route.
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
    // ALT: Trimethylchlorosilane synthesis — (CH3)3SiH + Cl2 → TMCS + HCl (UV LCR)
    // =========================================================
    private static void stepAlt_TrimethylchlorosilaneSynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(5))
            .fluidInputs(fluid(PrPMaterials.Trimethylsilane, 1000), fluid(Materials.Chlorine, 1000))
            .fluidOutputs(fluid(PrPMaterials.Trimethylchlorosilane, 1000), fluid(Materials.HydrochloricAcid, 1000))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
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
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 2a. Acid hydrolysis — TEOS + water, HF-catalysed → hydrolysed Silica Sol (+ ethanol released).
    // First half of the real two-step sol-gel: acid hydrolyses the alkoxide into a colloidal sol.
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
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 2b. Base condensation — Silica Sol + ammonia (base) → Wet Silica Gel (+ water released).
    // Second half: ammonia catalyses condensation of the sol into a solid gel network.
    // =========================================================
    private static void step2b_BaseCondensation() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(8))
            .fluidInputs(fluid(PrPMaterials.SilicaSol, 1000), fluid(Materials.Ammonia, 500))
            .itemOutputs(dust(PrPMaterials.WetSilicaGel, 2))
            .fluidOutputs(fluid(Materials.Water, 1000))
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
            .duration(3200)
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
            .duration(800)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sSCDRecipes);
    }

    // =========================================================
    // 4b. Acetone exchange — swap the pore ethanol for acetone, which is fully miscible with
    // supercritical CO2 (ethanol is not). Most of the ethanol is recovered for reuse.
    // =========================================================
    private static void step4b_AcetoneExchange() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.EthanolSaturatedGel, 4))
            .fluidInputs(fluid(Materials.Acetone, 4000))
            .fluidOutputs(fluid(Materials.Ethanol, 3500))
            .itemOutputs(dust(PrPMaterials.AcetoneSaturatedGel, 4))
            .duration(800)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sSCDRecipes);
    }

    // =========================================================
    // 5. Supercritical CO2 drying — liquid CO2 from the CSC goes supercritical inside the dryer and
    // extracts the pore acetone without surface tension collapsing the network. Both the CO2 (as gas)
    // and the acetone are recovered; the recovered CO2 loops straight back into the CSC.
    // =========================================================
    private static void step5_SupercriticalDrying() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.AcetoneSaturatedGel, 4))
            .fluidInputs(fluid(PrPMaterials.LiquidCO2, 8000))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 8000),
                fluid(Materials.Acetone, 3500))
            .itemOutputs(plate(PrPMaterials.SilicaAerogel, 2))
            .duration(1000)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sSCDRecipes);
    }

    // =========================================================
    // 6. Hydrophobic surface modification — TMCS caps the surface silanol (Si-OH) groups with inert
    // trimethylsilyl groups, making the aerogel permanently moisture-proof. Releases HCl.
    // =========================================================
    private static void step6_HydrophobicTreatment() {

        GTValues.RA.stdBuilder()
            .itemInputs(plate(PrPMaterials.SilicaAerogel, 2))
            .fluidInputs(fluid(PrPMaterials.Trimethylchlorosilane, 2000))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 2000))
            .itemOutputs(plate(PrPMaterials.HydrophobicSilicaAerogel, 2))
            .duration(600)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }
}
