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
import com.gtnh.processingplus.recipes.chains.materials.KaptonRecipes;
import com.gtnh.processingplus.recipes.chains.materials.Nylon66Recipes;
import com.gtnh.processingplus.recipes.chains.materials.PLARecipes;
import com.gtnh.processingplus.recipes.chains.materials.SiCRecipes;
import com.gtnh.processingplus.recipes.chains.photoresist.PhotoresistRecipes;
import com.gtnh.processingplus.recipes.chains.photoresist.UEVPhotoresistRecipes;
import com.gtnh.processingplus.recipes.chains.photoresist.UHVPhotoresistRecipes;
import com.gtnh.processingplus.recipes.chains.photoresist.UIVPhotoresistRecipes;
import com.gtnh.processingplus.recipes.chains.photoresist.UMVPhotoresistRecipes;
import com.gtnh.processingplus.recipes.chains.photoresist.UVPhotoresistRecipes;
import com.gtnh.processingplus.recipes.chains.photoresist.ZPMPhotoresistRecipes;

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
