package com.gtnh.processingplus.recipes.chains.photoresist;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeBuilder.TICKS;

import java.util.Collection;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.GTNHProcessingPlus;
import com.gtnh.processingplus.machines.spc.MachineType;
import com.gtnh.processingplus.machines.spc.SPCModuleType;
import com.gtnh.processingplus.machines.spc.SPCRecipeData;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;
import com.gtnh.processingplus.recipes.RecipeGuard;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeConstants;
import gtPlusPlus.core.material.MaterialMisc;

public class PhotoresistRecipes {

    // SPC station-sequence min tier: simple chemistry, low-tier machines are fine (MV = 2)
    private static final int MV = 2;

    // Method and comment prefixes like "MV:" or "UV:" name the photoresist STAGE a recipe belongs to, not the
    // machine voltage tier it runs at (see each recipe's eut for that).
    public static void init() {
        RecipeGuard.run("Photoresist MV to LuV", PhotoresistRecipes::initBaseTiers);
        RecipeGuard.run("Photoresist ZPM", PhotoresistRecipes::initZpm);
        RecipeGuard.run("Photoresist UV", PhotoresistRecipes::initUv);
        tryInit("UHV photoresist", PhotoresistRecipes::initUhv);
        tryInit("UEV photoresist", PhotoresistRecipes::initUev);
        tryInit("UIV photoresist", PhotoresistRecipes::initUiv);
        tryInit("UMV photoresist", PhotoresistRecipes::initUmv);
    }

    private static void tryInit(String name, Runnable r) {
        try {
            r.run();
        } catch (IllegalStateException e) {
            GTNHProcessingPlus.LOG.warn("Skipping {} recipes: {}", name, e.getMessage());
        }
    }

    private static void initBaseTiers() {
        // MV
        mvFormaldehydeSynthesis();
        mvNovolacSynthesis();
        mvBenzeneSensitizer();
        mvTanninSensitizer();
        mvBasicBlend();
        // HV
        hvNaphthaleneSensitizer();
        hvAnthraceneSensitizer();
        hvBenzeneSensitizer();
        hvAdvancedBlend();
        // EV
        evAcetoxystyrene();
        evPHSResin();
        evBariumOxideSynthesis();
        evBariumPeroxideSynthesis();
        evHydrogenPeroxidePrimitive();
        evBariumWasteWaterDistillation();
        evBariumOxideElectrolysis();
        evBariumPeroxideElectrolysis();
        evBariumChlorideElectrolysis();
        evPHSResinPrimitive();
        evSulfurDichloride();
        evDiphenylsulfoniumSalt();
        evEVBlend();
        // IV
        ivFurfural();
        ivDihydropyran();
        ivTHPProtection();
        ivIVBlendEV();
        // LuV stage: Triflic Acid sub-chain (reused through UMV)
        luvTrifluoromethane();
        luvSulfurTrioxide();
        luvTriflicAcid();
        // LuV stage: main chain
        luvAdamantolSynthesis();
        luvMethacrylicAcid();
        luvAdamantylMethacrylate();
        luvAcetoneAzine();
        luvAIBN();
        luvAlicyclicResin();
        luvTriphenylsulfoniumTriflate();
        luvPropyleneOxide();
        luvPGME();
        luvPGMEA();
        luvLuVBlend();
        // Byproduct recycling
        luvAmmoniumBisulfateCracking();
    }

    // MV stage: Formaldehyde: Ethanol + O₂
    private static void mvFormaldehydeSynthesis() {
        GTValues.RA.stdBuilder()
            // O2 as a cell so it fits the single-block CR at MV; UniversalChemical makes the LCR copy.
            .itemInputs(Materials.Oxygen.getCells(1), circuit(1))
            .fluidInputs(fluid(Materials.Ethanol, 1000))
            .itemOutputs(ItemList.Cell_Empty.get(1))
            .fluidOutputs(fluid("fluid.formaldehyde", 1000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // MV stage: Novolac Resin: Phenol + Formaldehyde + H₂SO₄ (cat)
    private static void mvNovolacSynthesis() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(
                fluid(Materials.Phenol, 2000),
                fluid("fluid.formaldehyde", 1000),
                fluid(Materials.SulfuricAcid, 100))
            .fluidOutputs(molten(PrPMaterials.NovolacResin, 1000), fluid(Materials.Water, 1000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // MV stage: Sensitizer route A: Benzene + O₂
    private static void mvBenzeneSensitizer() {
        GTValues.RA.stdBuilder()
            // Benzene as a cell so it fits the single-block CR at MV (O2 stays the one fluid).
            .itemInputs(Materials.Benzene.getCells(1), circuit(3))
            .fluidInputs(fluid(Materials.Oxygen, 2000))
            .itemOutputs(ItemList.Cell_Empty.get(1))
            .fluidOutputs(fluid(PrPMaterials.MVPhotoresistSensitizer, 1000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // MV stage: Sensitizer route B: Wood → Tannin Solution → Sensitizer (Distillery)
    private static void mvTanninSensitizer() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Wood, 4), circuit(4))
            .fluidInputs(fluid(Materials.Water, 2000))
            .fluidOutputs(fluid(PrPMaterials.TanninSolution, 2000))
            .duration(4 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.chemicalBathRecipes);

        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.TanninSolution, 2000))
            .fluidOutputs(fluid(PrPMaterials.MVPhotoresistSensitizer, 1000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.distilleryRecipes);
    }

    // MV stage: Basic Photoresist blend: Novolac + Sensitizer + Ethanol (multiblock Mixer and single-block Mixer)
    private static void mvBasicBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(5))
            .fluidInputs(
                molten(PrPMaterials.NovolacResin, 576),
                fluid(PrPMaterials.MVPhotoresistSensitizer, 6000),
                fluid(Materials.Ethanol, 1000))
            .fluidOutputs(fluid(PrPMaterials.BasicPhotoresist, 3000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.mixerNonCellRecipes);

        // Single-block Mixer has one fluid slot: sensitizer and ethanol go in as cells, the molten Novolac stays the
        // fluid.
        GTValues.RA.stdBuilder()
            .itemInputs(cell(PrPMaterials.MVPhotoresistSensitizer, 6), Materials.Ethanol.getCells(1), circuit(5))
            .fluidInputs(molten(PrPMaterials.NovolacResin, 576))
            .itemOutputs(ItemList.Cell_Empty.get(7))
            .fluidOutputs(fluid(PrPMaterials.BasicPhotoresist, 3000))
            .duration(6 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.mixerRecipes);
    }

    // HV stage: Sensitizer route A: Naphthalene + H₂SO₄
    private static void hvNaphthaleneSensitizer() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid("fluid.naphthalene", 1000), fluid(Materials.SulfuricAcid, 500))
            .fluidOutputs(fluid(PrPMaterials.HVPhotoresistSensitizer, 1000))
            .duration(15 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // HV stage: Sensitizer route B: Anthracene + HNO₃
    private static void hvAnthraceneSensitizer() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid("fluid.anthracene", 1000), fluid(Materials.NitricAcid, 500))
            .fluidOutputs(fluid(PrPMaterials.HVPhotoresistSensitizer, 1000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // HV stage: Sensitizer route C: Benzene + H₂O₂ + HNO₃
    private static void hvBenzeneSensitizer() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(
                fluid(Materials.Benzene, 1000),
                fluid("fluid.hydrogenperoxide", 500),
                fluid(Materials.NitricAcid, 500))
            .fluidOutputs(
                fluid(PrPMaterials.HVPhotoresistSensitizer, 2000),
                fluid(Materials.Water, 500),
                fluid(Materials.NitrousOxide, 250))
            .duration(8 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // HV stage: Advanced Photoresist blend: Basic + HV Sensitizer (multiblock Mixer and single-block Mixer)
    private static void hvAdvancedBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(fluid(PrPMaterials.BasicPhotoresist, 10000), fluid(PrPMaterials.HVPhotoresistSensitizer, 2000))
            .fluidOutputs(fluid(PrPMaterials.AdvancedPhotoresist, 5000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.mixerNonCellRecipes);

        // Single-block Mixer: Basic Photoresist goes in as cells.
        GTValues.RA.stdBuilder()
            .itemInputs(cell(PrPMaterials.BasicPhotoresist, 10), circuit(4))
            .fluidInputs(fluid(PrPMaterials.HVPhotoresistSensitizer, 2000))
            .itemOutputs(ItemList.Cell_Empty.get(10))
            .fluidOutputs(fluid(PrPMaterials.AdvancedPhotoresist, 5000))
            .duration(6 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.mixerRecipes);
    }

    // EV stage: Acetoxystyrene: Styrene + Acetic Anhydride
    private static void evAcetoxystyrene() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(Materials.Styrene, 1000), fluid("molten.aceticanhydride", 1000))
            .fluidOutputs(fluid(PrPMaterials.Acetoxystyrene, 1000))
            .duration(4 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // EV stage: PHS Resin: Acetoxystyrene + H₂O₂ + HCl (cat)
    private static void evPHSResin() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(
                fluid(PrPMaterials.Acetoxystyrene, 2000),
                fluid("fluid.hydrogenperoxide", 1000),
                fluid(Materials.HydrochloricAcid, 100))
            .fluidOutputs(molten(PrPMaterials.PHSResin, 144 * 9), fluid(Materials.AceticAcid, 2000))
            .duration(150 * TICKS)
            .metadata(GTRecipeConstants.COIL_HEAT, 4500)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // EV stage: PHS Resin (primitive route): Acetoxystyrene + Impure H₂O₂ + HCl (cat); lower yield than the
    // clean-H₂O₂ route since the crude peroxide brings contaminants along with it.
    private static void evPHSResinPrimitive() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(
                fluid(PrPMaterials.Acetoxystyrene, 2000),
                fluid(PrPMaterials.ImpureHydrogenPeroxide, 1000),
                fluid(Materials.HydrochloricAcid, 100))
            .fluidOutputs(molten(PrPMaterials.PHSResin, 144 * 3), fluid(Materials.AceticAcid, 2000))
            .duration(25 * SECONDS)
            .metadata(GTRecipeConstants.COIL_HEAT, 4500)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    private static void evBariumOxideSynthesis() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Barium, 1), circuit(1))
            .fluidInputs(fluid(Materials.Oxygen, 1000))
            .itemOutputs(dust(PrPMaterials.BariumOxide, 2))
            .duration(40 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // EV stage: Barium Peroxide: 2 BaO + O₂ ⇌ 2 BaO₂ (Brin process)
    private static void evBariumPeroxideSynthesis() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.BariumOxide, 4))
            .fluidInputs(fluid(Materials.Oxygen, 2000))
            .itemOutputs(dust(PrPMaterials.BariumPeroxide, 6))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // EV stage: Impure Hydrogen Peroxide: Barium Peroxide + 2 HCl → BaCl₂ (waste) + H₂O₂ (impure)
    // Crude acid-digestion route: no clean water source needed, but the barium ends up dissolved in the output
    // therefore being impure
    private static void evHydrogenPeroxidePrimitive() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.BariumPeroxide, 3))
            .fluidInputs(fluid(Materials.HydrochloricAcid, 2000))
            .fluidOutputs(
                fluid(PrPMaterials.ImpureHydrogenPeroxide, 1000),
                fluid(PrPMaterials.BariumRichWasteWater, 1000))
            .duration(150 * TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // EV stage: Barium-Rich Waste Water reclamation (Distillery), recovers the dissolved BaCl₂ instead of
    // just dumping the wastewater. TODO: possibly a third output here once we know what else is
    // dissolved in it besides BaCl2.
    private static void evBariumWasteWaterDistillation() {
        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.BariumRichWasteWater, 1000))
            .itemOutputs(dust(PrPMaterials.BariumChloride, 3))
            .fluidOutputs(fluid(Materials.Water, 1000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.distilleryRecipes);
    }

    // EV stage: Barium Oxide electrolysis: 2 BaO → 2 Ba + O₂ (reverse of evBariumOxideSynthesis)
    private static void evBariumOxideElectrolysis() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.BariumOxide, 2))
            .itemOutputs(dust(Materials.Barium, 1))
            .fluidOutputs(fluid(Materials.Oxygen, 1000))
            .duration(6 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.electrolyzerRecipes);
    }

    // EV stage: Barium Peroxide electrolysis: BaO₂ → Ba + O₂ (straight to Barium; BaO₂ carries twice the
    // oxygen per Ba that BaO does, hence double the O₂ yield vs evBariumOxideElectrolysis)
    private static void evBariumPeroxideElectrolysis() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.BariumPeroxide, 3))
            .itemOutputs(dust(Materials.Barium, 1))
            .fluidOutputs(fluid(Materials.Oxygen, 2000))
            .duration(6 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.electrolyzerRecipes);
    }

    // EV stage: Barium Chloride electrolysis (molten-salt/Downs-process style), BaCl₂ → Ba + Cl₂
    private static void evBariumChlorideElectrolysis() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.BariumChloride, 3))
            .itemOutputs(dust(Materials.Barium, 1))
            .fluidOutputs(fluid(Materials.Chlorine, 2000))
            .duration(6 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.electrolyzerRecipes);
    }

    // EV stage: Sulfur Dichloride (PAG precursor): S + Cl₂
    private static void evSulfurDichloride() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Sulfur, 1))
            .fluidInputs(fluid(Materials.Chlorine, 2000))
            .fluidOutputs(fluid(PrPMaterials.SulfurDichloride, 1000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(GTRecipeConstants.UniversalChemical);
    }

    // EV stage: Diphenylsulfonium Salt (PAG): SCl₂ + 2 Benzene
    private static void evDiphenylsulfoniumSalt() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(PrPMaterials.SulfurDichloride, 1000), fluid(Materials.Benzene, 2000))
            .itemOutputs(dust(PrPMaterials.DiphenylsulfoniumSalt, 2))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 2000))
            .duration(4 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // EV stage: EV Photoresist blend: Advanced + PHS Resin + PAG (multiblock Mixer and single-block Mixer)
    private static void evEVBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.DiphenylsulfoniumSalt, 4), circuit(5))
            .fluidInputs(molten(PrPMaterials.PHSResin, 1152), fluid(PrPMaterials.AdvancedPhotoresist, 8000))
            .fluidOutputs(fluid(PrPMaterials.EVPhotoresist, 7000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.mixerNonCellRecipes);

        // Single-block Mixer: Advanced Photoresist goes in as cells (8 = 8000 mB), molten PHS Resin stays the fluid.
        GTValues.RA.stdBuilder()
            .itemInputs(
                dust(PrPMaterials.DiphenylsulfoniumSalt, 4),
                cell(PrPMaterials.AdvancedPhotoresist, 8),
                circuit(5))
            .fluidInputs(molten(PrPMaterials.PHSResin, 1152))
            .itemOutputs(ItemList.Cell_Empty.get(8))
            .fluidOutputs(fluid(PrPMaterials.EVPhotoresist, 7000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.mixerRecipes);
    }

    // IV stage: Furfural: Wheat + H₂SO₄
    private static void ivFurfural() {
        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.wheat, 4), circuit(1))
            .fluidInputs(fluid(Materials.SulfuricAcid, 500))
            .fluidOutputs(fluid(PrPMaterials.Furfural, 1000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // IV stage: Dihydropyran: Furfural pyrolysis
    private static void ivDihydropyran() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(PrPMaterials.Furfural, 1000))
            .fluidOutputs(fluid(PrPMaterials.Dihydropyran, 1000))
            .duration(4 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .metadata(GTRecipeConstants.COIL_HEAT, 1800)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // IV stage: THP-Protected PHS: PHS Resin + Dihydropyran + HCl (cat)
    private static void ivTHPProtection() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(
                molten(PrPMaterials.PHSResin, 288),
                fluid(PrPMaterials.Dihydropyran, 1000),
                fluid(Materials.HydrochloricAcid, 50))
            .fluidOutputs(fluid(PrPMaterials.THPProtectedPHS, 1000))
            .duration(6 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .metadata(GTRecipeConstants.COIL_HEAT, 4200)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // IV stage: IV Photoresist blend: EV Photoresist + THP-PHS (multiblock Mixer, and a single-block Mixer recipe
    // where EVPhotoresist goes in as cells (4 = 4000 mB) and THP-PHS stays the fluid).
    private static void ivIVBlendEV() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(fluid(PrPMaterials.EVPhotoresist, 4000), fluid(PrPMaterials.THPProtectedPHS, 1000))
            .fluidOutputs(fluid(PrPMaterials.IVPhotoresist, 2000))
            .duration(4 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.mixerNonCellRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(cell(PrPMaterials.EVPhotoresist, 4), circuit(4))
            .fluidInputs(fluid(PrPMaterials.THPProtectedPHS, 1000))
            .itemOutputs(ItemList.Cell_Empty.get(4))
            .fluidOutputs(fluid(PrPMaterials.IVPhotoresist, 2000))
            .duration(4 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.mixerRecipes);
    }

    // LuV stage: Trifluoromethane: CHCl₃ + 3 HF (gates Triflic Acid)
    private static void luvTrifluoromethane() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(Materials.Chloroform, 1000), fluid(Materials.HydrofluoricAcid, 3000))
            .fluidOutputs(fluid(PrPMaterials.Trifluoromethane, 1000))
            .duration(4 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // LuV stage: SO₃: 2 SO₂ + O₂
    private static void luvSulfurTrioxide() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(Materials.SulfurDioxide, 2000), fluid(Materials.Oxygen, 1000))
            .fluidOutputs(fluid(Materials.SulfurTrioxide, 2000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV stage: Triflic Acid: CHF₃ + SO₃
    private static void luvTriflicAcid() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(PrPMaterials.Trifluoromethane, 1000), fluid(Materials.SulfurTrioxide, 1000))
            .fluidOutputs(fluid(PrPMaterials.TriflicAcid, 1000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // LuV stage: Adamantol (Adamantium gate): Adamantium + HF + H₂SO₄
    private static void luvAdamantolSynthesis() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Adamantium, 1))
            .fluidInputs(fluid(Materials.HydrofluoricAcid, 2000), fluid(Materials.SulfuricAcid, 500))
            .itemOutputs(dust(PrPMaterials.Adamantol, 2))
            .duration(150 * TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // LuV stage: Methacrylic Acid: Acetone + HCN + H₂SO₄; byproduces Ammonium Bisulfate
    private static void luvMethacrylicAcid() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(
                fluid(Materials.Acetone, 1000),
                fluid("hydrogencyanide", 1000),
                fluid(Materials.SulfuricAcid, 1000))
            .itemOutputs(ammoniumBisulfateDust(2))
            .fluidOutputs(fluid(PrPMaterials.MethacrylicAcid, 1000))
            .duration(6 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // LuV stage: Adamantyl Methacrylate: Methacrylic Acid + Adamantol
    private static void luvAdamantylMethacrylate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.Adamantol, 1), circuit(5))
            .fluidInputs(fluid(PrPMaterials.MethacrylicAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.AdamantylMethacrylate, 1000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .metadata(GTRecipeConstants.COIL_HEAT, 4500 + 400)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // LuV stage: Acetone Azine (AIBN precursor): Acetone + Hydrazine
    private static void luvAcetoneAzine() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(6))
            .fluidInputs(fluid(Materials.Acetone, 2000), fluid("fluid.hydrazine", 1000))
            .fluidOutputs(fluid(PrPMaterials.AcetoneAzine, 1000))
            .duration(4 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // LuV stage: AIBN radical initiator: Acetone Azine + HCN + Cl₂
    private static void luvAIBN() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(7))
            .fluidInputs(
                fluid(PrPMaterials.AcetoneAzine, 1000),
                fluid("hydrogencyanide", 2000),
                fluid(Materials.Chlorine, 2000))
            .itemOutputs(dust(PrPMaterials.AIBN, 4))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 2000))
            .duration(6 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // LuV stage: Alicyclic Resin: AdMA + MAA + AIBN + N₂ polymerization
    private static void luvAlicyclicResin() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.AIBN, 1), circuit(8))
            .fluidInputs(
                fluid(PrPMaterials.AdamantylMethacrylate, 1000),
                fluid(PrPMaterials.MethacrylicAcid, 1000),
                fluid(Materials.Nitrogen, 2000))
            .fluidOutputs(molten(PrPMaterials.AlicyclicResin, 576))
            .duration(150 * TICKS)
            .eut(TierEU.RECIPE_LuV)
            .metadata(GTRecipeConstants.COIL_HEAT, 3600)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // LuV stage: Triphenylsulfonium Triflate (stronger PAG): Diphenylsulfonium + TriflicAcid + Benzene
    private static void luvTriphenylsulfoniumTriflate() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.DiphenylsulfoniumSalt, 1), circuit(9))
            .fluidInputs(fluid(PrPMaterials.TriflicAcid, 1000), fluid(Materials.Benzene, 1000))
            .itemOutputs(dust(PrPMaterials.TriphenylsulfoniumTriflate, 2))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 1000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_REACTOR, MachineType.MIXER, MachineType.CHEMICAL_REACTOR,
                MachineType.MIXER, MachineType.CHEMICAL_BATH },
            new int[] { 3, 4, 3, 4, 5 });
    }

    // LuV stage: Propylene Oxide (PGME precursor): Propylene + H₂O₂
    private static void luvPropyleneOxide() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(Materials.Propene, 1000), fluid("fluid.hydrogenperoxide", 1000))
            .fluidOutputs(fluid(PrPMaterials.PropyleneOxide, 1000), fluid(Materials.Water, 1000))
            .duration(4 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // LuV stage: PGME solvent: Propylene Oxide + Methanol
    private static void luvPGME() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(10))
            .fluidInputs(fluid(PrPMaterials.PropyleneOxide, 1000), fluid(Materials.Methanol, 1000))
            .fluidOutputs(fluid(PrPMaterials.PGME, 1000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // LuV stage: PGMEA solvent: PGME + Acetic Acid
    private static void luvPGMEA() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(11))
            .fluidInputs(fluid(PrPMaterials.PGME, 1000), fluid(Materials.AceticAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.PGMEA, 1000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // LuV stage: LuV Photoresist blend: IV + Alicyclic Resin + PAG + PGMEA (Mixer)
    private static void luvLuVBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.TriphenylsulfoniumTriflate, 1), circuit(13))
            .fluidInputs(
                molten(PrPMaterials.AlicyclicResin, 576),
                fluid(PrPMaterials.IVPhotoresist, 4250),
                fluid(PrPMaterials.PGMEA, 1050))
            .fluidOutputs(fluid(PrPMaterials.LuVPhotoresist, 8000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.mixerNonCellRecipes);
    }

    // LuV stage: Ammonium Bisulfate cracking: recovers H₂SO₄ + NH₃ (byproduct of MethacrylicAcid)
    private static void luvAmmoniumBisulfateCracking() {
        GTValues.RA.stdBuilder()
            .itemInputs(ammoniumBisulfateDust(2))
            .fluidOutputs(fluid(Materials.SulfuricAcid, 1000), fluid(Materials.Ammonia, 1000))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.distillationTowerRecipes);
    }

    private static ItemStack ammoniumBisulfateDust(int amount) {
        return dust(PrPMaterials.AmmoniumBisulfate, amount);
    }

    private static void initZpm() {
        zpmTelluriumMolybdenumOxides();
        zpmHexafluoroacetone();
        zpmHFIMAMonomer();
        zpmGBLMAMonomer();
        zpmHAdMAMonomer();
        zpmArFCopolymerResin();
        zpmZPMBlend();
    }

    // ZPM stage: Tellurium and Molybdenum dioxide (GT has no direct recipe for these), roasted in the HTRF
    private static void zpmTelluriumMolybdenumOxides() {
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Tellurium.getDust(1), circuit(24))
            .fluidInputs(fluid(Materials.Oxygen, 2000))
            .itemOutputs(item("dustTellurium(IV)Oxide", 3))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .metadata(GTRecipeConstants.COIL_HEAT, 722)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Molybdenum.getDust(1), circuit(24))
            .fluidInputs(fluid(Materials.Oxygen, 2000))
            .itemOutputs(item("dustMolybdenum(IV)Oxide", 3))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .metadata(GTRecipeConstants.COIL_HEAT, 722)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // ZPM stage: Hexafluoroacetone
    // 2 CHF₃ + ½O₂ → (CF₃)₂CO + H₂O
    // Trifluoromethane reused from LuV Triflic Acid sub-chain
    // =========================================================
    private static void zpmHexafluoroacetone() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(PrPMaterials.Trifluoromethane, 2000), fluid(Materials.Oxygen, 1000))
            .fluidOutputs(fluid(PrPMaterials.Hexafluoroacetone, 4000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .metadata(GTRecipeConstants.COIL_HEAT, 2700)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // ZPM stage: HFIMA Monomer (hexafluoroisopropyl methacrylate)
    // (CF₃)₂CO + MethacrylicAcid → HFIMA + H₂O
    // MethacrylicAcid reused from LuV chain
    // =========================================================
    private static void zpmHFIMAMonomer() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(PrPMaterials.Hexafluoroacetone, 1000), fluid(PrPMaterials.MethacrylicAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.HFIMAMonomer, 2000))
            .duration(4 * SECONDS)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // =========================================================
    // ZPM stage: GBLMA Monomer (gamma-butyrolactone methacrylate)
    // GBL + MethacrylicAcid → GBLMA + H₂O
    // GBL = solvent chemistry gate
    // =========================================================
    private static void zpmGBLMAMonomer() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(Materials.GammaButyrolactone, 1000), fluid(PrPMaterials.MethacrylicAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.GBLMAMonomer, 2000))
            .duration(4 * SECONDS)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // =========================================================
    // ZPM stage: HAdMA Monomer (hydroxy-adamantyl methacrylate)
    // Adamantol + Hexafluoroacetone + MethacrylicAcid → HAdMA + H₂O
    // Adamantol gate reused from LuV Naquadah processing
    // =========================================================
    private static void zpmHAdMAMonomer() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.Adamantol, 1), circuit(4))
            .fluidInputs(fluid(PrPMaterials.Hexafluoroacetone, 500), fluid(PrPMaterials.MethacrylicAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.HAdMAMonomer, 2000))
            .duration(4 * SECONDS)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // =========================================================
    // ZPM stage: ArF Copolymer Resin
    // HFIMA + GBLMA + HAdMA + AIBN (cat, reused from LuV) + N₂ → ArF Copolymer Resin
    // Radical polymerization under inert atmosphere
    // =========================================================
    private static void zpmArFCopolymerResin() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.AIBN, 1), circuit(5))
            .fluidInputs(
                fluid(PrPMaterials.HFIMAMonomer, 1000),
                fluid(PrPMaterials.GBLMAMonomer, 1000),
                fluid(PrPMaterials.HAdMAMonomer, 1000))
            .fluidOutputs(molten(PrPMaterials.ArFCopolymerResin, 1000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_ZPM)
            .metadata(GTRecipeConstants.COIL_HEAT, 3600)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // ZPM stage: ZPM Photoresist blend (Mixer, circuit 14)
    // LuV Photoresist + ArF Resin + Triphenylsulfonium Triflate + PGMEA → ZPM Photoresist
    // PGMEA and TriphenylsulfoniumTriflate run continuously from LuV through UMV
    // =========================================================
    private static void zpmZPMBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.TriphenylsulfoniumTriflate, 2), circuit(14))
            .fluidInputs(
                fluid(PrPMaterials.LuVPhotoresist, 1200),
                molten(PrPMaterials.ArFCopolymerResin, 720),
                fluid(PrPMaterials.PGMEA, 1200))
            .fluidOutputs(fluid(PrPMaterials.ZPMPhotoresist, 3000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.mixerNonCellRecipes);
    }

    private static void initUv() {
        uvTinOxoAcetateCluster();
        uvErbiumTriflate();
        uvYtterbiumAcetate();
        uvTerbiumChloride();
        uvTerbiumAcetylacetonate();
        uvDysprosiumDopedCalciumFluoride();
        uvREDopedPhotoresistMatrix();
        uvUVBlend();
    }

    // =========================================================
    // UV stage: Tin Oxo-Acetate Cluster (EUV sensitizer precursor)
    // Sn + AceticAcid + O₂ → TinOxoAcetateCluster + H₂O
    // Light-isolated in SPC; tin organometallics are UV-sensitive
    // =========================================================
    private static void uvTinOxoAcetateCluster() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Tin, 1), circuit(1))
            .fluidInputs(fluid(Materials.AceticAcid, 2000), fluid(Materials.Oxygen, 2000))
            .fluidOutputs(fluid(PrPMaterials.TinOxoAcetateCluster, 1000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.MIXER, MachineType.CHEMICAL_BATH },
            new int[] { MV, MV });
    }

    // =========================================================
    // UV stage: Erbium Triflate
    // Er + TriflicAcid + O₂ → ErbiumTriflate + H₂O
    // RE triflate dopant for photoactive matrix
    // =========================================================
    private static void uvErbiumTriflate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(mat("Erbium"), 2), circuit(2))
            .fluidInputs(fluid(PrPMaterials.TriflicAcid, 4000), fluid(Materials.Oxygen, 2000))
            .itemOutputs(dust(PrPMaterials.ErbiumTriflate, 6))
            .fluidOutputs(fluid(Materials.Water, 1000))
            .duration(4 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // =========================================================
    // UV stage: Ytterbium Acetate
    // Yb + AceticAcid → YtterbiumAcetate + H₂O
    // =========================================================
    private static void uvYtterbiumAcetate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(mat("Ytterbium"), 1), circuit(3))
            .fluidInputs(fluid(Materials.AceticAcid, 3000))
            .itemOutputs(dust(PrPMaterials.YtterbiumAcetate, 4))
            .fluidOutputs(fluid(Materials.Water, 1000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // =========================================================
    // UV stage: Terbium Chloride
    // Tb + HCl → TerbiumChloride + H₂O
    // =========================================================
    private static void uvTerbiumChloride() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(mat("Terbium"), 1), circuit(4))
            .fluidInputs(fluid(Materials.HydrochloricAcid, 3000))
            .itemOutputs(dust(PrPMaterials.TerbiumChloride, 4))
            .fluidOutputs(fluid(Materials.Water, 1000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // =========================================================
    // UV stage: Terbium Acetylacetonate
    // TerbiumChloride + Acetone + AceticAcid → TerbiumAcetylacetonate + HCl
    // Acetylacetonate ligand formed in situ
    // =========================================================
    private static void uvTerbiumAcetylacetonate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.TerbiumChloride, 1), circuit(5))
            .fluidInputs(fluid(Materials.Acetone, 1000), fluid(Materials.AceticAcid, 1000))
            .itemOutputs(dust(PrPMaterials.TerbiumAcetylacetonate, 3))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 1000))
            .duration(4 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // =========================================================
    // UV stage: Dysprosium-Doped Calcium Fluoride (hot press sintering)
    // Dy + Ca + HF → CaF₂:Dy + H₂O
    // Argon atmosphere prevents oxide formation
    // =========================================================
    private static void uvDysprosiumDopedCalciumFluoride() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(mat("Dysprosium"), 1), dust(Materials.Calcium, 1))
            .fluidInputs(fluid(Materials.HydrofluoricAcid, 4000), fluid(Materials.Argon, 1000))
            .itemOutputs(dust(PrPMaterials.DysprosiumDopedCalciumFluoride, 6))
            .duration(6 * SECONDS)
            .metadata(GTRecipeConstants.COIL_HEAT, 5000)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // =========================================================
    // UV stage: RE-Doped Photoresist Matrix (CIDC)
    // TinOxoAcetate + Er/Yb/Tb/Dy dopants + TriflicAcid → RE-Doped Matrix
    // Controlled Isotopic Doping Chamber assembles the rare-earth composite
    // =========================================================
    private static void uvREDopedPhotoresistMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                dust(PrPMaterials.ErbiumTriflate, 1),
                dust(PrPMaterials.YtterbiumAcetate, 1),
                dust(PrPMaterials.TerbiumAcetylacetonate, 1),
                dust(PrPMaterials.DysprosiumDopedCalciumFluoride, 1))
            .fluidInputs(fluid(PrPMaterials.TinOxoAcetateCluster, 1000), fluid(PrPMaterials.TriflicAcid, 500))
            .itemOutputs(dust(PrPMaterials.REDopedPhotoresistMatrix, 6))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sCIDCRecipes);
    }

    // =========================================================
    // UV stage: UV Photoresist blend (Mixer, circuit 15)
    // ZPM Photoresist + RE-Doped Matrix + PGMEA → UV Photoresist
    // =========================================================
    private static void uvUVBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.REDopedPhotoresistMatrix, 4), circuit(15))
            .fluidInputs(fluid(PrPMaterials.ZPMPhotoresist, 1000), fluid(PrPMaterials.PGMEA, 1000))
            .fluidOutputs(fluid(PrPMaterials.UVPhotoresist, 2000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.mixerNonCellRecipes);
    }

    private static void initUhv() {
        uhvBioRefinedIntermediate();
        uhvRadoxXenoxeneMatrix();
        uhvLivingSolderAcetate();
        uhvPhotoresistMatrix();
        uhvBlend();
    }

    // =========================================================
    // UHV stage: Bio-Refined Intermediate
    // Mutagen + Unknown Liquid → BioRefinedIntermediate
    // HPR simultaneous liquid/plasma chemistry unlocks exotic bio-matrix
    // =========================================================
    private static void uhvBioRefinedIntermediate() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            // .fluidInputs(fluid("mutagen", 1000), fluid(Materials.Xenoxene, 500))
            .fluidOutputs(fluid(PrPMaterials.BioRefinedIntermediate, 1000))
            .duration(6 * SECONDS)
            .eut(TierEU.RECIPE_UV)
            .specialValue(1 | (1 << 4) | (0 << 8)) // coil=Tier I, freqTag=1, freqRequired=0
            .addTo(GTNHPPRecipeMaps.sHPRRecipes);
    }

    // =========================================================
    // UHV stage: Radox-Xenoxene Matrix
    // BioRefinedIntermediate + Radox Polymer + Xenoxene → RadoxXenoxeneMatrix
    // HPR: high-pressure plasma conditions force exotic polymer crosslinking
    // =========================================================
    private static void uhvRadoxXenoxeneMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(
                fluid(PrPMaterials.BioRefinedIntermediate, 2000),
                molten(Materials.RadoxPolymer, 288),
                fluid(Materials.Xenoxene, 250))
            .fluidOutputs(fluid(PrPMaterials.RadoxXenoxeneMatrix, 2500))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_UHV)
            .specialValue(2 | (2 << 4) | (1 << 8)) // coil=Tier II, freqTag=2, freqRequired=1
            .addTo(GTNHPPRecipeMaps.sHPRRecipes);
    }

    // =========================================================
    // UHV stage: Living Solder Acetate
    // Living Solder + AceticAcid → LivingSolderAcetate + H₂O
    // Acetate ligand substitution: stabilizes living solder for photoresist use
    // =========================================================
    private static void uhvLivingSolderAcetate() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(MaterialMisc.MUTATED_LIVING_SOLDER, 1000), fluid(Materials.AceticAcid, 500))
            .fluidOutputs(fluid(PrPMaterials.LivingSolderAcetate, 1000))
            .duration(4 * SECONDS)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // =========================================================
    // UHV stage: UHV Photoresist Matrix
    // RadoxXenoxeneMatrix + LivingSolderAcetate + Grade 6 Water → UHVPhotoresistMatrix
    // HPR: plasma-assisted matrix assembly under ultra-pure conditions
    // =========================================================
    private static void uhvPhotoresistMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(
                fluid(PrPMaterials.RadoxXenoxeneMatrix, 500),
                fluid(PrPMaterials.LivingSolderAcetate, 500),
                fluid(Materials.Grade6PurifiedWater, 500))
            .fluidOutputs(fluid(PrPMaterials.UHVPhotoresistMatrix, 100))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_UHV)
            .specialValue(2 | (2 << 4) | (1 << 8)) // coil=Tier II, freqTag=2, freqRequired=1
            .addTo(GTNHPPRecipeMaps.sHPRRecipes);
    }

    // =========================================================
    // UHV stage: UHV Photoresist blend (Mixer, circuit 16)
    // UV Photoresist + UHV Matrix + PGMEA + Triflic Acid → UHV Photoresist
    // =========================================================
    private static void uhvBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(16))
            .fluidInputs(
                fluid(PrPMaterials.UVPhotoresist, 12000),
                fluid(PrPMaterials.UHVPhotoresistMatrix, 6000),
                fluid(PrPMaterials.PGMEA, 3000),
                fluid(PrPMaterials.TriflicAcid, 1200))
            .fluidOutputs(fluid(PrPMaterials.UHVPhotoresist, 3000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.mixerNonCellRecipes);
    }

    private static void initUev() {
        uevTengamTriflate();
        uevActivatedNaquadria();
        uevHypogenQuantumMatrix();
        uevFermiumTriflate();
        uevQuantumPrimedIntermediate();
        uevBeamActivation();
        uevNaquadriaLoaded();
        uevQuantumCascadeMatrix();
        uevPurification();
        uevBlend();
    }

    // =========================================================
    // UEV stage: Tengam Triflate
    // Tengam + TriflicAcid → TengamTriflate + H₂O
    // Exotic triflate salt; runs continuously through UMV
    // =========================================================
    private static void uevTengamTriflate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.TengamPurified, 1), circuit(1))
            .fluidInputs(fluid(PrPMaterials.TriflicAcid, 2000))
            .fluidOutputs(fluid(PrPMaterials.TengamTriflate, 1000))
            .duration(4 * SECONDS)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // =========================================================
    // UEV stage: Activated Naquadria Fluid
    // Naquadria + HF + TriflicAcid → ActivatedNaquadriaFluid + HCl
    // HTRF fluoride activation: highly reactive naquadria matrix
    // =========================================================
    private static void uevActivatedNaquadria() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Naquadria, 1), circuit(2))
            .fluidInputs(fluid(Materials.HydrofluoricAcid, 2000), fluid(PrPMaterials.TriflicAcid, 500))
            .fluidOutputs(fluid(PrPMaterials.ActivatedNaquadriaFluid, 1000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_UHV)
            .metadata(GTRecipeConstants.COIL_HEAT, 10000)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // UEV stage: Hypogen Quantum Matrix
    // Hypogen + ActivatedNaquadriaFluid → HypogenQuantumMatrix
    // HPR: plasma/liquid interface drives quantum-coherent crosslinking
    // =========================================================
    private static void uevHypogenQuantumMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid("molten.hypogen", 1000), fluid(PrPMaterials.ActivatedNaquadriaFluid, 1000))
            .fluidOutputs(fluid(PrPMaterials.HypogenQuantumMatrix, 1000))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_UEV)
            .specialValue(3 | (3 << 4) | (2 << 8)) // coil=Tier III, freqTag=3, freqRequired=2
            .addTo(GTNHPPRecipeMaps.sHPRRecipes);
    }

    // =========================================================
    // UEV stage: Fermium Triflate
    // Fermium + TriflicAcid → FermiumTriflate + H₂O
    // Radioactive triflate dopant
    // =========================================================
    private static void uevFermiumTriflate() {
        GTValues.RA.stdBuilder()
            .itemInputs(item("dustFermium", 1), circuit(4))
            .fluidInputs(fluid(PrPMaterials.TriflicAcid, 2000))
            .fluidOutputs(fluid(PrPMaterials.FermiumTriflate, 1000))
            .duration(4 * SECONDS)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sCSTRRecipes);
    }

    // =========================================================
    // UEV stage: Quantum-Primed Intermediate (QFT Tier 3)
    // HypogenQuantumMatrix + FermiumTriflate + TengamTriflate → QuantumPrimedIntermediate
    // =========================================================
    private static void uevQuantumPrimedIntermediate() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(
                fluid(PrPMaterials.HypogenQuantumMatrix, 1000),
                fluid(PrPMaterials.FermiumTriflate, 500),
                fluid(PrPMaterials.TengamTriflate, 500))
            .fluidOutputs(fluid(PrPMaterials.QuantumPrimedIntermediate, 1000))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_UEV)
            .metadata(GTRecipeConstants.QFT_CATALYST, item("catalystRawIntelligence", 0))
            .metadata(GTRecipeConstants.QFT_FOCUS_TIER, 3)
            .addTo(RecipeMaps.quantumForceTransformerRecipes);
    }

    // =========================================================
    // UEV stage: Beam Activation (SPC, Laser Engraver station, requires Quantum Module)
    // QuantumPrimedIntermediate → BeamActivatedIntermediate
    // High-energy photon beam restructures the quantum lattice
    // =========================================================
    private static void uevBeamActivation() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(PrPMaterials.QuantumPrimedIntermediate, 1000))
            .fluidOutputs(fluid(PrPMaterials.BeamActivatedIntermediate, 1000))
            .duration(6 * SECONDS)
            .eut(TierEU.RECIPE_UEV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.LASER_ENGRAVER },
            new int[] { MV },
            SPCModuleType.QUANTUM);
    }

    // =========================================================
    // UEV stage: Naquadria-Loaded Intermediate (QFT Tier 3)
    // BeamActivatedIntermediate + ActivatedNaquadriaFluid → NaquadriaLoadedIntermediate
    // =========================================================
    private static void uevNaquadriaLoaded() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(
                fluid(PrPMaterials.BeamActivatedIntermediate, 1000),
                fluid(PrPMaterials.ActivatedNaquadriaFluid, 500))
            .fluidOutputs(fluid(PrPMaterials.NaquadriaLoadedIntermediate, 1000))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_UEV)
            .metadata(GTRecipeConstants.QFT_CATALYST, item("catalystRawIntelligence", 0))
            .metadata(GTRecipeConstants.QFT_FOCUS_TIER, 3)
            .addTo(RecipeMaps.quantumForceTransformerRecipes);
    }

    // =========================================================
    // UEV stage: Quantum Cascade Matrix (QFT Tier 3)
    // NaquadriaLoadedIntermediate + TengamTriflate → QuantumCascadeMatrix
    // =========================================================
    private static void uevQuantumCascadeMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(fluid(PrPMaterials.NaquadriaLoadedIntermediate, 1000), fluid(PrPMaterials.TengamTriflate, 500))
            .fluidOutputs(fluid(PrPMaterials.QuantumCascadeMatrix, 1000))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_UEV)
            .metadata(GTRecipeConstants.QFT_CATALYST, item("catalystRawIntelligence", 0))
            .metadata(GTRecipeConstants.QFT_FOCUS_TIER, 3)
            .addTo(RecipeMaps.quantumForceTransformerRecipes);
    }

    // =========================================================
    // UEV stage: Purified Quantum Cascade Matrix (SPC, light-isolated)
    // QuantumCascadeMatrix + Grade 7 Water → PurifiedQuantumCascadeMatrix + H₂O
    // =========================================================
    private static void uevPurification() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(circuit(5))
            .fluidInputs(fluid(PrPMaterials.QuantumCascadeMatrix, 1000), fluid(Materials.Grade7PurifiedWater, 500))
            .fluidOutputs(fluid(PrPMaterials.PurifiedQuantumCascadeMatrix, 1000))
            .duration(6 * SECONDS)
            .eut(TierEU.RECIPE_UEV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH },
            new int[] { MV },
            SPCModuleType.QUANTUM);
    }

    // =========================================================
    // UEV stage: UEV Photoresist blend (Mixer, circuit 17)
    // UHV Photoresist + PurifiedQCM + PGMEA + TriflicAcid → UEV Photoresist
    // =========================================================
    private static void uevBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(17))
            .fluidInputs(
                fluid(PrPMaterials.UHVPhotoresist, 5000),
                fluid(PrPMaterials.PurifiedQuantumCascadeMatrix, 5000),
                fluid(PrPMaterials.PGMEA, 2500),
                fluid(PrPMaterials.TriflicAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.UEVPhotoresist, 10000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_UEV)
            .addTo(RecipeMaps.mixerNonCellRecipes);
    }

    private static void initUiv() {
        uivStabilizedQGPMatrix();
        uivTranscendentQGPLattice();
        uivCreonTriflate();
        uivQuantumFieldImprintedIntermediate();
        uivPhotoresistMatrix();
        uivBlend();
    }

    // =========================================================
    // UIV stage: Stabilized QGP Matrix (SPU)
    // SpaceTime + H plasma → StabilizedQGPMatrix
    // Quark-gluon plasma stabilized via quantum lattice imprinting
    // =========================================================
    private static void uivStabilizedQGPMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(molten(Materials.SpaceTime, 1000), plasma(Materials.Hydrogen, 4000))
            .fluidOutputs(fluid(PrPMaterials.StabilizedQGPMatrix, 1000))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_UIV)
            .addTo(GTNHPPRecipeMaps.sSPURecipes);
    }

    // =========================================================
    // UIV stage: Transcendent QGP Lattice (SPU)
    // StabilizedQGPMatrix + TengamTriflate + Transcendent Metal → TranscendentQGPLattice
    // =========================================================
    private static void uivTranscendentQGPLattice() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.TranscendentMetal, 1), circuit(2))
            .fluidInputs(fluid(PrPMaterials.StabilizedQGPMatrix, 1000), fluid(PrPMaterials.TengamTriflate, 500))
            .fluidOutputs(fluid(PrPMaterials.TranscendentQGPLattice, 1000))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_UIV)
            .addTo(GTNHPPRecipeMaps.sSPURecipes);
    }

    // =========================================================
    // UIV stage: Creon Triflate
    // Creon + TriflicAcid + N₂ → CreonTriflate + H₂O
    // HPR: plasma conditions required for Creon dissolution
    // Runs continuously through UMV alongside PGMEA and TriflicAcid
    // =========================================================
    private static void uivCreonTriflate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Creon, 1), circuit(3))
            .fluidInputs(fluid(PrPMaterials.TriflicAcid, 2000), fluid(Materials.Nitrogen, 1000))
            .fluidOutputs(fluid(PrPMaterials.CreonTriflate, 1000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_UEV)
            .specialValue(4 | (4 << 4) | (3 << 8)) // coil=Tier IV, freqTag=4, freqRequired=3
            .addTo(GTNHPPRecipeMaps.sHPRRecipes);
    }

    // =========================================================
    // UIV stage: Quantum Field-Imprinted Intermediate (SPU)
    // TranscendentQGPLattice + CreonTriflate + Graviton Shards + Grade 7 Water → QFII
    // =========================================================
    private static void uivQuantumFieldImprintedIntermediate() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(
                fluid(PrPMaterials.TranscendentQGPLattice, 1000),
                fluid(PrPMaterials.CreonTriflate, 500),
                fluid(Materials.Grade7PurifiedWater, 250))
            .fluidOutputs(fluid(PrPMaterials.QuantumFieldImprintedIntermediate, 1000))
            .duration(15 * SECONDS)
            .eut(TierEU.RECIPE_UIV)
            .addTo(GTNHPPRecipeMaps.sSPURecipes);
    }

    // =========================================================
    // UIV stage: UIV Photoresist Matrix (SPC, light-isolated)
    // QuantumFieldImprintedIntermediate + Grade 7 Water → UIVPhotoresistMatrix + H₂O
    // =========================================================
    private static void uivPhotoresistMatrix() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(circuit(5))
            .fluidInputs(
                fluid(PrPMaterials.QuantumFieldImprintedIntermediate, 1000),
                fluid(Materials.Grade7PurifiedWater, 500))
            .fluidOutputs(fluid(PrPMaterials.UIVPhotoresistMatrix, 1000))
            .duration(6 * SECONDS)
            .eut(TierEU.RECIPE_UIV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH },
            new int[] { MV },
            SPCModuleType.QUANTUM);
    }

    // =========================================================
    // UIV stage: UIV Photoresist blend (Mixer, circuit 18)
    // UEV Photoresist + UIV Matrix + PGMEA + CreonTriflate → UIV Photoresist
    // =========================================================
    private static void uivBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(18))
            .fluidInputs(
                fluid(PrPMaterials.UEVPhotoresist, 5000),
                fluid(PrPMaterials.UIVPhotoresistMatrix, 5000),
                fluid(PrPMaterials.PGMEA, 2500),
                fluid(PrPMaterials.CreonTriflate, 1000))
            .fluidOutputs(fluid(PrPMaterials.UIVPhotoresist, 10000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_UIV)
            .addTo(RecipeMaps.mixerNonCellRecipes);
    }

    private static void initUmv() {
        umvBlend();
    }

    // =========================================================
    // UMV stage: UMV Photoresist blend (Mixer, circuit 19)
    // UIV Photoresist + UMV Matrix + PGMEA + ShirabonTriflate → UMV Photoresist
    // =========================================================
    private static void umvBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(19))
            .fluidInputs(
                fluid(PrPMaterials.UIVPhotoresist, 2000),
                fluid(PrPMaterials.UMVPhotoresistMatrix, 2000),
                fluid(PrPMaterials.PGMEA, 1000)
            // , fluid(PrPMaterials.ShirabonTriflate, 400)
            )
            .fluidOutputs(fluid(PrPMaterials.UMVPhotoresist, 4000))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_UMV)
            .addTo(RecipeMaps.mixerNonCellRecipes);
    }
}
