package com.gtnh.processingplus.recipes;

import com.gtnh.processingplus.recipes.chains.infrastructure.AARRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.BOFRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.CACRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.CIDCRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.CRVRecipes;
import com.gtnh.processingplus.recipes.chains.infrastructure.CSCRecipes;
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
import com.gtnh.processingplus.recipes.chains.materials.CarbonFiberRecipes;
import com.gtnh.processingplus.recipes.chains.materials.KaptonRecipes;
import com.gtnh.processingplus.recipes.chains.materials.MaterialUsesRecipes;
import com.gtnh.processingplus.recipes.chains.materials.SiCRecipes;
import com.gtnh.processingplus.recipes.chains.materials.finishedChains.AerogelRecipes;
import com.gtnh.processingplus.recipes.chains.materials.finishedChains.HBNRecipes;
import com.gtnh.processingplus.recipes.chains.materials.finishedChains.LuVExotics;
import com.gtnh.processingplus.recipes.chains.materials.finishedChains.NeptuniumSynthesis;
import com.gtnh.processingplus.recipes.chains.materials.finishedChains.Nylon66Recipes;
import com.gtnh.processingplus.recipes.chains.materials.finishedChains.PLARecipes;
import com.gtnh.processingplus.recipes.chains.materials.finishedChains.PassiveableMaterials;
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
        DAFRecipes.init();
    }
}
