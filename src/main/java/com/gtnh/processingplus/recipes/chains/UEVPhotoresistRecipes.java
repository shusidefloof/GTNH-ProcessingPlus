package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.material.ELEMENT;
import gtPlusPlus.core.material.MaterialsElements;

public class UEVPhotoresistRecipes {

    public static void init() {
        uevTengamTriflate();
        uevActivatedNaquadria();
        uevHypogenQuantumMatrix();
        uevFermiumTriflate();
        uevQuantumPrimedIntermediate();
        uevBeamActivation();
        uevNaquadriaLoaded();
        uevQuantumCascadeMatrix();
        uevPurification();
        uevBlend();
    }

    // =========================================================
    // UEV — Tengam Triflate
    // Tengam + TriflicAcid → TengamTriflate + H₂O
    // Exotic triflate salt; runs continuously through UMV
    // =========================================================
    private static void uevTengamTriflate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.TengamPurified, 1), circuit(1))
            .fluidInputs(fluid(PrPMaterials.TriflicAcid, 2000))
            .fluidOutputs(fluid(PrPMaterials.TengamTriflate, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // UEV — Activated Naquadria Fluid
    // Naquadria + HF + TriflicAcid → ActivatedNaquadriaFluid + HCl
    // HTRF fluoride activation — highly reactive naquadria matrix
    // =========================================================
    private static void uevActivatedNaquadria() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Naquadria, 1), circuit(2))
            .fluidInputs(fluid(Materials.HydrofluoricAcid, 2000), fluid(PrPMaterials.TriflicAcid, 500))
            .fluidOutputs(fluid(PrPMaterials.ActivatedNaquadriaFluid, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // UEV — Hypogen Quantum Matrix
    // Hypogen + ActivatedNaquadriaFluid → HypogenQuantumMatrix
    // HPR — plasma/liquid interface drives quantum-coherent crosslinking
    // =========================================================
    private static void uevHypogenQuantumMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(
                fluid(MaterialsElements.STANDALONE.HYPOGEN, 1000),
                fluid(PrPMaterials.ActivatedNaquadriaFluid, 1000))
            .fluidOutputs(fluid(PrPMaterials.HypogenQuantumMatrix, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_UEV)
            .addTo(GTNHPPRecipeMaps.sHPRRecipes);
    }

    // =========================================================
    // UEV — Fermium Triflate
    // Fermium + TriflicAcid → FermiumTriflate + H₂O
    // Radioactive triflate dopant
    // =========================================================
    private static void uevFermiumTriflate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(ELEMENT.getInstance().FERMIUM, 1), circuit(4))
            .fluidInputs(fluid(PrPMaterials.TriflicAcid, 2000))
            .fluidOutputs(fluid(PrPMaterials.FermiumTriflate, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // UEV — Quantum-Primed Intermediate (QFT Tier 1)
    // HypogenQuantumMatrix + FermiumTriflate + TengamTriflate → QuantumPrimedIntermediate
    // =========================================================
    private static void uevQuantumPrimedIntermediate() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(
                fluid(PrPMaterials.HypogenQuantumMatrix, 1000),
                fluid(PrPMaterials.FermiumTriflate, 500),
                fluid(PrPMaterials.TengamTriflate, 500))
            .fluidOutputs(fluid(PrPMaterials.QuantumPrimedIntermediate, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_UEV)
            .addTo(GTPPRecipeMaps.quantumForceTransformerRecipes);
    }

    // =========================================================
    // UEV — Beam Activation (Beamcrafter)
    // QuantumPrimedIntermediate → BeamActivatedIntermediate
    // High-energy photon beam restructures the quantum lattice
    // =========================================================
    private static void uevBeamActivation() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(PrPMaterials.QuantumPrimedIntermediate, 1000))
            .fluidOutputs(fluid(PrPMaterials.BeamActivatedIntermediate, 1000))
            .duration(120)
            .eut(TierEU.RECIPE_UEV)
            .addTo(GTNHPPRecipeMaps.sBeamcrafterRecipes);
    }

    // =========================================================
    // UEV — Naquadria-Loaded Intermediate (QFT Tier 1)
    // BeamActivatedIntermediate + ActivatedNaquadriaFluid → NaquadriaLoadedIntermediate
    // =========================================================
    private static void uevNaquadriaLoaded() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(
                fluid(PrPMaterials.BeamActivatedIntermediate, 1000),
                fluid(PrPMaterials.ActivatedNaquadriaFluid, 500))
            .fluidOutputs(fluid(PrPMaterials.NaquadriaLoadedIntermediate, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_UEV)
            .addTo(GTPPRecipeMaps.quantumForceTransformerRecipes);
    }

    // =========================================================
    // UEV — Quantum Cascade Matrix (QFT Tier 1)
    // NaquadriaLoadedIntermediate + TengamTriflate → QuantumCascadeMatrix
    // =========================================================
    private static void uevQuantumCascadeMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(fluid(PrPMaterials.NaquadriaLoadedIntermediate, 1000), fluid(PrPMaterials.TengamTriflate, 500))
            .fluidOutputs(fluid(PrPMaterials.QuantumCascadeMatrix, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_UEV)
            .addTo(GTPPRecipeMaps.quantumForceTransformerRecipes);
    }

    // =========================================================
    // UEV — Purified Quantum Cascade Matrix (SPC — light-isolated)
    // QuantumCascadeMatrix + Grade 7 Water → PurifiedQuantumCascadeMatrix + H₂O
    // =========================================================
    private static void uevPurification() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(5))
            .fluidInputs(fluid(PrPMaterials.QuantumCascadeMatrix, 1000), fluid(Materials.Grade7PurifiedWater, 500))
            .fluidOutputs(fluid(PrPMaterials.PurifiedQuantumCascadeMatrix, 1000))
            .duration(120)
            .eut(TierEU.RECIPE_UEV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
    }

    // =========================================================
    // UEV — UEV Photoresist blend (Mixer, circuit 17)
    // UHV Photoresist + PurifiedQCM + PGMEA + TriflicAcid → UEV Photoresist
    // =========================================================
    private static void uevBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(17))
            .fluidInputs(
                fluid(PrPMaterials.UHVPhotoresist, 500),
                fluid(PrPMaterials.PurifiedQuantumCascadeMatrix, 500),
                fluid(PrPMaterials.PGMEA, 250),
                fluid(PrPMaterials.TriflicAcid, 100))
            .fluidOutputs(fluid(PrPMaterials.UEVPhotoresist, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_UEV)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
    }
}
