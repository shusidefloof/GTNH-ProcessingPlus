package com.gtnh.processingplus.recipes;

import static com.gtnh.processingplus.items.GTNHPPItemList.*;

import net.minecraft.item.ItemStack;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

import com.gtnh.processingplus.materials.GTNHPPMaterials;

public class GTNHPPRecipes {

    public static void init() {
        addFormConversions();
        addNylon66Chain();
        addPLAChain();
        addKaptonChain();
        addSiCChain();
        addHBNChain();
        addCFCChain();
        addAerogelChain();
    }

    private static ItemStack mat(OrePrefixes prefix, Materials material, int amount) {
        return GTOreDictUnificator.get(prefix, material, amount);
    }

    // =========================================================================
    // FORM CONVERSIONS — standard GT machines, ZPM/UV tier
    // =========================================================================

    private static void addFormConversions() {
        addThermoplasticForms(GTNHPPMaterials.Nylon66);
        addThermoplasticForms(GTNHPPMaterials.PolylacticAcid);
        addKaptonForms();
        addSiCForms();
        addHBNForms();
        addCFCForms();
        addAerogelForms();
    }

    private static void addThermoplasticForms(Materials m) {
        // Ingot → dust (macerator)
        GTValues.RA.stdBuilder()
            .itemInputs(m.getIngots(1))
            .itemOutputs(m.getDust(1))
            .duration(400).eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.maceratorRecipes);

        // Ingot → plate (bender)
        GTValues.RA.stdBuilder()
            .itemInputs(m.getIngots(3), GTUtility.getIntegratedCircuit(1))
            .itemOutputs(m.getPlates(3))
            .duration(600).eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.benderRecipes);

        // Ingot → rod (extruder)
        GTValues.RA.stdBuilder()
            .itemInputs(m.getIngots(2), ItemList.Shape_Extruder_Rod.get(0))
            .itemOutputs(mat(OrePrefixes.stick, m, 4))
            .duration(400).eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.extruderRecipes);

        // Ingot → gear (extruder)
        GTValues.RA.stdBuilder()
            .itemInputs(m.getIngots(4), ItemList.Shape_Extruder_Gear.get(0))
            .itemOutputs(mat(OrePrefixes.gearGt, m, 1))
            .duration(600).eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.extruderRecipes);

        // Ingot → ring (extruder)
        GTValues.RA.stdBuilder()
            .itemInputs(m.getIngots(1), ItemList.Shape_Extruder_Ring.get(0))
            .itemOutputs(mat(OrePrefixes.ring, m, 4))
            .duration(200).eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.extruderRecipes);

        // Ingot → bolt (extruder)
        GTValues.RA.stdBuilder()
            .itemInputs(m.getIngots(1), ItemList.Shape_Extruder_Bolt.get(0))
            .itemOutputs(mat(OrePrefixes.bolt, m, 8))
            .duration(200).eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.extruderRecipes);

        // Plate → foil (bender)
        GTValues.RA.stdBuilder()
            .itemInputs(m.getPlates(4), GTUtility.getIntegratedCircuit(2))
            .itemOutputs(mat(OrePrefixes.foil, m, 4))
            .duration(400).eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.benderRecipes);

        // Rod → spring (bender)
        GTValues.RA.stdBuilder()
            .itemInputs(mat(OrePrefixes.stick, m, 2), GTUtility.getIntegratedCircuit(8))
            .itemOutputs(mat(OrePrefixes.spring, m, 1))
            .duration(200).eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.benderRecipes);

        // Rod → bolt + screw (lathe)
        GTValues.RA.stdBuilder()
            .itemInputs(mat(OrePrefixes.stick, m, 1))
            .itemOutputs(mat(OrePrefixes.bolt, m, 2), mat(OrePrefixes.screw, m, 1))
            .duration(200).eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.latheRecipes);

        // Rod → round + tiny dust (lathe)
        GTValues.RA.stdBuilder()
            .itemInputs(mat(OrePrefixes.stick, m, 1))
            .itemOutputs(mat(OrePrefixes.round, m, 4), m.getDustTiny(1))
            .duration(150).eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.latheRecipes);
    }

    private static void addKaptonForms() {
        // Plate → foil (bender)
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.Kapton.getPlates(4), GTUtility.getIntegratedCircuit(2))
            .itemOutputs(mat(OrePrefixes.foil, GTNHPPMaterials.Kapton, 4))
            .duration(400).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.benderRecipes);

        // Plate → rod (cutter)
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.Kapton.getPlates(2))
            .fluidInputs(Materials.Lubricant.getFluid(20))
            .itemOutputs(mat(OrePrefixes.stick, GTNHPPMaterials.Kapton, 4))
            .duration(300).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.cutterRecipes);

        // Plate → ring (bender)
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.Kapton.getPlates(2), GTUtility.getIntegratedCircuit(4))
            .itemOutputs(mat(OrePrefixes.ring, GTNHPPMaterials.Kapton, 4))
            .duration(300).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.benderRecipes);

        // Rod → spring (bender)
        GTValues.RA.stdBuilder()
            .itemInputs(mat(OrePrefixes.stick, GTNHPPMaterials.Kapton, 2), GTUtility.getIntegratedCircuit(8))
            .itemOutputs(mat(OrePrefixes.spring, GTNHPPMaterials.Kapton, 1))
            .duration(300).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.benderRecipes);

        // Plate → dust (macerator)
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.Kapton.getPlates(1))
            .itemOutputs(GTNHPPMaterials.Kapton.getDust(2))
            .duration(400).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.maceratorRecipes);
    }

    private static void addSiCForms() {
        // Dust → plate via HPSF
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.SiliconCarbide.getDust(4), GTUtility.getIntegratedCircuit(1))
            .fluidInputs(Materials.Argon.getGas(500))
            .itemOutputs(GTNHPPMaterials.SiliconCarbide.getPlates(1))
            .duration(800).eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);

        // Dust → rod via HPSF
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.SiliconCarbide.getDust(2), GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Argon.getGas(250))
            .itemOutputs(mat(OrePrefixes.stick, GTNHPPMaterials.SiliconCarbide, 1))
            .duration(600).eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);

        // Rod → gear (lathe)
        GTValues.RA.stdBuilder()
            .itemInputs(mat(OrePrefixes.stick, GTNHPPMaterials.SiliconCarbide, 4))
            .fluidInputs(Materials.Lubricant.getFluid(40))
            .itemOutputs(mat(OrePrefixes.gearGt, GTNHPPMaterials.SiliconCarbide, 1), GTNHPPMaterials.SiliconCarbide.getDustSmall(4))
            .duration(1200).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.latheRecipes);

        // Rod → round (lathe)
        GTValues.RA.stdBuilder()
            .itemInputs(mat(OrePrefixes.stick, GTNHPPMaterials.SiliconCarbide, 1))
            .fluidInputs(Materials.Lubricant.getFluid(10))
            .itemOutputs(mat(OrePrefixes.round, GTNHPPMaterials.SiliconCarbide, 4), GTNHPPMaterials.SiliconCarbide.getDustTiny(2))
            .duration(600).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.latheRecipes);

        // Plate → dust (macerator)
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.SiliconCarbide.getPlates(1))
            .itemOutputs(GTNHPPMaterials.SiliconCarbide.getDust(3), GTNHPPMaterials.SiliconCarbide.getDustSmall(1))
            .duration(400).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.maceratorRecipes);
    }

    private static void addHBNForms() {
        // Dust → plate via HPSF
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.HexagonalBoronNitride.getDust(4), GTUtility.getIntegratedCircuit(1))
            .fluidInputs(Materials.Nitrogen.getGas(500))
            .itemOutputs(GTNHPPMaterials.HexagonalBoronNitride.getPlates(1))
            .duration(1000).eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);

        // Plate → dust (macerator)
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.HexagonalBoronNitride.getPlates(1))
            .itemOutputs(GTNHPPMaterials.HexagonalBoronNitride.getDust(3), GTNHPPMaterials.HexagonalBoronNitride.getDustSmall(1))
            .duration(400).eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.maceratorRecipes);
    }

    private static void addCFCForms() {
        // Plate → rod (cutter)
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.CarbonFiberComposite.getPlates(2))
            .fluidInputs(Materials.Lubricant.getFluid(20))
            .itemOutputs(mat(OrePrefixes.stick, GTNHPPMaterials.CarbonFiberComposite, 4))
            .duration(300).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.cutterRecipes);

        // Plate → foil (bender)
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.CarbonFiberComposite.getPlates(4), GTUtility.getIntegratedCircuit(2))
            .itemOutputs(mat(OrePrefixes.foil, GTNHPPMaterials.CarbonFiberComposite, 4))
            .duration(400).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.benderRecipes);

        // Rod → spring (bender)
        GTValues.RA.stdBuilder()
            .itemInputs(mat(OrePrefixes.stick, GTNHPPMaterials.CarbonFiberComposite, 2), GTUtility.getIntegratedCircuit(8))
            .itemOutputs(mat(OrePrefixes.spring, GTNHPPMaterials.CarbonFiberComposite, 1))
            .duration(300).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.benderRecipes);

        // Gear: assembler (4 plates + rod + epoxy)
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.CarbonFiberComposite.getPlates(4), mat(OrePrefixes.stick, GTNHPPMaterials.CarbonFiberComposite, 1))
            .fluidInputs(Materials.Epoxid.getFluid(144))
            .itemOutputs(mat(OrePrefixes.gearGt, GTNHPPMaterials.CarbonFiberComposite, 1))
            .duration(400).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.assemblerRecipes);

        // Rod → round (lathe)
        GTValues.RA.stdBuilder()
            .itemInputs(mat(OrePrefixes.stick, GTNHPPMaterials.CarbonFiberComposite, 1))
            .itemOutputs(mat(OrePrefixes.round, GTNHPPMaterials.CarbonFiberComposite, 4))
            .duration(200).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.latheRecipes);

        // Plate → dust (macerator)
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.CarbonFiberComposite.getPlates(1))
            .itemOutputs(GTNHPPMaterials.CarbonFiberComposite.getDust(2), GTNHPPMaterials.CarbonFiberComposite.getDustSmall(1))
            .duration(400).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.maceratorRecipes);
    }

    private static void addAerogelForms() {
        // Plate → dust (macerator — extremely fragile)
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.SilicaAerogel.getPlates(1))
            .itemOutputs(GTNHPPMaterials.SilicaAerogel.getDust(2), GTNHPPMaterials.SilicaAerogel.getDustSmall(2))
            .duration(400).eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.maceratorRecipes);
    }

    // =========================================================================
    // NYLON-6,6 CHAIN — ZPM tier
    // =========================================================================

    private static void addNylon66Chain() {
        // Step 1: Benzene + HNO3 → Adipic Acid + water
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(Materials.Benzene.getFluid(1000), Materials.NitricAcid.getFluid(3000))
            .fluidOutputs(Materials.Water.getFluid(1000))
            .itemOutputs(AdipicAcid.get(4))
            .duration(400).eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        // Step 2: Adipic Acid + NH3 → Adiponitrile + water
        GTValues.RA.stdBuilder()
            .itemInputs(AdipicAcid.get(2))
            .fluidInputs(Materials.Ammonia.getFluid(2000))
            .fluidOutputs(Materials.Water.getFluid(2000))
            .itemOutputs(Adiponitrile.get(2))
            .duration(400).eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        // Step 3: Adiponitrile + H2 → HMD (hydrogenation)
        GTValues.RA.stdBuilder()
            .itemInputs(Adiponitrile.get(1))
            .fluidInputs(Materials.Hydrogen.getGas(4000))
            .itemOutputs(HMD.get(1))
            .duration(300).eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        // Step 4 (PCV): Adipic Acid + HMD → Nylon-6,6 ingot (polycondensation)
        GTValues.RA.stdBuilder()
            .itemInputs(AdipicAcid.get(4), HMD.get(4))
            .fluidOutputs(Materials.Water.getFluid(4000))
            .itemOutputs(GTNHPPMaterials.Nylon66.getIngots(8))
            .duration(1600).eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);
    }

    // =========================================================================
    // PLA CHAIN — ZPM tier
    // =========================================================================

    private static void addPLAChain() {
        // Step 1: Ethanol + H2SO4 → Lactic Acid
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Ethanol.getFluid(1000), Materials.SulfuricAcid.getFluid(100))
            .itemOutputs(LacticAcid.get(2))
            .fluidOutputs(Materials.Water.getFluid(500))
            .duration(400).eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        // Step 2 (PCV): Lactic Acid → Lactide + water (vacuum dehydration)
        GTValues.RA.stdBuilder()
            .itemInputs(LacticAcid.get(4))
            .fluidOutputs(Materials.Water.getFluid(2000))
            .itemOutputs(Lactide.get(2))
            .duration(800).eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);

        // Step 3 (PCV): Lactide + Sn catalyst → PLA ingot (ring-opening polymerization)
        GTValues.RA.stdBuilder()
            .itemInputs(Lactide.get(4), Materials.Tin.getDust(1))
            .itemOutputs(GTNHPPMaterials.PolylacticAcid.getIngots(4))
            .duration(1600).eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);
    }

    // =========================================================================
    // KAPTON CHAIN — UV tier
    // =========================================================================

    private static void addKaptonChain() {
        // Step 1: Carbon + O2 + HNO3 → PMDA
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Carbon.getDust(3))
            .fluidInputs(Materials.Oxygen.getGas(6000), Materials.NitricAcid.getFluid(500))
            .fluidOutputs(Materials.Water.getFluid(2000))
            .itemOutputs(PMDA.get(2))
            .duration(600).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        // Step 2: Benzene + HNO3 → ODA (nitration then reduction)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(3))
            .fluidInputs(Materials.Benzene.getFluid(2000), Materials.NitricAcid.getFluid(1000))
            .itemOutputs(ODA.get(1))
            .fluidOutputs(Materials.Water.getFluid(1000))
            .duration(600).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        // Step 3 (PFC Casting): PMDA + ODA + ethanol → PAA green film
        GTValues.RA.stdBuilder()
            .itemInputs(PMDA.get(2), ODA.get(2))
            .fluidInputs(Materials.Ethanol.getFluid(500))
            .itemOutputs(PolyamicAcidFilm.get(4))
            .duration(800).eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sPFCCastingRecipes);

        // Step 4 (PFC Imidization): PAA film → Kapton plate + water (thermal cure 350°C)
        GTValues.RA.stdBuilder()
            .itemInputs(PolyamicAcidFilm.get(2))
            .fluidOutputs(Materials.Water.getFluid(1000))
            .itemOutputs(GTNHPPMaterials.Kapton.getPlates(2))
            .duration(1200).eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sPFCImidizationRecipes);
    }

    // =========================================================================
    // SILICON CARBIDE CHAIN — UV tier
    // =========================================================================

    private static void addSiCChain() {
        // Acheson process (HTRF only — requires 2200°C+)
        GTValues.RA.stdBuilder()
            .itemInputs(
                Materials.SiliconDioxide.getDust(3),
                Materials.Carbon.getDust(3),
                GTUtility.getIntegratedCircuit(1))
            .fluidInputs(Materials.Argon.getGas(1000))
            .fluidOutputs(Materials.CarbonMonoxide.getGas(2000))
            .itemOutputs(GTNHPPMaterials.SiliconCarbide.getDust(2))
            .duration(1200).eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);

        // Acid wash: SiC + HF + H2SO4 → purified SiC
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.SiliconCarbide.getDust(4))
            .fluidInputs(
                Materials.HydrofluoricAcid.getFluid(500),
                Materials.SulfuricAcid.getFluid(500))
            .fluidOutputs(Materials.Water.getFluid(800))
            .itemOutputs(GTNHPPMaterials.SiliconCarbide.getDust(4))
            .duration(600).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        // SiC ceramic casing (for HTRF construction)
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.SiliconCarbide.getDust(9), GTUtility.getIntegratedCircuit(9))
            .fluidInputs(Materials.Argon.getGas(1000))
            .itemOutputs(
                new ItemStack(
                    com.gtnh.processingplus.blocks.GTNHPPBlocks.CASINGS,
                    1,
                    com.gtnh.processingplus.blocks.BlockGTNHPPCasings.HTRF_CASING))
            .duration(1600).eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // =========================================================================
    // HEXAGONAL BORON NITRIDE CHAIN — UHV tier
    // =========================================================================

    private static void addHBNChain() {
        // Step 1: Borax + H2SO4 → B2O3 + water
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Borax.getDust(2))
            .fluidInputs(Materials.SulfuricAcid.getFluid(1000))
            .fluidOutputs(Materials.Water.getFluid(2000))
            .itemOutputs(BoronTrioxide.get(1))
            .duration(600).eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        // Step 2: B2O3 + Carbon → B4C + CO (HTRF)
        GTValues.RA.stdBuilder()
            .itemInputs(BoronTrioxide.get(2), Materials.Carbon.getDust(5))
            .fluidOutputs(Materials.CarbonMonoxide.getGas(4000))
            .itemOutputs(BoronCarbide.get(1))
            .duration(1600).eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);

        // Step 3 (AAR): B4C + NH3 → crude hBN + CO (nitriding)
        GTValues.RA.stdBuilder()
            .itemInputs(BoronCarbide.get(1))
            .fluidInputs(Materials.Ammonia.getFluid(3000))
            .fluidOutputs(Materials.CarbonMonoxide.getGas(3000))
            .itemOutputs(CrudeHBN.get(3))
            .duration(2400).eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sAARRecipes);

        // Step 4 (HPSF): crude hBN + sintering aid → hBN plate
        GTValues.RA.stdBuilder()
            .itemInputs(CrudeHBN.get(4), Materials.Yttrium.getDust(1))
            .fluidInputs(Materials.Nitrogen.getGas(1000))
            .itemOutputs(GTNHPPMaterials.HexagonalBoronNitride.getPlates(2))
            .duration(2400).eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);

        // hBN Ceramic Block (for CRV inner lining)
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPMaterials.HexagonalBoronNitride.getDust(9), GTUtility.getIntegratedCircuit(8))
            .fluidInputs(Materials.Nitrogen.getGas(2000))
            .itemOutputs(
                new ItemStack(
                    com.gtnh.processingplus.blocks.GTNHPPBlocks.CASINGS,
                    1,
                    com.gtnh.processingplus.blocks.BlockGTNHPPCasings.HBN_CERAMIC_BLOCK))
            .duration(3200).eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // =========================================================================
    // CARBON FIBER COMPOSITE CHAIN — UV tier
    // =========================================================================

    private static void addCFCChain() {
        // Step 1: Propene + NH3 + O2 → Acrylonitrile + water (ammoxidation)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(
                Materials.Propene.getFluid(1000),
                Materials.Ammonia.getFluid(1000),
                Materials.Oxygen.getGas(3000))
            .fluidOutputs(Materials.Water.getFluid(3000))
            .itemOutputs(Acrylonitrile.get(2))
            .duration(400).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        // Step 2: Acrylonitrile polymerization → PAN fiber
        GTValues.RA.stdBuilder()
            .itemInputs(Acrylonitrile.get(4))
            .fluidInputs(Materials.Ethanol.getFluid(500))
            .itemOutputs(PANFiber.get(4))
            .duration(800).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        // Step 3 (DAF Oxidizing, 250°C): PAN fiber → stabilized PAN
        GTValues.RA.stdBuilder()
            .itemInputs(PANFiber.get(4))
            .fluidInputs(Materials.Oxygen.getGas(2000))
            .itemOutputs(StabilizedPANFiber.get(4))
            .duration(1600).eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sDAFOxidizingRecipes);

        // Step 4 (DAF Inert, 1200°C): stabilized PAN → carbon fiber tow
        GTValues.RA.stdBuilder()
            .itemInputs(StabilizedPANFiber.get(4))
            .fluidInputs(Materials.Nitrogen.getGas(2000))
            .fluidOutputs(Materials.CarbonMonoxide.getGas(500))
            .itemOutputs(CarbonFiberTow.get(3))
            .duration(2000).eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sDAFInertRecipes);

        // Step 5 (Assembler): CF tow + epoxy under pressure → CF composite plate
        GTValues.RA.stdBuilder()
            .itemInputs(CarbonFiberTow.get(4))
            .fluidInputs(Materials.Epoxid.getFluid(576))
            .itemOutputs(GTNHPPMaterials.CarbonFiberComposite.getPlates(4))
            .duration(1200).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // =========================================================================
    // SILICA AEROGEL CHAIN — UHV tier
    // =========================================================================

    private static void addAerogelChain() {
        // Step 1: SiO2 + HF + ethanol → wet silica gel (sol-gel)
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.SiliconDioxide.getDust(3))
            .fluidInputs(
                Materials.HydrofluoricAcid.getFluid(250),
                Materials.Ethanol.getFluid(1000))
            .itemOutputs(WetSilicaGel.get(2))
            .duration(800).eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        // Step 2 (SCD): wet silica gel → aerogel plate (supercritical drying)
        GTValues.RA.stdBuilder()
            .itemInputs(WetSilicaGel.get(4))
            .fluidInputs(Materials.Ethanol.getFluid(4000))
            .fluidOutputs(Materials.Ethanol.getFluid(3800))
            .itemOutputs(GTNHPPMaterials.SilicaAerogel.getPlates(2))
            .duration(3200).eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sSCDRecipes);
    }
}
