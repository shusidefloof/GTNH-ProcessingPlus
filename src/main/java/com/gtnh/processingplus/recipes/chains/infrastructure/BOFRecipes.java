package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import static com.gtnh.processingplus.items.Intermediate.*;
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
        slagAcidRefine();
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
            .fluidInputs(fluid(Materials.Oxygen, 200))
            .itemOutputs(ingot(Materials.Steel, 10), dust(PrPMaterials.BOFSlag, 1))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 800))
            .duration(5 * 20)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNHPPRecipeMaps.sBOFRecipes);
    }

    private static void limestoneConversion() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Iron, 8), dust(Materials.Calcite, 4), circuit(2))
            .fluidInputs(fluid(Materials.Oxygen, 150))
            .itemOutputs(ingot(Materials.Steel, 14), dust(PrPMaterials.BOFSlag, 2))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 1600))
            .duration(4 * 20)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNHPPRecipeMaps.sBOFRecipes);
    }

    private static void dolomiteConversion() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Iron, 8), dust(Materials.Dolomite, 8), circuit(2))
            .fluidInputs(fluid(Materials.Oxygen, 125))
            .itemOutputs(ingot(Materials.Steel, 16), dust(PrPMaterials.BOFSlag, 4))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 1600), fluid(Materials.CarbonMonoxide, 400))
            .duration(3 * 20)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNHPPRecipeMaps.sBOFRecipes);
    }

    private static void slagSeparation() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.BOFSlag, 4))
            .itemOutputs(dust(Materials.Iron, 2), dust(Materials.Manganese, 1), dust(PrPMaterials.SlagResidue, 8))
            .duration(3 * 20)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.centrifugeRecipes);
    }

    private static void slagAcidRefine() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.SlagResidue, 4))
            .fluidInputs(fluid(Materials.SulfuricAcid, 1000))
            .itemOutputs(dust(Materials.Calcium, 2), dust(Materials.Phosphorus, 1))
            .fluidOutputs(fluid(Materials.DilutedSulfuricAcid, 1000))
            .duration(2 * 20)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.chemicalBathRecipes);
    }
}
