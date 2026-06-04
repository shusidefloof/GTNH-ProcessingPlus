package com.gtnh.processingplus.blocks;

import com.gtnh.processingplus.machines.MTE_AAR;
import com.gtnh.processingplus.machines.MTE_BOF;
import com.gtnh.processingplus.machines.MTE_CRV;
import com.gtnh.processingplus.machines.MTE_CSC;
import com.gtnh.processingplus.machines.MTE_HPSF;
import com.gtnh.processingplus.machines.MTE_SPC;
import com.gtnh.processingplus.machines.MTE_SPCBioModule;
import com.gtnh.processingplus.machines.MTE_SPCCryoModule;
import com.gtnh.processingplus.machines.MTE_SPCQuantumModule;

import cpw.mods.fml.common.registry.GameRegistry;

public class GTNHPPBlocks {

    public static BlockGTNHPPCasings CASINGS;

    /** SPC controller instance — kept so its craft recipe can reference the controller stack. */
    public static MTE_SPC SPC;

    /** HPSF controller instance — kept so its craft recipe can reference the controller stack. */
    public static MTE_HPSF HPSF;

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
        new MTE_AAR(31505, "gtnhpp.aar", "Ammonia Atmosphere Reactor").getStackForm(1);
        new MTE_CRV(31507, "gtnhpp.crv", "Ceramic Reaction Vessel").getStackForm(1);
        SPC = new MTE_SPC(31508, "gtnhpp.spc", "Spectral Photolithography Chamber");
        SPC.getStackForm(1);
        new MTE_CSC(31509, "gtnhpp.csc", "Cryogenic Separation Column").getStackForm(1);
        new MTE_BOF(31510, "gtnhpp.bof", "Basic Oxygen Furnace").getStackForm(1);
        HPSF = new MTE_HPSF(31511, "gtnhpp.hpsf", "High Pressure Sintering Furnace");
        HPSF.getStackForm(1);
        new MTE_SPCBioModule(31512, "gtnhpp.spc_bio_module", "Bio-Lithography Module").getStackForm(1);
        new MTE_SPCCryoModule(31513, "gtnhpp.spc_cryo_module", "Cryo-Stabilization Module").getStackForm(1);
        new MTE_SPCQuantumModule(31514, "gtnhpp.spc_quantum_module", "Quantum Alignment Module").getStackForm(1);
        // new MTE_PrimitiveAirIntake(31516, "gtnhpp.primitive_air_intake", "Primitive Air Intake").getStackForm(1);
    }
}
