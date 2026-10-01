package dev.genesis.client.render;
import dev.genesis.GenesisCore;
import dev.genesis.client.ClientState;
import dev.genesis.client.gui.RadialScreen;
import dev.genesis.species.SpeciesRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.*;
@Mod.EventBusSubscriber(modid=GenesisCore.MOD_ID,value=Dist.CLIENT,bus=Mod.EventBusSubscriber.Bus.MOD)
public final class ClientRenderEvents {
    private static final Map<String,SpeciesRenderer> RENDERERS=new HashMap<>();
    @SubscribeEvent public static void layerDefs(EntityRenderersEvent.RegisterLayerDefinitions e){e.registerLayerDefinition(CoreLayer.LAYER,CoreLayer::create);}
    @SubscribeEvent public static void layers(EntityRenderersEvent.AddLayers e){
        RENDERERS.clear();for(var s:SpeciesRegistry.playable())RENDERERS.put(s.id(),new SpeciesRenderer(e.getContext(),s.id()));
        for(String skin:e.getSkins()){PlayerRenderer r=e.getSkin(skin);if(r!=null)r.addLayer(new CoreLayer(r,e.getEntityModels().bakeLayer(CoreLayer.LAYER)));}
    }
    @Mod.EventBusSubscriber(modid=GenesisCore.MOD_ID,value=Dist.CLIENT)
    public static class Rendering {
        @SubscribeEvent public static void hand(RenderArmEvent e){
            if(e.getPlayer().isInvisible())return;
            String species=ClientState.data(e.getPlayer().getUUID()).state.form;
            if(!RENDERERS.containsKey(species))return;
            e.setCanceled(true);SpeciesHandRenderer.render(e,species);
        }
        @SubscribeEvent public static void player(RenderPlayerEvent.Pre e){
            if(!(e.getEntity() instanceof net.minecraft.client.player.AbstractClientPlayer player))return;
            var d=ClientState.data(e.getEntity().getUUID());var renderer=RENDERERS.get(d.state.form);if(renderer==null)return;
            e.setCanceled(true);e.getPoseStack().pushPose();if(d.state.absolute)e.getPoseStack().scale(1.1f,1.1f,1.1f);
            renderer.render(player,player.getYRot(),e.getPartialTick(),e.getPoseStack(),e.getMultiBufferSource(),e.getPackedLight());e.getPoseStack().popPose();
        }
        @SubscribeEvent public static void focus(ViewportEvent.ComputeFov e){if(Minecraft.getInstance().screen instanceof RadialScreen)e.setFOV(e.getFOV()*.86);}
    }
}
