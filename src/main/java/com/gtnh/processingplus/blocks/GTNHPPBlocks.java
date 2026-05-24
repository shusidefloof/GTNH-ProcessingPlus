package com.gtnh.processingplus.blocks;

import com.gtnh.processingplus.machines.MTE_AAR;
import com.gtnh.processingplus.machines.MTE_BOF;
import com.gtnh.processingplus.machines.MTE_CRV;
import com.gtnh.processingplus.machines.MTE_CSC;
import com.gtnh.processingplus.machines.MTE_DAF;
import com.gtnh.processingplus.machines.MTE_HPSF;
import com.gtnh.processingplus.machines.MTE_HTRF;
import com.gtnh.processingplus.machines.MTE_PCV;
import com.gtnh.processingplus.machines.MTE_PFC;
// import com.gtnh.processingplus.machines.MTE_PrimitiveAirIntake;
import com.gtnh.processingplus.machines.MTE_SCD;
import com.gtnh.processingplus.machines.MTE_SPC;

import cpw.mods.fml.common.registry.GameRegistry;

public class GTNHPPBlocks {

    public static BlockGTNHPPCasings CASINGS;

    /** Call from CommonProxy.preInit — registers block before item/machine init. */
    public static void registerBlocks() {
        CASINGS = new BlockGTNHPPCasings();
        GameRegistry.registerBlock(CASINGS, ItemBlockGTNHPPCasings.class, "gtnhpp_casings");
    }

    /**
     * Call from CommonProxy.init — GT5U machine registration MUST happen in the FML init phase, not preInit.
     * IDs 31500–31507 are chosen to avoid conflicts with the merged GT5U MetaTileEntityIDs enum.
     */
    public static void registerMachines() {
        new MTE_HTRF(31500, "gtnhpp.htrf", "High Temperature Reaction Furnace").getStackForm(1);
        new MTE_HPSF(31501, "gtnhpp.hpsf", "High Pressure Sintering Furnace").getStackForm(1);
        new MTE_DAF(31502, "gtnhpp.daf", "Dual Atmosphere Furnace").getStackForm(1);
        new MTE_PCV(31503, "gtnhpp.pcv", "Polycondensation Vessel").getStackForm(1);
        new MTE_PFC(31504, "gtnhpp.pfc", "Precision Film Caster").getStackForm(1);
        new MTE_AAR(31505, "gtnhpp.aar", "Ammonia Atmosphere Reactor").getStackForm(1);
        new MTE_SCD(31506, "gtnhpp.scd", "Supercritical Dryer").getStackForm(1);
        new MTE_CRV(31507, "gtnhpp.crv", "Ceramic Reaction Vessel").getStackForm(1);
        new MTE_SPC(31508, "gtnhpp.spc", "Spectral Photolithography Chamber").getStackForm(1);
        new MTE_CSC(31509, "gtnhpp.csc", "Cryogenic Separation Column").getStackForm(1);
        new MTE_BOF(31510, "gtnhpp.bof", "Basic Oxygen Furnace").getStackForm(1);
        // new MTE_PrimitiveAirIntake(31515, "gtnhpp.primitive_air_intake", "Primitive Air Intake").getStackForm(1);
    }
}
