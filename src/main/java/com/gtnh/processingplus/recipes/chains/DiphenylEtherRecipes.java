package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeConstants;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class DiphenylEtherRecipes {

    public static void init() {
        synthesis();
        hotDrawSpinning();
    }

    // =========================================================
    // Synthesis — Phenol dehydration (Williamson ether synthesis)
    // 2 C₆H₅OH --H₂SO₄(cat)--> (C₆H₅)₂O + H₂O
    // H₂SO₄ is catalytic — 100 mB drives 2000 mB phenol.
    // =========================================================
    private static void synthesis() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(Materials.Phenol, 2000), fluid(Materials.SulfuricAcid, 100))
            .fluidOutputs(fluid(PrPMaterials.DiphenylEther, 1000), fluid(Materials.Water, 1000))
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // Enhanced PAN wet spinning — DiphenylEther hot-draw bath
    // DPE (bp 258 °C) acts as a high-boiling draw bath medium,
    // orienting nascent PAN chains as they exit the spinneret.
    // Better chain alignment = +25% fiber yield vs water-only bath.
    // ~80% DPE recovered for reuse.
    // =========================================================
    private static void hotDrawSpinning() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(
                fluid(PrPMaterials.PolyacrylonitrileSolution, 1000),
                fluid(PrPMaterials.DiphenylEther, 500),
                fluid(Materials.Water, 3000))
            .itemOutputs(dust(PrPMaterials.Polyacrylonitrile, 5))
            .fluidOutputs(fluid(PrPMaterials.DilutedNMP, 1500), fluid(PrPMaterials.DiphenylEther, 400))
            .duration(600)
            .eut(TierEU.RECIPE_EV)
            .metadata(GTRecipeConstants.CHEMPLANT_CASING_TIER, 4)
            .addTo(GTPPRecipeMaps.chemicalPlantRecipes);
    }
}
