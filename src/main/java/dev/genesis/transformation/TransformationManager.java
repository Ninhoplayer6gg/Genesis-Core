package dev.genesis.transformation;
import dev.genesis.core.*;
import dev.genesis.config.CoreConfig;
import dev.genesis.network.CoreNetwork;
import dev.genesis.palladium.PalladiumPowerBridge;
import dev.genesis.species.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.AABB;
public final class TransformationManager {
    public static void transform(ServerPlayer p,String id,boolean absolute) {
        var d=GenesisCoreData.get(p);var s=d.state;
        String error=s.denial(id,absolute,CoreConfig.START_COST.get());
        if(!error.isEmpty()) {message(p,error);return;}
        if(!p.isAlive()||p.isSpectator()||p.isPassenger()||p.isSleeping()) return;
        Species species=SpeciesRegistry.get(id);
        float width=species.width()*(absolute?1.15f:1),height=species.height()*(absolute?1.1f:1);
        if(!p.level().noCollision(p,EntityDimensions.scalable(width,height).makeBoundingBox(p.position()))) {message(p,"Sem espaço para esta anatomia.");return;}
        if(!PalladiumPowerBridge.replace(p,species.powerId(absolute))) {message(p,"Power set ausente. Consulte os logs do Palladium.");return;}
        d.clearTransient();PalladiumPowerBridge.clearFlags(p);
        s.enter(id,absolute,CoreConfig.START_COST.get(),cooldown(s));d.animation=6;d.animationTicks=8;p.refreshDimensions();
        pulse(p);CoreNetwork.sync(p);message(p,species.name()+(absolute?" • Forma Absoluta":""));
    }
    public static void revert(ServerPlayer p) {
        var d=GenesisCoreData.get(p);if(!d.state.transformed()) return;
        d.state.leave(cooldown(d.state));d.clearTransient();PalladiumPowerBridge.replace(p,"");PalladiumPowerBridge.clearFlags(p);
        p.refreshDimensions();safeExit(p);pulse(p);CoreNetwork.sync(p);
    }
    private static int cooldown(CoreState s){return s.recoveryTicks()*CoreConfig.COOLDOWN.get()/100;}
    private static void safeExit(ServerPlayer p) {
        if(p.level().noCollision(p))return;
        for(int y=0;y<=6;y++) for(int r=0;r<=3;r++) for(int x=-r;x<=r;x++) for(int z=-r;z<=r;z++) {
            AABB b=EntityDimensions.scalable(.6f,1.8f).makeBoundingBox(p.getX()+x,p.getY()+y,p.getZ()+z);
            if(p.level().noCollision(p,b)){p.teleportTo(p.getX()+x,p.getY()+y,p.getZ()+z);p.fallDistance=0;return;}
        }
    }
    public static void reconcile(ServerPlayer p) {
        var d=GenesisCoreData.get(p);d.clearTransient();PalladiumPowerBridge.clearFlags(p);
        var s=d.state;var species=SpeciesRegistry.get(s.form);
        if(s.transformed()&&(species==null||s.energy<=0||!s.unlocked.contains(s.form)))s.leave(cooldown(s));
        if(s.transformed()&&!PalladiumPowerBridge.replace(p,species.powerId(s.absolute))) {
            s.leave(cooldown(s));
            PalladiumPowerBridge.replace(p,"");
        } else if(!s.transformed()) PalladiumPowerBridge.replace(p,"");
        p.refreshDimensions();CoreNetwork.sync(p);
    }
    public static void pulse(ServerPlayer p) {
        p.serverLevel().sendParticles(ParticleTypes.ENCHANT,p.getX(),p.getY()+1,p.getZ(),18,.35,.5,.35,.08);
        p.level().playSound(null,p.blockPosition(),SoundEvents.BEACON_ACTIVATE,SoundSource.PLAYERS,.5f,1.4f);
    }
    public static void message(ServerPlayer p,String text){p.displayClientMessage(Component.literal(text),true);}
}
