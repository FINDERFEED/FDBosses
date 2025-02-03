package com.finderfeed.fdbosses.entities.chesed_boss.kinetic_field;

import com.finderfeed.fdbosses.client.particles.arc_lightning.ArcLightningParticle;
import com.finderfeed.fdbosses.client.util.BossRenderTypes;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDFreeEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.List;

public class ChesedKineticFieldRenderer {

    public static void render(Entity entity, float v, float pticks, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {

        ChesedKineticFieldEntity e = (ChesedKineticFieldEntity) entity;

        double radius = e.getSquareRadius();
        VertexConsumer vertex = multiBufferSource.getBuffer(BossRenderTypes.LIGHTNING_NO_CULL);

        int breakCount = (int) Math.max(2,Math.ceil(radius * 1.5));


        float alpha = entity.tickCount > 3 ? 1 : 0;

        List<Vec3> positions = List.of(
                new Vec3(-radius,1.5,0),
                new Vec3(radius,1.5,0)
        );

        for (int g = 0; g < 4;g++) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(90 * g));
            poseStack.translate(0,0,radius);
            Matrix4f t = poseStack.last().pose();

            int seed = 2345 * e.getId() + g * 543;

            ArcLightningParticle.fullLightningDraw(e.level().getGameTime(), seed, breakCount,t,vertex,positions,0.25f,0.8f,0.3f, 1f, 1f,alpha);

            poseStack.translate(0,0,0.05);
            ArcLightningParticle.fullLightningDraw(e.level().getGameTime(), seed, breakCount,t,vertex,positions,0.05f,0.8f,1f, 1f, 1f,alpha);

            poseStack.translate(0,0,-0.1);
            ArcLightningParticle.fullLightningDraw(e.level().getGameTime(), seed, breakCount,t,vertex,positions,0.05f,0.8f,1f, 1f, 1f,alpha);

            poseStack.popPose();
        }
    }
}
