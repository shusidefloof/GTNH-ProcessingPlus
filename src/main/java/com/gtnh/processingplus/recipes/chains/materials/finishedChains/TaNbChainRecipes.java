package com.gtnh.processingplus.recipes.chains.materials.finishedChains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeConstants;

public class TaNbChainRecipes {

    public static void init() {
        pyrochlore_HFLeach();
        pyrochlore_Precipitate();
        pyrochlore_Reduce();
        tantalite_HFLeach();
        tantalite_MIBKExtraction();
        tantalite_Strip();
        tantalite_Reduce();
        mibk_Synthesis();
    }

    // =========================================================
    // PYROCHLORE → NIOBIUM
    // =========================================================
    // 4 pyrochlore → 2000 mB NbF₅ solution → 2 Nb₂O₅ → 4 Nb ingots
    // Nb raffinate from tantalite feeds into step 2 (same recipe).
    // =========================================================

    private static void pyrochlore_HFLeach() {
        GTValues.RA.stdBuilder()
            .itemInputs(item("dustPyrochlore", 4), circuit(1))
            .fluidInputs(fluid(Materials.HydrofluoricAcid, 2000))
            .fluidOutputs(fluid(PrPMaterials.NiobiumFluorideSolution, 2000))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // NH₃ precipitates Nb(OH)₅; calcination collapses it to Nb₂O₅.
    // HF is recovered — feed back into leach steps.
    private static void pyrochlore_Precipitate() {
        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.NiobiumFluorideSolution, 2000), fluid(Materials.Ammonia, 1000))
            .itemOutputs(dust(PrPMaterials.NiobiumPentoxide, 2))
            .fluidOutputs(fluid(Materials.HydrofluoricAcid, 1000))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // Aluminothermic: Nb₂O₅ + (10/3)Al → 2Nb + (5/3)Al₂O₃
    private static void pyrochlore_Reduce() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.NiobiumPentoxide, 3), dust(Materials.Aluminium, 4))
            .itemOutputs(ingot(Materials.Niobium, 6), item("dustAlumina", 10))
            .duration(90*20)
            .eut(TierEU.RECIPE_HV)
            .metadata(GTRecipeConstants.COIL_HEAT, 2750)
            .addTo(RecipeMaps.blastFurnaceRecipes);
    }

    // =========================================================
    // TANTALITE → TANTALUM (+ Nb raffinate byproduct)
    // =========================================================
    // 4 tantalite → 2 Ta ingots (vs 23 tantalite per 1 Ta in electrolyzer)
    // Nb raffinate (1000 mB NbF₅ solution) feeds directly into
    // pyrochlore_Precipitate — same recipe, no extra machine.
    // =========================================================

    private static void tantalite_HFLeach() {
        GTValues.RA.stdBuilder()
            .itemInputs(item("dustTantalite", 4), item("dustPyrochlore", 4), circuit(2))
            .fluidInputs(fluid(Materials.HydrofluoricAcid, 2000))
            .fluidOutputs(fluid(PrPMaterials.MixedTaNbFluorideSolution, 2000))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // MIBK preferentially extracts Ta into the organic phase;
    // Nb stays in the aqueous raffinate as NiobiumFluorideSolution.
    private static void tantalite_MIBKExtraction() {
        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.MixedTaNbFluorideSolution, 2000), fluid(PrPMaterials.MIBK, 1000))
            .fluidOutputs(fluid(PrPMaterials.TaLoadedMIBK, 1000), fluid(PrPMaterials.NiobiumFluorideSolution, 1000))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // Water back-extraction strips Ta from organic; MIBK is fully recovered.
    private static void tantalite_Strip() {
        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.TaLoadedMIBK, 1000), fluid(Materials.Water, 500))
            .itemOutputs(dust(PrPMaterials.TantalumPentoxide, 4))
            .fluidOutputs(fluid(PrPMaterials.MIBK, 1000))
            .duration(300)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // Aluminothermic: Ta₂O₅ + (10/3)Al → 2Ta + (5/3)Al₂O₃
    private static void tantalite_Reduce() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.TantalumPentoxide, 3), dust(Materials.Aluminium, 4))
            .itemOutputs(ingot(Materials.Tantalum, 6), item("dustAlumina", 10))
            .duration(90 * 20)
            .eut(TierEU.RECIPE_HV)
            .metadata(GTRecipeConstants.COIL_HEAT, 2400)
            .addTo(RecipeMaps.blastFurnaceRecipes);
    }

    // =========================================================
    // MIBK SYNTHESIS — acetone aldol condensation (simplified)
    // 2 acetone → MIBK + water
    // Net consumption is near-zero since MIBK is recovered in tantalite_Strip.
    // =========================================================
    private static void mibk_Synthesis() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(Materials.Acetone, 2000), fluid(Materials.Hydrogen, 1000))
            .fluidOutputs(fluid(PrPMaterials.MIBK, 1000), fluid(Materials.Water, 1000))
            .duration(300)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }
}
