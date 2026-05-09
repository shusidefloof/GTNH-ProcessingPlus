package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import bartworks.system.material.Werkstoff;
import com.gtnh.processingplus.materials.PPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class CarbonFiberRecipes {

    public static void init() {
        step1_AcrylonitrileSynthesis();
        step2_Polymerization();
        step3_Spinning();
        step4_SolventRecovery();
        step5_Stabilization();
        step6_Carbonization();
        step7_CompositeFinal();
    }

    // =========================================================
    // 1. Propene → Acrylonitrile (Ammoxidation)
    // =========================================================
    private static void step1_AcrylonitrileSynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(5))
            .fluidInputs(fluid(Materials.Propene, 3000), fluid(Materials.Ammonia, 3000), fluid(Materials.Oxygen, 1000))
            .fluidOutputs(fluid(Materials.Water, 3000))
            .itemOutputs(dust(PPMaterials.Acrylonitrile, 2))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 2. Acrylonitrile → PAN Solution
    // =========================================================
    private static void step2_Polymerization() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PPMaterials.Acrylonitrile, 4))
            .fluidInputs(fluid(Materials.NMethylIIPyrrolidone, 1000))
            .fluidOutputs(fluid(PPMaterials.PolyacrylonitrileSolution, 1000))
            .duration(800)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 3. Wet Spinning (fiber formation)
    // =========================================================
    private static void step3_Spinning() {

        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PPMaterials.PolyacrylonitrileSolution, 1000), fluid(Materials.Water, 3000))
            .itemOutputs(dust(PPMaterials.Polyacrylonitrile, 4))
            .fluidOutputs(fluid(PPMaterials.DilutedNMP, 1500))
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTPPRecipeMaps.chemicalPlantRecipes);
    }

    // =========================================================
    // 4. Solvent Recovery
    // =========================================================
    private static void step4_SolventRecovery() {

        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PPMaterials.DilutedNMP, 1500))
            .fluidOutputs(fluid(Materials.NMethylIIPyrrolidone, 950), fluid(Materials.Water, 550))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.distillationTowerRecipes);
    }

    // =========================================================
    // 5. Oxidative Stabilization
    // =========================================================
    private static void step5_Stabilization() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PPMaterials.Polyacrylonitrile, 4))
            .fluidInputs(fluid(Materials.Oxygen, 2000))
            .itemOutputs(dust(PPMaterials.StabilizedPolyacrylonitrile, 4))
            .duration(1600)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sDAFOxidizingRecipes);
    }

    // =========================================================
    // 6. Carbonization
    // =========================================================
    private static void step6_Carbonization() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PPMaterials.StabilizedPolyacrylonitrile, 4))
            .fluidInputs(fluid(Materials.Nitrogen, 2000))
            .itemOutputs(dust(PPMaterials.CarbonFiberTow, 3))
            .fluidOutputs(fluid(Materials.CarbonMonoxide, 500), fluid(PPMaterials.HydrogenCyanide, 250))
            .duration(2000)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sDAFInertRecipes);
    }

    // =========================================================
    // 7. Final Composite
    // =========================================================
    private static void step7_CompositeFinal() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PPMaterials.CarbonFiberTow, 4))
            .fluidInputs(fluid("molten.epoxid", 576))
            .itemOutputs(plate(PPMaterials.CarbonFiberComposite, 4))
            .duration(1200)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.assemblerRecipes);
    }
}
