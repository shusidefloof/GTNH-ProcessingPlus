package com.gtnh.processingplus.recipes.chains.materials;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import com.gtnh.processingplus.items.GTNHPPItems;
import com.gtnh.processingplus.materials.PrPMaterials;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
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
        vibVibranium();

        // Unobtanium (9 steps) — SbF5 comes from GoodGenerator (fluid "antimony pentafluoride")
        unoFluorosulfuricAcid();
        unoMagicAcid();
        unoDissolution();
        unoWash();
        unoCrystallize();
        unoLaserPurify();
        unoEuropiumChlorideRecovery();
        unoCentrifuge();
        unoClumps();
        unoElectrolyze();
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
                .itemOutputs(GTNHPPItems.stack(GTNHPPItems.RED_HOT_ADAMANTIUM, 1))
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
                    molten(Materials.Oriharukon, 36),
                    molten(Materials.Quantium, 36))
                .fluidOutputs(fluid(PrPMaterials.VibraniumDye, 1440))
                .duration(400)
                .eut(TierEU.RECIPE_LuV)
                .addTo(GTPPRecipeMaps.mixerNonCellRecipes));
    }

    // 3. Chem Bath — bathe Red Hot Adamantium in Vibranium Dye
    private static void vibVibrantAdamantium() {
        safe(
            "red hot vibrant adamantium",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(GTNHPPItems.stack(GTNHPPItems.RED_HOT_ADAMANTIUM, 1))
                .fluidInputs(fluid(PrPMaterials.VibraniumDye, 144))
                .itemOutputs(GTNHPPItems.stack(GTNHPPItems.RED_HOT_VIBRANT_ADAMANTIUM, 1))
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
                .itemInputs(GTNHPPItems.stack(GTNHPPItems.RED_HOT_VIBRANT_ADAMANTIUM, 1))
                .fluidInputs(fluid(Materials.LifeEssence, 10000), molten(Materials.BloodInfusedIron, 144))
                .itemOutputs(PrPMaterials.Vibranium.get(OrePrefixes.ingotHot, 1))
                .duration(800)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(RecipeMaps.multiblockChemicalReactorRecipes));
    }

    // 5. Vac Freezer — cool the hot ingot into the finished metal (explicit: Vibranium has no
    // blast-furnace stats, so GT doesn't auto-generate this freezer recipe).
    private static void vibVibranium() {
        safe(
            "vibranium ingot",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(PrPMaterials.Vibranium.get(OrePrefixes.ingotHot, 1))
                .itemOutputs(ingot(PrPMaterials.Vibranium, 1))
                .duration(300)
                .eut(TierEU.RECIPE_LuV)
                .addTo(RecipeMaps.vacuumFreezerRecipes));
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
                .addTo(RecipeMaps.multiblockChemicalReactorRecipes));
    }

    // 2. Chem Reactor — Fluorosulfuric Acid + Antimony Pentafluoride → Magic Acid (fluoroantimonic)
    private static void unoMagicAcid() {
        safe(
            "magic acid",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(circuit(1))
                .fluidInputs(
                    fluid(PrPMaterials.FluorosulfuricAcid, 1000),
                    fluid("antimony pentafluoride", 1000))
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
                .itemInputs(GTNHPPItems.stack(GTNHPPItems.UNOBTANIUM_ORE_CONCENTRATE, 2))
                .fluidInputs(fluid(PrPMaterials.MagicAcid, 500), fluid(Materials.DenseSuperheatedSteam, 8000))
                .itemOutputs(dust(Materials.Endstone, 1))
                .fluidOutputs(fluid(PrPMaterials.DirtyUnobtaniumSlurry, 900))
                .duration(400)
                .eut(TierEU.RECIPE_LuV)
                .addTo(RecipeMaps.multiblockChemicalReactorRecipes));
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
                .itemOutputs(GTNHPPItems.stack(GTNHPPItems.UNOBTANIUM_CRYSTAL_FRAGMENT, 2))
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
                .itemInputs(GTNHPPItems.stack(GTNHPPItems.UNOBTANIUM_CRYSTAL_FRAGMENT, 2))
                .fluidInputs(fluid(PrPMaterials.EuropiumChloride, 500))
                .itemOutputs(GTNHPPItems.stack(GTNHPPItems.PURIFIED_UNOBTANIUM_CRYSTAL, 2))
                .fluidOutputs(fluid(PrPMaterials.EuropiumChlorideSolution, 500))
                .duration(500)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(RecipeMaps.laserEngraverRecipes));
    }

    // 6b. DT Tower — recover Europium Chloride from the spent solution
    private static void unoEuropiumChlorideRecovery() {
        safe(
            "europium chloride recovery",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(circuit(1))
                .fluidInputs(fluid(PrPMaterials.EuropiumChlorideSolution, 1000))
                .fluidOutputs(fluid(PrPMaterials.EuropiumChloride, 500), fluid(Materials.Water, 500))
                .duration(300)
                .eut(TierEU.RECIPE_LuV)
                .addTo(RecipeMaps.distillationTowerRecipes));
    }

    // 7. Thermal Centrifuge — shatter purified crystals into shards
    private static void unoCentrifuge() {
        safe(
            "unobtanium centrifuge",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(GTNHPPItems.stack(GTNHPPItems.PURIFIED_UNOBTANIUM_CRYSTAL, 2))
                .itemOutputs(
                    GTNHPPItems.stack(GTNHPPItems.PURIFIED_UNOBTANIUM_SHARD, 4),
                    dustSmall(PrPMaterials.Unobtanium, 1))
                .duration(400)
                .eut(TierEU.RECIPE_LuV)
                .addTo(RecipeMaps.thermalCentrifugeRecipes));
    }

    // 8. Chem Reactor — reduce shards with Fiery Steel into clumps
    private static void unoClumps() {
        safe(
            "unobtanium clumps",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(GTNHPPItems.stack(GTNHPPItems.PURIFIED_UNOBTANIUM_SHARD, 4))
                .fluidInputs(molten(Materials.FierySteel, 144))
                .itemOutputs(
                    GTNHPPItems.stack(GTNHPPItems.UNOBTANIUM_CLUMP, 5),
                    GTNHPPItems.stack(GTNHPPItems.IRON_SLAG, 1))
                .duration(400)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(RecipeMaps.multiblockChemicalReactorRecipes));
    }

    // 9. Electrolyzer — break a clump into Unobtanium dust
    private static void unoElectrolyze() {
        safe(
            "unobtanium electrolyze",
            () -> GTValues.RA.stdBuilder()
                .itemInputs(GTNHPPItems.stack(GTNHPPItems.UNOBTANIUM_CLUMP, 1))
                .itemOutputs(dust(PrPMaterials.Unobtanium, 5))
                .duration(300)
                .eut(TierEU.RECIPE_LuV)
                .addTo(RecipeMaps.electrolyzerRecipes));
    }
}
