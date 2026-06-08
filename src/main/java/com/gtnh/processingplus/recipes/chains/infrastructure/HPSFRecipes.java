package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeConstants;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class HPSFRecipes {

    public static void init() {
        controllerRecipe();
        casingRecipes();
        sinteringPath_Mix();
        sinteringPath_Compact();
        sinteringPath_Sinter();
        sinteringPath_Cool();
        meltingPath();
    }

    // SPC-style controller — IV assembler recipe.
    private static void controllerRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                new ItemStack(GTNHPPBlocks.CASINGS, 4, BlockGTNHPPCasings.HPSF_CASING),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 4),
                ItemList.Electric_Pump_EV.get(2),
                ItemList.Field_Generator_EV.get(1))
            .fluidInputs(molten(Materials.SolderingAlloy, 1152))
            .itemOutputs(GTNHPPBlocks.HPSF.getStackForm(1))
            .duration(800)
            .eut(TierEU.RECIPE_IV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // Casings used by the HPSF structure — assembler recipes.
    private static void casingRecipes() {
        // Hardened Pressure Vessel Casing
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 1),
                plate(Materials.TungstenSteel, 4),
                plate(Materials.Tungsten, 2),
                circuit(6))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.HPSF_CASING))
            .duration(50)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.assemblerRecipes);

        // High-Pressure Containment Casing (SCD casing — also used by the HPSF shell)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Titanium, 1),
                plate(Materials.Titanium, 4),
                plate(Materials.Polytetrafluoroethylene, 2),
                circuit(6))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.SCD_CASING))
            .duration(50)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // =========================================================
    // SINTERING PATH (EV)
    // Np dust + W dust + Ta dust + Ti dust
    // → Mixer → Compressor → HPSF → Vacuum Freezer → ingot
    // 4 input units : 1 ingot output
    // =========================================================

    // Step 1 — GT++ Mixer: blend four equimolar powders into homogeneous RHEA blend
    private static void sinteringPath_Mix() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                item("dustNeptunium", 1),
                dust(Materials.Tungsten, 1),
                dust(Materials.Tantalum, 1),
                dust(Materials.Titanium, 1))
            .itemOutputs(dust(PrPMaterials.RHEAPowderBlend, 4))
            .duration(200)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTPPRecipeMaps.mixerNonCellRecipes);
    }

    // Step 2 — Compressor: cold-press blend into a dense sintering compact
    private static void sinteringPath_Compact() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.RHEAPowderBlend, 4))
            .itemOutputs(dust(PrPMaterials.RHEASinteringCompact, 2))
            .duration(200)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.compressorRecipes);
    }

    // Step 3 — HPSF: hot-press compact under N₂ at 3000 K (requires Nichrome coils min.)
    // N₂ is consumed per cycle — ensure continuous supply.
    private static void sinteringPath_Sinter() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.RHEASinteringCompact, 1), circuit(1))
            .fluidInputs(fluid(Materials.Nitrogen, 2000))
            .itemOutputs(ingotHot(PrPMaterials.RefractoryHighEntropyAlloy, 1))
            .duration(600)
            .eut(TierEU.RECIPE_EV)
            .metadata(GTRecipeConstants.COIL_HEAT, 3000)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }

    // Step 4 — Vacuum Freezer: quench hot ingot → dense RHEA ingot
    private static void sinteringPath_Cool() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingotHot(PrPMaterials.RefractoryHighEntropyAlloy, 1))
            .itemOutputs(ingot(PrPMaterials.RefractoryHighEntropyAlloy, 1))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.vacuumFreezerRecipes);
    }

    // =========================================================
    // MELTING PATH (IV) — Alloy Blast Smelter
    // Same 4 input units → 2 ingots worth of molten RHEA (2× yield)
    // Skips powder prep and cooling step; faster and more efficient.
    // =========================================================
    private static void meltingPath() {
        GTValues.RA.stdBuilder()
            .itemInputs(item("ingotNeptunium", 1), dust(Materials.Tantalum, 1), dust(Materials.Titanium, 1))
            .fluidInputs(molten(Materials.Tungsten, 144))
            .fluidOutputs(molten(PrPMaterials.RefractoryHighEntropyAlloy, 288))
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTPPRecipeMaps.alloyBlastSmelterRecipes);
    }

}
