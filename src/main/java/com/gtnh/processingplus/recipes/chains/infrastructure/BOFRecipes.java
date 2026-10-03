package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeBuilder.TICKS;

import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
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
                plate(Materials.Copper, 1))
            .circuit(11)
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.BOF_CASING))
            .duration(2 * SECONDS + 10 * TICKS)
            .eut(TierEU.RECIPE_LV / 2)
            .addTo(RecipeMaps.assemblerRecipes);

        // Controller is hand-crafted only, like GT's own multiblock controllers.
        GTModHandler.addCraftingRecipe(
            GTNHPPBlocks.BOF.getStackForm(1),
            GTModHandler.RecipeBits.BITS | GTModHandler.RecipeBits.DO_NOT_CHECK_FOR_COLLISIONS,
            new Object[] { "SHS", "CAC", "SPS", 'S', OrePrefixes.plate.get(Materials.Aluminium), 'C',
                OrePrefixes.circuit.get(Materials.MV), 'H', "pipeHugePotin", 'A', ItemList.Hull_MV, 'P',
                ItemList.Electric_Pump_MV });
    }

    // Fe + Ca + O₂ → steel + BOF slag + CO₂ (lime flux)
    private static void limedConversion() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Iron, 16), dust(Materials.Calcium, 2))
            .circuit(2)
            .fluidInputs(fluid(Materials.Oxygen, 1000))
            .itemOutputs(ingot(Materials.Steel, 16), dust(PrPMaterials.BOFSlag, 5))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 1000))
            .duration(40 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNHPPRecipeMaps.sBOFRecipes);
    }

    // Fe + CaCO₃ + O₂ → steel + BOF slag + CO₂ (limestone flux)
    private static void limestoneConversion() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Iron, 20), dust(Materials.Calcite, 4))
            .circuit(2)
            .fluidInputs(fluid(Materials.Oxygen, 2000))
            .itemOutputs(ingot(Materials.Steel, 20), dust(PrPMaterials.BOFSlag, 5))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 2000))
            .duration(32 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNHPPRecipeMaps.sBOFRecipes);
    }

    // Fe + CaMg(CO₃)₂ + O₂ → steel + BOF slag + CO₂ + CO (dolomite flux, partial combustion)
    private static void dolomiteConversion() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Iron, 32), dust(Materials.Dolomite, 11))
            .circuit(2)
            .fluidInputs(fluid(Materials.Oxygen, 3000))
            .itemOutputs(ingot(Materials.Steel, 32), dust(PrPMaterials.BOFSlag, 20))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 2000), fluid(Materials.CarbonMonoxide, 1000))
            .duration(32 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNHPPRecipeMaps.sBOFRecipes);
    }

    // Slag → iron + flux-bearing residue. Residue → Quicklime (sifter) → Calcium (electrolyzer) closes the lime-flux
    // loop.
    private static void slagSeparation() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.BOFSlag, 5))
            .itemOutputs(dust(PrPMaterials.SlagResidue, 4), dust(Materials.Iron, 1))
            .duration(3 * SECONDS)
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
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.sifterRecipes);
    }
}
