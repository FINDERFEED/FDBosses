package com.finderfeed.fdbosses.content.entities.geburah.geburah_opening_floor;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.renderers.QuadRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class GeburahOpeningFloorRenderer extends EntityRenderer<GeburahOpeningFloor> {

    public static final ResourceLocation STONE = FDBosses.location("textures/block/justicestone_bricks.png");

    public GeburahOpeningFloorRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public void render(GeburahOpeningFloor floor, float p_114486_, float pticks, PoseStack matrices, MultiBufferSource src, int light) {

        var vertex = src.getBuffer(RenderType.text(STONE));

        int start = 20;

        float time = floor.tickCount + pticks;
        float percentOpeness = Mth.clamp((time - start) / 80f,0,1);

        matrices.pushPose();

        for (int i = 0; i < 2; i++) {

            matrices.pushPose();

            int md = i > 0 ? -1 : 1;

            float t = time * 2f;
            matrices.mulPose(Axis.YP.rotationDegrees(i * 180));

            if (time > start) {
                float k = 0.02f * md;
                matrices.translate(Math.sin(t) * k, Math.sin(t + FDMathUtil.FPI) * k, Math.cos(t + FDMathUtil.FPI / 4) * k);
            }

            float openess = percentOpeness * 5.5f;

            Matrix4f mat = matrices.last().pose();

            float openess1 = 5.5f - openess;
            vertex.addVertex(mat, 2.5f, 0.25f, openess1 + openess).setColor(1f, 1f, 1f, 1f).setUv(0, md * openess1).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, 2.5f, 0.25f, openess).setColor(1f, 1f, 1f, 1f).setUv(0, 0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, -2.5f, 0.25f, openess).setColor(1f, 1f, 1f, 1f).setUv(5 * md, 0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, -2.5f, 0.25f, openess1 + openess).setColor(1f, 1f, 1f, 1f).setUv(5 * md, md * openess1).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);


            float openess2 = Mth.clamp(4.5f - openess, 0, 4.5f);
            float offs2 = 3f;
            vertex.addVertex(mat, 0.5f + offs2, 0.25f, openess2 + openess).setColor(1f, 1f, 1f, 1f).setUv(0, md * openess2).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, 0.5f + offs2, 0.25f, openess).setColor(1f, 1f, 1f, 1f).setUv(0, 0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, -0.5f + offs2, 0.25f, openess).setColor(1f, 1f, 1f, 1f).setUv(md, 0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, -0.5f + offs2, 0.25f, openess2 + openess).setColor(1f, 1f, 1f, 1f).setUv(md, md * openess2).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);

            vertex.addVertex(mat, 0.5f - offs2, 0.25f, openess2 + openess).setColor(1f, 1f, 1f, 1f).setUv(0, md * openess2).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, 0.5f - offs2, 0.25f, openess).setColor(1f, 1f, 1f, 1f).setUv(0, 0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, -0.5f - offs2, 0.25f, openess).setColor(1f, 1f, 1f, 1f).setUv(md, 0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, -0.5f - offs2, 0.25f, openess2 + openess).setColor(1f, 1f, 1f, 1f).setUv(md, md * openess2).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);


            float openess3 = Mth.clamp(3.5f - openess, 0, 3.5f);
            float offs3 = 4f;
            vertex.addVertex(mat, 0.5f + offs3, 0.25f, openess3 + openess).setColor(1f, 1f, 1f, 1f).setUv(0, md * openess3).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, 0.5f + offs3, 0.25f, openess).setColor(1f, 1f, 1f, 1f).setUv(0, 0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, -0.5f + offs3, 0.25f, openess).setColor(1f, 1f, 1f, 1f).setUv(md, 0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, -0.5f + offs3, 0.25f, openess3 + openess).setColor(1f, 1f, 1f, 1f).setUv(md, md * openess3).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);

            vertex.addVertex(mat, 0.5f - offs3, 0.25f, openess3 + openess).setColor(1f, 1f, 1f, 1f).setUv(0, md * openess3).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, 0.5f - offs3, 0.25f, openess).setColor(1f, 1f, 1f, 1f).setUv(0, 0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, -0.5f - offs3, 0.25f, openess).setColor(1f, 1f, 1f, 1f).setUv(md, 0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, -0.5f - offs3, 0.25f, openess3 + openess).setColor(1f, 1f, 1f, 1f).setUv(md, md * openess3).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);

            float openess4 = Mth.clamp(2.5f - openess, 0, 2.5f);
            float offs4 = 5f;
            vertex.addVertex(mat, 0.5f + offs4, 0.25f, openess4 + openess).setColor(1f, 1f, 1f, 1f).setUv(0, md * openess4).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, 0.5f + offs4, 0.25f, 0 + openess).setColor(1f, 1f, 1f, 1f).setUv(0, 0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, -0.5f + offs4, 0.25f, 0 + openess).setColor(1f, 1f, 1f, 1f).setUv(md, 0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, -0.5f + offs4, 0.25f, openess4 + openess).setColor(1f, 1f, 1f, 1f).setUv(md, md * openess4).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);

            vertex.addVertex(mat, 0.5f - offs4, 0.25f, openess4 + openess).setColor(1f, 1f, 1f, 1f).setUv(0, md * openess4).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, 0.5f - offs4, 0.25f, 0 + openess).setColor(1f, 1f, 1f, 1f).setUv(0, 0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, -0.5f - offs4, 0.25f, 0 + openess).setColor(1f, 1f, 1f, 1f).setUv(md, 0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
            vertex.addVertex(mat, -0.5f - offs4, 0.25f, openess4 + openess).setColor(1f, 1f, 1f, 1f).setUv(md, md * openess4).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);

            matrices.popPose();

        }


        matrices.popPose();

        VertexConsumer c = src.getBuffer(RenderType.text(FDBosses.location("textures/misc/black_dot.png")));

        QuadRenderer.start(c)
                .translate(0,0.125f,0)
                .pose(matrices)
                .sizeX(2.5f)
                .sizeY(5.5f)
                .render();

        QuadRenderer.start(c)
                .translate(3f,0.125f,0)
                .pose(matrices)
                .sizeX(0.5f)
                .sizeY(4.5f)
                .render();

        QuadRenderer.start(c)
                .translate(-3f,0.125f,0)
                .pose(matrices)
                .sizeX(0.5f)
                .sizeY(4.5f)
                .render();

        QuadRenderer.start(c)
                .translate(4f,0.125f,0)
                .pose(matrices)
                .sizeX(0.5f)
                .sizeY(3.5f)
                .render();

        QuadRenderer.start(c)
                .translate(-4f,0.125f,0)
                .pose(matrices)
                .sizeX(0.5f)
                .sizeY(3.5f)
                .render();

        QuadRenderer.start(c)
                .translate(5f,0.125f,0)
                .pose(matrices)
                .sizeX(0.5f)
                .sizeY(2.5f)
                .render();

        QuadRenderer.start(c)
                .translate(-5f,0.125f,0)
                .pose(matrices)
                .sizeX(0.5f)
                .sizeY(2.5f)
                .render();

    }

    @Override
    public ResourceLocation getTextureLocation(GeburahOpeningFloor p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public boolean shouldRender(GeburahOpeningFloor p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return true;
    }

}
