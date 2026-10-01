package dev.genesis.core;
import net.minecraft.nbt.*;
import net.minecraft.world.entity.player.Player;
import java.util.*;
public final class GenesisCoreData {
    public CoreState state=new CoreState();
    public int shieldTicks, movementTicks, bloomTicks, animationTicks, animation;
    public int scanCooldown, packetTokens=12, exposureDelay;
    public boolean dirty=true;
    public final Set<String> sessionBiomes=new HashSet<>();
    public static GenesisCoreData get(Player p) { return p.getCapability(CoreCapability.CAP).orElseThrow(()->new IllegalStateException("Genesis capability missing")); }
    public CompoundTag save() {
        var n=new CompoundTag();var s=state;
        n.putInt("Schema",1);n.putBoolean("Installed",s.installed);n.putBoolean("AbsoluteUnlocked",s.absoluteUnlocked);
        n.putInt("Xp",s.xp);n.putDouble("Energy",s.energy);n.putString("Form",s.form);n.putBoolean("Absolute",s.absolute);
        n.putInt("Recovery",s.transformationCooldown);n.putInt("AbilityLock",s.abilityLock);n.putIntArray("Cooldowns",s.cooldowns);
        n.putInt("Preset",s.preset);n.put("Discoveries",strings(s.discoveries));n.put("Unlocked",strings(s.unlocked));n.put("Upgrades",strings(s.upgrades));
        var a=new CompoundTag();s.adaptations.forEach(a::putDouble);n.put("Adaptations",a);
        return n;
    }
    public CompoundTag sync() {
        var n=save();n.putInt("Shield",shieldTicks);n.putInt("Movement",movementTicks);n.putInt("Bloom",bloomTicks);
        n.putInt("Animation",animation);n.putInt("AnimationTicks",animationTicks);return n;
    }
    public CompoundTag vitals() {
        var n=sync();n.remove("Discoveries");n.remove("Upgrades");n.putBoolean("VitalsOnly",true);return n;
    }
    public CompoundTag visualSync() {
        var n=new CompoundTag();n.putBoolean("VisualOnly",true);n.putBoolean("Installed",state.installed);
        n.putString("Form",state.form);n.putBoolean("Absolute",state.absolute);n.putInt("Preset",state.preset);
        var a=new CompoundTag();state.adaptations.forEach(a::putDouble);n.put("Adaptations",a);
        n.putInt("Shield",shieldTicks);n.putInt("Bloom",bloomTicks);n.putInt("Animation",animation);n.putInt("AnimationTicks",animationTicks);return n;
    }
    public void load(CompoundTag n) {
        CoreState s=new CoreState();s.installed=n.getBoolean("Installed");s.xp=Math.max(0,Math.min(1000000,n.getInt("Xp")));
        s.energy=Double.isFinite(n.getDouble("Energy"))?Math.max(0,Math.min(s.maxEnergy(),n.getDouble("Energy"))):0;
        s.absoluteUnlocked=n.getBoolean("AbsoluteUnlocked");s.form=n.getString("Form");
        var species=dev.genesis.species.SpeciesRegistry.get(s.form);
        if(species==null || !species.playable() || !s.installed) s.form="";
        s.absolute=n.getBoolean("Absolute")&&s.form.equals("adaptaris")&&(s.absoluteUnlocked||n.getBoolean("VisualOnly"));
        s.transformationCooldown=clamp(n.getInt("Recovery"));s.abilityLock=clamp(n.getInt("AbilityLock"));
        int[] cd=n.getIntArray("Cooldowns");for(int i=0;i<Math.min(cd.length,5);i++)s.cooldowns[i]=clamp(cd[i]);
        s.preset=Math.floorMod(n.getInt("Preset"),3);
        readStrings(n,"Discoveries",s.discoveries);readStrings(n,"Unlocked",s.unlocked);readStrings(n,"Upgrades",s.upgrades);
        var a=n.getCompound("Adaptations");for(String key:a.getAllKeys()) if(s.adaptations.size()<2 && Double.isFinite(a.getDouble(key))) s.adaptations.put(key,Math.max(0,Math.min(100,a.getDouble(key))));
        s.unlockAvailable();state=s;shieldTicks=clamp(n.getInt("Shield"));movementTicks=clamp(n.getInt("Movement"));bloomTicks=clamp(n.getInt("Bloom"));
        animation=n.getInt("Animation");animationTicks=clamp(n.getInt("AnimationTicks"));dirty=true;
    }
    private static int clamp(int n) { return Math.max(0,Math.min(72000,n)); }
    private static ListTag strings(Set<String> values) {var l=new ListTag();values.forEach(v->l.add(StringTag.valueOf(v)));return l;}
    private static void readStrings(CompoundTag n,String key,Set<String> dest) {var l=n.getList(key,Tag.TAG_STRING);for(int i=0;i<Math.min(l.size(),1024);i++) {String v=l.getString(i);if(v.length()<=160)dest.add(v);}}
    public void clearTransient() {shieldTicks=movementTicks=bloomTicks=animationTicks=0;animation=0;}
}
