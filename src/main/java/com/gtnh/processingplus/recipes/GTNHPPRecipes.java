package com.gtnh.processingplus.recipes;

import static com.gtnh.processingplus.items.GTNHPPItemList.*;
import static gregtech.api.util.GTRecipeConstants.CHEMPLANT_CASING_TIER;

import gregtech.api.util.GTRecipeConstants;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import net.minecraft.item.ItemStack;
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

    // =========================================================================
    // NYLON-6,6 CHAIN — ZPM tier
    // =========================================================================

    private static void addNylon66Chain() {

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

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
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
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

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

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(
                gtppFluid("cyclohexene", 1000),
                gtppFluid("fluid.hydrogenperoxide", 3000)
            )
            .fluidOutputs(
                GTNHPPMaterials.AdipicAcid.getFluid(1000),
                Materials.Water.getFluid(3000)
            )
            .duration(300)
            .eut(TierEU.RECIPE_LuV)
            .metadata(CHEMPLANT_CASING_TIER, 6)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

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

        GTValues.RA.stdBuilder()
            .fluidInputs(new FluidStack[]{
                Materials.Ammonia.getFluid(2000),
                gtppFluid("adipicacid", 4000)
            })
            .fluidOutputs(Materials.Water.getFluid(2000))
            .itemOutputs(Adiponitrile.get(2))
            .duration(400).eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sAARRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(Adiponitrile.get(1))
            .fluidInputs(Materials.Hydrogen.getGas(4000))
            .itemOutputs(HMD.get(1))
            .duration(300).eut(TierEU.RECIPE_ZPM)
            .addTo(GTRecipeConstants.UniversalChemical);

        GTValues.RA.stdBuilder()
            .itemInputs(HMD.get(4))
            .fluidInputs(gtppFluid("adipicacid", 4000))
            .fluidOutputs(Materials.Water.getFluid(4000))
            .itemOutputs(mat(OrePrefixes.ingot, GTNHPPMaterials.Nylon66, 8))
            .duration(1600).eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);
    }

    // =========================================================================
    // PLA CHAIN — LuV→ZPM tiers
    // =========================================================================

    private static void addPLAChain() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(new FluidStack[]{
                Materials.Ethanol.getFluid(1000),
                Materials.SulfuricAcid.getFluid(100)
            })
            .itemOutputs(LacticAcid.get(2))
            .fluidOutputs(Materials.Water.getFluid(500))
            .duration(400).eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(LacticAcid.get(4))
            .fluidOutputs(Materials.Water.getFluid(2000))
            .itemOutputs(Lactide.get(2))
            .duration(800).eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);

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

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(new FluidStack[]{
                Materials.Benzene.getFluid(2000),
                Materials.NitricAcid.getFluid(1000)
            })
            .itemOutputs(ODA.get(1))
            .fluidOutputs(Materials.Water.getFluid(1000))
            .duration(600).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================================
    // SILICON CARBIDE CHAIN — ZPM→UV tiers
    // =========================================================================

    private static void addSiCChain() {

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
    }

    // =========================================================================
    // HEXAGONAL BORON NITRIDE CHAIN — ZPM→UHV tiers
    // =========================================================================

    private static void addHBNChain() {

        GTValues.RA.stdBuilder()
            .itemInputs(mat(OrePrefixes.dust, Materials.Borax, 2))
            .fluidInputs(Materials.SulfuricAcid.getFluid(1000))
            .fluidOutputs(Materials.Water.getFluid(2000))
            .itemOutputs(BoronTrioxide.get(1))
            .duration(600).eut(TierEU.RECIPE_ZPM)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================================
    // CARBON FIBER COMPOSITE CHAIN — LuV→UV tiers
    // =========================================================================

    private static void addCFCChain() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(5))
            .itemOutputs(Acrylonitrile.get(2))
            .fluidInputs(
                Materials.Propene.getGas(3000),
                Materials.Ammonia.getGas(3000),
                Materials.Oxygen.getGas(1000)
            )
            .fluidOutputs(Materials.Water.getFluid(3000))
            .duration(400).eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================================
    // SILICA AEROGEL CHAIN — UV→UHV tiers
    // =========================================================================

    private static void addAerogelChain() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(6))
            .fluidInputs(
                Materials.SiliconTetrachloride.getFluid(1000),
                Materials.Ethanol.getFluid(4000)
            )
            .fluidOutputs(
                GTNHPPMaterials.TEOS.getFluid(1000),
                Materials.HydrochloricAcid.getFluid(4000)
            )
            .duration(400).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }
}
