package dev.genesis.client.render;
import dev.genesis.GenesisCore;
import dev.genesis.client.ClientState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
public final class SpeciesGeoModel extends GeoModel<SpeciesAnimatable> {
    @Override public ResourceLocation getModelResource(SpeciesAnimatable a){return GenesisCore.id("geo/"+a.species+".geo.json");}
    @Override public ResourceLocation getTextureResource(SpeciesAnimatable a){return GenesisCore.id("textures/entity/"+a.species+".png");}
    @Override public ResourceLocation getAnimationResource(SpeciesAnimatable a){return GenesisCore.id("animations/"+a.species+".animation.json");}
    @Override public void setCustomAnimations(SpeciesAnimatable a,long id,AnimationState<SpeciesAnimatable> state) {
        super.setCustomAnimations(a,id,state);
        var entity=state.getData(DataTickets.ENTITY);if(!(entity instanceof Player p))return;
        var d=ClientState.data(p.getUUID());
        for(String type:new String[]{"thermal","cold","electric","impact"})getBone(type).ifPresent(b->b.setHidden(d.state.adaptations.getOrDefault(type,0d)<25));
        getBone("absolute").ifPresent(b->b.setHidden(!d.state.absolute));
        getBone("shield").ifPresent(b->b.setHidden(d.shieldTicks==0));
        getBone("bloom").ifPresent(b->b.setHidden(d.bloomTicks==0));
        getBone("blade").ifPresent(b->b.setHidden(d.animationTicks==0&&d.bloomTicks==0));
        getBone("head").ifPresent(b->b.setRotX(p.getXRot()*(float)Math.PI/180*.5f));
        getBone("core_ring").ifPresent(b->b.setRotZ((p.tickCount+state.getPartialTick())*.035f));
    }
}
