package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

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

/**
 * High Temperature Reaction Furnace — controller craft recipe. The 2000K+ ceramic-synthesis
 * reactions are added separately; recipes go on {@code GTNHPPRecipeMaps.sHTRFRecipes} with a
 * {@code GTRecipeConstants.COIL_HEAT} metadata above 2000.
 */
public class HTRFRecipes {

    public static void init() {
        controllerRecipe();
    }

    // Controller — EV assembler recipe (project pattern, SiC + EV-tier components).
    private static void controllerRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                new ItemStack(GTNHPPBlocks.CASINGS, 4, BlockGTNHPPCasings.HTRF_CASING),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 4),
                ItemList.Field_Generator_EV.get(1),
                plate(PrPMaterials.SinteredSiliconCarbide, 4))
            .fluidInputs(molten(Materials.SolderingAlloy, 1152))
            .itemOutputs(GTNHPPBlocks.HTRF.getStackForm(1))
            .duration(1000)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.assemblerRecipes);
        GTValues.RA.stdBuilder()
            .itemInputs(
                plate(Materials.BlackSteel, 3),
                plate(Materials.Polytetrafluoroethylene, 3),
                item("frameGtBlackSteel", 1))
            .itemOutputs(
                new net.minecraft.item.ItemStack(
                    com.gtnh.processingplus.blocks.GTNHPPBlocks.CASINGS,
                    1,
                    BlockGTNHPPCasings.HTRF_REINFORCED_CASING))
            .duration(50)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.assemblerRecipes);
        GTValues.RA.stdBuilder()
            .itemInputs(
                doublePlate(PrPMaterials.SinteredSiliconCarbide, 6),
                gear(PrPMaterials.SinteredSiliconCarbide, 1),
                screw(PrPMaterials.SinteredSiliconCarbide, 2),
                item("frameGtBlackSteel", 1))
            .itemOutputs(
                new net.minecraft.item.ItemStack(
                    com.gtnh.processingplus.blocks.GTNHPPBlocks.CASINGS,
                    1,
                    com.gtnh.processingplus.blocks.BlockGTNHPPCasings.HTRF_CASING))
            .duration(50)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

}
