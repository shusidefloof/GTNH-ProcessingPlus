package com.gtnh.processingplus.materials;

import bartworks.system.material.Werkstoff;
import gregtech.api.enums.TextureSet;

import java.util.ArrayList;
import java.util.List;

import static bartworks.util.BWUtil.subscriptNumbers;

public class PrPMaterials implements Runnable {

    // =========================
    // ID SPACE (keep stable!)
    // =========================
    private static final int OFFSET = 26_000;
    private static int nextId = OFFSET;

    private static int id() { return nextId++; }

    // =========================
    // GENERATION PROFILES
    // =========================
    private static Werkstoff.GenerationFeatures polymerFeatures() {
        return new Werkstoff.GenerationFeatures().onlyDust().addCells();
    }

    // Structural polymers — dust + cells + simple metalworking items (plates, rods, etc.)
    private static Werkstoff.GenerationFeatures plasticFeatures() {
        return new Werkstoff.GenerationFeatures().onlyDust().addCells().addSimpleMetalWorkingItems();
    }

    private static Werkstoff.GenerationFeatures ceramicFeatures() {
        return new Werkstoff.GenerationFeatures().onlyDust().addMolten().addCasings().addSimpleMetalWorkingItems();
    }

    private static Werkstoff.GenerationFeatures fiberFeatures() {
        return new Werkstoff.GenerationFeatures().onlyDust().addMolten().addSimpleMetalWorkingItems();
    }

    private static Werkstoff.GenerationFeatures fluidFeatures() {
        return new Werkstoff.GenerationFeatures().addCells();
    }

    // Dense sintered ceramics — dust + metal items + simple metalworking (plates, rods, etc.)
    private static Werkstoff.GenerationFeatures metalCeramicFeatures() {
        return new Werkstoff.GenerationFeatures().onlyDust().addMetalItems().addSimpleMetalWorkingItems();
    }

    // Hard ceramics used in gear contexts — dust + gems + simple + crafting metalworking
    private static Werkstoff.GenerationFeatures gearedCeramicFeatures() {
        return new Werkstoff.GenerationFeatures().onlyDust().addGems().addSimpleMetalWorkingItems().addCraftingMetalWorkingItems();
    }

    // =========================
    // POLYMERS & SOLIDS
    // =========================
    public static Werkstoff Nylon66;
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
    public static Werkstoff BoricAcid;
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
    // USES — DERIVED MATERIALS
    // =========================
    public static Werkstoff HBNLubricant;        // hBN-suspended lubricant fluid (UHV machine fluid)
    public static Werkstoff PAAAdhesive;          // polyamic acid adhesive fluid (precision bonding)
    public static Werkstoff LoadedAerogelCatalystSupport; // PGM-impregnated aerogel catalyst (CRC consumable)
    public static Werkstoff AerogelInsulationPanel;       // aerogel + CF composite thermal panel

    public static List<Werkstoff> ALL = new ArrayList<>();

    // =========================
    // INIT
    // =========================
    @Override
    public void run() {

        // -------------------------
        // POLYMERS
        // -------------------------
        Nylon66 = register(new Werkstoff(
            rgb(245, 234, 208), "Nylon-6,6", subscriptNumbers("C12H22N2O2"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, plasticFeatures(), id(), TextureSet.SET_DULL
        ));

        PolylacticAcid = register(new Werkstoff(
            rgb(245, 245, 240), "Polylactic Acid", subscriptNumbers("C3H4O2"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, plasticFeatures(), id(), TextureSet.SET_DULL
        ));

        Kapton = register(new Werkstoff(
            rgb(204, 136, 0), "Kapton", subscriptNumbers("C22H10N2O5"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, ceramicFeatures(), id(), TextureSet.SET_DULL
        ));

        CarbonFiberComposite = register(new Werkstoff(
            rgb(26, 26, 46), "Carbon Fiber Composite", subscriptNumbers("(C)n"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, fiberFeatures(), id(), TextureSet.SET_DULL
        ));

        SilicaAerogel = register(new Werkstoff(
            rgb(208, 232, 255), "Silica Aerogel", subscriptNumbers("SiO2"),
            new Werkstoff.Stats(), Werkstoff.Types.MATERIAL, ceramicFeatures(), id(), TextureSet.SET_DULL
        ));

        // -------------------------
        // PAN CHAIN — ORGANICS
        // -------------------------
        Polyacrylonitrile = register(new Werkstoff(
            rgb(238, 238, 238), "Polyacrylonitrile", subscriptNumbers("(C3H3N)n"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, polymerFeatures(), id(), TextureSet.SET_DULL
        ));

        StabilizedPolyacrylonitrile = register(new Werkstoff(
            rgb(34, 34, 34), "Stabilized Polyacrylonitrile", subscriptNumbers("(C3H3N)nO"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, polymerFeatures(), id(), TextureSet.SET_DULL
        ));

        PolyacrylonitrileSolution = register(new Werkstoff(
            rgb(229, 193, 0), "Polyacrylonitrile Solution", subscriptNumbers("PAN/NMP"),
            new Werkstoff.Stats(), Werkstoff.Types.MIXTURE, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        DilutedNMP = register(new Werkstoff(
            rgb(136, 170, 187), "Diluted NMP", subscriptNumbers("C5H9NO·H2O"),
            new Werkstoff.Stats(), Werkstoff.Types.MIXTURE, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        HydrogenCyanide = register(new Werkstoff(
            rgb(176, 224, 230), "Hydrogen Cyanide", subscriptNumbers("HCN"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        Acrylonitrile = register(new Werkstoff(
            rgb(176, 176, 176), "Acrylonitrile", subscriptNumbers("C3H3N"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, polymerFeatures(), id(), TextureSet.SET_DULL
        ));

        Cyclohexanol = register(new Werkstoff(
            rgb(239, 239, 239), "Cyclohexanol", subscriptNumbers("C6H12O"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        Cyclohexene = register(new Werkstoff(
            rgb(239, 239, 239), "Cyclohexene", subscriptNumbers("C6H10"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        DiphenylEther = register(new Werkstoff(
            rgb(230, 230, 215), "Diphenyl Ether", subscriptNumbers("C12H10O"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        AdipicAcid = register(new Werkstoff(
            rgb(255, 214, 165), "Adipic Acid", subscriptNumbers("C6H10O4"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, polymerFeatures(), id(), TextureSet.SET_DULL
        ));

        CarbonFiberTow = register(new Werkstoff(
            rgb(26, 26, 26), "Carbon Fiber Tow", subscriptNumbers("(C)n"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, fiberFeatures(), id(), TextureSet.SET_DULL
        ));

        // -------------------------
        // FLUIDS — SILICA / POLYIMIDE
        // -------------------------
        TEOS = register(new Werkstoff(
            rgb(232, 244, 255), "Tetraethyl Orthosilicate", subscriptNumbers("Si(OC2H5)4"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        PAASolution = register(new Werkstoff(
            rgb(255, 224, 130), "Polyamic Acid Solution", subscriptNumbers("C22H14N2O7"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));


        // -------------------------
        // SiC CHAIN — CERAMICS
        // -------------------------
        CrudeSiCPowder = register(new Werkstoff(
            rgb(74, 74, 74), "Crude SiC Powder", subscriptNumbers("SiC+"),
            new Werkstoff.Stats(), Werkstoff.Types.MIXTURE, ceramicFeatures(), id(), TextureSet.SET_DULL
        ));

        PurifiedSiCPowder = register(new Werkstoff(
            rgb(47, 47, 47), "Purified SiC Powder", subscriptNumbers("SiC"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, ceramicFeatures(), id(), TextureSet.SET_DULL
        ));

        DenseSiCCompact = register(new Werkstoff(
            rgb(31, 31, 31), "Dense SiC Compact", subscriptNumbers("SiC"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, metalCeramicFeatures(), id(), TextureSet.SET_METALLIC
        ));

        SinteredSiliconCarbide = register(new Werkstoff(
            rgb(61, 61, 64), "Sintered Silicon Carbide", subscriptNumbers("SiC"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, ceramicFeatures(), id(), TextureSet.SET_DULL
        ));

        // -------------------------
        // hBN CHAIN — CERAMICS
        // -------------------------
        BoronCarbide = register(new Werkstoff(
            rgb(43, 43, 43), "Boron Carbide", subscriptNumbers("B4C"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, gearedCeramicFeatures(), id(), TextureSet.SET_DULL
        ));

        CrudeHBN = register(new Werkstoff(
            rgb(239, 239, 239), "Crude hBN", subscriptNumbers("BN*"),
            new Werkstoff.Stats(), Werkstoff.Types.MIXTURE, ceramicFeatures(), id(), TextureSet.SET_DULL
        ));

        BNitrideWaste = register(new Werkstoff(
            rgb(239, 239, 239), "Boron Nitride Waste", "??BN??",
            new Werkstoff.Stats(), Werkstoff.Types.MIXTURE, ceramicFeatures(), id(), TextureSet.SET_DULL
        ));

        HBNPowderBlend = register(new Werkstoff(
            rgb(218, 218, 218), "hBN Powder Blend", subscriptNumbers("BN+Y2"),
            new Werkstoff.Stats(), Werkstoff.Types.MIXTURE, ceramicFeatures(), id(), TextureSet.SET_DULL
        ));

        DenseHBNCeramic = register(new Werkstoff(
            rgb(245, 245, 245), "Dense hBN Ceramic", subscriptNumbers("BN"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, metalCeramicFeatures(), id(), TextureSet.SET_DULL
        ));

        HexagonalBoronNitride = register(new Werkstoff(
            rgb(240, 240, 236), "Hexagonal Boron Nitride", subscriptNumbers("BN"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, ceramicFeatures(), id(), TextureSet.SET_DULL
        ));

        // -------------------------
        // SILICA GEL CHAIN
        // -------------------------
        WetSilicaGel = register(new Werkstoff(
            rgb(191, 230, 255), "Wet Silica Gel", subscriptNumbers("SiO2·nH2O"),
            new Werkstoff.Stats(), Werkstoff.Types.MIXTURE, ceramicFeatures(), id(), TextureSet.SET_DULL
        ));

        AgedSilicaGel = register(new Werkstoff(
            rgb(158, 210, 242), "Aged Silica Gel", subscriptNumbers("SiO2"),
            new Werkstoff.Stats(), Werkstoff.Types.MATERIAL, ceramicFeatures(), id(), TextureSet.SET_DULL
        ));

        EthanolSaturatedGel = register(new Werkstoff(
            rgb(255, 224, 178), "Ethanol Saturated Gel", subscriptNumbers("SiO2/C2H5OH"),
            new Werkstoff.Stats(), Werkstoff.Types.MIXTURE, ceramicFeatures(), id(), TextureSet.SET_DULL
        ));

        // -------------------------
        // NYLON CHAIN INTERMEDIATES
        // -------------------------
        Adiponitrile = register(new Werkstoff(
            rgb(208, 255, 208), "Adiponitrile", subscriptNumbers("C6H8N2"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, polymerFeatures(), id(), TextureSet.SET_DULL
        ));

        HMD = register(new Werkstoff(
            rgb(240, 240, 255), "Hexamethylenediamine", subscriptNumbers("C6H16N2"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, polymerFeatures(), id(), TextureSet.SET_DULL
        ));

        // -------------------------
        // PLA CHAIN INTERMEDIATES
        // -------------------------
        LacticAcid = register(new Werkstoff(
            rgb(255, 224, 224), "Lactic Acid", subscriptNumbers("C3H6O3"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        Lactide = register(new Werkstoff(
            rgb(255, 255, 255), "Lactide", subscriptNumbers("C6H8O4"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, polymerFeatures(), id(), TextureSet.SET_DULL
        ));

        // -------------------------
        // KAPTON CHAIN INTERMEDIATES
        // -------------------------
        PMDA = register(new Werkstoff(
            rgb(255, 251, 230), "PMDA", subscriptNumbers("C10H2O6"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, polymerFeatures(), id(), TextureSet.SET_DULL
        ));

        ODA = register(new Werkstoff(
            rgb(255, 228, 196), "ODA", subscriptNumbers("C12H12N2O"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, polymerFeatures(), id(), TextureSet.SET_DULL
        ));

        ConcentratedPAA = register(new Werkstoff(
            rgb(255, 224, 130), "Concentrated Polyamic Acid", subscriptNumbers("C22H14N2O7"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, polymerFeatures(), id(), TextureSet.SET_DULL
        ));

        PolyamicAcidFilm = register(new Werkstoff(
            rgb(255, 215, 0), "Polyamic Acid Film", subscriptNumbers("C22H14N2O7"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, polymerFeatures(), id(), TextureSet.SET_DULL
        ));

        // -------------------------
        // NYLON CAPROLACTAM ROUTE
        // -------------------------
        CyclohexanoneOxime = register(new Werkstoff(
            rgb(240, 245, 230), "Cyclohexanone Oxime", subscriptNumbers("C6H11NO"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, polymerFeatures(), id(), TextureSet.SET_DULL
        ));

        HydroxylammoniumSulfate = register(new Werkstoff(
            rgb(245, 245, 245), "Hydroxylammonium Sulfate", subscriptNumbers("(NH3OH)2SO4"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, polymerFeatures(), id(), TextureSet.SET_DULL
        ));

        Caprolactam = register(new Werkstoff(
            rgb(240, 240, 250), "Caprolactam", subscriptNumbers("C6H11NO"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        // -------------------------
        // KAPTON CHEMICAL IMIDIZATION
        // -------------------------
        Triethylamine = register(new Werkstoff(
            rgb(200, 215, 240), "Triethylamine", subscriptNumbers("N(C2H5)3"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        AceticAnhydride = register(new Werkstoff(
            rgb(245, 245, 240), "Acetic Anhydride", subscriptNumbers("C4H6O3"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        Ketene = register(new Werkstoff(
            rgb(250, 255, 245), "Ketene", subscriptNumbers("C2H2O"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        // -------------------------
        // CARBON FIBER PITCH ROUTE
        // -------------------------
        MesophasePitch = register(new Werkstoff(
            rgb(20, 15, 10), "Mesophase Pitch", subscriptNumbers("(C)n"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        // -------------------------
        // AEROGEL AMBIENT DRYING
        // -------------------------
        Trimethylsilane = register(new Werkstoff(
            rgb(210, 230, 210), "Trimethylsilane", subscriptNumbers("(CH3)3SiH"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        Trimethylchlorosilane = register(new Werkstoff(
            rgb(205, 225, 205), "Trimethylchlorosilane", subscriptNumbers("(CH3)3SiCl"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        // -------------------------
        // PLA ALTERNATE ROUTES
        // -------------------------
        PropyleneGlycol = register(new Werkstoff(
            rgb(220, 245, 220), "Propylene Glycol", subscriptNumbers("C3H8O2"),
            new Werkstoff.Stats(), Werkstoff.Types.COMPOUND, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        // -------------------------
        // USES — DERIVED MATERIALS
        // -------------------------
        HBNLubricant = register(new Werkstoff(
            rgb(230, 240, 255), "hBN Lubricant", subscriptNumbers("BN/oil"),
            new Werkstoff.Stats(), Werkstoff.Types.MIXTURE, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        PAAAdhesive = register(new Werkstoff(
            rgb(255, 210, 80), "PAA Adhesive", subscriptNumbers("C22H14N2O7/NMP"),
            new Werkstoff.Stats(), Werkstoff.Types.MIXTURE, fluidFeatures(), id(), TextureSet.SET_FLUID
        ));

        LoadedAerogelCatalystSupport = register(new Werkstoff(
            rgb(180, 210, 240), "Loaded Aerogel Catalyst Support", subscriptNumbers("SiO2/PGM"),
            new Werkstoff.Stats(), Werkstoff.Types.MIXTURE, polymerFeatures(), id(), TextureSet.SET_DULL
        ));

        AerogelInsulationPanel = register(new Werkstoff(
            rgb(195, 225, 248), "Aerogel Insulation Panel", subscriptNumbers("SiO2/(C)n"),
            new Werkstoff.Stats(), Werkstoff.Types.MIXTURE, ceramicFeatures(), id(), TextureSet.SET_DULL
        ));
    }

    // =========================
    // HELPERS
    // =========================
    private static Werkstoff register(Werkstoff w) {
        ALL.add(w);
        return w;
    }

    private static short[] rgb(int r, int g, int b) {
        return new short[]{(short) r, (short) g, (short) b};
    }
}
