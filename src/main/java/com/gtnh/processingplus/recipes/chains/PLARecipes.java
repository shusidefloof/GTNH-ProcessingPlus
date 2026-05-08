package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.GTNHPPRecipeHelper.*;

import com.gtnh.processingplus.items.GTNHPPItemList;
import com.gtnh.processingplus.materials.GTNHPPMaterials;

import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;

public class PLARecipes {

    public static void init() {
        addPLAChain();
    }

    private static void addPLAChain() {

        // =========================================================
        // STEP 1: ETHANOL → LACTIC ACID (LUV fermentation route)
        // =========================================================
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(
                Materials.Ethanol.getFluid(1000),
                Materials.SulfuricAcid.getFluid(100)
            )
            .itemOutputs(GTNHPPItemList.LacticAcid.get(2))
            .fluidOutputs(Materials.Water.getFluid(500))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // =========================================================
        // STEP 2: LACTIC ACID → LACTIDE (ZPM dehydration)
        // =========================================================
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPItemList.LacticAcid.get(4))
            .fluidOutputs(Materials.Water.getFluid(2000))
            .itemOutputs(GTNHPPItemList.Lactide.get(2))
            .duration(800)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);

        // =========================================================
        // STEP 3: LACTIDE → PLA POLYMER (Sn catalysis)
        // =========================================================
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTNHPPItemList.Lactide.get(4),
                Materials.Tin.getDust(1)
            )
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.ingot, GTNHPPMaterials.PolylacticAcid, 4)
            )
            .duration(1600)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);

    }
}
