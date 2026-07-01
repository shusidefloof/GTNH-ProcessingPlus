package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;

public class CSTRRecipes {

    public static void init() {
        casingRecipe();
        controllerRecipe();
    }

    // =========================================================
    // CSTR casing — titanium/stainless tank with PTFE liner, agitator seal
    // =========================================================
    private static void casingRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.StainlessSteel, 1),
                plate(Materials.Titanium, 4),
                plate(Materials.StainlessSteel, 4),
                plate(Materials.Polytetrafluoroethylene, 2),
                ItemList.Electric_Motor_IV.get(1),
                circuit(8))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.CSTR_CASING))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // =========================================================
    // CSTR controller — assembler + shaped recipe (IV tier, accessible)
    // =========================================================
    private static void controllerRecipe() {
        ItemStack controller = new ItemStack(GregTechAPI.sBlockMachines, 1, GTNHPPBlocks.CSTR_ID);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Hull_IV.get(1),
                plate(Materials.Titanium, 4),
                plate(Materials.StainlessSteel, 2),
                ItemList.Electric_Pump_IV.get(2),
                ItemList.Electric_Motor_IV.get(2),
                circuit(8))
            .itemOutputs(controller)
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.assemblerRecipes);

        GameRegistry.addShapedRecipe(
            controller,
            "SHS",
            "MCM",
            "SPS",
            'S',
            plate(Materials.Titanium, 1),
            'H',
            ItemList.Hull_IV.get(1),
            'M',
            ItemList.Electric_Motor_IV.get(1),
            'C',
            circuit(8),
            'P',
            ItemList.Electric_Pump_IV.get(1));
    }
}
