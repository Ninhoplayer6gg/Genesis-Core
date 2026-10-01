package dev.genesis.testing;
import com.mojang.authlib.GameProfile;
import dev.genesis.core.*;
import dev.genesis.registry.CoreRegistry;
import dev.genesis.transformation.TransformationManager;
import dev.genesis.palladium.PalladiumPowerBridge;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.*;
import java.util.UUID;
@GameTestHolder("genesis")
@PrefixGameTestTemplate(false)
public final class GenesisGameTests {
    @GameTest(template="empty",timeoutTicks=200)
    public static void installation_persists_nbt(GameTestHelper h){
        var p=FakePlayerFactory.get(h.getLevel(),new GameProfile(UUID.randomUUID(),"GenesisNBT"));
        p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(CoreRegistry.CORE.get()));
        CoreRegistry.CORE.get().use(h.getLevel(),p,InteractionHand.MAIN_HAND);
        var data=GenesisCoreData.get(p);h.assertTrue(data.state.installed,"Core item did not install");
        data.state.addXp(250);data.state.discover("metal");data.state.cooldowns[0]=80;
        var restored=new GenesisCoreData();restored.load(data.save());
        h.assertTrue(restored.state.installed&&restored.state.xp==data.state.xp&&restored.state.unlocked.contains("ferronox")&&restored.state.cooldowns[0]==80,"NBT persistence lost data");
        var vanillaSave=p.saveWithoutId(new net.minecraft.nbt.CompoundTag());
        var reloaded=FakePlayerFactory.get(h.getLevel(),new GameProfile(UUID.randomUUID(),"GenesisReload"));
        reloaded.load(vanillaSave);
        h.assertTrue(GenesisCoreData.get(reloaded).state.installed&&GenesisCoreData.get(reloaded).state.xp==data.state.xp,"Forge capability missing from player save");h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=200)
    public static void power_sets_loaded(GameTestHelper h){
        var p=FakePlayerFactory.get(h.getLevel(),new GameProfile(UUID.randomUUID(),"GenesisPowers"));
        for(String id:new String[]{"adaptaris","ferronox","colonyx","adaptaris_absolute"})h.assertTrue(PalladiumPowerBridge.exists(p,"genesis:"+id),"Missing power "+id);
        h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=200)
    public static void switching_restores_attributes(GameTestHelper h){
        var p=FakePlayerFactory.get(h.getLevel(),new GameProfile(UUID.randomUUID(),"GenesisSwitch"));
        var pos=h.absolutePos(new net.minecraft.core.BlockPos(3,2,3));p.setPos(pos.getX()+.5,pos.getY(),pos.getZ()+.5);
        var d=GenesisCoreData.get(p);d.state.install();d.state.addXp(250);d.state.discover("metal");
        double armor=p.getAttributeValue(Attributes.ARMOR);
        TransformationManager.transform(p,"adaptaris",false);
        h.assertTrue(d.state.form.equals("adaptaris"),"Adaptaris failed");
        h.runAfterDelay(5,()->{PalladiumPowerBridge.tickForValidation(p);h.assertTrue(p.getAttributeValue(Attributes.ARMOR)>armor,"Palladium attributes not applied");
            d.state.abilityLock=0;dev.genesis.abilities.AbilityManager.request(p,1);
            h.assertTrue(d.shieldTicks>0&&d.state.cooldowns[1]>0,"Shield ability did not execute");
            double energy=d.state.energy;dev.genesis.abilities.AbilityManager.request(p,1);
            h.assertTrue(d.state.energy==energy,"Cooldown allowed duplicate cast");
            TransformationManager.transform(p,"ferronox",false);
            h.assertTrue(d.state.form.equals("adaptaris"),"Transformation cooldown bypassed");
            d.state.transformationCooldown=0;TransformationManager.transform(p,"ferronox",false);
            PalladiumPowerBridge.tickForValidation(p);
            h.assertTrue(d.state.form.equals("ferronox"),"Switch failed");
            TransformationManager.revert(p);PalladiumPowerBridge.tickForValidation(p);
            h.assertTrue(!d.state.transformed(),"Human return failed");
            h.assertTrue(p.getAttributeValue(Attributes.ARMOR)==armor,"Armor leaked after revert");h.succeed();});
    }
    @GameTest(template="empty",timeoutTicks=200)
    public static void death_clone_preserves_core(GameTestHelper h){
        var old=FakePlayerFactory.get(h.getLevel(),new GameProfile(UUID.randomUUID(),"GenesisBefore"));
        var next=FakePlayerFactory.get(h.getLevel(),new GameProfile(UUID.randomUUID(),"GenesisAfter"));
        var d=GenesisCoreData.get(old);d.state.install();d.state.addXp(1000);d.state.discover("colony");
        d.state.absoluteUnlocked=true;d.state.preset=2;d.state.cooldowns[1]=90;
        CoreEvents.clone(new net.minecraftforge.event.entity.player.PlayerEvent.Clone(next,old,true));
        var copy=GenesisCoreData.get(next).state;
        h.assertTrue(copy.installed&&copy.xp==d.state.xp&&copy.unlocked.contains("colonyx")&&copy.absoluteUnlocked&&copy.preset==2&&copy.cooldowns[1]==90,"Death clone lost persistent state");h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=200)
    public static void zero_energy_reverts(GameTestHelper h){
        var p=FakePlayerFactory.get(h.getLevel(),new GameProfile(UUID.randomUUID(),"GenesisEnergy"));
        var pos=h.absolutePos(new net.minecraft.core.BlockPos(3,2,3));p.setPos(pos.getX()+.5,pos.getY(),pos.getZ()+.5);
        var d=GenesisCoreData.get(p);d.state.install();TransformationManager.transform(p,"adaptaris",false);
        h.assertTrue(d.state.transformed(),"Initial transformation failed");d.state.energy=0;p.tickCount=20;
        CoreEvents.tick(new net.minecraftforge.event.TickEvent.PlayerTickEvent(net.minecraftforge.event.TickEvent.Phase.END,p));
        h.assertTrue(!d.state.transformed()&&d.state.installed&&d.state.transformationCooldown>0,"Exhaustion did not restore human state");h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=200)
    public static void movement_ability_uses_jump_animation(GameTestHelper h){
        var p=FakePlayerFactory.get(h.getLevel(),new GameProfile(UUID.randomUUID(),"GenesisMovement"));
        var pos=h.absolutePos(new net.minecraft.core.BlockPos(3,2,3));p.setPos(pos.getX()+.5,pos.getY(),pos.getZ()+.5);
        var d=GenesisCoreData.get(p);d.state.install();d.state.addXp(250);d.state.discover("metal");d.state.discover("colony");
        TransformationManager.transform(p,"colonyx",false);PalladiumPowerBridge.tickForValidation(p);d.state.abilityLock=0;
        dev.genesis.abilities.AbilityManager.request(p,4);
        h.assertTrue(d.movementTicks>0&&d.state.cooldowns[4]>0&&d.animation==7,"Movement ability did not activate with its jump animation");h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=200)
    public static void ferronox_projectile_and_ammo(GameTestHelper h){
        var p=FakePlayerFactory.get(h.getLevel(),new GameProfile(UUID.randomUUID(),"GenesisShot"));
        var pos=h.absolutePos(new net.minecraft.core.BlockPos(3,2,3));p.setPos(pos.getX()+.5,pos.getY(),pos.getZ()+.5);
        var d=GenesisCoreData.get(p);d.state.install();d.state.addXp(250);d.state.discover("metal");
        p.getInventory().add(new ItemStack(net.minecraft.world.item.Items.IRON_NUGGET,2));
        TransformationManager.transform(p,"ferronox",false);PalladiumPowerBridge.tickForValidation(p);d.state.abilityLock=0;
        dev.genesis.abilities.AbilityManager.request(p,0);
        var shots=h.getLevel().getEntities(p,p.getBoundingBox().inflate(12),e->e instanceof net.minecraft.world.entity.projectile.Projectile shot&&shot.getOwner()==p);
        h.assertTrue(shots.size()==1,"Palladium projectile was not spawned");
        h.assertTrue(p.getInventory().countItem(net.minecraft.world.item.Items.IRON_NUGGET)==1,"Ammunition not consumed exactly once");
        var visual=shots.get(0).saveWithoutId(new net.minecraft.nbt.CompoundTag());
        h.assertTrue(visual.getList("Appearances",10).toString().contains("minecraft:iron_nugget"),"Projectile item appearance was not preserved");h.succeed();
    }
}
