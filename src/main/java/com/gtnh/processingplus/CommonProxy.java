package com.gtnh.processingplus;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;

import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.event.TooltipHandler;
import com.gtnh.processingplus.items.GTNHPPItems;
import com.gtnh.processingplus.loader.MaterialLoader;
import com.gtnh.processingplus.loader.QuestLoader;
import com.gtnh.processingplus.materials.PrPMaterials;
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
import gregtech.api.util.GTUtility;

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
     *
     * Page 100 is shared with {@code MTE_BOF}'s Solid Steel registration below (different slot), since no
     * existing GT5U casingTexturePages slot renders as MACHINE_CASING_SOLID_STEEL either, every vanilla
     * BlockCasingsN class only falls back to it as an unreachable default, never a real placeable meta.
     */
    private static void registerExternalCasingTextures() {
        GTUtility.addTexturePage((byte) 100);

        Block wall = GameRegistry.findBlock("GoodGenerator", "pressureResistantWalls");
        if (wall == null) {
            GTNHProcessingPlus.LOG
                .warn("GoodGenerator:pressureResistantWalls not found: HPR casing texture will not match its wall");
        } else {
            Textures.BlockIcons.setCasingTextureForId(
                com.gtnh.processingplus.machines.MTE_HPR.PRESSURE_RESISTANT_WALLS_CASING_INDEX,
                TextureFactory.of(wall, 0));
        }

        Textures.BlockIcons.setCasingTextureForId(
            com.gtnh.processingplus.machines.MTE_BOF.SOLID_STEEL_MACHINE_CASING_INDEX,
            TextureFactory.of(Textures.BlockIcons.MACHINE_CASING_SOLID_STEEL));
    }

    public void loadComplete(FMLLoadCompleteEvent event) {
        try {
            PrPMaterials.resolveDeferredExternalMaterials();
            PrPlusRecipes.init();
        } catch (Throwable t) {
            GTNHProcessingPlus.LOG.error("Recipe registration failed", t);
        }
        try {
            com.gtnh.processingplus.recipes.RecipeSwaps.run();
        } catch (Throwable t) {
            GTNHProcessingPlus.LOG.error("Recipe swaps failed", t);
        }
        try {
            com.gtnh.processingplus.recipes.chains.infrastructure.CACRecipes.migrateSuperconductors();
        } catch (Throwable t) {
            GTNHProcessingPlus.LOG.error("CAC superconductor migration failed", t);
        }
    }

    public void serverStarting(FMLServerStartingEvent event) {}
}
