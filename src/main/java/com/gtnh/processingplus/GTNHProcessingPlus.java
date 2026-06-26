package com.gtnh.processingplus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(
    modid = GTNHProcessingPlus.MODID,
    version = Tags.VERSION,
    name = "GT:NH Processing+",
    acceptedMinecraftVersions = "",
    // Force this mod to load AFTER GregTech's and Bartwork's internal material init
    dependencies = "required-after:gregtech;" + "required-after:bartworks;" + "after:sciencenotleisure;")
public class GTNHProcessingPlus {

    public static final String MODID = "gtnhprp";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @SidedProxy(clientSide = "com.gtnh.processingplus.ClientProxy", serverSide = "com.gtnh.processingplus.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println(">>> MAIN MOD PREINIT FIRED");
        System.out.println("PROXY IS: " + proxy);
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        System.out.println("GT MATERIAL COUNT: " + gregtech.api.GregTechAPI.sGeneratedMaterials.length);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.loadComplete(event);
    }
}
