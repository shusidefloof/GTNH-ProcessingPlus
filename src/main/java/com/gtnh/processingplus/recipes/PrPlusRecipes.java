package com.gtnh.processingplus.recipes;

import com.gtnh.processingplus.GTNHProcessingPlus;
import com.gtnh.processingplus.recipes.chains.infrastructure.AARRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.BOFRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.CACRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.CIDCRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.CRVRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.CSCRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.CSTRRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.CryoUpgradeRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.DAFRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.FreonRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.HPRRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.HPSFRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.HTRFRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.RTGRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.SCDRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.SPCRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.SPURecipes;
import com.gtnh.processingplus.recipes.chains.materials.AerogelRecipes;
import com.gtnh.processingplus.recipes.chains.materials.CarbonFiberRecipes;
import com.gtnh.processingplus.recipes.chains.materials.HBNRecipes;
import com.gtnh.processingplus.recipes.chains.materials.KaptonRecipes;
import com.gtnh.processingplus.recipes.chains.materials.LuVExotics;
import com.gtnh.processingplus.recipes.chains.materials.MaterialUsesRecipes;
import com.gtnh.processingplus.recipes.chains.materials.NeptuniumSynthesis;
import com.gtnh.processingplus.recipes.chains.materials.Nylon66Recipes;
import com.gtnh.processingplus.recipes.chains.materials.PLARecipes;
import com.gtnh.processingplus.recipes.chains.materials.PassiveableMaterials;
import com.gtnh.processingplus.recipes.chains.materials.SiCRecipes;
import com.gtnh.processingplus.recipes.chains.photoresist.PhotoresistRecipes;

public class PrPlusRecipes {

    public static void init() {
        GTNHProcessingPlus.LOG.info("Registering Processing+ recipe chains");
        // All photoresist tiers (MV → UMV) now live in PhotoresistRecipes; its own init()
        // handles the per-tier fault isolation for the UHV+ exotic-fluid tiers internally.
        RecipeGuard.run("PhotoresistRecipes", PhotoresistRecipes::init);
        RecipeGuard.run("SPCRecipes", SPCRecipes::init);
        RecipeGuard.run("Nylon66Recipes", Nylon66Recipes::init);
        RecipeGuard.run("PLARecipes", PLARecipes::init);
        RecipeGuard.run("KaptonRecipes", KaptonRecipes::init);
        RecipeGuard.run("SiCRecipes", SiCRecipes::init);
        RecipeGuard.run("HBNRecipes", HBNRecipes::init);
        RecipeGuard.run("CarbonFiberRecipes", CarbonFiberRecipes::init);
        RecipeGuard.run("AerogelRecipes", AerogelRecipes::init);
        RecipeGuard.run("MaterialUsesRecipes", MaterialUsesRecipes::init);
        RecipeGuard.run("CRVRecipes", CRVRecipes::init);
        RecipeGuard.run("CACRecipes", CACRecipes::init);
        RecipeGuard.run("RTGRecipes", RTGRecipes::init);
        RecipeGuard.run("CIDCRecipes", CIDCRecipes::init);
        RecipeGuard.run("HPRRecipes", HPRRecipes::init);
        RecipeGuard.run("SPURecipes", SPURecipes::init);
        RecipeGuard.run("FreonRecipes", FreonRecipes::init);
        RecipeGuard.run("CSCRecipes", CSCRecipes::init);
        RecipeGuard.run("BOFRecipes", BOFRecipes::init);
        RecipeGuard.run("CSTRRecipes", CSTRRecipes::init);
        RecipeGuard.run("SCDRecipes", SCDRecipes::init);
        RecipeGuard.run("CryoUpgradeRecipes", CryoUpgradeRecipes::init);
        RecipeGuard.run("NeptuniumSynthesis", NeptuniumSynthesis::init);
        RecipeGuard.run("HPSFRecipes", HPSFRecipes::init);
        RecipeGuard.run("LuVExotics", LuVExotics::init);
        RecipeGuard.run("HTRFRecipes", HTRFRecipes::init);
        RecipeGuard.run("PassiveableMaterials", PassiveableMaterials::init);
        RecipeGuard.run("AARRecipes", AARRecipes::init);
        RecipeGuard.run("DAFRecipes", DAFRecipes::init);
    }
}
