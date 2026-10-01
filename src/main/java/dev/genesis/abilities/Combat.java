package dev.genesis.abilities;
import dev.genesis.config.CoreConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import java.util.List;
public final class Combat {
    public static List<LivingEntity> targets(ServerPlayer p,double range) {
        return p.level().getEntitiesOfClass(LivingEntity.class,p.getBoundingBox().inflate(range),e->e!=p&&e.isAlive()&&!e.isAlliedTo(p)&&p.hasLineOfSight(e)&&e.distanceToSqr(p)<=range*range&&(!(e instanceof Player q)||(CoreConfig.PVP.get()&&p.canHarmPlayer(q))));
    }
    public static void strike(ServerPlayer p,double range,float damage,boolean cone,double push) {
        for(var e:targets(p,range)) {
            Vec3 delta=e.position().subtract(p.position());
            if(cone&&delta.normalize().dot(p.getLookAngle())<.35)continue;
            e.hurt(p.damageSources().playerAttack(p),damage);
            Vec3 n=delta.normalize().scale(push);e.push(n.x,.2,n.z);e.hurtMarked=true;
        }
    }
}
