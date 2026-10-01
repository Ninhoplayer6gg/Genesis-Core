package dev.genesis.config;
import net.minecraftforge.common.ForgeConfigSpec;
import dev.genesis.core.CoreBalance;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
@Mod.EventBusSubscriber(modid="genesis",bus=Mod.EventBusSubscriber.Bus.MOD)
public final class CoreConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.DoubleValue DRAIN, REGEN, START_COST, ABILITY_COST;
    public static final ForgeConfigSpec.IntValue COOLDOWN, SCAN_RANGE;
    public static final ForgeConfigSpec.BooleanValue PVP, EMERGENCY_SHIELD;
    public static final ForgeConfigSpec.DoubleValue BASE_ENERGY, ENERGY_PER_TIER, BASE_DRAIN, ABSOLUTE_DRAIN, BASE_REGEN, REGEN_PER_TIER, XP;
    static {
        var b=new ForgeConfigSpec.Builder();
        BASE_ENERGY=b.defineInRange("baseEnergy",100d,10d,100000d);
        ENERGY_PER_TIER=b.defineInRange("energyPerTier",50d,0d,100000d);
        BASE_DRAIN=b.defineInRange("baseDrainPerSecond",1.2d,0d,1000d);
        ABSOLUTE_DRAIN=b.defineInRange("absoluteDrainPerSecond",4.5d,0d,1000d);
        BASE_REGEN=b.defineInRange("baseRegenPerSecond",3d,0d,1000d);
        REGEN_PER_TIER=b.defineInRange("regenPerTier",1.5d,0d,1000d);
        XP=b.defineInRange("xpMultiplier",1d,.1d,100d);
        DRAIN=b.defineInRange("energyDrainMultiplier",1d,.05d,20d);
        REGEN=b.defineInRange("energyRegenMultiplier",1d,.05d,20d);
        START_COST=b.defineInRange("transformationCost",8d,0d,100d);
        ABILITY_COST=b.defineInRange("abilityCostMultiplier",1d,.05d,20d);
        COOLDOWN=b.defineInRange("transformationCooldownPercent",100,10,1000);
        SCAN_RANGE=b.defineInRange("scannerRange",12,3,32);
        PVP=b.define("abilitiesAffectPlayers",false);
        EMERGENCY_SHIELD=b.define("emergencyShieldEnabled",true);
        SPEC=b.build();
    }
    @SubscribeEvent public static void config(ModConfigEvent e) {
        if(e.getConfig().getSpec()!=SPEC||e instanceof ModConfigEvent.Unloading)return;
        CoreBalance.baseEnergy=BASE_ENERGY.get();CoreBalance.energyPerTier=ENERGY_PER_TIER.get();
        CoreBalance.baseDrain=BASE_DRAIN.get();CoreBalance.absoluteDrain=ABSOLUTE_DRAIN.get();
        CoreBalance.baseRegen=BASE_REGEN.get();CoreBalance.regenPerTier=REGEN_PER_TIER.get();CoreBalance.xpMultiplier=XP.get();
    }
}
