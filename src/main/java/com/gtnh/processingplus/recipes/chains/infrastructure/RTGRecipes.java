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

public class RTGRecipes {

    public static void init() {
        alloy();
        betavoltaicCell();
        casingRecipe();
        controllerRecipe();
    }

    // -------------------------------------------------------------------------
    // Promethium Betavoltaic Alloy — Pm-147 dispersed into a gallium-arsenide matrix, forged in the
    // CRV from molten Promethium. The alloy's beta decay is what later drives the RTG.
    // -------------------------------------------------------------------------
    private static void alloy() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.GalliumArsenide, 8), circuit(11))
            .fluidInputs(
                molten(Materials.Promethium, 288),
                fluid(PrPMaterials.HBNLubricant, 1000),
                fluid(Materials.Argon, 4000))
            .itemOutputs(ingot(PrPMaterials.PromethiumBetavoltaicAlloy, 8))
            .duration(1200)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sCRVRecipes);
    }

    // -------------------------------------------------------------------------
    // Promethium Betavoltaic Cell — the RTG's fuel. Alloy plate sealed in an aluminium can with a
    // semiconductor junction. Each cell adds a fixed slice of EU/t to the RTG while it burns.
    // -------------------------------------------------------------------------
    private static void betavoltaicCell() {
        GTValues.RA.stdBuilder()
            .itemInputs(plate(PrPMaterials.PromethiumBetavoltaicAlloy, 1), foil(Materials.Aluminium, 2), circuit(1))
            .itemOutputs(GTNHPPItems.betavoltaicCell(1))
            .duration(200)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // -------------------------------------------------------------------------
    // Radioisotope Thermoelectric Casing — lead-shielded, alloy-faced.
    // -------------------------------------------------------------------------
    private static void casingRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 1),
                plate(Materials.Lead, 8),
                plate(PrPMaterials.PromethiumBetavoltaicAlloy, 1),
                ItemList.Electric_Piston_IV.get(2),
                foil(Materials.Tungsten, 4))
            .fluidInputs(fluid("molten.solderingalloy", 144))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.RADIOISOTOPE_CASING))
            .eut(TierEU.RECIPE_IV)
            .duration(10 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // -------------------------------------------------------------------------
    // Controller — assembly-line recipe (UV), scanned from a UV hull.
    // -------------------------------------------------------------------------
    private static void controllerRecipe() {
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Hull_UV.get(1))
            .metadata(SCANNING, new Scanning(60 * SECONDS, TierEU.RECIPE_UV))
            .itemInputs(
                ItemList.Hull_UV.get(8),
                new ItemStack(GTNHPPBlocks.CASINGS, 8, BlockGTNHPPCasings.RADIOISOTOPE_CASING),
                plate(PrPMaterials.PromethiumBetavoltaicAlloy, 16),
                plate(Materials.Lead, 64),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 8),
                ItemList.Electric_Pump_UV.get(16),
                ItemList.Electric_Piston_UV.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 8),
                item("wireGt01SuperconductorUV", 8))
            .fluidInputs(
                fluid("molten.indalloy140", 6912),
                molten(Materials.Lead, 4608),
                PrPMaterials.PromethiumBetavoltaicAlloy.getMolten(2304))
            .itemOutputs(GTNHPPBlocks.RTG.getStackForm(1))
            .eut(TierEU.RECIPE_UV)
            .duration(90 * SECONDS)
            .addTo(AssemblyLine);
    }
}
