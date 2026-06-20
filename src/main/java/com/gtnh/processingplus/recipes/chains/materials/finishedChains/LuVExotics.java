package com.gtnh.processingplus.recipes.chains.materials.finishedChains;

import static com.gtnh.processingplus.items.Intermediate.*;
import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import bwcrossmod.galacticgreg.VoidMinerUtility;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeConstants;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

/**
 * LuV-era exotic metals. Tiers are mixed on purpose — these are LuV-gated chains that reach into
 * ZPM-tier reagents (Fiery Steel, Life Essence, the Magic Acid superacid line).
 *
 * <p>
 * <b>Vibranium</b> — Adamantium ore is too tough to digest into a mud, so it is melted with Fiery
 * Steel, then infused with specific metals and the essence of life itself.
 *
 * <p>
 * <b>Unobtanium</b> — a Void-Miner-only material. The ore yields an Ore Concentrate that ordinary
 * acids can't touch, so it is dissolved with Magic Acid (fluoroantimonic superacid).
 */
public class LuVExotics {

    public static void init() {
        // Vibranium (5 steps)
        vibRedHotAdamantium();
        vibDye();
        vibVibrantAdamantium();
        vibHotVibranium();

        // Unobtanium (9 steps) — SbF5 comes from GoodGenerator (fluid "antimony pentafluoride")
        unoFluorosulfuricAcid();
        unoMagicAcid();
        unoDissolution();
        unoWash();
        unoCrystallize();
        unoLaserPurify();
        unoEuropiumChlorideRecovery();
        unoThermalCentrifuge();
        unoClumps();
        unoCentrifuge();

        // Late-LuV bootstrap source for the Ore Concentrate + the ZPM Void Miner bulk drop.
        unoEndConcentration();
        registerEndVoidMinerDrop();
    }

    /** Run one recipe in isolation so a single missing material handle can't break the rest. */
    private static void safe(String label, Runnable r) {
        try {
            r.run();
        } catch (Throwable t) {
            System.err.println("[GTNHPP] LuVExotics skipped '" + label + "': " + t);
        }
    }

    // =========================================================
    // VIBRANIUM
    // =========================================================

    // 1. Chem Bath — Adamantium ingot melted by molten Fiery Steel
    private static void vibRedHotAdamantium() {
        safe(
            "red hot adamantium",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(ingot(Materials.Adamantium, 1))
                .fluidInputs(molten(Materials.FierySteel, 144))
                .itemOutputs(intermediate(RED_HOT_ADAMANTIUM, 1))
                .fluidOutputs(molten(Materials.Iron, 144))
                .duration(600)
                .eut(TierEU.RECIPE_LuV)
                .addTo(RecipeMaps.chemicalBathRecipes));
    }

    // 2. Mixer — chemical green dye + molten Oriharukon + molten Quantium → Vibranium Dye
    private static void vibDye() {
        safe(
            "vibranium dye",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(circuit(1))
                .fluidInputs(
                    fluid("dye.chemical.dyegreen", 1152),
                    molten(Materials.Oriharukon, 72),
                    molten(Materials.Quantium, 72))
                .fluidOutputs(fluid(PrPMaterials.VibraniumDye, 720))
                .duration(400)
                .eut(TierEU.RECIPE_LuV)
                .addTo(GTPPRecipeMaps.mixerNonCellRecipes));
    }

    // 3. Chem Bath — bathe Red Hot Adamantium in Vibranium Dye
    private static void vibVibrantAdamantium() {
        safe(
            "red hot vibrant adamantium",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(intermediate(RED_HOT_ADAMANTIUM, 1))
                .fluidInputs(fluid(PrPMaterials.VibraniumDye, 144))
                .itemOutputs(intermediate(RED_HOT_VIBRANT_ADAMANTIUM, 1))
                .duration(600)
                .eut(TierEU.RECIPE_LuV)
                .addTo(RecipeMaps.chemicalBathRecipes));
    }

    // 4. Infuse with Life Essence + molten Blood Infused Iron → bartworks-generated Vibranium hot ingot.
    // (Chem Reactor — it's a chemical infusion, not a freeze; the freeze is step 5.)
    private static void vibHotVibranium() {
        safe(
            "hot vibranium",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(intermediate(RED_HOT_VIBRANT_ADAMANTIUM, 1))
                .fluidInputs(fluid(Materials.LifeEssence, 10000), molten(Materials.BloodInfusedIron, 144))
                .itemOutputs(ingotHot(PrPMaterials.Vibranium, 1))
                .duration(800)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(RecipeMaps.multiblockChemicalReactorRecipes));
    }

    // =========================================================
    // UNOBTANIUM
    // =========================================================

    // 1. Chem Reactor — HF + SO3 → Fluorosulfuric Acid
    private static void unoFluorosulfuricAcid() {
        safe(
            "fluorosulfuric acid",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(circuit(1))
                .fluidInputs(fluid(Materials.HydrofluoricAcid, 1000), fluid(Materials.SulfurTrioxide, 1000))
                .fluidOutputs(fluid(PrPMaterials.FluorosulfuricAcid, 1000))
                .duration(200)
                .eut(TierEU.RECIPE_EV)
                .metadata(GTRecipeConstants.COIL_HEAT, 7300)
                .addTo(GTNHPPRecipeMaps.sHTRFRecipes));
    }

    // 2. Chem Reactor — Fluorosulfuric Acid + Antimony Pentafluoride → Magic Acid (fluoroantimonic)
    private static void unoMagicAcid() {
        safe(
            "magic acid",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(circuit(1))
                .fluidInputs(fluid(PrPMaterials.FluorosulfuricAcid, 1000), fluid("antimony pentafluoride", 1000))
                .fluidOutputs(fluid(PrPMaterials.MagicAcid, 1000))
                .duration(200)
                .eut(TierEU.RECIPE_IV)
                .addTo(RecipeMaps.multiblockChemicalReactorRecipes));
    }

    // 3. Dissolution (GT++ Dissolution Tank map isn't exposed here — using the LCR as a substitute):
    // 2 Ore Concentrate + Magic Acid + Superheated Steam → Dirty Slurry + Endstone dust
    private static void unoDissolution() {
        safe(
            "unobtanium dissolution",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(intermediate(UNOBTANIUM_ORE_CONCENTRATE, 2))
                .fluidInputs(fluid(PrPMaterials.MagicAcid, 500), fluid("ic2superheatedsteam", 8000))
                .itemOutputs(dust(Materials.Endstone, 1))
                .fluidOutputs(fluid(PrPMaterials.DirtyUnobtaniumSlurry, 900))
                .duration(400)
                .eut(TierEU.RECIPE_LuV)
                .metadata(GTRecipeConstants.COIL_HEAT, 7200)
                .addTo(GTNHPPRecipeMaps.sHTRFRecipes));
    }

    // 4. Chem Bath — wash the slurry with distilled water
    private static void unoWash() {
        safe(
            "unobtanium wash",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(circuit(1))
                .fluidInputs(fluid(PrPMaterials.DirtyUnobtaniumSlurry, 1000), fluid("ic2distilledwater", 1000))
                .itemOutputs(dust(Materials.Endstone, 1))
                .fluidOutputs(fluid(PrPMaterials.WashedUnobtaniumSlurry, 1000))
                .duration(300)
                .eut(TierEU.RECIPE_LuV)
                .addTo(RecipeMaps.chemicalBathRecipes));
    }

    // 5. Autoclave — crystallise the washed slurry
    private static void unoCrystallize() {
        safe(
            "unobtanium crystallize",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(circuit(1))
                .fluidInputs(fluid(PrPMaterials.WashedUnobtaniumSlurry, 500))
                .itemOutputs(intermediate(UNOBTANIUM_CRYSTAL_FRAGMENT, 2))
                .fluidOutputs(fluid("ic2distilledwater", 50))
                .duration(400)
                .eut(TierEU.RECIPE_LuV)
                .addTo(RecipeMaps.autoclaveRecipes));
    }

    // 6. Laser Engraver — purify crystals with Europium Chloride
    private static void unoLaserPurify() {
        safe(
            "unobtanium laser purify",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(intermediate(UNOBTANIUM_CRYSTAL_FRAGMENT, 2))
                .fluidInputs(fluid(PrPMaterials.EuropiumChloride, 500))
                .itemOutputs(intermediate(PURIFIED_UNOBTANIUM_CRYSTAL, 2))
                .fluidOutputs(fluid(PrPMaterials.EuropiumChlorideSolution, 500))
                .duration(500)
                .eut(TierEU.RECIPE_LuV)
                .addTo(RecipeMaps.laserEngraverRecipes));
    }

    // 6b. DT Tower — recover Europium Chloride from the spent solution
    private static void unoEuropiumChlorideRecovery() {
        safe(
            "europium chloride recovery",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(circuit(1))
                .fluidInputs(fluid(PrPMaterials.EuropiumChlorideSolution, 1000))
                .fluidOutputs(fluid(PrPMaterials.EuropiumChloride, 1000), fluid(Materials.Water, 500))
                .duration(300)
                .eut(TierEU.RECIPE_LuV)
                .addTo(RecipeMaps.distillationTowerRecipes));

        safe(
            "europium chloride recovery",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(circuit(1))
                .fluidInputs(fluid(Materials.Chlorine, 1000), molten(Materials.Europium, 144))
                .fluidOutputs(fluid(PrPMaterials.EuropiumChloride, 1000))
                .duration(120)
                .eut(TierEU.RECIPE_IV)
                .addTo(RecipeMaps.multiblockChemicalReactorRecipes));
    }

    // 7. Thermal Centrifuge — shatter purified crystals into shards
    private static void unoThermalCentrifuge() {
        safe(
            "unobtanium centrifuge",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(intermediate(PURIFIED_UNOBTANIUM_CRYSTAL, 2))
                .itemOutputs(intermediate(PURIFIED_UNOBTANIUM_SHARD, 4))
                .duration(400)
                .eut(TierEU.RECIPE_LuV)
                .addTo(RecipeMaps.thermalCentrifugeRecipes));
    }

    // 8. Chem Reactor — reduce shards with Fiery Steel into clumps
    private static void unoClumps() {
        safe(
            "unobtanium clumps",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(intermediate(PURIFIED_UNOBTANIUM_SHARD, 4))
                .fluidInputs(molten(Materials.FierySteel, 144))
                .itemOutputs(intermediate(UNOBTANIUM_CLUMP, 1), intermediate(IRON_SLAG, 1))
                .duration(400)
                .eut(TierEU.RECIPE_LuV)
                .addTo(RecipeMaps.multiblockChemicalReactorRecipes));
    }

    // 9. Centrifuge — break a clump into Unobtanium dust
    private static void unoCentrifuge() {
        safe(
            "unobtanium Centrifuge",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(intermediate(UNOBTANIUM_CLUMP, 1))
                .itemOutputs(dust(PrPMaterials.Unobtanium, 1))
                .duration(300)
                .eut(TierEU.RECIPE_LuV)
                .addTo(RecipeMaps.centrifugeRecipes));
    }

    // -------------------------------------------------------------------------
    // Late-LuV entry: Endstone is acid-leached into a single Ore Concentrate. Deliberately
    // brutal — a huge Endstone batch plus two full buckets of precious Magic Acid for ONE
    // concentrate — so it only exists to bootstrap Unobtanium before ZPM. Once you have ZPM
    // components you build the Void Miner (see registerEndVoidMinerDrop) for the efficient bulk.
    // -------------------------------------------------------------------------
    private static void unoEndConcentration() {
        safe(
            "unobtanium end concentration",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(dust(Materials.Endstone, 256), circuit(4))
                .fluidInputs(fluid(PrPMaterials.MagicAcid, 2000))
                .itemOutputs(intermediate(UNOBTANIUM_ORE_CONCENTRATE, 1))
                .duration(360 * 20)
                .eut(TierEU.RECIPE_LuV)
                .metadata(GTRecipeConstants.COIL_HEAT, 7200)
                .addTo(GTNHPPRecipeMaps.sHTRFRecipes));
    }

    // Registers the Ore Concentrate as a Void Miner drop in the vanilla End ("The End", dim 1) —
    // the efficient ZPM bulk source. The map is built once during bartworks init, so adding here
    // (loadComplete) persists. Wrapped in safe() in case GalacticGreg/the End map isn't present.
    private static void registerEndVoidMinerDrop() {
        safe(
            "end void-miner unobtanium drop",
            () -> VoidMinerUtility.dropMapsByDimName.computeIfAbsent("The End", k -> new VoidMinerUtility.DropMap())
                .addDrop(intermediate(UNOBTANIUM_ORE_CONCENTRATE, 1), 10.0f));
    }
}
