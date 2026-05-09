package com.gtnh.processingplus.recipes;

import com.gtnh.processingplus.recipes.chains.*;

public class PrPlusRecipes {

    public static void init() {
        System.out.println("PrPlusRecipes init");
        Nylon66Recipes.init();
        PLARecipes.init();
        KaptonRecipes.init();
        SiCRecipes.init();
        HBNRecipes.init();
        CarbonFiberRecipes.init();
        AerogelRecipes.init();
    }
}
