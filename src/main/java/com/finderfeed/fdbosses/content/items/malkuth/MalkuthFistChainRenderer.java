package com.finderfeed.fdbosses.content.items.malkuth;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDFreeEntityRenderer;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class MalkuthFistChainRenderer implements FDFreeEntityRenderer<MalkuthFistChain> {

    public static final ResourceLocation TEXTURE = FDBosses.location("textures/entities/malkuth/malkuth_fist_chain_segment.png");

    @Override
    public void render(MalkuthFistChain malkuthFistChain,  float yaw, float pticks, PoseStack matrices, MultiBufferSource multiBufferSource, int i) {

        int ownerId = malkuthFistChain.getEntityData().get(MalkuthFistChain.OWNER_ID);
        if (!(malkuthFistChain.level().getEntity(ownerId) instanceof Player player)) return;


        Vec3 playerPos = player.getPosition(pticks).add(0,player.getBbHeight() / 2 + 0.25,0);
        Vec3 thisPos = malkuthFistChain.getPosition(pticks);

        Vec3 between = playerPos.subtract(thisPos);
        float length = (float) between.length();

        matrices.pushPose();

        VertexConsumer vertex = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        FDRenderUtil.applyMovementMatrixRotations(matrices, between);

        Matrix4f mat = matrices.last().pose();

        float segmentWidth = 0.05f;

        float ratio = 7/4f * 2;

        vertex.addVertex(mat,-segmentWidth,0,0).setColor(1f,1f,1f,1f).setUv(0,0).setLight(i).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), 0,0,1);
        vertex.addVertex(mat,-segmentWidth, length,0).setColor(1f,1f,1f,1f).setUv(0,ratio * length).setLight(i).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), 0,0,1);
        vertex.addVertex(mat,segmentWidth,length,0).setColor(1f,1f,1f,1f).setUv(1,ratio * length).setLight(i).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), 0,0,1);
        vertex.addVertex(mat,segmentWidth,0,0).setColor(1f,1f,1f,1f).setUv(1,0).setLight(i).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), 0,0,1);

        vertex.addVertex(mat,0,0,-segmentWidth).setColor(1f,1f,1f,1f).setUv(0,0).setLight(i).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), 1,0,0);
        vertex.addVertex(mat,0, length,-segmentWidth).setColor(1f,1f,1f,1f).setUv(0,ratio * length).setLight(i).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), 1,0,0);
        vertex.addVertex(mat,0,length,segmentWidth).setColor(1f,1f,1f,1f).setUv(1,ratio * length).setLight(i).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), 1,0,0);
        vertex.addVertex(mat,0,0,segmentWidth).setColor(1f,1f,1f,1f).setUv(1,0).setLight(i).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), 1,0,0);



        matrices.popPose();

    }

}
