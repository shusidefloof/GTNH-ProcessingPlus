package com.gtnh.processingplus.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * One shared item holding every single-use chain intermediate as a metadata sub-item (see
 * {@link Intermediate}). One item id + one texture folder replaces ~47 individual Werkstoffe.
 *
 * <p>Mirrors {@code BlockGTNHPPCasings}: per-meta display names and per-meta icons registered in a
 * loop. Textures live at {@code assets/gtnhprp/textures/items/intermediates/<reg>.png}.
 */
public class ItemGTNHPPIntermediates extends Item {

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemGTNHPPIntermediates() {
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("gtnhpp.intermediate");
        setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int meta = stack.getItemDamage();
        Intermediate[] values = Intermediate.values();
        if (meta >= 0 && meta < values.length) return values[meta].display;
        return "GTNHPP Intermediate";
    }

    /**
     * While {@code true}, every meta borrows a category-appropriate vanilla icon so nothing renders
     * as the missing-texture checkerboard. Flip to {@code false} once real art exists for ALL entries
     * at {@code assets/gtnhprp/textures/items/intermediates/<reg>.png}.
     */
    private static final boolean USE_PLACEHOLDERS = true;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        Intermediate[] values = Intermediate.values();
        icons = new IIcon[values.length];
        for (int i = 0; i < values.length; i++) {
            icons[i] = register.registerIcon(
                USE_PLACEHOLDERS ? placeholderIcon(values[i]) : "gtnhprp:intermediates/" + values[i].reg);
        }
    }

    /** A vanilla stand-in chosen from the entry's reg name, so placeholders at least look the part. */
    @SideOnly(Side.CLIENT)
    private static String placeholderIcon(Intermediate it) {
        String s = it.reg;
        if (s.contains("gel")) return "minecraft:slimeball";
        if (s.contains("crystal") || s.contains("shard")) return "minecraft:quartz";
        if (s.contains("fiber") || s.contains("tow")) return "minecraft:string";
        if (s.contains("adamantium") || s.contains("clump") || s.contains("compact") || s.contains("sintering"))
            return "minecraft:iron_ingot";
        if (s.contains("concentrate") || s.contains("ore")) return "minecraft:redstone_dust";
        if (s.contains("film") || s.contains("triflate") || s.contains("salt") || s.contains("sulfate")
            || s.contains("acetate") || s.contains("oxime") || s.contains("acid"))
            return "minecraft:paper";
        // powders / oxides / slags / residues / blends / carbide / nitride / default → grey dust
        return "minecraft:gunpowder";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        if (icons != null && meta >= 0 && meta < icons.length) return icons[meta];
        return (icons != null && icons.length > 0) ? icons[0] : null;
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (Intermediate v : Intermediate.values()) {
            list.add(new ItemStack(item, 1, v.ordinal()));
        }
    }
}
