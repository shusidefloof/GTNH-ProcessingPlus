package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;

public class DAFRecipes {

    public static void init() {
        casingRecipe();
        casingRecipeLuV();
        casingRecipeUV();
        casingRecipeUEV();
        controllerRecipe();
    }

    // Dual-Sealed Atmosphere Casing — sealed pressure vessel, mid-tier inputs
    private static void casingRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 1),
                plate(Materials.TungstenSteel, 4),
                plate(Materials.StainlessSteel, 4),
                circuit(6))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.DAF_CASING))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // Advanced Atmosphere Casing — LuV chamber shell, titanium-sealed
    private static void casingRecipeLuV() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Titanium, 1),
                plate(Materials.Titanium, 4),
                plate(Materials.TungstenSteel, 2),
                plate(Materials.Iridium, 2),
                circuit(8))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.DAF_CASING_LUV))
            .duration(45 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // Pristine Atmosphere Casing — UV chamber shell, neutronium-lined
    private static void casingRecipeUV() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Osmium, 1),
                plate(Materials.Osmium, 4),
                plate(Materials.Iridium, 4),
                circuit(10))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.DAF_CASING_UV))
            .duration(60 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // Absolute Atmosphere Casing — UEV chamber shell, transcendent alloy
    private static void casingRecipeUEV() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 1),
                plate(Materials.Neutronium, 4),
                plate(Materials.Osmium, 4),
                circuit(12))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.DAF_CASING_UEV))
            .duration(90 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // DAF controller — assembled at ZPM from LuV hull + UV-tier internals
    private static void controllerRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Hull_LuV.get(1),
                new ItemStack(GTNHPPBlocks.CASINGS, 4, BlockGTNHPPCasings.DAF_CASING),
                ItemList.Electric_Pump_UV.get(2),
                ItemList.Field_Generator_UV.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 2),
                item("wireGt01SuperconductorUV", 4))
            .fluidInputs(molten(Materials.SolderingAlloy, 2304))
            .itemOutputs(GTNHPPBlocks.DAF.getStackForm(1))
            .duration(60 * SECONDS)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.assemblerRecipes);
    }
}
