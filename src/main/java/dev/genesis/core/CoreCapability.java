package dev.genesis.core;
import dev.genesis.GenesisCore;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
@Mod.EventBusSubscriber(modid=GenesisCore.MOD_ID)
public final class CoreCapability implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<GenesisCoreData> CAP=CapabilityManager.get(new CapabilityToken<>(){});
    private final GenesisCoreData data=new GenesisCoreData();
    private final LazyOptional<GenesisCoreData> optional=LazyOptional.of(()->data);
    @Override public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap,@Nullable Direction side) {return cap==CAP?optional.cast():LazyOptional.empty();}
    @Override public CompoundTag serializeNBT(){return data.save();}
    @Override public void deserializeNBT(CompoundTag nbt){data.load(nbt);}
    @SubscribeEvent public static void attach(AttachCapabilitiesEvent<Entity> e) {
        if(e.getObject() instanceof Player) {var provider=new CoreCapability();e.addCapability(GenesisCore.id("core"),provider);e.addListener(provider.optional::invalidate);}
    }
}
