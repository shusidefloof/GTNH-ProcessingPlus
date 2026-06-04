package com.gtnh.processingplus.nei;

import java.lang.reflect.Field;

import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;
import gregtech.nei.GTNEIDefaultHandler;

public class SPCRecipeMapFrontend extends RecipeMapFrontend {

    // Station row layout (px): 2 pad + 1 sep + 2 gap + 9 numbers + 16 icons + 4 pad = 34
    static final int EXTRA_HEIGHT = 34;

    // neiProperties is a singleton shared across all handler instances.
    // createNEITemplate is called once per newInstance(), so both modifications
    // must be idempotent — we store the baseline values captured at construction.
    private final int originalBgHeight;
    private int originalWindowHeight = -1; // set on first createNEITemplate call

    public SPCRecipeMapFrontend(BasicUIPropertiesBuilder uiProps, NEIRecipePropertiesBuilder neiProps) {
        super(uiProps, neiProps);
        this.originalBgHeight = neiProperties.recipeBackgroundSize.height;
    }

    @Override
    public ModularWindow.Builder createNEITemplate(GTNEIDefaultHandler.NEITemplateContext context) {
        ModularWindow.Builder builder = super.createNEITemplate(context);

        // Extend NEI background panel — idempotent via originalBgHeight baseline.
        // getDescriptionYOffset() reads this field directly, so description auto-shifts down.
        neiProperties.recipeBackgroundSize = new Size(
            neiProperties.recipeBackgroundSize.width,
            originalBgHeight + EXTRA_HEIGHT);

        // Extend the ModularUI window so the station row sits inside the dark frame.
        // The builder is created fresh on every call, so this never accumulates.
        try {
            Field f = ModularWindow.Builder.class.getDeclaredField("size");
            f.setAccessible(true);
            Size current = (Size) f.get(builder);
            if (originalWindowHeight < 0) originalWindowHeight = current.height;
            builder.setSize(current.width, originalWindowHeight + EXTRA_HEIGHT);
        } catch (Exception ignored) {}

        return builder;
    }
}
