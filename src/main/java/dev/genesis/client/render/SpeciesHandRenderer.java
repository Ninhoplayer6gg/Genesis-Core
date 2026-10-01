package dev.genesis.client.render;

import dev.genesis.GenesisCore;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.client.event.RenderArmEvent;
import java.util.HashMap;
import java.util.Map;

/** Uses the same 128px texture atlas as the full species model. */
public final class SpeciesHandRenderer {
    private static final Map<String, ModelPart> MODELS = new HashMap<>();

    public static void render(RenderArmEvent event, String species) {
        boolean right = event.getArm() == HumanoidArm.RIGHT;
        var root = MODELS.computeIfAbsent(species, SpeciesHandRenderer::create);
        root.getChild(right ? "right" : "left").render(event.getPoseStack(),
            event.getMultiBufferSource().getBuffer(RenderType.entityCutoutNoCull(
                GenesisCore.id("textures/entity/" + species + ".png"))),
            event.getPackedLight(), OverlayTexture.NO_OVERLAY);
    }

    private static ModelPart create(String species) {
        var mesh = new MeshDefinition();
        for (boolean right : new boolean[]{true, false}) {
            float x = right ? -3 : -1;
            CubeListBuilder cubes = CubeListBuilder.create().texOffs(0, 64);
            if (species.equals("colonyx")) {
                cubes.texOffs(64, 64).addBox(x + 1, -2, -1, 2, 11, 2)
                    .texOffs(0, 96).addBox(x, 7, -3, 4, 3, 4);
            } else {
                cubes.addBox(x, -2, -2, 4, 12, 4)
                    .texOffs(0, 96).addBox(x - .25f, 2, -2.75f, 4.5f, 5, 1);
            }
            mesh.getRoot().addOrReplaceChild(right ? "right" : "left", cubes,
                PartPose.offset(right ? -5 : 5, 2, 0));
        }
        return LayerDefinition.create(mesh, 128, 128).bakeRoot();
    }
}
