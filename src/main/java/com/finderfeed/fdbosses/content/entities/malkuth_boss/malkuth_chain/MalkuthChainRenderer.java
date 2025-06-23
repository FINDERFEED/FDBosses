package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_chain;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class MalkuthChainRenderer extends EntityRenderer<MalkuthChainEntity> {

    public MalkuthChainRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }


    @Override
    public void render(MalkuthChainEntity chain, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {
        matrices.pushPose();


        Vec3 handPos = chain.getMalkuthHandPos(pticks);

        if (handPos == null) {
            matrices.popPose();
            return;
        }

        Vec3 pos = chain.position();

        Vec3 between = handPos.subtract(pos);

        FDRenderUtil.applyMovementMatrixRotations(matrices, between);

        VertexConsumer b = src.getBuffer(RenderType.text(this.getTextureLocation(chain)));

        float length = (float) between.length();

        Matrix4f m = matrices.last().pose();

        float width = 0.2f;

        if (!chain.getPassengers().isEmpty()) {
            matrices.mulPose(Axis.YP.rotationDegrees(-135));
        }
        b.addVertex(m, -width,0,0).setColor(1f,1f,1f,1f).setUv(0,0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
        b.addVertex(m, -width,length,0).setColor(1f,1f,1f,1f).setUv(length * 4,0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
        b.addVertex(m, width,length,0).setColor(1f,1f,1f,1f).setUv(length * 4,1).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
        b.addVertex(m, width,0,0).setColor(1f,1f,1f,1f).setUv(0,1).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);

        b.addVertex(m, width,0,0).setColor(1f,1f,1f,1f).setUv(0,1).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
        b.addVertex(m, width,length,0).setColor(1f,1f,1f,1f).setUv(length * 4,1).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
        b.addVertex(m, -width,length,0).setColor(1f,1f,1f,1f).setUv(length * 4,0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
        b.addVertex(m, -width,0,0).setColor(1f,1f,1f,1f).setUv(0,0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);


        b.addVertex(m, 0,0,-width).setColor(1f,1f,1f,1f).setUv(0,0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
        b.addVertex(m, 0,length,-width).setColor(1f,1f,1f,1f).setUv(length * 4,0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
        b.addVertex(m, 0,length,width).setColor(1f,1f,1f,1f).setUv(length * 4,1).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
        b.addVertex(m, 0,0,width).setColor(1f,1f,1f,1f).setUv(0,1).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);

        b.addVertex(m, 0,0,width).setColor(1f,1f,1f,1f).setUv(0,1).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
        b.addVertex(m, 0,length,width).setColor(1f,1f,1f,1f).setUv(length * 4,1).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
        b.addVertex(m, 0,length,-width).setColor(1f,1f,1f,1f).setUv(length * 4,0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);
        b.addVertex(m, 0,0,-width).setColor(1f,1f,1f,1f).setUv(0,0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY);

        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(MalkuthChainEntity p_114482_) {
        return FDBosses.location("textures/util/chain_segment.png");
    }

    @Override
    public boolean shouldRender(MalkuthChainEntity p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return true;
    }

}
