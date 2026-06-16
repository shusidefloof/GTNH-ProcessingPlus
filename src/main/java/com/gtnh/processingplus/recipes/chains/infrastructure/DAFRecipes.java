package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

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

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

public class DAFRecipes {

    public static void init() {
        casingRecipe();
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
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.SuperconductorUV, 4))
            .fluidInputs(molten(Materials.SolderingAlloy, 2304))
            .itemOutputs(GTNHPPBlocks.DAF.getStackForm(1))
            .duration(60 * SECONDS)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.assemblerRecipes);
    }
}
