package com.gtnh.processingplus.recipes;

import com.gtnh.processingplus.recipes.chains.AerogelRecipes;
import com.gtnh.processingplus.recipes.chains.BOFRecipes;
import com.gtnh.processingplus.recipes.chains.CRVRecipes;
import com.gtnh.processingplus.recipes.chains.CryoUpgradeRecipes;
import com.gtnh.processingplus.recipes.chains.DiphenylEtherRecipes;
import com.gtnh.processingplus.recipes.chains.CarbonFiberRecipes;
import com.gtnh.processingplus.recipes.chains.CSCRecipes;
import com.gtnh.processingplus.recipes.chains.CoalFlyashRecipes;
import com.gtnh.processingplus.recipes.chains.FreonRecipes;
import com.gtnh.processingplus.recipes.chains.HBNRecipes;
import com.gtnh.processingplus.recipes.chains.KaptonRecipes;
import com.gtnh.processingplus.recipes.chains.Nylon66Recipes;
import com.gtnh.processingplus.recipes.chains.PLARecipes;
import com.gtnh.processingplus.recipes.chains.PhotoresistRecipes;
import com.gtnh.processingplus.recipes.chains.SPCRecipes;
import com.gtnh.processingplus.recipes.chains.SiCRecipes;

public class PrPlusRecipes {

    public static void init() {
        System.out.println("PrPlusRecipes init");
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
    }
}
