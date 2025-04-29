package com.finderfeed.fdbosses.content.entities.chesed_boss;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDFreeEntityRenderer;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.renderers.QuadRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ChesedRenderer implements FDFreeEntityRenderer<ChesedEntity> {

    @Override
    public void render(ChesedEntity chesedEntity, float yaw, float pticks, PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {

        this.renderRayEffect(chesedEntity, yaw, pticks, poseStack, multiBufferSource, light);

    }

    public static final ResourceLocation CHESED_RAY_PREPARE = FDBosses.location("textures/util/chesed_ray_prepare.png");

    private void renderRayEffect(ChesedEntity chesedEntity, float yaw, float pticks, PoseStack poseStack, MultiBufferSource multiBufferSource, int light){

        var system = chesedEntity.getSystem();

        var ticker = system.getTicker(ChesedEntity.RAY_ATTACK_LAYER);

        if (ticker != null){

            float elapsedTime = ticker.getTime(pticks);

            float offset = 10;
            float prepareTime = 27f;
            float offsetP = Mth.clamp((elapsedTime - offset) / prepareTime,0, 1f);

            float easedP = FDEasings.easeOut(offsetP);

            float rotation = 360 * FDEasings.easeOutBack(offsetP);
            float alpha = easedP;
            float directionOffset = 0.5f * (1 - easedP);
            float sizeMod = 0;

            if (elapsedTime - offset - prepareTime > 0){

                float awayTime = 7f;

                float offsetP2 = Math.clamp((elapsedTime - offset - prepareTime) / awayTime, 0, 1);

                float easedP2 = FDEasings.easeOut(offsetP2);

                alpha = 1 - offsetP2;
                directionOffset = 5 * (easedP2);
                sizeMod = offsetP2 * 2;

            }

            VertexConsumer consumer = multiBufferSource.getBuffer(RenderType.breezeEyes(CHESED_RAY_PREPARE));

            Vec3 dir = new Vec3(1,0,0).yRot((float)Math.toRadians(-yaw - 90));

            QuadRenderer.start(consumer)
                    .color(1,1,1,alpha)
                    .direction(dir)
                    .pose(poseStack)
                    .offsetOnDirection(2 - directionOffset)
                    .translate(0,1.5f,0)
                    .size(1.5f + sizeMod)
                    .rotationDegrees(rotation)
                    .render();

            QuadRenderer.start(consumer)
                    .color(1,1,1,alpha)
                    .direction(dir)
                    .pose(poseStack)
                    .offsetOnDirection(3 - directionOffset)
                    .translate(0,1.5f,0)
                    .size(0.75f + sizeMod)
                    .rotationDegrees(rotation)
                    .render();


        }




    }

}
