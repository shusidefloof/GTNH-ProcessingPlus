package com.gtnh.processingplus.nei;

import java.util.List;

import gregtech.nei.RecipeDisplayInfo;
import gregtech.nei.formatter.HeatingCoilSpecialValueFormatter;
import gregtech.nei.formatter.INEISpecialInfoFormatter;

public class AARNEIFormatter implements INEISpecialInfoFormatter {

    public static final AARNEIFormatter INSTANCE = new AARNEIFormatter();

    private AARNEIFormatter() {}

    @Override
    public List<String> format(RecipeDisplayInfo info) {
        return HeatingCoilSpecialValueFormatter.INSTANCE.format(info);
    }
}
