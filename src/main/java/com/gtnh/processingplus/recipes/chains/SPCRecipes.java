package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import java.util.Collection;

import com.gtnh.processingplus.items.GTNHPPItems;
import com.gtnh.processingplus.machines.MachineType;
import com.gtnh.processingplus.machines.SPCRecipeData;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GTRecipe;

public class SPCRecipes {

    // GT voltage tier constants (used in SPCRecipeData min-tier arrays)
    // ULV=0 LV=1 MV=2 HV=3 EV=4 IV=5 LuV=6 ZPM=7 UV=8 UHV=9 UEV=10 UIV=11 UMV=12
    private static final int MV = 2;
    private static final int HV = 3;
    private static final int EV = 4;
    private static final int LuV = 6;
    private static final int UV = 8;
    private static final int UHV = 9;
    private static final int UEV = 10;

    public static void init() {
        epoxyBoard();
        epoxyBoardEngrave();
        fiberglassBoard();
        fiberglassBoardEngrave();
        multifiberglassBoard();
        multifiberglassBoardEngrave();
        wetwareBoard();
        wetwareBoardEngrave();
        bioBoard();
        bioBoardEngrave();
        opticalBoard();
        opticalBoardEngrave();
    }

    // -------------------------------------------------------------------------
    // Epoxy Circuit Board substrate — HV
    //
    // Sequence: [Chemical Bath MV] → [Laser Engraver HV] → [Chemical Bath MV]
    // -------------------------------------------------------------------------
    private static void epoxyBoard() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(plate(Materials.Epoxid, 1), foil(Materials.Gold, 8))
            .fluidInputs(fluid(Materials.SulfuricAcid, 500), fluid(Materials.HydrochloricAcid, 250))
            .itemOutputs(ItemList.Circuit_Board_Epoxy.get(1))
            .duration(3 * 20)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);

        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.LASER_ENGRAVER, MachineType.CHEMICAL_BATH },
            new int[] { MV, HV, MV });
    }

    // -------------------------------------------------------------------------
    // Epoxy Circuit Board engraving — HV → Epoxy Advanced
    //
    // Sequence: [Chemical Bath HV] → [Mixer EV] → [Laser Engraver HV] → [Chemical Bath HV]
    // -------------------------------------------------------------------------
    private static void epoxyBoardEngrave() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Circuit_Board_Epoxy.get(1), foil(Materials.Electrum, 8))
            .fluidInputs(fluid(PrPMaterials.EVPhotoresist, 500))
            .itemOutputs(ItemList.Circuit_Board_Epoxy_Advanced.get(1))
            .duration(3 * 20)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);

        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.MIXER, MachineType.LASER_ENGRAVER,
                MachineType.CHEMICAL_BATH },
            new int[] { HV, EV, HV, HV });
    }

    // -------------------------------------------------------------------------
    // FR4 Fiberglass Circuit Board substrate — HV
    //
    // Sequence: [Chemical Bath MV] → [Laser Engraver HV] → [Chemical Bath MV]
    // -------------------------------------------------------------------------
    private static void fiberglassBoard() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(
                plate(Materials.EpoxidFiberReinforced, 1),
                foil(Materials.Aluminium, 12),
                plate(Materials.Nickel, 1))
            .fluidInputs(fluid(Materials.SulfuricAcid, 500), fluid(Materials.HydrochloricAcid, 250))
            .itemOutputs(ItemList.Circuit_Board_Fiberglass.get(1))
            .duration(2 * 20)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);

        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.LASER_ENGRAVER, MachineType.CHEMICAL_BATH },
            new int[] { MV, HV, MV });
    }

    // -------------------------------------------------------------------------
    // Fiberglass Circuit Board engraving — HV → Fiberglass Advanced
    //
    // Sequence: [Chemical Bath MV] → [Laser Engraver HV] → [Assembler EV] → [Chemical Bath HV]
    // -------------------------------------------------------------------------
    private static void fiberglassBoardEngrave() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Fiberglass.get(1),
                foil(Materials.EnergeticAlloy, 12),
                foil(Materials.Palladium, 4))
            .fluidInputs(fluid(PrPMaterials.EVPhotoresist, 1000))
            .itemOutputs(ItemList.Circuit_Board_Fiberglass_Advanced.get(1))
            .duration(3 * 20)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);

        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.LASER_ENGRAVER, MachineType.ASSEMBLER,
                MachineType.CHEMICAL_BATH },
            new int[] { MV, HV, EV, HV });
    }

    // -------------------------------------------------------------------------
    // Multilayer Fiberglass Board substrate — LuV
    //
    // Sequence: [Chemical Bath HV] → [Laser Engraver LuV] → [Chemical Bath HV]
    // -------------------------------------------------------------------------
    private static void multifiberglassBoard() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Circuit_Board_Fiberglass_Advanced.get(2), foil(Materials.Iridium, 16))
            .fluidInputs(fluid(Materials.SulfuricAcid, 1000), fluid(PrPMaterials.LuVPhotoresist, 500))
            .itemOutputs(ItemList.Circuit_Board_Multifiberglass.get(1))
            .duration(3 * 20)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);

        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.LASER_ENGRAVER, MachineType.CHEMICAL_BATH },
            new int[] { HV, LuV, HV });
    }

    // -------------------------------------------------------------------------
    // Multilayer Fiberglass Board engraving — LuV → Multifiberglass Elite
    //
    // Sequence: [Chemical Bath HV] → [Laser Engraver LuV] → [Electrolyzer LuV] → [Chemical Bath HV]
    // -------------------------------------------------------------------------
    private static void multifiberglassBoardEngrave() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Circuit_Board_Multifiberglass.get(1), foil(Materials.Platinum, 8))
            .fluidInputs(fluid(PrPMaterials.LuVPhotoresist, 3000))
            .itemOutputs(ItemList.Circuit_Board_Multifiberglass_Elite.get(1))
            .duration(4 * 20)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);

        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.LASER_ENGRAVER, MachineType.ELECTROLYZER,
                MachineType.CHEMICAL_BATH },
            new int[] { HV, LuV, LuV, HV });
    }

    // -------------------------------------------------------------------------
    // Wetware Circuit Board substrate — UV
    //
    // Sequence: [Chemical Bath HV] → [Mixer UV] → [Laser Engraver UV] → [Chemical Bath HV]
    // -------------------------------------------------------------------------
    private static void wetwareBoard() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Multifiberglass.get(2),
                foil(Materials.NiobiumTitanium, 16),
                foil(Materials.NaquadahEnriched, 8),
                ItemList.Circuit_Parts_PetriDish.get(1))
            .fluidInputs(fluid(Materials.GrowthMediumRaw, 500), fluid(PrPMaterials.ZPMPhotoresist, 1000))
            .itemOutputs(ItemList.Circuit_Board_Wetware.get(1))
            .duration(3 * 20)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);

        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.MIXER, MachineType.LASER_ENGRAVER,
                MachineType.CHEMICAL_BATH },
            new int[] { HV, UV, UV, HV });
    }

    // -------------------------------------------------------------------------
    // Wetware Circuit Board engraving — UV → Wetware Extreme
    //
    // Sequence: [Chemical Bath HV] → [Electrolyzer UV] → [Laser Engraver UV] → [Chemical Bath UV]
    // -------------------------------------------------------------------------
    private static void wetwareBoardEngrave() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Wetware.get(1),
                foil(Materials.NiobiumTitanium, 64),
                foil(Materials.Naquadah, 16))
            .fluidInputs(fluid(PrPMaterials.UVPhotoresist, 5000))
            .itemOutputs(ItemList.Circuit_Board_Wetware_Extreme.get(1))
            .duration(2 * 20)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);

        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.ELECTROLYZER, MachineType.LASER_ENGRAVER,
                MachineType.CHEMICAL_BATH },
            new int[] { HV, UV, UV, UV });
    }

    // -------------------------------------------------------------------------
    // Bio Circuit Board substrate — UV
    //
    // Sequence: [Chemical Bath UV] → [Mixer UHV] → [Laser Engraver UHV] → [Chemical Bath UV]
    // -------------------------------------------------------------------------
    private static void bioBoard() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Wetware.get(1),
                foil(Materials.Neutronium, 16),
                foil(Materials.SuperconductorUVBase, 8),
                ItemList.Circuit_Parts_PetriDish.get(4))
            .fluidInputs(fluid(Materials.GrowthMediumSterilized, 1000), fluid(PrPMaterials.UVPhotoresist, 500))
            .itemOutputs(ItemList.Circuit_Board_Bio.get(1))
            .duration(30 * 20)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);

        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.MIXER, MachineType.LASER_ENGRAVER,
                MachineType.CHEMICAL_BATH },
            new int[] { UV, UHV, UHV, UV });
    }

    // -------------------------------------------------------------------------
    // Bio Circuit Board engraving — UHV → Bio Ultra
    //
    // Sequence: [Chemical Bath UV] → [Mixer UHV] → [Laser Engraver UHV] → [Chemical Bath UHV]
    // -------------------------------------------------------------------------
    private static void bioBoardEngrave() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Bio.get(2),
                foil(Materials.Neutronium, 24),
                foil(Materials.Osmium, 8),
                ItemList.Circuit_Parts_PetriDish.get(1))
            .fluidInputs(fluid(PrPMaterials.UHVPhotoresist, 1000))
            .itemOutputs(ItemList.Circuit_Board_Bio_Ultra.get(1))
            .duration(25 * 20)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);

        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.MIXER, MachineType.LASER_ENGRAVER,
                MachineType.CHEMICAL_BATH },
            new int[] { UV, UHV, UHV, UHV });
    }

    // -------------------------------------------------------------------------
    // Optical Circuit Board substrate — UEV (custom intermediate item)
    //
    // Sequence: [Chemical Bath UHV] → [Mixer UEV] → [Laser Engraver UEV] → [Chemical Bath UHV]
    // -------------------------------------------------------------------------
    private static void opticalBoard() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(plate(Materials.Kevlar, 2), item("foilTairitsu", 16), foil(Materials.Osmium, 8))
            .fluidInputs(fluid(Materials.Grade7PurifiedWater, 500))
            .itemOutputs(GTNHPPItems.opticalBoardRaw(1))
            .duration(22 * 20)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);

        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.MIXER, MachineType.LASER_ENGRAVER,
                MachineType.CHEMICAL_BATH },
            new int[] { UHV, UEV, UEV, UHV });
    }

    // -------------------------------------------------------------------------
    // Optical Circuit Board engraving — UEV → Circuit_Board_Optical
    //
    // Sequence: [Laser Engraver UEV] → [Electrolyzer UEV] → [Chemical Bath UHV] → [Laser Engraver UEV]
    // -------------------------------------------------------------------------
    private static void opticalBoardEngrave() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(
                GTNHPPItems.opticalBoardRaw(1),
                item("foilChromaticGlass", 16),
                plate(Materials.MysteriousCrystal, 4))
            .fluidInputs(fluid(PrPMaterials.UEVPhotoresist, 1000))
            .itemOutputs(ItemList.Circuit_Board_Optical.get(2))
            .duration(25 * 20)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);

        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.LASER_ENGRAVER, MachineType.ELECTROLYZER, MachineType.CHEMICAL_BATH,
                MachineType.LASER_ENGRAVER },
            new int[] { UEV, UEV, UHV, UEV });
    }
}
