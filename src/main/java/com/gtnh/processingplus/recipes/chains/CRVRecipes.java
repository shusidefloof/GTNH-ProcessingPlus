package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import bartworks.system.material.Werkstoff;
import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeConstants.AssemblyLine;
import static gregtech.api.util.GTRecipeConstants.RESEARCH_ITEM;
import static gregtech.api.util.GTRecipeConstants.SCANNING;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.*;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.recipe.Scanning;
import gtPlusPlus.core.fluids.GTPPFluids;
import net.minecraft.item.ItemStack;

public class CRVRecipes {

    public static void init() {
        amorphousTritaniumAlloy();
        amorphousNaquadria();
        controllerRecipe();
    }

    // -------------------------------------------------------------------------
    // CRV controller — assembly-line recipe (EV).
    //
    // The vessel is an EV hull lined with sintered silicon-carbide ceramic plates
    // (the crucible wall) and braced with a tungstensteel frame. Stainless pipes
    // feed the Argon quench pulse. Scanned from a plain EV hull.
    // -------------------------------------------------------------------------
    private static void controllerRecipe() {
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Hull_LuV.get(1))
            .metadata(SCANNING, new Scanning(45 * SECONDS, TierEU.RECIPE_ZPM))
            .itemInputs(
                ItemList.AlloySmelterLuV.get(4),
                block(PrPMaterials.HexagonalBoronNitride, 8),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 4),
                GTOreDictUnificator.get(OrePrefixes.pipeMedium, Materials.StainlessSteel, 16),
                item("screwLumiium", 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 2),
                item("cableGt16RefractoryHigh-EntropyAlloy", 16))
            .fluidInputs(fluid("molten.indalloy140",1152),
                fluid(PrPMaterials.HBNLubricant, 1000),
                fluid(Materials.Argon, 2000))
            .itemOutputs(GTNHPPBlocks.CRV.getStackForm(1))
            .eut(TierEU.RECIPE_LuV)
            .duration(40 * SECONDS)
            .addTo(AssemblyLine);

        GTValues.RA.stdBuilder()
            .itemInputs(item("frameGtIridium",1),
                plate(Materials.Gadolinium, 8),
                ItemList.Electric_Piston_IV.get(2),
                foil(PrPMaterials.PHSResin, 4))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.CRV_CASING))
            .eut(TierEU.RECIPE_EV)
            .duration(10 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(item("frameGtDuranium",1),
                doublePlate(PrPMaterials.HexagonalBoronNitride, 6),
                item("boltRuridit", 16),
                ItemList.Electric_Piston_IV.get(2),
                foil(PrPMaterials.PHSResin, 4))
            .fluidInputs(fluid("wet.concrete", 1152))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.HBN_CERAMIC_BLOCK))
            .eut(TierEU.RECIPE_EV)
            .duration(10 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);
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
