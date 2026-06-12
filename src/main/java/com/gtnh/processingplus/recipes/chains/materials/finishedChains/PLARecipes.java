package com.gtnh.processingplus.recipes.chains.materials.finishedChains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import static com.gtnh.processingplus.items.Intermediate.*;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;

public class PLARecipes {

    public static void init() {
        step0_PropyleneGlycolSynthesis();
        step1_FermentationRoute();
        step1b_PropyleneGlycolRoute();
        step1c_AcetaldehydeStreckerRoute();
        step2_LactideFormation();
        step3_Polymerization();
    }

    // =========================================================
    // 0. Propylene Glycol synthesis — HPPO process (LuV LCR)
    // Propene + H2O2 → PropyleneGlycol + Water
    // =========================================================
    private static void step0_PropyleneGlycolSynthesis() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(
                fluid(Materials.Propene, 1000),
                fluid("fluid.hydrogenperoxide", 1000),
                fluid(Materials.Water, 500))
            .fluidOutputs(fluid(PrPMaterials.PropyleneGlycol, 1000), fluid(Materials.Water, 1500))
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 1. Fermentation route — sugarcane + water → Lactic Acid (LuV Fermenter)
    // =========================================================
    private static void step1_FermentationRoute() {

        GTValues.RA.stdBuilder()
            .itemInputs(new net.minecraft.item.ItemStack(net.minecraft.init.Items.reeds, 32))
            .fluidInputs(fluid(Materials.Water, 1000))
            .fluidOutputs(fluid(PrPMaterials.LacticAcid, 1000))
            .duration(600)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.fermentingRecipes);
    }

    // =========================================================
    // 1b. Propylene Glycol oxidation → Lactic Acid (ZPM LCR)
    // =========================================================
    private static void step1b_PropyleneGlycolRoute() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(PrPMaterials.PropyleneGlycol, 1000), fluid(Materials.Oxygen, 500))
            .fluidOutputs(fluid(PrPMaterials.LacticAcid, 3000))
            .duration(400)
            .eut(TierEU.RECIPE_IV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 1c. Acetaldehyde Strecker route → Lactic Acid (ZPM LCR)
    // Uses HCN from carbon fiber carbonization byproduct
    // =========================================================
    private static void step1c_AcetaldehydeStreckerRoute() {

        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(
                fluid(Materials.Acetaldehyde, 1000),
                fluid("hydrogencyanide", 1000),
                fluid(Materials.Ammonia, 1000),
                fluid(Materials.Water, 2000))
            .fluidOutputs(fluid(PrPMaterials.LacticAcid, 6000), fluid("ammonium chloride", 500))
            .duration(600)
            .eut(TierEU.RECIPE_IV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 2. 2x Lactic Acid → Lactide + Water (ZPM vacuum dehydration)
    // =========================================================
    private static void step2_LactideFormation() {

        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.LacticAcid, 2000))
            .itemOutputs(dust(PrPMaterials.Lactide, 2))
            .fluidOutputs(fluid(Materials.Water, 500))
            .duration(400)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);
    }

    // =========================================================
    // 3. Lactide + Sn catalyst → molten PLA (LuV PCV)
    // LuV-tier so a LuV energy hatch runs it — consistent with the rest of the PCV.
    // =========================================================
    private static void step3_Polymerization() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.Lactide, 4), dust(Materials.Tin, 1))
            .fluidOutputs(molten(PrPMaterials.PolylacticAcid, 576))
            .duration(600)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sPCVRecipes);
    }
}
