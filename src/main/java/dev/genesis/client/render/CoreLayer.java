package dev.genesis.client.render;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.genesis.GenesisCore;
import dev.genesis.client.ClientState;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.math.Axis;
public final class CoreLayer extends RenderLayer<AbstractClientPlayer,PlayerModel<AbstractClientPlayer>> {
    public static final ModelLayerLocation LAYER=new ModelLayerLocation(GenesisCore.id("chest_core"),"main");
    private final ModelPart model;
    public CoreLayer(RenderLayerParent<AbstractClientPlayer,PlayerModel<AbstractClientPlayer>> parent,ModelPart model){super(parent);this.model=model;}
    public static LayerDefinition create(){
        MeshDefinition mesh=new MeshDefinition();var root=mesh.getRoot();
        root.addOrReplaceChild("base",CubeListBuilder.create().texOffs(0,0).addBox(-3,-3,-1,6,6,2),PartPose.ZERO);
        root.addOrReplaceChild("jewel",CubeListBuilder.create().texOffs(20,0).addBox(-1.3f,-1.3f,-2,2.6f,2.6f,1),PartPose.rotation(0,0,(float)Math.PI/4));
        for(int i=0;i<3;i++)root.addOrReplaceChild("prong"+i,CubeListBuilder.create().texOffs(0,12).addBox(-1,-5,-1.5f,2,3,2),PartPose.rotation(0,0,(float)(i*Math.PI*2/3)));
        return LayerDefinition.create(mesh,32,32);
    }
    @Override public void render(PoseStack ps,MultiBufferSource buffers,int light,AbstractClientPlayer p,float swing,float amount,float pt,float age,float yaw,float pitch){
        var d=ClientState.data(p.getUUID());if(!d.state.installed||p.isInvisible())return;
        ps.pushPose();getParentModel().body.translateAndRotate(ps);ps.translate(0,.4,-.20);
        ps.mulPose(Axis.ZP.rotationDegrees((p.tickCount+pt)*(d.shieldTicks>0?8:.6f)));
        int c=d.state.preset;float r=c==1?.5f:1,g=c==2?.6f:1,b=c==0?1:1;
        model.render(ps,buffers.getBuffer(RenderType.entityCutoutNoCull(GenesisCore.id("textures/entity/core.png"))),light,OverlayTexture.NO_OVERLAY,r,g,b,1);ps.popPose();
    }
}
