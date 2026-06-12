package com.gtnh.processingplus.recipes.chains.materials.finishedChains;

import com.gtnh.processingplus.materials.PrPMaterials;
import static com.gtnh.processingplus.items.Intermediate.*;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeConstants;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;
import static com.gtnh.processingplus.recipes.PPRecipeHelper.dust;
import static com.gtnh.processingplus.recipes.PPRecipeHelper.fluid;

public class PassiveableMaterials {
    public static void init() {
        biomassInit();
        coalFlyash();
        TaNbChain();
    }

    private static void biomassInit() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(Materials.Biomass, 10000))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 2000),
                fluid(Materials.Phenol, 500),
                fluid(Materials.AceticAcid, 350),
                fluid(PrPMaterials.Ketene, 300))
            .itemOutputs(dust(Materials.Carbon, 25), dust(Materials.Ash, 5))
            .eut(TierEU.RECIPE_HV)
            .duration(28 * 20)
            .metadata(GTRecipeConstants.COIL_HEAT, 975)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(Materials.FermentedBiomass, 10000))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 2000),
                fluid(Materials.Methane, 600),
                fluid(Materials.Ethanol, 500),
                fluid(Materials.MethylAcetate, 250))
            .itemOutputs(dust(Materials.Carbon, 35), dust(Materials.Ash, 8))
            .eut(TierEU.RECIPE_HV)
            .duration(40 * 20)
            .metadata(GTRecipeConstants.COIL_HEAT, 975)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    private static void coalFlyash() {

        // =========================================================
        // 1. Coal combustion — captures flyash before it escapes
        // Coal Dust × 8 + O₂ → Coal Flyash × 4 + CO₂
        // =========================================================

        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Coal, 8), circuit(1))
            .fluidInputs(fluid(Materials.Oxygen, 4000))
            .itemOutputs(dust(PrPMaterials.CoalFlyash, 4))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 4000))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);


        // =========================================================
        // 2. Sulfuric acid leach — dissolves Ga and Ge, silica stays
        // CoalFlyash × 8 + H₂SO₄ + H₂O → MetalLeachate + SiO₂
        // =========================================================

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.CoalFlyash, 8), circuit(2))
            .fluidInputs(fluid(Materials.SulfuricAcid, 2000), fluid(Materials.Water, 1000))
            .itemOutputs(dust(Materials.SiliconDioxide, 2))
            .fluidOutputs(fluid(PrPMaterials.MetalLeachate, 4000))
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // =========================================================
        // 3. pH adjustment — NH₃ raises pH, precipitates hydroxides
        // MetalLeachate + NH₃ → Ga(OH)₃ + Ge(OH)₄ + H₂O + recovered NH₃
        // Ga precipitates first (pH 3-4); Ge follows (pH 6-7).
        // ~200 mB NH₃ recovered from stripping the spent liquor — net 300 mB consumed.
        // =========================================================

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(PrPMaterials.MetalLeachate, 4000), fluid(Materials.Ammonia, 500))
            .itemOutputs(dust(PrPMaterials.GalliumHydroxide, 2), dust(PrPMaterials.GermaniumHydroxide, 2))
            .fluidOutputs(fluid(Materials.Water, 1000), fluid(Materials.Ammonia, 200))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // =========================================================
        // 4a. Ga(OH)₃ + 3 HCl → GaCl₃ solution + H₂O
        // =========================================================

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.GalliumHydroxide, 2), circuit(4))
            .fluidInputs(fluid(Materials.HydrochloricAcid, 3000))
            .fluidOutputs(fluid(PrPMaterials.GalliumTrichlorideSolution, 2000), fluid(Materials.Water, 1000))
            .duration(160)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // =========================================================
        // 4b. Ge(OH)₄ + 4 HCl → GeCl₄ + H₂O
        // =========================================================

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.GermaniumHydroxide, 2), circuit(5))
            .fluidInputs(fluid(Materials.HydrochloricAcid, 4000))
            .fluidOutputs(fluid(PrPMaterials.GermaniumTetrachlorideSolution, 2000), fluid(Materials.Water, 2000))
            .duration(160)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // =========================================================
        // 5a. GaCl₃ solution → Ga dust + HCl (electroreduction)
        // Improved yield — better recovery per mole of chloride
        // =========================================================

        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.GalliumTrichlorideSolution, 2000))
            .itemOutputs(dust(Materials.Gallium, 3))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 3000))
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.electrolyzerRecipes);

        // =========================================================
        // 5b. GeCl₄ + 2 H₂ → Ge dust + 4 HCl (hydrogen reduction)
        // Improved yield — better recovery per mole of chloride
        // =========================================================

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(6))
            .fluidInputs(fluid(PrPMaterials.GermaniumTetrachlorideSolution, 2000), fluid(Materials.Hydrogen, 4000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, "Germanium", 3))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 4000))
            .duration(280)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    private static void TaNbChain() {

        // =========================================================
        // PYROCHLORE → NIOBIUM
        // =========================================================
        // 4 pyrochlore → 2000 mB NbF₅ solution → 2 Nb₂O₅ → 4 Nb ingots
        // Nb raffinate from tantalite feeds into step 2 (same recipe).
        // =========================================================

        GTValues.RA.stdBuilder()
            .itemInputs(item("dustPyrochlore", 4), circuit(1))
            .fluidInputs(fluid(Materials.HydrofluoricAcid, 2000))
            .fluidOutputs(fluid(PrPMaterials.NiobiumFluorideSolution, 2000))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // NH₃ precipitates Nb(OH)₅; calcination collapses it to Nb₂O₅.
        // HF is recovered — feed back into leach steps.
        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.NiobiumFluorideSolution, 2000), fluid(Materials.Ammonia, 1000))
            .itemOutputs(dust(PrPMaterials.NiobiumPentoxide, 2))
            .fluidOutputs(fluid(Materials.HydrofluoricAcid, 1000))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // Aluminothermic: Nb₂O₅ + (10/3)Al → 2Nb + (5/3)Al₂O₃
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.NiobiumPentoxide, 3), dust(Materials.Aluminium, 4))
            .itemOutputs(ingot(Materials.Niobium, 6), item("dustAlumina", 10))
            .duration(90 * 20)
            .eut(TierEU.RECIPE_HV)
            .metadata(GTRecipeConstants.COIL_HEAT, 2750)
            .addTo(RecipeMaps.blastFurnaceRecipes);


        // =========================================================
        // TANTALITE → TANTALUM (+ Nb raffinate byproduct)
        // =========================================================
        // 4 tantalite → 2 Ta ingots (vs 23 tantalite per 1 Ta in electrolyzer)
        // Nb raffinate (1000 mB NbF₅ solution) feeds directly into
        // pyrochlore_Precipitate — same recipe, no extra machine.
        // =========================================================

        GTValues.RA.stdBuilder()
            .itemInputs(item("dustTantalite", 4), item("dustPyrochlore", 4), circuit(2))
            .fluidInputs(fluid(Materials.HydrofluoricAcid, 2000))
            .fluidOutputs(fluid(PrPMaterials.MixedTaNbFluorideSolution, 2000))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // MIBK preferentially extracts Ta into the organic phase;
        // Nb stays in the aqueous raffinate as NiobiumFluorideSolution.
        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.MixedTaNbFluorideSolution, 2000), fluid(PrPMaterials.MIBK, 1000))
            .fluidOutputs(fluid(PrPMaterials.TaLoadedMIBK, 1000), fluid(PrPMaterials.NiobiumFluorideSolution, 1000))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // Water back-extraction strips Ta from organic; MIBK is fully recovered.
        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.TaLoadedMIBK, 1000), fluid(Materials.Water, 500))
            .itemOutputs(dust(PrPMaterials.TantalumPentoxide, 4))
            .fluidOutputs(fluid(PrPMaterials.MIBK, 1000))
            .duration(300)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // Aluminothermic: Ta₂O₅ + (10/3)Al → 2Ta + (5/3)Al₂O₃
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.TantalumPentoxide, 3), dust(Materials.Aluminium, 4))
            .itemOutputs(ingot(Materials.Tantalum, 6), item("dustAlumina", 10))
            .duration(90 * 20)
            .eut(TierEU.RECIPE_HV)
            .metadata(GTRecipeConstants.COIL_HEAT, 2400)
            .addTo(RecipeMaps.blastFurnaceRecipes);

        // =========================================================
        // MIBK SYNTHESIS — acetone aldol condensation (simplified)
        // 2 acetone → MIBK + water
        // Net consumption is near-zero since MIBK is recovered in tantalite_Strip.
        // =========================================================
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(Materials.Acetone, 2000), fluid(Materials.Hydrogen, 1000))
            .fluidOutputs(fluid(PrPMaterials.MIBK, 1000), fluid(Materials.Water, 1000))
            .duration(300)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }
}
