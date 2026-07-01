package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeConstants.AssemblyLine;
import static gregtech.api.util.GTRecipeConstants.RESEARCH_ITEM;
import static gregtech.api.util.GTRecipeConstants.SCANNING;

import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.machines.MTE_SCD;
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
        registerStageFluids();
        casingRecipe();
        controllerRecipe();
        carbonAerogelChain();
    }

    // =========================================================
    // Stage fluid registration — must run before recipe additions
    // =========================================================
    private static void registerStageFluids() {
        // Stage 1 fluids (solvent purge)
        MTE_SCD.registerStage1Fluid(MTE_SCD.STAGE1_ACETONE, fluid(Materials.Acetone, 1).getFluid());
        MTE_SCD.registerStage1Fluid(MTE_SCD.STAGE1_ETHANOL, fluid(Materials.Ethanol, 1).getFluid());

        // Stage 2 supercritical fluids — extraTicksPerTick: 0 = 1× baseline, 1 = 2× faster
        MTE_SCD.registerStage2Fluid(fluid(PrPMaterials.LiquidCO2, 1).getFluid(), 0); // Liquid CO₂: baseline
        MTE_SCD.registerStage2Fluid(fluid(PrPMaterials.FreonR12, 1).getFluid(), 1); // Freon R-12: 2× faster
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
    // Step A (PCV): PolyacrylonitrileSolution + H₂O → WetPANGel + DilutedNMP
    // Water triggers gelation of the PAN/NMP dope; NMP is displaced and
    // recovered as diluted NMP. Moved to PCV (polycondensation/gelation step).
    //
    // Step B (SCD, 3-stage): WetPANGel → PANAerogel + CO₂ gas
    // Stage 1 (empty hatch): PAN gel enters pre-dried — no solvent exchange needed.
    // Stage 2 (Liquid CO₂ / Freon R-12): supercritical CO₂ extraction.
    // ~24 mB/t with Liquid CO₂ → ~8 L total; Freon R-12 halves stage-2 time.
    // Stage 3 (empty hatch): depressurization — fluid contamination degrades the aerogel.
    //
    // Step C (HTRF): PANAerogel → CarbonAerogel + CO₂ + NH₃
    // Pyrolysis / carbonization at ~3600 K.
    // =========================================================
    private static void carbonAerogelChain() {

        // Step A — PAN sol-gel (PCV, water-triggered gelation)
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(PrPMaterials.PolyacrylonitrileSolution, 4000), fluid(Materials.Water, 1000))
            .itemOutputs(dust(PrPMaterials.WetPANGel, 4))
            .fluidOutputs(fluid(PrPMaterials.DilutedNMP, 3000))
            .duration(1200)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);

        // Step B — supercritical CO₂ drying (SCD, 3-stage mechanic)
        // Stage 1: Ethanol flush (~10 mB/t) — displaces residual water from the PAN gel pores
        // before CO₂ is introduced; ethanol is miscible with both water and CO₂.
        // Stage 2: Liquid CO₂ baseline (24 mB/t × ~333 t ≈ 8 000 mB); Freon R-12 halves stage-2 time.
        // Stage 3: empty hatch. Fluid present → degraded output (WetPANGel × 2, gel collapsed).
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.WetPANGel, 4))
            .itemOutputs(
                dust(PrPMaterials.PANAerogel, 4), // [0] perfect: aerogel network intact
                dust(PrPMaterials.WetPANGel, 2)) // [1] degraded: gel collapsed, half returned
            .fluidOutputs(fluid(Materials.CarbonDioxide, 8000))
            .duration(1000)
            .eut(TierEU.RECIPE_EV)
            .metadata(GTRecipeConstants.COIL_HEAT, MTE_SCD.encodeStageData(MTE_SCD.STAGE1_ETHANOL, 10, 24))
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
