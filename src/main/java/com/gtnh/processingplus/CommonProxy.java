package com.gtnh.processingplus;

import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.loader.MaterialLoader;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import com.gtnh.processingplus.recipes.PrPlusRecipes;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
       // Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        //GTNHPPBlocks.registerBlocks();
        GTNHProcessingPlus.LOG.info("GT:NH Processing+ v{} loading", Tags.VERSION);

        MaterialLoader.load();
    }

    public void init(FMLInitializationEvent event) {
        GTNHPPBlocks.registerMachines();
    }

    public void postInit(FMLPostInitializationEvent event) {}

    public void loadComplete(FMLLoadCompleteEvent event) {
        copyRecipesToHTRF();
        copyRecipesToCRV();
        PrPlusRecipes.init();
    }

    public void serverStarting(FMLServerStartingEvent event) {}

    /** Copies all ABS recipes into the CRV recipe map at 80% EU cost. */
    private static void copyRecipesToCRV() {
        for (GTRecipe recipe : GTPPRecipeMaps.alloyBlastSmelterRecipes.getAllRecipes()) {
            GTRecipe copy = recipe.copy();
            copy.mEUt = Math.max(1, (int) (copy.mEUt * 0.8));
            GTNHPPRecipeMaps.sCRVRecipes.addRecipe(copy);
        }
    }

    /** Copies all EBF and ABS recipes into the HTRF recipe map at 80% EU cost. */
    private static void copyRecipesToHTRF() {
        for (GTRecipe recipe : RecipeMaps.blastFurnaceRecipes.getAllRecipes()) {
            GTRecipe copy = recipe.copy();
            copy.mEUt = Math.max(1, (int) (copy.mEUt * 0.8));
            GTNHPPRecipeMaps.sHTRFRecipes.addRecipe(copy);
        }
    }
}
