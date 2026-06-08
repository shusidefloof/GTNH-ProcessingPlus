package com.gtnh.processingplus.recipes.chains.infrastructure;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.*;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.gtnh.processingplus.blocks.BlockGTNHPPCasings;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;
import com.gtnh.processingplus.items.GTNHPPItems;
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
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;

public class SPCRecipes {

    // Voltage tier indices used in SPCRecipeData min-tier arrays
    private static final int MV = 2, HV = 3, EV = 4, IV = 5,LuV = 6, ZPM = 7 ,UV = 8, UHV = 9, UEV = 10;

    public static void init() {
        casingRecipe();
        controllerRecipe();
        moduleBlockRecipes();
        scorchedRecycling();
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
        premiumBoardRecipes();
    }

    // SPC controller — IV assembler recipe.
    private static void controllerRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Hull_EV.get(1),
                item("rotorVibrantAlloy", 4),
                ItemList.Sensor_EV.get(2),
                ItemList.Emitter_EV.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 4))
            .fluidInputs(molten(Materials.Epoxid, 1152))
            .itemOutputs(GTNHPPBlocks.SPC.getStackForm(1))
            .duration(600)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // Scorched Circuit Board (SPC failure output) — macerate the burnt board back into ash.
    private static void scorchedRecycling() {
        GTValues.RA.stdBuilder()
            .itemInputs(GTNHPPItems.scorchedBoard(1))
            .itemOutputs(dust(Materials.Ash, 1))
            .duration(160)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.maceratorRecipes);
    }

    // Spectral Isolation Casing — HV assembler recipe (hatch-capable shell)
    private static void casingRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.StainlessSteel, 1),
                plate(Materials.StainlessSteel, 4),
                plate(Materials.Aluminium, 4),
                plate(Materials.Polytetrafluoroethylene, 2),
                circuit(10))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.SPC_CASING))
            .duration(50)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.assemblerRecipes);

        // Photonic Alignment Casing — central beam column
        GTValues.RA.stdBuilder()
            .itemInputs(
                new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.SPC_CASING),
                plate(Materials.Aluminium, 2),
                foil(Materials.Silver, 4),
                circuit(10))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.SPC_BEAM_CASING))
            .duration(50)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.assemblerRecipes);

        // Spectral Frame Casing — structural frame
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.StainlessSteel, 1),
                plate(Materials.StainlessSteel, 6),
                circuit(10))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.SPC_FRAME_CASING))
            .duration(50)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // SPC upgrade adapters + external module casings — assembler recipes.
    private static void moduleBlockRecipes() {
        // --- Support-bay adapters (the physical "port" routed into the SPC) ---
        // Bio-Lithography Adapter — gates wetware / bio boards
        GTValues.RA.stdBuilder()
            .itemInputs(
                new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.SPC_CASING),
                ItemList.Circuit_Parts_PetriDish.get(2),
                plate(Materials.Kevlar, 2),
                circuit(12))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.BIO_ADAPTER))
            .duration(600)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.assemblerRecipes);

        // Cryo-Stabilization Adapter — gates optical boards / efficiency
        GTValues.RA.stdBuilder()
            .itemInputs(
                new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.SPC_CASING),
                plate(Materials.Polytetrafluoroethylene, 2),
                foil(Materials.NiobiumTitanium, 4),
                circuit(12))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.CRYO_ADAPTER))
            .duration(600)
            .eut(TierEU.RECIPE_IV)
            .addTo(RecipeMaps.assemblerRecipes);

        // Quantum Alignment Adapter — gates UEV+ / optical-quantum tiers
        GTValues.RA.stdBuilder()
            .itemInputs(
                new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.SPC_BEAM_CASING),
                plate(Materials.Osmium, 2),
                foil(Materials.Naquadah, 8),
                plate(PrPMaterials.Nylon66, 2),
                circuit(13))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.QUANTUM_ADAPTER))
            .duration(800)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.assemblerRecipes);

        // --- External module casings ---
        GTValues.RA.stdBuilder()
            .itemInputs(plate(Materials.StainlessSteel, 6), ItemList.Circuit_Parts_PetriDish.get(1), circuit(10))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.BIO_MODULE_CASING))
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(RecipeMaps.assemblerRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(plate(Materials.Aluminium, 6), foil(Materials.NiobiumTitanium, 2), circuit(10))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.CRYO_MODULE_CASING))
            .duration(400)
            .eut(TierEU.RECIPE_IV)
            .addTo(RecipeMaps.assemblerRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                plate(Materials.Osmium, 6),
                foil(Materials.Naquadah, 4),
                plate(PrPMaterials.Nylon66, 2),
                circuit(13))
            .itemOutputs(new ItemStack(GTNHPPBlocks.CASINGS, 1, BlockGTNHPPCasings.QUANTUM_MODULE_CASING))
            .duration(600)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    // Epoxy Board — [Chem Bath MV] → [Laser HV] → [Chem Bath MV]
    private static void epoxyBoard() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(plate(Materials.Epoxid, 1), foil(Materials.Gold, 8))
            .fluidInputs(fluid(Materials.SulfuricAcid, 500))
            .itemOutputs(ItemList.Circuit_Board_Epoxy.get(1))
            .duration(4 * 20)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.LASER_ENGRAVER, MachineType.CHEMICAL_BATH },
            new int[] { MV, HV, MV });
    }

    // Epoxy Board → Advanced — [Chem Bath HV] → [Mixer EV] → [Laser HV] → [Chem Bath HV]
    private static void epoxyBoardEngrave() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Circuit_Board_Epoxy.get(1), foil(Materials.Electrum, 8))
            .fluidInputs(fluid(PrPMaterials.EVPhotoresist, 500))
            .itemOutputs(ItemList.Circuit_Board_Epoxy_Advanced.get(1))
            .duration(4 * 20)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.MIXER, MachineType.LASER_ENGRAVER,
                MachineType.CHEMICAL_BATH },
            new int[] { HV, EV, HV, HV });
    }

    // Fiberglass Board — [Chem Bath MV] → [Laser HV] → [Chem Bath MV]
    private static void fiberglassBoard() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(plate(Materials.EpoxidFiberReinforced, 1), foil(Materials.Aluminium, 12))
            .fluidInputs(fluid(Materials.SulfuricAcid, 500))
            .itemOutputs(ItemList.Circuit_Board_Fiberglass.get(1))
            .duration(4 * 20)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.LASER_ENGRAVER, MachineType.CHEMICAL_BATH },
            new int[] { MV, HV, MV });
    }

    // Fiberglass Board → Advanced — [Chem Bath MV] → [Laser HV] → [Assembler EV] → [Chem Bath HV]
    private static void fiberglassBoardEngrave() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Fiberglass.get(1),
                foil(Materials.EnergeticAlloy, 12),
                foil(Materials.Palladium, 4))
            .fluidInputs(fluid(PrPMaterials.IVPhotoresist, 1000))
            .itemOutputs(ItemList.Circuit_Board_Fiberglass_Advanced.get(1))
            .duration(4 * 20)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.LASER_ENGRAVER, MachineType.ASSEMBLER,
                MachineType.CHEMICAL_BATH },
            new int[] { MV, HV, EV, HV });
    }

    // Multifiberglass Board — [Chem Bath HV] → [Laser LuV] → [Chem Bath HV]
    private static void multifiberglassBoard() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Circuit_Board_Fiberglass.get(2), foil(Materials.Iridium, 16))
            .fluidInputs(fluid(Materials.SulfuricAcid, 1000), fluid(PrPMaterials.LuVPhotoresist, 500))
            .itemOutputs(ItemList.Circuit_Board_Multifiberglass.get(1))
            .duration(4 * 20)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.LASER_ENGRAVER, MachineType.CHEMICAL_BATH },
            new int[] { HV, IV, HV });
    }

    // Multifiberglass Board → Elite — [Chem Bath HV] → [Laser LuV] → [Electrolyzer LuV] → [Chem Bath HV]
    private static void multifiberglassBoardEngrave() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Circuit_Board_Multifiberglass.get(1), foil(Materials.Platinum, 8))
            .fluidInputs(fluid(PrPMaterials.LuVPhotoresist, 2000))
            .itemOutputs(ItemList.Circuit_Board_Multifiberglass_Elite.get(1))
            .duration(5 * 20)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.LASER_ENGRAVER, MachineType.ELECTROLYZER,
                MachineType.CHEMICAL_BATH },
            new int[] { HV, LuV, LuV, HV });
    }

    // Wetware Board — [Chem Bath HV] → [Mixer UV] → [Laser UV] → [Chem Bath HV]
    private static void wetwareBoard() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Multifiberglass.get(2),
                foil(Materials.NiobiumTitanium, 16),
                foil(Materials.NaquadahEnriched, 8),
                ItemList.Circuit_Parts_PetriDish.get(1))
            .fluidInputs(fluid(Materials.GrowthMediumSterilized, 500), fluid(PrPMaterials.ZPMPhotoresist, 1000))
            .itemOutputs(ItemList.Circuit_Board_Wetware.get(1))
            .duration(5 * 20)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.MIXER, MachineType.LASER_ENGRAVER,
                MachineType.CHEMICAL_BATH },
            new int[] { HV, UV, UV, HV },
            SPCModuleType.BIO);
    }

    // Wetware Board → Extreme — [Chem Bath HV] → [Electrolyzer UV] → [Laser UV] → [Chem Bath UV]
    private static void wetwareBoardEngrave() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Wetware.get(1),
                foil(Materials.NiobiumTitanium, 64),
                foil(Materials.Naquadah, 16))
            .fluidInputs(fluid(PrPMaterials.UVPhotoresist, 5000))
            .itemOutputs(ItemList.Circuit_Board_Wetware_Extreme.get(1))
            .duration(5 * 20)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.ELECTROLYZER, MachineType.LASER_ENGRAVER,
                MachineType.CHEMICAL_BATH },
            new int[] { HV, UV, UV, UV },
            SPCModuleType.BIO);
    }

    // Bio Board — [Chem Bath UV] → [Mixer UHV] → [Laser UHV] → [Chem Bath UV]
    private static void bioBoard() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Wetware.get(2),
                foil(Materials.Neutronium, 16),
                foil(Materials.SuperconductorUVBase, 8),
                ItemList.Circuit_Parts_PetriDish.get(4))
            .fluidInputs(fluid(Materials.BioMediumSterilized, 1000), fluid(PrPMaterials.UVPhotoresist, 500))
            .itemOutputs(ItemList.Circuit_Board_Bio.get(1))
            .duration(15 * 20)
            .eut(TierEU.RECIPE_UV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.MIXER, MachineType.LASER_ENGRAVER,
                MachineType.CHEMICAL_BATH },
            new int[] { UV, UHV, UHV, UV },
            SPCModuleType.BIO);
    }

    // Bio Board → Ultra — [Chem Bath UV] → [Mixer UHV] → [Laser UHV] → [Chem Bath UHV]
    private static void bioBoardEngrave() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Bio.get(2),
                foil(Materials.Neutronium, 24),
                foil(Materials.Osmium, 8),
                ItemList.Circuit_Parts_PetriDish.get(1))
            .fluidInputs(fluid(PrPMaterials.UHVPhotoresist, 1000))
            .itemOutputs(ItemList.Circuit_Board_Bio_Ultra.get(1))
            .duration(10 * 20)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.MIXER, MachineType.LASER_ENGRAVER,
                MachineType.CHEMICAL_BATH },
            new int[] { UV, UHV, UHV, UHV },
            SPCModuleType.BIO);
    }

    // Optical Board (raw) — [Chem Bath UHV] → [Mixer UEV] → [Laser UEV] → [Chem Bath UHV]
    private static void opticalBoard() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(plate(Materials.Kevlar, 2), item("foilTairitsu", 16), foil(Materials.Osmium, 8))
            .fluidInputs(fluid(Materials.Grade7PurifiedWater, 500))
            .itemOutputs(GTNHPPItems.opticalBoardRaw(1))
            .duration(5 * 20)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.CHEMICAL_BATH, MachineType.MIXER, MachineType.LASER_ENGRAVER,
                MachineType.CHEMICAL_BATH },
            new int[] { UHV, UEV, UEV, UHV },
            SPCModuleType.CRYO);
    }

    // Optical Board → Circuit_Board_Optical — [Laser UEV] → [Electrolyzer UEV] → [Chem Bath UHV] → [Laser UEV]
    private static void opticalBoardEngrave() {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(
                GTNHPPItems.opticalBoardRaw(1),
                item("foilChromaticGlass", 16),
                plate(Materials.MysteriousCrystal, 4))
            .fluidInputs(fluid(PrPMaterials.UEVPhotoresist, 1000))
            .itemOutputs(ItemList.Circuit_Board_Optical.get(2))
            .duration(10 * 20)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        SPCRecipeData.register(
            recipes,
            new MachineType[] { MachineType.LASER_ENGRAVER, MachineType.ELECTROLYZER, MachineType.CHEMICAL_BATH,
                MachineType.LASER_ENGRAVER },
            new int[] { UEV, UEV, UHV, UEV },
            SPCModuleType.CRYO);
    }

    // -------------------------------------------------------------------------
    // Premium board variants — feed the NEXT photoresist tier up for DOUBLE output, same station
    // sequence / module gate / other inputs. The costlier photoresist is the trade. This is the
    // "higher-tier photoresist = better yield" mechanic, and it gives every higher photoresist
    // (including UIV, via the optical board) an extra use.
    // -------------------------------------------------------------------------
    private static void premiumBoardRecipes() {
        final MachineType CB = MachineType.CHEMICAL_BATH, LE = MachineType.LASER_ENGRAVER, MX = MachineType.MIXER,
            AS = MachineType.ASSEMBLER, EL = MachineType.ELECTROLYZER;

        // Epoxy Advanced — IV photoresist (was EV)
        premium(new ItemStack[] { ItemList.Circuit_Board_Epoxy.get(1), foil(Materials.Electrum, 8) },
            new FluidStack[] { fluid(PrPMaterials.IVPhotoresist, 500) }, ItemList.Circuit_Board_Epoxy_Advanced.get(4),
            3 * 20, TierEU.RECIPE_HV, new MachineType[] { CB, MX, LE, CB }, new int[] { HV, EV, HV, HV }, null);

        // Fiberglass Advanced — LuV photoresist (was IV)
        premium(
            new ItemStack[] { ItemList.Circuit_Board_Fiberglass.get(1), foil(Materials.EnergeticAlloy, 12),
                foil(Materials.Palladium, 4) },
            new FluidStack[] { fluid(PrPMaterials.LuVPhotoresist, 1000) },
            ItemList.Circuit_Board_Fiberglass_Advanced.get(4), 3 * 20, TierEU.RECIPE_HV,
            new MachineType[] { CB, LE, AS, CB }, new int[] { MV, HV, EV, HV }, null);

        // Multifiberglass — ZPM photoresist (was LuV)
        premium(new ItemStack[] { ItemList.Circuit_Board_Fiberglass.get(2), foil(Materials.Iridium, 16) },
            new FluidStack[] { fluid(Materials.SulfuricAcid, 1000), fluid(PrPMaterials.ZPMPhotoresist, 500) },
            ItemList.Circuit_Board_Multifiberglass.get(4), 3 * 20, TierEU.RECIPE_EV, new MachineType[] { CB, LE, CB },
            new int[] { HV, IV, HV }, null);

        // Multifiberglass Elite — ZPM photoresist (was LuV)
        premium(new ItemStack[] { ItemList.Circuit_Board_Multifiberglass.get(1), foil(Materials.Platinum, 8) },
            new FluidStack[] { fluid(PrPMaterials.ZPMPhotoresist, 2000) },
            ItemList.Circuit_Board_Multifiberglass_Elite.get(4), 4 * 20, TierEU.RECIPE_EV,
            new MachineType[] { CB, LE, EL, CB }, new int[] { HV, LuV, LuV, HV }, null);

        // Wetware — UV photoresist (was ZPM)
        premium(
            new ItemStack[] { ItemList.Circuit_Board_Multifiberglass.get(2), foil(Materials.NiobiumTitanium, 16),
                foil(Materials.NaquadahEnriched, 8), ItemList.Circuit_Parts_PetriDish.get(1) },
            new FluidStack[] { fluid(Materials.GrowthMediumSterilized, 500), fluid(PrPMaterials.UVPhotoresist, 1000) },
            ItemList.Circuit_Board_Wetware.get(4), 4 * 20, TierEU.RECIPE_EV, new MachineType[] { CB, MX, LE, CB },
            new int[] { HV, UV, UV, HV }, SPCModuleType.BIO);

        // Wetware Extreme — UHV photoresist (was UV)
        premium(
            new ItemStack[] { ItemList.Circuit_Board_Wetware.get(1), foil(Materials.NiobiumTitanium, 64),
                foil(Materials.Naquadah, 16) },
            new FluidStack[] { fluid(PrPMaterials.UHVPhotoresist, 5000) },
            ItemList.Circuit_Board_Wetware_Extreme.get(4), 7 * 20, TierEU.RECIPE_LuV,
            new MachineType[] { CB, EL, LE, CB }, new int[] { HV, UV, UV, UV }, SPCModuleType.BIO);

        // Bio — UHV photoresist (was UV)
        premium(
            new ItemStack[] { ItemList.Circuit_Board_Wetware.get(2), foil(Materials.Neutronium, 16),
                foil(Materials.SuperconductorUVBase, 8), ItemList.Circuit_Parts_PetriDish.get(4) },
            new FluidStack[] { fluid(Materials.BioMediumSterilized, 1000), fluid(PrPMaterials.UHVPhotoresist, 500) },
            ItemList.Circuit_Board_Bio.get(4), 7 * 20, TierEU.RECIPE_UV, new MachineType[] { CB, MX, LE, CB },
            new int[] { UV, UHV, UHV, UV }, SPCModuleType.BIO);

        // Bio Ultra — UEV photoresist (was UHV)
        premium(
            new ItemStack[] { ItemList.Circuit_Board_Bio.get(2), foil(Materials.Neutronium, 24),
                foil(Materials.Osmium, 8), ItemList.Circuit_Parts_PetriDish.get(1) },
            new FluidStack[] { fluid(PrPMaterials.UEVPhotoresist, 1000) }, ItemList.Circuit_Board_Bio_Ultra.get(4),
            7 * 20, TierEU.RECIPE_EV, new MachineType[] { CB, MX, LE, CB }, new int[] { UV, UHV, UHV, UHV },
            SPCModuleType.BIO);

        // Optical — UIV photoresist (was UEV); base outputs 2, premium outputs 4. Gives UIV a use.
        premium(
            new ItemStack[] { GTNHPPItems.opticalBoardRaw(1), item("foilChromaticGlass", 16),
                plate(Materials.MysteriousCrystal, 4) },
            new FluidStack[] { fluid(PrPMaterials.UIVPhotoresist, 1000) }, ItemList.Circuit_Board_Optical.get(4),
            7 * 20, TierEU.RECIPE_ZPM, new MachineType[] { LE, EL, CB, LE }, new int[] { UEV, UEV, UHV, UEV },
            SPCModuleType.CRYO);
    }

    /** Registers one premium board recipe + its station-sequence data (module optional). */
    private static void premium(ItemStack[] items, FluidStack[] fluids, ItemStack output, int duration, long eut,
        MachineType[] seq, int[] tiers, SPCModuleType module) {
        Collection<GTRecipe> recipes = GTValues.RA.stdBuilder()
            .itemInputs(items)
            .fluidInputs(fluids)
            .itemOutputs(output)
            .duration(duration)
            .eut(eut)
            .addTo(GTNHPPRecipeMaps.sSPCRecipes);
        if (module != null) SPCRecipeData.register(recipes, seq, tiers, module);
        else SPCRecipeData.register(recipes, seq, tiers);
    }
}
