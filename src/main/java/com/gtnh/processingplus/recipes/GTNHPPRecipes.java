package com.gtnh.processingplus.recipes;

import static com.gtnh.processingplus.items.GTNHPPItemList.*;
import static gregtech.api.util.GTRecipeConstants.CHEMPLANT_CASING_TIER;

import gregtech.api.util.GTRecipeConstants;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.item.chemistry.GenericChem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

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

    private static FluidStack gtppFluid(String name, int amount) {
        FluidStack fs = FluidRegistry.getFluidStack(name, amount);
        if (fs == null) throw new IllegalStateException("GT++ fluid not found: " + name);
        return fs;
    }

    // =========================================================================
    // NYLON-6,6 CHAIN — ZPM tier
    // =========================================================================

    private static void addNylon66Chain() {

        /*
         * ==============================
         * 1. PHENOL → CYCLOHEXANOL
         * Hydrogenation (Catalytic)
         * ==============================
         */

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, "catalystCobaltTitanium", 0)
            )
            .fluidInputs(
                Materials.Phenol.getFluid(1000),
                Materials.Hydrogen.getGas(3000)
            )
            .fluidOutputs(
                GTNHPPMaterials.Cyclohexanol.getFluid(1000)
            )
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .metadata(CHEMPLANT_CASING_TIER, 3)
            .addTo(GTPPRecipeMaps.chemicalPlantRecipes);

        /*
         * ==============================
         * 2. KA OIL FORMATION
         * Cyclohexanol ⇌ Cyclohexanone mix
         * (partial oxidation equilibrium)
         * ==============================
         */

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2)
            )
            .fluidInputs(
                GTNHPPMaterials.Cyclohexanol.getFluid(1000),
                Materials.Oxygen.getGas(1000)
            )
            .fluidOutputs(
                gtppFluid("cyclohexanone", 5000),
                GTNHPPMaterials.Cyclohexanol.getFluid(500)
            )
            .duration(120)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTRecipeConstants.UniversalChemical);

        /*
         * ==============================
         * 3. KA OIL → ADIPIC ACID
         * Nitric acid oxidation (CORE PROCESS)
         * ==============================
         */

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, "catalystCobaltTitanium", 0)
            )
            .fluidInputs(
                GTNHPPMaterials.Cyclohexanol.getFluid(1000),
                gtppFluid("cyclohexanone", 1000),
                Materials.NitricAcid.getFluid(4000)
            )
            .fluidOutputs(
                GTNHPPMaterials.AdipicAcid.getFluid(2000),
                Materials.NitrousOxide.getGas(2000),
                Materials.Water.getFluid(2000)
            )
            .duration(350)
            .eut(TierEU.RECIPE_UV)
            .metadata(CHEMPLANT_CASING_TIER, 6)
            .addTo(GTPPRecipeMaps.chemicalPlantRecipes);

        /*
         * ==============================
         * 4. GREEN ROUTE (optional)
         * Cyclohexene oxidation
         * ==============================
         */

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4)
            )
            .fluidInputs(new FluidStack[]{
                gtppFluid("cyclohexene", 1000),
                gtppFluid("fluid.hydrogenperoxide", 3000)}
            )
            .fluidOutputs(
                GTNHPPMaterials.AdipicAcid.getFluid(1000),
                Materials.Water.getFluid(3000)
            )
            .duration(300)
            .eut(TierEU.RECIPE_LuV)
            .metadata(CHEMPLANT_CASING_TIER, 6)
            .addTo(GTRecipeConstants.UniversalChemical);

        /*
         * ==============================
         * 5. ADVANCED ROUTE (butadiene)
         * Petrochemical synthesis
         * ==============================
         */

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(5),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, "catalystCobaltTitanium", 0)
            )
            .fluidInputs(
                Materials.Butene.getGas(2000),
                Materials.CarbonMonoxide.getGas(2000),
                Materials.Water.getFluid(2000)
            )
            .fluidOutputs(
                GTNHPPMaterials.AdipicAcid.getFluid(1000)
            )
            .duration(500)
            .eut(TierEU.RECIPE_UV)
            .metadata(CHEMPLANT_CASING_TIER, 7)
            .addTo(GTPPRecipeMaps.chemicalPlantRecipes);

        // Step 2: Adipic Acid + NH3 → Adiponitrile + water  (LCR ZPM)
        GTValues.RA.stdBuilder()
            .fluidInputs(new FluidStack[]{
                Materials.Ammonia.getFluid(2000),
                gtppFluid("adipicacid", 4000)}

            )
            .fluidOutputs(Materials.Water.getFluid(2000))
            .itemOutputs(Adiponitrile.get(2))
            .duration(400).eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sAARRecipes);

        // Step 3: Adiponitrile + H2 → HMD  (LCR ZPM, hydrogenation)
        GTValues.RA.stdBuilder()
            .itemInputs(Adiponitrile.get(1))
            .fluidInputs(Materials.Hydrogen.getGas(4000))
            .itemOutputs(HMD.get(1))
            .duration(300).eut(TierEU.RECIPE_ZPM)
            .addTo(GTRecipeConstants.UniversalChemical);

        // Step 4 (PCV): Adipic Acid + HMD → Nylon-6,6 + water  (polycondensation)
        GTValues.RA.stdBuilder()
            .itemInputs(
                HMD.get(4))
            .fluidInputs(
                gtppFluid("adipicacid", 4000)
            )
            .fluidOutputs(Materials.Water.getFluid(4000))
            .itemOutputs(mat(OrePrefixes.ingot, GTNHPPMaterials.Nylon66, 8))
            .duration(1600).eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);
    }

    // =========================================================================
    // PLA CHAIN — LuV→ZPM tiers
    // =========================================================================

    private static void addPLAChain() {
        // Step 1: Ethanol + H2SO4 → Lactic Acid  (LCR LuV, fermentation shortcut)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(new FluidStack[]{
                Materials.Ethanol.getFluid(1000),
                Materials.SulfuricAcid.getFluid(100)})
            .itemOutputs(LacticAcid.get(2))
            .fluidOutputs(Materials.Water.getFluid(500))
            .duration(400).eut(TierEU.RECIPE_LuV)
            .addTo(GTRecipeConstants.UniversalChemical);

        // Step 2 (PCV): Lactic Acid → Lactide + water  (vacuum dehydration, ZPM)
        GTValues.RA.stdBuilder()
            .itemInputs(LacticAcid.get(4))
            .fluidOutputs(Materials.Water.getFluid(2000))
            .itemOutputs(Lactide.get(2))
            .duration(800).eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);

        // Step 3 (PCV): Lactide + Sn catalyst → PLA ingot  (ring-opening polymerization, ZPM)
        GTValues.RA.stdBuilder()
            .itemInputs(Lactide.get(4), Materials.Tin.getDust(1))
            .itemOutputs(mat(OrePrefixes.ingot, GTNHPPMaterials.PolylacticAcid, 4))
            .duration(1600).eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);
    }

    // =========================================================================
    // KAPTON CHAIN — LuV→UV tiers
    // =========================================================================

    private static void addKaptonChain() {
        // Step 1: PhthalicAcid + O2 → PMDA + CO2 + water  (LCR LuV, oxidative dehydration)
        GTValues.RA.stdBuilder()
            .itemInputs(
                mat(OrePrefixes.dust, Materials.PhthalicAcid, 2),
                GTUtility.getIntegratedCircuit(3))
            .fluidInputs(Materials.Oxygen.getGas(6000))
            .fluidOutputs(
                Materials.CarbonDioxide.getGas(2000),
                Materials.Water.getFluid(1000))
            .itemOutputs(PMDA.get(2))
            .duration(600).eut(TierEU.RECIPE_LuV)
            .addTo(GTRecipeConstants.UniversalChemical);

        // Step 2: Benzene + HNO3 → ODA  (LCR UV, nitration + reduction)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(new FluidStack[]{
                Materials.Benzene.getFluid(2000),
                Materials.NitricAcid.getFluid(1000)})
            .itemOutputs(ODA.get(1))
            .fluidOutputs(Materials.Water.getFluid(1000))
            .duration(600).eut(TierEU.RECIPE_UV)
            .addTo(GTRecipeConstants.UniversalChemical);

        // Step 3: PMDA + ODA + NMP → PAA Solution  (LCR UV, polycondensation in solvent)
        GTValues.RA.stdBuilder()
            .itemInputs(PMDA.get(2), ODA.get(2))
            .fluidInputs(Materials.NMethylIIPyrrolidone.getFluid(1000))
            .fluidOutputs(GTNHPPMaterials.PAASolution.getFluid(2000))
            .duration(800).eut(TierEU.RECIPE_UV)
            .addTo(GTRecipeConstants.UniversalChemical);

        // Step 4: PAA Solution → Concentrated PAA + recovered NMP  (distillation, UV)
        GTValues.RA.stdBuilder()
            .fluidInputs(GTNHPPMaterials.PAASolution.getFluid(2000))
            .itemOutputs(ConcentratedPAA.get(2))
            .fluidOutputs(Materials.NMethylIIPyrrolidone.getFluid(800))
            .duration(600).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.distillationTowerRecipes);

        // Step 5 (PFC Casting): Concentrated PAA + ethanol → PAA Film  (UV)
        GTValues.RA.stdBuilder()
            .itemInputs(ConcentratedPAA.get(2))
            .fluidInputs(Materials.Ethanol.getFluid(200))
            .itemOutputs(PolyamicAcidFilm.get(4))
            .duration(800).eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sPFCCastingRecipes);

        // Step 6 (PFC Imidization): PAA Film → Kapton plate + water  (thermal cure 350°C, UV)
        GTValues.RA.stdBuilder()
            .itemInputs(PolyamicAcidFilm.get(2))
            .fluidOutputs(Materials.Water.getFluid(1000))
            .itemOutputs(mat(OrePrefixes.plate, GTNHPPMaterials.Kapton, 2))
            .duration(1200).eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sPFCImidizationRecipes);
    }

    // =========================================================================
    // SILICON CARBIDE CHAIN — ZPM→UV tiers
    // =========================================================================

    private static void addSiCChain() {
        // Step 1 (HTRF): SiO2 + C → Crude SiC Powder + CO  (Acheson process, UV 2200°C+)
        GTValues.RA.stdBuilder()
            .itemInputs(
                mat(OrePrefixes.dust, Materials.SiliconDioxide, 3),
                mat(OrePrefixes.dust, Materials.Carbon, 3),
                GTUtility.getIntegratedCircuit(1))
            .fluidInputs(Materials.Argon.getGas(1000))
            .fluidOutputs(Materials.CarbonMonoxide.getGas(2000))
            .itemOutputs(CrudeSiCPowder.get(2))
            .duration(1200).eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);

        // Step 2 (LCR): Crude SiC + HF + H2SO4 → Purified SiC + water  (acid wash, ZPM)
        GTValues.RA.stdBuilder()
            .itemInputs(CrudeSiCPowder.get(4))
            .fluidInputs(new FluidStack[]{
                Materials.HydrofluoricAcid.getFluid(500),
                Materials.SulfuricAcid.getFluid(500)})
            .fluidOutputs(Materials.Water.getFluid(800))
            .itemOutputs(PurifiedSiCPowder.get(4))
            .duration(600).eut(TierEU.RECIPE_ZPM)
            .addTo(GTRecipeConstants.UniversalChemical);

        // Step 3 (HPSF): Purified SiC + sintering aid → Dense SiC Compact  (hot-press, UV)
        GTValues.RA.stdBuilder()
            .itemInputs(PurifiedSiCPowder.get(4), mat(OrePrefixes.dust, Materials.Boron, 1))
            .fluidInputs(Materials.Argon.getGas(500))
            .itemOutputs(DenseSiCCompact.get(2))
            .duration(1600).eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);

        // Step 4 (Lathe): Dense SiC Compact → SiC plates  (machining)
        GTValues.RA.stdBuilder()
            .itemInputs(DenseSiCCompact.get(1))
            .itemOutputs(
                mat(OrePrefixes.plate, GTNHPPMaterials.SinteredSiliconCarbide, 2),
                mat(OrePrefixes.dustSmall, GTNHPPMaterials.SinteredSiliconCarbide, 2))
            .duration(800).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.latheRecipes);

        // SiC ceramic casing (for HTRF construction) — HPSF
        GTValues.RA.stdBuilder()
            .itemInputs(
                mat(OrePrefixes.dust, GTNHPPMaterials.SinteredSiliconCarbide, 9),
                GTUtility.getIntegratedCircuit(9))
            .fluidInputs(Materials.Argon.getGas(1000))
            .itemOutputs(new ItemStack(
                com.gtnh.processingplus.blocks.GTNHPPBlocks.CASINGS,
                1,
                com.gtnh.processingplus.blocks.BlockGTNHPPCasings.HTRF_CASING))
            .duration(1600).eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // =========================================================================
    // HEXAGONAL BORON NITRIDE CHAIN — ZPM→UHV tiers
    // =========================================================================

    private static void addHBNChain() {
        // Step 1: Borax + H2SO4 → B2O3 + water  (LCR ZPM)
        GTValues.RA.stdBuilder()
            .itemInputs(mat(OrePrefixes.dust, Materials.Borax, 2))
            .fluidInputs(Materials.SulfuricAcid.getFluid(1000))
            .fluidOutputs(Materials.Water.getFluid(2000))
            .itemOutputs(BoronTrioxide.get(1))
            .duration(600).eut(TierEU.RECIPE_ZPM)
            .addTo(GTRecipeConstants.UniversalChemical);

        // Step 2 (HTRF): B2O3 + C → B4C + CO  (carbothermal reduction, UV)
        GTValues.RA.stdBuilder()
            .itemInputs(
                BoronTrioxide.get(2),
                mat(OrePrefixes.dust, Materials.Carbon, 5))
            .fluidOutputs(Materials.CarbonMonoxide.getGas(4000))
            .itemOutputs(BoronCarbide.get(1))
            .duration(1600).eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);

        // Step 3 (AAR): B4C + NH3 → Crude hBN + CO  (nitriding, UHV)
        GTValues.RA.stdBuilder()
            .itemInputs(BoronCarbide.get(1))
            .fluidInputs(Materials.Ammonia.getFluid(3000))
            .fluidOutputs(Materials.CarbonMonoxide.getGas(3000))
            .itemOutputs(CrudeHBN.get(3))
            .duration(2400).eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sAARRecipes);

        // Step 4: Crude hBN + Yttrium → hBN Powder Blend  (blending for sinter aid, UHV LCR)
        GTValues.RA.stdBuilder()
            .itemInputs(
                CrudeHBN.get(4),
                mat(OrePrefixes.dust, Materials.Yttrium, 1))
            .fluidInputs(Materials.Nitrogen.getGas(500))
            .itemOutputs(HBNPowderBlend.get(4))
            .duration(800).eut(TierEU.RECIPE_UHV)
            .addTo(GTRecipeConstants.UniversalChemical);

        // Step 5 (HPSF): hBN Powder Blend → Dense hBN Ceramic  (hot-press sintering, UHV)
        GTValues.RA.stdBuilder()
            .itemInputs(HBNPowderBlend.get(4))
            .fluidInputs(Materials.Nitrogen.getGas(1000))
            .itemOutputs(DenseHBNCeramic.get(2))
            .duration(2400).eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);

        // Step 6 (Lathe): Dense hBN Ceramic → hBN plates
        GTValues.RA.stdBuilder()
            .itemInputs(DenseHBNCeramic.get(1))
            .itemOutputs(
                mat(OrePrefixes.plate, GTNHPPMaterials.HexagonalBoronNitride, 2),
                mat(OrePrefixes.dustSmall, GTNHPPMaterials.HexagonalBoronNitride, 2))
            .duration(1000).eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.latheRecipes);

        // hBN Ceramic Block (for CRV inner lining) — HPSF
        GTValues.RA.stdBuilder()
            .itemInputs(
                mat(OrePrefixes.dust, GTNHPPMaterials.HexagonalBoronNitride, 9),
                GTUtility.getIntegratedCircuit(8))
            .fluidInputs(Materials.Nitrogen.getGas(2000))
            .itemOutputs(new ItemStack(
                com.gtnh.processingplus.blocks.GTNHPPBlocks.CASINGS,
                1,
                com.gtnh.processingplus.blocks.BlockGTNHPPCasings.HBN_CERAMIC_BLOCK))
            .duration(3200).eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // =========================================================================
    // CARBON FIBER COMPOSITE CHAIN — LuV→UV tiers
    // =========================================================================gt

    private static void addCFCChain() {
        // Step 1: Propylene + NH3 + O2 → Acrylonitrile + water  (LCR LuV, ammoxidation)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(5))
            .fluidInputs(gtppFluid("propene", 3000))
            .fluidInputs(gtppFluid("ammonia", 1000))
            .fluidInputs(gtppFluid("oxygen", 3000))
            .fluidOutputs(Materials.Water.getFluid(3000))
            .itemOutputs(Acrylonitrile.get(2))
            .duration(400).eut(TierEU.RECIPE_LuV)
            .addTo(GTRecipeConstants.UniversalChemical);

        // Step 2: Acrylonitrile + NMP → PAN Fiber  (LCR UV, solution polymerization)
        GTValues.RA.stdBuilder()
            .itemInputs(Acrylonitrile.get(4))
            .fluidInputs(Materials.NMethylIIPyrrolidone.getFluid(500))
            .itemOutputs(PANFiber.get(4))
            .duration(800).eut(TierEU.RECIPE_UV)
            .addTo(GTRecipeConstants.UniversalChemical);

        // Step 3 (DAF Oxidizing, 250°C): PAN Fiber + O2 → Stabilized PAN  (UV)
        GTValues.RA.stdBuilder()
            .itemInputs(PANFiber.get(4))
            .fluidInputs(Materials.Oxygen.getGas(2000))
            .itemOutputs(StabilizedPANFiber.get(4))
            .duration(1600).eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sDAFOxidizingRecipes);

        // Step 4 (DAF Inert, 1200°C): Stabilized PAN + N2 → Carbon Fiber Tow + CO  (UV)
        GTValues.RA.stdBuilder()
            .itemInputs(StabilizedPANFiber.get(4))
            .fluidInputs(Materials.Nitrogen.getGas(2000))
            .fluidOutputs(Materials.CarbonMonoxide.getGas(500))
            .itemOutputs(CarbonFiberTow.get(3))
            .duration(2000).eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sDAFInertRecipes);

        // Step 5 (Assembler): CF Tow + epoxy → CFC plate  (UV)
        GTValues.RA.stdBuilder()
            .itemInputs(CarbonFiberTow.get(4))
            .fluidInputs(Materials.Epoxid.getFluid(576))
            .itemOutputs(mat(OrePrefixes.plate, GTNHPPMaterials.CarbonFiberComposite, 4))
            .duration(1200).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // =========================================================================
    // SILICA AEROGEL CHAIN — UV→UHV tiers
    // =========================================================================

    private static void addAerogelChain() {
        // Step 1: SiCl4 + ethanol → TEOS + HCl  (LCR UV, alkoxide synthesis)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(6))
            .fluidInputs(
                Materials.SiliconTetrachloride.getFluid(1000),
                Materials.Ethanol.getFluid(4000))
            .fluidOutputs(
                GTNHPPMaterials.TEOS.getFluid(1000),
                Materials.HydrochloricAcid.getFluid(4000))
            .duration(400).eut(TierEU.RECIPE_UV)
            .addTo(GTRecipeConstants.UniversalChemical);

        // Step 2 (LCR UHV): TEOS + water + HF → Wet Silica Gel  (sol-gel hydrolysis)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(7))
            .fluidInputs(GTNHPPMaterials.TEOS.getFluid(1000))
            .fluidInputs(Materials.Water.getFluid(2000))
            .fluidInputs(Materials.HydrofluoricAcid.getFluid(1000))
            .itemOutputs(WetSilicaGel.get(2))
            .duration(800).eut(TierEU.RECIPE_UHV)
            .addTo(GTRecipeConstants.UniversalChemical);

        // Step 3: Wet Silica Gel → Aged Silica Gel  (aging in sealed vessel, SCD UHV)
        GTValues.RA.stdBuilder()
            .itemInputs(WetSilicaGel.get(4))
            .fluidInputs(Materials.Water.getFluid(1000))
            .itemOutputs(AgedSilicaGel.get(4))
            .duration(1600).eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sSCDRecipes);

        // Step 4: Aged Silica Gel + ethanol → Ethanol-Saturated Gel  (solvent exchange, SCD UHV)
        GTValues.RA.stdBuilder()
            .itemInputs(AgedSilicaGel.get(4))
            .fluidInputs(Materials.Ethanol.getFluid(4000))
            .fluidOutputs(Materials.Water.getFluid(3000))
            .itemOutputs(EthanolSaturatedGel.get(4))
            .duration(2400).eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sSCDRecipes);

        // Step 5 (SCD): Ethanol-Saturated Gel → Aerogel plate + ethanol  (supercritical drying, UHV)
        GTValues.RA.stdBuilder()
            .itemInputs(EthanolSaturatedGel.get(4))
            .fluidInputs(Materials.Ethanol.getFluid(2000))
            .fluidOutputs(Materials.Ethanol.getFluid(1800))
            .itemOutputs(mat(OrePrefixes.plate, GTNHPPMaterials.SilicaAerogel, 2))
            .duration(3200).eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sSCDRecipes);
    }
}
