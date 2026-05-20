package com.gtnh.processingplus.machines;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;

public enum MachineType {

    CHEMICAL_REACTOR("chemicalreactor"),
    ELECTROLYZER("electrolyzer"),
    ASSEMBLER("assembler"),
    LASER_ENGRAVER("laserengraver"),
    CHEMICAL_BATH("chemicalbath"),
    FORMING_PRESS("formingpress"),
    ELECTRIC_FURNACE("electricfurnace"),
    CENTRIFUGE("centrifuge"),
    MIXER("mixer"),
    NONE,
    UNKNOWN;

    private final String[] nameFragments;

    MachineType(String... fragments) {
        this.nameFragments = fragments;
    }

    MachineType() {
        this.nameFragments = new String[0];
    }

    public static MachineType detect(IMetaTileEntity mte) {
        if (mte == null) return NONE;
        String name = mte.getMetaName();
        if (name == null) return UNKNOWN;
        // Normalize: lowercase and strip separators so "Chemical_Bath_LV", "chemicalbath.01", etc. all match
        String normalized = name.toLowerCase()
            .replaceAll("[_.\\- ]", "");
        for (MachineType type : values()) {
            if (type == NONE || type == UNKNOWN) continue;
            for (String fragment : type.nameFragments) {
                if (normalized.contains(fragment)) return type;
            }
        }
        return UNKNOWN;
    }
}
