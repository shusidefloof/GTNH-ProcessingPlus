package com.gtnh.processingplus.recipes;

import com.gtnh.processingplus.nei.SPCRecipeMapFrontend;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBuilder;
import gregtech.nei.formatter.HeatingCoilSpecialValueFormatter;

public class GTNHPPRecipeMaps {

    // @formatter:off
    /** High Temperature Reaction Furnace — also receives all EBF and ABS recipes at 80% EU cost (added in postInit). */
    public static final RecipeMap<RecipeMapBackend> sHTRFRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.htrf")
        .maxIO(6, 6, 3, 3)
        .neiSpecialInfoFormatter(HeatingCoilSpecialValueFormatter.INSTANCE)
        .build();

    /** High Pressure Sintering Furnace — ceramic sintering, hot isostatic pressing. */
    public static final RecipeMap<RecipeMapBackend> sHPSFRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.hpsf")
        .maxIO(4, 4, 2, 2)
        .neiSpecialInfoFormatter(HeatingCoilSpecialValueFormatter.INSTANCE)
        .build();

    /** Dual Atmosphere Furnace — oxidizing (air) atmosphere mode. */
    public static final RecipeMap<RecipeMapBackend> sDAFOxidizingRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.daf.oxidizing")
        .maxIO(4, 4, 2, 2)
        .build();

    /** Dual Atmosphere Furnace — inert (nitrogen/argon) atmosphere mode. */
    public static final RecipeMap<RecipeMapBackend> sDAFInertRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.daf.inert")
        .maxIO(4, 4, 2, 2)
        .build();

    /** Polycondensation Vessel — vacuum-assisted condensation and ring-opening polymerization. */
    public static final RecipeMap<RecipeMapBackend> sPCVRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.pcv")
        .maxIO(4, 4, 3, 3)
        .build();

    /** Precision Film Caster — casting mode (room temperature film formation). */
    public static final RecipeMap<RecipeMapBackend> sPFCCastingRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.pfc.casting")
        .maxIO(4, 4, 2, 2)
        .build();

    /** Precision Film Caster — imidization mode (staged high-temperature curing). */
    public static final RecipeMap<RecipeMapBackend> sPFCImidizationRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.pfc.imidization")
        .maxIO(4, 4, 2, 2)
        .build();

    /** Ammonia Atmosphere Reactor — reactive NH₃ gas atmosphere at high temperature. */
    public static final RecipeMap<RecipeMapBackend> sAARRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.aar")
        .maxIO(4, 4, 2, 3)
        .build();

    /** Supercritical Dryer — above ethanol's critical point (241°C, 63 bar). */
    public static final RecipeMap<RecipeMapBackend> sSCDRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.scd")
        .maxIO(4, 4, 2, 2)
        .build();

    /** Ceramic Reaction Vessel — hBN-lined vessel for exotic molten alloy synthesis at LuV/ZPM. */
    public static final RecipeMap<RecipeMapBackend> sCRVRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.crv")
        .maxIO(9, 4, 6, 3)
        .build();

    /** Spectral Photolithography Chamber — light-isolated synthesis of photoresist chemistry, EV+. */
    public static final RecipeMap<RecipeMapBackend> sSPCRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.spc")
        .maxIO(6, 2, 3, 2)
        .frontend(SPCRecipeMapFrontend::new)
        .disableRegisterNEI()
        .build();

    /** Cryogenic Separation Column — circuit(1) = ASU, circuit(2) = CO₂ liquefaction. */
    public static final RecipeMap<RecipeMapBackend> sCSCRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.csc")
        .maxIO(2, 1, 2, 4)
        .build();

    /** Basic Oxygen Furnace — LOX-driven iron→steel converter, three circuit modes. */
    public static final RecipeMap<RecipeMapBackend> sBOFRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.bof")
        .maxIO(3, 2, 2, 2)
        .build();

    /** Controlled Isotopic Doping Chamber — rare-earth dopant matrix assembly for UV photoresist (UV tier). */
    public static final RecipeMap<RecipeMapBackend> sCIDCRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.cidc")
        .maxIO(6, 2, 2, 2)
        .build();

    /** Hybrid Phase Reactor — simultaneous liquid/plasma phase chemistry (UHV tier). */
    public static final RecipeMap<RecipeMapBackend> sHPRRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.hpr")
        .maxIO(2, 2, 4, 2)
        .build();

    /** Subatomic Patterning Unit — quantum lattice imprinting (UIV tier). */
    public static final RecipeMap<RecipeMapBackend> sSPURecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.spu")
        .maxIO(4, 2, 4, 2)
        .build();

    // TODO: wire to actual external-mod recipe maps when dependencies are confirmed
    /** Forge of the Gods — placeholder until FotG exposes a recipe map. */
    public static final RecipeMap<RecipeMapBackend> sFotGRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.fotg")
        .maxIO(4, 2, 4, 2)
        .build();

    /** Beamcrafter — placeholder until GoodGenerator exposes a recipe map. */
    public static final RecipeMap<RecipeMapBackend> sBeamcrafterRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.beamcrafter")
        .maxIO(2, 1, 2, 1)
        .build();

    /** Cryogenic Annealing Cryostat — aerogel-insulated superconductor anneal, UHV-tier and up. */
    public static final RecipeMap<RecipeMapBackend> sCACRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.cac")
        .maxIO(8, 2, 2, 2)
        .build();

    /** RTG fuel — placeholder map; the RTG counts betavoltaic cells directly, this only keeps getRecipeMap non-null. */
    public static final RecipeMap<RecipeMapBackend> sRTGRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.rtg")
        .maxIO(1, 1, 0, 0)
        .disableRegisterNEI()
        .build();

    // @formatter:on
}
