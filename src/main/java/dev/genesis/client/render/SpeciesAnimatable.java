package dev.genesis.client.render;
import dev.genesis.client.ClientState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.constant.DataTickets;
public final class SpeciesAnimatable implements GeoReplacedEntity {
    public final String species;
    private final AnimatableInstanceCache cache=GeckoLibUtil.createInstanceCache(this);
    public SpeciesAnimatable(String id){species=id;}
    @Override public EntityType<?> getReplacingEntityType(){return EntityType.PLAYER;}
    @Override public AnimatableInstanceCache getAnimatableInstanceCache(){return cache;}
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar r){
        r.add(new AnimationController<>(this,"body",3,state->{
            var entity=state.getData(DataTickets.ENTITY);
            String animation="idle";
            if(entity instanceof Player p){
                var d=ClientState.data(p.getUUID());
                animation=d.animationTicks>0?switch(d.animation){
                    case 2 -> "guard";case 3 -> "ultimate";case 4 -> "adaptation_scan";
                    case 5 -> "adaptation_complete";case 6 -> "transformation";case 7 -> "jump";
                    case 8 -> "regeneration";case 9 -> "magnetic_polarity";case 10 -> "magnetic_shard";
                    case 11 -> "magnetic_lift";case 12 -> "colonial_lance";case 13 -> "colony_regroup";
                    case 14 -> "colony_climb";default -> "attack";
                }:p.hurtTime>0?"hurt":!p.onGround()?(p.getDeltaMovement().y>0?"jump":"fall"):state.isMoving()?(p.isSprinting()?"run":"walk"):"idle";
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop(animation));
        }));
    }
}
