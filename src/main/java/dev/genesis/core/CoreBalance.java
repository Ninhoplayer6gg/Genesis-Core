package dev.genesis.core;
/** Synchronized by Forge server configuration. No game dependency in domain logic. */
public final class CoreBalance {
    public static double baseEnergy=100, energyPerTier=50, baseDrain=1.2, absoluteDrain=4.5;
    public static double baseRegen=3, regenPerTier=1.5, xpMultiplier=1;
    private CoreBalance() { }
}
