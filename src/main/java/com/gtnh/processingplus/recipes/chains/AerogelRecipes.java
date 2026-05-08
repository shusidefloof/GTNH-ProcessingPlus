package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.GTNHPPRecipeHelper.*;

import com.gtnh.processingplus.materials.GTNHPPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;

public class AerogelRecipes {

    public static void init() {
        step1_TEOSSynthesis();
        step2_SolGelHydrolysis();
        step3_Aging();
        step4_SolventExchange();
        step5_SupercriticalDrying();
    }

    // =========================================================
    // 1. SiCl4 + Ethanol → TEOS
    // =========================================================
    private static void step1_TEOSSynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(6))
            .fluidInputs(
                fluid(Materials.SiliconTetrachloride, 1000),
                fluid(Materials.Ethanol, 4000)
            )
            .fluidOutputs(
                GTNHPPMaterials.TEOS.getFluid(1000),
                fluid(Materials.HydrochloricAcid, 4000)
            )
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
                GTNHPPMaterials.TEOS.getFluid(1000),
                fluid(Materials.Water, 2000),
                fluid(Materials.HydrofluoricAcid, 1000)
            )
            .itemOutputs(GTNHPPMaterials.WetSilicaGel.getDust(2))
            .duration(800)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 3. Aging
    // =========================================================
    private static void step3_Aging() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.WetSilicaGel.getDust(4))
            .fluidInputs(fluid(Materials.Water, 1000))
            .itemOutputs(GTNHPPMaterials.AgedSilicaGel.getDust(4))
            .duration(1600)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sSCDRecipes);
    }

    // =========================================================
    // 4. Solvent Exchange
    // =========================================================
    private static void step4_SolventExchange() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.AgedSilicaGel.getDust(4))
            .fluidInputs(fluid(Materials.Ethanol, 4000))
            .fluidOutputs(fluid(Materials.Water, 3000))
            .itemOutputs(GTNHPPMaterials.EthanolSaturatedGel.getDust(4))
            .duration(2400)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sSCDRecipes);
    }

    // =========================================================
    // 5. Supercritical Drying → Aerogel
    // =========================================================
    private static void step5_SupercriticalDrying() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.EthanolSaturatedGel.getDust(4))
            .fluidInputs(fluid(Materials.Ethanol, 2000))
            .fluidOutputs(fluid(Materials.Ethanol, 1800))
            .itemOutputs(
                plate(GTNHPPMaterials.SilicaAerogel, 2)
            )
            .duration(3200)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sSCDRecipes);
    }
}
