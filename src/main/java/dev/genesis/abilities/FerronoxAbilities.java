package dev.genesis.abilities;
import dev.genesis.core.GenesisCoreData;
import dev.genesis.palladium.PalladiumPowerBridge;
import dev.genesis.transformation.TransformationManager;
import dev.genesis.GenesisCore;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.Vec3;
public final class FerronoxAbilities {
    public static final TagKey<Item> METALS=TagKey.create(Registries.ITEM,GenesisCore.id("magnetic_materials"));
    public static boolean cast(ServerPlayer p,int slot) {
        var d=GenesisCoreData.get(p);
        switch(slot) {
            case 0 -> {
                int index=-1;for(int i=0;i<p.getInventory().getContainerSize();i++)if(p.getInventory().getItem(i).is(Items.IRON_NUGGET)){index=i;break;}
                if(index<0&&!p.isCreative()){TransformationManager.message(p,"Precisa de 1 pepita de ferro para formar um estilhaço.");return false;}
                if(!PalladiumPowerBridge.projectile(p,"orbital_shard"))return false;
                if(!p.isCreative())p.getInventory().getItem(index).shrink(1);
            }
            case 1 -> {
                for(var item:p.level().getEntitiesOfClass(ItemEntity.class,p.getBoundingBox().inflate(9),i->i.getItem().is(METALS)&&p.hasLineOfSight(i))) {
                    Vec3 v=p.position().add(0,.8,0).subtract(item.position()).normalize().scale(p.isShiftKeyDown()?-.9:.8);
                    item.setDeltaMovement(v);item.hurtMarked=true;
                }
                for(var target:Combat.targets(p,7)) {
                    boolean metal=false;for(var armor:target.getArmorSlots())if(armor.is(METALS))metal=true;
                    if(metal){Vec3 v=p.position().subtract(target.position()).normalize().scale(p.isShiftKeyDown()?-.7:.7);target.push(v.x,.15,v.z);target.hurtMarked=true;}
                }
            }
            case 2 -> d.shieldTicks=140;
            case 3 -> {
                Combat.strike(p,6,9,false,.25);
                for(var target:Combat.targets(p,6))target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN,80,2));
            }
            case 4 -> d.movementTicks=d.movementTicks>0?0:120;
            default -> {return false;}
        }
        return true;
    }
}
