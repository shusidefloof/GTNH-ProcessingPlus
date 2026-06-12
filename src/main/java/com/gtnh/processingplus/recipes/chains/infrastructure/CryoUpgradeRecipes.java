package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import static com.gtnh.processingplus.items.Intermediate.*;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeConstants;

/**
 * Alternate recipes using LiquidArgon/LiquidNitrogen from the CSC instead of large gas volumes.
 * All use circuit(11+) to avoid conflicts with the base gas-atmosphere recipes.
 * Reward: 25% better yields and 4-8x less fluid input.
 */
public class CryoUpgradeRecipes {

    public static void init() {
        hbnBlendingCryo();
        graphitizationCryo();
        crvTitaniumCryo();
        crvNaquadriaCryo();
    }

    // hBN blending with cryo atmosphere — saves N₂ 16k + Ar 8k, gives 10 blend vs 8 base
    private static void hbnBlendingCryo() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.CrudeHBN, 4), dust(Materials.Yttrium, 16), circuit(11))
            .fluidInputs(fluid(Materials.Nitrogen, 2000), fluid(PrPMaterials.LiquidArgon, 1000))
            .itemOutputs(dust(PrPMaterials.HBNPowderBlend, 10))
            .fluidOutputs(fluid(Materials.NitricOxide, 3000), fluid(Materials.Oxygen, 1500))
            .duration(480)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // CF graphitization with LAr inert atmosphere — saves Ar 16k, gives 5 fibers vs 4 base
    private static void graphitizationCryo() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.CarbonFiberTow, 4), circuit(11))
            .fluidInputs(fluid(PrPMaterials.LiquidArgon, 2000))
            .itemOutputs(dust(PrPMaterials.GraphitizedCarbonFiber, 5))
            .duration(1000)
            .eut(TierEU.RECIPE_ZPM)
            .metadata(GTRecipeConstants.COIL_HEAT, 4500)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // CRV Amorphous Tritanium — LAr cryo-quench, saves Ar 2k, gives 5 ingots vs 4 base
    private static void crvTitaniumCryo() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Tritanium, 4), dust(Materials.Americium, 2), circuit(12))
            .fluidInputs(fluid(PrPMaterials.HBNLubricant, 500), fluid(PrPMaterials.LiquidArgon, 500))
            .itemOutputs(ingot(PrPMaterials.AmorphousTritaniumAlloy, 5))
            .duration(640)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sCRVRecipes);
    }

    // CRV Amorphous Naquadria — LAr cryo-quench, saves Ar 4k, gives 3 ingots vs 2 base
    private static void crvNaquadriaCryo() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Naquadria, 4), ingot(Materials.NaquadahEnriched, 2), circuit(13))
            .fluidInputs(fluid(PrPMaterials.HBNLubricant, 1000), fluid(PrPMaterials.LiquidArgon, 1000))
            .itemOutputs(ingot(PrPMaterials.AmorphousNaquadria, 3))
            .duration(960)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sCRVRecipes);
    }
}
