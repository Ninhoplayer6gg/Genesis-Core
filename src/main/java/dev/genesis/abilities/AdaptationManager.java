package dev.genesis.abilities;
import dev.genesis.core.GenesisCoreData;
import dev.genesis.network.CoreNetwork;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
public final class AdaptationManager {
    public static void onHurt(ServerPlayer p,LivingHurtEvent e) {
        var d=GenesisCoreData.get(p);var s=d.state;
        if(!s.form.equals("adaptaris")||e.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY))return;
        String type=e.getSource().is(DamageTypeTags.IS_FIRE)?"thermal":e.getSource().is(DamageTypeTags.IS_FREEZING)?"cold":e.getSource().is(DamageTypeTags.IS_LIGHTNING)?"electric":"impact";
        // Apply only the adaptation known BEFORE this hit, never instant immunity.
        e.setAmount((float)(e.getAmount()*(1-s.mitigation(type))));
        if(d.exposureDelay==0) {
            double before=s.adaptations.getOrDefault(type,0d);
            s.expose(type,e.getAmount());d.exposureDelay=30;
            if(s.adaptations.getOrDefault(type,0d)>before) {d.animation=s.adaptations.get(type)>=100?5:4;d.animationTicks=12;}
            CoreNetwork.sync(p);
        }
    }
}
