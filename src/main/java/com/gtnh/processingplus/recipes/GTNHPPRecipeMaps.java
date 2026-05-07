package com.gtnh.processingplus.recipes;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBuilder;

public class GTNHPPRecipeMaps {

    // @formatter:off
    /** High Temperature Reaction Furnace — also receives all EBF and ABS recipes at 80% EU cost (added in postInit). */
    public static final RecipeMap<RecipeMapBackend> sHTRFRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.htrf")
        .maxIO(6, 6, 3, 3)
        .build();

    /** High Pressure Sintering Furnace — ceramic sintering, hot isostatic pressing. */
    public static final RecipeMap<RecipeMapBackend> sHPSFRecipes = RecipeMapBuilder
        .of("gtnhpp.recipe.hpsf")
        .maxIO(4, 4, 2, 2)
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
    // @formatter:on
}
