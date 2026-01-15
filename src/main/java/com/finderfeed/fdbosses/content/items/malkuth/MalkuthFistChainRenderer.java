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

        vertex.vertex(mat,-segmentWidth,0,0).color(1f,1f,1f,1f).uv(0,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(i).normal(matrices.last().normal(), 0,0,1).endVertex();
        vertex.vertex(mat,-segmentWidth, length,0).color(1f,1f,1f,1f).uv(0,ratio * length).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(i).normal(matrices.last().normal(), 0,0,1).endVertex();
        vertex.vertex(mat,segmentWidth,length,0).color(1f,1f,1f,1f).uv(1,ratio * length).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(i).normal(matrices.last().normal(), 0,0,1).endVertex();
        vertex.vertex(mat,segmentWidth,0,0).color(1f,1f,1f,1f).uv(1,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(i).normal(matrices.last().normal(), 0,0,1).endVertex();

        vertex.vertex(mat,0,0,-segmentWidth).color(1f,1f,1f,1f).uv(0,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(i).normal(matrices.last().normal(), 1,0,0).endVertex();
        vertex.vertex(mat,0, length,-segmentWidth).color(1f,1f,1f,1f).uv(0,ratio * length).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(i).normal(matrices.last().normal(), 1,0,0).endVertex();
        vertex.vertex(mat,0,length,segmentWidth).color(1f,1f,1f,1f).uv(1,ratio * length).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(i).normal(matrices.last().normal(), 1,0,0).endVertex();
        vertex.vertex(mat,0,0,segmentWidth).color(1f,1f,1f,1f).uv(1,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(i).normal(matrices.last().normal(), 1,0,0).endVertex();



        matrices.popPose();

    }

}
