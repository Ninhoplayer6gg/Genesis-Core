package dev.genesis.palladium;
import dev.genesis.core.GenesisCoreData;
import dev.genesis.species.SpeciesRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.threetag.palladium.power.PowerManager;
import net.threetag.palladium.power.SuperpowerUtil;
import net.threetag.palladium.power.ability.AbilityUtil;
import java.util.*;
/** The ONLY package that imports Palladium. Never clears another mod's powers. */
public final class PalladiumPowerBridge {
    public static boolean exists(ServerPlayer p,String id) {return PowerManager.getInstance(p.level()).getPower(new ResourceLocation(id))!=null;}
    public static boolean replace(ServerPlayer p,String id) {
        if(!id.isEmpty()&&!exists(p,id)) return false;
        List<ResourceLocation> keep=new ArrayList<>(SuperpowerUtil.getSuperpowerIds(p));
        keep.removeIf(r->SpeciesRegistry.ownedPower(r.toString()));
        if(!id.isEmpty()) keep.add(new ResourceLocation(id));
        SuperpowerUtil.setSuperpowerIds(p,keep);return true;
    }
    public static boolean ready(ServerPlayer p) {
        var s=GenesisCoreData.get(p).state;
        return s.transformed()&&AbilityUtil.hasPower(p,new ResourceLocation(SpeciesRegistry.get(s.form).powerId(s.absolute)));
    }
    /** Trigger a built-in one-shot on server, after Genesis validates energy/cooldown.
     * Its JSON condition is false, so Palladium keys cannot bypass Genesis validation. */
    public static boolean projectile(ServerPlayer p,String key) {
        var s=GenesisCoreData.get(p).state;
        var entry=AbilityUtil.getInstance(p,new ResourceLocation(SpeciesRegistry.get(s.form).powerId(s.absolute)),key);
        if(entry==null) return false;
        entry.getConfiguration().getAbility().tick(p,entry,entry.getHolder(),true);
        return true;
    }
    public static void flags(ServerPlayer p) {
        var d=GenesisCoreData.get(p);
        flag(p,"genesis_shield",d.shieldTicks>0&&d.state.transformed());
        flag(p,"genesis_lift",d.movementTicks>0&&d.state.form.equals("ferronox"));
        flag(p,"genesis_leap",d.movementTicks>0&&d.state.form.equals("adaptaris"));
        flag(p,"genesis_climb",d.movementTicks>0&&d.state.form.equals("colonyx"));
        flag(p,"genesis_bloom",d.bloomTicks>0&&d.state.form.equals("colonyx"));
    }
    private static void flag(ServerPlayer p,String tag,boolean enabled) {if(enabled)p.addTag(tag);else p.removeTag(tag);}
    public static void tickForValidation(ServerPlayer p) { PowerManager.getPowerHandler(p).ifPresent(h->h.tick()); }
    public static void clearFlags(ServerPlayer p) {for(String t:List.of("genesis_shield","genesis_lift","genesis_leap","genesis_climb","genesis_bloom"))p.removeTag(t);}
}
