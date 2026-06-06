package com.gtnh.processingplus.recipes.chains.materials.finishedChains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeConstants;

public class NeptuniumSynthesis {

    public static void init() {
        step1_NitricAcidDissolution();
        step2_NaphthaNitricExtraction();
        step3_AmmoniaPrecipitation();
        step3r_ReconcentrateNitricAcid();
        step3r2_DecomposeAmmoniumNitrate();
        step4a_CalciumThermite();
        step4b_BariumThermite();
    }

    // =========================================================
    // 1. Dissolve depleted uranium rod in HNO3 — LCR
    // Single / dual / quad rod variants, scaled 1× / 2× / 4×.
    // Outputs U dust + neptunium extraction residue + diluted HNO3 + empty rod casings.
    // =========================================================
    private static void step1_NitricAcidDissolution() {

        // Single rod
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.DepletedRodUranium.get(1), circuit(1))
            .fluidInputs(fluid(Materials.NitricAcid, 2000))
            .itemOutputs(
                dust(Materials.Uranium, 3),
                dust(PrPMaterials.NeptuniumExtractionResidue, 1),
                ItemList.IC2_Fuel_Rod_Empty.get(1))
            .fluidOutputs(fluid(PrPMaterials.DilutedNitricAcid, 2000))
            .duration(600)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // Dual rod — 2×
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.DepletedRodUranium2.get(1), circuit(1))
            .fluidInputs(fluid(Materials.NitricAcid, 4000))
            .itemOutputs(
                dust(Materials.Uranium, 6),
                dust(PrPMaterials.NeptuniumExtractionResidue, 2),
                ItemList.IC2_Fuel_Rod_Empty.get(2))
            .fluidOutputs(fluid(PrPMaterials.DilutedNitricAcid, 4000))
            .duration(1000)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // Quad rod — 4×
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.DepletedRodUranium4.get(1), circuit(1))
            .fluidInputs(fluid(Materials.NitricAcid, 8000))
            .itemOutputs(
                dust(Materials.Uranium, 12),
                dust(PrPMaterials.NeptuniumExtractionResidue, 4),
                ItemList.IC2_Fuel_Rod_Empty.get(4))
            .fluidOutputs(fluid(PrPMaterials.DilutedNitricAcid, 8000))
            .duration(1800)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 2. Naphtha/nitric acid liquid-liquid extraction — LCR
    // 4× residue + HNO3 + naphtha → neptunium nitrate solution + recovered naphtha.
    // Naphtha acts as organic extractant (80% recovery).
    // =========================================================
    private static void step2_NaphthaNitricExtraction() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.NeptuniumExtractionResidue, 4))
            .fluidInputs(fluid(Materials.NitricAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.NeptuniumNitrateSolution, 2000))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 3. Ammonia precipitation — LCR
    // Np nitrate solution + NH3 + water → neptunium oxide + ammonium nitrate solution.
    // All ammonia is retained in the ammonium nitrate solution; step 3r2 recovers it fully.
    // =========================================================
    private static void step3_AmmoniaPrecipitation() {

        GTValues.RA.stdBuilder()
            .fluidInputs(
                fluid(PrPMaterials.NeptuniumNitrateSolution, 2000),
                fluid(Materials.Ammonia, 1000),
                fluid(Materials.Water, 500))
            .itemOutputs(dust(PrPMaterials.NeptuniumOxide, 2))
            .fluidOutputs(fluid(PrPMaterials.AmmoniumNitrateSolution, 2000))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // Recycling 3r: Reconcentrate diluted HNO3 — Distillation Tower
    // 4000 mB diluted → 1000 mB HNO3 + 3000 mB water.
    // =========================================================
    private static void step3r_ReconcentrateNitricAcid() {

        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.DilutedNitricAcid, 4000))
            .fluidOutputs(fluid(Materials.NitricAcid, 1000), fluid(Materials.Water, 3000))
            .duration(600)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.distillationTowerRecipes);
    }

    // =========================================================
    // Recycling 3r2: Decompose ammonium nitrate solution — Distillation Tower
    // 2000 mB ammonium nitrate solution → 1000 mB NH3 + 2000 mB diluted HNO3.
    // Closes both the ammonia and nitric acid loops completely.
    // =========================================================
    private static void step3r2_DecomposeAmmoniumNitrate() {

        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.AmmoniumNitrateSolution, 2000))
            .fluidOutputs(fluid(Materials.Ammonia, 1000), fluid(Materials.NitricAcid, 2000))
            .duration(800)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.distillationTowerRecipes);
    }

    // =========================================================
    // 4a. NpO2 + 2 Ca → Np ingot + 2 CaO (quicklime) — EBF 1500 K
    // =========================================================
    private static void step4a_CalciumThermite() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.NeptuniumOxide, 1), dust(Materials.Calcium, 2))
            .itemOutputs(item("ingotNeptunium", 1), dust(Materials.Quicklime, 2))
            .duration(600)
            .eut(TierEU.RECIPE_HV)
            .metadata(GTRecipeConstants.COIL_HEAT, 1500)
            .addTo(RecipeMaps.blastFurnaceRecipes);
    }

    // =========================================================
    // 4b. NpO2 + 2 Ba → Np ingot + 2 BaO — EBF 1500 K (alternate)
    // =========================================================
    private static void step4b_BariumThermite() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.NeptuniumOxide, 1), dust(Materials.Barium, 2))
            .itemOutputs(item("ingotNeptunium", 1), dust(PrPMaterials.BariumOxide, 2))
            .duration(600)
            .eut(TierEU.RECIPE_HV)
            .metadata(GTRecipeConstants.COIL_HEAT, 1500)
            .addTo(RecipeMaps.blastFurnaceRecipes);
    }
}
