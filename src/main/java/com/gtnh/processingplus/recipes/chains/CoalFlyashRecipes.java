package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeConstants;

public class CoalFlyashRecipes {

    public static void init() {
        step1_FlyashProduction();
        step2_AcidLeach();
        step3_HydroxidePrecipitation();
        step4a_GalliumTrichloride();
        step4b_GermaniumTetrachloride();
        step5a_GalliumElectrolysis();
        step5b_GermaniumReduction();
    }

    // =========================================================
    // 1. Coal combustion — captures flyash before it escapes
    // Coal Dust × 8 + O₂ → Coal Flyash × 4 + CO₂
    // =========================================================
    private static void step1_FlyashProduction() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Coal, 8), circuit(1))
            .fluidInputs(fluid(Materials.Oxygen, 4000))
            .itemOutputs(dust(PrPMaterials.CoalFlyash, 4))
            .fluidOutputs(fluid(Materials.CarbonDioxide, 4000))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 2. Sulfuric acid leach — dissolves Ga and Ge, silica stays
    // CoalFlyash × 8 + H₂SO₄ + H₂O → MetalLeachate + SiO₂
    // =========================================================
    private static void step2_AcidLeach() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.CoalFlyash, 8), circuit(2))
            .fluidInputs(fluid(Materials.SulfuricAcid, 2000), fluid(Materials.Water, 1000))
            .itemOutputs(dust(Materials.SiliconDioxide, 2))
            .fluidOutputs(fluid(PrPMaterials.MetalLeachate, 4000))
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 3. pH adjustment — NH₃ raises pH, precipitates hydroxides
    // MetalLeachate + NH₃ → Ga(OH)₃ + Ge(OH)₄ + H₂O + recovered NH₃
    // Ga precipitates first (pH 3-4); Ge follows (pH 6-7).
    // ~200 mB NH₃ recovered from stripping the spent liquor — net 300 mB consumed.
    // =========================================================
    private static void step3_HydroxidePrecipitation() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(PrPMaterials.MetalLeachate, 4000), fluid(Materials.Ammonia, 500))
            .itemOutputs(dust(PrPMaterials.GalliumHydroxide, 2), dust(PrPMaterials.GermaniumHydroxide, 2))
            .fluidOutputs(fluid(Materials.Water, 1000), fluid(Materials.Ammonia, 200))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 4a. Ga(OH)₃ + 3 HCl → GaCl₃ solution + H₂O
    // =========================================================
    private static void step4a_GalliumTrichloride() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.GalliumHydroxide, 2), circuit(4))
            .fluidInputs(fluid(Materials.HydrochloricAcid, 3000))
            .fluidOutputs(fluid(PrPMaterials.GalliumTrichlorideSolution, 2000), fluid(Materials.Water, 1000))
            .duration(160)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================
    // 4b. Ge(OH)₄ + 4 HCl → GeCl₄ + H₂O
    // =========================================================
    private static void step4b_GermaniumTetrachloride() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.GermaniumHydroxide, 2), circuit(5))
            .fluidInputs(fluid(Materials.HydrochloricAcid, 4000))
            .fluidOutputs(fluid(PrPMaterials.GermaniumTetrachlorideSolution, 2000), fluid(Materials.Water, 2000))
            .duration(160)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // =========================================================
    // 5a. GaCl₃ solution → Ga dust + HCl (electroreduction)
    // Improved yield — better recovery per mole of chloride
    // =========================================================
    private static void step5a_GalliumElectrolysis() {
        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.GalliumTrichlorideSolution, 2000))
            .itemOutputs(dust(Materials.Gallium, 3))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 3000))
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.electrolyzerRecipes);
    }

    // =========================================================
    // 5b. GeCl₄ + 2 H₂ → Ge dust + 4 HCl (hydrogen reduction)
    // Improved yield — better recovery per mole of chloride
    // =========================================================
    private static void step5b_GermaniumReduction() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(6))
            .fluidInputs(fluid(PrPMaterials.GermaniumTetrachlorideSolution, 2000), fluid(Materials.Hydrogen, 4000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, "Germanium", 3))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 4000))
            .duration(280)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }
}
