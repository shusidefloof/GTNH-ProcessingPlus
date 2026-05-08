package com.gtnh.processingplus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnh.processingplus.materials.GTNHPPMaterials;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import gregtech.api.enums.Materials;

@Mod(
    modid = GTNHProcessingPlus.MODID,
    version = Tags.VERSION,
    name = "GT:NH Processing+",
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:gregtech;after:gtplusplus")
public class GTNHProcessingPlus {

    public static final String MODID = "gtnhpp";
    public static final Logger LOG = LogManager.getLogger(MODID);

    // Registers our IMaterialHandler before GT's preInit calls Materials.init()
    static {
        GTNHPPMaterials.init();
    }

    @SidedProxy(clientSide = "com.gtnh.processingplus.ClientProxy", serverSide = "com.gtnh.processingplus.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.loadComplete(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
