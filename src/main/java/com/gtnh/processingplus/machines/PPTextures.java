package com.gtnh.processingplus.machines;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;

/**
 * Processing+ controller overlays. Files live in {@code assets/gtnhprp/textures/blocks/overlays/<machine>/}. GT only
 * loads icons that exist before textures are stitched, so {@link #init()} is called from preInit to create them.
 */
public final class PPTextures {

    private static final String DOMAIN = "gtnhprp";

    public static final IIconContainer BOF_FRONT = overlay("bof/front");
    public static final IIconContainer BOF_FRONT_ACTIVE = overlay("bof/front_active");
    public static final IIconContainer BOF_FRONT_GLOW = overlay("bof/front_glow");
    public static final IIconContainer BOF_FRONT_ACTIVE_GLOW = overlay("bof/front_active_glow");

    private PPTextures() {}

    private static IIconContainer overlay(String path) {
        return Textures.BlockIcons.custom(DOMAIN, "overlays/" + path);
    }

    /** Loads this class so the icons above are registered in time. */
    public static void init() {}
}
