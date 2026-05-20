package com.gtnh.processingplus.machines;

public class StationEntry {

    public static final StationEntry EMPTY = new StationEntry(MachineType.NONE, 0);

    public final MachineType type;
    public final int tier;

    public StationEntry(MachineType type, int tier) {
        this.type = type;
        this.tier = tier;
    }

    public boolean isEmpty() {
        return type == MachineType.NONE;
    }

    public boolean matches(MachineType requiredType, int minTier) {
        return type == requiredType && tier >= minTier;
    }

    @Override
    public String toString() {
        return isEmpty() ? "empty" : (type.name() + " T" + tier);
    }
}
