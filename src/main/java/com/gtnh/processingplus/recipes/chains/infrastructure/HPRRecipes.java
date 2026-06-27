package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeConstants.AssemblyLine;
import static gregtech.api.util.GTRecipeConstants.RESEARCH_ITEM;
import static gregtech.api.util.GTRecipeConstants.SCANNING;

import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.items.GTNHPPItems;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.recipe.Scanning;

public class HPRRecipes {

    public static void init() {
        casingRecipe();
        controllerRecipe();
        phaseSynchronizerRecipe();
        phaseDesynchronizerRecipe();
    }

    // -------------------------------------------------------------------------
    // Hybrid Phase Casing — energetic dual-phase reactor shell.
    // -------------------------------------------------------------------------
    private static void casingRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.NaquadahAlloy, 1),
                plate(Materials.Naquadah, 8),
                plate(Materials.Iridium, 2),
                ItemList.Electric_Piston_LuV.get(2),
                foil(Materials.Iridium, 4))
            .fluidInputs(fluid("molten.solderingalloy", 288))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.HYBRID_PHASE_CASING))
            .eut(TierEU.RECIPE_LuV)
            .duration(10 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // -------------------------------------------------------------------------
    // Phase Synchronizer — consumed by the HPR to retune its resonance field.
    // 1 consumed per frequency step jumped; large jumps also reduce output.
    // -------------------------------------------------------------------------
    private static void phaseSynchronizerRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Field_Generator_ZPM.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 2),
                plate(Materials.NaquadahAlloy, 4),
                foil(Materials.Iridium, 4))
            .fluidInputs(fluid("molten.solderingalloy", 576))
            .itemOutputs(GTNHPPItems.phaseSynchronizer(4))
            .eut(TierEU.RECIPE_ZPM)
            .duration(30 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // -------------------------------------------------------------------------
    // Phase Desynchronizer — deliberately collapses the resonance field by 1 step.
    // Cheaper than a Synchronizer; useful for downshifting without burning fatigue.
    // -------------------------------------------------------------------------
    private static void phaseDesynchronizerRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Field_Generator_LuV.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 2),
                plate(Materials.NaquadahAlloy, 2),
                foil(Materials.Naquadah, 4))
            .fluidInputs(fluid("molten.solderingalloy", 288))
            .itemOutputs(GTNHPPItems.phaseDesynchronizer(4))
            .eut(TierEU.RECIPE_LuV)
            .duration(20 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // -------------------------------------------------------------------------
    // Controller — assembly-line recipe (UHV), scanned from a UV hull.
    // -------------------------------------------------------------------------
    private static void controllerRecipe() {
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Hull_UV.get(1))
            .metadata(SCANNING, new Scanning(60 * SECONDS, TierEU.RECIPE_UHV))
            .itemInputs(
                ItemList.Hull_UV.get(16),
                new ItemStack(GTNHPPBlocks.CASINGS, 8, BlockGTNHPPCasings.HYBRID_PHASE_CASING),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.NaquadahAlloy, 16),
                ItemList.Field_Generator_UV.get(16),
                ItemList.Electric_Pump_UV.get(16),
                plate(Materials.Naquadah, 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 16),
                item("wireGt01SuperconductorUV", 8))
            .fluidInputs(
                fluid("molten.indalloy140", 11520),
                molten(Materials.Naquadah, 9216),
                molten(Materials.Naquadria, 2304))
            .itemOutputs(GTNHPPBlocks.HPR.getStackForm(1))
            .eut(TierEU.RECIPE_UHV)
            .duration(120 * SECONDS)
            .addTo(AssemblyLine);
    }
}
