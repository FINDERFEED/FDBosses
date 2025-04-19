package com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_one_shot_vertical_ray;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class ChesedOneShotVerticalRayRenderer extends EntityRenderer<ChesedOneShotVerticalRayEntity> {

    public ChesedOneShotVerticalRayRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public void render(ChesedOneShotVerticalRayEntity entity, float yaw, float partialTicks, PoseStack matrices, MultiBufferSource src, int light) {

        matrices.pushPose();

        VertexConsumer vertexConsumer = src.getBuffer(RenderType.eyes(FDBosses.location("textures/util/circle_and_crosshair.png")));


        float time = entity.tickCount + partialTicks;

        float p = time / entity.getAttackPreparationTime();



        matrices.mulPose(Axis.YP.rotationDegrees(FDEasings.easeOut(p) * 1080));

        p = FDEasings.easeOut(p);
        matrices.scale(p*2,p*2,p*2);

        Matrix4f mt = matrices.last().pose();
        vertexConsumer.addVertex(mt, -0.5f,0.05f,0.5f).setColor(0.2f,0.9f,1f,1f).setUv(0,.5f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0,1,0);
        vertexConsumer.addVertex(mt, 0.5f,0.05f,0.5f).setColor(0.2f,0.9f,1f,1f).setUv(1,.5f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0,1,0);
        vertexConsumer.addVertex(mt, 0.5f,0.05f,-0.5f).setColor(0.2f,0.9f,1f,1f).setUv(1,0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0,1,0);
        vertexConsumer.addVertex(mt, -0.5f,0.05f,-0.5f).setColor(0.2f,0.9f,1f,1f).setUv(0,0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0,1,0);


        vertexConsumer.addVertex(mt, -0.5f,0.05f,0.5f).setColor(0.2f,0.9f,1f,1f).setUv(0,1f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0,1,0);
        vertexConsumer.addVertex(mt, 0.5f,0.05f,0.5f).setColor(0.2f,0.9f,1f,1f).setUv(1,1f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0,1,0);
        vertexConsumer.addVertex(mt, 0.5f,0.05f,-0.5f).setColor(0.2f,0.9f,1f,1f).setUv(1,0.5f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0,1,0);
        vertexConsumer.addVertex(mt, -0.5f,0.05f,-0.5f).setColor(0.2f,0.9f,1f,1f).setUv(0,0.5f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0,1,0);

        matrices.popPose();

    }

    @Override
    public ResourceLocation getTextureLocation(ChesedOneShotVerticalRayEntity p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
