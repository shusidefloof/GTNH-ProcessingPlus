package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;

public class BOFRecipes {

    public static void init() {
        casingOrBlockRecipe();
        limedConversion();
        limestoneConversion();
        dolomiteConversion();
        slagSeparation();
        slagResidueSift();
    }

    private static void casingOrBlockRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1),
                plate(Materials.StainlessSteel, 4),
                plate(Materials.BorosilicateGlass, 2),
                plate(Materials.Copper, 1),
                circuit(11))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 4, BlockGTNHPPCasings.BOF_CASING))
            .duration(300)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.assemblerRecipes);

        ItemStack controller = new ItemStack(GregTechAPI.sBlockMachines, 1, 31510);
        GTValues.RA.stdBuilder()
            .itemInputs(
                plate(Materials.Aluminium, 2),
                item("circuitGood", 2),
                item("pipeLargePotin", 1),
                ItemList.Hull_MV.get(1),
                ItemList.Electric_Pump_MV.get(1))
            .itemOutputs(controller)
            .duration(10 * 20)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.assemblerRecipes);

        GameRegistry.addShapedRecipe(
            controller,
            "SHS",
            "CAC",
            "SPS",
            'S',
            plate(Materials.Aluminium, 1),
            'C',
            item("circuitGood", 1),
            'H',
            item("pipeHugePotin", 1),
            'A',
            ItemList.Hull_MV.get(1),
            'P',
            ItemList.Electric_Pump_MV.get(1));
    }

    private static void limedConversion() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Iron, 8), dust(Materials.Calcium, 2), circuit(2))
            .fluidInputs(fluid(Materials.Oxygen, 800))
            .itemOutputs(ingot(Materials.Steel, 16), dust(PrPMaterials.BOFSlag, 1))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 800))
            .duration(5 * 8 * 20)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNHPPRecipeMaps.sBOFRecipes);
    }

    private static void limestoneConversion() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Iron, 8), dust(Materials.Calcite, 4), circuit(2))
            .fluidInputs(fluid(Materials.Oxygen, 1600))
            .itemOutputs(ingot(Materials.Steel, 20), dust(PrPMaterials.BOFSlag, 2))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 1600))
            .duration(4 * 8 * 20)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNHPPRecipeMaps.sBOFRecipes);
    }

    private static void dolomiteConversion() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Iron, 8), dust(Materials.Dolomite, 8), circuit(2))
            .fluidInputs(fluid(Materials.Oxygen, 1800))
            .itemOutputs(ingot(Materials.Steel, 24), dust(PrPMaterials.BOFSlag, 4))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 1600), fluid(Materials.CarbonMonoxide, 400))
            .duration(3 * 8 * 20)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNHPPRecipeMaps.sBOFRecipes);
    }

    private static void slagSeparation() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.BOFSlag, 5))
            .itemOutputs(dust(PrPMaterials.SlagResidue, 2), dust(Materials.Iron, 2), dust(Materials.Manganese, 1))
            .duration(3 * 20)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.centrifugeRecipes);
    }

    private static void slagResidueSift() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.SlagResidue, 4))
            .itemOutputs(
                dust(Materials.Quicklime, 2),
                dust(Materials.SiliconDioxide, 1),
                dust(Materials.Magnesia, 1),
                dust(Materials.Aluminiumoxide, 1))
            .outputChances(10000, 6000, 4000, 1000)
            .duration(3 * 20)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.sifterRecipes);
    }
}
