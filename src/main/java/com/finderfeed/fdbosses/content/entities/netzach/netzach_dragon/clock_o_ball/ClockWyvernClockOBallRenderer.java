package com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon.clock_o_ball;

import com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon.ClockWyvern;
import com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon.ClockWyvernRenderer;
import com.finderfeed.fdlib.systems.trails.FDTrailRenderer;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class ClockWyvernClockOBallRenderer extends EntityRenderer<ClockWyvernClockOBall> {

    public ClockWyvernClockOBallRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(ClockWyvernClockOBall ball, float p_114486_, float pticks, PoseStack matrices, MultiBufferSource src, int light) {
        matrices.pushPose();

        FDTrailRenderer.renderTrail(ball, ball.trail, src.getBuffer(RenderType.lightning()), matrices, 0.2f, 4, 20, pticks,
                new FDColor(1f, 0.8f, 0.6f, 1f),
                new FDColor(0.9f, 0.55f, 0.1f, 1f)
        );
        Vec3 lastKnownDeltaMovement = ball.getLastKnownDeltaMovement();

        FDRenderUtil.applyMovementMatrixRotations(matrices, lastKnownDeltaMovement);
        float time = ball.tickCount + pticks;

        matrices.mulPose(Axis.YP.rotationDegrees(time * 15));
        ClockWyvernRenderer.renderClockOBall(matrices, src, 0.9f);

        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ClockWyvernClockOBall p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
