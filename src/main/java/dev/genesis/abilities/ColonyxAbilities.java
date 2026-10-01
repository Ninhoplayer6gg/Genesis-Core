package dev.genesis.abilities;
import dev.genesis.core.GenesisCoreData;
import net.minecraft.server.level.ServerPlayer;
public final class ColonyxAbilities {
    public static boolean cast(ServerPlayer p,int slot) {
        var d=GenesisCoreData.get(p);
        switch(slot) {
            case 0 -> Combat.strike(p,5.5,6,true,.25);
            case 1 -> d.shieldTicks=100;
            case 2 -> {if(p.getHealth()>=p.getMaxHealth())return false;p.heal(5);}
            case 3 -> {d.bloomTicks=160;Combat.strike(p,5,8,false,.6);}
            case 4 -> d.movementTicks=d.movementTicks>0?0:200;
            default -> {return false;}
        }
        return true;
    }
}
