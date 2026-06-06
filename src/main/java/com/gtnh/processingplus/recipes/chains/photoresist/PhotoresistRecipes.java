package com.gtnh.processingplus.recipes.chains.photoresist;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import java.util.Collection;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.machines.spc.MachineType;
import com.gtnh.processingplus.machines.spc.SPCModuleType;
import com.gtnh.processingplus.machines.spc.SPCRecipeData;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeConstants;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.material.MaterialMisc;

public class PhotoresistRecipes {

    // SPC station-sequence min tier — simple chemistry, low-tier machines are fine (MV = 2)
    private static final int MV = 2;

    public static void init() {
        initBaseTiers();
        initZpm();
        initUv();
        tryInit("UHV photoresist", PhotoresistRecipes::initUhv);
        tryInit("UEV photoresist", PhotoresistRecipes::initUev);
        tryInit("UIV photoresist", PhotoresistRecipes::initUiv);
        tryInit("UMV photoresist", PhotoresistRecipes::initUmv);
    }

    private static void tryInit(String name, Runnable r) {
        try {
            r.run();
        } catch (IllegalStateException e) {
            System.err.println("[GTNHPP] Skipping " + name + " recipes: " + e.getMessage());
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
        hvAnthraceeneSensitizer();
        hvResorcinolSensitizer();
        hvAdvancedBlend();
        // EV
        evAcetoxystyrene();
        evPHSResin();
        evSulfurDichloride();
        evDiphenylsulfoniumSalt();
        evEVBlend();
        // IV
        ivFurfural();
        ivDihydropyran();
        ivTHPProtection();
        ivIVBlend();
        ivIVBlendEV();
        // LuV — Triflic Acid sub-chain (reused through UMV)
        luvTrifluoromethane();
        luvSulfurTrioxide();
        luvTriflicAcid();
        // LuV — main chain
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

    // MV: Formaldehyde — Ethanol + O₂
    private static void mvFormaldehydeSynthesis() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(Materials.Ethanol, 1000), fluid(Materials.Oxygen, 1000))
            .fluidOutputs(fluid("fluid.formaldehyde", 1000))
            .duration(60)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // MV: Novolac Resin — Phenol + Formaldehyde + H₂SO₄ (cat)
    private static void mvNovolacSynthesis() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(
                fluid(Materials.Phenol, 2000),
                fluid("fluid.formaldehyde", 1000),
                fluid(Materials.SulfuricAcid, 100))
            .fluidOutputs(molten(PrPMaterials.NovolacResin, 1000), fluid(Materials.Water, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // MV: Sensitizer route A — Benzene + O₂
    private static void mvBenzeneSensitizer() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(Materials.Benzene, 1000), fluid(Materials.Oxygen, 2000))
            .fluidOutputs(fluid(PrPMaterials.MVPhotoresistSensitizer, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // MV: Sensitizer route B — Wood → Tannin Solution → Sensitizer (Distillery)
    private static void mvTanninSensitizer() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Wood, 4), circuit(4))
            .fluidInputs(fluid(Materials.Water, 2000))
            .fluidOutputs(fluid(PrPMaterials.TanninSolution, 2000))
            .duration(80)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        GTValues.RA.stdBuilder()
            .fluidInputs(fluid(PrPMaterials.TanninSolution, 2000))
            .fluidOutputs(fluid(PrPMaterials.MVPhotoresistSensitizer, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.distilleryRecipes);
    }

    // MV: Basic Photoresist blend — Novolac + Sensitizer + Ethanol (Mixer)
    private static void mvBasicBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(5))
            .fluidInputs(
                molten(PrPMaterials.NovolacResin, 288),
                fluid(PrPMaterials.MVPhotoresistSensitizer, 3000),
                fluid(Materials.Ethanol, 500))
            .fluidOutputs(fluid(PrPMaterials.BasicPhotoresist, 1500))
            .duration(60)
            .eut(TierEU.RECIPE_MV)
            // Multi-fluid blend → LCR (HV) instead of the IV multi-mixer, so it's reachable below IV.
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // HV: Sensitizer route A — Naphthalene + H₂SO₄
    private static void hvNaphthaleneSensitizer() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid("fluid.naphthalene", 1000), fluid(Materials.SulfuricAcid, 500))
            .fluidOutputs(fluid(PrPMaterials.HVPhotoresistSensitizer, 1000))
            .duration(4*20)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // HV: Sensitizer route B — Anthracene + HNO₃
    private static void hvAnthraceeneSensitizer() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid("fluid.anthracene", 1000), fluid(Materials.NitricAcid, 500))
            .fluidOutputs(fluid(PrPMaterials.HVPhotoresistSensitizer, 1000))
            .duration(4*20)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // HV: Sensitizer route C — Benzene + H₂O₂ + HNO₃
    private static void hvResorcinolSensitizer() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(
                fluid(Materials.Benzene, 1000),
                fluid("fluid.hydrogenperoxide", 500),
                fluid(Materials.NitricAcid, 500))
            .fluidOutputs(fluid(PrPMaterials.HVPhotoresistSensitizer, 5000))
            .duration(3*20)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // HV: Advanced Photoresist blend — Basic + HV Sensitizer (Mixer)
    private static void hvAdvancedBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(
                fluid(PrPMaterials.BasicPhotoresist, 1250 * 2),
                fluid(PrPMaterials.HVPhotoresistSensitizer, 500))
            .fluidOutputs(fluid(PrPMaterials.AdvancedPhotoresist, 1250))
            .duration(60)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // EV: Acetoxystyrene — Styrene + Acetic Anhydride
    private static void evAcetoxystyrene() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(Materials.Styrene, 1000), fluid(PrPMaterials.AceticAnhydride, 1000))
            .fluidOutputs(fluid(PrPMaterials.Acetoxystyrene, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_MV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // EV: PHS Resin — Acetoxystyrene + H₂O₂ + HCl (cat)
    private static void evPHSResin() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(
                fluid(PrPMaterials.Acetoxystyrene, 2000),
                fluid("fluid.hydrogenperoxide", 1000),
                fluid(Materials.HydrochloricAcid, 100))
            .fluidOutputs(molten(PrPMaterials.PHSResin, 1000), fluid(Materials.AceticAcid, 2000))
            .duration(150)
            .metadata(GTRecipeConstants.COIL_HEAT, 4500)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // EV: Sulfur Dichloride (PAG precursor) — S + Cl₂
    private static void evSulfurDichloride() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Sulfur, 1))
            .fluidInputs(fluid(Materials.Chlorine, 2000))
            .fluidOutputs(fluid(PrPMaterials.SulfurDichloride, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // EV: Diphenylsulfonium Salt (PAG) — SCl₂ + 2 Benzene
    private static void evDiphenylsulfoniumSalt() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(PrPMaterials.SulfurDichloride, 1000), fluid(Materials.Benzene, 2000))
            .itemOutputs(dust(PrPMaterials.DiphenylsulfoniumSalt, 2))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 2000))
            .duration(4*20)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // EV: EV Photoresist blend — Advanced + PHS Resin + PAG (Mixer)
    private static void evEVBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.DiphenylsulfoniumSalt, 1), circuit(5))
            .fluidInputs(molten(PrPMaterials.PHSResin, 288), fluid(PrPMaterials.AdvancedPhotoresist, 2000))
            .fluidOutputs(fluid(PrPMaterials.EVPhotoresist, 1750))
            .duration(60)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // IV: Furfural — Wheat + H₂SO₄
    private static void ivFurfural() {
        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.wheat, 4), circuit(1))
            .fluidInputs(fluid(Materials.SulfuricAcid, 500))
            .fluidOutputs(fluid(PrPMaterials.Furfural, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // IV: Dihydropyran — Furfural pyrolysis
    private static void ivDihydropyran() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(PrPMaterials.Furfural, 1000))
            .fluidOutputs(fluid(PrPMaterials.Dihydropyran, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_EV)
            .metadata(GTRecipeConstants.COIL_HEAT, 1800)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // IV: THP-Protected PHS — PHS Resin + Dihydropyran + HCl (cat)
    private static void ivTHPProtection() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(
                molten(PrPMaterials.PHSResin, 288),
                fluid(PrPMaterials.Dihydropyran, 1000),
                fluid(Materials.HydrochloricAcid, 50))
            .fluidOutputs(fluid(PrPMaterials.THPProtectedPHS, 1000))
            .duration(120)
            .eut(TierEU.RECIPE_EV)
            .metadata(GTRecipeConstants.COIL_HEAT, 4200)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // IV: IV Photoresist blend — EV + THP-PHS (Mixer)
    private static void ivIVBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(fluid(PrPMaterials.EVPhotoresist, 1250 * 2), fluid(PrPMaterials.THPProtectedPHS, 500))
            .fluidOutputs(fluid(PrPMaterials.IVPhotoresist, 1250))
            .duration(60)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
    }

    // IV Photoresist — accessible EV single-block Mixer route so the IV blend doesn't strictly
    // require the IV multi-mixer. EVPhotoresist goes in as cells (2 = 2000mB) so it fits the single
    // Mixer's 1-fluid cap; THP-PHS stays the fluid. Slightly lower yield than the multi-mixer bulk route.
    private static void ivIVBlendEV() {
        GTValues.RA.stdBuilder()
            .itemInputs(PrPMaterials.EVPhotoresist.get(OrePrefixes.cell, 2), circuit(4))
            .fluidInputs(fluid(PrPMaterials.THPProtectedPHS, 500))
            .itemOutputs(ItemList.Cell_Empty.get(2))
            .fluidOutputs(fluid(PrPMaterials.IVPhotoresist, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.mixerRecipes);
    }

    // LuV: Trifluoromethane — CHCl₃ + 3 HF (gates Triflic Acid)
    private static void luvTrifluoromethane() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(Materials.Chloroform, 1000), fluid(Materials.HydrofluoricAcid, 3000))
            .fluidOutputs(fluid(PrPMaterials.Trifluoromethane, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: SO₃ — 2 SO₂ + O₂
    private static void luvSulfurTrioxide() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(Materials.SulfurDioxide, 2000), fluid(Materials.Oxygen, 1000))
            .fluidOutputs(fluid(Materials.SulfurTrioxide, 2000))
            .duration(60)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: Triflic Acid — CHF₃ + SO₃
    private static void luvTriflicAcid() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(PrPMaterials.Trifluoromethane, 1000), fluid(Materials.SulfurTrioxide, 1000))
            .fluidOutputs(fluid(PrPMaterials.TriflicAcid, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_IV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: Adamantol (Adamantium gate) — Adamantium + HF + H₂SO₄
    private static void luvAdamantolSynthesis() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Adamantium, 1))
            .fluidInputs(fluid(Materials.HydrofluoricAcid, 2000), fluid(Materials.SulfuricAcid, 500))
            .itemOutputs(dust(PrPMaterials.Adamantol, 2))
            .duration(150)
            .eut(TierEU.RECIPE_IV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: Methacrylic Acid — Acetone + HCN + H₂SO₄; byproduces Ammonium Bisulfate
    private static void luvMethacrylicAcid() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(
                fluid(Materials.Acetone, 1000),
                fluid("hydrogencyanide", 1000),
                fluid(Materials.SulfuricAcid, 100))
            .itemOutputs(dust(PrPMaterials.AmmoniumBisulfate, 2))
            .fluidOutputs(fluid(PrPMaterials.MethacrylicAcid, 1000))
            .duration(120)
            .eut(TierEU.RECIPE_IV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: Adamantyl Methacrylate — Methacrylic Acid + Adamantol
    private static void luvAdamantylMethacrylate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.Adamantol, 1), circuit(5))
            .fluidInputs(fluid(PrPMaterials.MethacrylicAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.AdamantylMethacrylate, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_IV)
            .metadata(GTRecipeConstants.COIL_HEAT, 4500 + 400)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // LuV: Acetone Azine (AIBN precursor) — Acetone + Hydrazine
    private static void luvAcetoneAzine() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(6))
            .fluidInputs(fluid(Materials.Acetone, 2000), fluid("fluid.hydrazine", 1000))
            .fluidOutputs(fluid(PrPMaterials.AcetoneAzine, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: AIBN radical initiator — Acetone Azine + HCN + Cl₂
    private static void luvAIBN() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(7))
            .fluidInputs(
                fluid(PrPMaterials.AcetoneAzine, 1000),
                fluid("hydrogencyanide", 2000),
                fluid(Materials.Chlorine, 2000))
            .itemOutputs(dust(PrPMaterials.AIBN, 4))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 2000))
            .duration(120)
            .eut(TierEU.RECIPE_IV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: Alicyclic Resin — AdMA + MAA + AIBN + N₂ polymerization
    private static void luvAlicyclicResin() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.AIBN, 1), circuit(8))
            .fluidInputs(
                fluid(PrPMaterials.AdamantylMethacrylate, 1000),
                fluid(PrPMaterials.MethacrylicAcid, 1000),
                fluid(Materials.Nitrogen, 2000))
            .fluidOutputs(molten(PrPMaterials.AlicyclicResin, 576))
            .duration(150)
            .eut(TierEU.RECIPE_LuV)
            .metadata(GTRecipeConstants.COIL_HEAT, 3600)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // LuV: Triphenylsulfonium Triflate (stronger PAG) — Diphenylsulfonium + TriflicAcid + Benzene
    private static void luvTriphenylsulfoniumTriflate() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.DiphenylsulfoniumSalt, 1), circuit(9))
            .fluidInputs(fluid(PrPMaterials.TriflicAcid, 1000), fluid(Materials.Benzene, 1000))
            .itemOutputs(dust(PrPMaterials.TriphenylsulfoniumTriflate, 2))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_LuV)
            .metadata(GTRecipeConstants.COIL_HEAT, 1300)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_REACTOR, MachineType.MIXER, MachineType.CHEMICAL_REACTOR,
                MachineType.MIXER, MachineType.CHEMICAL_BATH},
            new int[] { 3, 4, 3, 4, 5});
    }

    // HV: Propylene Oxide (PGME precursor) — Propylene + H₂O₂
    private static void luvPropyleneOxide() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(Materials.Propene, 1000), fluid("fluid.hydrogenperoxide", 1000))
            .fluidOutputs(fluid(PrPMaterials.PropyleneOxide, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: PGME solvent — Propylene Oxide + Methanol
    private static void luvPGME() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(10))
            .fluidInputs(fluid(PrPMaterials.PropyleneOxide, 1000), fluid(Materials.Methanol, 1000))
            .fluidOutputs(fluid(PrPMaterials.PGME, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: PGMEA solvent — PGME + Acetic Acid
    private static void luvPGMEA() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(11))
            .fluidInputs(fluid(PrPMaterials.PGME, 1000), fluid(Materials.AceticAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.PGMEA, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: LuV Photoresist blend — IV + Alicyclic Resin + PAG + PGMEA (Mixer)
    private static void luvLuVBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.TriphenylsulfoniumTriflate, 1), circuit(13))
            .fluidInputs(
                molten(PrPMaterials.AlicyclicResin, 288),
                fluid(PrPMaterials.IVPhotoresist, 2000),
                fluid(PrPMaterials.PGMEA, 500))
            .fluidOutputs(fluid(PrPMaterials.LuVPhotoresist, 3750))
            .duration(60)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
    }

    // LuV: Ammonium Bisulfate cracking — recovers H₂SO₄ + NH₃ (byproduct of MethacrylicAcid)
    private static void luvAmmoniumBisulfateCracking() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.AmmoniumBisulfate, 2))
            .fluidOutputs(fluid(Materials.SulfuricAcid, 1000), fluid(Materials.Ammonia, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.distillationTowerRecipes);
    }

    private static void initZpm() {
        kevlarwtfdude();
        zpmHexafluoroacetone();
        zpmHFIMAMonomer();
        zpmGBLMAMonomer();
        zpmHAdMAMonomer();
        zpmArFCopolymerResin();
        zpmZPMBlend();
    }

    // finishing kevlar or something idk these didnt have recipes
    private static void kevlarwtfdude() {
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Tellurium.getDust(1), circuit(24))
            .fluidInputs(fluid(Materials.Oxygen, 2000))
            .itemOutputs(item("dustTellurium(IV)Oxide", 3))
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .metadata(GTRecipeConstants.COIL_HEAT, 722)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Molybdenum.getDust(1), circuit(24))
            .fluidInputs(fluid(Materials.Oxygen, 2000))
            .itemOutputs(item("dustMolybdenum(IV)Oxide", 3))
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .metadata(GTRecipeConstants.COIL_HEAT, 722)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // ZPM — Hexafluoroacetone
    // 2 CHF₃ + ½O₂ → (CF₃)₂CO + H₂O
    // Trifluoromethane reused from LuV Triflic Acid sub-chain
    // =========================================================
    private static void zpmHexafluoroacetone() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(PrPMaterials.Trifluoromethane, 2000), fluid(Materials.Oxygen, 1000))
            .fluidOutputs(fluid(PrPMaterials.Hexafluoroacetone, 4000))
            .duration(3*20)
            .eut(TierEU.RECIPE_IV)
            .metadata(GTRecipeConstants.COIL_HEAT, 2700)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // ZPM — HFIMA Monomer (hexafluoroisopropyl methacrylate)
    // (CF₃)₂CO + MethacrylicAcid → HFIMA + H₂O
    // MethacrylicAcid reused from LuV chain
    // =========================================================
    private static void zpmHFIMAMonomer() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(PrPMaterials.Hexafluoroacetone, 1000), fluid(PrPMaterials.MethacrylicAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.HFIMAMonomer, 2000))
            .duration(4*20)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // ZPM — GBLMA Monomer (gamma-butyrolactone methacrylate)
    // GBL + MethacrylicAcid → GBLMA + H₂O
    // GBL = solvent chemistry gate
    // =========================================================
    private static void zpmGBLMAMonomer() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(Materials.GammaButyrolactone, 1000), fluid(PrPMaterials.MethacrylicAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.GBLMAMonomer, 2000))
            .duration(4*20)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // ZPM — HAdMA Monomer (hydroxy-adamantyl methacrylate)
    // Adamantol + Hexafluoroacetone + MethacrylicAcid → HAdMA + H₂O
    // Adamantol gate reused from LuV Naquadah processing
    // =========================================================
    private static void zpmHAdMAMonomer() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.Adamantol, 1), circuit(4))
            .fluidInputs(fluid(PrPMaterials.Hexafluoroacetone, 500), fluid(PrPMaterials.MethacrylicAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.HAdMAMonomer, 2000))
            .duration(4*20)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // ZPM — ArF Copolymer Resin
    // HFIMA + GBLMA + HAdMA + AIBN (cat, reused from LuV) + N₂ → ArF Copolymer Resin
    // Radical polymerization under inert atmosphere
    // =========================================================
    private static void zpmArFCopolymerResin() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.AIBN, 1), circuit(5))
            .fluidInputs(
                fluid(PrPMaterials.HFIMAMonomer, 1000),
                fluid(PrPMaterials.GBLMAMonomer, 1000),
                fluid(PrPMaterials.HAdMAMonomer, 1000),
                fluid(Materials.Nitrogen, 2000))
            .fluidOutputs(molten(PrPMaterials.ArFCopolymerResin, 1000))
            .duration(5*20)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // ZPM — ZPM Photoresist blend (Mixer, circuit 14)
    // LuV Photoresist + ArF Resin + Triphenylsulfonium Triflate + PGMEA → ZPM Photoresist
    // PGMEA and TriphenylsulfoniumTriflate run continuously from LuV through UMV
    // =========================================================
    private static void zpmZPMBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.TriphenylsulfoniumTriflate, 1), circuit(14))
            .fluidInputs(
                fluid(PrPMaterials.LuVPhotoresist, 500),
                molten(PrPMaterials.ArFCopolymerResin, 288),
                fluid(PrPMaterials.PGMEA, 500))
            .fluidOutputs(fluid(PrPMaterials.ZPMPhotoresist, 1250))
            .duration(60)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
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
    // UV — Tin Oxo-Acetate Cluster (EUV sensitizer precursor)
    // Sn + AceticAcid + O₂ → TinOxoAcetateCluster + H₂O
    // Light-isolated in SPC; tin organometallics are UV-sensitive
    // =========================================================
    private static void uvTinOxoAcetateCluster() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Tin, 1), circuit(1))
            .fluidInputs(fluid(Materials.AceticAcid, 2000), fluid(Materials.Oxygen, 2000))
            .fluidOutputs(fluid(PrPMaterials.TinOxoAcetateCluster, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.MIXER, MachineType.CHEMICAL_BATH },
            new int[] { MV, MV });
    }

    // =========================================================
    // UV — Erbium Triflate
    // Er + TriflicAcid + O₂ → ErbiumTriflate + H₂O
    // RE triflate dopant for photoactive matrix
    // =========================================================
    private static void uvErbiumTriflate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(mat("Erbium"), 2), circuit(2))
            .fluidInputs(fluid(PrPMaterials.TriflicAcid, 4000), fluid(Materials.Oxygen, 2000))
            .itemOutputs(dust(PrPMaterials.ErbiumTriflate, 6))
            .fluidOutputs(fluid(Materials.Water, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // UV — Ytterbium Acetate
    // Yb + AceticAcid → YtterbiumAcetate + H₂O
    // =========================================================
    private static void uvYtterbiumAcetate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(mat("Ytterbium"), 1), circuit(3))
            .fluidInputs(fluid(Materials.AceticAcid, 3000))
            .itemOutputs(dust(PrPMaterials.YtterbiumAcetate, 4))
            .fluidOutputs(fluid(Materials.Water, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // UV — Terbium Chloride
    // Tb + HCl → TerbiumChloride + H₂O
    // =========================================================
    private static void uvTerbiumChloride() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(mat("Terbium"), 1), circuit(4))
            .fluidInputs(fluid(Materials.HydrochloricAcid, 3000))
            .itemOutputs(dust(PrPMaterials.TerbiumChloride, 4))
            .fluidOutputs(fluid(Materials.Water, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // UV — Terbium Acetylacetonate
    // TerbiumChloride + Acetone + AceticAcid → TerbiumAcetylacetonate + HCl
    // Acetylacetonate ligand formed in situ
    // =========================================================
    private static void uvTerbiumAcetylacetonate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.TerbiumChloride, 1), circuit(5))
            .fluidInputs(fluid(Materials.Acetone, 1000), fluid(Materials.AceticAcid, 1000))
            .itemOutputs(dust(PrPMaterials.TerbiumAcetylacetonate, 3))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // UV — Dysprosium-Doped Calcium Fluoride (hot press sintering)
    // Dy + Ca + HF → CaF₂:Dy + H₂O
    // Argon atmosphere prevents oxide formation
    // =========================================================
    private static void uvDysprosiumDopedCalciumFluoride() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(mat("Dysprosium"), 1), dust(Materials.Calcium, 1))
            .fluidInputs(fluid(Materials.HydrofluoricAcid, 4000), fluid(Materials.Argon, 1000))
            .itemOutputs(dust(PrPMaterials.DysprosiumDopedCalciumFluoride, 6))
            .duration(120)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // =========================================================
    // UV — RE-Doped Photoresist Matrix (CIDC)
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
            .duration(200)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sCIDCRecipes);
    }

    // =========================================================
    // UV — UV Photoresist blend (Mixer, circuit 15)
    // ZPM Photoresist + RE-Doped Matrix + PGMEA → UV Photoresist
    // =========================================================
    private static void uvUVBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.REDopedPhotoresistMatrix, 2), circuit(15))
            .fluidInputs(fluid(PrPMaterials.ZPMPhotoresist, 500), fluid(PrPMaterials.PGMEA, 500))
            .fluidOutputs(fluid(PrPMaterials.UVPhotoresist, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
    }

    private static void initUhv() {
        uhvBioRefinedIntermediate();
        uhvRadoxXenoxeneMatrix();
        uhvLivingSolderAcetate();
        uhvPhotoresistMatrix();
        uhvBlend();
    }

    // =========================================================
    // UHV — Bio-Refined Intermediate
    // Mutagen + Unknown Liquid → BioRefinedIntermediate
    // HPR simultaneous liquid/plasma chemistry unlocks exotic bio-matrix
    // =========================================================
    private static void uhvBioRefinedIntermediate() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            // .fluidInputs(fluid("mutagen", 1000), fluid(Materials.Xenoxene, 500))
            .fluidOutputs(fluid(PrPMaterials.BioRefinedIntermediate, 1000))
            .duration(120)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sHPRRecipes);
    }

    // =========================================================
    // UHV — Radox-Xenoxene Matrix
    // BioRefinedIntermediate + Radox Polymer + Xenoxene → RadoxXenoxeneMatrix
    // HPR — high-pressure plasma conditions force exotic polymer crosslinking
    // =========================================================
    private static void uhvRadoxXenoxeneMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(
                fluid(PrPMaterials.BioRefinedIntermediate, 2000),
                molten(Materials.RadoxPolymer, 288),
                fluid(Materials.Xenoxene, 250))
            .fluidOutputs(fluid(PrPMaterials.RadoxXenoxeneMatrix, 2500))
            .duration(200)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sHPRRecipes);
    }

    // =========================================================
    // UHV — Living Solder Acetate
    // Living Solder + AceticAcid → LivingSolderAcetate + H₂O
    // Acetate ligand substitution — stabilizes living solder for photoresist use
    // =========================================================
    private static void uhvLivingSolderAcetate() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(MaterialMisc.MUTATED_LIVING_SOLDER, 1000), fluid(Materials.AceticAcid, 500))
            .fluidOutputs(fluid(PrPMaterials.LivingSolderAcetate, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // UHV — UHV Photoresist Matrix
    // RadoxXenoxeneMatrix + LivingSolderAcetate + Grade 6 Water → UHVPhotoresistMatrix
    // HPR — plasma-assisted matrix assembly under ultra-pure conditions
    // =========================================================
    private static void uhvPhotoresistMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(
                fluid(PrPMaterials.RadoxXenoxeneMatrix, 500),
                fluid(PrPMaterials.LivingSolderAcetate, 500),
                fluid(Materials.Grade6PurifiedWater, 500))
            .fluidOutputs(fluid(PrPMaterials.UHVPhotoresistMatrix, 100))
            .duration(200)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTNHPPRecipeMaps.sHPRRecipes);
    }

    // =========================================================
    // UHV — UHV Photoresist blend (Mixer, circuit 16)
    // UV Photoresist + UHV Matrix + PGMEA + Triflic Acid → UHV Photoresist
    // =========================================================
    private static void uhvBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(16))
            .fluidInputs(
                fluid(PrPMaterials.UVPhotoresist, 2000),
                fluid(PrPMaterials.UHVPhotoresistMatrix, 1000),
                fluid(PrPMaterials.PGMEA, 500),
                fluid(PrPMaterials.TriflicAcid, 200))
            .fluidOutputs(fluid(PrPMaterials.UHVPhotoresist, 500))
            .duration(60)
            .eut(TierEU.RECIPE_UHV)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
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
    // UEV — Tengam Triflate
    // Tengam + TriflicAcid → TengamTriflate + H₂O
    // Exotic triflate salt; runs continuously through UMV
    // =========================================================
    private static void uevTengamTriflate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.TengamPurified, 1), circuit(1))
            .fluidInputs(fluid(PrPMaterials.TriflicAcid, 2000))
            .fluidOutputs(fluid(PrPMaterials.TengamTriflate, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // UEV — Activated Naquadria Fluid
    // Naquadria + HF + TriflicAcid → ActivatedNaquadriaFluid + HCl
    // HTRF fluoride activation — highly reactive naquadria matrix
    // =========================================================
    private static void uevActivatedNaquadria() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Naquadria, 1), circuit(2))
            .fluidInputs(fluid(Materials.HydrofluoricAcid, 2000), fluid(PrPMaterials.TriflicAcid, 500))
            .fluidOutputs(fluid(PrPMaterials.ActivatedNaquadriaFluid, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_UHV)
            .metadata(GTRecipeConstants.COIL_HEAT, 10000)
            .addTo(GTNHPPRecipeMaps.sHTRFRecipes);
    }

    // =========================================================
    // UEV — Hypogen Quantum Matrix
    // Hypogen + ActivatedNaquadriaFluid → HypogenQuantumMatrix
    // HPR — plasma/liquid interface drives quantum-coherent crosslinking
    // =========================================================
    private static void uevHypogenQuantumMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid("molten.hypogen", 1000), fluid(PrPMaterials.ActivatedNaquadriaFluid, 1000))
            .fluidOutputs(fluid(PrPMaterials.HypogenQuantumMatrix, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_UEV)
            .addTo(GTNHPPRecipeMaps.sHPRRecipes);
    }

    // =========================================================
    // UEV — Fermium Triflate
    // Fermium + TriflicAcid → FermiumTriflate + H₂O
    // Radioactive triflate dopant
    // =========================================================
    private static void uevFermiumTriflate() {
        GTValues.RA.stdBuilder()
            .itemInputs(item("dustFermium", 1), circuit(4))
            .fluidInputs(fluid(PrPMaterials.TriflicAcid, 2000))
            .fluidOutputs(fluid(PrPMaterials.FermiumTriflate, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // =========================================================
    // UEV — Quantum-Primed Intermediate (QFT Tier 3)
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
            .duration(200)
            .eut(TierEU.RECIPE_UEV)
            .metadata(GTRecipeConstants.QFT_CATALYST, item("catalystRawIntelligence", 0))
            .metadata(GTRecipeConstants.QFT_FOCUS_TIER, 3)
            .addTo(GTPPRecipeMaps.quantumForceTransformerRecipes);
    }

    // =========================================================
    // UEV — Beam Activation (Beamcrafter)
    // QuantumPrimedIntermediate → BeamActivatedIntermediate
    // High-energy photon beam restructures the quantum lattice
    // =========================================================
    private static void uevBeamActivation() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(PrPMaterials.QuantumPrimedIntermediate, 1000))
            .fluidOutputs(fluid(PrPMaterials.BeamActivatedIntermediate, 1000))
            .duration(120)
            .eut(TierEU.RECIPE_UEV)
            .addTo(GTNHPPRecipeMaps.sBeamcrafterRecipes);
    }

    // =========================================================
    // UEV — Naquadria-Loaded Intermediate (QFT Tier 3)
    // BeamActivatedIntermediate + ActivatedNaquadriaFluid → NaquadriaLoadedIntermediate
    // =========================================================
    private static void uevNaquadriaLoaded() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(
                fluid(PrPMaterials.BeamActivatedIntermediate, 1000),
                fluid(PrPMaterials.ActivatedNaquadriaFluid, 500))
            .fluidOutputs(fluid(PrPMaterials.NaquadriaLoadedIntermediate, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_UEV)
            .metadata(GTRecipeConstants.QFT_CATALYST, item("catalystRawIntelligence", 0))
            .metadata(GTRecipeConstants.QFT_FOCUS_TIER, 3)
            .addTo(GTPPRecipeMaps.quantumForceTransformerRecipes);
    }

    // =========================================================
    // UEV — Quantum Cascade Matrix (QFT Tier 3)
    // NaquadriaLoadedIntermediate + TengamTriflate → QuantumCascadeMatrix
    // =========================================================
    private static void uevQuantumCascadeMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(fluid(PrPMaterials.NaquadriaLoadedIntermediate, 1000), fluid(PrPMaterials.TengamTriflate, 500))
            .fluidOutputs(fluid(PrPMaterials.QuantumCascadeMatrix, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_UEV)
            .metadata(GTRecipeConstants.QFT_CATALYST, item("catalystRawIntelligence", 0))
            .metadata(GTRecipeConstants.QFT_FOCUS_TIER, 3)
            .addTo(GTPPRecipeMaps.quantumForceTransformerRecipes);
    }

    // =========================================================
    // UEV — Purified Quantum Cascade Matrix (SPC — light-isolated)
    // QuantumCascadeMatrix + Grade 7 Water → PurifiedQuantumCascadeMatrix + H₂O
    // =========================================================
    private static void uevPurification() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(circuit(5))
            .fluidInputs(fluid(PrPMaterials.QuantumCascadeMatrix, 1000), fluid(Materials.Grade7PurifiedWater, 500))
            .fluidOutputs(fluid(PrPMaterials.PurifiedQuantumCascadeMatrix, 1000))
            .duration(120)
            .eut(TierEU.RECIPE_UEV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH },
            new int[] { MV },
            SPCModuleType.QUANTUM);
    }

    // =========================================================
    // UEV — UEV Photoresist blend (Mixer, circuit 17)
    // UHV Photoresist + PurifiedQCM + PGMEA + TriflicAcid → UEV Photoresist
    // =========================================================
    private static void uevBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(17))
            .fluidInputs(
                fluid(PrPMaterials.UHVPhotoresist, 500),
                fluid(PrPMaterials.PurifiedQuantumCascadeMatrix, 500),
                fluid(PrPMaterials.PGMEA, 250),
                fluid(PrPMaterials.TriflicAcid, 100))
            .fluidOutputs(fluid(PrPMaterials.UEVPhotoresist, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_UEV)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
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
    // UIV — Stabilized QGP Matrix (SPU)
    // SpaceTime + H plasma → StabilizedQGPMatrix
    // Quark-gluon plasma stabilized via quantum lattice imprinting
    // =========================================================
    private static void uivStabilizedQGPMatrix() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(molten(Materials.SpaceTime, 1000), plasma(Materials.Hydrogen, 4000))
            .fluidOutputs(fluid(PrPMaterials.StabilizedQGPMatrix, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_UIV)
            .addTo(GTNHPPRecipeMaps.sSPURecipes);
    }

    // =========================================================
    // UIV — Transcendent QGP Lattice (SPU)
    // StabilizedQGPMatrix + TengamTriflate + Transcendent Metal → TranscendentQGPLattice
    // =========================================================
    private static void uivTranscendentQGPLattice() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.TranscendentMetal, 1), circuit(2))
            .fluidInputs(fluid(PrPMaterials.StabilizedQGPMatrix, 1000), fluid(PrPMaterials.TengamTriflate, 500))
            .fluidOutputs(fluid(PrPMaterials.TranscendentQGPLattice, 1000))
            .duration(200)
            .eut(TierEU.RECIPE_UIV)
            .addTo(GTNHPPRecipeMaps.sSPURecipes);
    }

    // =========================================================
    // UIV — Creon Triflate
    // Creon + TriflicAcid + N₂ → CreonTriflate + H₂O
    // HPR — plasma conditions required for Creon dissolution
    // Runs continuously through UMV alongside PGMEA and TriflicAcid
    // =========================================================
    private static void uivCreonTriflate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Creon, 1), circuit(3))
            .fluidInputs(fluid(PrPMaterials.TriflicAcid, 2000), fluid(Materials.Nitrogen, 1000))
            .fluidOutputs(fluid(PrPMaterials.CreonTriflate, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_UEV)
            .addTo(GTNHPPRecipeMaps.sHPRRecipes);
    }

    // =========================================================
    // UIV — Quantum Field-Imprinted Intermediate (SPU)
    // TranscendentQGPLattice + CreonTriflate + Graviton Shards + Grade 7 Water → QFII
    // =========================================================
    private static void uivQuantumFieldImprintedIntermediate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.GravitonShard, 1), circuit(4))
            .fluidInputs(
                fluid(PrPMaterials.TranscendentQGPLattice, 1000),
                fluid(PrPMaterials.CreonTriflate, 500),
                fluid(Materials.Grade7PurifiedWater, 250))
            .fluidOutputs(fluid(PrPMaterials.QuantumFieldImprintedIntermediate, 1000))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(GTNHPPRecipeMaps.sSPURecipes);
    }

    // =========================================================
    // UIV — UIV Photoresist Matrix (SPC — light-isolated)
    // QuantumFieldImprintedIntermediate + Grade 7 Water → UIVPhotoresistMatrix + H₂O
    // =========================================================
    private static void uivPhotoresistMatrix() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(circuit(5))
            .fluidInputs(
                fluid(PrPMaterials.QuantumFieldImprintedIntermediate, 1000),
                fluid(Materials.Grade7PurifiedWater, 500))
            .fluidOutputs(fluid(PrPMaterials.UIVPhotoresistMatrix, 1000))
            .duration(120)
            .eut(TierEU.RECIPE_UIV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH },
            new int[] { MV },
            SPCModuleType.QUANTUM);
    }

    // =========================================================
    // UIV — UIV Photoresist blend (Mixer, circuit 18)
    // UEV Photoresist + UIV Matrix + PGMEA + CreonTriflate → UIV Photoresist
    // =========================================================
    private static void uivBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(18))
            .fluidInputs(
                fluid(PrPMaterials.UEVPhotoresist, 500),
                fluid(PrPMaterials.UIVPhotoresistMatrix, 500),
                fluid(PrPMaterials.PGMEA, 250),
                fluid(PrPMaterials.CreonTriflate, 100))
            .fluidOutputs(fluid(PrPMaterials.UIVPhotoresist, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_UIV)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
    }

    private static void initUmv() {
        umvBlend();
    }

    // =========================================================
    // UMV — UMV Photoresist blend (Mixer, circuit 19)
    // UIV Photoresist + UMV Matrix + PGMEA + ShirabonTriflate → UMV Photoresist
    // =========================================================
    private static void umvBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(19))
            .fluidInputs(
                fluid(PrPMaterials.UIVPhotoresist, 500),
                fluid(PrPMaterials.UMVPhotoresistMatrix, 500),
                fluid(PrPMaterials.PGMEA, 250)
            // , fluid(PrPMaterials.ShirabonTriflate, 100)
            )
            .fluidOutputs(fluid(PrPMaterials.UMVPhotoresist, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_UMV)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
    }
}
