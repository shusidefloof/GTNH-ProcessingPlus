package com.gtnh.processingplus.recipes.chains;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import java.util.Collection;

import com.gtnh.processingplus.machines.MachineType;
import com.gtnh.processingplus.machines.SPCRecipeData;
import com.gtnh.processingplus.materials.PrPMaterials;
import com.gtnh.processingplus.recipes.GTNHPPRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;

public class SPCRecipes {

    // GT voltage tier constants (used in SPCRecipeData min-tier arrays)
    // ULV=0 LV=1 MV=2 HV=3 EV=4 IV=5 LuV=6 ZPM=7 UV=8
    private static final int MV = 2;
    private static final int HV = 3;
    private static final int EV = 4;
    private static final int IV = 5;
    private static final int LuV = 6;
    private static final int UV = 8;

    public static void init() {
        fr4FiberglassBoard();
        multifiberglassBoard();
        fiberglassAdvancedBoard();
        multifiberglassEliteBoard();
    }

    // -------------------------------------------------------------------------
    // Multilayer Fiber-Reinforced Circuit Board
    //
    // Upgrades an Advanced Fiberglass board into a multilayer laminate.
    //
    // Sequence: [Mixer LuV] → [Laser Engraver LuV] → [Electrolyzer LuV]
    // → [Laser Engraver LuV] → [Chemical Bath LuV]
    // Step 1 — bond Kapton dielectric layers with PAA adhesive (mixer)
    // Step 2 — UV-expose outer copper trace pattern (laser engraver)
    // Step 3 — electroplate buried vias and outer traces (electrolyzer)
    // Step 4 — UV-expose inner layer pattern (laser engraver)
    // Step 5 — etch and strip residual photoresist (chem bath)
    //
    // Input: Circuit_Board_Fiberglass(2) + Kapton(2) + Palladium foil(8)
    // → Circuit_Board_Multifiberglass
    // -------------------------------------------------------------------------
    private static void multifiberglassBoard() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Fiberglass.get(2),
                plate(PrPMaterials.Kapton, 2),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Palladium, 8),
                circuit(8))
            .fluidInputs(
                fluid(Materials.SulfuricAcid, 750),
                fluid(PrPMaterials.AdvancedPhotoresist, 1000),
                fluid(PrPMaterials.PAASolution, 500))
            .itemOutputs(ItemList.Circuit_Board_Multifiberglass.get(1))
            .duration(20 * 30)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);

        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.MIXER, MachineType.LASER_ENGRAVER, MachineType.ELECTROLYZER,
                MachineType.LASER_ENGRAVER, MachineType.CHEMICAL_BATH },
            new int[] { HV, HV, HV, HV, HV });
    }

    // -------------------------------------------------------------------------
    // FR4 Fiberglass Circuit Board
    //
    // Sequence: [Chemical Bath MV] → [Laser Engraver MV] → [Chemical Bath MV]
    // Step 1 — coat prepreg surface with etchant solution (chem bath)
    // Step 2 — UV-expose the copper traces through a photomask (laser engraver)
    // Step 3 — etch exposed copper, strip remaining photoresist (chem bath)
    //
    // No support machine required.
    // Base output: 2 boards. Tier-excess bonus: 2^n parallels + flat +1 per stack.
    // -------------------------------------------------------------------------
    private static void fr4FiberglassBoard() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(
                plate(Materials.EpoxidFiberReinforced, 2),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Aluminium, 4),
                circuit(4))
            .fluidInputs(fluid(Materials.SulfuricAcid, 500), fluid(PrPMaterials.BasicPhotoresist, 500))
            .itemOutputs(ItemList.Circuit_Board_Fiberglass.get(2))
            .duration(7 * 20)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);

        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.LASER_ENGRAVER, MachineType.CHEMICAL_BATH },
            new int[] { MV, MV, MV });
    }

    // -------------------------------------------------------------------------
    // Advanced Fiberglass Circuit Board
    //
    // Upgrades a Fiberglass board with palladium traces and PAA bonding layer.
    //
    // Sequence: [Mixer IV] → [Laser Engraver IV] → [Electrolyzer IV] → [Chemical Bath IV]
    // Step 1 — apply PAASolution as dielectric bonding layer (mixer)
    // Step 2 — UV-expose fine Pd trace pattern (laser engraver)
    // Step 3 — electroplate palladium into exposed channels (electrolyzer)
    // Step 4 — strip photoresist, acid rinse (chem bath)
    //
    // PAASolution cross-chain dependency: requires Kapton chain infrastructure.
    // Input: Circuit_Board_Fiberglass(1) + Pd foil(6) → Circuit_Board_Fiberglass_Advanced
    // -------------------------------------------------------------------------
    private static void fiberglassAdvancedBoard() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Fiberglass.get(1),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Palladium, 6),
                circuit(7))
            .fluidInputs(
                fluid(Materials.SulfuricAcid, 500),
                fluid(PrPMaterials.IVPhotoresist, 750),
                fluid(PrPMaterials.PAASolution, 250))
            .itemOutputs(ItemList.Circuit_Board_Fiberglass_Advanced.get(1))
            .duration(1000)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);

        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.MIXER, MachineType.LASER_ENGRAVER, MachineType.ELECTROLYZER,
                MachineType.CHEMICAL_BATH },
            new int[] { IV, IV, IV, IV });
    }

    // -------------------------------------------------------------------------
    // Elite Multilayer Circuit Board
    //
    // Bonds Carbon Fiber Composite layers into the multilayer stack for maximum
    // mechanical stiffness and thermal conductivity at UV operating temperatures.
    //
    // Sequence: [Mixer UV] → [Laser Engraver UV] → [Electrolyzer UV]
    // → [Laser Engraver UV] → [Chemical Bath UV]
    //
    // PAAAdhesive cross-chain dependency: requires full Kapton → PAAAdhesive chain.
    // CF Composite cross-chain dependency: requires Carbon Fiber chain.
    // Input: Circuit_Board_Multifiberglass(2) + CF plate(2) + Pd foil(8)
    // → Circuit_Board_Multifiberglass_Elite
    // -------------------------------------------------------------------------
    private static void multifiberglassEliteBoard() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Multifiberglass.get(2),
                plate(PrPMaterials.CarbonFiberComposite, 2),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Palladium, 8),
                circuit(9))
            .fluidInputs(
                fluid(Materials.SulfuricAcid, 1000),
                fluid(PrPMaterials.LuVPhotoresist, 1500),
                fluid(PrPMaterials.PAAAdhesive, 500))
            .itemOutputs(ItemList.Circuit_Board_Multifiberglass_Elite.get(1))
            .duration(1600)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);

        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.MIXER, MachineType.LASER_ENGRAVER, MachineType.ELECTROLYZER,
                MachineType.LASER_ENGRAVER, MachineType.CHEMICAL_BATH },
            new int[] { UV, UV, UV, UV, UV });
    }
}
