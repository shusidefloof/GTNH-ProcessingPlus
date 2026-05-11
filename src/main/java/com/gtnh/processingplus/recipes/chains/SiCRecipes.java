package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
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
        step5_Casing();
    }

    // =========================================================
    // 1. SiO2 + C → Crude SiC (Acheson process)
    // =========================================================
    private static void step1_CarbothermalReduction() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.SiliconDioxide, 3), dust(Materials.Carbon, 3), circuit(1))
            .fluidInputs(fluid(Materials.Argon, 1000))
            .fluidOutputs(fluid(Materials.CarbonMonoxide, 2000))
            .itemOutputs(dust(PrPMaterials.CrudeSiCPowder, 2))
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // ALT: CVD route — SiCl4 + CH4 → PurifiedSiC + HCl (HTRF, UV)
    // Higher purity than Acheson; skips crushing and acid wash
    // =========================================================
    private static void stepAlt_CVDRoute() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(Materials.SiliconTetrachloride, 1000), fluid(Materials.Methane, 1000))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 4000))
            .itemOutputs(dust(PrPMaterials.PurifiedSiCPowder, 2))
            .duration(800)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // 2. HF purification → Purified SiC
    // =========================================================
    private static void step2_AcidPurification() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.CrudeSiCPowder, 4))
            .fluidInputs(fluid(Materials.HydrofluoricAcid, 500), fluid(Materials.SulfuricAcid, 500))
            .fluidOutputs(fluid(Materials.Water, 800))
            .itemOutputs(dust(PrPMaterials.PurifiedSiCPowder, 4))
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================
    // 3. Hot pressing → Dense SiC ceramic
    // =========================================================
    private static void step3_Sintering() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.PurifiedSiCPowder, 4), dust(Materials.Boron, 1))
            .fluidInputs(fluid(Materials.Argon, 500))
            .itemOutputs(dust(PrPMaterials.DenseSiCCompact, 2))
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // =========================================================
    // 4. Cutting / machining → plates
    // =========================================================
    private static void step4_Machining() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.DenseSiCCompact, 1))
            .itemOutputs(
                plate(PrPMaterials.SinteredSiliconCarbide, 2),
                dust(PrPMaterials.SinteredSiliconCarbide, 2))
            .duration(200)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.latheRecipes);
    }

    // =========================================================
    // 5. SiC casing block for multiblocks
    // =========================================================
    private static void step5_Casing() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.SinteredSiliconCarbide, 9), circuit(9))
            .fluidInputs(fluid(Materials.Argon, 1000))
            .itemOutputs(
                new net.minecraft.item.ItemStack(
                    com.gtnh.processingplus.blocks.GTNHPPBlocks.CASINGS,
                    1,
                    com.gtnh.processingplus.blocks.BlockGTNHPPCasings.HTRF_CASING))
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }
}
