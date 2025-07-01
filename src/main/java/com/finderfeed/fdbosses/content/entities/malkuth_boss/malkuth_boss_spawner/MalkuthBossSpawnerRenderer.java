package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_boss_spawner;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDFreeEntityRenderer;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class MalkuthBossSpawnerRenderer implements FDFreeEntityRenderer<MalkuthBossSpawner> {

    public static final ResourceLocation HELL_BASE = FDBosses.location("textures/skyboxes/hellscape/hell_base.png");
    public static final ResourceLocation HELL_LIGHT = FDBosses.location("textures/skyboxes/hellscape/hell_light.png");

    @Override
    public void render(MalkuthBossSpawner malkuthBossSpawner, float v, float pticks, PoseStack matrices, MultiBufferSource src, int light) {

        //back    *empty*
        //front   left
        //right   top
        //bottom  *empty*

        matrices.pushPose();

        //skybox
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        Vec3 entityPos = malkuthBossSpawner.getPosition(pticks);

        Vec3 between = cameraPos.subtract(entityPos);

        matrices.translate(between.x,between.y,between.z);

        VertexConsumer vertexConsumer = src.getBuffer(RenderType.text(HELL_BASE));

        Matrix4f m = matrices.last().pose();

        float boxRadius = 100;

        //back (z is back)
        vertexConsumer.addVertex(m, -boxRadius,-boxRadius,-boxRadius).setColor(1,1,1,1f).setUv(0,0).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, boxRadius,-boxRadius,-boxRadius).setColor(1,1,1,1f).setUv(0.5f,0).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, boxRadius,boxRadius,-boxRadius).setColor(1,1,1,1f).setUv(0.5f,0.25f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, -boxRadius,boxRadius,-boxRadius).setColor(1,1,1,1f).setUv(0,0.25f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);


        //front
        vertexConsumer.addVertex(m, -boxRadius,boxRadius,boxRadius).setColor(1,1,1,1f).setUv(0.5f,0.5f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, boxRadius,boxRadius,boxRadius).setColor(1,1,1,1f).setUv(0.0f,0.5f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, boxRadius,-boxRadius,boxRadius).setColor(1,1,1,1f).setUv(0.0f,0.25f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, -boxRadius,-boxRadius,boxRadius).setColor(1,1,1,1f).setUv(0.5f,0.25f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);

        //left
        vertexConsumer.addVertex(m, boxRadius,-boxRadius,-boxRadius).setColor(1,1,1,1f).setUv(0.5f,0.25f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, boxRadius,-boxRadius,boxRadius).setColor(1,1,1,1f).setUv(1f,0.25f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, boxRadius,boxRadius,boxRadius).setColor(1,1,1,1f).setUv(1f,0.5f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, boxRadius,boxRadius,-boxRadius).setColor(1,1,1,1f).setUv(0.5f,0.5f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);


        //right
        vertexConsumer.addVertex(m, -boxRadius,boxRadius,-boxRadius).setColor(1,1,1,1f).setUv(0.5f,0.75f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, -boxRadius,boxRadius,boxRadius).setColor(1,1,1,1f).setUv(0f,0.75f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, -boxRadius,-boxRadius,boxRadius).setColor(1,1,1,1f).setUv(0f,0.5f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, -boxRadius,-boxRadius,-boxRadius).setColor(1,1,1,1f).setUv(0.5f,0.5f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);


        //down (but for some reason its up)
        vertexConsumer.addVertex(m, boxRadius,boxRadius,-boxRadius).setColor(1,1,1,1f).setUv(0f,1f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, boxRadius,boxRadius,boxRadius).setColor(1,1,1,1f).setUv(0f,.75f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, -boxRadius,boxRadius,boxRadius).setColor(1,1,1,1f).setUv(0.5f,.75f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, -boxRadius,boxRadius,-boxRadius).setColor(1,1,1,1f).setUv(0.5f,1f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);

        //up (but for some reason its down
        vertexConsumer.addVertex(m, -boxRadius,-boxRadius,-boxRadius).setColor(1,1,1,1f).setUv(1f,.5f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, -boxRadius,-boxRadius,boxRadius).setColor(1,1,1,1f).setUv(1f,.75f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, boxRadius,-boxRadius,boxRadius).setColor(1,1,1,1f).setUv(0.5f,.75f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
        vertexConsumer.addVertex(m, boxRadius,-boxRadius,-boxRadius).setColor(1,1,1,1f).setUv(.5f,.5f).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);


        matrices.popPose();
    }




}
