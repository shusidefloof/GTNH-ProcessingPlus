package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.GTNHPPRecipeHelper.*;

import com.gtnh.processingplus.materials.GTNHPPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gregtech.api.util.GTRecipeConstants;

public class SiCRecipes {

    public static void init() {
        step1_CarbothermalReduction();
        step2_AcidPurification();
        step3_Sintering();
        step4_Machining();
        step5_Casing();
    }

    // =========================================================
    // 1. SiO2 + C → Crude SiC (Acheson process)
    // =========================================================
    private static void step1_CarbothermalReduction() {

        GTValues.RA.stdBuilder()
            .itemInputs(
                dust(Materials.SiliconDioxide, 3),
                dust(Materials.Carbon, 3),
                circuit(1)
            )
            .fluidInputs(
                fluid(Materials.Argon, 1000)
            )
            .fluidOutputs(
                fluid(Materials.CarbonMonoxide, 2000)
            )
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, GTNHPPMaterials.CrudeSiCPowder, 2)

            )
            .duration(1200)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // 2. HF purification → Purified SiC
    // =========================================================
    private static void step2_AcidPurification() {

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, GTNHPPMaterials.CrudeSiCPowder, 4)
            )
            .fluidInputs(
                fluid(Materials.HydrofluoricAcid, 500),
                fluid(Materials.SulfuricAcid, 500)
            )
            .fluidOutputs(
                fluid(Materials.Water, 800)
            )
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, GTNHPPMaterials.PurifiedSiCPowder, 4)
            )
            .duration(600)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================
    // 3. Hot pressing → Dense SiC ceramic
    // =========================================================
    private static void step3_Sintering() {

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, GTNHPPMaterials.PurifiedSiCPowder, 4),
                dust(Materials.Boron, 1)
            )
            .fluidInputs(
                fluid(Materials.Argon, 500)
            )
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, GTNHPPMaterials.DenseSiCCompact, 2)

            )
            .duration(1600)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // =========================================================
    // 4. Cutting / machining → plates
    // =========================================================
    private static void step4_Machining() {

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, GTNHPPMaterials.DenseSiCCompact, 1)
            )
            .itemOutputs(
                plate(GTNHPPMaterials.SinteredSiliconCarbide, 2),
                dustSmall(GTNHPPMaterials.SinteredSiliconCarbide, 2)
            )
            .duration(800)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.latheRecipes);
    }

    // =========================================================
    // 5. SiC casing block for multiblocks
    // =========================================================
    private static void step5_Casing() {

        GTValues.RA.stdBuilder()
            .itemInputs(
                dust(GTNHPPMaterials.SinteredSiliconCarbide, 9),
                circuit(9)
            )
            .fluidInputs(
                fluid(Materials.Argon, 1000)
            )
            .itemOutputs(
                new net.minecraft.item.ItemStack(
                    com.gtnh.processingplus.blocks.GTNHPPBlocks.CASINGS,
                    1,
                    com.gtnh.processingplus.blocks.BlockGTNHPPCasings.HTRF_CASING
                )
            )
            .duration(1600)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }
}
