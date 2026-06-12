package com.gtnh.processingplus.recipes;

import java.util.Iterator;

import gregtech.api.enums.*;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import bartworks.system.material.WerkstoffLoader;
import com.gtnh.processingplus.GTNHProcessingPlus;
import com.gtnh.processingplus.materials.PrPMaterials;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.plate;

public final class RecipeSwaps {

    /** Voltage-tier index for LuV (GTValues.V[6]). The hBN lubricant gate only applies at this tier and up. */
    private static final int LUV_TIER = 6;

    private RecipeSwaps() {}

    public static void run() {
        removeBoardRecipes();
        swapIVHull();
        gateLubricantBehindHBN();
        gateUVMotorMagnet();
        gateUVMotorCable();
        gateZPMComponentsWithVibranium();
        gateZPMHullWithUnobtanium();
        gateZPMSuperconductorWithUnobtanium();
        gateUVComponentsWithAmorphousNaquadria();
        gateConveyorsWithNylon();
        gateRobotArmsWithNylon();
        gateFieldGensWithPrometheanNaquadria();
    }

    // -------------------------------------------------------------------------
    // Nylon-6,6 motion-parts gate — high-tier (ZPM+) conveyors and robot arms now need molten
    // Nylon-6,6 (reinforced belt / self-lubricating joints). Both are consumed by nearly every
    // ZPM+ machine, so this makes the PCV nylon line recurring infrastructure. In-place fluid
    // append on assline recipes (real list + NEI copies), capped at 4 fluids. Note: ZPM/UV
    // conveyors & arms are already exotic-taxed (Vibranium/Naquadria) — nylon is one more fluid.
    // -------------------------------------------------------------------------
    private static void gateConveyorsWithNylon() {
        ItemStack[] conveyors = { ItemList.Conveyor_Module_ZPM.get(1), ItemList.Conveyor_Module_UV.get(1),
            ItemList.Conveyor_Module_UHV.get(1), ItemList.Conveyor_Module_UEV.get(1),
            ItemList.Conveyor_Module_UIV.get(1) };
        taxAsslineWithFluid(conveyors, PrPMaterials.Nylon66.getMolten(576), "Nylon conveyor-belt gate");
    }

    private static void gateRobotArmsWithNylon() {
        ItemStack[] robotArms = { ItemList.Robot_Arm_ZPM.get(1), ItemList.Robot_Arm_UV.get(1),
            ItemList.Robot_Arm_UHV.get(1), ItemList.Robot_Arm_UEV.get(1), ItemList.Robot_Arm_UIV.get(1) };
        taxAsslineWithFluid(robotArms, PrPMaterials.Nylon66.getMolten(288), "Nylon robot-arm gate");
    }

    // -------------------------------------------------------------------------
    // Promethean Naquadria field-generator gate — the alloy's first real use. Every UHV+ field
    // generator now also needs molten Promethean Naquadria (glowing exotic energy alloy), so the
    // whole Promethium → CRV-alloy chain becomes required for top-tier field/energy components.
    // -------------------------------------------------------------------------
    private static void gateFieldGensWithPrometheanNaquadria() {
        ItemStack[] fieldGens = { ItemList.Field_Generator_UHV.get(1), ItemList.Field_Generator_UEV.get(1),
            ItemList.Field_Generator_UIV.get(1) };
        taxAsslineWithFluid(fieldGens, PrPMaterials.PrometheanNaquadria.getMolten(288),
            "Promethean Naquadria field-gen gate");
    }

    /** Append a molten fluid in-place to every assembly-line recipe (real + NEI copy) whose output matches. */
    private static void taxAsslineWithFluid(ItemStack[] targets, FluidStack add, String label) {
        if (add == null) {
            GTNHProcessingPlus.LOG.warn("{}: fluid unavailable — skipped.", label);
            return;
        }
        int taxed = 0;
        for (GTRecipe.RecipeAssemblyLine r : GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes) {
            if (!matchesAny(r.mOutput, targets)) continue;
            if (r.mFluidInputs != null && r.mFluidInputs.length >= 4) continue; // assembly-line fluid cap
            r.mFluidInputs = appendFluid(r.mFluidInputs, add.copy());
            taxed++;
        }
        // Keep the NEI visual copies aligned with the real recipes.
        for (GTRecipe r : RecipeMaps.assemblylineVisualRecipes.getAllRecipes()) {
            if (r.mOutputs == null || r.mOutputs.length == 0 || !matchesAny(r.mOutputs[0], targets)) continue;
            if (r.mFluidInputs != null && r.mFluidInputs.length >= 4) continue;
            r.mFluidInputs = appendFluid(r.mFluidInputs, add.copy());
        }
        GTNHProcessingPlus.LOG.info("{}: taxed {} recipe(s).", label, taxed);
    }

    // -------------------------------------------------------------------------
    // hBN Lubricant gate — every assembly-line recipe that used plain Lubricant now
    // requires Hexagonal Boron Nitride Lubricant (the LuV hBN chain). In-place fluid
    // swap so it catches GT + every addon's assline recipes regardless of who added them.
    // The real recipe list (sAssemblylineRecipes) is what the machine checks; the visual
    // map is the NEI copy — both are swapped so NEI stays in sync.
    // -------------------------------------------------------------------------
    private static void gateLubricantBehindHBN() {
        FluidStack lubeProbe = Materials.Lubricant.getFluid(1);
        FluidStack hbnProbe = PrPMaterials.HBNLubricant.getFluidOrGas(1);
        if (lubeProbe == null || hbnProbe == null) {
            GTNHProcessingPlus.LOG.warn("hBN lubricant gate: Lubricant or hBN Lubricant fluid missing — skipped.");
            return;
        }
        Fluid lube = lubeProbe.getFluid();
        Fluid hbn = hbnProbe.getFluid();

        int swapped = 0;
        for (GTRecipe.RecipeAssemblyLine r : GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes) {
            if (GTUtility.getTier(r.mEUt) < LUV_TIER) continue; // leave IV-and-below recipes on plain lubricant
            swapped += swapFluid(r.mFluidInputs, lube, hbn);
        }
        // Keep the NEI visual copies aligned with the real recipes.
        for (GTRecipe r : RecipeMaps.assemblylineVisualRecipes.getAllRecipes()) {
            if (GTUtility.getTier(r.mEUt) < LUV_TIER) continue;
            swapFluid(r.mFluidInputs, lube, hbn);
        }
        GTNHProcessingPlus.LOG
            .info("hBN gate: swapped {} LuV+ assembly-line Lubricant input(s) to hBN Lubricant.", swapped);
    }

    /** Replaces every {@code from}-fluid stack in the array with an equal-amount {@code to} stack. */
    private static int swapFluid(FluidStack[] fluids, Fluid from, Fluid to) {
        if (fluids == null) return 0;
        int n = 0;
        for (int i = 0; i < fluids.length; i++) {
            if (fluids[i] != null && fluids[i].getFluid() == from) {
                fluids[i] = new FluidStack(to, fluids[i].amount);
                n++;
            }
        }
        return n;
    }

    // -------------------------------------------------------------------------
    // UV motor gate — the stock UV motor's magnet rod (Samarium Magnetic) is replaced
    // in place with an Amorphous Tritanium Alloy rod (the CRV output). In-place input
    // swap preserves the recipe's research/scanner identity, so no data-stick breakage.
    // -------------------------------------------------------------------------
    private static void gateUVMotorMagnet() {
        ItemStack samariumRod = GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.SamariumMagnetic, 1);
        ItemStack amorphousRod = PPRecipeHelper.rodLong(PrPMaterials.AmorphousTritaniumAlloy, 1);
        if (samariumRod == null || amorphousRod == null) {
            GTNHProcessingPlus.LOG.warn("UV-motor amorphous gate: rod item missing — skipped.");
            return;
        }
        int gated = swapAssemblyLineInput(new ItemStack[] { ItemList.Electric_Motor_UV.get(1) }, samariumRod,
            amorphousRod);
        GTNHProcessingPlus.LOG.info("UV motor: gated {} magnet rod(s) behind Amorphous Tritanium Alloy.", gated);
    }

    // -------------------------------------------------------------------------
    // UV motor cable gate — replaces the NaquadahAlloy cable in the UV motor with 4x Unobtanium
    // cable (count changes 2 -> 4), threading the Unobtanium chain through UV motor production too.
    // -------------------------------------------------------------------------
    private static void gateUVMotorCable() {
        Materials unobtanium = PrPMaterials.Unobtanium.getBridgeMaterial();
        if (unobtanium == null) {
            GTNHProcessingPlus.LOG.warn("UV-motor cable gate: no Unobtanium bridge material — skipped.");
            return;
        }
        ItemStack naquadahAlloyCable = GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.NaquadahAlloy, 1);
        ItemStack unobtaniumCable = GTOreDictUnificator.get(OrePrefixes.cableGt04, unobtanium, 4);
        if (naquadahAlloyCable == null || unobtaniumCable == null) {
            GTNHProcessingPlus.LOG.warn("UV-motor cable gate: cable item missing — skipped.");
            return;
        }
        int swapped = swapAssemblyLineInput(new ItemStack[] { ItemList.Electric_Motor_UV.get(1) }, naquadahAlloyCable,
            unobtaniumCable, false);
        GTNHProcessingPlus.LOG.info("UV motor: swapped {} NaquadahAlloy cable(s) for 4x Unobtanium cable.", swapped);
    }

    // -------------------------------------------------------------------------
    // AmorphousNaquadria UV-structural gate — the CRV's second amorphous output. Swaps the
    // Neutronium structural plate in the UV components for an Amorphous Naquadria plate (count kept),
    // so both CRV outputs gate the UV tier (Tritanium -> motor magnet, Naquadria -> structural plate).
    // Neutronium keeps every other role (rings, rounds, gears, frames, and all non-UV uses).
    // -------------------------------------------------------------------------
    private static void gateUVComponentsWithAmorphousNaquadria() {
        ItemStack neutroniumPlate = GTOreDictUnificator.get(OrePrefixes.plate, Materials.Neutronium, 1);
        ItemStack naquadriaPlate = plate(PrPMaterials.AmorphousNaquadria, 1);
        if (neutroniumPlate == null || naquadriaPlate == null) {
            GTNHProcessingPlus.LOG.warn("UV-component amorphous gate: plate item missing — skipped.");
            return;
        }
        ItemStack[] uvComponents = { ItemList.Electric_Motor_UV.get(1), ItemList.Electric_Pump_UV.get(1),
            ItemList.Conveyor_Module_UV.get(1), ItemList.Electric_Piston_UV.get(1), ItemList.Robot_Arm_UV.get(1),
            ItemList.Emitter_UV.get(1), ItemList.Sensor_UV.get(1), ItemList.Field_Generator_UV.get(1) };
        int swapped = swapAssemblyLineInput(uvComponents, neutroniumPlate, naquadriaPlate);
        GTNHProcessingPlus.LOG
            .info("UV components: swapped {} Neutronium plate(s) for Amorphous Naquadria.", swapped);
    }

    /**
     * Swaps every {@code from} item to {@code to} (count preserved) in the assembly-line recipes whose
     * output matches one of {@code outputs}, across both the real recipe list and the NEI visual copies.
     * Locks the matched slot's ore-dict alternatives to the replacement so the old item can't satisfy it.
     */
    private static int swapAssemblyLineInput(ItemStack[] outputs, ItemStack from, ItemStack to) {
        return swapAssemblyLineInput(outputs, from, to, true);
    }

    /**
     * @param keepCount when true the replacement keeps the original input's stack size; when false it
     *                  uses {@code to}'s stack size (i.e. the swap also changes the required amount).
     */
    private static int swapAssemblyLineInput(ItemStack[] outputs, ItemStack from, ItemStack to, boolean keepCount) {
        int swapped = 0;
        for (GTRecipe.RecipeAssemblyLine r : GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes) {
            if (!matchesAny(r.mOutput, outputs)) continue;
            for (int i = 0; i < r.mInputs.length; i++) {
                if (r.mInputs[i] == null || !GTUtility.areStacksEqual(r.mInputs[i], from)) continue;
                ItemStack rep = to.copy();
                if (keepCount) rep.stackSize = r.mInputs[i].stackSize;
                r.mInputs[i] = rep;
                if (r.mOreDictAlt != null && i < r.mOreDictAlt.length) {
                    r.mOreDictAlt[i] = new ItemStack[] { rep.copy() };
                }
                swapped++;
            }
        }
        // Keep the NEI visual copies aligned with the real recipes.
        for (GTRecipe r : RecipeMaps.assemblylineVisualRecipes.getAllRecipes()) {
            if (r.mOutputs == null || r.mOutputs.length == 0 || !matchesAny(r.mOutputs[0], outputs)) continue;
            for (int i = 0; i < r.mInputs.length; i++) {
                if (r.mInputs[i] == null || !GTUtility.areStacksEqual(r.mInputs[i], from)) continue;
                ItemStack rep = to.copy();
                if (keepCount) rep.stackSize = r.mInputs[i].stackSize;
                r.mInputs[i] = rep;
            }
        }
        return swapped;
    }

    // -------------------------------------------------------------------------
    // Vibranium ZPM-component tax — every ZPM machine component now also costs molten
    // Vibranium (the LuV exotic chain). In-place fluid append on the assembly-line recipes
    // whose output is one of the 8 ZPM components, so Vibranium becomes the LuV foundation
    // the whole ZPM tier rests on. Both the real list and the NEI visual copies are updated.
    // -------------------------------------------------------------------------
    private static void gateZPMComponentsWithVibranium() {
        FluidStack vibranium = PrPMaterials.Vibranium.getMolten(1296);
        ItemStack naquadahAlloyPlate = GTOreDictUnificator.get(OrePrefixes.plate, Materials.NaquadahAlloy, 1);
        if (vibranium == null) {
            GTNHProcessingPlus.LOG.warn("Vibranium ZPM gate: no molten Vibranium — skipped.");
            return;
        }
        ItemStack[] zpmComponents = { ItemList.Electric_Motor_ZPM.get(1), ItemList.Electric_Pump_ZPM.get(1),
            ItemList.Conveyor_Module_ZPM.get(1), ItemList.Electric_Piston_ZPM.get(1), ItemList.Robot_Arm_ZPM.get(1),
            ItemList.Emitter_ZPM.get(1), ItemList.Sensor_ZPM.get(1), ItemList.Field_Generator_ZPM.get(1) };

        int taxed = 0;
        for (GTRecipe.RecipeAssemblyLine r : GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes) {
            if (!matchesAny(r.mOutput, zpmComponents)) continue;
            if (r.mFluidInputs != null && r.mFluidInputs.length >= 4) continue; // assembly-line fluid cap
            r.mFluidInputs = appendFluid(r.mFluidInputs, vibranium.copy());
            taxed++;
        }
        // Keep the NEI visual copies aligned with the real recipes.
        for (GTRecipe r : RecipeMaps.assemblylineVisualRecipes.getAllRecipes()) {
            if (r.mOutputs == null || r.mOutputs.length == 0 || !matchesAny(r.mOutputs[0], zpmComponents)) continue;
            if (r.mFluidInputs != null && r.mFluidInputs.length >= 4) continue;
            r.mFluidInputs = appendFluid(r.mFluidInputs, vibranium.copy());
        }
        GTNHProcessingPlus.LOG.info("Vibranium gate: taxed {} ZPM component recipe(s) with 1296mB molten Vibranium.",
            taxed);

        int swapped = swapAssemblyLineInput(zpmComponents, naquadahAlloyPlate, plate(PrPMaterials.Vibranium,1));

        GTNHProcessingPlus.LOG.info("Vibranium gate: swapped {} ZPM component recipe(s) with 1 Vibranium Plate",
            swapped);
    }

    private static boolean matchesAny(ItemStack stack, ItemStack[] set) {
        if (stack == null) return false;
        for (ItemStack t : set) {
            if (t != null && GTUtility.areStacksEqual(stack, t)) return true;
        }
        return false;
    }

    private static FluidStack[] appendFluid(FluidStack[] arr, FluidStack add) {
        if (arr == null || arr.length == 0) return new FluidStack[] { add };
        FluidStack[] out = new FluidStack[arr.length + 1];
        System.arraycopy(arr, 0, out, 0, arr.length);
        out[arr.length] = add;
        return out;
    }

    // -------------------------------------------------------------------------
    // Unobtanium ZPM-hull gate — every ZPM machine hull now needs an Unobtanium superconductor
    // cable. The assembler recipe keeps its Naquadah cable and gains Unobtanium alongside; the
    // hand-craftable recipe (a bypass that never touches the assembler) has its Naquadah cables
    // replaced by Unobtanium so neither path skips the gate. No material composition touched.
    // This is the sink for the painful Unobtanium chain: every ZPM machine routes through it.
    // -------------------------------------------------------------------------
    private static void gateZPMHullWithUnobtanium() {
        Materials unobtanium = PrPMaterials.Unobtanium.getBridgeMaterial();
        if (unobtanium == null) {
            GTNHProcessingPlus.LOG.warn("ZPM hull Unobtanium gate: no bridge material — skipped.");
            return;
        }
        ItemStack unobtaniumCable = GTOreDictUnificator.get(OrePrefixes.cableGt04, unobtanium, 2);
        ItemStack hullZPM = ItemList.Hull_ZPM.get(1);
        if (unobtaniumCable == null) {
            GTNHProcessingPlus.LOG.warn("ZPM hull Unobtanium gate: no Unobtanium cable (cable loader ran?) — skipped.");
            return;
        }

        int removed = PPRecipeHelper.removeRecipesByOutput(RecipeMaps.assemblerRecipes, hullZPM);

        GTValues.RA.stdBuilder()
            .itemInputs(
                unobtaniumCable,
                ItemList.Casing_ZPM.get(1)) // new structural superconductor component
            .itemOutputs(hullZPM)
            .fluidInputs(Materials.Polybenzimidazole.getMolten(288))
            .duration(50)
            .eut(TierEU.RECIPE_LV / 2)
            .addTo(RecipeMaps.assemblerRecipes);

        // Close the crafting-table bypass: the stock ZPM hull is hand-craftable with Naquadah cables
        // and never touches the assembler. Remove it (direct CraftingManager removal bypasses the
        // recipe's NOT_REMOVABLE flag) and re-add with Unobtanium cables in place of the Naquadah.
        int craftRemoved = 0;
        Iterator<?> it = CraftingManager.getInstance()
            .getRecipeList()
            .iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (!(o instanceof IRecipe)) continue;
            ItemStack out = ((IRecipe) o).getRecipeOutput();
            if (out != null && GTUtility.areStacksEqual(out, hullZPM)) {
                it.remove();
                craftRemoved++;
            }
        }
        GTModHandler.addCraftingRecipe(
            hullZPM,
            GTModHandler.RecipeBits.BUFFERED | GTModHandler.RecipeBits.NOT_REMOVABLE,
            new Object[] { "PHP", "CMC", 'M', ItemList.Casing_ZPM, 'C',
                GTOreDictUnificator.get(OrePrefixes.cableGt02, unobtanium, 1L), 'H',
                OrePrefixes.plate.get(Materials.Iridium), 'P', OrePrefixes.plate.get(Materials.Polybenzimidazole) });

        GTNHProcessingPlus.LOG.info(
            "ZPM hull: removed {} assembler + {} crafting recipe(s), re-added behind Unobtanium cable.",
            removed,
            craftRemoved);
    }

    // -------------------------------------------------------------------------
    // Unobtanium ZPM-superconductor gate — the finishing (anneal) step that turns
    // SuperconductorZPMBase wire into finished SuperconductorZPM wire now also needs an Unobtanium
    // cable (like the Naquadah pipe). This is the anneal recipe, NOT the alloy composition, so the
    // base material is untouched. Output is nudged 18 -> 20 to compensate for the new cost. All
    // three stock coolant variants (Helium / Liquid Helium / SpaceTime) are preserved. Together with
    // the hull gate this creates a double Unobtanium demand across the ZPM tier.
    // -------------------------------------------------------------------------
    private static void gateZPMSuperconductorWithUnobtanium() {
        Materials unobtanium = PrPMaterials.Unobtanium.getBridgeMaterial();
        if (unobtanium == null) {
            GTNHProcessingPlus.LOG.warn("ZPM superconductor gate: no Unobtanium bridge material — skipped.");
            return;
        }
        ItemStack unobtaniumCable = GTOreDictUnificator.get(OrePrefixes.cableGt04, unobtanium, 2);
        ItemStack superconductor = GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorZPM, 18);
        if (unobtaniumCable == null || superconductor == null) {
            GTNHProcessingPlus.LOG.warn("ZPM superconductor gate: missing item — skipped.");
            return;
        }

        int removed = PPRecipeHelper.removeRecipesByOutput(RecipeMaps.assemblerRecipes, superconductor);

        // Re-add the three stock coolant variants, each now requiring Unobtanium cable + boosted output.
        addUnobtaniumSuperconductor(Materials.Helium.getGas(16_000), 1600, unobtaniumCable);
        addUnobtaniumSuperconductor(WerkstoffLoader.LiquidHelium.getFluidOrGas(16_000), 1280, unobtaniumCable);
        addUnobtaniumSuperconductor(Materials.SpaceTime.getMolten(32), 800, unobtaniumCable);

        addUnobtaniumSuperconductorToUHV(Materials.Helium.getGas(20_000), 80 * 20, unobtaniumCable);
        addUnobtaniumSuperconductorToUHV((WerkstoffLoader.LiquidHelium.getFluidOrGas(20_000)), 64 * 20, unobtaniumCable);
        addUnobtaniumSuperconductorToUHV(Materials.SpaceTime.getMolten(40), 40 * 20, unobtaniumCable);

        GTNHProcessingPlus.LOG
            .info("ZPM superconductor: removed {} stock recipe(s), re-added gated behind Unobtanium cable.", removed);
    }

    private static void addUnobtaniumSuperconductor(FluidStack coolant, int duration, ItemStack unobtaniumCable) {
        if (coolant == null) return; // a coolant material may be absent depending on installed mods
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorZPMBase, 18),
                GTOreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Naquadah, 12),
                unobtaniumCable.copy(),
                ItemList.Electric_Pump_ZPM.get(1)
                )
            .circuit(9)
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorZPM, 20))
            .fluidInputs(coolant)
            .duration(duration)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    private static void addUnobtaniumSuperconductorToUHV(FluidStack coolant, int duration, ItemStack unobtaniumCable) {
        if (coolant == null) return; // a coolant material may be absent depending on installed mods
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUVBase, 21),
                GTOreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Neutronium, 14),
                unobtaniumCable.copy(),
                ItemList.Electric_Pump_UV.get(1)
            )
            .circuit(9)
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorZPM, 23))
            .fluidInputs(coolant)
            .duration(duration)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    private static void removeBoardRecipes() {
        ItemStack[] targets = {
            ItemList.Circuit_Board_Epoxy.get(1),
            ItemList.Circuit_Board_Epoxy_Advanced.get(1),
            ItemList.Circuit_Board_Fiberglass.get(1),
            ItemList.Circuit_Board_Fiberglass_Advanced.get(1),
            ItemList.Circuit_Board_Multifiberglass.get(1),
            ItemList.Circuit_Board_Multifiberglass_Elite.get(1),
            ItemList.Circuit_Board_Wetware.get(1),
            ItemList.Circuit_Board_Wetware_Extreme.get(1),
            ItemList.Circuit_Board_Bio.get(1),
            ItemList.Circuit_Board_Bio_Ultra.get(1),
            ItemList.Circuit_Board_Optical.get(1),
        };

        int removed = 0;
        for (ItemStack target : targets) {
            removed += PPRecipeHelper.removeRecipesByOutput(RecipeMaps.chemicalReactorRecipes, target);
            removed += PPRecipeHelper.removeRecipesByOutput(RecipeMaps.multiblockChemicalReactorRecipes, target);
            removed += PPRecipeHelper.removeRecipesByOutput(RecipeMaps.circuitAssemblerRecipes, target);
        }
        GTNHProcessingPlus.LOG.info("Removed {} vanilla circuit board recipes.", removed);
    }

    private static void swapIVHull() {
        Materials rhea = PrPMaterials.RefractoryHighEntropyAlloy.getBridgeMaterial();
        if (rhea == null) {
            GTNHProcessingPlus.LOG.warn("IV-hull RHEA swap: RHEA material not found.");
            return;
        }

        ItemStack rheaCable = GTOreDictUnificator.get(OrePrefixes.cableGt01, rhea, 1L);
        if (rheaCable == null) {
            GTNHProcessingPlus.LOG.warn("IV-hull RHEA swap: no RHEA cable item (did the cable loader run?).");
            return;
        }

        ItemStack hullIV = ItemList.Hull_IV.get(1);

        // Remove existing crafting table recipe
        int removed = 0;
        Iterator<?> it = CraftingManager.getInstance().getRecipeList().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (!(o instanceof IRecipe)) continue;
            ItemStack out = ((IRecipe) o).getRecipeOutput();
            if (out != null && GTUtility.areStacksEqual(out, hullIV)) {
                it.remove();
                removed++;
            }
        }

        // Remove existing assembler recipe
        removed += PPRecipeHelper.removeRecipesByOutput(RecipeMaps.assemblerRecipes, hullIV);

        // Re-add assembler recipe with RHEA cable
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Casing_IV.get(1), GTOreDictUnificator.get(OrePrefixes.cableGt01, rhea, 2L))
            .fluidInputs(Materials.Polytetrafluoroethylene.getMolten(288))
            .itemOutputs(hullIV)
            .duration(5 * 20)
            .eut(TierEU.RECIPE_LV)
            .addTo(RecipeMaps.assemblerRecipes);

        // Re-add crafting table recipe with RHEA cable
        GTModHandler.addCraftingRecipe(
            hullIV,
            GTModHandler.RecipeBits.BUFFERED | GTModHandler.RecipeBits.NOT_REMOVABLE,
            new Object[] {
                "PHP",
                "CMC",
                'M', ItemList.Casing_IV.get(1),
                'C', rheaCable,
                'H', GTOreDictUnificator.get(OrePrefixes.plate, Materials.TungstenSteel, 1L),
                'P', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polytetrafluoroethylene, 1L)
            });

        GTNHProcessingPlus.LOG.info("IV hull: removed {} stock recipe(s), re-added with RHEA cable.", removed);
    }
}
