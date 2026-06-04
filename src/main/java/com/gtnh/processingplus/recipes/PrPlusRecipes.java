package com.gtnh.processingplus.recipes;

import com.gtnh.processingplus.recipes.chains.CRVRecipes;
import com.gtnh.processingplus.recipes.chains.SPCRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.BOFRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.CSCRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.CryoUpgradeRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.FreonRecipes;
import com.gtnh.processingplus.recipes.chains.materials.AerogelRecipes;
import com.gtnh.processingplus.recipes.chains.materials.CarbonFiberRecipes;
import com.gtnh.processingplus.recipes.chains.materials.CoalFlyashRecipes;
import com.gtnh.processingplus.recipes.chains.materials.DiphenylEtherRecipes;
import com.gtnh.processingplus.recipes.chains.materials.HBNRecipes;
import com.gtnh.processingplus.recipes.chains.materials.HPSFRecipes;
import com.gtnh.processingplus.recipes.chains.materials.KaptonRecipes;
import com.gtnh.processingplus.recipes.chains.materials.LuVExotics;
import com.gtnh.processingplus.recipes.chains.materials.NeptuniumSynthesis;
import com.gtnh.processingplus.recipes.chains.materials.Nylon66Recipes;
import com.gtnh.processingplus.recipes.chains.materials.PLARecipes;
import com.gtnh.processingplus.recipes.chains.materials.SiCRecipes;
import com.gtnh.processingplus.recipes.chains.materials.TaNbChainRecipes;
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
    }
}
