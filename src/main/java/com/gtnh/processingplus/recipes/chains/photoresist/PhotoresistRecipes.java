package com.gtnh.processingplus.recipes.chains.photoresist;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.materials.PrPMaterials;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class PhotoresistRecipes {

    public static void init() {
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
            .itemOutputs(dust(PrPMaterials.NovolacResin, 4))
            .fluidOutputs(fluid(Materials.Water, 1000))
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
                fluid(PrPMaterials.MVPhotoresistSensitizer, 500),
                fluid(Materials.Ethanol, 500))
            .fluidOutputs(fluid(PrPMaterials.BasicPhotoresist, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
    }

    // HV: Sensitizer route A — Naphthalene + H₂SO₄
    private static void hvNaphthaleneSensitizer() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid("fluid.naphthalene", 1000), fluid(Materials.SulfuricAcid, 500))
            .fluidOutputs(fluid(PrPMaterials.HVPhotoresistSensitizer, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // HV: Sensitizer route B — Anthracene + HNO₃
    private static void hvAnthraceeneSensitizer() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid("fluid.anthracene", 1000), fluid(Materials.NitricAcid, 500))
            .fluidOutputs(fluid(PrPMaterials.HVPhotoresistSensitizer, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_HV)
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
            .fluidOutputs(fluid(PrPMaterials.HVPhotoresistSensitizer, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_HV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // HV: Advanced Photoresist blend — Basic + HV Sensitizer (Mixer)
    private static void hvAdvancedBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(fluid(PrPMaterials.BasicPhotoresist, 750), fluid(PrPMaterials.HVPhotoresistSensitizer, 500))
            .fluidOutputs(fluid(PrPMaterials.AdvancedPhotoresist, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
    }

    // EV: Acetoxystyrene — Styrene + Acetic Anhydride
    private static void evAcetoxystyrene() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(Materials.Styrene, 1000), fluid(PrPMaterials.AceticAnhydride, 1000))
            .fluidOutputs(fluid(PrPMaterials.Acetoxystyrene, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_EV)
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
            .itemOutputs(dust(PrPMaterials.PHSResin, 4))
            .fluidOutputs(fluid(Materials.AceticAcid, 2000))
            .duration(150)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // EV: Sulfur Dichloride (PAG precursor) — S + Cl₂
    private static void evSulfurDichloride() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Sulfur, 1))
            .fluidInputs(fluid(Materials.Chlorine, 2000))
            .fluidOutputs(fluid(PrPMaterials.SulfurDichloride, 1000))
            .duration(40)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // EV: Diphenylsulfonium Salt (PAG) — SCl₂ + 2 Benzene
    private static void evDiphenylsulfoniumSalt() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(PrPMaterials.SulfurDichloride, 1000), fluid(Materials.Benzene, 2000))
            .itemOutputs(dust(PrPMaterials.DiphenylsulfoniumSalt, 2))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 2000))
            .duration(100)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // EV: EV Photoresist blend — Advanced + PHS Resin + PAG (Mixer)
    private static void evEVBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.DiphenylsulfoniumSalt, 1), circuit(5))
            .fluidInputs(molten(PrPMaterials.PHSResin, 288), fluid(PrPMaterials.AdvancedPhotoresist, 750))
            .fluidOutputs(fluid(PrPMaterials.EVPhotoresist, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
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
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // IV: THP-Protected PHS — PHS Resin + Dihydropyran + HCl (cat)
    private static void ivTHPProtection() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.PHSResin, 2), circuit(3))
            .fluidInputs(fluid(PrPMaterials.Dihydropyran, 1000), fluid(Materials.HydrochloricAcid, 50))
            .fluidOutputs(fluid(PrPMaterials.THPProtectedPHS, 1000))
            .duration(120)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // IV: IV Photoresist blend — EV + THP-PHS (Mixer)
    private static void ivIVBlend() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(fluid(PrPMaterials.EVPhotoresist, 750), fluid(PrPMaterials.THPProtectedPHS, 500))
            .fluidOutputs(fluid(PrPMaterials.IVPhotoresist, 1000))
            .duration(60)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
    }

    // LuV: Trifluoromethane — CHCl₃ + 3 HF (gates Triflic Acid)
    private static void luvTrifluoromethane() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(1))
            .fluidInputs(fluid(Materials.Chloroform, 1000), fluid(Materials.HydrofluoricAcid, 3000))
            .fluidOutputs(fluid(PrPMaterials.Trifluoromethane, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: SO₃ — 2 SO₂ + O₂
    private static void luvSulfurTrioxide() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(2))
            .fluidInputs(fluid(Materials.SulfurDioxide, 2000), fluid(Materials.Oxygen, 1000))
            .fluidOutputs(fluid(Materials.SulfurTrioxide, 2000))
            .duration(60)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: Triflic Acid — CHF₃ + SO₃
    private static void luvTriflicAcid() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(3))
            .fluidInputs(fluid(PrPMaterials.Trifluoromethane, 1000), fluid(Materials.SulfurTrioxide, 1000))
            .fluidOutputs(fluid(PrPMaterials.TriflicAcid, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: Adamantol (Naquadah gate) — Naquadah + HF + H₂SO₄
    private static void luvAdamantolSynthesis() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(Materials.Naquadah, 1))
            .fluidInputs(fluid(Materials.HydrofluoricAcid, 2000), fluid(Materials.SulfuricAcid, 500))
            .itemOutputs(dust(PrPMaterials.Adamantol, 2))
            .duration(150)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: Methacrylic Acid — Acetone + HCN + H₂SO₄; byproduces Ammonium Bisulfate
    private static void luvMethacrylicAcid() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(4))
            .fluidInputs(
                fluid(Materials.Acetone, 1000),
                fluid(PrPMaterials.HydrogenCyanide, 1000),
                fluid(Materials.SulfuricAcid, 100))
            .itemOutputs(dust(PrPMaterials.AmmoniumBisulfate, 2))
            .fluidOutputs(fluid(PrPMaterials.MethacrylicAcid, 1000))
            .duration(120)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: Adamantyl Methacrylate — Methacrylic Acid + Adamantol
    private static void luvAdamantylMethacrylate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.Adamantol, 1), circuit(5))
            .fluidInputs(fluid(PrPMaterials.MethacrylicAcid, 1000))
            .fluidOutputs(fluid(PrPMaterials.AdamantylMethacrylate, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: Acetone Azine (AIBN precursor) — Acetone + Hydrazine
    private static void luvAcetoneAzine() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(6))
            .fluidInputs(fluid(Materials.Acetone, 2000), fluid("fluid.hydrazine", 1000))
            .fluidOutputs(fluid(PrPMaterials.AcetoneAzine, 1000))
            .duration(80)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: AIBN radical initiator — Acetone Azine + HCN + Cl₂
    private static void luvAIBN() {
        GTValues.RA.stdBuilder()
            .itemInputs(circuit(7))
            .fluidInputs(
                fluid(PrPMaterials.AcetoneAzine, 1000),
                fluid(PrPMaterials.HydrogenCyanide, 2000),
                fluid(Materials.Chlorine, 2000))
            .itemOutputs(dust(PrPMaterials.AIBN, 4))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 2000))
            .duration(120)
            .eut(TierEU.RECIPE_LuV)
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
            .itemOutputs(dust(PrPMaterials.AlicyclicResin, 4))
            .duration(200)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    // LuV: Triphenylsulfonium Triflate (stronger PAG) — Diphenylsulfonium + TriflicAcid + Benzene
    private static void luvTriphenylsulfoniumTriflate() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.DiphenylsulfoniumSalt, 1), circuit(9))
            .fluidInputs(fluid(PrPMaterials.TriflicAcid, 1000), fluid(Materials.Benzene, 1000))
            .itemOutputs(dust(PrPMaterials.TriphenylsulfoniumTriflate, 2))
            .fluidOutputs(fluid(Materials.HydrochloricAcid, 1000))
            .duration(100)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);
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
                fluid(PrPMaterials.IVPhotoresist, 500),
                fluid(PrPMaterials.PGMEA, 500))
            .fluidOutputs(fluid(PrPMaterials.LuVPhotoresist, 1000))
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
}
