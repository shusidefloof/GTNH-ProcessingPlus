package com.gtnh.processingplus.machines.spc;

/**
 * The categories of external upgrade module that can be data-stick linked to the SPC.
 * Each links through a matching support-bay adapter and gates a band of board recipes.
 */
public enum SPCModuleType {

    /** Bio-Lithography Module — gates wetware + bio boards. */
    BIO,
    /** Cryo-Stabilization Module — gates optical boards / efficiency. */
    CRYO,
    /** Quantum Alignment Module — gates UEV+ / optical-quantum tiers. */
    QUANTUM
}
