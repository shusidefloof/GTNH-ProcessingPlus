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

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.recipe.Scanning;

public class CIDCRecipes {

    public static void init() {
        casingRecipe();
        controllerRecipe();
    }

    // -------------------------------------------------------------------------
    // Isotopic Doping Casing — clean precision shell.
    // -------------------------------------------------------------------------
    private static void casingRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Iridium, 1),
                densePlate(PrPMaterials.Unobtanium, 6),
                plate(Materials.Duranium, 2),
                ItemList.Electric_Piston_LuV.get(2),
                foil(Materials.Tin, 4))
            .fluidInputs(fluid("molten.solderingalloy", 1440))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.ISOTOPIC_DOPING_CASING))
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
                new ItemStack(GTNHPPBlocks.CASINGS, 8, BlockGTNHPPCasings.ISOTOPIC_DOPING_CASING),
                new ItemStack(GTNHPPBlocks.CASINGS, 3, BlockGTNHPPCasings.SPC_CASING),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 8),
                ItemList.Emitter_UV.get(16),
                plate(Materials.Duranium, 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 8),
                item("wireGt01SuperconductorUV", 8))
            .fluidInputs(
                fluid("molten.indalloy140", 6912),
                molten(Materials.Tin, 4608),
                fluid(PrPMaterials.TriflicAcid, 4000))
            .itemOutputs(GTNHPPBlocks.CIDC.getStackForm(1))
            .eut(TierEU.RECIPE_UV)
            .duration(90 * SECONDS)
            .addTo(AssemblyLine);
    }
}
