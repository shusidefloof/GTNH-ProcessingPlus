package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;

/**
 * Alternate recipes that use LiquidArgon and LiquidNitrogen from the CSC in place of
 * large volumes of gas. Rewards players who invest in the cryogenic separation chain:
 *
 *   HBN blending      — N₂ 16000 + Ar 8000   → LN₂ 2000  + LAr 1000  | +25% blend yield
 *   HBN sintering     — N₂ 128000 + Ar 32000 → LN₂ 16000 + LAr 4000  | +25% hBN yield
 *   CF graphitization — Ar 16000              → LAr 2000               | +1 fiber output
 *   CRV Ti alloy      — Ar 2000               → LAr 500                | +1 ingot output
 *   CRV Naquadria     — Ar 4000               → LAr 1000               | +1 ingot output
 *
 * All cryo alternates use circuit(11) or higher to avoid recipe conflicts with the
 * gas-atmosphere base recipes.
 */
public class CryoUpgradeRecipes {

    public static void init() {
        hbnBlendingCryo();
        hbnSinteringCryo();
        graphitizationCryo();
        crvTitaniumCryo();
        crvNaquadriaCryo();
    }

    // =========================================================
    // hBN Powder Blending — cryo atmosphere (alt to HBNRecipes step4)
    // Cryogenic N₂+Ar atmosphere provides better inert blanket:
    // no moisture, no oxidation → superior dopant distribution.
    // Gas saved: N₂ 16000 + Ar 8000 → LN₂ 2000 + LAr 1000 (8× each).
    // Yield: +25% (10 blend vs 8 base).
    // =========================================================
    private static void hbnBlendingCryo() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.CrudeHBN, 4), dust(Materials.Yttrium, 16), circuit(11))
            .fluidInputs(
                fluid(PrPMaterials.LiquidNitrogen, 2000),
                fluid(PrPMaterials.LiquidArgon, 1000))
            .itemOutputs(dust(PrPMaterials.HBNPowderBlend, 10))
            .fluidOutputs(fluid(Materials.NitricOxide, 3000), fluid(Materials.Oxygen, 1500))
            .duration(480)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // hBN Hot-Press Sintering — cryo-dense atmosphere (alt to HBNRecipes step5)
    // Liquid gases deliver far denser inert atmosphere per mB.
    // Gas saved: N₂ 128000 + Ar 32000 → LN₂ 16000 + LAr 4000 (8× each).
    // Yield: +25% (20 hBN vs 16 base per 32 blend input).
    // =========================================================
    private static void hbnSinteringCryo() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.HBNPowderBlend, 32), circuit(11))
            .fluidInputs(
                fluid(PrPMaterials.LiquidNitrogen, 16000),
                fluid(PrPMaterials.LiquidArgon, 4000),
                fluid("oganesson", 288))
            .itemOutputs(dust(PrPMaterials.HexagonalBoronNitride, 20))
            .duration(600)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // =========================================================
    // CF Graphitization — LiquidArgon (alt to CarbonFiberRecipes step7)
    // Liquid Ar delivers ~840× denser inert atmosphere per mB — better
    // crystal plane alignment during 2500°C graphitization.
    // Gas saved: Ar 16000 → LAr 2000 (8×).
    // Yield: +1 GraphitizedCarbonFiber tow (5 vs 4 base).
    // =========================================================
    private static void graphitizationCryo() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.CarbonFiberTow, 4), circuit(11))
            .fluidInputs(fluid(PrPMaterials.LiquidArgon, 2000))
            .itemOutputs(dust(PrPMaterials.GraphitizedCarbonFiber, 5))
            .duration(1000)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // CRV Amorphous Tritanium — LiquidArgon cryo-quench (alt to CRVRecipes)
    // Liquid Ar shock-quenches the melt ~840× faster per mB than gas Ar.
    // Faster quench = smaller critical radius = better amorphous glass fraction.
    // Gas saved: Ar 2000 → LAr 500 (4×).
    // Yield: +1 ingot (5 vs 4 base).
    // =========================================================
    private static void crvTitaniumCryo() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Tritanium, 4), dust(Materials.Americium, 2), circuit(12))
            .fluidInputs(
                fluid(PrPMaterials.HBNLubricant, 500),
                fluid(PrPMaterials.LiquidArgon, 500))
            .itemOutputs(ingot(PrPMaterials.AmorphousTritaniumAlloy, 5))
            .duration(640)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sCRVRecipes);
    }

    // =========================================================
    // CRV Amorphous Naquadria — LiquidArgon cryo-quench (alt to CRVRecipes)
    // Naquadria's high reactivity demands rapid quench to prevent re-crystallisation.
    // Liquid Ar delivers the required quench rate at a fraction of the gas volume.
    // Gas saved: Ar 4000 → LAr 1000 (4×).
    // Yield: +1 ingot (3 vs 2 base).
    // =========================================================
    private static void crvNaquadriaCryo() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Naquadria, 4), ingot(Materials.NaquadahEnriched, 2), circuit(13))
            .fluidInputs(
                fluid(PrPMaterials.HBNLubricant, 1000),
                fluid(PrPMaterials.LiquidArgon, 1000))
            .itemOutputs(ingot(PrPMaterials.AmorphousNaquadria, 3))
            .duration(960)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sCRVRecipes);
    }
}
