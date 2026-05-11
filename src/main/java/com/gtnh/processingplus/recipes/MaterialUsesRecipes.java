package com.gtnh.processingplus.recipes;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;
import static gregtech.api.util.GTRecipeConstants.RESEARCH_ITEM;
import static gregtech.api.util.GTRecipeConstants.SCANNING;

import com.gtnh.processingplus.materials.PrPMaterials;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.recipe.Scanning;
import gtPlusPlus.core.material.MaterialsAlloy;

public class MaterialUsesRecipes {

    // All component recipes use the regular GT5U Assembly Line (Research Station scan required).
    // Quantities are derived from vanilla COAL batch ÷ 48 — preserving the same progression gates
    // (Americium, Naquadria, Neutronium, CosmicNeutronium) at single-unit scale.

    public static void init() {
        nylonPlates();
        nylonCableInsulation();
        nylonAssemblyLine();
        plaPlates();
        plaPCVCasing();
        kaptonPFCCasing();
        kaptonCableInsulation();
        kaptonPAAFluid();
        kaptonAssemblyLine();
        carbonFiberDAFCasing();
        carbonFiberAssemblyLine();
        sicMaceratingBonus();
        sicAssemblyLine();
        hbnLubricant();
        hbnCableInsulation();
        hbnCRVAlloys();
        hbnAssemblyLine();
        aerogelCatalystSupport();
        aerogelInsulationPanel();
    }

    // =========================================================================
    // NYLON-6,6 (ZPM) — polymer insulation + ZPM pump seals and conveyor belt
    // =========================================================================

    private static void nylonPlates() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.Nylon66, 1), ItemList.Shape_Extruder_Plate.get(0))
            .itemOutputs(plate(PrPMaterials.Nylon66, 1))
            .duration(200)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.extruderRecipes);
    }

    private static void nylonCableInsulation() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.Naquadah, 1),
                plate(PrPMaterials.Nylon66, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Naquadah, 1))
            .duration(200)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.wiremillRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.NaquadahAlloy, 1),
                plate(PrPMaterials.Nylon66, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.NaquadahAlloy, 1))
            .duration(200)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.wiremillRecipes);
    }

    // Nylon seal bodies replace rubber at ZPM process pressures; Nylon belt replaces rubber molten.
    // Scan an existing ZPM pump/conveyor to unlock. All NaquadahAlloy + Enderium costs preserved.
    private static void nylonAssemblyLine() {
        // ZPM Pump — Nylon seal bodies eliminate the rubber molten requirement
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Electric_Pump_ZPM.get(1))
            .metadata(SCANNING, new Scanning(1200, TierEU.RECIPE_LuV))
            .itemInputs(
                ItemList.Electric_Motor_ZPM.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.NaquadahAlloy, 1),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.VanadiumGallium, 1),
                plate(PrPMaterials.Nylon66, 4))
            .fluidInputs(
                MaterialsAlloy.INDALLOY_140.getFluidStack(576),
                Materials.Lubricant.getFluid(750),
                molten(Materials.NaquadahAlloy, 1296),
                molten(Materials.Enderium,       864))
            .itemOutputs(ItemList.Electric_Pump_ZPM.get(1))
            .duration(1200)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTRecipeConstants.AssemblyLine);

        // ZPM Conveyor — Nylon belt replaces the enormous rubber molten requirement
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Conveyor_Module_ZPM.get(1))
            .metadata(SCANNING, new Scanning(1200, TierEU.RECIPE_LuV))
            .itemInputs(
                ItemList.Electric_Motor_ZPM.get(2),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.NaquadahAlloy, 1),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.VanadiumGallium, 1),
                plate(PrPMaterials.Nylon66, 8))
            .fluidInputs(
                MaterialsAlloy.INDALLOY_140.getFluidStack(576),
                Materials.Lubricant.getFluid(750),
                molten(Materials.NaquadahAlloy, 576))
            .itemOutputs(ItemList.Conveyor_Module_ZPM.get(1))
            .duration(1200)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTRecipeConstants.AssemblyLine);
    }

    // =========================================================================
    // PLA (ZPM) — PCV reaction vessel lining
    // =========================================================================

    private static void plaPlates() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.PolylacticAcid, 1), ItemList.Shape_Extruder_Plate.get(0))
            .itemOutputs(plate(PrPMaterials.PolylacticAcid, 1))
            .duration(200)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.extruderRecipes);
    }

    private static void plaPCVCasing() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Casing_ZPM.get(1),
                plate(PrPMaterials.PolylacticAcid, 4),
                circuit(4))
            .fluidInputs(fluid(Materials.Water, 2000))
            .itemOutputs(new net.minecraft.item.ItemStack(
                com.gtnh.processingplus.blocks.GTNHPPBlocks.CASINGS,
                1,
                com.gtnh.processingplus.blocks.BlockGTNHPPCasings.PCV_CASING))
            .duration(400)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // =========================================================================
    // KAPTON (UV) — PFC casing, Osmiridium cable, PAA adhesive, UV motor
    // =========================================================================

    private static void kaptonPFCCasing() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Casing_UV.get(1),
                plate(PrPMaterials.Kapton, 4),
                circuit(3))
            .fluidInputs(fluid("molten.epoxid", 576))
            .itemOutputs(new net.minecraft.item.ItemStack(
                com.gtnh.processingplus.blocks.GTNHPPBlocks.CASINGS,
                1,
                com.gtnh.processingplus.blocks.BlockGTNHPPCasings.PFC_CASING))
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    private static void kaptonCableInsulation() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.Osmiridium, 1),
                plate(PrPMaterials.Kapton, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Osmiridium, 1))
            .duration(200)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.wiremillRecipes);
    }

    private static void kaptonPAAFluid() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.PolyamicAcidFilm, 2), circuit(2))
            .fluidInputs(fluid(Materials.NMethylIIPyrrolidone, 500))
            .fluidOutputs(fluid(PrPMaterials.PAAAdhesive, 1000))
            .duration(300)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // Kapton coil insulation + PAA adhesive bond layer replaces the Samarium molten requirement.
    // Americium / Naquadria / Neutronium fluid costs preserved — full UV progression gate maintained.
    private static void kaptonAssemblyLine() {
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Electric_Motor_UV.get(1))
            .metadata(SCANNING, new Scanning(2400, TierEU.RECIPE_ZPM))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.NaquadahAlloy, 1),
                plate(PrPMaterials.Kapton, 4))
            .fluidInputs(
                MaterialsAlloy.INDALLOY_140.getFluidStack(1296),
                molten(Materials.Americium, 6912),
                molten(Materials.Naquadria, 1296),
                fluid(PrPMaterials.PAAAdhesive, 288))
            .itemOutputs(ItemList.Electric_Motor_UV.get(1))
            .duration(1800)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTRecipeConstants.AssemblyLine);
    }

    // =========================================================================
    // CARBON FIBER COMPOSITE (UV) — DAF casing, UV pump
    // =========================================================================

    private static void carbonFiberDAFCasing() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                plate(PrPMaterials.CarbonFiberComposite, 4),
                plate(Materials.TungstenSteel, 2),
                circuit(1))
            .fluidInputs(fluid("molten.epoxid", 288))
            .itemOutputs(new net.minecraft.item.ItemStack(
                com.gtnh.processingplus.blocks.GTNHPPBlocks.CASINGS,
                1,
                com.gtnh.processingplus.blocks.BlockGTNHPPCasings.DAF_CASING))
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // CF composite impeller eliminates the rubber molten requirement and halves dense Neutronium plates.
    // Naquadah / Neutronium / Naquadria fluid costs preserved — full UV pump gate maintained.
    private static void carbonFiberAssemblyLine() {
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Electric_Pump_UV.get(1))
            .metadata(SCANNING, new Scanning(2400, TierEU.RECIPE_ZPM))
            .itemInputs(
                ItemList.Electric_Motor_UV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Neutronium, 1),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.NaquadahAlloy, 1),
                plate(PrPMaterials.CarbonFiberComposite, 4))
            .fluidInputs(
                MaterialsAlloy.INDALLOY_140.getFluidStack(1296),
                Materials.Lubricant.getFluid(2000),
                molten(Materials.Naquadria, 1296),
                molten(Materials.Neutronium, 976))
            .itemOutputs(ItemList.Electric_Pump_UV.get(1))
            .duration(1800)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTRecipeConstants.AssemblyLine);
    }

    // =========================================================================
    // SILICON CARBIDE (UV) — ore macerating bonus, UV piston
    // =========================================================================

    private static void sicMaceratingBonus() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Iron, 1),
                dust(PrPMaterials.SinteredSiliconCarbide, 1))
            .itemOutputs(
                dust(Materials.Iron, 2),
                dustSmall(Materials.Iron, 4),
                dustSmall(Materials.Nickel, 1))
            .duration(200).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.maceratorRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Copper, 1),
                dust(PrPMaterials.SinteredSiliconCarbide, 1))
            .itemOutputs(
                dust(Materials.Copper, 2),
                dustSmall(Materials.Copper, 4),
                dustSmall(Materials.Gold, 1))
            .duration(200).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.maceratorRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tungsten, 1),
                dust(PrPMaterials.SinteredSiliconCarbide, 1))
            .itemOutputs(
                dust(Materials.Tungsten, 2),
                dustSmall(Materials.Tungsten, 4),
                dustSmall(Materials.Molybdenum, 1))
            .duration(200).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.maceratorRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Platinum, 1),
                dust(PrPMaterials.SinteredSiliconCarbide, 1))
            .itemOutputs(
                dust(Materials.Platinum, 2),
                dustSmall(Materials.Platinum, 4),
                dustSmall(Materials.Palladium, 1))
            .duration(200).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.maceratorRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Naquadah, 1),
                dust(PrPMaterials.SinteredSiliconCarbide, 1))
            .itemOutputs(
                dust(Materials.Naquadah, 2),
                dustSmall(Materials.Naquadah, 4),
                dustSmall(Materials.NaquadahEnriched, 1))
            .duration(200).eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.maceratorRecipes);
    }

    // SiC bore liner halves the dense Neutronium plate count — ceramic handles bore wall abrasion.
    // Neutronium + Naquadria fluid costs preserved — full UV piston gate maintained.
    private static void sicAssemblyLine() {
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Electric_Piston_UV.get(1))
            .metadata(SCANNING, new Scanning(2400, TierEU.RECIPE_ZPM))
            .itemInputs(
                ItemList.Electric_Motor_UV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Neutronium, 2),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.NaquadahAlloy, 1),
                plate(PrPMaterials.SinteredSiliconCarbide, 4))
            .fluidInputs(
                MaterialsAlloy.INDALLOY_140.getFluidStack(1296),
                Materials.Lubricant.getFluid(2000),
                molten(Materials.Neutronium, 1808),
                molten(Materials.Naquadria,  1296))
            .itemOutputs(ItemList.Electric_Piston_UV.get(1))
            .duration(1800)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTRecipeConstants.AssemblyLine);
    }

    // =========================================================================
    // HBN (UHV) — lubricant, cable insulation, CRV alloys, UHV components
    // =========================================================================

    private static void hbnLubricant() {
        GTValues.RA.stdBuilder()
            .itemInputs(plate(PrPMaterials.HexagonalBoronNitride, 1), circuit(1))
            .fluidInputs(fluid(Materials.Lubricant, 4000))
            .fluidOutputs(fluid(PrPMaterials.HBNLubricant, 4000))
            .duration(400)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    private static void hbnCableInsulation() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.Naquadah, 1),
                plate(PrPMaterials.HexagonalBoronNitride, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Naquadah, 1))
            .duration(200)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.wiremillRecipes);
    }

    private static void hbnCRVAlloys() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                dust(Materials.Naquadah, 4),
                dust(Materials.Osmiridium, 1),
                dust(PrPMaterials.SinteredSiliconCarbide, 1),
                circuit(2))
            .fluidInputs(fluid(Materials.Argon, 4000), fluid(PrPMaterials.HBNLubricant, 1000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.NaquadahAlloy, 4))
            .duration(1200)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sCRVRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                dust(Materials.Naquadah, 8),
                dust(PrPMaterials.LoadedAerogelCatalystSupport, 2),
                circuit(3))
            .fluidInputs(fluid(Materials.Nitrogen, 8000), fluid(PrPMaterials.HBNLubricant, 2000))
            .itemOutputs(
                dust(Materials.NaquadahEnriched, 2),
                dust(Materials.Naquadah, 4),
                dust(PrPMaterials.LoadedAerogelCatalystSupport, 1))
            .duration(2000)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sCRVRecipes);
    }

    // hBN ceramic bearings replace Lubricant with HBNLubricant; removes Samarium requirement.
    // Neutronium (64 ingots) + CosmicNeutronium preserved — full UHV progression gate maintained.
    private static void hbnAssemblyLine() {
        // UHV Motor — HBNLubricant replaces Lubricant; Samarium removed
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Electric_Motor_UHV.get(1))
            .metadata(SCANNING, new Scanning(3600, TierEU.RECIPE_UV))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Bedrockium, 1),
                plate(PrPMaterials.HexagonalBoronNitride, 4))
            .fluidInputs(
                MaterialsAlloy.INDALLOY_140.getFluidStack(2592),
                fluid(PrPMaterials.HBNLubricant, 4000),
                molten(Materials.Neutronium,       9216),
                molten(Materials.CosmicNeutronium, 1952))
            .itemOutputs(ItemList.Electric_Motor_UHV.get(1))
            .duration(2400)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTRecipeConstants.AssemblyLine);

        // UHV Pump — hBN seal faces replace rubber molten; HBNLubricant replaces Lubricant
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Electric_Pump_UHV.get(1))
            .metadata(SCANNING, new Scanning(3600, TierEU.RECIPE_UV))
            .itemInputs(
                ItemList.Electric_Motor_UHV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.CosmicNeutronium, 1),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Bedrockium, 1),
                plate(PrPMaterials.HexagonalBoronNitride, 2))
            .fluidInputs(
                MaterialsAlloy.INDALLOY_140.getFluidStack(2592),
                fluid(PrPMaterials.HBNLubricant, 4000),
                molten(Materials.CosmicNeutronium, 2736),
                molten(Materials.Neutronium,       1728))
            .itemOutputs(ItemList.Electric_Pump_UHV.get(1))
            .duration(2400)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTRecipeConstants.AssemblyLine);

        // UHV Robot Arm — hBN joint bearings replace Lubricant; CosmicNeutronium + Naquadria preserved
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Robot_Arm_UHV.get(1))
            .metadata(SCANNING, new Scanning(3600, TierEU.RECIPE_UV))
            .itemInputs(
                ItemList.Electric_Motor_UHV.get(2),
                ItemList.Electric_Piston_UHV.get(1),
                plate(PrPMaterials.HexagonalBoronNitride, 4))
            .fluidInputs(
                MaterialsAlloy.INDALLOY_140.getFluidStack(2592),
                fluid(PrPMaterials.HBNLubricant, 4000),
                molten(Materials.CosmicNeutronium, 3168),
                molten(Materials.Naquadria,        2592))
            .itemOutputs(ItemList.Robot_Arm_UHV.get(1))
            .duration(2400)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTRecipeConstants.AssemblyLine);
    }

    // =========================================================================
    // SILICA AEROGEL (UHV) — loaded catalyst support, insulation panel
    // =========================================================================

    private static void aerogelCatalystSupport() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                plate(PrPMaterials.SilicaAerogel, 1),
                dustSmall(Materials.Platinum, 1),
                circuit(1))
            .itemOutputs(dust(PrPMaterials.LoadedAerogelCatalystSupport, 1))
            .duration(600)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.assemblerRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                plate(PrPMaterials.SilicaAerogel, 1),
                dustSmall(Materials.Palladium, 1),
                circuit(2))
            .itemOutputs(dust(PrPMaterials.LoadedAerogelCatalystSupport, 1))
            .duration(600)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    private static void aerogelInsulationPanel() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                plate(PrPMaterials.SilicaAerogel, 2),
                plate(PrPMaterials.CarbonFiberComposite, 1),
                circuit(3))
            .itemOutputs(plate(PrPMaterials.AerogelInsulationPanel, 2))
            .duration(600)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.assemblerRecipes);
    }
}
