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
    public static final int SCD_CASING = 5;
    public static final int CRV_CASING = 6;
    public static final int HBN_CERAMIC_BLOCK = 7;
    public static final int SPC_CASING = 8;
    public static final int CSC_CASING = 9;
    public static final int BOF_CASING = 10;
    public static final int SPC_BEAM_CASING = 11;
    public static final int SPC_FRAME_CASING = 12;
    public static final int BIO_ADAPTER = 13;
    public static final int CRYO_ADAPTER = 14;
    public static final int QUANTUM_ADAPTER = 15;
    public static final int BIO_MODULE_CASING = 16;
    public static final int CRYO_MODULE_CASING = 17;
    public static final int QUANTUM_MODULE_CASING = 18;
    public static final int HTRF_REINFORCED_CASING = 19; // after your existing ones
    public static final int CF_COMPOSITE_CASING = 20; // Carbon Fiber Composite Casing
    public static final int CRYOSTAT_VACUUM_CASING = 21; // CAC outer vacuum shell
    public static final int AEROGEL_INSULATION_BLOCK = 22; // CAC inner aerogel lining
    public static final int RADIOISOTOPE_CASING = 23; // RTG generator shell
    public static final int ISOTOPIC_DOPING_CASING = 24; // CIDC chamber shell
    public static final int HYBRID_PHASE_CASING = 25; // HPR reactor shell
    public static final int SUBATOMIC_PATTERNING_CASING = 26; // SPU shell
    public static final int DAF_CASING_LUV = 27; // DAF LuV-tier chamber shell
    public static final int DAF_CASING_UV = 28; // DAF UV-tier chamber shell
    public static final int DAF_CASING_UEV = 29; // DAF UEV-tier chamber shell
    public static final int CSTR_CASING = 30; // Continuous Stirred Tank Reactor shell
    public static final int PRESSURE_VESSEL_RING_CASING = 31; // HPSF main pressure vessel ring
    public static final int NUM_CASINGS = 32;

    // @formatter:off
    private static final String[] DISPLAY_NAMES = {
        "Silicon Carbide Ceramic Casing",       // HTRF
        "Hardened Pressure Vessel Casing",       // HPSF
        "Dual-Sealed Atmosphere Casing",         // DAF
        "Chemically Inert Reaction Vessel",      // PCV
        "Precision Cleanroom Casing",            // PFC
        "High-Pressure Containment Casing",      // SCD
        "Iridium-Reinforced Reactor Casing",     // CRV outer shell
        "Hexagonal Boron Nitride Ceramic Block", // CRV inner lining
        "Spectral Isolation Casing",             // SPC
        "Cryogenic Column Casing",               // CSC
        "Basic Oxygen Furnace Casing",           // BOF
        "Photonic Alignment Casing",             // SPC beam column
        "Spectral Frame Casing",                 // SPC structural frame
        "Bio-Lithography Adapter",               // SPC support-bay port -> Bio Module
        "Cryo-Stabilization Adapter",            // SPC support-bay port -> Cryo Module
        "Quantum Alignment Adapter",             // SPC support-bay port -> Quantum Module
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
        "Advanced Atmosphere Casing",         // DAF LuV chamber
        "Pristine Atmosphere Casing",         // DAF UV chamber
        "Absolute Atmosphere Casing",         // DAF UEV chamber
        "Agitated Reaction Vessel Casing",    // CSTR shell
        "Pressure Vessel Ring Casing",        // HPSF main pressure vessel ring
    };

    // Borrow GT5U's existing block textures as placeholders until custom art is made.
    // Each path references an existing texture in assets/gregtech/textures/blocks/iconsets/
    private static final String[] TEXTURE_NAMES = {
        "gregtech:iconsets/MACHINE_HEATPROOFCASING",            // HTRF
        "gregtech:iconsets/MACHINE_CASING_ROBUST_TUNGSTENSTEEL",// HPSF
        "gregtech:iconsets/MACHINE_CASING_SOLID_STEEL",         // DAF
        "gregtech:iconsets/MACHINE_CASING_CLEAN_STAINLESSSTEEL",// PCV
        "gregtech:iconsets/MACHINE_CASING_STABLE_TITANIUM",     // PFC
        "gregtech:iconsets/MACHINE_CASING_STABLE_TITANIUM",     // SCD
        "gregtech:iconsets/MACHINE_CASING_ROBUST_TUNGSTENSTEEL",// CRV
        "gregtech:iconsets/MACHINE_CASING_CLEAN_STAINLESSSTEEL",// hBN
        "gregtech:iconsets/MACHINE_CASING_STABLE_TITANIUM",     // SPC
        "gregtech:iconsets/MACHINE_CASING_CLEAN_STAINLESSSTEEL", // CSC
        "gregtech:iconsets/MACHINE_CASING_SOLID_STEEL",          // BOF
        "gregtech:iconsets/MACHINE_CASING_CLEAN_STAINLESSSTEEL", // SPC beam
        "gregtech:iconsets/MACHINE_CASING_SOLID_STEEL",          // SPC frame
        "gregtech:iconsets/MACHINE_CASING_CLEAN_STAINLESSSTEEL", // Bio adapter
        "gregtech:iconsets/MACHINE_CASING_FROST_PROOF",          // Cryo adapter
        "gregtech:iconsets/MACHINE_CASING_FUSION_GLASS",         // Quantum adapter
        "gregtech:iconsets/MACHINE_CASING_CLEAN_STAINLESSSTEEL", // Bio module casing
        "gregtech:iconsets/MACHINE_CASING_FROST_PROOF",          // Cryo module casing
        "gregtech:iconsets/MACHINE_CASING_RADIATIONPROOF",       // Quantum module casing
        "gregtech:iconsets/MACHINE_CASING_CHEMICALLY_INERT", //HTRF casing
        "gregtech:iconsets/MACHINE_HEATPROOFCASING", // CF composite (placeholder)
        "gregtech:iconsets/MACHINE_CASING_FROST_PROOF",          // CAC vacuum shell
        "gregtech:iconsets/MACHINE_CASING_CLEAN_STAINLESSSTEEL", // CAC aerogel lining
        "gregtech:iconsets/MACHINE_CASING_RADIATIONPROOF",       // RTG shell
        "gregtech:iconsets/MACHINE_CASING_CLEAN_STAINLESSSTEEL", // CIDC shell
        "gregtech:iconsets/MACHINE_CASING_FUSION_GLASS",         // HPR shell
        "gregtech:iconsets/MACHINE_CASING_RADIATIONPROOF",       // SPU shell
        "gregtech:iconsets/MACHINE_CASING_STABLE_TITANIUM",      // DAF LuV
        "gregtech:iconsets/MACHINE_CASING_RADIATIONPROOF",       // DAF UV
        "gregtech:iconsets/MACHINE_CASING_FUSION_GLASS",         // DAF UEV
        "gregtech:iconsets/MACHINE_CASING_CLEAN_STAINLESSSTEEL", // CSTR
        "gregtech:iconsets/MACHINE_CASING_ROBUST_TUNGSTENSTEEL", // HPSF ring
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
