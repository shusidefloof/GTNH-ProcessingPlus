package com.gtnh.processingplus.recipes;

import com.gtnh.processingplus.GTNHProcessingPlus;

/**
 * Fault isolation for recipe registration: one bad lookup (missing OreDict entry, absent fluid, null
 * material) must only drop its own chain, not every chain registered after it.
 */
final class RecipeGuard {

    private RecipeGuard() {}

    static void run(String name, Runnable chain) {
        try {
            chain.run();
        } catch (Throwable t) {
            GTNHProcessingPlus.LOG.error("Recipe chain '" + name + "' failed — skipped the rest of it", t);
        }
    }
}
