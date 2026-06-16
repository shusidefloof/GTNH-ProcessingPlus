package com.gtnh.processingplus.recipes;

import com.gtnh.processingplus.recipes.chains.infrastructure.*;
import com.gtnh.processingplus.recipes.chains.materials.*;
import com.gtnh.processingplus.recipes.chains.materials.finishedChains.*;
import com.gtnh.processingplus.recipes.chains.photoresist.PhotoresistRecipes;

public class PrPlusRecipes {

    public static void init() {
        System.out.println("PrPlusRecipes init");
        // All photoresist tiers (MV → UMV) now live in PhotoresistRecipes; its own init()
        // handles the per-tier fault isolation for the UHV+ exotic-fluid tiers internally.
        PhotoresistRecipes.init();
        SPCRecipes.init();
        Nylon66Recipes.init();
        PLARecipes.init();
        KaptonRecipes.init();
        SiCRecipes.init();
        HBNRecipes.init();
        CarbonFiberRecipes.init();
        AerogelRecipes.init();
        MaterialUsesRecipes.init();
        CRVRecipes.init();
        CACRecipes.init();
        RTGRecipes.init();
        CIDCRecipes.init();
        HPRRecipes.init();
        SPURecipes.init();
        FreonRecipes.init();
        CSCRecipes.init();
        BOFRecipes.init();
        SCDRecipes.init();
        CryoUpgradeRecipes.init();
        NeptuniumSynthesis.init();
        HPSFRecipes.init();
        LuVExotics.init();
        HTRFRecipes.init();
        PassiveableMaterials.init();
        AARRecipes.init();
    }
}
