package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class ZPMPhotoresistRecipes {

    public static void init() {
        zpmHexafluoroacetone();
        zpmHFIMAMonomer();
        zpmGBLMAMonomer();
        zpmHAdMAMonomer();
        zpmArFCopolymerResin();
        zpmZPMBlend();
    }

    // =========================================================
    // ZPM — Hexafluoroacetone
    // 2 CHF₃ + ½O₂ → (CF₃)₂CO + H₂O
    // Trifluoromethane reused from LuV Triflic Acid sub-chain
    // =========================================================
    private static void zpmHexafluoroacetone() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(PrPMaterials.Trifluoromethane, 2000), fluid(Materials.Oxygen, 1000))
            .fluidOutputs(fluid(PrPMaterials.Hexafluoroacetone, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // ZPM — HFIMA Monomer (hexafluoroisopropyl methacrylate)
    // (CF₃)₂CO + MethacrylicAcid → HFIMA + H₂O
    // MethacrylicAcid reused from LuV chain
    // =========================================================
    private static void zpmHFIMAMonomer() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(PrPMaterials.Hexafluoroacetone, 1000), fluid(PrPMaterials.MethacrylicAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.HFIMAMonomer, 1000))
            .duration(120)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // ZPM — GBLMA Monomer (gamma-butyrolactone methacrylate)
    // GBL + MethacrylicAcid → GBLMA + H₂O
    // GBL = solvent chemistry gate
    // =========================================================
    private static void zpmGBLMAMonomer() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(Materials.GammaButyrolactone, 1000), fluid(PrPMaterials.MethacrylicAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.GBLMAMonomer, 1000))
            .duration(120)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // ZPM — HAdMA Monomer (hydroxy-adamantyl methacrylate)
    // Adamantol + Hexafluoroacetone + MethacrylicAcid → HAdMA + H₂O
    // Adamantol gate reused from LuV Naquadah processing
    // =========================================================
    private static void zpmHAdMAMonomer() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.Adamantol, 1), circuit(4))
            .fluidInputs(fluid(PrPMaterials.Hexafluoroacetone, 500), fluid(PrPMaterials.MethacrylicAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.HAdMAMonomer, 1000))
            .duration(150)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // ZPM — ArF Copolymer Resin
    // HFIMA + GBLMA + HAdMA + AIBN (cat, reused from LuV) + N₂ → ArF Copolymer Resin
    // Radical polymerization under inert atmosphere
    // =========================================================
    private static void zpmArFCopolymerResin() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.AIBN, 1), circuit(5))
            .fluidInputs(
                fluid(PrPMaterials.HFIMAMonomer, 1000),
                fluid(PrPMaterials.GBLMAMonomer, 1000),
                fluid(PrPMaterials.HAdMAMonomer, 1000),
                fluid(Materials.Nitrogen, 2000))
            .itemOutputs(dust(PrPMaterials.ArFCopolymerResin, 4))
            .duration(300)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // ZPM — ZPM Photoresist blend (Mixer, circuit 14)
    // LuV Photoresist + ArF Resin + Triphenylsulfonium Triflate + PGMEA → ZPM Photoresist
    // PGMEA and TriphenylsulfoniumTriflate run continuously from LuV through UMV
    // =========================================================
    private static void zpmZPMBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                dust(PrPMaterials.ArFCopolymerResin, 2),
                dust(PrPMaterials.TriphenylsulfoniumTriflate, 1),
                circuit(14))
            .fluidInputs(fluid(PrPMaterials.LuVPhotoresist, 500), fluid(PrPMaterials.PGMEA, 500))
            .fluidOutputs(fluid(PrPMaterials.ZPMPhotoresist, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
    }
}
