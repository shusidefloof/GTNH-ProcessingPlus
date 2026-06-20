package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeConstants.AssemblyLine;
import static gregtech.api.util.GTRecipeConstants.RESEARCH_ITEM;
import static gregtech.api.util.GTRecipeConstants.SCANNING;

import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.recipe.Scanning;

public class SCDRecipes {

    public static void init() {
        casingRecipe();
        controllerRecipe();
        carbonAerogelChain();
    }

    // =========================================================
    // SCD casing — Neutronium shell, AmTi plates, PTFE liner, Osmium + Iridium seals
    // =========================================================
    private static void casingRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 1),
                plate(PrPMaterials.AmorphousTritaniumAlloy, 8),
                plate(Materials.Polytetrafluoroethylene, 4),
                plate(Materials.Osmium, 2),
                plate(Materials.Iridium, 4),
                circuit(10))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 2, BlockGTNHPPCasings.SCD_CASING))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // =========================================================
    // SCD controller — Assembly Line, UHV scan
    // =========================================================
    private static void controllerRecipe() {
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Machine_UV_Printer.get(1))
            .metadata(SCANNING, new Scanning(45 * SECONDS, TierEU.RECIPE_UHV))
            .itemInputs(
                ItemList.Hull_UV.get(64),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 8),
                plate(PrPMaterials.AmorphousTritaniumAlloy, 32),
                plate(Materials.Polytetrafluoroethylene, 16),
                plate(Materials.Osmium, 8),
                ItemList.Electric_Pump_UHV.get(8),
                ItemList.Field_Generator_UHV.get(4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 8),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UHV, 4),
                item("wireGt01SuperconductorUV", 8))
            .fluidInputs(fluid("molten.indalloy140", 4608), fluid(PrPMaterials.LiquidCO2, 4000))
            .itemOutputs(GTNHPPBlocks.SCD.getStackForm(1))
            .eut(TierEU.RECIPE_UHV)
            .duration(45 * SECONDS)
            .addTo(AssemblyLine);
    }

    // =========================================================
    // Carbon aerogel chain (PAN route) — design doc v1.1
    //
    // Step A (SCD): PolyacrylonitrileSolution + H₂O → WetPANGel + DilutedNMP
    // Water triggers gelation of the PAN/NMP dope; the majority of NMP is
    // displaced and recovered as diluted NMP (same by-product as the PAN chain).
    //
    // Step B (SCD): WetPANGel + LiquidCO₂ → PANAerogel + CO₂ gas
    // Supercritical CO₂ drying — identical mechanism to the silica route but
    // without the acetone exchange step (PAN is directly CO₂-compatible at
    // supercritical conditions after water removal in Step A).
    //
    // Step C (HTRF): PANAerogel → CarbonAerogel + CO₂ + NH₃
    // Pyrolysis / carbonization at ~3600 K. PAN's nitrile groups cyclize,
    // then aromatize to a turbostratic carbon network; oxygen and nitrogen
    // off-gas as CO₂ and NH₃.
    // =========================================================
    private static void carbonAerogelChain() {

        // Step A — PAN sol-gel (SCD, water-triggered gelation)
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(PrPMaterials.PolyacrylonitrileSolution, 4000), fluid(Materials.Water, 1000))
            .itemOutputs(dust(PrPMaterials.WetPANGel, 4))
            .fluidOutputs(fluid(PrPMaterials.DilutedNMP, 3000))
            .duration(1200)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sSCDRecipes);

        // Step B — supercritical CO₂ drying (SCD)
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.WetPANGel, 4))
            .fluidInputs(fluid(PrPMaterials.LiquidCO2, 8000))
            .itemOutputs(dust(PrPMaterials.PANAerogel, 4))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 8000))
            .duration(1000)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sSCDRecipes);

        // Step C — pyrolysis / carbonization (HTRF)
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.PANAerogel, 2), circuit(1))
            .itemOutputs(plate(PrPMaterials.CarbonAerogel, 2))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 1000), fluid(Materials.Ammonia, 500))
            .duration(600)
            .eut(TierEU.RECIPE_ZPM)
            .metadata(GTRecipeConstants.COIL_HEAT, 3600)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }
}
