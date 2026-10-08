package com.gtnh.processingplus.recipes.chains.photoresist;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import java.util.Collection;

import com.gtnh.processingplus.machines.spc.MachineType;
import com.gtnh.processingplus.machines.spc.SPCRecipeData;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeConstants;

/**
 * The LuV "Decathlon": ten steps, ten different machines, ending in a carborane photoacid generator
 * (Triphenylsulfonium Undecachlorocarboranide, "Magic Cage") that the LuV photoresist blend consumes.
 *
 * <p>
 * LCR → AAR → HTRF → CSC → CRV → CSTR → HPSF → BOF → PCV → SPC. Francium (a tiny dust per HPSF run) templates the
 * cage crystal; the BOF chlorine blow throws it, the boron and the chlorine into Carborane Chloride Slag, which
 * must be processed or the BOF's output fills up and the machine stops. One run of every step makes one SPC run
 * (2 Magic Cage); the LuV blend takes 6.
 */
public class CarboraneRecipes {

    public static void init() {
        step1_Diborane();
        step1Alt_BoronTrioxideRoute();
        step2_AmmoniaBorane();
        step3_DecaboraneMelt();
        step4_CryoFractionation();
        step5_CarboraneCage();
        step6_CageOpening();
        step7_CageClosure();
        step8_ChlorineBlow();
        step9_VacuumStripping();
        step10_MagicCage();
        slag_Hydrolysis();
    }

    // 1. LCR: NaBH4 + H2SO4 → B2H6 (diborane is pyrophoric, hence the inert-gas steps later). Deliberately generous:
    // 6 dust of borohydride make 2000 mB of diborane, half of what the stoichiometry would need.
    private static void step1_Diborane() {
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.SodiumBorohydride.getDust(6), circuit(1))
            .fluidInputs(fluid(Materials.SulfuricAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.Diborane, 2000), fluid(Materials.Hydrogen, 2000))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // 1 (alt). HPSF: B2O3 + 2 Al + 3 H2 → B2H6 + Al2O3 under high-pressure hydrogen. Skips the sodium borohydride
    // line and uses the Boron Trioxide the hBN chain already makes, but is far less efficient (10 B2O3 dust and
    // 4 Al for the same 2000 mB of diborane) and needs real heat.
    private static void step1Alt_BoronTrioxideRoute() {
        GTValues.RA.stdBuilder()
            .itemInputs(item("dustBoronTrioxide", 10), dust(Materials.Aluminium, 4), circuit(2))
            .fluidInputs(fluid(Materials.Hydrogen, 6000))
            .itemOutputs(item("dustAlumina", 10))
            .fluidOutputs(fluid(PrPMaterials.Diborane, 2000))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .metadata(GTRecipeConstants.COIL_HEAT, 2400)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // 2. AAR: B2H6 + NH3 in an ammonia atmosphere → ammonia-borane adduct
    private static void step2_AmmoniaBorane() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(PrPMaterials.Diborane, 2000), fluid(Materials.Ammonia, 4000))
            .itemOutputs(dust(PrPMaterials.AmmoniaBorane, 6))
            .fluidOutputs(fluid(Materials.Hydrogen, 2000))
            .duration(12 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .metadata(GTRecipeConstants.COIL_HEAT, 900)
            .addTo(GTNHPPRecipeMaps.sAARRecipes);
    }

    // 3. HTRF: thermal dehydrocoupling of the adduct into a decaborane melt (ammonia comes back out)
    private static void step3_DecaboraneMelt() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.AmmoniaBorane, 6), circuit(3))
            .fluidOutputs(
                fluid(PrPMaterials.CrudeDecaboraneMelt, 3000),
                fluid(Materials.Hydrogen, 6000),
                fluid(Materials.Ammonia, 4000))
            .duration(15 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .metadata(GTRecipeConstants.COIL_HEAT, 2700)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // 4. CSC: cryogenic fractionation strips the volatile impurities (the CSC keeps its Freon cycle)
    private static void step4_CryoFractionation() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(fluid(PrPMaterials.CrudeDecaboraneMelt, 3000), fluid(PrPMaterials.FreonR12, 1000))
            .fluidOutputs(fluid(PrPMaterials.CryoFractionatedDecaborane, 2000), fluid(PrPMaterials.FreonR12, 900))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);
    }

    // 5. CRV: decaborane + acetylene close the carbon into the icosahedral cage (hBN-lined, Argon blanket)
    private static void step5_CarboraneCage() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(11))
            .fluidInputs(
                fluid(PrPMaterials.CryoFractionatedDecaborane, 2000),
                fluid(Materials.Acetylene, 2000),
                fluid(PrPMaterials.HBNLubricant, 500),
                fluid(Materials.Argon, 2000))
            .itemOutputs(dust(PrPMaterials.OrthoCarborane, 2))
            .fluidOutputs(fluid(Materials.Hydrogen, 2000))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sCRVRecipes);
    }

    // 6. CSTR: base-mediated deboronation opens the cage (a borate leaves as boric acid, feeding GT's own
    // trimethyl borate line)
    private static void step6_CageOpening() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.OrthoCarborane, 2), dust(Materials.SodiumHydroxide, 6), circuit(6))
            .fluidInputs(fluid(Materials.Ethanol, 1000))
            .itemOutputs(dust(PrPMaterials.NidoCarboranideSalt, 2))
            .fluidOutputs(fluid("boricacid", 1000), fluid(Materials.Water, 1000))
            .duration(15 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // 7. HPSF: high-pressure sintering closes the cage around a tiny Francium dust that templates the crystal
    private static void step7_CageClosure() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                dust(PrPMaterials.NidoCarboranideSalt, 2),
                GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Francium, 1),
                circuit(7))
            .fluidInputs(fluid(Materials.Argon, 1000))
            .itemOutputs(dust(PrPMaterials.FranciumCarboranideCrystal, 2))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .metadata(GTRecipeConstants.COIL_HEAT, 4200)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // 8. BOF: chlorine blown through the molten crystal. The cage takes 11 chlorines, and the Francium, the
    // leftover boron and the spent chlorine are thrown off as Carborane Chloride Slag. Slag is an item output, so a
    // full output bus stops the BOF until it is processed (see slag_Hydrolysis).
    private static void step8_ChlorineBlow() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.FranciumCarboranideCrystal, 2), circuit(3))
            .fluidInputs(fluid(Materials.Chlorine, 11000))
            .itemOutputs(dust(PrPMaterials.CarboraneChlorideSlag, 5))
            .fluidOutputs(fluid(PrPMaterials.CarboraneAcidSolution, 2000), fluid(Materials.HydrochloricAcid, 6000))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sBOFRecipes);
    }

    // 9. PCV: vacuum strips the water and HCl off the acid solution
    private static void step9_VacuumStripping() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(10))
            .fluidInputs(fluid(PrPMaterials.CarboraneAcidSolution, 2000))
            .fluidOutputs(
                fluid(PrPMaterials.AnhydrousCarboraneSuperacid, 1000),
                fluid(Materials.HydrochloricAcid, 1000))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);
    }

    // 10. SPC: the superacid protonates the sulfonium salt into the photoacid generator. Light-isolated, with the
    // LuV station sequence.
    private static void step10_MagicCage() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.DiphenylsulfoniumSalt, 2), circuit(6))
            .fluidInputs(fluid(PrPMaterials.AnhydrousCarboraneSuperacid, 1000), fluid(Materials.Benzene, 2000))
            .itemOutputs(dust(PrPMaterials.MagicCage, 2))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 2000))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_REACTOR, MachineType.MIXER, MachineType.CHEMICAL_BATH,
                MachineType.LASER_ENGRAVER },
            new int[] { 4, 5, 5, 6 });
    }

    // Slag: hydrolysis returns boron as boric acid, about two thirds of the Francium, and Salt (which GT's
    // electrolyzer turns back into Chlorine and Sodium).
    private static void slag_Hydrolysis() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.CarboraneChlorideSlag, 15), circuit(7))
            .fluidInputs(fluid(Materials.Water, 6000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Francium, 2), dust(Materials.Salt, 8))
            .fluidOutputs(fluid("boricacid", 3000), fluid(Materials.HydrochloricAcid, 3000))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }
}
