package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;
import static gregtech.api.enums.Materials.getGtMaterialFromFluid;

import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeConstants;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.fluids.GTPPFluids;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

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
            .fluidInputs(new FluidStack(GTPPFluids.BoricAcid, 2000))
            .fluidOutputs(fluid(Materials.Steam, 6000))
            .itemOutputs(GTOreDictUnificator.get("dustBoronTrioxide", 1))
            .duration(600)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTPPRecipeMaps.chemicalDehydratorRecipes);
    }

    // =========================================================
    // 2. B2O3 → Boron Carbide (HTRF)
    // =========================================================
    private static void step2_CarbothermalReduction() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get("dustBoronTrioxide", 4), dust(Materials.Carbon, 9))
            .fluidOutputs(fluid(Materials.CarbonMonoxide, 2000),
                fluid(Materials.CarbonDioxide, 5000))
            .itemOutputs(dust(PrPMaterials.BoronCarbide, 2))
            .duration(1600)
            .eut((int) (TierEU.RECIPE_LuV * 0.75))
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // 3. Boron Carbide → Crude hBN (nitriding)
    // =========================================================
    private static void step3_Nitriding() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.BoronCarbide, 4))
            .fluidInputs(fluid(Materials.Oxygen, 5000)
                )
            .fluidOutputs(fluid(Materials.Hydrogen, 24000), fluid(Materials.CarbonDioxide, 1000), fluid(Materials.CarbonMonoxide, 3000))
            .itemOutputs(dust(PrPMaterials.CrudeHBN, 12))
            .duration(2400)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sAARRecipes);
    }

    // =========================================================
    // 4. Powder blending (dopants / sinter aid)
    // =========================================================
    private static void step4_PowderBlending() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.CrudeHBN, 4), dust(Materials.Yttrium, 4))
            .fluidInputs(fluid(Materials.Nitrogen, 16000))
            .itemOutputs(dust(PrPMaterials.HBNPowderBlend, 12))
            .duration(800)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================
    // 5. Hot pressing → Dense hBN ceramic
    // =========================================================
    private static void step5_Sintering() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.HBNPowderBlend, 4))
            .fluidInputs(fluid(Materials.Nitrogen, 16000), fluid(Materials.Argon, 4000))
            .itemOutputs(dust(PrPMaterials.DenseHBNCeramic, 2))
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
            .itemInputs(ingot(PrPMaterials.DenseHBNCeramic, 1))
            .itemOutputs(
                plate(PrPMaterials.HexagonalBoronNitride, 2),
                dustSmall(PrPMaterials.HexagonalBoronNitride, 2))
            .duration(1000)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.latheRecipes);

        // Casing block
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.HexagonalBoronNitride, 9), circuit(8))
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
