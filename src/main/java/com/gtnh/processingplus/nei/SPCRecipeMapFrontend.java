package com.gtnh.processingplus.nei;

import java.lang.reflect.Field;

import com.gtnewhorizons.modularui.api.math.Size;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;

public class SPCRecipeMapFrontend extends RecipeMapFrontend {

    // Station row layout (px): 2 pad + 1 sep + 2 gap + 9 numbers + 16 icons + 4 pad = 34
    static final int EXTRA_HEIGHT = 34;

    public SPCRecipeMapFrontend(BasicUIPropertiesBuilder uiProps, NEIRecipePropertiesBuilder neiProps) {
        super(uiProps, neiProps);
    }

    /**
     * GT5U (2.9) makes {@code recipeBackgroundSize} final and bakes it at construction — and the NEI
     * window plus {@code getDescriptionYOffset()} both derive from it. So we extend the panel ONCE
     * here, via the intended {@code modifyNEIProperties} hook (called by the base constructor), and
     * the dark frame, the ModularUI window, and the description offset all follow automatically.
     * The builder has a setter but no getter for the size, so we read the current value reflectively
     * (RecipeMapBuilder has already populated it with the SPC's slot-based dimensions) and add the
     * station row's height to it.
     */
    @Override
    protected NEIRecipePropertiesBuilder modifyNEIProperties(NEIRecipePropertiesBuilder builder) {
        builder = super.modifyNEIProperties(builder);
        try {
            Field f = NEIRecipePropertiesBuilder.class.getDeclaredField("recipeBackgroundSize");
            f.setAccessible(true);
            Size current = (Size) f.get(builder);
            builder.recipeBackgroundSize(new Size(current.width, current.height + EXTRA_HEIGHT));
        } catch (Exception ignored) {}
        return builder;
    }
}
