package com.gtnh.processingplus.recipes;

import com.gtnh.processingplus.recipes.chains.infrastructure.CRVRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.SPCRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.BOFRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.CSCRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.CryoUpgradeRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.FreonRecipes;
import com.gtnh.processingplus.recipes.chains.materials.*;
import com.gtnh.processingplus.recipes.chains.materials.finishedChains.CoalFlyashRecipes;
import com.gtnh.processingplus.recipes.chains.materials.finishedChains.HBNRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.HPSFRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.HTRFRecipes;
import com.gtnh.processingplus.recipes.chains.materials.finishedChains.LuVExotics;
import com.gtnh.processingplus.recipes.chains.materials.finishedChains.NeptuniumSynthesis;
import com.gtnh.processingplus.recipes.chains.materials.finishedChains.TaNbChainRecipes;
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
        CoalFlyashRecipes.init();
        FreonRecipes.init();
        CSCRecipes.init();
        BOFRecipes.init();
        DiphenylEtherRecipes.init();
        CryoUpgradeRecipes.init();
        NeptuniumSynthesis.init();
        HPSFRecipes.init();
        TaNbChainRecipes.init();
        LuVExotics.init();
        HTRFRecipes.init();
    }
}
