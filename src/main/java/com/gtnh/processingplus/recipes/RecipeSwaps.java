package com.gtnh.processingplus.recipes;

import static com.gtnh.processingplus.recipes.PPRecipeHelper.appendFluid;
import static com.gtnh.processingplus.recipes.PPRecipeHelper.appendItems;
import static com.gtnh.processingplus.recipes.PPRecipeHelper.countItems;
import static com.gtnh.processingplus.recipes.PPRecipeHelper.densePlate;
import static com.gtnh.processingplus.recipes.PPRecipeHelper.matchesAny;
import static com.gtnh.processingplus.recipes.PPRecipeHelper.plate;
import static com.gtnh.processingplus.recipes.PPRecipeHelper.stripItems;
import static gregtech.api.util.GTRecipeConstants.COAL_CASING_TIER;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.gtnh.processingplus.GTNHProcessingPlus;
import com.gtnh.processingplus.materials.PrPMaterials;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.api.recipe.GoodGeneratorRecipeMaps;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;

public final class RecipeSwaps {

    /** Voltage-tier index for LuV (GTValues.V[6]). The hBN lubricant gate only applies at this tier and up. */
    private static final int LUV_TIER = 6;

    private RecipeSwaps() {}

    public static void run() {
        removeBoardRecipes();
        swapIVHull();
        gateLubricantBehindHBN();
        gateUVMotorMagnet();
        swapNaquadahCablesToUnobtanium();
        gateZPMComponentsWithVibranium();
        gateZPMHullWithUnobtanium();
        gateZPMSuperconductorWithUnobtanium();
        gateUVComponentsWithAmorphousNaquadria();
        gateConveyorsWithNylon();
        gateRobotArmsWithNylon();
        gateFieldGensWithPrometheanNaquadria();
        gateCOALLubricantBehindHBN();
        gateCOALNylonFluid();
        gateCOALZPMVibraniumFluid();
        gateCOALUVMotor();
        gateUVCasingWithCarbonFiber();
        gateVoidMinerWithUnobtanium();
        normalizeCoALCircuits();
    }

    // -------------------------------------------------------------------------
    // Carbon Fiber UV-casing gate — the endgame ZPM carbon-fiber chain's structural sink. The UV
    // machine casing (8 Osmium plates) now takes 4 Osmium + 4 Carbon Fiber Composite plates, so the
    // whole PAN → graphitization → composite line becomes the structural cost of building UV machines.
    // Both the assembler recipe and the hand-craft bypass (PPP/PwP/PPP) are gated so neither path skips
    // the carbon fiber.
    // -------------------------------------------------------------------------
    private static void gateUVCasingWithCarbonFiber() {
        ItemStack carbonPlate1 = plate(PrPMaterials.CarbonFiberComposite, 1);
        ItemStack osmium4 = GTOreDictUnificator.get(OrePrefixes.plate, Materials.Osmium, 4);
        ItemStack carbon4 = plate(PrPMaterials.CarbonFiberComposite, 4);
        ItemStack casingUV = ItemList.Casing_UV.get(1);
        if (carbonPlate1 == null || osmium4 == null || carbon4 == null) {
            GTNHProcessingPlus.LOG.warn("UV casing carbon-fiber gate: Carbon Fiber Composite plate missing — skipped.");
            return;
        }

        int removed = PPRecipeHelper.removeRecipesByOutput(RecipeMaps.assemblerRecipes, casingUV);
        GTValues.RA.stdBuilder()
            .itemInputs(osmium4, carbon4)
            .circuit(8)
            .itemOutputs(casingUV)
            .duration(50)
            .eut(TierEU.RECIPE_LV / 2)
            .addTo(RecipeMaps.assemblerRecipes);

        // Close the hand-craft bypass: remove the stock 8-Osmium crafting recipe and re-add as 4+4.
        int craftRemoved = 0;
        Iterator<?> it = CraftingManager.getInstance()
            .getRecipeList()
            .iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (!(o instanceof IRecipe)) continue;
            ItemStack out = ((IRecipe) o).getRecipeOutput();
            if (out != null && GTUtility.areStacksEqual(out, casingUV)) {
                it.remove();
                craftRemoved++;
            }
        }
        GTModHandler.addCraftingRecipe(
            casingUV,
            GTModHandler.RecipeBits.BUFFERED | GTModHandler.RecipeBits.NOT_REMOVABLE,
            new Object[] { "PCP", "CwC", "PCP", 'P', OrePrefixes.plate.get(Materials.Osmium), 'C', carbonPlate1 });

        GTNHProcessingPlus.LOG.info(
            "UV casing carbon-fiber gate: removed {} assembler + {} crafting recipe(s), re-added with 4 Carbon Fiber Composite plates.",
            removed,
            craftRemoved);
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
        taxAsslineWithFluid(
            fieldGens,
            PrPMaterials.PrometheanNaquadria.getMolten(288),
            "Promethean Naquadria field-gen gate");
    }

    private static void gateCOALNylonFluid() {
        // 576 mB/item × 64 items × 0.75 = 27648; 288 mB/item × 64 × 0.75 = 13824
        FluidStack conveyorNylon = PrPMaterials.Nylon66.getMolten(27648);
        FluidStack armNylon = PrPMaterials.Nylon66.getMolten(13824);
        if (conveyorNylon == null) {
            GTNHProcessingPlus.LOG.warn("CoAL Nylon gate: Nylon66 molten unavailable — skipped.");
            return;
        }
        ItemStack[] conveyors = { ItemList.Conveyor_Module_ZPM.get(1), ItemList.Conveyor_Module_UV.get(1),
            ItemList.Conveyor_Module_UHV.get(1), ItemList.Conveyor_Module_UEV.get(1),
            ItemList.Conveyor_Module_UIV.get(1) };
        ItemStack[] robotArms = { ItemList.Robot_Arm_ZPM.get(1), ItemList.Robot_Arm_UV.get(1),
            ItemList.Robot_Arm_UHV.get(1), ItemList.Robot_Arm_UEV.get(1), ItemList.Robot_Arm_UIV.get(1) };
        int n = 0;
        n += modifyCoALRecipes(conveyors, c -> c.mFluidInputs = appendFluid(c.mFluidInputs, conveyorNylon.copy()));
        n += modifyCoALRecipes(robotArms, c -> c.mFluidInputs = appendFluid(c.mFluidInputs, armNylon.copy()));
        GTNHProcessingPlus.LOG.info("CoAL Nylon gate: updated {} recipe(s) with molten Nylon66.", n);
    }

    private static void gateCOALZPMVibraniumFluid() {
        // 1296 mB/item × 64 items × 0.75 = 62208
        FluidStack vibranium = PrPMaterials.Vibranium.getMolten(62208);
        if (vibranium == null) {
            GTNHProcessingPlus.LOG.warn("CoAL Vibranium gate: Vibranium molten unavailable — skipped.");
            return;
        }
        ItemStack[] zpmComponents = { ItemList.Electric_Motor_ZPM.get(1), ItemList.Electric_Pump_ZPM.get(1),
            ItemList.Conveyor_Module_ZPM.get(1), ItemList.Electric_Piston_ZPM.get(1), ItemList.Robot_Arm_ZPM.get(1),
            ItemList.Emitter_ZPM.get(1), ItemList.Sensor_ZPM.get(1), ItemList.Field_Generator_ZPM.get(1) };
        int n = modifyCoALRecipes(zpmComponents, c -> c.mFluidInputs = appendFluid(c.mFluidInputs, vibranium.copy()));
        GTNHProcessingPlus.LOG.info("CoAL Vibranium gate: updated {} ZPM recipe(s) with 62208mB molten Vibranium.", n);
    }

    private static void gateCOALUVMotor() {
        FluidStack samariumProbe = Materials.Samarium.getMolten(1);
        FluidStack amorphousProbe = PrPMaterials.AmorphousTritaniumAlloy.getMolten(1);
        if (samariumProbe == null || amorphousProbe == null) {
            GTNHProcessingPlus.LOG
                .warn("CoAL UV motor gate: Samarium or Amorphous Tritanium molten missing — skipped.");
            return;
        }
        Fluid samarium = samariumProbe.getFluid();
        Fluid amorphous = amorphousProbe.getFluid();
        int n = modifyCoALRecipes(
            new ItemStack[] { ItemList.Electric_Motor_UV.get(1) },
            c -> swapFluid(c.mFluidInputs, samarium, amorphous));
        GTNHProcessingPlus.LOG.info("CoAL UV motor gate: swapped Samarium → Amorphous Tritanium in {} recipe(s).", n);
    }

    private static void normalizeCoALCircuits() {
        int n = modifyCoALRecipes(r -> r.mInputs != null && hasCircuit(r.mInputs), copy -> {
            ItemStack circuit = null;
            List<ItemStack> rest = new ArrayList<>();
            for (ItemStack s : copy.mInputs) {
                if (circuit == null && isCircuit(s)) circuit = s;
                else rest.add(s);
            }
            if (circuit != null) rest.add(circuit);
            copy.mInputs = rest.toArray(new ItemStack[0]);
        });
        GTNHProcessingPlus.LOG.info("normalizeCoALCircuits: moved circuit to last slot in {} recipe(s).", n);
    }

    private static boolean isCircuit(ItemStack s) {
        if (s == null) return false;
        ItemStack c = GTUtility.getIntegratedCircuit(s.getItemDamage());
        return c != null && GTUtility.areStacksEqual(s, c);
    }

    private static boolean hasCircuit(ItemStack[] inputs) {
        for (ItemStack s : inputs) if (isCircuit(s)) return true;
        return false;
    }

    /**
     * Copy+remove+re-add pattern for CoAL recipes. For every recipe passing {@code filter},
     * copies it, runs {@code modifier} on the copy, removes the original by output, then re-adds.
     * Returns the number of recipes modified.
     */
    private static int modifyCoALRecipes(Predicate<GTRecipe> filter, Consumer<GTRecipe> modifier) {
        List<GTRecipe> toAdd = new ArrayList<>();
        List<ItemStack> toRemove = new ArrayList<>();
        for (GTRecipe r : GoodGeneratorRecipeMaps.componentAssemblyLineRecipes.getAllRecipes()) {
            if (r.mOutputs == null || r.mOutputs.length == 0) continue;
            if (!filter.test(r)) continue;
            GTRecipe copy = r.copy();
            modifier.accept(copy);
            toAdd.add(copy);
            toRemove.add(r.mOutputs[0]);
        }
        for (ItemStack out : toRemove)
            PPRecipeHelper.removeRecipesByOutput(GoodGeneratorRecipeMaps.componentAssemblyLineRecipes, out);
        for (GTRecipe c : toAdd) GoodGeneratorRecipeMaps.componentAssemblyLineRecipes.addRecipe(c);
        return toAdd.size();
    }

    /** Convenience overload: filter by output item matching any of {@code outputs}. */
    private static int modifyCoALRecipes(ItemStack[] outputs, Consumer<GTRecipe> modifier) {
        return modifyCoALRecipes(r -> matchesAny(r.mOutputs[0], outputs), modifier);
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

    private static void gateCOALLubricantBehindHBN() {
        final int COAL_LUV = 7;
        FluidStack lubeProbe = Materials.Lubricant.getFluid(1);
        FluidStack hbnProbe = PrPMaterials.HBNLubricant.getFluidOrGas(1);
        if (lubeProbe == null || hbnProbe == null) {
            GTNHProcessingPlus.LOG.warn("CoAL hBN lubricant gate: fluid missing — skipped.");
            return;
        }
        Fluid lube = lubeProbe.getFluid();
        Fluid hbn = hbnProbe.getFluid();
        int n = modifyCoALRecipes(r -> {
            if (r.getMetadataOrDefault(COAL_CASING_TIER, 0) < COAL_LUV) return false;
            if (r.mFluidInputs == null) return false;
            for (FluidStack f : r.mFluidInputs) if (f != null && f.getFluid() == lube) return true;
            return false;
        }, copy -> swapFluid(copy.mFluidInputs, lube, hbn));
        GTNHProcessingPlus.LOG.info("CoAL hBN gate: swapped {} LuV+ CoAL Lubricant(s) to hBN Lubricant.", n);
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
        int gated = swapAssemblyLineInput(
            new ItemStack[] { ItemList.Electric_Motor_UV.get(1) },
            samariumRod,
            amorphousRod);
        GTNHProcessingPlus.LOG.info("UV motor: gated {} magnet rod(s) behind Amorphous Tritanium Alloy.", gated);
    }

    // -------------------------------------------------------------------------
    // NaquadahAlloy cable → 4x cableGt02 Unobtanium swap — standardises every assembly-line recipe
    // that has any NaquadahAlloy cable to 4x 2x-cable Unobtanium. CoAL gets the batch-scaled amount:
    // 4 × 64 × 0.75 = 192.
    // -------------------------------------------------------------------------
    private static void swapNaquadahCablesToUnobtanium() {
        Materials unobtanium = PrPMaterials.Unobtanium.getBridgeMaterial();
        if (unobtanium == null) {
            GTNHProcessingPlus.LOG.warn("NaquadahAlloy cable swap: no Unobtanium bridge material — skipped.");
            return;
        }

        OrePrefixes[] naqPrefixes = { OrePrefixes.cableGt01, OrePrefixes.cableGt02, OrePrefixes.cableGt04,
            OrePrefixes.cableGt08, OrePrefixes.cableGt12, OrePrefixes.cableGt16 };

        // Replacement is always 4x cableGt02 Unobtanium in the assembly line.
        ItemStack asmRep = GTOreDictUnificator.get(OrePrefixes.cableGt02, unobtanium, 4);
        // CoAL batch-scaled: 4 × 64 × 0.75 = 192 cableGt02, compacted to 192/8 = 24 cableGt16.
        ItemStack coalRep = GTOreDictUnificator.get(OrePrefixes.cableGt16, unobtanium, 24);
        if (asmRep == null || coalRep == null) {
            GTNHProcessingPlus.LOG.warn("NaquadahAlloy cable swap: Unobtanium cable missing — skipped.");
            return;
        }

        // Assembly line — in-place swap (real list + NEI copies).
        int asmSwapped = 0;
        for (OrePrefixes prefix : naqPrefixes) {
            ItemStack naq1 = GTOreDictUnificator.get(prefix, Materials.NaquadahAlloy, 1);
            if (naq1 == null) continue;
            for (GTRecipe.RecipeAssemblyLine r : GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes) {
                for (int i = 0; i < r.mInputs.length; i++) {
                    if (r.mInputs[i] == null || !GTUtility.areStacksEqual(r.mInputs[i], naq1)) continue;
                    r.mInputs[i] = asmRep.copy();
                    if (r.mOreDictAlt != null && i < r.mOreDictAlt.length)
                        r.mOreDictAlt[i] = new ItemStack[] { asmRep.copy() };
                    asmSwapped++;
                }
            }
            for (GTRecipe r : RecipeMaps.assemblylineVisualRecipes.getAllRecipes()) {
                if (r.mOutputs == null || r.mOutputs.length == 0) continue;
                for (int i = 0; i < r.mInputs.length; i++) {
                    if (r.mInputs[i] == null || !GTUtility.areStacksEqual(r.mInputs[i], naq1)) continue;
                    r.mInputs[i] = asmRep.copy();
                }
            }
        }

        // CoAL — copy+remove+re-add; check all prefixes per recipe in one pass.
        int coalSwapped = modifyCoALRecipes(r -> {
            for (OrePrefixes prefix : naqPrefixes) {
                ItemStack naq1 = GTOreDictUnificator.get(prefix, Materials.NaquadahAlloy, 1);
                if (naq1 == null) continue;
                for (ItemStack s : r.mInputs) {
                    if (s != null && GTUtility.areStacksEqual(s, naq1)) return true;
                }
            }
            return false;
        }, copy -> {
            for (OrePrefixes prefix : naqPrefixes) {
                ItemStack naq1 = GTOreDictUnificator.get(prefix, Materials.NaquadahAlloy, 1);
                if (naq1 == null) continue;
                for (int i = 0; i < copy.mInputs.length; i++) {
                    if (copy.mInputs[i] != null && GTUtility.areStacksEqual(copy.mInputs[i], naq1))
                        copy.mInputs[i] = coalRep.copy();
                }
            }
        });

        GTNHProcessingPlus.LOG.info(
            "NaquadahAlloy cable swap: {} assembly-line slot(s) → 4x cableGt02 Unobtanium, {} CoAL recipe(s) → 24x cableGt16.",
            asmSwapped,
            coalSwapped);
    }

    // -------------------------------------------------------------------------
    // AmorphousNaquadria UV-structural gate — the CRV's second amorphous output. Swaps the
    // Neutronium structural plate in the UV components for an Amorphous Naquadria plate (count kept),
    // so both CRV outputs gate the UV tier (Tritanium -> motor magnet, Naquadria -> structural plate).
    // Neutronium keeps every other role (rings, rounds, gears, frames, and all non-UV uses).
    // -------------------------------------------------------------------------
    private static void gateUVComponentsWithAmorphousNaquadria() {
        ItemStack neutroniumPlate1 = GTOreDictUnificator.get(OrePrefixes.plate, Materials.Neutronium, 1);
        ItemStack neutroniumDensePlate1 = GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Neutronium, 1);
        ItemStack naquadriaPlate1 = plate(PrPMaterials.AmorphousNaquadria, 1);
        if (neutroniumPlate1 == null || naquadriaPlate1 == null
            || densePlate(PrPMaterials.AmorphousNaquadria, 1) == null) {
            GTNHProcessingPlus.LOG.warn("UV-component amorphous gate: plate item missing — skipped.");
            return;
        }
        ItemStack[] uvComponents = { ItemList.Electric_Motor_UV.get(1), ItemList.Electric_Pump_UV.get(1),
            ItemList.Conveyor_Module_UV.get(1), ItemList.Electric_Piston_UV.get(1), ItemList.Robot_Arm_UV.get(1),
            ItemList.Emitter_UV.get(1), ItemList.Sensor_UV.get(1), ItemList.Field_Generator_UV.get(1) };

        // Assembly line: swap Neutronium plate → Amorphous Naquadria plate (count preserved per component).
        int swapped = swapAssemblyLineInput(uvComponents, neutroniumPlate1, naquadriaPlate1);
        GTNHProcessingPlus.LOG.info("UV components: swapped {} Neutronium plate(s) for Amorphous Naquadria.", swapped);

        // CoAL: for each UV component, read its per-item Amorphous Naquadria plate count from the
        // assembly line recipe, apply (count × 64 × 0.75 ÷ 9) to get the dense plate gate, and
        // append those dense plates to that component's UV-tier CoAL recipe.
        int coalTaxed = 0;
        for (GTRecipe.RecipeAssemblyLine r : GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes) {
            if (!matchesAny(r.mOutput, uvComponents)) continue;
            int plateCount = countItems(r.mInputs, naquadriaPlate1);
            if (plateCount == 0) continue;
            int denseCount = Math.max(1, Math.round(plateCount * 64 * 0.75f / 9));
            coalTaxed += modifyCoALRecipes(new ItemStack[] { r.mOutput }, copy -> {
                copy.mInputs = stripItems(copy.mInputs, neutroniumPlate1, neutroniumDensePlate1);
                copy.mInputs = appendItems(copy.mInputs, densePlate(PrPMaterials.AmorphousNaquadria, denseCount));
            });
        }
        GTNHProcessingPlus.LOG
            .info("UV components CoAL: added Amorphous Naquadria dense plates to {} UV CoAL recipe(s).", coalTaxed);
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
        GTNHProcessingPlus.LOG
            .info("Vibranium gate: taxed {} ZPM component recipe(s) with 1296mB molten Vibranium.", taxed);

        int swapped = swapAssemblyLineInput(zpmComponents, naquadahAlloyPlate, plate(PrPMaterials.Vibranium, 1));
        GTNHProcessingPlus.LOG
            .info("Vibranium gate: swapped {} ZPM component recipe(s) with 1 Vibranium Plate", swapped);

        // Field generator, pump, piston, and conveyor also carry dense NaquadahAlloy plates —
        // replace those with 10 dense Vibranium plates (count changes, so keepCount = false).
        ItemStack naquadahAlloyDensePlate = GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.NaquadahAlloy, 1);
        ItemStack vibraniumDense10 = densePlate(PrPMaterials.Vibranium, 10);
        ItemStack[] densePlateComponents = { ItemList.Field_Generator_ZPM.get(1), ItemList.Electric_Pump_ZPM.get(1),
            ItemList.Electric_Piston_ZPM.get(1), ItemList.Conveyor_Module_ZPM.get(1) };
        if (naquadahAlloyDensePlate != null && vibraniumDense10 != null) {
            int denseSwapped = swapAssemblyLineInput(
                densePlateComponents,
                naquadahAlloyDensePlate,
                vibraniumDense10,
                false);
            GTNHProcessingPlus.LOG.info(
                "Vibranium gate: swapped {} dense NaquadahAlloy plate(s) for 10 dense Vibranium plates.",
                denseSwapped);
        }

        // CoAL: GoodGenerator generated these before run(), so they still have NaquadahAlloy plates.
        // Mirror what gateUVComponentsWithAmorphousNaquadria does for UV:
        // regular plates → strip NaquadahAlloy, append dense Vibranium (count × 64 × 0.75 / 9)
        // dense plates → strip dense NaquadahAlloy, append dense Vibranium (count × 64 × 0.75)
        // Counts are read from the now-updated assembly-line recipe so they match exactly.
        if (naquadahAlloyPlate != null && naquadahAlloyDensePlate != null) {
            ItemStack vibPlate1 = plate(PrPMaterials.Vibranium, 1);
            ItemStack denseVib1 = densePlate(PrPMaterials.Vibranium, 1);
            if (vibPlate1 != null) {
                int coalTotal = 0;
                for (GTRecipe.RecipeAssemblyLine asmR : GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes) {
                    if (!matchesAny(asmR.mOutput, zpmComponents)) continue;
                    boolean isDense = matchesAny(asmR.mOutput, densePlateComponents);
                    int regularCount = countItems(asmR.mInputs, vibPlate1);
                    int denseAsmCount = (isDense && denseVib1 != null) ? countItems(asmR.mInputs, denseVib1) : 0;
                    if (regularCount == 0 && denseAsmCount == 0) continue;
                    final int regularDenseOut = Math.max(1, Math.round(regularCount * 64 * 0.75f / 9));
                    final int denseDenseOut = Math.round(denseAsmCount * 64 * 0.75f);
                    coalTotal += modifyCoALRecipes(new ItemStack[] { asmR.mOutput }, copy -> {
                        copy.mInputs = stripItems(
                            copy.mInputs,
                            naquadahAlloyPlate,
                            isDense ? naquadahAlloyDensePlate : null);
                        if (regularCount > 0) {
                            ItemStack dv = densePlate(PrPMaterials.Vibranium, regularDenseOut);
                            if (dv != null) copy.mInputs = appendItems(copy.mInputs, dv);
                        }
                        if (denseDenseOut > 0) {
                            ItemStack dv = densePlate(PrPMaterials.Vibranium, denseDenseOut);
                            if (dv != null) copy.mInputs = appendItems(copy.mInputs, dv);
                        }
                    });
                }
                GTNHProcessingPlus.LOG
                    .info("Vibranium gate: updated {} ZPM CoAL recipe(s) with dense Vibranium plates.", coalTotal);
            }
        }
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
            .itemInputs(unobtaniumCable, ItemList.Casing_ZPM.get(1)) // new structural superconductor component
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
        addUnobtaniumSuperconductorToUHV(
            (WerkstoffLoader.LiquidHelium.getFluidOrGas(20_000)),
            64 * 20,
            unobtaniumCable);
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
                ItemList.Electric_Pump_ZPM.get(1))
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
                ItemList.Electric_Pump_UV.get(1))
            .circuit(9)
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorZPM, 23))
            .fluidInputs(coolant)
            .duration(duration)
            .eut(TierEU.RECIPE_UV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    private static void removeBoardRecipes() {
        ItemStack[] targets = { ItemList.Circuit_Board_Epoxy.get(1), ItemList.Circuit_Board_Epoxy_Advanced.get(1),
            ItemList.Circuit_Board_Fiberglass.get(1), ItemList.Circuit_Board_Fiberglass_Advanced.get(1),
            ItemList.Circuit_Board_Multifiberglass.get(1), ItemList.Circuit_Board_Multifiberglass_Elite.get(1),
            ItemList.Circuit_Board_Wetware.get(1), ItemList.Circuit_Board_Wetware_Extreme.get(1),
            ItemList.Circuit_Board_Bio.get(1), ItemList.Circuit_Board_Bio_Ultra.get(1),
            ItemList.Circuit_Board_Optical.get(1), };

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
        Iterator<?> it = CraftingManager.getInstance()
            .getRecipeList()
            .iterator();
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
            new Object[] { "PHP", "CMC", 'M', ItemList.Casing_IV.get(1), 'C', rheaCable, 'H',
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.TungstenSteel, 1L), 'P',
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polytetrafluoroethylene, 1L) });

        GTNHProcessingPlus.LOG.info("IV hull: removed {} stock recipe(s), re-added with RHEA cable.", removed);
    }

    // -------------------------------------------------------------------------
    // Void Miner Unobtanium gate — appends 4 Unobtanium gears to every assembly-line recipe
    // whose output display name contains "Void Miner". The sifting-End-Stone bootstrap gives
    // enough Unobtanium to build the miner; the miner then provides bulk Unobtanium.
    // -------------------------------------------------------------------------
    private static void gateVoidMinerWithUnobtanium() {
        Materials unobtanium = PrPMaterials.Unobtanium.getBridgeMaterial();
        if (unobtanium == null) {
            GTNHProcessingPlus.LOG.warn("Void miner Unobtanium gate: no bridge material — skipped.");
            return;
        }
        ItemStack unobtGear4 = GTOreDictUnificator.get(OrePrefixes.gearGt, unobtanium, 4);
        if (unobtGear4 == null) {
            GTNHProcessingPlus.LOG.warn("Void miner Unobtanium gate: Unobtanium gear missing — skipped.");
            return;
        }

        int modified = 0;
        for (GTRecipe.RecipeAssemblyLine r : GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes) {
            if (r.mOutput == null) continue;
            String name = r.mOutput.getDisplayName();
            if (name == null || !name.toLowerCase()
                .contains("void miner")) continue;
            r.mInputs = appendItems(r.mInputs, unobtGear4.copy());
            if (r.mOreDictAlt != null) {
                ItemStack[][] newAlt = new ItemStack[r.mOreDictAlt.length + 1][];
                System.arraycopy(r.mOreDictAlt, 0, newAlt, 0, r.mOreDictAlt.length);
                newAlt[r.mOreDictAlt.length] = new ItemStack[] { unobtGear4.copy() };
                r.mOreDictAlt = newAlt;
            }
            modified++;
        }
        for (GTRecipe r : RecipeMaps.assemblylineVisualRecipes.getAllRecipes()) {
            if (r.mOutputs == null || r.mOutputs.length == 0 || r.mOutputs[0] == null) continue;
            String name = r.mOutputs[0].getDisplayName();
            if (name == null || !name.toLowerCase()
                .contains("void miner")) continue;
            r.mInputs = appendItems(r.mInputs, unobtGear4.copy());
        }

        GTNHProcessingPlus.LOG
            .info("Void miner Unobtanium gate: appended 4 Unobtanium gears to {} assembly-line recipe(s).", modified);
    }
}
