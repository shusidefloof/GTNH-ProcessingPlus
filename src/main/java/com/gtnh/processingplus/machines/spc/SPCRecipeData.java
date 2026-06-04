package com.gtnh.processingplus.machines.spc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gregtech.api.util.GTRecipe;

/**
 * Stores the station-sequence requirements for an SPC recipe and provides
 * validation + tier-excess calculation used by checkProcessing().
 */
public class SPCRecipeData {

    private static final Map<GTRecipe, SPCRecipeData> REGISTRY = new HashMap<>();

    public final MachineType[] stationTypes;
    public final int[] stationMinTiers;
    public final MachineType supportType;
    public final int supportMinTier;
    /** External upgrade module this recipe requires (linked + adapter routed in), or null for none. */
    public final SPCModuleType requiredModule;

    private SPCRecipeData(MachineType[] types, int[] minTiers, MachineType supportType, int supportMinTier,
        SPCModuleType requiredModule) {
        this.stationTypes = types;
        this.stationMinTiers = minTiers;
        this.supportType = supportType;
        this.supportMinTier = supportMinTier;
        this.requiredModule = requiredModule;
    }

    /** Register sequence data for a recipe that needs no support machine or module. */
    public static void register(Collection<GTRecipe> recipes, MachineType[] types, int[] minTiers) {
        register(recipes, types, minTiers, MachineType.NONE, 0, null);
    }

    /** Register sequence data for a recipe that requires an external upgrade module. */
    public static void register(Collection<GTRecipe> recipes, MachineType[] types, int[] minTiers,
        SPCModuleType requiredModule) {
        register(recipes, types, minTiers, MachineType.NONE, 0, requiredModule);
    }

    /** Register sequence data with a support machine and/or module requirement. */
    public static void register(Collection<GTRecipe> recipes, MachineType[] types, int[] minTiers,
        MachineType supportType, int supportMinTier, SPCModuleType requiredModule) {
        SPCRecipeData data = new SPCRecipeData(types, minTiers, supportType, supportMinTier, requiredModule);
        for (GTRecipe r : recipes) {
            REGISTRY.put(r, data);
        }
    }

    public static SPCRecipeData get(GTRecipe recipe) {
        return REGISTRY.get(recipe);
    }

    public boolean requiresSupport() {
        return supportType != MachineType.NONE;
    }

    /**
     * Validates the detected station list against this recipe's required sequence.
     *
     * Returns the minimum tier excess across all stations (>= 0) if all checks pass.
     * Returns -1 if any station has the wrong machine type or is below the minimum tier.
     *
     * minExcess == 0: all stations exactly at minimum → 1x parallel, no bonus
     * minExcess == 1: all stations at least 1 tier above minimum → 2x parallel + flat +1
     * minExcess == n: 2^n parallels
     */
    public int validateAndGetMinExcess(List<StationEntry> detected) {
        if (detected.size() < stationTypes.length) return -1;
        int minExcess = Integer.MAX_VALUE;
        for (int i = 0; i < stationTypes.length; i++) {
            StationEntry s = detected.get(i);
            if (s.type != stationTypes[i]) return -1;
            if (s.tier < stationMinTiers[i]) return -1;
            minExcess = Math.min(minExcess, s.tier - stationMinTiers[i]);
        }
        return stationTypes.length == 0 ? 0 : minExcess;
    }
}
