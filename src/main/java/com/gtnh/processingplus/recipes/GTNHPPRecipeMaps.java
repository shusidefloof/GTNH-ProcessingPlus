package com.gtnh.processingplus.recipes;

import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.gtnewhorizons.modularui.common.widget.ProgressBar;
import com.gtnh.processingplus.nei.AARNEIFormatter;
import com.gtnh.processingplus.nei.HPRNEIFormatter;
import com.gtnh.processingplus.nei.SPCRecipeMapFrontend;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBuilder;
import gregtech.api.recipe.maps.LargeNEIFrontend;
import gregtech.nei.formatter.HeatingCoilSpecialValueFormatter;

public class GTNHPPRecipeMaps {

    // @formatter:off
    /** High Temperature Reaction Furnace: also receives all EBF and ABS recipes at 80% EU cost (added in postInit). */
    public static final RecipeMap<RecipeMapBackend> sHTRFRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.htrf")
        .maxIO(6, 6, 6, 6)
        .neiSpecialInfoFormatter(HeatingCoilSpecialValueFormatter.INSTANCE)
        // 6 item slots each way overflow the default single-row layout.
        .frontend(LargeNEIFrontend::new)
        .build();

    /** High Pressure Sintering Furnace: ceramic sintering, hot isostatic pressing. */
    public static final RecipeMap<RecipeMapBackend> sHPSFRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.hpsf")
        .maxIO(4, 4, 2, 2)
        .neiSpecialInfoFormatter(HeatingCoilSpecialValueFormatter.INSTANCE)
        .progressBar(GTUITextures.PROGRESSBAR_COMPRESS)
        .progressBarMUI2(GTGuiTextures.PROGRESSBAR_COMPRESS)
        .build();

    /** Dual Atmosphere Furnace: oxidizing (air) atmosphere mode. */
    public static final RecipeMap<RecipeMapBackend> sDAFOxidizingRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.daf.oxidizing")
        .maxIO(4, 4, 2, 2)
        .build();

    /** Dual Atmosphere Furnace: inert (nitrogen/argon) atmosphere mode. */
    public static final RecipeMap<RecipeMapBackend> sDAFInertRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.daf.inert")
        .maxIO(4, 4, 2, 2)
        .build();

    /** Polycondensation Vessel: vacuum-assisted condensation and ring-opening polymerization. */
    public static final RecipeMap<RecipeMapBackend> sPCVRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.pcv")
        .maxIO(4, 4, 3, 3)
        .build();

    /** Continuous Stirred Tank Reactor: continuous-flow liquid-phase chemistry (IV tier). */
    public static final RecipeMap<RecipeMapBackend> sCSTRRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.cstr")
        .maxIO(4, 4, 3, 3)
        .progressBar(GTUITextures.PROGRESSBAR_MIXER, ProgressBar.Direction.CIRCULAR_CW)
        .progressBarMUI2(GTGuiTextures.PROGRESSBAR_MIXER, ProgressWidget.Direction.CIRCULAR_CW)
        .build();

    /** Precision Film Caster: casting mode (room temperature film formation). */
    public static final RecipeMap<RecipeMapBackend> sPFCCastingRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.pfc.casting")
        .maxIO(4, 4, 2, 2)
        .build();

    /** Precision Film Caster: imidization mode (staged high-temperature curing). */
    public static final RecipeMap<RecipeMapBackend> sPFCImidizationRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.pfc.imidization")
        .maxIO(4, 4, 2, 2)
        .build();

    /** Ammonia Atmosphere Reactor: reactive NH₃ gas atmosphere at high temperature. */
    public static final RecipeMap<RecipeMapBackend> sAARRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.aar")
        .maxIO(6, 6, 6, 6)
        .neiSpecialInfoFormatter(AARNEIFormatter.INSTANCE)
        // Gas-phase reactor, not an assembler: the old PROGRESSBAR_ASSEMBLE icon didn't fit.
        .progressBar(GTUITextures.PROGRESSBAR_FLUID_REACTOR, ProgressBar.Direction.CIRCULAR_CW)
        .progressBarMUI2(GTGuiTextures.PROGRESSBAR_FLUID_REACTOR, ProgressWidget.Direction.CIRCULAR_CW)
        .frontend(LargeNEIFrontend::new)
        .build();

    /**
     * Supercritical Dryer: 3-stage stall mechanic (see MTE_SCD).
     * Recipe format: 1 item input, 2 item outputs (perfect + degraded), 0 fluid inputs, ≤2 fluid outputs.
     * Stage fluids are consumed per-tick by the machine, not declared as recipe fluid inputs.
     * Stage fluid parameters are encoded in mSpecialValue via {@link MTE_SCD#encodeStageData}.
     */
    public static final RecipeMap<RecipeMapBackend> sSCDRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.scd")
        .maxIO(1, 2, 0, 2)
        .build();

    /** Ceramic Reaction Vessel: hBN-lined vessel for exotic molten alloy synthesis at LuV/ZPM. */
    public static final RecipeMap<RecipeMapBackend> sCRVRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.crv")
        .maxIO(9, 9, 6, 6)
        // 9 item inputs and 6 fluid inputs overflow the default single-row layout.
        .frontend(LargeNEIFrontend::new)
        .build();

    /** Spectral Photolithography Chamber: light-isolated synthesis of photoresist chemistry, EV+. */
    public static final RecipeMap<RecipeMapBackend> sSPCRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.spc")
        .maxIO(6, 2, 3, 2)
        .frontend(SPCRecipeMapFrontend::new)
        .slotOverlays(
            (index, isFluid, isOutput, isSpecial) -> !isFluid && !isOutput ? GTUITextures.OVERLAY_SLOT_CIRCUIT : null)
        .slotOverlaysMUI2(
            (index, isFluid, isOutput, isSpecial) -> !isFluid && !isOutput ? GTGuiTextures.OVERLAY_SLOT_CIRCUIT : null)
        .progressBar(GTUITextures.PROGRESSBAR_CIRCUIT_ASSEMBLER)
        .progressBarMUI2(GTGuiTextures.PROGRESSBAR_CIRCUIT_ASSEMBLER)
        .disableRegisterNEI()
        .build();

    /**
     * Cryogenic Separation Column: circuit(1) = ASU, circuit(2) = CO₂ liquefaction, circuit(3-5) = noble
     * gas extraction, circuit(6) = natural gas fractionation, circuit(7) = LPG fractionation, circuit(8) =
     * Helium extraction from natural gas.
     */
    public static final RecipeMap<RecipeMapBackend> sCSCRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.csc")
        .maxIO(2, 0, 2, 6)
        .progressBar(GTUITextures.PROGRESSBAR_EXTRACT)
        .progressBarMUI2(GTGuiTextures.PROGRESSBAR_EXTRACT)
        // 4 fluid outputs is already a tight fit for the default single-row layout.
        .frontend(LargeNEIFrontend::new)
        .build();

    /** Basic Oxygen Furnace: oxygen-blast iron→steel converter. */
    public static final RecipeMap<RecipeMapBackend> sBOFRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.bof")
        .maxIO(3, 2, 2, 2)
        .build();

    /** Controlled Isotopic Doping Chamber: rare-earth dopant matrix assembly for UV photoresist (UV tier). */
    public static final RecipeMap<RecipeMapBackend> sCIDCRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.cidc")
        .maxIO(6, 2, 2, 2)
        // 6 item inputs overflow the default single-row layout.
        .frontend(LargeNEIFrontend::new)
        .build();

    /** Hybrid Phase Reactor: simultaneous liquid/plasma phase chemistry (UHV tier). */
    public static final RecipeMap<RecipeMapBackend> sHPRRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.hpr")
        .maxIO(2, 2, 4, 2)
        .neiSpecialInfoFormatter(HPRNEIFormatter.INSTANCE)
        .progressBar(GTUITextures.PROGRESSBAR_PLASMA_HEATER)
        .progressBarMUI2(GTGuiTextures.PROGRESSBAR_PLASMA_HEATER)
        .build();

    /** Subatomic Patterning Unit: quantum lattice imprinting (UIV tier). */
    public static final RecipeMap<RecipeMapBackend> sSPURecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.spu")
        .maxIO(4, 2, 4, 2)
        .build();

    // TODO: wire to actual external-mod recipe maps when dependencies are confirmed
    /** Forge of the Gods: placeholder until FotG exposes a recipe map. */
    public static final RecipeMap<RecipeMapBackend> sFotGRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.fotg")
        .maxIO(4, 2, 4, 2)
        .build();

    /** Cryogenic Annealing Cryostat: aerogel-insulated superconductor anneal, UHV-tier and up. */
    public static final RecipeMap<RecipeMapBackend> sCACRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.cac")
        .maxIO(9, 1, 1, 0)
        // 9 item inputs overflow the default single-row layout.
        .frontend(LargeNEIFrontend::new)
        .build();

    /** RTG fuel: placeholder map; the RTG counts betavoltaic cells directly, this only keeps getRecipeMap non-null. */
    public static final RecipeMap<RecipeMapBackend> sRTGRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.rtg")
        .maxIO(1, 1, 0, 0)
        .disableRegisterNEI()
        .build();

    // @formatter:on
}
