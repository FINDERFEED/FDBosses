package com.finderfeed.fdbosses.content.entities.geburah.chain_trap;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class GeburahChainTrapRenderer extends EntityRenderer<GeburahChainTrapEntity> {

    public static ResourceLocation CHAIN = FDBosses.location("textures/entities/geburah/geburah_chain.png");
    public static ResourceLocation CHAIN_TRAP = FDBosses.location("textures/entities/geburah/geburah_chain_trap.png");

    public GeburahChainTrapRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public void render(GeburahChainTrapEntity trap, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {
        matrices.pushPose();

        QuadRenderer.start(src.getBuffer(RenderType.text(CHAIN_TRAP)))
                .renderBack()
                .size(0.5f)
                .translate(0,0.01f,0)
                .rotationDegrees((trap.tickCount + pticks) / 2f)
                .pose(matrices)
                .render();

        this.renderChainCircle(trap,yaw,pticks,matrices,src,light);

        this.renderCatching(trap,yaw,pticks,matrices,src,light);

        matrices.popPose();
    }

    public void renderCatching(GeburahChainTrapEntity trap, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light){


        float catchingChainsPercent = -1;
        float restrictingChainsPercent = -1;

        Vec3 center = null;

        float squareRadius = -1;

        if (!trap.getPassengers().isEmpty()){

            Entity entity = trap.getPassengers().get(0);

            squareRadius = entity.getBbWidth();

            center = entity.getPosition(pticks).add(0,entity.getBbHeight()/2,0);

            catchingChainsPercent = 1;

            restrictingChainsPercent = 1 - Mth.clamp((trap.pullingTime - pticks) / GeburahChainTrapEntity.PULL_TIME,0,1);

        }else{

            LivingEntity entity;

            if ((entity = trap.getEntityAboutToTrap()) != null){
                squareRadius = entity.getBbWidth();
                center = entity.getPosition(pticks).add(0,entity.getBbHeight()/2,0);

                catchingChainsPercent = 1 - Mth.clamp((trap.catchingTime - pticks) / GeburahChainTrapEntity.PULL_TIME,0,1);

            }

        }

        if (center == null) return;

        if (catchingChainsPercent != -1){

            float catchingChainOffset = 0.425f;

            catchingChainsPercent = FDEasings.easeOut(catchingChainsPercent);

            this.renderChainFromPosToPos(trap, matrices, src,
                    trap.position().add(-catchingChainOffset,0,-catchingChainOffset),
                    center.add(-squareRadius/2,0,-squareRadius/2),
                    catchingChainsPercent
            );

            this.renderChainFromPosToPos(trap, matrices, src,
                    trap.position().add(catchingChainOffset,0,catchingChainOffset),
                    center.add(squareRadius/2,0,squareRadius/2),
                    catchingChainsPercent
            );

            this.renderChainFromPosToPos(trap, matrices, src,
                    trap.position().add(catchingChainOffset,0,-catchingChainOffset),
                    center.add(squareRadius/2,0,-squareRadius/2),
                    catchingChainsPercent
            );

            this.renderChainFromPosToPos(trap, matrices, src,
                    trap.position().add(-catchingChainOffset,0,catchingChainOffset),
                    center.add(-squareRadius/2,0,squareRadius/2),
                    catchingChainsPercent
            );

        }

        if (restrictingChainsPercent != -1){



            this.renderChainFromPosToPos(trap, matrices, src,
                    center.add(-squareRadius/2,0,squareRadius/2),
                    center.add(-squareRadius/2,0,-squareRadius/2),
                    restrictingChainsPercent
            );

            this.renderChainFromPosToPos(trap, matrices, src,
                    center.add(squareRadius/2,0,-squareRadius/2),
                    center.add(squareRadius/2,0,squareRadius/2),
                    restrictingChainsPercent
            );

            this.renderChainFromPosToPos(trap, matrices, src,
                    center.add(-squareRadius/2,0,-squareRadius/2),
                    center.add(squareRadius/2,0,-squareRadius/2),
                    restrictingChainsPercent
            );

            this.renderChainFromPosToPos(trap, matrices, src,
                    center.add(squareRadius/2,0,squareRadius/2),
                    center.add(-squareRadius/2,0,squareRadius/2),
                    restrictingChainsPercent
            );

        }

    }

    public void renderChainFromPosToPos(GeburahChainTrapEntity trap, PoseStack matrices, MultiBufferSource src, Vec3 pos1, Vec3 pos2, float p){

        Vec3 offset1 = pos1.subtract(trap.position());
        Vec3 between = pos2.subtract(pos1);

        VertexConsumer vertex = src.getBuffer(RenderType.text(CHAIN));

        matrices.pushPose();

        float length = (float) between.length() * p;

        float size = 0.2f;
        float xSize = size * 0.6f / 2;

        matrices.translate(offset1.x,offset1.y,offset1.z);

        FDRenderUtil.applyMovementMatrixRotations(matrices, between);

        float v = length / size;

        Matrix4f m = matrices.last().pose();

        matrices.mulPose(Axis.YP.rotationDegrees(90));
        m = matrices.last().pose();

        vertex.vertex(m,xSize,0,0).color(1f,1f,1f,1f).uv(1,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(matrices.last().normal(), 0,0,1);
        vertex.vertex(m,xSize,length,0).color(1f,1f,1f,1f).uv(1,v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(matrices.last().normal(), 0,0,1);
        vertex.vertex(m,-xSize,length,0).color(1f,1f,1f,1f).uv(0,v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(matrices.last().normal(), 0,0,1);
        vertex.vertex(m,-xSize,0,0).color(1f,1f,1f,1f).uv(0,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(matrices.last().normal(), 0,0,1);


        vertex.vertex(m,-xSize,0,0).color(1f,1f,1f,1f).uv(0,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(matrices.last().normal(), 0,0,1);
        vertex.vertex(m,-xSize,length,0).color(1f,1f,1f,1f).uv(0,v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(matrices.last().normal(), 0,0,1);
        vertex.vertex(m,xSize,length,0).color(1f,1f,1f,1f).uv(1,v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(matrices.last().normal(), 0,0,1);
        vertex.vertex(m,xSize,0,0).color(1f,1f,1f,1f).uv(1,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(matrices.last().normal(), 0,0,1);


        matrices.popPose();


    }

    public void renderChainCircle(GeburahChainTrapEntity trap, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light){
        matrices.pushPose();

        VertexConsumer vertex = src.getBuffer(RenderType.text(CHAIN));

        float time = trap.tickCount + pticks; time = -time * 5;

        float offset = 0.6f;

        float size = 0.12f;

        int count = 16;
        float angle = 360f / count;

        for (int i = 0; i < count; i++) {

            matrices.pushPose();

            matrices.translate(0,0.1f,0);

            matrices.mulPose(Axis.YP.rotationDegrees(angle * i + time));

            matrices.translate(offset,0,0);

            matrices.mulPose(Axis.ZP.rotationDegrees(-45));

            QuadRenderer.start(vertex)
                    .pose(matrices)
                    .renderBack()
                    .size(size)
                    .sizeX(size * 0.6f)
                    .render();

            matrices.popPose();

        }


        matrices.popPose();
    }


    @Override
    public boolean shouldRender(GeburahChainTrapEntity p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(GeburahChainTrapEntity p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
