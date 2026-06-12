package com.gtnh.processingplus.recipes.chains.materials.finishedChains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import static com.gtnh.processingplus.items.Intermediate.*;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeConstants;
import gregtech.loaders.postload.recipes.beamcrafter.BeamCrafterMetadata;

import gtnhlanth.common.beamline.Particle;

public class NeptuniumSynthesis {

    public static void init() {
        step1_NitricAcidDissolution();
        step2_NaphthaNitricExtraction();
        step3_AmmoniaPrecipitation();
        step3r_ReconcentrateNitricAcid();
        step3r2_DecomposeAmmoniumNitrate();
        step4a_CalciumThermite();
        step4b_BariumThermite();

        promethium();
        heavyWaterHeliumBranch();
    }

    // =========================================================
    // 1. Dissolve depleted uranium rod in HNO3 — LCR
    // Single / dual / quad rod variants, scaled 1× / 2× / 4×.
    // Outputs U dust + neptunium extraction residue + diluted HNO3 + empty rod casings.
    // =========================================================
    private static void step1_NitricAcidDissolution() {

        // Single rod
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.DepletedRodUranium.get(1), circuit(1))
            .fluidInputs(fluid(Materials.NitricAcid, 2000))
            .itemOutputs(
                dust(Materials.Uranium, 3),
                dust(PrPMaterials.NeptuniumExtractionResidue, 1),
                ItemList.IC2_Fuel_Rod_Empty.get(1))
            .fluidOutputs(fluid(PrPMaterials.DilutedNitricAcid, 2000))
            .duration(600)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // Dual rod — 2×
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.DepletedRodUranium2.get(1), circuit(1))
            .fluidInputs(fluid(Materials.NitricAcid, 4000))
            .itemOutputs(
                dust(Materials.Uranium, 6),
                dust(PrPMaterials.NeptuniumExtractionResidue, 2),
                ItemList.IC2_Fuel_Rod_Empty.get(2))
            .fluidOutputs(fluid(PrPMaterials.DilutedNitricAcid, 4000))
            .duration(1000)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // Quad rod — 4×
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.DepletedRodUranium4.get(1), circuit(1))
            .fluidInputs(fluid(Materials.NitricAcid, 8000))
            .itemOutputs(
                dust(Materials.Uranium, 12),
                dust(PrPMaterials.NeptuniumExtractionResidue, 4),
                ItemList.IC2_Fuel_Rod_Empty.get(4))
            .fluidOutputs(fluid(PrPMaterials.DilutedNitricAcid, 8000))
            .duration(1800)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 2. Naphtha/nitric acid liquid-liquid extraction — LCR
    // 4× residue + HNO3 + naphtha → neptunium nitrate solution + recovered naphtha.
    // Naphtha acts as organic extractant (80% recovery).
    // =========================================================
    private static void step2_NaphthaNitricExtraction() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.NeptuniumExtractionResidue, 4))
            .fluidInputs(fluid(Materials.NitricAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.NeptuniumNitrateSolution, 2000))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // 3. Ammonia precipitation — LCR
    // Np nitrate solution + NH3 + water → neptunium oxide + ammonium nitrate solution.
    // All ammonia is retained in the ammonium nitrate solution; step 3r2 recovers it fully.
    // =========================================================
    private static void step3_AmmoniaPrecipitation() {

        GTValues.RA.stdBuilder()
            .fluidInputs(
                fluid(PrPMaterials.NeptuniumNitrateSolution, 2000),
                fluid(Materials.Ammonia, 1000),
                fluid(Materials.Water, 500))
            .itemOutputs(dust(PrPMaterials.NeptuniumOxide, 2))
            .fluidOutputs(fluid(PrPMaterials.AmmoniumNitrateSolution, 2000))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // Recycling 3r: Reconcentrate diluted HNO3 — Distillation Tower
    // 4000 mB diluted → 1000 mB HNO3 + 3000 mB water.
    // =========================================================
    private static void step3r_ReconcentrateNitricAcid() {

        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.DilutedNitricAcid, 4000))
            .fluidOutputs(fluid(Materials.NitricAcid, 1000), fluid(Materials.Water, 3000))
            .duration(600)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.distillationTowerRecipes);
    }

    // =========================================================
    // Recycling 3r2: Decompose ammonium nitrate solution — Distillation Tower
    // 2000 mB ammonium nitrate solution → 1000 mB NH3 + 2000 mB diluted HNO3.
    // Closes both the ammonia and nitric acid loops completely.
    // =========================================================
    private static void step3r2_DecomposeAmmoniumNitrate() {

        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.AmmoniumNitrateSolution, 2000))
            .fluidOutputs(fluid(Materials.Ammonia, 1000), fluid(Materials.NitricAcid, 2000))
            .duration(800)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.distillationTowerRecipes);
    }

    // =========================================================
    // 4a. NpO2 + 2 Ca → Np ingot + 2 CaO (quicklime) — EBF 1500 K
    // =========================================================
    private static void step4a_CalciumThermite() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.NeptuniumOxide, 1), dust(Materials.Calcium, 2))
            .itemOutputs(item("ingotNeptunium", 1), dust(Materials.Quicklime, 2))
            .duration(600)
            .eut(TierEU.RECIPE_HV)
            .metadata(GTRecipeConstants.COIL_HEAT, 1500)
            .addTo(RecipeMaps.blastFurnaceRecipes);
    }

    // =========================================================
    // 4b. NpO2 + 2 Ba → Np ingot + 2 BaO — EBF 1500 K (alternate)
    // =========================================================
    private static void step4b_BariumThermite() {

        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.NeptuniumOxide, 1), dust(Materials.Barium, 2))
            .itemOutputs(item("ingotNeptunium", 1), dust(PrPMaterials.BariumOxide, 2))
            .duration(600)
            .eut(TierEU.RECIPE_HV)
            .metadata(GTRecipeConstants.COIL_HEAT, 1500)
            .addTo(RecipeMaps.blastFurnaceRecipes);
    }

    private static void promethium() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Neodymium, 1), circuit(10))
            .itemOutputs(dust(PrPMaterials.Neodymium146, 1))
            .outputChances(10_00)
            .duration(50*20)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.centrifugeRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Neodymium, 1), circuit(10))
            .fluidInputs(fluid("xenon", 2000))
            .itemOutputs(dust(PrPMaterials.Neodymium146, 1))
            .duration(25*20)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.centrifugeRecipes);

        // Neutron activation: ¹⁴⁶Nd + neutron capture → ¹⁴⁷Nd. BeamCrafter collides two neutron
        // beams into the target; no fluid needed — the "bombardment" IS the particle metadata below.
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.Neodymium146, 1))
            .fluidInputs(fluid(Materials.Deuterium, 700))
            .itemOutputs(dust(PrPMaterials.Neodymium147, 1))
            .metadata(
                RecipeMaps.BEAMCRAFTER_METADATA,
                BeamCrafterMetadata.builder()
                    .particleID_A(Particle.NEUTRON.getId())
                    .particleID_B(Particle.NEUTRON.getId())
                    .amount_A(250)
                    .amount_B(250)
                    .build())
            .duration(10 * 20)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.beamcrafterRecipes);

    // =========================================================
    // Promethium chain (late ZPM): Nd-147 → fusion → Pm plasma → crude → resin purification → Pm.
    // No stable Pm isotope exists, so the only way to get it is to breed it — fitting for ZPM.
    // =========================================================

        // --- Stage 2: melt the activated isotope, then fuse a proton onto it ---
        // Nd-147 dust → molten Nd-147 (Fluid Extractor)
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.Neodymium147, 1))
            .fluidOutputs(molten(PrPMaterials.Neodymium147, 144))
            .duration(8 * 20)
            .eut(TierEU.RECIPE_IV)
            .addTo(RecipeMaps.fluidExtractionRecipes);

        // Fusion: molten Nd-147 + Hydrogen plasma → Promethium plasma (proton capture, Z 60 → 61).
        // MK2-tier startup (400M) lands this squarely at late ZPM.
        GTValues.RA.stdBuilder()
            .fluidInputs(molten(PrPMaterials.Neodymium147, 144), Materials.Hydrogen.getPlasma(1000))
            .fluidOutputs(Materials.Promethium.getPlasma(144))
            .duration(8 * 20)
            .eut(TierEU.RECIPE_ZPM)
            .metadata(GTRecipeConstants.FUSION_THRESHOLD, 400_000_000L)
            .addTo(RecipeMaps.fusionRecipes);

        // --- Stage 3: freeze the plasma into a crude condensate using cold helium as the coolant ---
        // (the crude carries the Sm-147 that Pm-147 decays into). Helium comes from the heavy-water
        // branch below, but is a standard fluid so the chain isn't hard-locked to that path.
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Promethium.getPlasma(144), fluid(PrPMaterials.HeavyWater, 1000))
            .fluidOutputs(PrPMaterials.RawPromethium.getFluidOrGas(144))
            .duration(10 * 20)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.vacuumFreezerRecipes);

        // --- Stage 4: regenerable resin purification (all fluids) ---
        // PRIME — make fresh resin from scratch (no loop needed, so the cycle is start-able).
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(fluid(Materials.PhosphoricAcid, 1000), molten(Materials.Polytetrafluoroethylene, 1000))
            .fluidOutputs(PrPMaterials.PromethiumResin.getFluidOrGas(1000))
            .duration(12 * 20)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // LOAD — resin grabs the promethium; samarium falls out as raffinate (the Pm-147 decay product).
        GTValues.RA.stdBuilder()
            .fluidInputs(PrPMaterials.RawPromethium.getFluidOrGas(144), PrPMaterials.PromethiumResin.getFluidOrGas(1000))
            .itemOutputs(dust(Materials.Samarium, 1))
            .fluidOutputs(PrPMaterials.LoadedPromethiumResin.getFluidOrGas(1000))
            .duration(10 * 20)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // STRIP + REGENERATE — acid elutes pure promethium as a molten fluid and hands the resin back.
        GTValues.RA.stdBuilder()
            .fluidInputs(PrPMaterials.LoadedPromethiumResin.getFluidOrGas(1000), fluid(Materials.HydrochloricAcid, 1000))
            .fluidOutputs(Materials.Promethium.getMolten(144), PrPMaterials.PromethiumResin.getFluidOrGas(1000))
            .duration(10 * 20)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // Heavy-water → cold-helium branch. Heavy water electrolyses to deuterium (feeding GT's own
    // D-T → helium-plasma fusion); that helium plasma is then cooled — using heavy water as coolant —
    // in the Vacuum Freezer into cold helium, which freezes the promethium plasma in Stage 3.
    // GT cools helium plasma only via the (hardcoded) Extreme Heat Exchanger, so there's no recipe
    // to remove — this IS the recipe-based cooling.
    // =========================================================
    private static void heavyWaterHeliumBranch() {

        // Enrich heavy water from ordinary water (distillation — D₂O is rare, hence the low yield).
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(Materials.Water, 2000))
            .fluidOutputs(PrPMaterials.HeavyWater.getFluidOrGas(100))
            .duration(20 * 20)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.distilleryRecipes);

        // Heavy water electrolysis → deuterium (+ oxygen), feedstock for D-T → He plasma fusion.
        GTValues.RA.stdBuilder()
            .fluidInputs(PrPMaterials.HeavyWater.getFluidOrGas(1000))
            .fluidOutputs(Materials.Deuterium.getGas(1000), Materials.Oxygen.getGas(500))
            .duration(10 * 20)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.electrolyzerRecipes);

        // Cool helium plasma with heavy water → cold helium (the Stage 3 coolant).
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Helium.getPlasma(1000), PrPMaterials.HeavyWater.getFluidOrGas(1000))
            .fluidOutputs(Materials.Helium.getGas(1000))
            .duration(10 * 20)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.vacuumFreezerRecipes);
    }
}
