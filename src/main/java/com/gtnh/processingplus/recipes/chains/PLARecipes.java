package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

public class PLARecipes {

    public static void init() {
        addPLAChain();
    }

    private static void addPLAChain() {

        // =========================================================
        // STEP 1: ETHANOL → LACTIC ACID (LUV fermentation route)
        // =========================================================
        // GTValues.RA.stdBuilder()
        // .itemInputs(circuit(2))
        // .fluidInputs(
        // fluid(Materials.Ethanol, 1000),
        // fluid(Materials.SulfuricAcid, 100)
        // )
        // .itemOutputs(GTNHPPItemList.LacticAcid.get(2))
        // .fluidOutputs(fluid(Materials.Water, 500))
        // .duration(400)
        // .eut(TierEU.RECIPE_LuV)
        // .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // =========================================================
        // STEP 2: LACTIC ACID → LACTIDE (ZPM dehydration)
        // =========================================================
        // GTValues.RA.stdBuilder()
        // .itemInputs(GTNHPPItemList.LacticAcid.get(4))
        // .fluidOutputs(fluid(Materials.Water, 2000))
        // .itemOutputs(GTNHPPItemList.Lactide.get(2))
        // .duration(800)
        // .eut(TierEU.RECIPE_ZPM)
        // .addTo(GTNHPPRecipeMaps.sPCVRecipes);

        // =========================================================
        // STEP 3: LACTIDE → PLA POLYMER (Sn catalysis)
        // =========================================================
        // GTValues.RA.stdBuilder()
        // .itemInputs(
        // GTNHPPItemList.Lactide.get(4),
        // dust(Materials.Tin, 1)
        // )
        // .itemOutputs(ingot(PPMaterials.PolylacticAcid, 4))
        // .duration(1600)
        // .eut(TierEU.RECIPE_ZPM)
        // .addTo(GTNHPPRecipeMaps.sPCVRecipes);
    }
}
