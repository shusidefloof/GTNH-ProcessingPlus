package com.gtnh.processingplus.materials;

import com.gtnh.processingplus.Tags;

import gregtech.api.enums.*;
import gregtech.api.interfaces.IMaterialHandler;

public class GTNHPPMaterials implements IMaterialHandler {

    // Polymers & Solids
    public static Materials Nylon66, PolylacticAcid, Kapton, SinteredSiliconCarbide, HexagonalBoronNitride,
        CarbonFiberComposite, SilicaAerogel;

    // PAN Chain
    public static Materials Polyacrylonitrile, StabilizedPolyacrylonitrile, PolyacrylonitrileSolution, DilutedNMP,
        HydrogenCyanide, Acrylonitrile, CarbonFiberTow, Cyclohexanol, Cyclohexene, AdipicAcid;

    // SiC / BN / Silica Gel Chains
    public static Materials CrudeSiCPowder, PurifiedSiCPowder, DenseSiCCompact, BoronCarbide, CrudeHBN,
        HBNPowderBlend, DenseHBNCeramic, WetSilicaGel, AgedSilicaGel, EthanolSaturatedGel, TEOS, PAASolution;

    @Override
    public void onMaterialsInit() {

        System.out.println("GT:NH Processing+ MATERIALS INIT");

        int id = 28000;

        // =========================
        // POLYMERS
        // =========================

//        Nylon66 = new MaterialBuilder().setName("Nylon66")
//            .setDefaultLocalName("Nylon-6,6")
//            .setChemicalFormula("C12H22N2O2")
//            .setARGB(0xFFF5EAD0)
//            .setColor(Dyes.dyeWhite)
//            .setIconSet(TextureSet.SET_DULL)
//            .setUnifiable(false)
//            .addMetalItems()
//            .addDustItems()
//            .addGearItems()
//            .setBlastFurnaceTemp(533)
//            .constructMaterial()
//            .setProcessingMaterialTierEU(TierEU.RECIPE_ZPM);
//        Nylon66.mMetaItemSubID = id++;
//
//        PolylacticAcid = new MaterialBuilder().setName("PolylacticAcid")
//            .setDefaultLocalName("Polylactic Acid")
//            .setChemicalFormula("C3H4O2")
//            .setARGB(0xFFF5F5F0)
//            .setColor(Dyes.dyeWhite)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .setBlastFurnaceTemp(450)
//            .constructMaterial()
//            .setProcessingMaterialTierEU(TierEU.RECIPE_ZPM);
//        PolylacticAcid.mMetaItemSubID = id++;
//
//        Kapton = new MaterialBuilder().setName("Kapton")
//            .setDefaultLocalName("Polyimide (Kapton)")
//            .setChemicalFormula("C22H10N2O5")
//            .setARGB(0xFFCC8800)
//            .setColor(Dyes.dyeYellow)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial()
//            .setProcessingMaterialTierEU(TierEU.RECIPE_UV);
//        Kapton.mMetaItemSubID = id++;
//
//        SinteredSiliconCarbide = new MaterialBuilder().setName("SinteredSiliconCarbide")
//            .setDefaultLocalName("Sintered Silicon Carbide")
//            .setChemicalFormula("SiC")
//            .setARGB(0xFF3D3D40)
//            .setColor(Dyes.dyeGray)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .addGearItems()
//            .constructMaterial()
//            .setProcessingMaterialTierEU(TierEU.RECIPE_UV);
//        SinteredSiliconCarbide.mMetaItemSubID = id++;
//
//        HexagonalBoronNitride = new MaterialBuilder().setName("HexagonalBoronNitride")
//            .setDefaultLocalName("Hexagonal Boron Nitride")
//            .setChemicalFormula("BN")
//            .setARGB(0xFFF0F0EC)
//            .setColor(Dyes.dyeWhite)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial()
//            .setProcessingMaterialTierEU(TierEU.RECIPE_UHV);
//        HexagonalBoronNitride.mMetaItemSubID = id++;
//
//        CarbonFiberComposite = new MaterialBuilder().setName("CarbonFiberComposite")
//            .setDefaultLocalName("Carbon Fiber Composite")
//            .setARGB(0xFF1A1A2E)
//            .setColor(Dyes.dyeBlack)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .addGearItems()
//            .constructMaterial()
//            .setProcessingMaterialTierEU(TierEU.RECIPE_UV);
//        CarbonFiberComposite.mMetaItemSubID = id++;
//
//        SilicaAerogel = new MaterialBuilder().setName("SilicaAerogel")
//            .setDefaultLocalName("Silica Aerogel")
//            .setChemicalFormula("SiO2")
//            .setARGB(0xFFD0E8FF)
//            .setColor(Dyes.dyeLightBlue)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial()
//            .setProcessingMaterialTierEU(TierEU.RECIPE_UHV);
//        SilicaAerogel.mMetaItemSubID = id++;
//
//        // =========================
//        // FLUIDS
//        // =========================
//
//        TEOS = new MaterialBuilder().setName("TEOS")
//            .setDefaultLocalName("Tetraethyl Orthosilicate")
//            .setChemicalFormula("Si(OC2H5)4")
//            .setARGB(0xFFE8F4FF)
//            .setIconSet(TextureSet.SET_FLUID)
//            .addFluid()
//            .constructMaterial();
//        TEOS.mMetaItemSubID = id++;
//
//        PAASolution = new MaterialBuilder().setName("PAASolution")
//            .setDefaultLocalName("Polyamic Acid Solution")
//            .setChemicalFormula("C22H14N2O7")
//            .setARGB(0xFFFFE082)
//            .setIconSet(TextureSet.SET_FLUID)
//            .addFluid()
//            .constructMaterial();
//        PAASolution.mMetaItemSubID = id++;
//
//        PolyacrylonitrileSolution = new MaterialBuilder().setName("PAN_Solution")
//            .setDefaultLocalName("Polyacrylonitrile Solution")
//            .setChemicalFormula("PAN/NMP")
//            .setARGB(0xFFE5C100)
//            .setIconSet(TextureSet.SET_FLUID)
//            .addFluid()
//            .constructMaterial();
//        PolyacrylonitrileSolution.mMetaItemSubID = id++;
//
//        DilutedNMP = new MaterialBuilder().setName("DilutedNMP")
//            .setDefaultLocalName("Diluted NMP")
//            .setChemicalFormula("C5H9NO·H2O")
//            .setARGB(0xFF88AABB)
//            .setIconSet(TextureSet.SET_FLUID)
//            .addFluid()
//            .constructMaterial();
//        DilutedNMP.mMetaItemSubID = id++;
//
//        HydrogenCyanide = new MaterialBuilder().setName("HydrogenCyanide")
//            .setDefaultLocalName("Hydrogen Cyanide")
//            .setChemicalFormula("HCN")
//            .setARGB(0xFFB0E0E6)
//            .setIconSet(TextureSet.SET_FLUID)
//            .addGas()
//            .constructMaterial();
//        HydrogenCyanide.mMetaItemSubID = id++;
//
//        Acrylonitrile = new MaterialBuilder().setName("Acrylonitrile")
//            .setDefaultLocalName("Acrylonitrile")
//            .setChemicalFormula("C3H3N")
//            .setARGB(0xFFB0B0B0)
//            .setIconSet(TextureSet.SET_FLUID)
//            .addFluid()
//            .constructMaterial()
//            .setProcessingMaterialTierEU(TierEU.RECIPE_LuV);
//        Acrylonitrile.mMetaItemSubID = id++;
//
//        Cyclohexanol = new MaterialBuilder().setName("Cyclohexanol")
//            .setDefaultLocalName("Cyclohexanol")
//            .setChemicalFormula("C6H12O")
//            .setARGB(0xFFEFEFEF)
//            .setIconSet(TextureSet.SET_FLUID)
//            .addFluid()
//            .constructMaterial();
//        Cyclohexanol.mMetaItemSubID = id++;
//
//        Cyclohexene = new MaterialBuilder().setName("Cyclohexene")
//            .setDefaultLocalName("Cyclohexene")
//            .setChemicalFormula("C6H10")
//            .setARGB(0xFFEFEFEF)
//            .setIconSet(TextureSet.SET_FLUID)
//            .addFluid()
//            .constructMaterial();
//        Cyclohexene.mMetaItemSubID = id++;
//
//        // =========================
//        // SOLIDS / POWDERS
//        // =========================
//
//        Polyacrylonitrile = new MaterialBuilder().setName("Polyacrylonitrile")
//            .setDefaultLocalName("Polyacrylonitrile")
//            .setChemicalFormula("(C3H3N)n")
//            .setARGB(0xFFEEEEEE)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial();
//        Polyacrylonitrile.mMetaItemSubID = id++;
//
//        StabilizedPolyacrylonitrile = new MaterialBuilder().setName("StabilizedPAN")
//            .setDefaultLocalName("Stabilized Polyacrylonitrile")
//            .setChemicalFormula("(C3H3N)nO")
//            .setARGB(0xFF222222)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial();
//        StabilizedPolyacrylonitrile.mMetaItemSubID = id++;
//
//        AdipicAcid = new MaterialBuilder().setName("AdipicAcid")
//            .setDefaultLocalName("Adipic Acid")
//            .setChemicalFormula("C6H10O4")
//            .setARGB(0xFFFFD6A5)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial();
//        AdipicAcid.mMetaItemSubID = id++;
//
//        CrudeSiCPowder = new MaterialBuilder().setName("CrudeSiCPowder")
//            .setDefaultLocalName("Crude SiC Powder")
//            .setChemicalFormula("SiC+")
//            .setARGB(0xFF4A4A4A)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial();
//        CrudeSiCPowder.mMetaItemSubID = id++;
//
//        PurifiedSiCPowder = new MaterialBuilder().setName("PurifiedSiCPowder")
//            .setDefaultLocalName("Purified SiC Powder")
//            .setChemicalFormula("SiC")
//            .setARGB(0xFF2F2F2F)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial();
//        PurifiedSiCPowder.mMetaItemSubID = id++;
//
//        DenseSiCCompact = new MaterialBuilder().setName("DenseSiCCompact")
//            .setDefaultLocalName("Dense SiC Compact")
//            .setChemicalFormula("SiC")
//            .setARGB(0xFF1F1F1F)
//            .setIconSet(TextureSet.SET_METALLIC)
//            .addDustItems()
//            .addMetalItems()
//            .constructMaterial();
//        DenseSiCCompact.mMetaItemSubID = id++;
//
//        BoronCarbide = new MaterialBuilder().setName("BoronCarbide")
//            .setDefaultLocalName("Boron Carbide")
//            .setChemicalFormula("B4C")
//            .setARGB(0xFF2B2B2B)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .addGearItems()
//            .constructMaterial();
//        BoronCarbide.mMetaItemSubID = id++;
//
//        CrudeHBN = new MaterialBuilder().setName("CrudeHBN")
//            .setDefaultLocalName("Crude hBN")
//            .setChemicalFormula("BN*")
//            .setARGB(0xFFEFEFEF)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial();
//        CrudeHBN.mMetaItemSubID = id++;
//
//        HBNPowderBlend = new MaterialBuilder().setName("HBNPowderBlend")
//            .setDefaultLocalName("hBN Powder Blend")
//            .setChemicalFormula("BN+Y")
//            .setARGB(0xFFDADADA)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial();
//        HBNPowderBlend.mMetaItemSubID = id++;
//
//        DenseHBNCeramic = new MaterialBuilder().setName("DenseHBNCeramic")
//            .setDefaultLocalName("Dense hBN Ceramic")
//            .setChemicalFormula("BN")
//            .setARGB(0xFFF5F5F5)
//            .setIconSet(TextureSet.SET_METALLIC)
//            .addDustItems()
//            .addMetalItems()
//            .constructMaterial();
//        DenseHBNCeramic.mMetaItemSubID = id++;
//
//        CarbonFiberTow = new MaterialBuilder().setName("CarbonFiberTow")
//            .setDefaultLocalName("Carbon Fiber Tow")
//            .setChemicalFormula("(C)n")
//            .setARGB(0xFF1A1A1A)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial()
//            .setProcessingMaterialTierEU(TierEU.RECIPE_UV);
//        CarbonFiberTow.mMetaItemSubID = id++;
//
//        WetSilicaGel = new MaterialBuilder().setName("WetSilicaGel")
//            .setDefaultLocalName("Wet Silica Gel")
//            .setChemicalFormula("SiO2·nH2O")
//            .setARGB(0xFFBFE6FF)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial()
//            .setProcessingMaterialTierEU(TierEU.RECIPE_UHV);
//        WetSilicaGel.mMetaItemSubID = id++;
//
//        AgedSilicaGel = new MaterialBuilder().setName("AgedSilicaGel")
//            .setDefaultLocalName("Aged Silica Gel")
//            .setChemicalFormula("SiO2")
//            .setARGB(0xFF9ED2F2)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial()
//            .setProcessingMaterialTierEU(TierEU.RECIPE_UHV);
//        AgedSilicaGel.mMetaItemSubID = id++;
//
//        EthanolSaturatedGel = new MaterialBuilder().setName("EthanolSaturatedGel")
//            .setDefaultLocalName("Ethanol Saturated Gel")
//            .setChemicalFormula("SiO2/C2H5OH")
//            .setARGB(0xFFFFE0B2)
//            .setIconSet(TextureSet.SET_DULL)
//            .addDustItems()
//            .constructMaterial()
//            .setProcessingMaterialTierEU(TierEU.RECIPE_UHV);
//        EthanolSaturatedGel.mMetaItemSubID = id++;
//
//        System.out.println("GT:NH Processing+ " + Tags.VERSION + " MATERIALS LOADED");
    }
}
