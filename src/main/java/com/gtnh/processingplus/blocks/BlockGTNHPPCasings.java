package com.gtnh.processingplus.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockGTNHPPCasings extends Block {

    public static final int HTRF_CASING = 0;
    public static final int HPSF_CASING = 1;
    public static final int DAF_CASING = 2;
    public static final int PCV_CASING = 3;
    public static final int PFC_CASING = 4;
    public static final int AAR_CASING = 5;
    public static final int SCD_CASING = 6;
    public static final int CRV_CASING = 7;
    public static final int HBN_CERAMIC_BLOCK = 8;
    public static final int NUM_CASINGS = 9;

    // @formatter:off
    private static final String[] DISPLAY_NAMES = {
        "Silicon Carbide Ceramic Casing",       // HTRF
        "Hardened Pressure Vessel Casing",       // HPSF
        "Dual-Sealed Atmosphere Casing",         // DAF
        "Chemically Inert Reaction Vessel",      // PCV
        "Precision Cleanroom Casing",            // PFC
        "Corrosion-Resistant Reactor Casing",    // AAR
        "High-Pressure Containment Casing",      // SCD
        "Iridium-Reinforced Reactor Casing",     // CRV outer shell
        "Hexagonal Boron Nitride Ceramic Block", // CRV inner lining
    };

    // Borrow GT5U's existing block textures as placeholders until custom art is made.
    // Each path references an existing texture in assets/gregtech/textures/blocks/iconsets/
    private static final String[] TEXTURE_NAMES = {
        "gregtech:iconsets/MACHINE_HEATPROOFCASING",            // HTRF: SiC ceramic (dark, heat-proof)
        "gregtech:iconsets/MACHINE_CASING_ROBUST_TUNGSTENSTEEL",// HPSF: pressure vessel (tungsten)
        "gregtech:iconsets/MACHINE_CASING_SOLID_STEEL",         // DAF: industrial steel
        "gregtech:iconsets/MACHINE_CASING_CLEAN_STAINLESSSTEEL",// PCV: chemical / clean steel
        "gregtech:iconsets/MACHINE_CASING_STABLE_TITANIUM",     // PFC: precision titanium
        "gregtech:iconsets/MACHINE_CASING_RADIATIONPROOF",      // AAR: corrosion/radiation proof
        "gregtech:iconsets/MACHINE_CASING_STABLE_TITANIUM",     // SCD: high-pressure titanium
        "gregtech:iconsets/MACHINE_CASING_ROBUST_TUNGSTENSTEEL",// CRV outer: LuV-tier reactor shell
        "gregtech:iconsets/MACHINE_CASING_CLEAN_STAINLESSSTEEL",// hBN ceramic: white/inert lining
    };
    // @formatter:on

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public BlockGTNHPPCasings() {
        super(Material.iron);
        setHardness(5.0f);
        setResistance(10.0f);
        setBlockName("gtnhpp.casings");
        setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icons = new IIcon[NUM_CASINGS];
        for (int i = 0; i < NUM_CASINGS; i++) {
            icons[i] = register.registerIcon(TEXTURE_NAMES[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta >= 0 && meta < NUM_CASINGS) return icons[meta];
        return icons[0];
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < NUM_CASINGS; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    public String getLocalizedName(int meta) {
        if (meta >= 0 && meta < DISPLAY_NAMES.length) return DISPLAY_NAMES[meta];
        return "GTNHPP Casing";
    }
}
