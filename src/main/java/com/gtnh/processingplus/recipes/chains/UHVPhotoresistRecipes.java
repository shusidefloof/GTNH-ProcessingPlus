package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.material.MaterialMisc;

public class UHVPhotoresistRecipes {

    public static void init() {
        uhvBioRefinedIntermediate();
        uhvRadoxXenoxeneMatrix();
        uhvLivingSolderAcetate();
        uhvPhotoresistMatrix();
        uhvBlend();
    }

    // =========================================================
    // UHV — Bio-Refined Intermediate
    // Mutagen + Unknown Liquid → BioRefinedIntermediate
    // HPR simultaneous liquid/plasma chemistry unlocks exotic bio-matrix
    // =========================================================
    private static void uhvBioRefinedIntermediate() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid("mutagen", 1000), fluid(Materials.Xenoxene, 500))
            .fluidOutputs(fluid(PrPMaterials.BioRefinedIntermediate, 1000))
            .duration(120)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHPRRecipes);
    }

    // =========================================================
    // UHV — Radox-Xenoxene Matrix
    // BioRefinedIntermediate + Radox Polymer + Xenoxene → RadoxXenoxeneMatrix
    // HPR — high-pressure plasma conditions force exotic polymer crosslinking
    // =========================================================
    private static void uhvRadoxXenoxeneMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(
                fluid(PrPMaterials.BioRefinedIntermediate, 1000),
                molten(Materials.RadoxPolymer, 500),
                fluid(Materials.Xenoxene, 500))
            .fluidOutputs(fluid(PrPMaterials.RadoxXenoxeneMatrix, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sHPRRecipes);
    }

    // =========================================================
    // UHV — Living Solder Acetate
    // Living Solder + AceticAcid → LivingSolderAcetate + H₂O
    // Acetate ligand substitution — stabilizes living solder for photoresist use
    // =========================================================
    private static void uhvLivingSolderAcetate() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(MaterialMisc.MUTATED_LIVING_SOLDER, 1000), fluid(Materials.AceticAcid, 500))
            .fluidOutputs(fluid(PrPMaterials.LivingSolderAcetate, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // UHV — UHV Photoresist Matrix
    // RadoxXenoxeneMatrix + LivingSolderAcetate + Grade 7 Water → UHVPhotoresistMatrix
    // HPR — plasma-assisted matrix assembly under ultra-pure conditions
    // =========================================================
    private static void uhvPhotoresistMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(
                fluid(PrPMaterials.RadoxXenoxeneMatrix, 1000),
                fluid(PrPMaterials.LivingSolderAcetate, 500),
                fluid(Materials.Grade7PurifiedWater, 500))
            .fluidOutputs(fluid(PrPMaterials.UHVPhotoresistMatrix, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sHPRRecipes);
    }

    // =========================================================
    // UHV — UHV Photoresist blend (Mixer, circuit 16)
    // UV Photoresist + UHV Matrix + PGMEA + Triflic Acid → UHV Photoresist
    // =========================================================
    private static void uhvBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(16))
            .fluidInputs(
                fluid(PrPMaterials.UVPhotoresist, 500),
                fluid(PrPMaterials.UHVPhotoresistMatrix, 500),
                fluid(PrPMaterials.PGMEA, 250),
                fluid(PrPMaterials.TriflicAcid, 100))
            .fluidOutputs(fluid(PrPMaterials.UHVPhotoresist, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
    }
}
