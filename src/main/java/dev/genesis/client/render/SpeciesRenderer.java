package dev.genesis.client.render;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;
public final class SpeciesRenderer extends GeoReplacedEntityRenderer<AbstractClientPlayer,SpeciesAnimatable> {
    public SpeciesRenderer(EntityRendererProvider.Context context,String species){super(context,new SpeciesGeoModel(),new SpeciesAnimatable(species));this.shadowRadius=.55f;addRenderLayer(new software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer<>(this));}
}
