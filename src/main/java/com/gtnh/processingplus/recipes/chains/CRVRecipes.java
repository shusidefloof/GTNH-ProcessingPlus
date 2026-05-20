package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;

public class CRVRecipes {

    public static void init() {
        amorphousTritaniumAlloy();
        amorphousNaquadria();
    }

    // -------------------------------------------------------------------------
    // Amorphous Tritanium Alloy — plasma-quench metallic glass (LuV CRV)
    //
    // Tritanium and Americium are alloyed above their liquidus in the hBN crucible,
    // then shock-cooled with a liquid Argon pulse before the alloy can nucleate
    // crystals. The hBN lining is the only material that survives contact with
    // molten Tritanium without dissolving into or catalysing the melt.
    // hBN Lubricant is consumed as the crucible interface layer each run.
    //
    // Product: stickLong form used as UV motor magnets (zap-cast after this step).
    // -------------------------------------------------------------------------
    private static void amorphousTritaniumAlloy() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingot(Materials.Tritanium, 4), dust(Materials.Americium, 2), circuit(8))
            .fluidInputs(fluid(PrPMaterials.HBNLubricant, 500), fluid(Materials.Argon, 2000))
            .itemOutputs(ingot(PrPMaterials.AmorphousTritaniumAlloy, 4))
            .duration(800)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sCRVRecipes);
    }

    // -------------------------------------------------------------------------
    // Amorphous Naquadria — plasma-quench of activated Naquadria (ZPM CRV)
    //
    // Enriched Naquadah enhances the radiative coupling during melting, letting
    // the Naquadria reach full liquid phase without a separate plasma stage.
    // The melt is then quenched under high-pressure Argon. hBN is the only
    // known liner that does not catalyse Naquadria re-crystallisation on contact.
    // hBN Lubricant consumption is doubled vs. Tritanium due to reactivity.
    //
    // Product: plate form used as mid-UV structural components.
    // -------------------------------------------------------------------------
    private static void amorphousNaquadria() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Naquadria, 4), ingot(Materials.NaquadahEnriched, 2), circuit(9))
            .fluidInputs(fluid(PrPMaterials.HBNLubricant, 1000), fluid(Materials.Argon, 4000))
            .itemOutputs(ingot(PrPMaterials.AmorphousNaquadria, 2))
            .duration(1200)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sCRVRecipes);
    }
}
