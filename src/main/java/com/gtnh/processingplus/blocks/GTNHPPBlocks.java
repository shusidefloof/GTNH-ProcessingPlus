package com.gtnh.processingplus.blocks;

import com.gtnh.processingplus.machines.MTE_AAR;
import com.gtnh.processingplus.machines.MTE_BOF;
import com.gtnh.processingplus.machines.MTE_CRV;
import com.gtnh.processingplus.machines.MTE_CSC;
import com.gtnh.processingplus.machines.MTE_HPSF;
import com.gtnh.processingplus.machines.MTE_HTRF;
import com.gtnh.processingplus.machines.MTE_SPC;
import com.gtnh.processingplus.machines.MTE_SPCBioModule;
import com.gtnh.processingplus.machines.MTE_SPCCryoModule;
import com.gtnh.processingplus.machines.MTE_SPCQuantumModule;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.materials.WerkstoffCableLoader;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;

public class GTNHPPBlocks {

    public static BlockGTNHPPCasings CASINGS;

    /** SPC controller instance — kept so its craft recipe can reference the controller stack. */
    public static MTE_SPC SPC;

    /** HPSF controller instance — kept so its craft recipe can reference the controller stack. */
    public static MTE_HPSF HPSF;

    /** HTRF controller instance — kept so its craft recipe can reference the controller stack. */
    public static MTE_HTRF HTRF;

    /** CRV controller instance — kept so its assembly-line recipe can reference the controller stack. */
    public static MTE_CRV CRV;
    private static final int OFFSET = 31_517;
    private static int nextId = OFFSET;


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
        CRV = new MTE_CRV(31507, "gtnhpp.crv", "Ceramic Reaction Vessel");
        CRV.getStackForm(1);
        SPC = new MTE_SPC(31508, "gtnhpp.spc", "Spectral Photolithography Chamber");
        SPC.getStackForm(1);
        new MTE_CSC(31509, "gtnhpp.csc", "Cryogenic Separation Column").getStackForm(1);
        new MTE_BOF(31510, "gtnhpp.bof", "Basic Oxygen Furnace").getStackForm(1);
        HPSF = new MTE_HPSF(31511, "gtnhpp.hpsf", "High Pressure Sintering Furnace");
        HPSF.getStackForm(1);
        new MTE_SPCBioModule(31512, "gtnhpp.spc_bio_module", "Bio-Lithography Module").getStackForm(1);
        new MTE_SPCCryoModule(31513, "gtnhpp.spc_cryo_module", "Cryo-Stabilization Module").getStackForm(1);
        new MTE_SPCQuantumModule(31514, "gtnhpp.spc_quantum_module", "Quantum Alignment Module").getStackForm(1);
        HTRF = new MTE_HTRF(31515, "gtnhpp.htrf", "High Temperature Reaction Furnace");
        HTRF.getStackForm(1);
        // new MTE_PrimitiveAirIntake(31516, "gtnhpp.primitive_air_intake", "Primitive Air Intake").getStackForm(1);

        // Unobtanium superconductor — lossless placeable wires + cables (ZPM); recipes auto-generated.
        // Uses MTE IDs 31517-31528 (6 wires + 6 cables).
        // Unobtanium: superconductor wires + cables insulated with Polyphenylene Sulfide (no rubber recipe).
        WerkstoffCableLoader.register(
            PrPMaterials.Unobtanium,
            id(),
            0L,
            0L,
            4L,
            TierEU.ZPM,
            TierEU.RECIPE_LuV,
            200,
            true);
        // RHEA: cables insulated with Polyphenylene Sulfide foil, like Tungsten.
        WerkstoffCableLoader.register(
            PrPMaterials.RefractoryHighEntropyAlloy,
            id(),
            8,
            4,
            6,
            TierEU.ZPM,
            TierEU.RECIPE_LV,
            5 * 20,
            true);
    }
    public static int id() {
        return nextId += 12;}
}
