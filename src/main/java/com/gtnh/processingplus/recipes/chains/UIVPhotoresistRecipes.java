package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class UIVPhotoresistRecipes {

    public static void init() {
        uivStabilizedQGPMatrix();
        uivTranscendentQGPLattice();
        uivCreonTriflate();
        uivQuantumFieldImprintedIntermediate();
        uivPhotoresistMatrix();
        uivBlend();
    }

    // =========================================================
    // UIV — Stabilized QGP Matrix (SPU)
    // SpaceTime + H plasma → StabilizedQGPMatrix
    // Quark-gluon plasma stabilized via quantum lattice imprinting
    // =========================================================
    private static void uivStabilizedQGPMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(molten(Materials.SpaceTime, 1000), plasma(Materials.Hydrogen, 4000))
            .fluidOutputs(fluid(PrPMaterials.StabilizedQGPMatrix, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_UIV)
            .addTo(GTNHPPRecipeMaps.sSPURecipes);
    }

    // =========================================================
    // UIV — Transcendent QGP Lattice (SPU)
    // StabilizedQGPMatrix + TengamTriflate + Transcendent Metal → TranscendentQGPLattice
    // =========================================================
    private static void uivTranscendentQGPLattice() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.TranscendentMetal, 1), circuit(2))
            .fluidInputs(fluid(PrPMaterials.StabilizedQGPMatrix, 1000), fluid(PrPMaterials.TengamTriflate, 500))
            .fluidOutputs(fluid(PrPMaterials.TranscendentQGPLattice, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_UIV)
            .addTo(GTNHPPRecipeMaps.sSPURecipes);
    }

    // =========================================================
    // UIV — Creon Triflate
    // Creon + TriflicAcid + N₂ → CreonTriflate + H₂O
    // HPR — plasma conditions required for Creon dissolution
    // Runs continuously through UMV alongside PGMEA and TriflicAcid
    // =========================================================
    private static void uivCreonTriflate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Creon, 1), circuit(3))
            .fluidInputs(fluid(PrPMaterials.TriflicAcid, 2000), fluid(Materials.Nitrogen, 1000))
            .fluidOutputs(fluid(PrPMaterials.CreonTriflate, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_UEV)
            .addTo(GTNHPPRecipeMaps.sHPRRecipes);
    }

    // =========================================================
    // UIV — Quantum Field-Imprinted Intermediate (SPU)
    // TranscendentQGPLattice + CreonTriflate + Graviton Shards + Grade 7 Water → QFII
    // =========================================================
    private static void uivQuantumFieldImprintedIntermediate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.GravitonShard, 1), circuit(4))
            .fluidInputs(
                fluid(PrPMaterials.TranscendentQGPLattice, 1000),
                fluid(PrPMaterials.CreonTriflate, 500),
                fluid(Materials.Grade7PurifiedWater, 250))
            .fluidOutputs(fluid(PrPMaterials.QuantumFieldImprintedIntermediate, 1000))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(GTNHPPRecipeMaps.sSPURecipes);
    }

    // =========================================================
    // UIV — UIV Photoresist Matrix (SPC — light-isolated)
    // QuantumFieldImprintedIntermediate + Grade 7 Water → UIVPhotoresistMatrix + H₂O
    // =========================================================
    private static void uivPhotoresistMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(5))
            .fluidInputs(
                fluid(PrPMaterials.QuantumFieldImprintedIntermediate, 1000),
                fluid(Materials.Grade7PurifiedWater, 500))
            .fluidOutputs(fluid(PrPMaterials.UIVPhotoresistMatrix, 1000))
            .duration(120)
            .eut(TierEU.RECIPE_UIV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
    }

    // =========================================================
    // UIV — UIV Photoresist blend (Mixer, circuit 18)
    // UEV Photoresist + UIV Matrix + PGMEA + CreonTriflate → UIV Photoresist
    // =========================================================
    private static void uivBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(18))
            .fluidInputs(
                fluid(PrPMaterials.UEVPhotoresist, 500),
                fluid(PrPMaterials.UIVPhotoresistMatrix, 500),
                fluid(PrPMaterials.PGMEA, 250),
                fluid(PrPMaterials.CreonTriflate, 100))
            .fluidOutputs(fluid(PrPMaterials.UIVPhotoresist, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_UIV)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
    }
}
