package com.gtnh.processingplus.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GTNHPPMetaItem extends Item {

    public static final int INTER_OFFSET = 0;
    public static final int INTER_ADIPIC_ACID = 0;
    public static final int INTER_HMD = 1;
    public static final int INTER_ADIPONITRILE = 2;
    public static final int INTER_LACTIC_ACID = 3;
    public static final int INTER_LACTIDE = 4;
    public static final int INTER_ACRYLONITRILE = 5;
    public static final int INTER_PAN_FIBER = 6;
    public static final int INTER_STABILIZED_PAN = 7;
    public static final int INTER_CF_TOW = 8;
    public static final int INTER_PMDA = 9;
    public static final int INTER_ODA = 10;
    public static final int INTER_PAA_FILM = 11;
    public static final int INTER_BORON_OXIDE = 12;
    public static final int INTER_BORON_CARBIDE = 13;
    public static final int INTER_CRUDE_HBN = 14;
    public static final int INTER_WET_SILICA_GEL = 15;
    public static final int INTER_COUNT = 16;

    // @formatter:off
    private static final String[] INTER_DISPLAY_NAMES = {
        "Adipic Acid",
        "Hexamethylenediamine",
        "Adiponitrile",
        "Lactic Acid",
        "Lactide",
        "Acrylonitrile",
        "Polyacrylonitrile Fiber",
        "Stabilized PAN Fiber",
        "Carbon Fiber Tow",
        "PMDA",
        "ODA (4,4'-Oxydianiline)",
        "Polyamic Acid Film",
        "Boron Trioxide",
        "Boron Carbide",
        "Crude Hexagonal Boron Nitride",
        "Wet Silica Gel",
    };

    private static final int[] INTER_COLORS = {
        0xFFFFD0, 0xF0F0FF, 0xD0FFD0, 0xFFE0E0, 0xFFFFFF, 0xE8FFE8,
        0xF0ECD4, 0xB8A882, 0x111111,
        0xFFFBE6, 0xFFE4C4, 0xFFD700,
        0xF8F8F2, 0x2A2A2A, 0xEEECE8, 0xBBDDFF,
    };
    // @formatter:on

    // Icon indices matching greg texture names
    private static final int ICON_DUST  = 0;
    private static final int ICON_STICK = 1;
    private static final int ICON_PLATE = 2;
    private static final int ICON_COUNT = 3;

    private static final String[] ICON_TEXTURES = {
        "gregtech:materialicons/DULL/dust",
        "gregtech:materialicons/DULL/stick",
        "gregtech:materialicons/DULL/plate",
    };

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public GTNHPPMetaItem() {
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("gtnhpp.intermediate");
    }

    public static int interMeta(int interIndex) {
        return INTER_OFFSET + interIndex;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[ICON_COUNT];
        for (int i = 0; i < ICON_COUNT; i++) icons[i] = register.registerIcon(ICON_TEXTURES[i]);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        if (meta == INTER_PAN_FIBER || meta == INTER_STABILIZED_PAN || meta == INTER_CF_TOW)
            return icons[ICON_STICK];
        if (meta == INTER_PAA_FILM)
            return icons[ICON_PLATE];
        return icons[ICON_DUST];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        int meta = stack.getItemDamage();
        if (meta >= 0 && meta < INTER_COUNT) return INTER_COLORS[meta];
        return 0xFFFFFF;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int meta = stack.getItemDamage();
        if (meta >= 0 && meta < INTER_COUNT) return INTER_DISPLAY_NAMES[meta];
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {}

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < INTER_COUNT; i++) list.add(new ItemStack(item, 1, interMeta(i)));
    }

    public static void registerItems(GTNHPPMetaItem item) {
        for (int i = 0; i < INTER_COUNT; i++) {
            GTNHPPItemList.INTER_MAP[i].set(new ItemStack(item, 1, interMeta(i)));
        }
    }
}
