package com.gtnh.processingplus.materials;

import gregtech.api.enums.*;
import gregtech.api.interfaces.IOreRecipeRegistrator;
import gregtech.api.interfaces.IMaterialHandler;

//public class GTNHPPMaterials implements IMaterialHandler {
//
//    public static Materials Nylon66;
//    public static Materials PolylacticAcid;
//    public static Materials Kapton;
//    public static Materials SinteredSiliconCarbide;
//    public static Materials PAASolution;
//    public static Materials TEOS;
//    public static Materials HexagonalBoronNitride;
//    public static Materials CarbonFiberComposite;
//    public static Materials SilicaAerogel;
//
//    public static Materials Polyacrylonitrile;
//    public static Materials StabilizedPolyacrylonitrile;
//    public static Materials PolyacrylonitrileSolution;
//    public static Materials DilutedNMethylPyrrolidone;
//    public static Materials HydrogenCyanide;
//    public static Materials Cyclohexanol;
//    public static Materials AdipicAcid;
//
//    @Override
//    public void onMaterialsInit() {
//
//        // =========================
//        // POLYMERS / SOLIDS
//        // =========================
//
//        Nylon66 = new MaterialBuilder()
//            .setName("Nylon66")
//            .setDefaultLocalName("Nylon-6,6")
//            .setChemicalFormula("C12H22N2O2")
//            .setARGB(0xFFF5EAD0)
//            .setColor(Dyes.dyeWhite)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .addGearItems()
//            .setMeltingPoint(533)
//            .constructMaterial();
//
//        Nylon66.setProcessingMaterialTierEU(TierEU.RECIPE_ZPM);
//
//        PolylacticAcid = new MaterialBuilder()
//            .setName("PolylacticAcid")
//            .setDefaultLocalName("Polylactic Acid")
//            .setChemicalFormula("C3H4O2")
//            .setARGB(0xFFF5F5F0)
//            .setColor(Dyes.dyeWhite)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .setMeltingPoint(450)
//            .constructMaterial();
//
//        PolylacticAcid.setProcessingMaterialTierEU(TierEU.RECIPE_ZPM);
//
//        Kapton = new MaterialBuilder()
//            .setName("Kapton")
//            .setDefaultLocalName("Polyimide (Kapton)")
//            .setChemicalFormula("C22H10N2O5")
//            .setARGB(0xFFCC8800)
//            .setColor(Dyes.dyeYellow)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial();
//
//        Kapton.setProcessingMaterialTierEU(TierEU.RECIPE_UV);
//
//        SinteredSiliconCarbide = new MaterialBuilder()
//            .setName("SinteredSiliconCarbide")
//            .setDefaultLocalName("Sintered Silicon Carbide")
//            .setChemicalFormula("SiC")
//            .setARGB(0xFF3D3D40)
//            .setColor(Dyes.dyeGray)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .addGearItems()
//            .constructMaterial();
//
//        SinteredSiliconCarbide.setProcessingMaterialTierEU(TierEU.RECIPE_UV);
//
//        HexagonalBoronNitride = new MaterialBuilder()
//            .setName("HexagonalBoronNitride")
//            .setDefaultLocalName("Hexagonal Boron Nitride")
//            .setChemicalFormula("BN")
//            .setARGB(0xFFF0F0EC)
//            .setColor(Dyes.dyeWhite)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial();
//
//        HexagonalBoronNitride.setProcessingMaterialTierEU(TierEU.RECIPE_UHV);
//
//        CarbonFiberComposite = new MaterialBuilder()
//            .setName("CarbonFiberComposite")
//            .setDefaultLocalName("Carbon Fiber Composite")
//            .setARGB(0xFF1A1A2E)
//            .setColor(Dyes.dyeBlack)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .addGearItems()
//            .constructMaterial();
//
//        CarbonFiberComposite.setProcessingMaterialTierEU(TierEU.RECIPE_UV);
//
//        SilicaAerogel = new MaterialBuilder()
//            .setName("SilicaAerogel")
//            .setDefaultLocalName("Silica Aerogel")
//            .setChemicalFormula("SiO2")
//            .setARGB(0xFFD0E8FF)
//            .setColor(Dyes.dyeLightBlue)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial();
//
//        SilicaAerogel.setProcessingMaterialTierEU(TierEU.RECIPE_UHV);
//
//        // =========================
//        // FLUIDS / CHEMICALS
//        // =========================
//
//        TEOS = new MaterialBuilder()
//            .setName("TEOS")
//            .setDefaultLocalName("Tetraethyl Orthosilicate")
//            .setChemicalFormula("Si(OC2H5)4")
//            .setARGB(0xFFE8F4FF)
//            .setColor(Dyes.dyeWhite)
//            .setIconSet(TextureSet.SET_FLUID)
//            .addFluid()
//            .constructMaterial();
//
//        PAASolution = new MaterialBuilder()
//            .setName("PAASolution")
//            .setDefaultLocalName("Polyamic Acid Solution")
//            .setChemicalFormula("C22H14N2O7")
//            .setARGB(0xFFFFE082)
//            .setColor(Dyes.dyeYellow)
//            .setIconSet(TextureSet.SET_FLUID)
//            .addFluid()
//            .constructMaterial();
//
//        // =========================
//        // PAN CHAIN
//        // =========================
//
//        Polyacrylonitrile = new MaterialBuilder()
//            .setName("Polyacrylonitrile")
//            .setDefaultLocalName("Polyacrylonitrile")
//            .setChemicalFormula("(C3H3N)n")
//            .setARGB(0xFFEEEEEE)
//            .setColor(Dyes.dyeWhite)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial();
//
//        StabilizedPolyacrylonitrile = new MaterialBuilder()
//            .setName("StabilizedPAN")
//            .setDefaultLocalName("Stabilized Polyacrylonitrile")
//            .setChemicalFormula("(C3H3N)nO")
//            .setARGB(0xFF222222)
//            .setColor(Dyes.dyeBlack)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial();
//
//        PolyacrylonitrileSolution = new MaterialBuilder()
//            .setName("PAN_Solution")
//            .setDefaultLocalName("Polyacrylonitrile Solution")
//            .setChemicalFormula("PAN/NMP")
//            .setARGB(0xFFE5C100)
//            .setColor(Dyes.dyeYellow)
//            .setIconSet(TextureSet.SET_FLUID)
//            .addFluid()
//            .constructMaterial();
//
//        DilutedNMethylPyrrolidone = new MaterialBuilder()
//            .setName("DilutedNMP")
//            .setDefaultLocalName("Diluted NMP")
//            .setChemicalFormula("C5H9NO·H2O")
//            .setARGB(0xFF88AABB)
//            .setColor(Dyes.dyeLightBlue)
//            .setIconSet(TextureSet.SET_FLUID)
//            .addFluid()
//            .constructMaterial();
//
//        HydrogenCyanide = new MaterialBuilder()
//            .setName("HydrogenCyanide")
//            .setDefaultLocalName("Hydrogen Cyanide")
//            .setChemicalFormula("HCN")
//            .setARGB(0xFFB0E0E6)
//            .setColor(Dyes.dyeBlue)
//            .setIconSet(TextureSet.SET_FLUID)
//            .addGas()
//            .constructMaterial();
//
//        Cyclohexanol = new MaterialBuilder()
//            .setName("Cyclohexanol")
//            .setDefaultLocalName("Cyclohexanol")
//            .setChemicalFormula("C6H12O")
//            .setARGB(0xFFEFEFEF)
//            .setColor(Dyes.dyeWhite)
//            .setIconSet(TextureSet.SET_DULL)
//            .addFluid()
//            .constructMaterial();
//
//        AdipicAcid = new MaterialBuilder()
//            .setName("AdipicAcid")
//            .setDefaultLocalName("Adipic Acid")
//            .setChemicalFormula("C6H10O4")
//            .setARGB(0xFFFFD6A5)
//            .setColor(Dyes.dyeWhite)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial();
//    }
//}

import gregtech.api.enums.*;
import net.minecraft.block.material.Material;

public class GTNHPPMaterials {

    public static Materials Nylon66;
    public static Materials PolylacticAcid;
    public static Materials Kapton;
    public static Materials SinteredSiliconCarbide;
    public static Materials PAASolution;
    public static Materials TEOS;
    public static Materials HexagonalBoronNitride;
    public static Materials CarbonFiberComposite;
    public static Materials SilicaAerogel;

    public static Materials Polyacrylonitrile;
    public static Materials StabilizedPolyacrylonitrile;
    public static Materials PolyacrylonitrileSolution;
    public static Materials DilutedNMP;
    public static Materials HydrogenCyanide;
    public static Materials Cyclohexanol;
    public static Materials Cyclohexene;
    public static Materials AdipicAcid;

    public static Materials CrudeSiCPowder;
    public static Materials PurifiedSiCPowder;
    public static Materials DenseSiCCompact;

    public static Materials BoronCarbide;
    public static Materials CrudeHBN;
    public static Materials HBNPowderBlend;
    public static Materials DenseHBNCeramic;

    public static Materials Acrylonitrile;
    public static Materials CarbonFiberTow;

    public static Materials EthanolSaturatedGel;
    public static Materials WetSilicaGel;
    public static Materials AgedSilicaGel;

    public static void init() {

        // =========================
        // POLYMERS / SOLIDS
        // =========================

        Nylon66 = new MaterialBuilder()
            .setName("Nylon66")
            .setDefaultLocalName("Nylon-6,6")
            .setChemicalFormula("C12H22N2O2")
            .setARGB(0xFFF5EAD0)
            .setColor(Dyes.dyeWhite)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .addGearItems()
            .setMeltingPoint(533)
            .constructMaterial();

        Nylon66.setProcessingMaterialTierEU(TierEU.RECIPE_ZPM);

        PolylacticAcid = new MaterialBuilder()
            .setName("PolylacticAcid")
            .setDefaultLocalName("Polylactic Acid")
            .setChemicalFormula("C3H4O2")
            .setARGB(0xFFF5F5F0)
            .setColor(Dyes.dyeWhite)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .setMeltingPoint(450)
            .constructMaterial();

        PolylacticAcid.setProcessingMaterialTierEU(TierEU.RECIPE_ZPM);

        Kapton = new MaterialBuilder()
            .setName("Kapton")
            .setDefaultLocalName("Polyimide (Kapton)")
            .setChemicalFormula("C22H10N2O5")
            .setARGB(0xFFCC8800)
            .setColor(Dyes.dyeYellow)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .constructMaterial();

        Kapton.setProcessingMaterialTierEU(TierEU.RECIPE_UV);

        SinteredSiliconCarbide = new MaterialBuilder()
            .setName("SinteredSiliconCarbide")
            .setDefaultLocalName("Sintered Silicon Carbide")
            .setChemicalFormula("SiC")
            .setARGB(0xFF3D3D40)
            .setColor(Dyes.dyeGray)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .addGearItems()
            .constructMaterial();

        SinteredSiliconCarbide.setProcessingMaterialTierEU(TierEU.RECIPE_UV);

        HexagonalBoronNitride = new MaterialBuilder()
            .setName("HexagonalBoronNitride")
            .setDefaultLocalName("Hexagonal Boron Nitride")
            .setChemicalFormula("BN")
            .setARGB(0xFFF0F0EC)
            .setColor(Dyes.dyeWhite)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .constructMaterial();

        HexagonalBoronNitride.setProcessingMaterialTierEU(TierEU.RECIPE_UHV);

        CarbonFiberComposite = new MaterialBuilder()
            .setName("CarbonFiberComposite")
            .setDefaultLocalName("Carbon Fiber Composite")
            .setARGB(0xFF1A1A2E)
            .setColor(Dyes.dyeBlack)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .addGearItems()
            .constructMaterial();

        CarbonFiberComposite.setProcessingMaterialTierEU(TierEU.RECIPE_UV);

        SilicaAerogel = new MaterialBuilder()
            .setName("SilicaAerogel")
            .setDefaultLocalName("Silica Aerogel")
            .setChemicalFormula("SiO2")
            .setARGB(0xFFD0E8FF)
            .setColor(Dyes.dyeLightBlue)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .constructMaterial();

        SilicaAerogel.setProcessingMaterialTierEU(TierEU.RECIPE_UHV);

        // =========================
        // FLUIDS / CHEMICALS
        // =========================

        TEOS = new MaterialBuilder()
            .setName("TEOS")
            .setDefaultLocalName("Tetraethyl Orthosilicate")
            .setChemicalFormula("Si(OC2H5)4")
            .setARGB(0xFFE8F4FF)
            .setColor(Dyes.dyeWhite)
            .setIconSet(TextureSet.SET_FLUID)
            .addFluid()
            .constructMaterial();

        PAASolution = new MaterialBuilder()
            .setName("PAASolution")
            .setDefaultLocalName("Polyamic Acid Solution")
            .setChemicalFormula("C22H14N2O7")
            .setARGB(0xFFFFE082)
            .setColor(Dyes.dyeYellow)
            .setIconSet(TextureSet.SET_FLUID)
            .addFluid()
            .constructMaterial();

        Polyacrylonitrile = new MaterialBuilder()
            .setName("Polyacrylonitrile")
            .setDefaultLocalName("Polyacrylonitrile")
            .setChemicalFormula("(C3H3N)n")
            .setARGB(0xFFEEEEEE)
            .setColor(Dyes.dyeWhite)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .constructMaterial();

        StabilizedPolyacrylonitrile = new MaterialBuilder()
            .setName("StabilizedPAN")
            .setDefaultLocalName("Stabilized Polyacrylonitrile")
            .setChemicalFormula("(C3H3N)nO")
            .setARGB(0xFF222222)
            .setColor(Dyes.dyeBlack)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .constructMaterial();

        PolyacrylonitrileSolution = new MaterialBuilder()
            .setName("PAN_Solution")
            .setDefaultLocalName("Polyacrylonitrile Solution")
            .setChemicalFormula("PAN/NMP")
            .setARGB(0xFFE5C100)
            .setColor(Dyes.dyeYellow)
            .setIconSet(TextureSet.SET_FLUID)
            .addFluid()
            .constructMaterial();

        DilutedNMP = new MaterialBuilder()
            .setName("DilutedNMP")
            .setDefaultLocalName("Diluted NMP")
            .setChemicalFormula("C5H9NO·H2O")
            .setARGB(0xFF88AABB)
            .setColor(Dyes.dyeLightBlue)
            .setIconSet(TextureSet.SET_FLUID)
            .addFluid()
            .constructMaterial();

        HydrogenCyanide = new MaterialBuilder()
            .setName("HydrogenCyanide")
            .setDefaultLocalName("Hydrogen Cyanide")
            .setChemicalFormula("HCN")
            .setARGB(0xFFB0E0E6)
            .setColor(Dyes.dyeBlue)
            .setIconSet(TextureSet.SET_FLUID)
            .addGas()
            .constructMaterial();

        Cyclohexanol = new MaterialBuilder()
            .setName("Cyclohexanol")
            .setDefaultLocalName("Cyclohexanol")
            .setChemicalFormula("C6H12O")
            .setARGB(0xFFEFEFEF)
            .setColor(Dyes.dyeWhite)
            .setIconSet(TextureSet.SET_DULL)
            .addFluid()
            .constructMaterial();

        Cyclohexene = new MaterialBuilder()
            .setName("Cyclohexene")
            .setDefaultLocalName("Cyclohexene")
            .setChemicalFormula("C6H10")
            .setARGB(0xFFEFEFEF)
            .setColor(Dyes.dyeWhite)
            .setIconSet(TextureSet.SET_DULL)
            .addFluid()
            .constructMaterial();

        AdipicAcid = new MaterialBuilder()
            .setName("AdipicAcid")
            .setDefaultLocalName("Adipic Acid")
            .setChemicalFormula("C6H10O4")
            .setARGB(0xFFFFD6A5)
            .setColor(Dyes.dyeWhite)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .constructMaterial();
        // =========================
        // SILICON CARBIDE PROCESSING CHAIN INTERMEDIATES
        // =========================

        CrudeSiCPowder = new MaterialBuilder()
            .setName("CrudeSiCPowder")
            .setDefaultLocalName("Crude Silicon Carbide Powder")
            .setChemicalFormula("SiC + impurities")
            .setARGB(0xFF4A4A4A)
            .setColor(Dyes.dyeGray)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .constructMaterial();

        PurifiedSiCPowder = new MaterialBuilder()
            .setName("PurifiedSiCPowder")
            .setDefaultLocalName("Purified Silicon Carbide Powder")
            .setChemicalFormula("SiC")
            .setARGB(0xFF2F2F2F)
            .setColor(Dyes.dyeGray)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .constructMaterial();

        DenseSiCCompact = new MaterialBuilder()
            .setName("DenseSiCCompact")
            .setDefaultLocalName("Dense SiC Compact")
            .setChemicalFormula("SiC")
            .setARGB(0xFF1F1F1F)
            .setColor(Dyes.dyeBlack)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .addMetalItems()
            .constructMaterial();

        BoronCarbide = new MaterialBuilder()
            .setName("BoronCarbide")
            .setDefaultLocalName("Boron Carbide")
            .setChemicalFormula("B4C")
            .setARGB(0xFF2B2B2B)
            .setColor(Dyes.dyeGray)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .addGearItems()
            .constructMaterial();

        CrudeHBN = new MaterialBuilder()
            .setName("CrudeHBN")
            .setDefaultLocalName("Crude Boron Nitride")
            .setChemicalFormula("BN*")
            .setARGB(0xFFEFEFEF)
            .setColor(Dyes.dyeWhite)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .constructMaterial();

        HBNPowderBlend = new MaterialBuilder()
            .setName("HBNPowderBlend")
            .setDefaultLocalName("hBN Powder Blend")
            .setChemicalFormula("BN+Y")
            .setARGB(0xFFDADADA)
            .setColor(Dyes.dyeLightGray)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .constructMaterial();

        DenseHBNCeramic = new MaterialBuilder()
            .setName("DenseHBNCeramic")
            .setDefaultLocalName("Dense hBN Ceramic")
            .setChemicalFormula("BN")
            .setARGB(0xFFF5F5F5)
            .setColor(Dyes.dyeWhite)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .addMetalItems()
            .constructMaterial();

        Acrylonitrile = new MaterialBuilder()
            .setName("Acrylonitrile")
            .setDefaultLocalName("Acrylonitrile")
            .setChemicalFormula("C3H3N")
            .setARGB(0xFFB0B0B0)
            .setColor(Dyes.dyeLightGray)
            .setIconSet(TextureSet.SET_FLUID)
            .addFluid()
            .constructMaterial();

        Acrylonitrile.setProcessingMaterialTierEU(TierEU.RECIPE_LuV);

        CarbonFiberTow = new MaterialBuilder()
            .setName("CarbonFiberTow")
            .setDefaultLocalName("Carbon Fiber Tow")
            .setChemicalFormula("(C)n")
            .setARGB(0xFF1A1A1A)
            .setColor(Dyes.dyeBlack)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .constructMaterial();

        CarbonFiberTow.setProcessingMaterialTierEU(TierEU.RECIPE_UV);

        WetSilicaGel = new MaterialBuilder()
            .setName("WetSilicaGel")
            .setDefaultLocalName("Wet Silica Gel")
            .setChemicalFormula("SiO2·nH2O")
            .setARGB(0xFFBFE6FF)
            .setColor(Dyes.dyeLightBlue)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .constructMaterial();

        WetSilicaGel.setProcessingMaterialTierEU(TierEU.RECIPE_UHV);

        AgedSilicaGel = new MaterialBuilder()
            .setName("AgedSilicaGel")
            .setDefaultLocalName("Aged Silica Gel")
            .setChemicalFormula("SiO2")
            .setARGB(0xFF9ED2F2)
            .setColor(Dyes.dyeLightBlue)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .constructMaterial();

        AgedSilicaGel.setProcessingMaterialTierEU(TierEU.RECIPE_UHV);

        EthanolSaturatedGel = new MaterialBuilder()
            .setName("EthanolSaturatedGel")
            .setDefaultLocalName("Ethanol Saturated Gel")
            .setChemicalFormula("SiO2/C2H5OH")
            .setARGB(0xFFFFE0B2)
            .setColor(Dyes.dyeYellow)
            .setIconSet(TextureSet.SET_DULL)
            .addDustItems()
            .constructMaterial();

        EthanolSaturatedGel.setProcessingMaterialTierEU(TierEU.RECIPE_UHV);
    }
}
