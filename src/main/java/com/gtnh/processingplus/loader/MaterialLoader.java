package com.gtnh.processingplus.loader;

import bartworks.API.WerkstoffAdderRegistry;
import com.gtnh.processingplus.materials.PrPMaterials;

public class MaterialLoader {

    public static void load() {

        WerkstoffAdderRegistry.addWerkstoffAdder(new PrPMaterials());
    }

}
