package com.gtnh.processingplus.nei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.EnumChatFormatting;

import gregtech.api.util.GTRecipe;
import gregtech.nei.RecipeDisplayInfo;
import gregtech.nei.formatter.INEISpecialInfoFormatter;

public class HPRNEIFormatter implements INEISpecialInfoFormatter {

    public static final HPRNEIFormatter INSTANCE = new HPRNEIFormatter();

    private static final String[] COIL_NAMES = { "Any", "Tier I", "Tier II", "Tier III", "Tier IV" };

    private HPRNEIFormatter() {}

    @Override
    public List<String> format(RecipeDisplayInfo info) {
        GTRecipe recipe = info.recipe;
        // mSpecialValue bit layout: bits 0-3 = coil tier, bits 4-7 = freq tag, bits 8-11 = min freq required
        int coilTier = recipe.mSpecialValue & 0xF;
        int freqTag = (recipe.mSpecialValue >> 4) & 0xF;
        int freqRequired = (recipe.mSpecialValue >> 8) & 0xF;

        List<String> lines = new ArrayList<>();

        String coilStr = (coilTier <= 0 || coilTier >= COIL_NAMES.length) ? COIL_NAMES[0] : COIL_NAMES[coilTier];
        lines.add(EnumChatFormatting.GOLD + "FRF Coil: " + EnumChatFormatting.WHITE + coilStr);

        if (freqTag > 0) {
            StringBuilder freqLine = new StringBuilder().append(EnumChatFormatting.AQUA)
                .append("Resonance: ")
                .append(EnumChatFormatting.WHITE)
                .append("Tunes field to Freq ")
                .append(freqTag);
            if (freqRequired > 0) {
                freqLine.append(EnumChatFormatting.GRAY)
                    .append("  (requires field ≥ ")
                    .append(freqRequired)
                    .append(")");
            }
            lines.add(freqLine.toString());
        }

        return lines;
    }
}
