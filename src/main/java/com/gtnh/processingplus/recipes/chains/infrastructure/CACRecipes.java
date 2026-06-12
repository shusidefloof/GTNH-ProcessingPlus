package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeConstants.AssemblyLine;
import static gregtech.api.util.GTRecipeConstants.RESEARCH_ITEM;
import static gregtech.api.util.GTRecipeConstants.SCANNING;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.GTNHProcessingPlus;
import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;
import com.gtnh.processingplus.recipes.PPRecipeHelper;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.recipe.Scanning;

public class CACRecipes {

    /**
     * The superconductors the CAC takes over. NOTE the deliberate omission of SuperconductorUV: GT's
     * naming is offset by one, so SuperconductorUV is the UHV-tier conductor — but it's also what UV
     * energy hatches are built from, and you need those to REACH UHV (and thus aerogel and the CAC).
     * Gating it here would soft-lock progression, so it stays on its stock assembler recipe. The CAC
     * only takes the genuinely post-UHV conductors (SuperconductorUHV and up).
     */
    private static final Materials[] CAC_SUPERCONDUCTORS = { Materials.SuperconductorUHV,
        Materials.SuperconductorUEV, Materials.SuperconductorUIV, Materials.SuperconductorUMV };

    public static void init() {
        casingRecipes();
        controllerRecipe();
    }

    // -------------------------------------------------------------------------
    // Casing crafts.
    // -------------------------------------------------------------------------
    private static void casingRecipes() {
        // Cryostat Vacuum Casing — the double-walled vacuum shell, aerogel-backed.
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 1),
                plate(Materials.Aluminium, 8),
                plate(PrPMaterials.SilicaAerogel, 4),
                ItemList.Electric_Piston_LuV.get(2),
                foil(Materials.StainlessSteel, 4))
            .fluidInputs(fluid("molten.solderingalloy", 288))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.CRYOSTAT_VACUUM_CASING))
            .eut(TierEU.RECIPE_LuV)
            .duration(10 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // Aerogel Insulation Block — the load-bearing inner lining. Consumes BOTH aerogel products,
        // so even building the cryostat routes through the whole aerogel chain.
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 1),
                plate(PrPMaterials.HydrophobicSilicaAerogel, 8),
                plate(PrPMaterials.AerogelInsulationPanel, 4),
                ItemList.Electric_Piston_LuV.get(2))
            .fluidInputs(fluid("molten.epoxid", 288))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.AEROGEL_INSULATION_BLOCK))
            .eut(TierEU.RECIPE_LuV)
            .duration(10 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // -------------------------------------------------------------------------
    // Controller — assembly-line recipe (UHV), scanned from a UHV hull.
    // -------------------------------------------------------------------------
    private static void controllerRecipe() {
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Hull_UV.get(1))
            .metadata(SCANNING, new Scanning(60 * SECONDS, TierEU.RECIPE_UHV))
            .itemInputs(
                ItemList.Hull_UV.get(16),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 16),
                new ItemStack(GTNHPPBlocks.CASINGS, 8, BlockGTNHPPCasings.AEROGEL_INSULATION_BLOCK),
                plate(PrPMaterials.HydrophobicSilicaAerogel, 64),
                plate(PrPMaterials.HydrophobicSilicaAerogel, 64),
                ItemList.Electric_Piston_UHV.get(32),
                ItemList.Electric_Pump_UHV.get(32),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UV, 16),
                GTOreDictUnificator.get(OrePrefixes.cableGt08, Materials.SuperconductorUV, 8))
            .fluidInputs(fluid("molten.indalloy140",  11520), PrPMaterials.PrometheanNaquadria.getMolten(10368), molten(Materials.SuperconductorUVBase, 2304))
            .itemOutputs(GTNHPPBlocks.CAC.getStackForm(1))
            .eut(TierEU.RECIPE_UHV)
            .duration(60 * SECONDS)
            .addTo(AssemblyLine);
    }

    // -------------------------------------------------------------------------
    // Superconductor migration — copies every UHV+ superconductor anneal recipe out of the assembler
    // into the CAC, injecting an aerogel multilayer-insulation tax, then deletes the originals so the
    // CAC is the ONLY path to UHV-and-up superconductors. Run from loadComplete (after GT's recipes).
    // -------------------------------------------------------------------------
    public static void migrateSuperconductors() {
        ItemStack[] targets = new ItemStack[CAC_SUPERCONDUCTORS.length];
        for (int i = 0; i < CAC_SUPERCONDUCTORS.length; i++) {
            targets[i] = GTOreDictUnificator.get(OrePrefixes.wireGt01, CAC_SUPERCONDUCTORS[i], 1);
        }

        // Snapshot the matching anneal recipes before touching either map.
        List<GTRecipe> matched = new ArrayList<>();
        for (GTRecipe r : RecipeMaps.assemblerRecipes.getAllRecipes()) {
            if (r.mOutputs == null || r.mOutputs.length == 0) continue;
            if (matchesAny(r.mOutputs[0], targets)) matched.add(r);
        }

        // Re-add each into the CAC map with the aerogel tax appended.
        int migrated = 0;
        for (GTRecipe r : matched) {
            GTRecipe copy = r.copy();
            copy.mInputs = appendItems(
                copy.mInputs,
                plate(PrPMaterials.AerogelInsulationPanel, 2),
                plate(PrPMaterials.HydrophobicSilicaAerogel, 1));
            GTNHPPRecipeMaps.sCACRecipes.addRecipe(copy);
            migrated++;
        }

        // Delete the originals from the assembler so the CAC is the only route.
        int removed = 0;
        for (ItemStack t : targets) {
            removed += PPRecipeHelper.removeRecipesByOutput(RecipeMaps.assemblerRecipes, t);
        }
        GTNHProcessingPlus.LOG.info(
            "CAC: migrated {} UHV+ superconductor anneal recipe(s) into the cryostat, removed {} from the assembler.",
            migrated,
            removed);
    }

    private static boolean matchesAny(ItemStack stack, ItemStack[] set) {
        if (stack == null) return false;
        for (ItemStack t : set) {
            if (t != null && GTUtility.areStacksEqual(stack, t)) return true;
        }
        return false;
    }

    private static ItemStack[] appendItems(ItemStack[] arr, ItemStack... add) {
        int base = (arr == null) ? 0 : arr.length;
        ItemStack[] out = new ItemStack[base + add.length];
        if (arr != null) System.arraycopy(arr, 0, out, 0, base);
        for (int i = 0; i < add.length; i++) out[base + i] = add[i];
        return out;
    }
}
