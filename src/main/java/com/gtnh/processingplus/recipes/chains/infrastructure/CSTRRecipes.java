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
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;

public class CSTRRecipes {

    public static void init() {
        casingRecipe();
        controllerRecipe();
    }

    // =========================================================
    // CSTR casing: titanium/stainless tank with PTFE liner, agitator seal
    // =========================================================
    private static void casingRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.StainlessSteel, 1),
                plate(Materials.Titanium, 4),
                plate(Materials.StainlessSteel, 4),
                plate(Materials.Polytetrafluoroethylene, 2),
                ItemList.Electric_Motor_EV.get(1),
                circuit(8))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.CSTR_CASING))
            .duration(7 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // =========================================================
    // CSTR controller: hand-crafted only (EV tier), like GT's own multiblock controllers
    // =========================================================
    private static void controllerRecipe() {
        GTModHandler.addCraftingRecipe(
            GTNHPPBlocks.CSTR.getStackForm(1),
            GTModHandler.RecipeBits.BITS | GTModHandler.RecipeBits.DO_NOT_CHECK_FOR_COLLISIONS,
            new Object[] { "SDS", "MHM", "SPS", 'S', OrePrefixes.plateDense.get(Materials.Titanium), 'D',
                OrePrefixes.plateDouble.get(Materials.Silver), 'M', ItemList.Electric_Motor_EV, 'H', ItemList.Hull_EV,
                'P', ItemList.Electric_Pump_EV });
    }
}
