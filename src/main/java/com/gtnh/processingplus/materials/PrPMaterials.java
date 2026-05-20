package com.gtnh.processingplus.materials;

import static bartworks.util.BWUtil.subscriptNumbers;

import java.util.ArrayList;
import java.util.List;

import bartworks.system.material.Werkstoff;
import gregtech.api.enums.TextureSet;

public class PrPMaterials implements Runnable {

    // =========================
    // ID SPACE (keep stable!)
    // =========================
    private static final int OFFSET = 26_000;
    private static int nextId = OFFSET;

    private static int id() {
        return nextId++;
    }

    // =========================
    // GENERATION PROFILES
    // =========================
    private static Werkstoff.GenerationFeatures polymerFeatures() {
        return new Werkstoff.GenerationFeatures().onlyDust()
            .addCells();
    }

    // Structural polymers — molten + cells + simple metalworking items (plates, rods, etc.)
    private static Werkstoff.GenerationFeatures plasticFeatures() {
        return new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addCells()
            .addSimpleMetalWorkingItems();
    }

    private static Werkstoff.GenerationFeatures ceramicFeatures() {
        return new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addCasings()
            .addSimpleMetalWorkingItems();
    }

    private static Werkstoff.GenerationFeatures fiberFeatures() {
        return new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addSimpleMetalWorkingItems();
    }

    private static Werkstoff.GenerationFeatures fluidFeatures() {
        return new Werkstoff.GenerationFeatures().addCells();
    }

    // Dense sintered ceramics — dust + metal items + simple metalworking (plates, rods, etc.)
    private static Werkstoff.GenerationFeatures metalCeramicFeatures() {
        return new Werkstoff.GenerationFeatures().onlyDust()
            .addMetalItems()
            .addSimpleMetalWorkingItems();
    }

    // Hard ceramics used in gear contexts — dust + gems + simple + crafting metalworking
    private static Werkstoff.GenerationFeatures gearedCeramicFeatures() {
        return new Werkstoff.GenerationFeatures().onlyDust()
            .addGems()
            .addSimpleMetalWorkingItems()
            .addCraftingMetalWorkingItems();
    }

    // =========================
    // POLYMERS & SOLIDS
    // =========================
    public static Werkstoff Nylon66;
    public static Werkstoff Nylon6;
    public static Werkstoff PolylacticAcid;
    public static Werkstoff Kapton;
    public static Werkstoff CarbonFiberComposite;
    public static Werkstoff SilicaAerogel;

    // =========================
    // PAN CHAIN
    // =========================
    public static Werkstoff Polyacrylonitrile;
    public static Werkstoff StabilizedPolyacrylonitrile;
    public static Werkstoff PolyacrylonitrileSolution;
    public static Werkstoff DilutedNMP;
    public static Werkstoff Acrylonitrile;
    public static Werkstoff HydrogenCyanide;
    public static Werkstoff CarbonFiberTow;
    public static Werkstoff GraphitizedCarbonFiber;
    public static Werkstoff Cyclohexanol;
    public static Werkstoff Cyclohexene;
    public static Werkstoff DiphenylEther;
    public static Werkstoff AdipicAcid;

    // =========================
    // SiC / BN / SILICA GEL CHAINS
    // =========================
    public static Werkstoff SinteredSiliconCarbide;
    public static Werkstoff BoronCarbide;
    public static Werkstoff HexagonalBoronNitride;
    public static Werkstoff CrudeSiCPowder;
    public static Werkstoff PurifiedSiCPowder;
    public static Werkstoff DenseSiCCompact;
    public static Werkstoff CrudeHBN;
    public static Werkstoff HBNPowderBlend;
    public static Werkstoff DenseHBNCeramic;
    public static Werkstoff WetSilicaGel;
    public static Werkstoff AgedSilicaGel;
    public static Werkstoff EthanolSaturatedGel;
    public static Werkstoff TEOS;
    public static Werkstoff PAASolution;
    public static Werkstoff BNitrideWaste;

    // =========================
    // NYLON CHAIN — INTERMEDIATES
    // =========================
    public static Werkstoff Adiponitrile;
    public static Werkstoff HMD;

    // =========================
    // PLA CHAIN — INTERMEDIATES
    // =========================
    public static Werkstoff LacticAcid;
    public static Werkstoff Lactide;

    // =========================
    // KAPTON CHAIN — INTERMEDIATES
    // =========================
    public static Werkstoff PMDA;
    public static Werkstoff ODA;
    public static Werkstoff ConcentratedPAA;
    public static Werkstoff PolyamicAcidFilm;

    // =========================
    // NYLON CAPROLACTAM ROUTE
    // =========================
    public static Werkstoff CyclohexanoneOxime;
    public static Werkstoff HydroxylammoniumSulfate;
    public static Werkstoff Caprolactam;

    // =========================
    // KAPTON CHEMICAL IMIDIZATION
    // =========================
    public static Werkstoff Triethylamine;
    public static Werkstoff AceticAnhydride;
    public static Werkstoff Ketene;

    // =========================
    // CARBON FIBER PITCH ROUTE
    // =========================
    public static Werkstoff MesophasePitch;

    // =========================
    // AEROGEL AMBIENT DRYING
    // =========================
    public static Werkstoff Trimethylsilane;
    public static Werkstoff Trimethylchlorosilane;

    // =========================
    // PLA ALTERNATE ROUTES
    // =========================
    public static Werkstoff PropyleneGlycol;

    // =========================
    // PHOTORESIST CHAIN
    // =========================

    // MV
    public static Werkstoff NovolacResin;
    public static Werkstoff TanninSolution;
    public static Werkstoff MVPhotoresistSensitizer;
    public static Werkstoff BasicPhotoresist;

    // HV
    public static Werkstoff HVPhotoresistSensitizer;
    public static Werkstoff AdvancedPhotoresist;

    // EV
    public static Werkstoff Acetoxystyrene;
    public static Werkstoff PHSResin;
    public static Werkstoff SulfurDichloride;
    public static Werkstoff DiphenylsulfoniumSalt;
    public static Werkstoff EVPhotoresist;

    // IV
    public static Werkstoff Furfural;
    public static Werkstoff Dihydropyran;
    public static Werkstoff THPProtectedPHS;
    public static Werkstoff IVPhotoresist;

    // LuV
    public static Werkstoff Trifluoromethane;
    public static Werkstoff TriflicAcid;
    public static Werkstoff MethacrylicAcid;
    public static Werkstoff AmmoniumBisulfate;
    public static Werkstoff AdamantylMethacrylate;
    public static Werkstoff AcetoneAzine;
    public static Werkstoff AIBN;
    public static Werkstoff AlicyclicResin;
    public static Werkstoff TriphenylsulfoniumTriflate;
    public static Werkstoff PropyleneOxide;
    public static Werkstoff PGME;
    public static Werkstoff PGMEA;
    public static Werkstoff LuVPhotoresist;

    // =========================
    // AMORPHOUS METALS (CRV)
    // =========================
    public static Werkstoff AmorphousTritaniumAlloy; // plasma-quenched metallic glass; stickLong = UV motor magnet
    public static Werkstoff AmorphousNaquadria; // plasma-quenched structural Naquadria; plate = mid-UV components

    // =========================
    // COAL FLYASH CHAIN
    // =========================
    public static Werkstoff CoalFlyash;
    public static Werkstoff MetalLeachate;
    public static Werkstoff GalliumHydroxide;
    public static Werkstoff GermaniumHydroxide;
    public static Werkstoff GalliumTrichlorideSolution;
    public static Werkstoff GermaniumTetrachlorideSolution;

    // =========================
    // FREON CHAIN
    // =========================
    public static Werkstoff CarbonTetrachloride;
    public static Werkstoff FreonR12;

    // =========================
    // CSC OUTPUTS
    // =========================
    public static Werkstoff LiquidNitrogen;
    public static Werkstoff LiquidArgon;
    public static Werkstoff LiquidCO2;

    // =========================
    // USES — DERIVED MATERIALS
    // =========================
    public static Werkstoff HBNLubricant; // hBN-suspended lubricant fluid (UHV machine fluid)
    public static Werkstoff PAAAdhesive; // polyamic acid adhesive fluid (precision bonding)
    public static Werkstoff LoadedAerogelCatalystSupport; // PGM-impregnated aerogel catalyst (CRC consumable)
    public static Werkstoff AerogelInsulationPanel; // aerogel + CF composite thermal panel

    public static List<Werkstoff> ALL = new ArrayList<>();

    // =========================
    // INIT
    // =========================
    @Override
    public void run() {

        // -------------------------
        // POLYMERS
        // -------------------------
        Nylon66 = register(
            new Werkstoff(
                rgb(245, 234, 208),
                "Nylon-6,6",
                subscriptNumbers("C12H22N2O2"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                plasticFeatures(),
                id(),
                TextureSet.SET_DULL));

        Nylon6 = register(
            new Werkstoff(
                rgb(240, 240, 235),
                "Nylon-6",
                subscriptNumbers("(C6H11NO)n"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                plasticFeatures(),
                id(),
                TextureSet.SET_DULL));

        PolylacticAcid = register(
            new Werkstoff(
                rgb(245, 245, 240),
                "Polylactic Acid",
                subscriptNumbers("C3H4O2"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                plasticFeatures(),
                id(),
                TextureSet.SET_DULL));

        Kapton = register(
            new Werkstoff(
                rgb(204, 136, 0),
                "Kapton",
                subscriptNumbers("C22H10N2O5"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                ceramicFeatures(),
                id(),
                TextureSet.SET_DULL));

        CarbonFiberComposite = register(
            new Werkstoff(
                rgb(26, 26, 46),
                "Carbon Fiber Composite",
                subscriptNumbers("(C)n"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fiberFeatures(),
                id(),
                TextureSet.SET_DULL));

        SilicaAerogel = register(
            new Werkstoff(
                rgb(208, 232, 255),
                "Silica Aerogel",
                subscriptNumbers("SiO2"),
                new Werkstoff.Stats(),
                Werkstoff.Types.MATERIAL,
                ceramicFeatures(),
                id(),
                TextureSet.SET_DULL));

        // -------------------------
        // PAN CHAIN — ORGANICS
        // -------------------------
        Polyacrylonitrile = register(
            new Werkstoff(
                rgb(238, 238, 238),
                "Polyacrylonitrile",
                subscriptNumbers("(C3H3N)n"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        StabilizedPolyacrylonitrile = register(
            new Werkstoff(
                rgb(34, 34, 34),
                "Stabilized Polyacrylonitrile",
                subscriptNumbers("(C3H3N)nO"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        PolyacrylonitrileSolution = register(
            new Werkstoff(
                rgb(229, 193, 0),
                "Polyacrylonitrile Solution",
                subscriptNumbers("PAN/NMP"),
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        DilutedNMP = register(
            new Werkstoff(
                rgb(136, 170, 187),
                "Diluted NMP",
                subscriptNumbers("C5H9NO·H2O"),
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        HydrogenCyanide = register(
            new Werkstoff(
                rgb(176, 224, 230),
                "Hydrogen Cyanide",
                subscriptNumbers("HCN"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        Acrylonitrile = register(
            new Werkstoff(
                rgb(176, 176, 176),
                "Acrylonitrile",
                subscriptNumbers("C3H3N"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        Cyclohexanol = register(
            new Werkstoff(
                rgb(239, 239, 239),
                "Cyclohexanol",
                subscriptNumbers("C6H12O"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        Cyclohexene = register(
            new Werkstoff(
                rgb(239, 239, 239),
                "Cyclohexene",
                subscriptNumbers("C6H10"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        DiphenylEther = register(
            new Werkstoff(
                rgb(230, 230, 215),
                "Diphenyl Ether",
                subscriptNumbers("C12H10O"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        AdipicAcid = register(
            new Werkstoff(
                rgb(255, 214, 165),
                "Adipic Acid",
                subscriptNumbers("C6H10O4"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        CarbonFiberTow = register(
            new Werkstoff(
                rgb(26, 26, 26),
                "Carbon Fiber Tow",
                subscriptNumbers("(C)n"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fiberFeatures(),
                id(),
                TextureSet.SET_DULL));

        GraphitizedCarbonFiber = register(
            new Werkstoff(
                rgb(10, 10, 10),
                "Graphitized Carbon Fiber",
                subscriptNumbers("(C)n*"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fiberFeatures(),
                id(),
                TextureSet.SET_DULL));

        // -------------------------
        // FLUIDS — SILICA / POLYIMIDE
        // -------------------------
        TEOS = register(
            new Werkstoff(
                rgb(232, 244, 255),
                "Tetraethyl Orthosilicate",
                subscriptNumbers("Si(OC2H5)4"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        PAASolution = register(
            new Werkstoff(
                rgb(255, 224, 130),
                "Polyamic Acid Solution",
                subscriptNumbers("C22H14N2O7"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        // -------------------------
        // SiC CHAIN — CERAMICS
        // -------------------------
        CrudeSiCPowder = register(
            new Werkstoff(
                rgb(74, 74, 74),
                "Crude SiC Powder",
                subscriptNumbers("SiC+"),
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                ceramicFeatures(),
                id(),
                TextureSet.SET_DULL));

        PurifiedSiCPowder = register(
            new Werkstoff(
                rgb(47, 47, 47),
                "Purified SiC Powder",
                subscriptNumbers("SiC"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                ceramicFeatures(),
                id(),
                TextureSet.SET_DULL));

        DenseSiCCompact = register(
            new Werkstoff(
                rgb(31, 31, 31),
                "Dense SiC Compact",
                subscriptNumbers("SiC"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                metalCeramicFeatures(),
                id(),
                TextureSet.SET_METALLIC));

        SinteredSiliconCarbide = register(
            new Werkstoff(
                rgb(61, 61, 64),
                "Sintered Silicon Carbide",
                subscriptNumbers("SiC"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                ceramicFeatures(),
                id(),
                TextureSet.SET_DULL));

        // -------------------------
        // hBN CHAIN — CERAMICS
        // -------------------------
        BoronCarbide = register(
            new Werkstoff(
                rgb(43, 43, 43),
                "Boron Carbide",
                subscriptNumbers("B4C"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                gearedCeramicFeatures(),
                id(),
                TextureSet.SET_DULL));

        CrudeHBN = register(
            new Werkstoff(
                rgb(239, 239, 239),
                "Crude hBN",
                subscriptNumbers("BN*"),
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                ceramicFeatures(),
                id(),
                TextureSet.SET_DULL));

        BNitrideWaste = register(
            new Werkstoff(
                rgb(239, 239, 239),
                "Boron Nitride Waste",
                "??BN??",
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                ceramicFeatures(),
                id(),
                TextureSet.SET_DULL));

        HBNPowderBlend = register(
            new Werkstoff(
                rgb(218, 218, 218),
                "hBN Powder Blend",
                subscriptNumbers("BN+Y2"),
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                ceramicFeatures(),
                id(),
                TextureSet.SET_DULL));

        DenseHBNCeramic = register(
            new Werkstoff(
                rgb(245, 245, 245),
                "Dense hBN Ceramic",
                subscriptNumbers("BN"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                metalCeramicFeatures(),
                id(),
                TextureSet.SET_DULL));

        HexagonalBoronNitride = register(
            new Werkstoff(
                rgb(240, 240, 236),
                "Hexagonal Boron Nitride",
                subscriptNumbers("BN"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                ceramicFeatures(),
                id(),
                TextureSet.SET_DULL));

        // -------------------------
        // SILICA GEL CHAIN
        // -------------------------
        WetSilicaGel = register(
            new Werkstoff(
                rgb(191, 230, 255),
                "Wet Silica Gel",
                subscriptNumbers("SiO2·nH2O"),
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                ceramicFeatures(),
                id(),
                TextureSet.SET_DULL));

        AgedSilicaGel = register(
            new Werkstoff(
                rgb(158, 210, 242),
                "Aged Silica Gel",
                subscriptNumbers("SiO2"),
                new Werkstoff.Stats(),
                Werkstoff.Types.MATERIAL,
                ceramicFeatures(),
                id(),
                TextureSet.SET_DULL));

        EthanolSaturatedGel = register(
            new Werkstoff(
                rgb(255, 224, 178),
                "Ethanol Saturated Gel",
                subscriptNumbers("SiO2/C2H5OH"),
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                ceramicFeatures(),
                id(),
                TextureSet.SET_DULL));

        // -------------------------
        // NYLON CHAIN INTERMEDIATES
        // -------------------------
        Adiponitrile = register(
            new Werkstoff(
                rgb(208, 255, 208),
                "Adiponitrile",
                subscriptNumbers("C6H8N2"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        HMD = register(
            new Werkstoff(
                rgb(240, 240, 255),
                "Hexamethylenediamine",
                subscriptNumbers("C6H16N2"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        // -------------------------
        // PLA CHAIN INTERMEDIATES
        // -------------------------
        LacticAcid = register(
            new Werkstoff(
                rgb(255, 224, 224),
                "Lactic Acid",
                subscriptNumbers("C3H6O3"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        Lactide = register(
            new Werkstoff(
                rgb(255, 255, 255),
                "Lactide",
                subscriptNumbers("C6H8O4"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        // -------------------------
        // KAPTON CHAIN INTERMEDIATES
        // -------------------------
        PMDA = register(
            new Werkstoff(
                rgb(255, 251, 230),
                "PMDA",
                subscriptNumbers("C10H2O6"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        ODA = register(
            new Werkstoff(
                rgb(255, 228, 196),
                "ODA",
                subscriptNumbers("C12H12N2O"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        ConcentratedPAA = register(
            new Werkstoff(
                rgb(255, 224, 130),
                "Concentrated Polyamic Acid",
                subscriptNumbers("C22H14N2O7"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        PolyamicAcidFilm = register(
            new Werkstoff(
                rgb(255, 215, 0),
                "Polyamic Acid Film",
                subscriptNumbers("C22H14N2O7"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        // -------------------------
        // NYLON CAPROLACTAM ROUTE
        // -------------------------
        CyclohexanoneOxime = register(
            new Werkstoff(
                rgb(240, 245, 230),
                "Cyclohexanone Oxime",
                subscriptNumbers("C6H11NO"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        HydroxylammoniumSulfate = register(
            new Werkstoff(
                rgb(245, 245, 245),
                "Hydroxylammonium Sulfate",
                subscriptNumbers("(NH3OH)2SO4"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        Caprolactam = register(
            new Werkstoff(
                rgb(240, 240, 250),
                "Caprolactam",
                subscriptNumbers("C6H11NO"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        // -------------------------
        // KAPTON CHEMICAL IMIDIZATION
        // -------------------------
        Triethylamine = register(
            new Werkstoff(
                rgb(200, 215, 240),
                "Triethylamine",
                subscriptNumbers("N(C2H5)3"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        AceticAnhydride = register(
            new Werkstoff(
                rgb(245, 245, 240),
                "Acetic Anhydride",
                subscriptNumbers("C4H6O3"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        Ketene = register(
            new Werkstoff(
                rgb(250, 255, 245),
                "Ketene",
                subscriptNumbers("C2H2O"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        // -------------------------
        // CARBON FIBER PITCH ROUTE
        // -------------------------
        MesophasePitch = register(
            new Werkstoff(
                rgb(20, 15, 10),
                "Mesophase Pitch",
                subscriptNumbers("(C)n"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        // -------------------------
        // AEROGEL AMBIENT DRYING
        // -------------------------
        Trimethylsilane = register(
            new Werkstoff(
                rgb(210, 230, 210),
                "Trimethylsilane",
                subscriptNumbers("(CH3)3SiH"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        Trimethylchlorosilane = register(
            new Werkstoff(
                rgb(205, 225, 205),
                "Trimethylchlorosilane",
                subscriptNumbers("(CH3)3SiCl"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        // -------------------------
        // PLA ALTERNATE ROUTES
        // -------------------------
        PropyleneGlycol = register(
            new Werkstoff(
                rgb(220, 245, 220),
                "Propylene Glycol",
                subscriptNumbers("C3H8O2"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        // -------------------------
        // PHOTORESIST CHAIN — MV
        // -------------------------
        NovolacResin = register(
            new Werkstoff(
                rgb(160, 110, 40),
                "Novolac Resin",
                subscriptNumbers("(C7H6O)n"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        TanninSolution = register(
            new Werkstoff(
                rgb(130, 90, 45),
                "Tannin Solution",
                "polyphenol(aq)",
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        MVPhotoresistSensitizer = register(
            new Werkstoff(
                rgb(200, 170, 50),
                "MV Photoresist Sensitizer",
                subscriptNumbers("C6H4O2/tannin"),
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        BasicPhotoresist = register(
            new Werkstoff(
                rgb(160, 80, 40),
                "Basic Photoresist",
                "novolac/sensitizer",
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        // -------------------------
        // PHOTORESIST CHAIN — HV
        // -------------------------
        HVPhotoresistSensitizer = register(
            new Werkstoff(
                rgb(220, 140, 30),
                "HV Photoresist Sensitizer",
                subscriptNumbers("C10H7O·DNQ"),
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        AdvancedPhotoresist = register(
            new Werkstoff(
                rgb(180, 60, 20),
                "Advanced Photoresist",
                "novolac/DNQ",
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        // -------------------------
        // PHOTORESIST CHAIN — EV
        // -------------------------
        Acetoxystyrene = register(
            new Werkstoff(
                rgb(230, 220, 170),
                "Acetoxystyrene",
                subscriptNumbers("C10H10O2"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        PHSResin = register(
            new Werkstoff(
                rgb(210, 190, 150),
                "PHS Resin",
                "poly(p-hydroxystyrene)",
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        SulfurDichloride = register(
            new Werkstoff(
                rgb(200, 170, 50),
                "Sulfur Dichloride",
                subscriptNumbers("SCl2"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        DiphenylsulfoniumSalt = register(
            new Werkstoff(
                rgb(240, 240, 235),
                "Diphenylsulfonium Salt",
                subscriptNumbers("(C6H5)2S·Cl"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        EVPhotoresist = register(
            new Werkstoff(
                rgb(190, 40, 30),
                "EV Photoresist",
                "PHS/PAG",
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        // -------------------------
        // PHOTORESIST CHAIN — IV
        // -------------------------
        Furfural = register(
            new Werkstoff(
                rgb(180, 130, 30),
                "Furfural",
                subscriptNumbers("C5H4O2"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        Dihydropyran = register(
            new Werkstoff(
                rgb(230, 230, 245),
                "Dihydropyran",
                subscriptNumbers("C5H8O"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        THPProtectedPHS = register(
            new Werkstoff(
                rgb(200, 200, 210),
                "THP-Protected PHS",
                "PHS·THP",
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        IVPhotoresist = register(
            new Werkstoff(
                rgb(150, 30, 20),
                "IV Photoresist",
                "PHS-THP/PAG",
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        // -------------------------
        // PHOTORESIST CHAIN — LuV
        // -------------------------
        Trifluoromethane = register(
            new Werkstoff(
                rgb(210, 230, 245),
                "Trifluoromethane",
                subscriptNumbers("CHF3"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        TriflicAcid = register(
            new Werkstoff(
                rgb(240, 245, 250),
                "Triflic Acid",
                subscriptNumbers("CF3SO3H"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        MethacrylicAcid = register(
            new Werkstoff(
                rgb(230, 230, 230),
                "Methacrylic Acid",
                subscriptNumbers("C4H6O2"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        AmmoniumBisulfate = register(
            new Werkstoff(
                rgb(245, 245, 245),
                "Ammonium Bisulfate",
                subscriptNumbers("(NH4)HSO4"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        AdamantylMethacrylate = register(
            new Werkstoff(
                rgb(240, 240, 245),
                "Adamantyl Methacrylate",
                subscriptNumbers("Ad·C4H5O2"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        AcetoneAzine = register(
            new Werkstoff(
                rgb(220, 210, 50),
                "Acetone Azine",
                subscriptNumbers("C6H12N2"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        AIBN = register(
            new Werkstoff(
                rgb(245, 245, 245),
                "AIBN",
                subscriptNumbers("C8H12N4"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        AlicyclicResin = register(
            new Werkstoff(
                rgb(240, 235, 210),
                "Alicyclic Resin",
                "poly(AdMA·MAA)",
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        TriphenylsulfoniumTriflate = register(
            new Werkstoff(
                rgb(240, 240, 240),
                "Triphenylsulfonium Triflate",
                subscriptNumbers("(C6H5)3S·OTf"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        PropyleneOxide = register(
            new Werkstoff(
                rgb(230, 235, 240),
                "Propylene Oxide",
                subscriptNumbers("C3H6O"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        PGME = register(
            new Werkstoff(
                rgb(235, 240, 235),
                "PGME",
                subscriptNumbers("C4H10O2"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        PGMEA = register(
            new Werkstoff(
                rgb(235, 240, 230),
                "PGMEA",
                subscriptNumbers("C6H12O3"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        LuVPhotoresist = register(
            new Werkstoff(
                rgb(120, 20, 80),
                "LuV Photoresist",
                "alicyclic/PAG/PGMEA",
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        // -------------------------
        // AMORPHOUS METALS (CRV)
        // -------------------------
        AmorphousTritaniumAlloy = register(
            new Werkstoff(
                rgb(80, 92, 120),
                "Amorphous Tritanium Alloy",
                "Ti-Am*",
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                metalCeramicFeatures(),
                id(),
                TextureSet.SET_METALLIC));

        AmorphousNaquadria = register(
            new Werkstoff(
                rgb(55, 200, 55),
                "Amorphous Naquadria",
                "Nq**",
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                metalCeramicFeatures(),
                id(),
                TextureSet.SET_METALLIC));

        // -------------------------
        // COAL FLYASH CHAIN
        // -------------------------
        CoalFlyash = register(
            new Werkstoff(
                rgb(58, 55, 50),
                "Coal Flyash",
                subscriptNumbers("SiO2/Al2O3/Fe2O3"),
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                new Werkstoff.GenerationFeatures().onlyDust(),
                id(),
                TextureSet.SET_DULL));

        MetalLeachate = register(
            new Werkstoff(
                rgb(180, 200, 130),
                "Metal Leachate",
                "Ga/Ge/H2SO4(aq)",
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        GalliumHydroxide = register(
            new Werkstoff(
                rgb(230, 240, 245),
                "Gallium Hydroxide",
                subscriptNumbers("Ga(OH)3"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                new Werkstoff.GenerationFeatures().onlyDust(),
                id(),
                TextureSet.SET_DULL));

        GermaniumHydroxide = register(
            new Werkstoff(
                rgb(235, 240, 235),
                "Germanium Hydroxide",
                subscriptNumbers("Ge(OH)4"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                new Werkstoff.GenerationFeatures().onlyDust(),
                id(),
                TextureSet.SET_DULL));

        GalliumTrichlorideSolution = register(
            new Werkstoff(
                rgb(210, 230, 255),
                "Gallium Trichloride Solution",
                subscriptNumbers("GaCl3(aq)"),
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        GermaniumTetrachlorideSolution = register(
            new Werkstoff(
                rgb(215, 230, 215),
                "Germanium Tetrachloride",
                subscriptNumbers("GeCl4"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        // -------------------------
        // FREON CHAIN
        // -------------------------
        CarbonTetrachloride = register(
            new Werkstoff(
                rgb(195, 220, 235),
                "Carbon Tetrachloride",
                subscriptNumbers("CCl4"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        FreonR12 = register(
            new Werkstoff(
                rgb(200, 240, 255),
                "Freon R-12",
                subscriptNumbers("CF2Cl2"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        // -------------------------
        // CSC OUTPUTS
        // -------------------------
        LiquidNitrogen = register(
            new Werkstoff(
                rgb(190, 215, 240),
                "Liquid Nitrogen",
                subscriptNumbers("N2(l)"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        LiquidArgon = register(
            new Werkstoff(
                rgb(200, 210, 230),
                "Liquid Argon",
                "Ar(l)",
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        LiquidCO2 = register(
            new Werkstoff(
                rgb(210, 230, 215),
                "Liquid Carbon Dioxide",
                subscriptNumbers("CO2(l)"),
                new Werkstoff.Stats(),
                Werkstoff.Types.COMPOUND,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        // -------------------------
        // USES — DERIVED MATERIALS
        // -------------------------
        HBNLubricant = register(
            new Werkstoff(
                rgb(230, 240, 255),
                "hBN Lubricant",
                subscriptNumbers("BN/oil"),
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        PAAAdhesive = register(
            new Werkstoff(
                rgb(255, 210, 80),
                "PAA Adhesive",
                subscriptNumbers("C22H14N2O7/NMP"),
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                fluidFeatures(),
                id(),
                TextureSet.SET_FLUID));

        LoadedAerogelCatalystSupport = register(
            new Werkstoff(
                rgb(180, 210, 240),
                "Loaded Aerogel Catalyst Support",
                subscriptNumbers("SiO2/PGM"),
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                polymerFeatures(),
                id(),
                TextureSet.SET_DULL));

        AerogelInsulationPanel = register(
            new Werkstoff(
                rgb(195, 225, 248),
                "Aerogel Insulation Panel",
                subscriptNumbers("SiO2/(C)n"),
                new Werkstoff.Stats(),
                Werkstoff.Types.MIXTURE,
                ceramicFeatures(),
                id(),
                TextureSet.SET_DULL));
    }

    // =========================
    // HELPERS
    // =========================
    private static Werkstoff register(Werkstoff w) {
        ALL.add(w);
        return w;
    }

    private static short[] rgb(int r, int g, int b) {
        return new short[] { (short) r, (short) g, (short) b };
    }
}
