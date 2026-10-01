package dev.genesis.core;
import dev.genesis.species.*;
import java.util.*;
/** Pure domain state: no client, world, Forge or Palladium dependency. */
public final class CoreState {
    public boolean installed, absolute, absoluteUnlocked;
    public String form = "";
    public int xp, transformationCooldown, abilityLock, preset;
    public double energy;
    public final int[] cooldowns = new int[5];
    public final Set<String> discoveries = new LinkedHashSet<>();
    public final Set<String> unlocked = new LinkedHashSet<>();
    public final Set<String> upgrades = new LinkedHashSet<>();
    public final Map<String, Double> adaptations = new LinkedHashMap<>();
    public int level() { int level=1, remaining=Math.max(0,xp); while(level<50 && remaining>=level*60) remaining-=60*level++; return level; }
    public int tier() { int l=level(); return l>=15?5:l>=10?4:l>=6?3:l>=3?2:1; }
    public double maxEnergy() { return CoreBalance.baseEnergy + (tier()-1)*CoreBalance.energyPerTier; }
    public double regen() { return CoreBalance.baseRegen + tier()*CoreBalance.regenPerTier; }
    public double drain() { return absolute ? CoreBalance.absoluteDrain : CoreBalance.baseDrain; }
    public int recoveryTicks() { return Math.max(20,180-(tier()-1)*35); }
    public boolean transformed() { return !form.isEmpty(); }
    public void install() { installed=true; energy=maxEnergy(); unlockAvailable(); }
    public void addXp(int amount) { if(installed) { xp=(int)Math.min(1000000d,xp+Math.max(0,amount)*CoreBalance.xpMultiplier); unlockAvailable(); } }
    public boolean discover(String id) {
        if(!installed || id.length()>160 || discoveries.size()>=1024 || !discoveries.add(id)) return false;
        addXp(18); unlockAvailable(); return true;
    }
    public void unlockAvailable() {
        if(!installed) return;
        for(Species s:SpeciesRegistry.playable())
            if(level()>=s.level() && (s.scan().isEmpty() || discoveries.contains(s.scan()))) unlocked.add(s.id());
    }
    public String denial(String id, boolean abs, double startCost) {
        Species s=SpeciesRegistry.get(id);
        if(!installed) return "Instale um Núcleo do Gênesis primeiro.";
        if(s==null || !s.playable()) return "Matriz indisponível nesta versão.";
        if(!unlocked.contains(id)) return "Matriz bloqueada: nível " + s.level()+" e descoberta necessária.";
        if(transformationCooldown>0) return "O Core está estabilizando.";
        if(abs && (!id.equals("adaptaris") || !absoluteUnlocked)) return "Forma Absoluta bloqueada.";
        if(form.equals(id) && absolute==abs) return "Esta forma já está ativa.";
        if(!Double.isFinite(energy)||energy<startCost) return "Energia insuficiente.";
        return "";
    }
    public void enter(String id, boolean abs, double startCost, int cooldown) {
        form=id; absolute=abs; energy-=startCost;
        transformationCooldown=cooldown; abilityLock=Math.max(abilityLock,10);
        adaptations.clear();
    }
    public void leave(int cooldown) {
        form=""; absolute=false; adaptations.clear();
        transformationCooldown=Math.max(transformationCooldown,cooldown);
        // Keep ability cooldowns and lock so switching/death cannot reset them.
    }
    public void advanceTick() {
        if(transformationCooldown>0) transformationCooldown--;
        if(abilityLock>0) abilityLock--;
        for(int i=0;i<5;i++) if(cooldowns[i]>0) cooldowns[i]--;
    }
    public void energySecond(double drainMultiplier,double regenMultiplier) {
        if(!installed) return;
        energy=Math.max(0,Math.min(maxEnergy(),energy + (transformed()?-drain()*drainMultiplier:regen()*regenMultiplier)));
    }
    public boolean canCast(int slot,double cost) {
        return installed&&transformed()&&slot>=0&&slot<5&&cooldowns[slot]==0&&abilityLock==0&&energy>=cost;
    }
    public void pay(int slot,double cost,int cooldown) { energy=Math.max(0,energy-cost);cooldowns[slot]=cooldown; abilityLock=5; }
    /** Exposure needs both repeated hits and elapsed analysis ticks. Maximum two slots. */
    public void expose(String type, double amount) {
        if(!form.equals("adaptaris") || (!adaptations.containsKey(type)&&adaptations.size()>=2)) return;
        adaptations.compute(type,(k,v)->Math.min(100,(v==null?0:v)+Math.min(14,Math.max(1,amount*1.6))));
    }
    public double mitigation(String type) { return Math.min(absolute?.65:.5,adaptations.getOrDefault(type,0d)/100d*(absolute?.65:.5)); }
}
