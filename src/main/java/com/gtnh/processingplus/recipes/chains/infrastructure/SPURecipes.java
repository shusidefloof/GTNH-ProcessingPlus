package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeConstants.AssemblyLine;
import static gregtech.api.util.GTRecipeConstants.RESEARCH_ITEM;
import static gregtech.api.util.GTRecipeConstants.SCANNING;

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
import gregtech.api.util.recipe.Scanning;

public class SPURecipes {

    public static void init() {
        casingRecipe();
        controllerRecipe();
    }

    // -------------------------------------------------------------------------
    // Subatomic Patterning Casing — UIV-grade lattice-imprinting shell.
    // -------------------------------------------------------------------------
    private static void casingRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 1),
                plate(Materials.Naquadria, 8),
                plate(Materials.Osmium, 2),
                ItemList.Electric_Piston_ZPM.get(2),
                foil(Materials.Osmium, 4))
            .fluidInputs(fluid("molten.solderingalloy", 288))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.SUBATOMIC_PATTERNING_CASING))
            .eut(TierEU.RECIPE_ZPM)
            .duration(10 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // -------------------------------------------------------------------------
    // Controller — assembly-line recipe (UIV), scanned from a UV hull.
    // -------------------------------------------------------------------------
    private static void controllerRecipe() {
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Hull_UV.get(1))
            .metadata(SCANNING, new Scanning(90 * SECONDS, TierEU.RECIPE_UIV))
            .itemInputs(
                ItemList.Hull_UV.get(24),
                new ItemStack(GTNHPPBlocks.CASINGS, 8, BlockGTNHPPCasings.SUBATOMIC_PATTERNING_CASING),
                new ItemStack(GTNHPPBlocks.CASINGS, 3, BlockGTNHPPCasings.SPC_CASING),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 24),
                ItemList.Field_Generator_UV.get(24),
                ItemList.Robot_Arm_UV.get(16),
                plate(Materials.Naquadria, 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 24),
                GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.SuperconductorUV, 16))
            .fluidInputs(fluid("molten.indalloy140", 18432), molten(Materials.Naquadria, 13824),
                molten(Materials.Naquadah, 4608))
            .itemOutputs(GTNHPPBlocks.SPU.getStackForm(1))
            .eut(TierEU.RECIPE_UIV)
            .duration(150 * SECONDS)
            .addTo(AssemblyLine);
    }
}
