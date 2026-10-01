package dev.genesis.core;
import dev.genesis.GenesisCore;
import dev.genesis.abilities.AdaptationManager;
import dev.genesis.config.CoreConfig;
import dev.genesis.network.CoreNetwork;
import dev.genesis.palladium.PalladiumPowerBridge;
import dev.genesis.species.SpeciesRegistry;
import dev.genesis.transformation.TransformationManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=GenesisCore.MOD_ID)
public final class CoreEvents {
    @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent e) {
        if(e.phase!=TickEvent.Phase.END||!(e.player instanceof ServerPlayer p))return;
        var d=GenesisCoreData.get(p);var s=d.state;
        if(!s.installed)return;
        s.advanceTick();if(p.tickCount%2==0)d.packetTokens=Math.min(12,d.packetTokens+1);
        if(d.scanCooldown>0)d.scanCooldown--;if(d.exposureDelay>0)d.exposureDelay--;
        boolean visual=false;
        if(d.shieldTicks>0 && --d.shieldTicks==0)visual=true;
        if(d.movementTicks>0 && --d.movementTicks==0)visual=true;
        if(d.bloomTicks>0 && --d.bloomTicks==0)visual=true;
        if(d.animationTicks>0)d.animationTicks--;
        PalladiumPowerBridge.flags(p);
        if(d.movementTicks>0&&s.form.equals("colonyx")&&p.horizontalCollision&&!p.isShiftKeyDown()){var v=p.getDeltaMovement();p.setDeltaMovement(v.x,.16,v.z);p.fallDistance=0;p.hurtMarked=true;}
        if(p.tickCount%20==0) {
            s.energySecond(CoreConfig.DRAIN.get(),CoreConfig.REGEN.get());
            if(d.movementTicks>0) {s.energy=Math.max(0,s.energy-1.5);if(s.energy==0)d.movementTicks=0;}
            if(s.transformed()&&s.energy<=0){TransformationManager.revert(p);return;}
            if(d.dirty)CoreNetwork.syncOwner(p);else CoreNetwork.syncVitals(p);
        }
        if(p.tickCount%100==0) {
            var biome=p.level().getBiome(p.blockPosition()).unwrapKey();
            biome.ifPresent(k->{if(s.discover("biome:"+k.location()))d.dirty=true;});
        }
        if(p.tickCount%1200==0&&s.transformed())s.addXp(6);
        if(visual)CoreNetwork.sync(p);
    }
    @SubscribeEvent public static void size(EntityEvent.Size e) {
        if(!(e.getEntity() instanceof Player p))return;
        p.getCapability(CoreCapability.CAP).ifPresent(d->{var s=SpeciesRegistry.get(d.state.form);if(s!=null&&s.playable()) {
            float scale=d.state.absolute?1.1f:1;
            float h=p.isCrouching()?s.height()*.85f:s.height();
            e.setNewSize(EntityDimensions.scalable(s.width()*(d.state.absolute?1.15f:1),h*scale));e.setNewEyeHeight(Math.min(h-.12f,s.eyeHeight())*scale);
        }});
    }
    @SubscribeEvent public static void clone(PlayerEvent.Clone e) {
        e.getOriginal().reviveCaps();
        e.getOriginal().getCapability(CoreCapability.CAP).ifPresent(old->{
            var next=GenesisCoreData.get(e.getEntity());next.load(old.save());
            if(e.isWasDeath()) {next.state.leave(Math.max(next.state.recoveryTicks(),old.state.transformationCooldown));next.state.energy=Math.min(next.state.energy,30);}
            next.clearTransient();
        });
        e.getOriginal().invalidateCaps();
    }
    @SubscribeEvent public static void login(PlayerEvent.PlayerLoggedInEvent e){if(e.getEntity() instanceof ServerPlayer p)TransformationManager.reconcile(p);}
    @SubscribeEvent public static void respawn(PlayerEvent.PlayerRespawnEvent e){if(e.getEntity() instanceof ServerPlayer p)TransformationManager.reconcile(p);}
    @SubscribeEvent public static void dimension(PlayerEvent.PlayerChangedDimensionEvent e){if(e.getEntity() instanceof ServerPlayer p)TransformationManager.reconcile(p);}
    @SubscribeEvent public static void tracking(PlayerEvent.StartTracking e){if(e.getEntity() instanceof ServerPlayer p&&e.getTarget() instanceof ServerPlayer t)CoreNetwork.tracking(p,t);}
    @SubscribeEvent(priority=EventPriority.LOW) public static void hurt(LivingHurtEvent e) {
        if(e.getSource().getDirectEntity()!=null && e.getSource().getDirectEntity().getTags().contains("genesis_projectile") && e.getSource().getEntity() instanceof ServerPlayer shooter) {
            if(e.getEntity().isAlliedTo(shooter) || (e.getEntity() instanceof Player target && (!CoreConfig.PVP.get() || !shooter.canHarmPlayer(target)))) {e.setCanceled(true);return;}
        }
        if(e.getEntity() instanceof ServerPlayer p) {
            var d=GenesisCoreData.get(p);
            if(!d.state.installed)return;
            AdaptationManager.onHurt(p,e);
            if(d.shieldTicks>0)e.setAmount(e.getAmount()*.55f);
            if(!d.state.transformed()&&CoreConfig.EMERGENCY_SHIELD.get()&&e.getAmount()>=p.getHealth()&&d.state.energy>=30&&d.state.cooldowns[2]==0) {
                d.state.energy-=30;d.state.cooldowns[2]=1200;e.setAmount(e.getAmount()*.35f);d.shieldTicks=40;CoreNetwork.sync(p);
            }
        }
    }
    @SubscribeEvent public static void killed(LivingDeathEvent e) {
        if(e.getSource().getEntity() instanceof ServerPlayer p&&e.getEntity() instanceof Monster) {
            var d=GenesisCoreData.get(p);d.state.addXp(12);if(d.state.installed)CoreNetwork.syncOwner(p);
        }
    }
}
