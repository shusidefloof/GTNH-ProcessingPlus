package com.gtnh.processingplus.recipes.chains.photoresist;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeConstants;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class UMVPhotoresistRecipes {

    public static void init() {
        umvShirabonTriflate();
        umvOsmiumPlasmaPrecursor();
        umvTachyonicPolymerBase();
        umvSpaceTimePolymerLattice();
        umvMagmatterStabilizer();
        umvNeutroniumCrystalMatrix();
        umvMoltenTungstenStellarAlloy();
        umvGravitonImprintedLattice();
        umvCosmicNeutroniumTriflate();
        umvRareEarthPlasmaBlend();
        umvStellarFieldMatrix();
        umvPhotonicStellarLayer();
        umvDimensionallyBoundMatrix();
        umvStellarPhotoresistPrecursor();
        umvPhotoresistMatrix();
        umvBlend();
    }

    // =========================================================
    // UMV — Shirabon Triflate
    // Shirabon + TriflicAcid → ShirabonTriflate + H₂O
    // Exotic triflate PAG; runs continuously through UMV blend
    // =========================================================
    private static void umvShirabonTriflate() {
        GTValues.RA.stdBuilder()
            .itemInputs(item("dustShirabon", 1), circuit(1))
            .fluidInputs(fluid(PrPMaterials.TriflicAcid, 2000))
            .fluidOutputs(fluid(PrPMaterials.ShirabonTriflate, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_UIV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // UMV — Osmium Plasma Precursor (FotG Helioflare — circuit 1)
    // Os plasma + TriflicAcid + N₂ → OsmiumPlasmaPrecursor dust
    // Stellar plasma quench-solidifies into reactive precursor powder
    // =========================================================
    private static void umvOsmiumPlasmaPrecursor() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(
                plasma(Materials.Osmium, 1000),
                fluid(PrPMaterials.TriflicAcid, 500),
                fluid(Materials.Nitrogen, 2000))
            .itemOutputs(dust(PrPMaterials.OsmiumPlasmaPrecursor, 2))
            .duration(120)
            .eut(TierEU.RECIPE_UMV)
            .addTo(GTNHPPRecipeMaps.sFotGRecipes);
    }

    // =========================================================
    // UMV — Tachyonic Polymer Base (FotG Heliofusion — circuit 3)
    // SpaceTime + Magmatter → TachyonicPolymerBase
    // Fusion-driven tachyonic polymer formation from spacetime fabric
    // =========================================================
    private static void umvTachyonicPolymerBase() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(molten(Materials.SpaceTime, 1000), molten(Materials.MagMatter, 500))
            .fluidOutputs(fluid(PrPMaterials.TachyonicPolymerBase, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_UMV)
            .addTo(GTNHPPRecipeMaps.sFotGRecipes);
    }

    // =========================================================
    // UMV — SpaceTime Polymer Lattice (QFT Tier 4)
    // TachyonicPolymerBase + ShirabonTriflate → SpaceTimePolymerLattice dust
    // =========================================================
    private static void umvSpaceTimePolymerLattice() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(PrPMaterials.TachyonicPolymerBase, 1000), fluid(PrPMaterials.ShirabonTriflate, 500))
            .itemOutputs(dust(PrPMaterials.SpaceTimePolymerLattice, 2))
            .duration(200)
            .eut(TierEU.RECIPE_UMV)
            .metadata(GTRecipeConstants.QFT_CATALYST, item("catalystTemporalHarmony", 0))
            .metadata(GTRecipeConstants.QFT_FOCUS_TIER, 4)
            .addTo(GTPPRecipeMaps.quantumForceTransformerRecipes);
    }

    // =========================================================
    // UMV — Magmatter Stabilizer (FotG Helioflux — circuit 4)
    // Magmatter + Cosmic Neutronium + TriflicAcid → MagmatterStabilizer
    // =========================================================
    private static void umvMagmatterStabilizer() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.CosmicNeutronium, 1), circuit(4))
            .fluidInputs(molten(Materials.MagMatter, 1000), fluid(PrPMaterials.TriflicAcid, 500))
            .fluidOutputs(fluid(PrPMaterials.MagmatterStabilizer, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_UMV)
            .addTo(GTNHPPRecipeMaps.sFotGRecipes);
    }

    // =========================================================
    // UMV — Neutronium Crystal Matrix (QFT Tier 4)
    // OsmiumPlasmaPrecursor + Cosmic Neutronium + Grade 7 Water → NeutroniumCrystalMatrix dust
    // =========================================================
    private static void umvNeutroniumCrystalMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.OsmiumPlasmaPrecursor, 1), dust(Materials.CosmicNeutronium, 1), circuit(2))
            .fluidInputs(fluid(Materials.Grade7PurifiedWater, 500))
            .itemOutputs(dust(PrPMaterials.NeutroniumCrystalMatrix, 2))
            .duration(200)
            .eut(TierEU.RECIPE_UMV)
            .metadata(GTRecipeConstants.QFT_CATALYST, item("catalystTemporalHarmony", 0))
            .metadata(GTRecipeConstants.QFT_FOCUS_TIER, 4)
            .addTo(GTPPRecipeMaps.quantumForceTransformerRecipes);
    }

    // =========================================================
    // UMV — Molten Tungsten Stellar Alloy (FotG Heliothermal — circuit 2)
    // W plasma + Osmium plasma + Argon → MoltenTungstenStellarAlloy
    // Stellar-temperature alloy formation
    // =========================================================
    private static void umvMoltenTungstenStellarAlloy() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(plasma(Materials.Tungsten, 1000), plasma(Materials.Osmium, 500), fluid(Materials.Argon, 2000))
            .fluidOutputs(fluid(PrPMaterials.MoltenTungstenStellarAlloy, 1000))
            .duration(160)
            .eut(TierEU.RECIPE_UMV)
            .addTo(GTNHPPRecipeMaps.sFotGRecipes);
    }

    // =========================================================
    // UMV — Graviton-Imprinted Lattice (SPU)
    // NeutroniumCrystalMatrix + Graviton Shards + SpaceTime + ShirabonTriflate → GravitonImprintedLattice dust
    // Graviton shards permanently imprint quantum geometry into the lattice
    // =========================================================
    private static void umvGravitonImprintedLattice() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.NeutroniumCrystalMatrix, 1), dust(Materials.GravitonShard, 1), circuit(1))
            .fluidInputs(molten(Materials.SpaceTime, 500), fluid(PrPMaterials.ShirabonTriflate, 250))
            .itemOutputs(dust(PrPMaterials.GravitonImprintedLattice, 2))
            .duration(300)
            .eut(TierEU.RECIPE_UMV)
            .addTo(GTNHPPRecipeMaps.sSPURecipes);
    }

    // =========================================================
    // UMV — Cosmic Neutronium Triflate (HPR)
    // Cosmic Neutronium + TriflicAcid + N₂ → CosmicNeutroniumTriflate + H₂O
    // =========================================================
    private static void umvCosmicNeutroniumTriflate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.CosmicNeutronium, 1), circuit(5))
            .fluidInputs(fluid(PrPMaterials.TriflicAcid, 3000), fluid(Materials.Nitrogen, 1000))
            .fluidOutputs(fluid(PrPMaterials.CosmicNeutroniumTriflate, 1000))
            .duration(120)
            .eut(TierEU.RECIPE_UMV)
            .addTo(GTNHPPRecipeMaps.sHPRRecipes);
    }

    // =========================================================
    // UMV — Rare Earth Plasma Blend (QFT Tier 4)
    // Eu plasma + Ce plasma + Nd plasma → RareEarthPlasmaBlend
    // Mixed RE plasma sensitizer for stellar photoresist
    // =========================================================
    private static void umvRareEarthPlasmaBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(
                plasma(Materials.Europium, 500),
                plasma(Materials.Cerium, 500),
                plasma(Materials.Neodymium, 500))
            .fluidOutputs(fluid(PrPMaterials.RareEarthPlasmaBlend, 1000))
            .duration(120)
            .eut(TierEU.RECIPE_UMV)
            .metadata(GTRecipeConstants.QFT_CATALYST, item("catalystTemporalHarmony", 0))
            .metadata(GTRecipeConstants.QFT_FOCUS_TIER, 4)
            .addTo(GTPPRecipeMaps.quantumForceTransformerRecipes);
    }

    // =========================================================
    // UMV — Stellar Field Matrix (QFT Tier 4)
    // GravitonImprintedLattice + MoltenTungstenStellarAlloy + MagmatterStabilizer → StellarFieldMatrix dust
    // =========================================================
    private static void umvStellarFieldMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.GravitonImprintedLattice, 1), circuit(4))
            .fluidInputs(
                fluid(PrPMaterials.MoltenTungstenStellarAlloy, 1000),
                fluid(PrPMaterials.MagmatterStabilizer, 500))
            .itemOutputs(dust(PrPMaterials.StellarFieldMatrix, 2))
            .duration(300)
            .eut(TierEU.RECIPE_UMV)
            .metadata(GTRecipeConstants.QFT_CATALYST, item("catalystTemporalHarmony", 0))
            .metadata(GTRecipeConstants.QFT_FOCUS_TIER, 4)
            .addTo(GTPPRecipeMaps.quantumForceTransformerRecipes);
    }

    // =========================================================
    // UMV — Photonic Stellar Layer (FotG Heliofusion — circuit 3)
    // StellarFieldMatrix + RareEarthPlasmaBlend + Os plasma → PhotonicStellarLayer dust
    // =========================================================
    private static void umvPhotonicStellarLayer() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.StellarFieldMatrix, 1), circuit(3))
            .fluidInputs(fluid(PrPMaterials.RareEarthPlasmaBlend, 1000), plasma(Materials.Osmium, 500))
            .itemOutputs(dust(PrPMaterials.PhotonicStellarLayer, 2))
            .duration(200)
            .eut(TierEU.RECIPE_UMV)
            .addTo(GTNHPPRecipeMaps.sFotGRecipes);
    }

    // =========================================================
    // UMV — Dimensionally Bound Matrix (QFT Tier 4)
    // PhotonicStellarLayer + SpaceTimePolymerLattice + Magmatter → DimensionallyBoundMatrix dust
    // =========================================================
    private static void umvDimensionallyBoundMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                dust(PrPMaterials.PhotonicStellarLayer, 1),
                dust(PrPMaterials.SpaceTimePolymerLattice, 1),
                circuit(5))
            .fluidInputs(molten(Materials.MagMatter, 500))
            .itemOutputs(dust(PrPMaterials.DimensionallyBoundMatrix, 2))
            .duration(400)
            .eut(TierEU.RECIPE_UMV)
            .metadata(GTRecipeConstants.QFT_CATALYST, item("catalystTemporalHarmony", 0))
            .metadata(GTRecipeConstants.QFT_FOCUS_TIER, 4)
            .addTo(GTPPRecipeMaps.quantumForceTransformerRecipes);
    }

    // =========================================================
    // UMV — Stellar Photoresist Precursor (SPU)
    // DimensionallyBoundMatrix + CosmicNeutroniumTriflate + Grade 7 Water → StellarPhotoresistPrecursor
    // =========================================================
    private static void umvStellarPhotoresistPrecursor() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.DimensionallyBoundMatrix, 1), circuit(2))
            .fluidInputs(fluid(PrPMaterials.CosmicNeutroniumTriflate, 1000), fluid(Materials.Grade7PurifiedWater, 250))
            .fluidOutputs(fluid(PrPMaterials.StellarPhotoresistPrecursor, 1000))
            .duration(300)
            .eut(TierEU.RECIPE_UMV)
            .addTo(GTNHPPRecipeMaps.sSPURecipes);
    }

    // =========================================================
    // UMV — UMV Photoresist Matrix (SPC — light-isolated)
    // StellarPhotoresistPrecursor + ShirabonTriflate → UMVPhotoresistMatrix
    // =========================================================
    private static void umvPhotoresistMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(6))
            .fluidInputs(
                fluid(PrPMaterials.StellarPhotoresistPrecursor, 1000),
                fluid(PrPMaterials.ShirabonTriflate, 500))
            .fluidOutputs(fluid(PrPMaterials.UMVPhotoresistMatrix, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_UMV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
    }

    // =========================================================
    // UMV — UMV Photoresist blend (Mixer, circuit 19)
    // UIV Photoresist + UMV Matrix + PGMEA + ShirabonTriflate → UMV Photoresist
    // =========================================================
    private static void umvBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(19))
            .fluidInputs(
                fluid(PrPMaterials.UIVPhotoresist, 500),
                fluid(PrPMaterials.UMVPhotoresistMatrix, 500),
                fluid(PrPMaterials.PGMEA, 250),
                fluid(PrPMaterials.ShirabonTriflate, 100))
            .fluidOutputs(fluid(PrPMaterials.UMVPhotoresist, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_UMV)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
    }
}
