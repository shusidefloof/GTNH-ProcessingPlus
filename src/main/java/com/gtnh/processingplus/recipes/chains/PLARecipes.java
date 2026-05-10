package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GTRecipeConstants;

public class PLARecipes {

    public static void init() {
        step1_LacticAcidSynthesis();
        step2_LactideFormation();
        step3_Polymerization();
    }

    // =========================================================
    // 1. Ethanol + H2SO4 → Lactic Acid (LuV hook)
    // =========================================================
    private static void step1_LacticAcidSynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(Materials.Ethanol, 1000), fluid(Materials.SulfuricAcid, 200))
            .fluidOutputs(fluid(PrPMaterials.LacticAcid, 1000), fluid(Materials.Water, 500))
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================
    // 2. 2x Lactic Acid → Lactide + Water (ZPM vacuum dehydration)
    // =========================================================
    private static void step2_LactideFormation() {

        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.LacticAcid, 2000))
            .itemOutputs(dust(PrPMaterials.Lactide, 2))
            .fluidOutputs(fluid(Materials.Water, 500))
            .duration(800)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);
    }

    // =========================================================
    // 3. Lactide + Sn catalyst → PLA pellets (ZPM PCV)
    // =========================================================
    private static void step3_Polymerization() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.Lactide, 4), dust(Materials.Tin, 1))
            .itemOutputs(dust(PrPMaterials.PolylacticAcid, 4))
            .duration(1600)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);
    }
}
