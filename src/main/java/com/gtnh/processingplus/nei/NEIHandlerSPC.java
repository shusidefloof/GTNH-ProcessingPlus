package com.gtnh.processingplus.nei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.machines.spc.SPCModuleType;
import com.gtnh.processingplus.machines.spc.SPCRecipeData;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.GTValues;
import gregtech.nei.GTNEIDefaultHandler;

@SideOnly(Side.CLIENT)
public class NEIHandlerSPC extends GTNEIDefaultHandler {

    // ── Tuning knobs ────────────────────────────────────────────────────────────
    // Change these to adjust the look without touching any logic.

    /** Horizontal inset from window border on each side. Increase to narrow the row. */
    private static final int INNER_PAD = 5;

    /**
     * Y of the separator line, relative to the top of the station area.
     * Decrease to move everything up; must stay >= 0.
     */
    private static final int SEP_OFFSET = 1;

    /**
     * Y of the icon row, relative to the top of the station area.
     * Must be > SEP_OFFSET + 1.
     */
    private static final int ICON_OFFSET = 5;

    /**
     * Y of the tier text row, relative to the top of the station area.
     * Should be >= ICON_OFFSET + 16 (icon height).
     */
    private static final int TIER_OFFSET = 23;

    /** Gap (px) between the last station and the vertical divider. */
    private static final int DIV_GAP_L = 4;
    /** Gap (px) between the vertical divider and the support machine icon. */
    private static final int DIV_GAP_R = 4;

    /** Background colour of each icon slot (ARGB). Matches GT5U's dark slot style. */
    private static final int SLOT_BG = 0xFF373737;
    /** Colour of the separator and divider lines (ARGB). */
    private static final int LINE_COLOR = 0xFF4A4A4A;
    /** Colour of the tier/label text (ARGB). */
    private static final int TEXT_COLOR = 0x999999;

    // ── Fixed geometry ──────────────────────────────────────────────────────────

    private static final int ICON_SIZE = 16;
    private static final int ICON_STEP = 18;

    // ── Constructor ─────────────────────────────────────────────────────────────

    public NEIHandlerSPC() {
        super(GTNHPPRecipeMaps.sSPCRecipes.getDefaultRecipeCategory());
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new NEIHandlerSPC();
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────

    private int areaTop() {
        return getDescriptionYOffset() - SPCRecipeMapFrontend.EXTRA_HEIGHT;
    }

    private int rowWidth(SPCRecipeData data) {
        int w = data.stationTypes.length * ICON_STEP - (ICON_STEP - ICON_SIZE);
        if (data.requiredModule != null) w += DIV_GAP_L + 1 + DIV_GAP_R + ICON_SIZE;
        return w;
    }

    private int rowStartX(SPCRecipeData data) {
        int innerLeft = WINDOW_OFFSET.x + INNER_PAD;
        int innerWidth = modularWindow.getSize().width - 2 * INNER_PAD;
        return innerLeft + Math.max(0, (innerWidth - rowWidth(data)) / 2);
    }

    private int moduleX(SPCRecipeData data) {
        return rowStartX(data) + data.stationTypes.length * ICON_STEP + DIV_GAP_L + 1 + DIV_GAP_R;
    }

    /** The adapter ItemStack shown as the required-module icon (hover gives its name). */
    private static ItemStack moduleAdapterIcon(SPCModuleType type) {
        if (type == null) return null;
        switch (type) {
            case BIO:
                return new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.BIO_ADAPTER);
            case CRYO:
                return new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.CRYO_ADAPTER);
            case QUANTUM:
                return new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.QUANTUM_ADAPTER);
            default:
                return null;
        }
    }

    private static String moduleLabel(SPCModuleType type) {
        if (type == null) return "";
        switch (type) {
            case BIO:
                return "Bio";
            case CRYO:
                return "Cryo";
            case QUANTUM:
                return "Qtm";
            default:
                return "M";
        }
    }

    private static String tierAbbrev(int tier) {
        switch (tier) {
            case 1:
                return "LV";
            case 2:
                return "MV";
            case 3:
                return "HV";
            case 4:
                return "EV";
            case 5:
                return "IV";
            case 6:
                return "LuV";
            case 7:
                return "ZPM";
            case 8:
                return "UV";
            case 9:
                return "UHV";
            case 10:
                return "UEV";
            case 11:
                return "UIV";
            default:
                return "?";
        }
    }

    // ── Hover ───────────────────────────────────────────────────────────────────

    @Override
    public List<PositionedStack> getIngredientStacks(int recipe) {
        List<PositionedStack> stacks = new ArrayList<>(super.getIngredientStacks(recipe));
        if (recipe >= arecipes.size()) return stacks;

        CachedDefaultRecipe cached = (CachedDefaultRecipe) arecipes.get(recipe);
        SPCRecipeData data = SPCRecipeData.get(cached.mRecipe);
        if (data == null) return stacks;

        int iconY = areaTop() + ICON_OFFSET;
        int startX = rowStartX(data);

        for (int i = 0; i < data.stationTypes.length; i++) {
            ItemStack icon = SPCMachineIcons.getStack(data.stationTypes[i], data.stationMinTiers[i]);
            if (icon != null) stacks.add(new PositionedStack(icon, startX + i * ICON_STEP, iconY));
        }
        if (data.requiredModule != null) {
            ItemStack si = moduleAdapterIcon(data.requiredModule);
            if (si != null) stacks.add(new PositionedStack(si, moduleX(data), iconY));
        }
        return stacks;
    }

    // ── Visual ──────────────────────────────────────────────────────────────────

    @Override
    public void drawExtras(int recipe) {
        super.drawExtras(recipe);
        if (recipe >= arecipes.size()) return;

        CachedDefaultRecipe cached = (CachedDefaultRecipe) arecipes.get(recipe);
        SPCRecipeData data = SPCRecipeData.get(cached.mRecipe);
        if (data == null || data.stationTypes.length == 0) return;

        int areaTop = areaTop();
        int sepY = areaTop + SEP_OFFSET;
        int iconY = areaTop + ICON_OFFSET;
        int tierY = areaTop + TIER_OFFSET;

        int innerLeft = WINDOW_OFFSET.x + INNER_PAD;
        int innerRight = WINDOW_OFFSET.x + modularWindow.getSize().width - INNER_PAD;
        int startX = rowStartX(data);

        // Separator line — inset from window border
        Gui.drawRect(innerLeft, sepY, innerRight, sepY + 1, LINE_COLOR);

        // Station sequence
        for (int i = 0; i < data.stationTypes.length; i++) {
            int x = startX + i * ICON_STEP;

            // Dark slot background
            Gui.drawRect(x - 1, iconY - 1, x + ICON_SIZE + 1, iconY + ICON_SIZE + 1, SLOT_BG);

            ItemStack icon = SPCMachineIcons.getStack(data.stationTypes[i], data.stationMinTiers[i]);
            if (icon != null) GuiContainerManager.drawItem(x, iconY, icon);

            // Tier abbreviation below slot, tinted in its GT voltage-tier colour
            String tier = tierAbbrev(data.stationMinTiers[i]);
            int mt = data.stationMinTiers[i];
            String coloredTier = (mt >= 0 && mt < GTValues.TIER_COLORS.length ? GTValues.TIER_COLORS[mt] : "") + tier;
            GuiDraw.fontRenderer.drawString(
                coloredTier,
                x + (ICON_SIZE - GuiDraw.fontRenderer.getStringWidth(tier)) / 2,
                tierY,
                TEXT_COLOR);
        }

        // Required upgrade module: vertical divider + adapter slot + module label
        if (data.requiredModule != null) {
            int divX = startX + data.stationTypes.length * ICON_STEP + DIV_GAP_L;
            int sx = divX + 1 + DIV_GAP_R;

            // Vertical divider spanning icon row
            Gui.drawRect(divX, iconY - 2, divX + 1, iconY + ICON_SIZE + 2, LINE_COLOR);

            // Module short-label centered above slot
            String label = moduleLabel(data.requiredModule);
            GuiDraw.fontRenderer.drawString(
                label,
                sx + (ICON_SIZE - GuiDraw.fontRenderer.getStringWidth(label)) / 2,
                sepY + 2,
                TEXT_COLOR);

            Gui.drawRect(sx - 1, iconY - 1, sx + ICON_SIZE + 1, iconY + ICON_SIZE + 1, SLOT_BG);

            ItemStack si = moduleAdapterIcon(data.requiredModule);
            if (si != null) GuiContainerManager.drawItem(sx, iconY, si);

            // "module" caption below the slot
            String cap = "module";
            GuiDraw.fontRenderer
                .drawString(cap, sx + (ICON_SIZE - GuiDraw.fontRenderer.getStringWidth(cap)) / 2, tierY, TEXT_COLOR);
        }
    }
}
