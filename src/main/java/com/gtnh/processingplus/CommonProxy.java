package com.gtnh.processingplus;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.items.GTNHPPItems;
import com.gtnh.processingplus.loader.MaterialLoader;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;
import com.gtnh.processingplus.recipes.PrPlusRecipes;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import gregtech.api.enums.ItemList;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        GTNHPPBlocks.registerBlocks();
        GTNHPPItems.register();
        GTNHProcessingPlus.LOG.info("GT:NH Processing+ v{} loading", Tags.VERSION);

        MaterialLoader.load();
    }

    public void init(FMLInitializationEvent event) {
        GTNHPPBlocks.registerMachines();
    }

    public void postInit(FMLPostInitializationEvent event) {}

    public void loadComplete(FMLLoadCompleteEvent event) {
        copyRecipesToCRV();
        removeBoardRecipes();
        PrPlusRecipes.init();
    }

    public void serverStarting(FMLServerStartingEvent event) {}

    /**
     * Removes all assembler-based circuit board production recipes so the SPC + photoresist is
     * the only path. Covers Basic, Phenolic, Plastic, Epoxy (new SPC recipes) and the four
     * fiberglass/multifiberglass tiers that already have SPC recipes.
     */
    private static void removeBoardRecipes() {
        ItemStack[] targets = { ItemList.Circuit_Board_Epoxy.get(1), ItemList.Circuit_Board_Epoxy_Advanced.get(1),
            ItemList.Circuit_Board_Fiberglass.get(1), ItemList.Circuit_Board_Fiberglass_Advanced.get(1),
            ItemList.Circuit_Board_Multifiberglass.get(1), ItemList.Circuit_Board_Multifiberglass_Elite.get(1),
            ItemList.Circuit_Board_Wetware.get(1), ItemList.Circuit_Board_Wetware_Extreme.get(1),
            ItemList.Circuit_Board_Bio.get(1), ItemList.Circuit_Board_Bio_Ultra.get(1),
            ItemList.Circuit_Board_Optical.get(1), };
        int removed = 0;
        for (ItemStack target : targets) {
            List<GTRecipe> toRemove = new ArrayList<>();
            for (GTRecipe recipe : RecipeMaps.assemblerRecipes.getAllRecipes()) {
                if (recipe.mOutputs == null || recipe.mOutputs.length == 0) continue;
                ItemStack out = recipe.mOutputs[0];
                if (out == null) continue;
                if (out.getItem() == target.getItem() && out.getItemDamage() == target.getItemDamage()) {
                    toRemove.add(recipe);
                }
            }
            RecipeMaps.assemblerRecipes.getBackend()
                .removeRecipes(toRemove);
            removed += toRemove.size();
        }
        GTNHProcessingPlus.LOG.info("Removed {} vanilla circuit board assembler recipes.", removed);
    }

    /** Copies all ABS recipes into the CRV recipe map at 80% EU cost. */
    private static void copyRecipesToCRV() {
        for (GTRecipe recipe : GTPPRecipeMaps.alloyBlastSmelterRecipes.getAllRecipes()) {
            GTRecipe copy = recipe.copy();
            copy.mEUt = Math.max(1, (int) (copy.mEUt * 0.8));
            GTNHPPRecipeMaps.sCRVRecipes.addRecipe(copy);
        }
    }
}
