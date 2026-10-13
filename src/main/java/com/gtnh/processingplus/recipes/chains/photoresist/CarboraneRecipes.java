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
 * LCR → AAR → HTRF → CSC → CRV → CSTR → HPSF → BOF → PCV → SPC. Diborane is pyrolysed to decaborane, the CSC
 * separates it from the unreacted diborane (which feeds the AAR's ammonia-borane), a carbon is inserted into the
 * open decaborane cage, and two BH₃ units from the ammonia-borane close it into the twelve-vertex monocarba cage.
 * Francium (a tiny dust per HPSF run) templates the cage salt crystal; the BOF chlorine blow perchlorinates the cage,
 * protonates it with its own HCl and throws the sodium, the Francium and the degraded boron off as Carborane Chloride
 * Slag, which must be processed or the BOF's output fills up and the machine stops. One run of every step makes one
 * SPC run (2 Magic Cage); the LuV blend takes 4.
 *
 * <p>
 * Elemental gases count per atom (one H₂ or Cl₂ is 2000 mB), molecules count 1000 mB each.
 */
public class CarboraneRecipes {

    public static void init() {
        step1_Diborane();
        step1Alt_BoronTrioxideRoute();
        step2_AmmoniaBorane();
        step3_DecaboraneMelt();
        step4_CryoFractionation();
        step5_CarbonInsertion();
        step6_CageClosure();
        step7_FranciumTemplating();
        step8_ChlorineBlow();
        step9_VacuumStripping();
        step10_MagicCage();
        slag_Hydrolysis();
    }

    // 1. LCR: 2 NaBH4 + H2SO4 → B2H6 + 2 H2 + Na2SO4 (diborane is pyrophoric, hence the inert-gas steps later).
    // Deliberately generous: 18 dust of borohydride make 6000 mB of diborane, far more than the stoichiometry allows.
    private static void step1_Diborane() {
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.SodiumBorohydride.getDust(18), circuit(1))
            .fluidInputs(fluid(Materials.SulfuricAcid, 3000))
            .fluidOutputs(fluid(PrPMaterials.Diborane, 6000), fluid(Materials.Hydrogen, 6000))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // 1 (alt). HPSF: 2 B2O3 + 4 Al + 6 H2 → 2 B2H6 + 2 Al2O3 under high-pressure hydrogen. Skips the sodium
    // borohydride line and uses the Boron Trioxide the hBN chain already makes, but needs three runs per SPC run and
    // real heat.
    private static void step1Alt_BoronTrioxideRoute() {
        GTValues.RA.stdBuilder()
            .itemInputs(item("dustBoronTrioxide", 10), dust(Materials.Aluminium, 4), circuit(2))
            .fluidInputs(fluid(Materials.Hydrogen, 12000))
            .itemOutputs(item("dustAlumina", 10))
            .fluidOutputs(fluid(PrPMaterials.Diborane, 2000))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .metadata(GTRecipeConstants.COIL_HEAT, 2400)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // 2. AAR: B2H6 + 2 NH3 → 2 H3N·BH3 in an ammonia atmosphere. Takes the diborane the CSC recovers in step 4; the
    // adduct is the BH3 source that closes the cage in step 6.
    private static void step2_AmmoniaBorane() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(PrPMaterials.Diborane, 1000), fluid(Materials.Ammonia, 2000))
            .itemOutputs(dust(PrPMaterials.AmmoniaBorane, 2))
            .duration(12 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .metadata(GTRecipeConstants.COIL_HEAT, 900)
            .addTo(GTNHPPRecipeMaps.sAARRecipes);
    }

    // 3. HTRF: diborane pyrolysis, 5 B2H6 → B10H14 + 8 H2. One extra diborane goes through unreacted, so the melt is
    // decaborane with diborane dissolved in it.
    private static void step3_DecaboraneMelt() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(PrPMaterials.Diborane, 6000))
            .fluidOutputs(fluid(PrPMaterials.CrudeDecaboraneMelt, 2000), fluid(Materials.Hydrogen, 16000))
            .duration(15 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .metadata(GTRecipeConstants.COIL_HEAT, 2700)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // 4. CSC: fractional condensation. Diborane (bp -92 °C) stays a gas long after decaborane has frozen out, so the
    // two separate cleanly; the recovered diborane goes back to the AAR (the CSC keeps its Freon cycle).
    private static void step4_CryoFractionation() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(fluid(PrPMaterials.CrudeDecaboraneMelt, 2000), fluid(PrPMaterials.FreonR12, 1000))
            .fluidOutputs(
                fluid(PrPMaterials.CryoFractionatedDecaborane, 1000),
                fluid(PrPMaterials.Diborane, 1000),
                fluid(PrPMaterials.FreonR12, 900))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSCRecipes);
    }

    // 5. CRV: Brellochs carbon insertion, B10H14 + CH2O + 2 NaOH + H2O → Na[arachno-CB9H14] + Na[B(OH)4] + H2. One
    // boron leaves the cage as borate (worked up to boric acid, feeding GT's own trimethyl borate line). hBN-lined,
    // Argon blanket since decaborane is air sensitive.
    private static void step5_CarbonInsertion() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.SodiumHydroxide, 6), circuit(11))
            .fluidInputs(
                fluid(PrPMaterials.CryoFractionatedDecaborane, 1000),
                fluid("fluid.formaldehyde", 1000),
                fluid(Materials.Water, 1000),
                fluid(PrPMaterials.HBNLubricant, 1000),
                fluid(Materials.Argon, 2000))
            .itemOutputs(dust(PrPMaterials.OpenCageCarbaborateSalt, 2))
            .fluidOutputs(fluid("boricacid", 1000), fluid(Materials.Hydrogen, 2000))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sCRVRecipes);
    }

    // 6. CSTR: cage closure, Na[CB9H14] + 2 H3N·BH3 → Na[closo-CB11H12] + 2 NH3 + 4 H2. Two BH3 units cap the open
    // face and the cage closes into the twelve-vertex icosahedron.
    private static void step6_CageClosure() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.OpenCageCarbaborateSalt, 2), dust(PrPMaterials.AmmoniaBorane, 2), circuit(6))
            .itemOutputs(dust(PrPMaterials.ClosoCarboraneCage, 2))
            .fluidOutputs(fluid(Materials.Ammonia, 2000), fluid(Materials.Hydrogen, 8000))
            .duration(15 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // 7. HPSF: the cage salt is recrystallised under pressure around a tiny Francium dust that templates the crystal
    // (real chemistry precipitates these anions with caesium, Francium is its heavier neighbour)
    private static void step7_FranciumTemplating() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                dust(PrPMaterials.ClosoCarboraneCage, 2),
                GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Francium, 1),
                circuit(7))
            .fluidInputs(fluid(Materials.Argon, 1000))
            .itemOutputs(dust(PrPMaterials.FranciumCarboranideCrystal, 2))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .metadata(GTRecipeConstants.COIL_HEAT, 4200)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // 8. BOF: chlorine blown through the molten crystal, [CB11H12]- + 11 Cl2 → [CHB11Cl11]- + 11 HCl. One of those
    // HCl protonates the cage (the sodium leaves as NaCl), so 10 come back out: 9 here and 1 dissolved in the acid
    // solution. The NaCl, the Francium and the degraded boron are thrown off as Carborane Chloride Slag. Slag is an
    // item output, so a full output bus stops the BOF until it is processed (see slag_Hydrolysis).
    private static void step8_ChlorineBlow() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.FranciumCarboranideCrystal, 2), circuit(3))
            .fluidInputs(fluid(Materials.Chlorine, 22000))
            .itemOutputs(dust(PrPMaterials.CarboraneChlorideSlag, 5))
            .fluidOutputs(fluid(PrPMaterials.CarboraneAcidSolution, 2000), fluid(Materials.HydrochloricAcid, 9000))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sBOFRecipes);
    }

    // 9. PCV: vacuum strips the dissolved HCl off the acid solution
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

    // Slag: hydrolysis, BCl3 + 3 H2O → H3BO3 + 3 HCl, returns the boron as boric acid, about two thirds of the
    // Francium, and Salt (which GT's electrolyzer turns back into Chlorine and Sodium).
    private static void slag_Hydrolysis() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.CarboraneChlorideSlag, 15), circuit(7))
            .fluidInputs(fluid(Materials.Water, 6000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Francium, 2), dust(Materials.Salt, 8))
            .fluidOutputs(fluid("boricacid", 2000), fluid(Materials.HydrochloricAcid, 6000))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }
}
