package com.gtnh.processingplus;

import com.gtnh.processingplus.nei.NEIHandlerSPC;

import codechicken.nei.api.API;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        API.registerRecipeHandler(new NEIHandlerSPC());
        API.registerUsageHandler(new NEIHandlerSPC());
    }
}
