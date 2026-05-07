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
