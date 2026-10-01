package dev.genesis.abilities;
import dev.genesis.core.*;
import dev.genesis.config.CoreConfig;
import dev.genesis.database.ScannerManager;
import dev.genesis.network.CoreNetwork;
import dev.genesis.palladium.PalladiumPowerBridge;
import dev.genesis.transformation.TransformationManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
public final class AbilityManager {
    private static final double[] COST={8,14,22,36,12};
    private static final int[] CD={30,160,300,600,160};
    public static void request(ServerPlayer p,int slot) {
        var d=GenesisCoreData.get(p);var s=d.state;
        if(!s.installed||slot<0||slot>4)return;
        if(!s.transformed()) {
            if(slot==0)ScannerManager.scan(p);
            else if(slot==1&&d.scanCooldown==0){ScannerManager.radar(p);d.scanCooldown=80;}
            else if(slot==2&&s.cooldowns[2]==0&&s.energy>=25&&CoreConfig.EMERGENCY_SHIELD.get()){
                s.energy-=25;s.cooldowns[2]=600;d.shieldTicks=100;CoreNetwork.sync(p);
            }
            return;
        }
        double cost=COST[slot]*CoreConfig.ABILITY_COST.get();
        if(!s.canCast(slot,cost)||!PalladiumPowerBridge.ready(p))return;
        boolean ok=switch(s.form) {
            case "adaptaris" -> AdaptarisAbilities.cast(p,slot);
            case "ferronox" -> FerronoxAbilities.cast(p,slot);
            case "colonyx" -> ColonyxAbilities.cast(p,slot);
            default -> false;
        };
        if(!ok)return;
        s.pay(slot,cost,CD[slot]);d.animation=slot==4?7:slot==1?2:slot==3?3:1;d.animationTicks=slot==3?40:15;
        PalladiumPowerBridge.flags(p);CoreNetwork.sync(p);
        p.level().playSound(null,p.blockPosition(),SoundEvents.AMETHYST_BLOCK_CHIME,SoundSource.PLAYERS,.45f,.6f+slot*.15f);
        if(s.energy<=0)TransformationManager.revert(p);
    }
}
