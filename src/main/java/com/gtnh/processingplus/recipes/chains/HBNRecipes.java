package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeConstants;

public class HBNRecipes {

    public static void init() {
        step1_BoraxToB2O3();
        step2_CarbothermalReduction();
        step3_Nitriding();
        step4_PowderBlending();
        step5_Sintering();
        step6_MachiningAndCasing();
    }

    // =========================================================
    // 1. Borax → Boron Trioxide
    // =========================================================
    private static void step1_BoraxToB2O3() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Borax, 2))
            .fluidInputs(fluid(Materials.SulfuricAcid, 1000))
            .fluidOutputs(fluid(Materials.Water, 2000))
            .itemOutputs(GTOreDictUnificator.get("dustBoronTrioxide", 1))
            .duration(600)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================
    // 2. B2O3 → Boron Carbide (HTRF)
    // =========================================================
    private static void step2_CarbothermalReduction() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get("dustBoronTrioxide", 2), dust(Materials.Carbon, 5))
            .fluidOutputs(fluid(Materials.CarbonMonoxide, 4000))
            .itemOutputs(dust(PPMaterials.BoronCarbide, 1))
            .duration(1600)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // 3. Boron Carbide → Crude hBN (nitriding)
    // =========================================================
    private static void step3_Nitriding() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PPMaterials.BoronCarbide, 1))
            .fluidInputs(fluid(Materials.Ammonia, 3000))
            .fluidOutputs(fluid(Materials.CarbonMonoxide, 3000))
            .itemOutputs(dust(PPMaterials.CrudeHBN, 3))
            .duration(2400)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sAARRecipes);
    }

    // =========================================================
    // 4. Powder blending (dopants / sinter aid)
    // =========================================================
    private static void step4_PowderBlending() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PPMaterials.CrudeHBN, 4), dust(Materials.Yttrium, 1))
            .fluidInputs(fluid(Materials.Nitrogen, 500))
            .itemOutputs(dust(PPMaterials.HBNPowderBlend, 4))
            .duration(800)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================
    // 5. Hot pressing → Dense hBN ceramic
    // =========================================================
    private static void step5_Sintering() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PPMaterials.HBNPowderBlend, 4))
            .fluidInputs(fluid(Materials.Nitrogen, 1000))
            .itemOutputs(dust(PPMaterials.DenseHBNCeramic, 2))
            .duration(2400)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // =========================================================
    // 6. Machining + casing block
    // =========================================================
    private static void step6_MachiningAndCasing() {

        // Plates
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PPMaterials.DenseHBNCeramic, 1))
            .itemOutputs(
                plate(PPMaterials.HexagonalBoronNitride, 2),
                dustSmall(PPMaterials.HexagonalBoronNitride, 2))
            .duration(1000)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.latheRecipes);

        // Casing block
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PPMaterials.HexagonalBoronNitride, 9), circuit(8))
            .fluidInputs(fluid(Materials.Nitrogen, 2000))
            .itemOutputs(
                new net.minecraft.item.ItemStack(
                    com.gtnh.processingplus.blocks.GTNHPPBlocks.CASINGS,
                    1,
                    com.gtnh.processingplus.blocks.BlockGTNHPPCasings.HBN_CERAMIC_BLOCK))
            .duration(3200)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }
}
