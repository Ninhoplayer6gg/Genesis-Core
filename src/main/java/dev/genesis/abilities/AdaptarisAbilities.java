package dev.genesis.abilities;
import dev.genesis.core.GenesisCoreData;
import dev.genesis.palladium.PalladiumPowerBridge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.*;
public final class AdaptarisAbilities {
    public static boolean cast(ServerPlayer p,int slot) {
        var d=GenesisCoreData.get(p);
        switch(slot) {
            case 0 -> {Combat.strike(p,3.5,7,true,.45);if(d.state.absolute)counter(p,3.5);}
            case 1 -> d.shieldTicks=100;
            case 2 -> {if(p.getHealth()>=p.getMaxHealth())return false;p.heal(6);}
            case 3 -> {Combat.strike(p,d.state.absolute?7:5,10,false,1.05);if(d.state.absolute){counter(p,7);PalladiumPowerBridge.projectile(p,"counter_spore");}}
            case 4 -> {d.movementTicks=100;}
            default -> {return false;}
        }
        return true;
    }
    private static void counter(ServerPlayer p,double range) {
        var s=GenesisCoreData.get(p).state;
        for(var e:Combat.targets(p,range)) {
            if(s.adaptations.getOrDefault("thermal",0d)>=50)e.setSecondsOnFire(3);
            if(s.adaptations.getOrDefault("cold",0d)>=50)e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,60,1));
            if(s.adaptations.getOrDefault("electric",0d)>=50)e.hurt(p.damageSources().playerAttack(p),3);
        }
    }
}
