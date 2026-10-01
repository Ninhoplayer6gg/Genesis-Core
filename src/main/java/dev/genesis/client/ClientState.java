package dev.genesis.client;
import dev.genesis.core.GenesisCoreData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import java.util.*;
public final class ClientState {
    private static final Map<UUID,GenesisCoreData> CACHE=new HashMap<>();
    public static GenesisCoreData data(UUID id){return CACHE.computeIfAbsent(id,k->new GenesisCoreData());}
    public static GenesisCoreData local(){var p=Minecraft.getInstance().player;return p==null?new GenesisCoreData():data(p.getUUID());}
    public static void accept(UUID id,CompoundTag tag) {
        if(tag==null)return;var d=data(id);if(tag.getBoolean("VitalsOnly")){var merged=d.save();for(String key:tag.getAllKeys())merged.put(key,tag.get(key).copy());tag=merged;}String old=d.state.form;boolean oldAbs=d.state.absolute;d.load(tag);
        var level=Minecraft.getInstance().level;
        if(level!=null){var p=level.getPlayerByUUID(id);if(p!=null){GenesisCoreData.get(p).load(tag);if(!old.equals(d.state.form)||oldAbs!=d.state.absolute)p.refreshDimensions();}}
    }
    public static void tick(){for(var d:CACHE.values()){if(d.animationTicks>0)d.animationTicks--;if(d.shieldTicks>0)d.shieldTicks--;if(d.movementTicks>0)d.movementTicks--;if(d.bloomTicks>0)d.bloomTicks--;d.state.advanceTick();}}
    public static void clear(){CACHE.clear();}
}
