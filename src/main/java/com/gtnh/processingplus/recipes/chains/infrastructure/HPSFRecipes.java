package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeBuilder.TICKS;

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

    private static void controllerRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                new ItemStack(GTNHPPBlocks.CASINGS, 4, BlockGTNHPPCasings.HPSF_CASING),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 4),
                ItemList.Electric_Pump_EV.get(2),
                ItemList.Field_Generator_HV.get(2))
            .fluidInputs(molten(Materials.SolderingAlloy, 1152))
            .itemOutputs(GTNHPPBlocks.HPSF.getStackForm(1))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    private static void casingRecipes() {
        // Hardened Pressure Vessel Casing
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 1),
                plate(Materials.TungstenSteel, 4),
                plate(Materials.Tungsten, 2),
                circuit(6))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.HPSF_CASING))
            .duration(50 * TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.assemblerRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 1),
                plate(Materials.TungstenSteel, 4),
                plate(Materials.Tungsten, 4),
                circuit(6))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.PRESSURE_VESSEL_RING_CASING))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    private static void sinteringPath_Mix() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                item("dustNeptunium", 1),
                dust(Materials.Tungsten, 1),
                dust(Materials.Tantalum, 1),
                dust(Materials.Titanium, 1))
            .itemOutputs(dust(PrPMaterials.RHEAPowderBlend, 4))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.mixerNonCellRecipes);
    }

    private static void sinteringPath_Compact() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.RHEAPowderBlend, 4))
            .itemOutputs(dust(PrPMaterials.RHEASinteringCompact, 2))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.compressorRecipes);
    }

    private static void sinteringPath_Sinter() {
        GTValues.RA.stdBuilder()
            .itemInputs(dust(PrPMaterials.RHEASinteringCompact, 1), circuit(1))
            .fluidInputs(fluid(Materials.Nitrogen, 2000))
            .itemOutputs(ingotHot(PrPMaterials.RefractoryHighEntropyAlloy, 1))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .metadata(GTRecipeConstants.COIL_HEAT, 3000)
            .addTo(GTNHPPRecipeMaps.sHPSFRecipes);
    }
    
    private static void sinteringPath_Cool() {
        GTValues.RA.stdBuilder()
            .itemInputs(ingotHot(PrPMaterials.RefractoryHighEntropyAlloy, 1))
            .itemOutputs(ingot(PrPMaterials.RefractoryHighEntropyAlloy, 1))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.vacuumFreezerRecipes);
    }

    private static void meltingPath() {
        GTValues.RA.stdBuilder()
            .itemInputs(item("dustNeptunium", 1), dust(Materials.Tantalum, 1), dust(Materials.Titanium, 1))
            .fluidInputs(molten(Materials.Tungsten, 144))
            .fluidOutputs(molten(PrPMaterials.RefractoryHighEntropyAlloy, 576))
            .duration(100 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.alloyBlastSmelterRecipes);
    }

}
