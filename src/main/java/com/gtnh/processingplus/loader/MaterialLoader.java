package com.gtnh.processingplus.loader;

import com.gtnh.processingplus.materials.PrPMaterials;

import bartworks.API.WerkstoffAdderRegistry;

public class MaterialLoader {

    public static void load() {

        WerkstoffAdderRegistry.addWerkstoffAdder(new PrPMaterials());
    }

}
