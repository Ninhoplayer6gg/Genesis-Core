package dev.genesis.client;
import com.mojang.blaze3d.platform.InputConstants;
import dev.genesis.GenesisCore;
import dev.genesis.client.gui.*;
import dev.genesis.network.CoreNetwork;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
@Mod.EventBusSubscriber(modid=GenesisCore.MOD_ID,value=Dist.CLIENT,bus=Mod.EventBusSubscriber.Bus.MOD)
public final class CoreKeys {
    public static final KeyMapping MENU=key("menu",GLFW.GLFW_KEY_R),DATABASE=key("database",GLFW.GLFW_KEY_B);
    public static final KeyMapping[] ABILITIES={key("ability1",GLFW.GLFW_KEY_Z),key("ability2",GLFW.GLFW_KEY_X),key("ability3",GLFW.GLFW_KEY_C),key("ultimate",GLFW.GLFW_KEY_V),key("movement",GLFW.GLFW_KEY_G)};
    private static KeyMapping key(String name,int key){return new KeyMapping("key.genesis."+name,KeyConflictContext.IN_GAME,InputConstants.Type.KEYSYM,key,"key.categories.genesis");}
    @SubscribeEvent public static void register(RegisterKeyMappingsEvent e){e.register(MENU);e.register(DATABASE);for(var k:ABILITIES)e.register(k);}
    @Mod.EventBusSubscriber(modid=GenesisCore.MOD_ID,value=Dist.CLIENT)
    public static class Input {
        @SubscribeEvent public static void tick(TickEvent.ClientTickEvent e) {
            if(e.phase!=TickEvent.Phase.END)return;var mc=Minecraft.getInstance();
            if(mc.player==null||mc.level==null)return;
            ClientState.tick();
            if(mc.screen!=null)return;
            if(MENU.consumeClick())mc.setScreen(new RadialScreen());
            if(DATABASE.consumeClick())mc.setScreen(new DatabaseScreen());
            for(int i=0;i<ABILITIES.length;i++)if(ABILITIES[i].consumeClick())CoreNetwork.input(1,i,false);
        }
        @SubscribeEvent public static void logout(net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut e){ClientState.clear();}
    }
}
