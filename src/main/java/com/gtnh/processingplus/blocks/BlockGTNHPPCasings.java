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
    public static final int SPC_CASING = 9;
    public static final int CSC_CASING = 10;
    public static final int BOF_CASING = 11;
    public static final int SPC_BEAM_CASING = 12;
    public static final int SPC_FRAME_CASING = 13;
    // SPC support-bay adapters (route an external upgrade module into the SPC)
    public static final int BIO_ADAPTER = 14;
    public static final int CRYO_ADAPTER = 15;
    public static final int QUANTUM_ADAPTER = 16;
    // External SPC upgrade-module casings
    public static final int BIO_MODULE_CASING = 17;
    public static final int CRYO_MODULE_CASING = 18;
    public static final int QUANTUM_MODULE_CASING = 19;
    public static final int HTRF_REINFORCED_CASING = 20; // after your existing ones
    public static final int CF_COMPOSITE_CASING = 21; // Carbon Fiber Composite Casing (CRC structural shell)
    public static final int CRYOSTAT_VACUUM_CASING = 22; // CAC outer vacuum shell
    public static final int AEROGEL_INSULATION_BLOCK = 23; // CAC inner aerogel lining (load-bearing)
    public static final int RADIOISOTOPE_CASING = 24; // RTG generator shell (lead-shielded)
    public static final int ISOTOPIC_DOPING_CASING = 25; // CIDC chamber shell
    public static final int HYBRID_PHASE_CASING = 26; // HPR reactor shell
    public static final int SUBATOMIC_PATTERNING_CASING = 27; // SPU shell
    public static final int NUM_CASINGS = 28;

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
        "Spectral Isolation Casing",             // SPC
        "Cryogenic Column Casing",               // CSC
        "Basic Oxygen Furnace Casing",           // BOF
        "Photonic Alignment Casing",             // SPC beam column
        "Spectral Frame Casing",                 // SPC structural frame
        "Bio-Lithography Adapter",               // SPC support-bay port → Bio Module
        "Cryo-Stabilization Adapter",            // SPC support-bay port → Cryo Module
        "Quantum Alignment Adapter",             // SPC support-bay port → Quantum Module
        "Bio Lithography Module Casing",         // Bio Module multiblock
        "Cryo Stabilization Module Casing",      // Cryo Module multiblock
        "Quantum Alignment Module Casing",       // Quantum Module multiblock
        "Heat-Treated Chemical Casing",  // HTRF reinforced
        "Carbon Fiber Composite Casing", // CRC structural shell
        "Cryostat Vacuum Casing",        // CAC outer shell
        "Aerogel Insulation Block",      // CAC inner lining
        "Radioisotope Thermoelectric Casing", // RTG shell
        "Isotopic Doping Casing",             // CIDC shell
        "Hybrid Phase Casing",                // HPR shell
        "Subatomic Patterning Casing",        // SPU shell
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
        "gregtech:iconsets/MACHINE_CASING_STABLE_TITANIUM",     // SPC: clean precision chamber
        "gregtech:iconsets/MACHINE_CASING_CLEAN_STAINLESSSTEEL", // CSC: cryogenic stainless column
        "gregtech:iconsets/MACHINE_CASING_SOLID_STEEL",          // BOF: industrial steel converter
        "gregtech:iconsets/MACHINE_HEATPROOFCASING",             // SPC beam: photonic alignment column (dark)
        "gregtech:iconsets/MACHINE_CASING_SOLID_STEEL",          // SPC frame: structural frame (grey)
        "gregtech:iconsets/MACHINE_CASING_CLEAN_STAINLESSSTEEL", // Bio adapter (clean white)
        "gregtech:iconsets/MACHINE_CASING_FROST_PROOF",          // Cryo adapter (icy)
        "gregtech:iconsets/MACHINE_CASING_FUSION_GLASS",         // Quantum adapter (energetic)
        "gregtech:iconsets/MACHINE_CASING_CLEAN_STAINLESSSTEEL", // Bio module casing
        "gregtech:iconsets/MACHINE_CASING_FROST_PROOF",          // Cryo module casing
        "gregtech:iconsets/MACHINE_CASING_RADIATIONPROOF",       // Quantum module casing
        "gregtech:iconsets/MACHINE_CASING_CHEMICALLY_INERT", // SiC color, same as HTRF casing
        "gregtech:iconsets/MACHINE_HEATPROOFCASING", // CF composite: dark woven look (placeholder)
        "gregtech:iconsets/MACHINE_CASING_FROST_PROOF",          // CAC vacuum shell (frost)
        "gregtech:iconsets/MACHINE_CASING_CLEAN_STAINLESSSTEEL", // CAC aerogel lining (white)
        "gregtech:iconsets/MACHINE_CASING_RADIATIONPROOF",       // RTG shell (lead-shielded)
        "gregtech:iconsets/MACHINE_CASING_CLEAN_STAINLESSSTEEL", // CIDC shell (clean precision)
        "gregtech:iconsets/MACHINE_CASING_FUSION_GLASS",         // HPR shell (energetic plasma/bio)
        "gregtech:iconsets/MACHINE_CASING_RADIATIONPROOF",       // SPU shell (high-tech)
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
