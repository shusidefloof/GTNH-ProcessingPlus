package com.gtnh.processingplus.machines;

import net.minecraft.util.EnumChatFormatting;

import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.tooltip.TooltipHelper;

/** Tooltip lines shared by several machines, worded exactly like GT's own Electric Blast Furnace. */
final class PPTooltips {

    private PPTooltips() {}

    /** Heat capacity / EU discount / perfect overclock lines for the coil-heated machines. */
    static void addHeatInfo(MultiblockTooltipBuilder tt) {
        tt.addInfo(
            "Increases Heat by " + EnumChatFormatting.RED
                + "100K"
                + EnumChatFormatting.GRAY
                + " for every "
                + TooltipHelper.tierText("Voltage")
                + " tier past "
                + EnumChatFormatting.AQUA
                + "MV")
            .addInfo(
                "Reduces " + TooltipHelper.effText("EU Usage")
                    + " by "
                    + EnumChatFormatting.WHITE
                    + "5%"
                    + EnumChatFormatting.GRAY
                    + " every "
                    + EnumChatFormatting.RED
                    + "900K"
                    + EnumChatFormatting.GRAY
                    + " above the recipe requirement")
            .addInfo(
                "Every " + EnumChatFormatting.RED
                    + "1800K"
                    + EnumChatFormatting.GRAY
                    + " over the recipe requirement grants 1 "
                    + EnumChatFormatting.LIGHT_PURPLE
                    + "Perfect Overclock");
    }
}
