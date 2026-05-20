package com.gtnh.processingplus.recipes;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeConstants;

public class MaterialUsesRecipes {

    public static void init() {
        paaAdhesive();
        hbnLubricant();
        aerogelInsulationPanel();
        loadedAerogelCatalyst();
        plasticSolidification();
    }

    // -------------------------------------------------------------------------
    // PAA Adhesive — moisture-cured polyamic acid bonding fluid
    //
    // ConcentratedPAA absorbs water and partially imidizes at room temperature,
    // forming a tacky adhesive network. Used as a bonding layer in SPC boards
    // and as a high-temp structural adhesive for Kapton lamination.
    // -------------------------------------------------------------------------
    private static void paaAdhesive() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.ConcentratedPAA, 1), circuit(3))
            .fluidInputs(fluid(Materials.Water, 500))
            .fluidOutputs(fluid(PrPMaterials.PAAAdhesive, 1000))
            .duration(300)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.mixerRecipes);
    }

    // -------------------------------------------------------------------------
    // hBN Lubricant — hexagonal boron nitride suspended in ethanol carrier
    //
    // hBN's layered crystal structure shears under load, giving dry lubrication
    // stable to 900 °C in air. Ethanol carrier evaporates on application.
    // Required for CRV operation: protects the hBN lining during alloy contact.
    // -------------------------------------------------------------------------
    private static void hbnLubricant() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.HexagonalBoronNitride, 2), circuit(4))
            .fluidInputs(fluid(Materials.Ethanol, 1000))
            .fluidOutputs(fluid(PrPMaterials.HBNLubricant, 2000))
            .duration(200)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.mixerRecipes);
    }

    // -------------------------------------------------------------------------
    // Aerogel Insulation Panel — silica aerogel bonded to carbon fiber backing
    //
    // Aerogel provides extreme thermal resistance; CF backing prevents mechanical
    // fragility. Epoxid matrix locks the composite. Used as high-tier multiblock
    // insulation lining — primarily the HTRF outer wall.
    // -------------------------------------------------------------------------
    private static void aerogelInsulationPanel() {
        GTValues.RA.stdBuilder()
            .itemInputs(plate(PrPMaterials.SilicaAerogel, 4), plate(PrPMaterials.CarbonFiberComposite, 2), circuit(5))
            .fluidInputs(fluid("molten.epoxid", 288))
            .itemOutputs(plate(PrPMaterials.AerogelInsulationPanel, 4))
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // -------------------------------------------------------------------------
    // Loaded Aerogel Catalyst Support — Pt/Pd nanoparticles in aerogel matrix
    //
    // HCl wets the aerogel surface, anchoring PGM particles into the pore walls.
    // The huge surface area of silica aerogel (>1000 m²/g) concentrates catalysis
    // far beyond conventional support materials. Consumed in CRV exotic reactions.
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------
    // Thermoplastic solidification — molten polymer → plates via fluid solidifier
    // 144 mB = 1 plate (GT standard unit)
    // -------------------------------------------------------------------------
    private static void plasticSolidification() {
        GTValues.RA.stdBuilder()
            .fluidInputs(molten(PrPMaterials.Nylon66, 144))
            .itemOutputs(plate(PrPMaterials.Nylon66, 1))
            .duration(80).eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.fluidSolidifierRecipes);

        GTValues.RA.stdBuilder()
            .fluidInputs(molten(PrPMaterials.Nylon6, 144))
            .itemOutputs(plate(PrPMaterials.Nylon6, 1))
            .duration(80).eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.fluidSolidifierRecipes);

        GTValues.RA.stdBuilder()
            .fluidInputs(molten(PrPMaterials.PolylacticAcid, 144))
            .itemOutputs(plate(PrPMaterials.PolylacticAcid, 1))
            .duration(80).eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.fluidSolidifierRecipes);
    }

    private static void loadedAerogelCatalyst() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                plate(PrPMaterials.SilicaAerogel, 2),
                dustSmall(Materials.Platinum, 1),
                dustSmall(Materials.Palladium, 1),
                circuit(6))
            .fluidInputs(fluid(Materials.HydrochloricAcid, 500))
            .fluidOutputs(fluid(Materials.Water, 500))
            .itemOutputs(dust(PrPMaterials.LoadedAerogelCatalystSupport, 2))
            .duration(600)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTRecipeConstants.UniversalChemical);
    }
}
