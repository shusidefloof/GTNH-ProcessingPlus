package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeConstants;

public class SCDRecipes {

    public static void init() {
        casingRecipe();
        controllerRecipe();
        carbonAerogelChain();
    }

    // =========================================================
    // SCD casing — titanium pressure-vessel shell with PTFE inner liner
    // =========================================================
    private static void casingRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Titanium, 1),
                plate(Materials.Titanium, 4),
                plate(Materials.StainlessSteel, 2),
                plate(Materials.Polytetrafluoroethylene, 1),
                circuit(12))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 4, BlockGTNHPPCasings.SCD_CASING))
            .duration(20 * 20)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // =========================================================
    // SCD controller — EV-tier build, handles supercritical fluid pressures
    // =========================================================
    private static void controllerRecipe() {
        ItemStack controller = new ItemStack(GregTechAPI.sBlockMachines, 1, GTNHPPBlocks.SCD_ID);

        GTValues.RA.stdBuilder()
            .itemInputs(
                plate(Materials.Titanium, 4),
                plate(Materials.Polytetrafluoroethylene, 2),
                ItemList.Hull_EV.get(1),
                ItemList.Electric_Pump_EV.get(2),
                item("circuitAdvanced", 2))
            .itemOutputs(controller)
            .duration(20 * 20)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.assemblerRecipes);

        GameRegistry.addShapedRecipe(
            controller,
            "TPT",
            "CHC",
            "TET",
            'T', plate(Materials.Titanium, 1),
            'P', plate(Materials.Polytetrafluoroethylene, 1),
            'H', ItemList.Hull_EV.get(1),
            'C', item("circuitAdvanced", 1),
            'E', ItemList.Electric_Pump_EV.get(1));
    }

    // =========================================================
    // Carbon aerogel chain (PAN route) — design doc v1.1
    //
    // Step A (SCD):  PolyacrylonitrileSolution + H₂O → WetPANGel + DilutedNMP
    //   Water triggers gelation of the PAN/NMP dope; the majority of NMP is
    //   displaced and recovered as diluted NMP (same by-product as the PAN chain).
    //
    // Step B (SCD):  WetPANGel + LiquidCO₂ → PANAerogel + CO₂ gas
    //   Supercritical CO₂ drying — identical mechanism to the silica route but
    //   without the acetone exchange step (PAN is directly CO₂-compatible at
    //   supercritical conditions after water removal in Step A).
    //
    // Step C (HTRF): PANAerogel → CarbonAerogel + CO₂ + NH₃
    //   Pyrolysis / carbonization at ~3600 K. PAN's nitrile groups cyclize,
    //   then aromatize to a turbostratic carbon network; oxygen and nitrogen
    //   off-gas as CO₂ and NH₃.
    // =========================================================
    private static void carbonAerogelChain() {

        // Step A — PAN sol-gel (SCD, water-triggered gelation)
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(
                fluid(PrPMaterials.PolyacrylonitrileSolution, 4000),
                fluid(Materials.Water, 1000))
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
