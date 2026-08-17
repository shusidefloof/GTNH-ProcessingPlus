package com.gtnh.processingplus;

import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.event.TooltipHandler;
import com.gtnh.processingplus.items.GTNHPPItems;
import com.gtnh.processingplus.loader.MaterialLoader;
import com.gtnh.processingplus.loader.QuestLoader;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;
import com.gtnh.processingplus.recipes.PrPlusRecipes;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.Mods;
import gregtech.api.enums.Textures;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        GTNHPPBlocks.registerBlocks();
        GTNHPPItems.register();
        GTNHProcessingPlus.LOG.info("GT:NH Processing+ v{} loading", Tags.VERSION);

        MaterialLoader.load();
        MinecraftForge.EVENT_BUS.register(new TooltipHandler());

        if (Mods.BetterQuesting.isModLoaded()) {
            QuestLoader.registry();
        }
    }

    public void init(FMLInitializationEvent event) {
        GTNHPPBlocks.registerMachines();
    }

    public void postInit(FMLPostInitializationEvent event) {
        registerExternalCasingTextures();
    }

    /**
     * GoodGenerator's {@code pressureResistantWalls} (used as HPR's wall block) renders its own icon directly
     * and was never plugged into GT5U's shared {@code casingTexturePages} registry, so no existing composite
     * casing index can reproduce it for hatches/controllers. We register it ourselves into an unused page so
     * {@code MTE_HPR}'s hatches and controller faces can actually match the wall they sit in.
     */
    private static void registerExternalCasingTextures() {
        Block wall = GameRegistry.findBlock("GoodGenerator", "pressureResistantWalls");
        if (wall == null) {
            GTNHProcessingPlus.LOG.warn("GoodGenerator:pressureResistantWalls not found — HPR casing texture will not match its wall");
            return;
        }
        GTUtility.addTexturePage((byte) 100);
        Textures.BlockIcons.setCasingTextureForId(
            com.gtnh.processingplus.machines.MTE_HPR.PRESSURE_RESISTANT_WALLS_CASING_INDEX,
            TextureFactory.of(wall, 0));
    }

    public void loadComplete(FMLLoadCompleteEvent event) {
        try {
            copyRecipesToCRV();
        } catch (Throwable t) {
            GTNHProcessingPlus.LOG.error("ABS→CRV recipe copy failed (GT++ present?)", t);
        }
        try {
            PrPMaterials.resolveDeferredExternalMaterials();
            PrPlusRecipes.init();
        } catch (Throwable t) {
            GTNHProcessingPlus.LOG.error("Recipe registration failed", t);
        }
        try {
            com.gtnh.processingplus.recipes.RecipeSwaps.run();
        } catch (Throwable t) {
            GTNHProcessingPlus.LOG.error("IV-hull RHEA swap failed", t);
        }
        try {
            com.gtnh.processingplus.recipes.chains.infrastructure.CACRecipes.migrateSuperconductors();
        } catch (Throwable t) {
            GTNHProcessingPlus.LOG.error("CAC superconductor migration failed", t);
        }
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
}
