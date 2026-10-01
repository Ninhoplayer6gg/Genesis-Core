package dev.genesis.database;
import dev.genesis.core.GenesisCoreData;
import dev.genesis.config.CoreConfig;
import dev.genesis.network.CoreNetwork;
import dev.genesis.transformation.TransformationManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.ParticleTypes;
public final class ScannerManager {
    public static void scan(ServerPlayer p) {
        var d=GenesisCoreData.get(p);if(!d.state.installed||d.scanCooldown>0||d.state.energy<3)return;
        d.scanCooldown=30;
        int range=CoreConfig.SCAN_RANGE.get();Vec3 start=p.getEyePosition(),end=start.add(p.getLookAngle().scale(range));
        BlockHitResult block=p.level().clip(new ClipContext(start,end,ClipContext.Block.OUTLINE,ClipContext.Fluid.ANY,p));
        double nearest=block.getType()==HitResult.Type.MISS?range*range:start.distanceToSqr(block.getLocation());
        LivingEntity found=null;
        for(var e:p.level().getEntitiesOfClass(LivingEntity.class,p.getBoundingBox().expandTowards(p.getLookAngle().scale(range)).inflate(1),e->e!=p)) {
            var hit=e.getBoundingBox().inflate(.15).clip(start,end);
            if(hit.isPresent()&&start.distanceToSqr(hit.get())<nearest){nearest=start.distanceToSqr(hit.get());found=e;}
        }
        String id,name;
        if(found!=null) {
            var key=BuiltInRegistries.ENTITY_TYPE.getKey(found.getType());id="entity:"+key;name=found.getName().getString();
            if(key.getPath().equals("slime")||key.getPath().equals("magma_cube"))d.state.discover("colony");
            if(key.getPath().equals("iron_golem"))d.state.discover("metal");
        } else if(block.getType()!=HitResult.Type.MISS) {
            var state=p.level().getBlockState(block.getBlockPos());var key=BuiltInRegistries.BLOCK.getKey(state.getBlock());
            id="block:"+key;name=state.getBlock().getName().getString();
            if(key.getPath().contains("iron")||key.getPath().contains("copper")||key.getPath().contains("gold"))d.state.discover("metal");
            if(key.getPath().equals("slime_block"))d.state.discover("colony");
        } else {TransformationManager.message(p,"Aponte para uma criatura ou bloco próximo.");return;}
        d.state.energy-=3;boolean fresh=d.state.discover(id);
        TransformationManager.message(p,(fresh?"Nova descoberta: ":"Registrado: ")+name+" • Lv "+d.state.level());
        CoreNetwork.syncOwner(p);
    }
    public static void radar(ServerPlayer p) {
        var d=GenesisCoreData.get(p);if(!d.state.installed||d.state.energy<5)return;
        d.state.energy-=5;
        var list=p.level().getEntitiesOfClass(LivingEntity.class,p.getBoundingBox().inflate(20),e->e!=p&&e.isAlive());
        for(var e:list.stream().limit(16).toList())p.serverLevel().sendParticles(p,ParticleTypes.END_ROD,true,e.getX(),e.getY()+e.getBbHeight(),e.getZ(),3,.1,.2,.1,0);
        TransformationManager.message(p,"Radar local: "+list.size()+" formas de vida.");CoreNetwork.syncOwner(p);
    }
}
