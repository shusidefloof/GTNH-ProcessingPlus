package com.gtnh.processingplus.recipes;

import com.gtnh.processingplus.recipes.chains.AerogelRecipes;
import com.gtnh.processingplus.recipes.chains.BOFRecipes;
import com.gtnh.processingplus.recipes.chains.CRVRecipes;
import com.gtnh.processingplus.recipes.chains.CSCRecipes;
import com.gtnh.processingplus.recipes.chains.CarbonFiberRecipes;
import com.gtnh.processingplus.recipes.chains.CoalFlyashRecipes;
import com.gtnh.processingplus.recipes.chains.CryoUpgradeRecipes;
import com.gtnh.processingplus.recipes.chains.DiphenylEtherRecipes;
import com.gtnh.processingplus.recipes.chains.FreonRecipes;
import com.gtnh.processingplus.recipes.chains.HBNRecipes;
import com.gtnh.processingplus.recipes.chains.KaptonRecipes;
import com.gtnh.processingplus.recipes.chains.Nylon66Recipes;
import com.gtnh.processingplus.recipes.chains.PLARecipes;
import com.gtnh.processingplus.recipes.chains.PhotoresistRecipes;
import com.gtnh.processingplus.recipes.chains.SPCRecipes;
import com.gtnh.processingplus.recipes.chains.SiCRecipes;
import com.gtnh.processingplus.recipes.chains.UEVPhotoresistRecipes;
import com.gtnh.processingplus.recipes.chains.UHVPhotoresistRecipes;
import com.gtnh.processingplus.recipes.chains.UIVPhotoresistRecipes;
import com.gtnh.processingplus.recipes.chains.UMVPhotoresistRecipes;
import com.gtnh.processingplus.recipes.chains.UVPhotoresistRecipes;
import com.gtnh.processingplus.recipes.chains.ZPMPhotoresistRecipes;

public class PrPlusRecipes {

    private static void tryInit(String name, Runnable init) {
        try {
            init.run();
        } catch (IllegalStateException e) {
            System.err.println("[GTNHPP] Skipping " + name + " recipes — missing fluid: " + e.getMessage());
        }
    }

    public static void init() {
        System.out.println("PrPlusRecipes init");
        PhotoresistRecipes.init();
        ZPMPhotoresistRecipes.init();
        UVPhotoresistRecipes.init();
        tryInit("UHV photoresist", UHVPhotoresistRecipes::init);
        tryInit("UEV photoresist", UEVPhotoresistRecipes::init);
        tryInit("UIV photoresist", UIVPhotoresistRecipes::init);
        tryInit("UMV photoresist", UMVPhotoresistRecipes::init);
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
    }
}
