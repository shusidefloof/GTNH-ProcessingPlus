package com.gtnh.processingplus.blocks;

import com.gtnh.processingplus.machines.MTE_AAR;
import com.gtnh.processingplus.machines.MTE_BOF;
import com.gtnh.processingplus.machines.MTE_CAC;
import com.gtnh.processingplus.machines.MTE_CIDC;
import com.gtnh.processingplus.machines.MTE_CRV;
import com.gtnh.processingplus.machines.MTE_CSC;
import com.gtnh.processingplus.machines.MTE_HPR;
import com.gtnh.processingplus.machines.MTE_HPSF;
import com.gtnh.processingplus.machines.MTE_HTRF;
import com.gtnh.processingplus.machines.MTE_SPU;
import com.gtnh.processingplus.machines.MTE_PCV;
import com.gtnh.processingplus.machines.MTE_RTG;
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

    /** PCV controller instance — kept so its assembler controller recipe can reference the controller stack. */
    public static MTE_PCV PCV;

    /** CAC controller instance — kept so its assembly-line recipe can reference the controller stack. */
    public static MTE_CAC CAC;

    /** RTG controller instance — kept so its assembly-line recipe can reference the controller stack. */
    public static MTE_RTG RTG;

    /** CIDC controller instance — kept so its assembly-line recipe can reference the controller stack. */
    public static MTE_CIDC CIDC;

    /** HPR controller instance — kept so its assembly-line recipe can reference the controller stack. */
    public static MTE_HPR HPR;

    /** SPU controller instance — kept so its assembly-line recipe can reference the controller stack. */
    public static MTE_SPU SPU;

    // Reserved MTE IDs for the four not-yet-built multiblocks. The design doc's values
    // (CIDC 31511, HPR 31512, SPU 31513, CRC 31514) are ALREADY taken by HPSF and the three SPC
    // modules — use these instead. They sit in the unused 31500-31504 gap, below every live machine
    // and clear of the cable loader (id() starts at 31529). Do NOT use 31511-31514.
    public static final int CIDC_ID = 31500;
    public static final int HPR_ID = 31501;
    public static final int SPU_ID = 31502;
    public static final int CRC_ID = 31503;

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
        PCV = new MTE_PCV(31506, "gtnhpp.pcv", "Polycondensation Vessel");
        PCV.getStackForm(1);
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
        CAC = new MTE_CAC(31516, "gtnhpp.cac", "Cryogenic Annealing Cryostat");
        CAC.getStackForm(1);
        RTG = new MTE_RTG(31504, "gtnhpp.rtg", "Radioisotope Thermoelectric Generator");
        RTG.getStackForm(1);
        CIDC = new MTE_CIDC(CIDC_ID, "gtnhpp.cidc", "Controlled Isotopic Doping Chamber");
        CIDC.getStackForm(1);
        HPR = new MTE_HPR(HPR_ID, "gtnhpp.hpr", "Hybrid Phase Reactor");
        HPR.getStackForm(1);
        SPU = new MTE_SPU(SPU_ID, "gtnhpp.spu", "Subatomic Patterning Unit");
        SPU.getStackForm(1);

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
