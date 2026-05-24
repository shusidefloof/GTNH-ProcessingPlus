package com.gtnh.processingplus.recipes.chains.photoresist;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class UVPhotoresistRecipes {

    public static void init() {
        uvTinOxoAcetateCluster();
        uvErbiumTriflate();
        uvYtterbiumAcetate();
        uvTerbiumChloride();
        uvTerbiumAcetylacetonate();
        uvDysprosiumDopedCalciumFluoride();
        uvREDopedPhotoresistMatrix();
        uvUVBlend();
    }

    // =========================================================
    // UV — Tin Oxo-Acetate Cluster (EUV sensitizer precursor)
    // Sn + AceticAcid + O₂ → TinOxoAcetateCluster + H₂O
    // Light-isolated in SPC; tin organometallics are UV-sensitive
    // =========================================================
    private static void uvTinOxoAcetateCluster() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Tin, 1), circuit(1))
            .fluidInputs(fluid(Materials.AceticAcid, 2000), fluid(Materials.Oxygen, 2000))
            .fluidOutputs(fluid(PrPMaterials.TinOxoAcetateCluster, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
    }

    // =========================================================
    // UV — Erbium Triflate
    // Er + TriflicAcid + O₂ → ErbiumTriflate + H₂O
    // RE triflate dopant for photoactive matrix
    // =========================================================
    private static void uvErbiumTriflate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(mat("Erbium"), 2), circuit(2))
            .fluidInputs(fluid(PrPMaterials.TriflicAcid, 4000), fluid(Materials.Oxygen, 2000))
            .itemOutputs(dust(PrPMaterials.ErbiumTriflate, 4))
            .fluidOutputs(fluid(Materials.Water, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // UV — Ytterbium Acetate
    // Yb + AceticAcid → YtterbiumAcetate + H₂O
    // =========================================================
    private static void uvYtterbiumAcetate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(mat("Ytterbium"), 1), circuit(3))
            .fluidInputs(fluid(Materials.AceticAcid, 3000))
            .itemOutputs(dust(PrPMaterials.YtterbiumAcetate, 2))
            .fluidOutputs(fluid(Materials.Water, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // UV — Terbium Chloride
    // Tb + HCl → TerbiumChloride + H₂O
    // =========================================================
    private static void uvTerbiumChloride() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(mat("Terbium"), 1), circuit(4))
            .fluidInputs(fluid(Materials.HydrochloricAcid, 3000))
            .itemOutputs(dust(PrPMaterials.TerbiumChloride, 2))
            .fluidOutputs(fluid(Materials.Water, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // UV — Terbium Acetylacetonate
    // TerbiumChloride + Acetone + AceticAcid → TerbiumAcetylacetonate + HCl
    // Acetylacetonate ligand formed in situ
    // =========================================================
    private static void uvTerbiumAcetylacetonate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.TerbiumChloride, 1), circuit(5))
            .fluidInputs(fluid(Materials.Acetone, 1000), fluid(Materials.AceticAcid, 1000))
            .itemOutputs(dust(PrPMaterials.TerbiumAcetylacetonate, 2))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // UV — Dysprosium-Doped Calcium Fluoride (hot press sintering)
    // Dy + Ca + HF → CaF₂:Dy + H₂O
    // Argon atmosphere prevents oxide formation
    // =========================================================
    private static void uvDysprosiumDopedCalciumFluoride() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(mat("Dysprosium"), 1), dust(Materials.Calcium, 1))
            .fluidInputs(fluid(Materials.HydrofluoricAcid, 4000), fluid(Materials.Argon, 1000))
            .itemOutputs(dust(PrPMaterials.DysprosiumDopedCalciumFluoride, 2))
            .duration(120)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // =========================================================
    // UV — RE-Doped Photoresist Matrix (CIDC)
    // TinOxoAcetate + Er/Yb/Tb/Dy dopants + TriflicAcid → RE-Doped Matrix
    // Controlled Isotopic Doping Chamber assembles the rare-earth composite
    // =========================================================
    private static void uvREDopedPhotoresistMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                dust(PrPMaterials.ErbiumTriflate, 1),
                dust(PrPMaterials.YtterbiumAcetate, 1),
                dust(PrPMaterials.TerbiumAcetylacetonate, 1),
                dust(PrPMaterials.DysprosiumDopedCalciumFluoride, 1))
            .fluidInputs(fluid(PrPMaterials.TinOxoAcetateCluster, 1000), fluid(PrPMaterials.TriflicAcid, 500))
            .itemOutputs(dust(PrPMaterials.REDopedPhotoresistMatrix, 4))
            .duration(200)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sCIDCRecipes);
    }

    // =========================================================
    // UV — UV Photoresist blend (Mixer, circuit 15)
    // ZPM Photoresist + RE-Doped Matrix + PGMEA → UV Photoresist
    // =========================================================
    private static void uvUVBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.REDopedPhotoresistMatrix, 2), circuit(15))
            .fluidInputs(fluid(PrPMaterials.ZPMPhotoresist, 500), fluid(PrPMaterials.PGMEA, 500))
            .fluidOutputs(fluid(PrPMaterials.UVPhotoresist, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
    }
}
