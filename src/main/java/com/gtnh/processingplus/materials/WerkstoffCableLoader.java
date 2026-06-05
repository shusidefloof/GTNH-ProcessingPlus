package com.gtnh.processingplus.materials;

import bartworks.system.material.Werkstoff;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.metatileentity.implementations.MTECable;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

/**
 * Made by Shusidefloof:
 * Adds GregTech-style placeable wires and insulated cables for a bartworks {@link Werkstoff}, which
 * bartworks itself cannot generate. Mirrors GregTech's internal (private) {@code WireCableBuilder}:
 * it registers the 6 wire + 6 cable {@link MTECable} meta-tile-entities against the Werkstoff's
 * bridge {@link Materials}, then auto-generates wiremill + assembler recipes.
 *
 * <p>
 * Each call reserves 12 MTE IDs starting at {@code startId} (6 wires, then 6 cables). Set both
 * losses to 0 for a superconductor (lossless) cable.
 */
public final class WerkstoffCableLoader {

    private static final OrePrefixes[] WIRE = { OrePrefixes.wireGt01, OrePrefixes.wireGt02, OrePrefixes.wireGt04,
        OrePrefixes.wireGt08, OrePrefixes.wireGt12, OrePrefixes.wireGt16 };
    private static final OrePrefixes[] CABLE = { OrePrefixes.cableGt01, OrePrefixes.cableGt02, OrePrefixes.cableGt04,
        OrePrefixes.cableGt08, OrePrefixes.cableGt12, OrePrefixes.cableGt16 };
    private static final int[] SIZE = { 1, 2, 4, 8, 12, 16 };
    private static final float[] WIRE_THICK = { 0.125F, 0.25F, 0.375F, 0.5F, 0.625F, 0.75F };
    private static final float[] CABLE_THICK = { 0.25F, 0.375F, 0.5F, 0.625F, 0.75F, 0.875F };


    private WerkstoffCableLoader() {}

    /**
     * Register placeable wires + cables and their recipes for a Werkstoff.
     *
     * @param w              the source Werkstoff
     * @param startId        first of 12 consecutive free GT meta-tile-entity IDs
     * @param lossWire       EU/t lost per block for bare wire (0 = lossless)
     * @param lossCable      EU/t lost per block for insulated cable (0 = lossless)
     * @param amperage       base amperage for the 1x wire, scaling with thickness
     * @param voltage        max voltage the wire/cable handles (e.g. TierEU.ZPM)
     * @param recipeEU       EU/t for the generated wiremill/assembler recipes
     * @param recipeDuration ticks for the generated recipes
     * @param canShock       whether bare wire electrocutes the player on contact
     *
     */
    public static void register(Werkstoff w, int startId, long lossWire, long lossCable, long amperage, long voltage,
        long recipeEU, int recipeDuration, boolean canShock) {
        Materials m = w.getBridgeMaterial();
        if (m == null) {
            System.err.println("[GTNHPP] WerkstoffCableLoader: no bridge material for " + w.getDefaultName());
            return;
        }
        String nw = "wire." + m.mName.toLowerCase();
        String nc = "cable." + m.mName.toLowerCase();

        // ── Placeable items (MTECable entities) ──
        for (int i = 0; i < 6; i++) {
            GTOreDictUnificator.registerOre(
                WIRE[i],
                m,
                new MTECable(
                    startId + i,
                    nw + "." + pad(SIZE[i]),
                    "gt.oreprefix." + SIZE[i] + "x_material_wire",
                    WIRE_THICK[i],
                    m,
                    lossWire,
                    (long) SIZE[i] * amperage,
                    voltage,
                    false,
                    canShock).getStackForm(1L));
        }

        for (int i = 0; i < 6; i++) {
            GTOreDictUnificator.registerOre(
                CABLE[i],
                m,
                new MTECable(
                    startId + 6 + i,
                    nc + "." + pad(SIZE[i]),
                    "gt.oreprefix." + SIZE[i] + "x_material_cable",
                    CABLE_THICK[i],
                    m,
                    lossCable,
                    (long) SIZE[i] * amperage,
                    voltage,
                    true,
                    false).getStackForm(1L));
        }

        // ── Recipes ──
        // Wiremill: 1 ingot -> 2x 1x-wire
        GTValues.RA.stdBuilder()
            .itemInputs(w.get(OrePrefixes.ingot, 1))
            .itemOutputs(GTOreDictUnificator.get(WIRE[0], m, 2))
            .duration(recipeDuration)
            .eut(recipeEU)
            .addTo(RecipeMaps.wiremillRecipes);

        // Assembler: combine 1x-wires into thicker wires (circuit selects thickness)
        for (int i = 1; i < 6; i++) {
            GTValues.RA.stdBuilder()
                .itemInputs(GTOreDictUnificator.get(WIRE[0], m, SIZE[i]), GTUtility.getIntegratedCircuit(SIZE[i]))
                .itemOutputs(GTOreDictUnificator.get(WIRE[i], m, 1))
                .duration(recipeDuration)
                .eut(recipeEU)
                .addTo(RecipeMaps.assemblerRecipes);
        }
    }

    private static String pad(int n) {
        return n < 10 ? "0" + n : Integer.toString(n);
    }
}
