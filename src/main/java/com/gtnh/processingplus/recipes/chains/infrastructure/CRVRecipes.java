package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeConstants.AssemblyLine;
import static gregtech.api.util.GTRecipeConstants.RESEARCH_ITEM;
import static gregtech.api.util.GTRecipeConstants.SCANNING;

import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.recipe.Scanning;

public class CRVRecipes {

    public static void init() {
        amorphousTritaniumAlloy();
        amorphousNaquadria();
        prometheanNaquadria();
        controllerRecipe();
    }

    private static void prometheanNaquadria() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Naquadria, 4), circuit(10))
            .fluidInputs(
                molten(Materials.Promethium, 576),
                fluid(PrPMaterials.HBNLubricant, 1000),
                fluid(Materials.Argon, 4000))
            .itemOutputs(ingot(PrPMaterials.PrometheanNaquadria, 8))
            .duration(60 * SECONDS)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sCRVRecipes);
    }

    private static void controllerRecipe() {
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Hull_LuV.get(1))
            .metadata(SCANNING, new Scanning(45 * SECONDS, TierEU.RECIPE_ZPM))
            .itemInputs(
                ItemList.AlloySmelterLuV.get(4),
                block(PrPMaterials.HexagonalBoronNitride, 8),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 4),
                GTOreDictUnificator.get(OrePrefixes.pipeMedium, Materials.StainlessSteel, 16),
                item("screwLumiium", 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 2),
                item("cableGt16RefractoryHigh-EntropyAlloy", 16))
            .fluidInputs(
                fluid("molten.indalloy140", 1152),
                fluid(PrPMaterials.HBNLubricant, 1000),
                fluid(Materials.Argon, 2000))
            .itemOutputs(GTNHPPBlocks.CRV.getStackForm(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(40 * SECONDS)
            .addTo(AssemblyLine);

        GTValues.RA.stdBuilder()
            .itemInputs(
                item("frameGtIridium", 1),
                plate(Materials.Gadolinium, 8),
                ItemList.Electric_Piston_IV.get(2),
                foil(PrPMaterials.PHSResin, 4))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.CRV_CASING))
            .eut(TierEU.RECIPE_EV)
            .duration(10 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                item("frameGtDuranium", 1),
                doublePlate(PrPMaterials.HexagonalBoronNitride, 6),
                item("boltRuridit", 16),
                ItemList.Electric_Piston_IV.get(2),
                foil(PrPMaterials.PHSResin, 4))
            .fluidInputs(fluid("wet.concrete", 1152))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.HBN_CERAMIC_BLOCK))
            .eut(TierEU.RECIPE_EV)
            .duration(10 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    private static void amorphousTritaniumAlloy() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Tritanium, 4), dust(Materials.Americium, 2), circuit(8))
            .fluidInputs(fluid(PrPMaterials.HBNLubricant, 1000), fluid(Materials.Argon, 2000))
            .itemOutputs(ingot(PrPMaterials.AmorphousTritaniumAlloy, 4))
            .duration(40 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sCRVRecipes);
    }

    private static void amorphousNaquadria() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Naquadria, 4), ingot(Materials.NaquadahEnriched, 2), circuit(9))
            .fluidInputs(fluid(PrPMaterials.HBNLubricant, 1000), fluid(Materials.Argon, 4000))
            .itemOutputs(ingot(PrPMaterials.AmorphousNaquadria, 2))
            .duration(60 * SECONDS)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sCRVRecipes);
    }
}
