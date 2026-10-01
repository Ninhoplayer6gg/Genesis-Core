package dev.genesis.network;
import dev.genesis.GenesisCore;
import dev.genesis.abilities.AbilityManager;
import dev.genesis.core.GenesisCoreData;
import dev.genesis.database.ScannerManager;
import dev.genesis.species.SpeciesRegistry;
import dev.genesis.transformation.TransformationManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
public final class CoreNetwork {
    private static final String PROTOCOL="1";
    private static final SimpleChannel CHANNEL=NetworkRegistry.newSimpleChannel(GenesisCore.id("core"),()->PROTOCOL,PROTOCOL::equals,PROTOCOL::equals);
    public record Input(int action,int value,boolean absolute) {
        static Input decode(FriendlyByteBuf b){return new Input(b.readUnsignedByte(),b.readInt(),b.readBoolean());}
        void encode(FriendlyByteBuf b){b.writeByte(action);b.writeInt(value);b.writeBoolean(absolute);}
        void handle(Supplier<NetworkEvent.Context> supplier) {
            var c=supplier.get();c.enqueueWork(()->{
                ServerPlayer p=c.getSender();if(p==null||!p.isAlive()||p.isSpectator())return;
                var d=GenesisCoreData.get(p);if(d.packetTokens<=0||!d.state.installed)return;d.packetTokens--;
                switch(action) {
                    case 0 -> {if(value==-1)TransformationManager.revert(p);else {var list=SpeciesRegistry.playable();if(value>=0&&value<list.size())TransformationManager.transform(p,list.get(value).id(),absolute);}}
                    case 1 -> {if(value>=0&&value<5)AbilityManager.request(p,value);}
                    case 2 -> ScannerManager.scan(p);
                    case 3 -> {if(value>=0&&value<3){d.state.preset=value;sync(p);}}
                    case 4 -> {if(d.scanCooldown==0){ScannerManager.radar(p);d.scanCooldown=80;}}
                    default -> { }
                }
            });c.setPacketHandled(true);
        }
    }
    public record Sync(UUID player,CompoundTag data) {
        static Sync decode(FriendlyByteBuf b){return new Sync(b.readUUID(),b.readNbt());}
        void encode(FriendlyByteBuf b){b.writeUUID(player);b.writeNbt(data);}
        void handle(Supplier<NetworkEvent.Context> supplier){var c=supplier.get();c.enqueueWork(()->DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->dev.genesis.client.ClientState.accept(player,data)));c.setPacketHandled(true);}
    }
    public static void register() {
        CHANNEL.registerMessage(0,Input.class,Input::encode,Input::decode,Input::handle,Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(1,Sync.class,Sync::encode,Sync::decode,Sync::handle,Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
    public static void input(int action,int value,boolean absolute){CHANNEL.sendToServer(new Input(action,value,absolute));}
    public static void sync(ServerPlayer p) {syncOwner(p);CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(()->p),new Sync(p.getUUID(),GenesisCoreData.get(p).visualSync()));}
    public static void syncVitals(ServerPlayer p){CHANNEL.send(PacketDistributor.PLAYER.with(()->p),new Sync(p.getUUID(),GenesisCoreData.get(p).vitals()));}
    public static void syncOwner(ServerPlayer p){CHANNEL.send(PacketDistributor.PLAYER.with(()->p),new Sync(p.getUUID(),GenesisCoreData.get(p).sync()));GenesisCoreData.get(p).dirty=false;}
    public static void tracking(ServerPlayer observer,ServerPlayer target){CHANNEL.send(PacketDistributor.PLAYER.with(()->observer),new Sync(target.getUUID(),GenesisCoreData.get(target).visualSync()));}
}
