package com.finderfeed.fdbosses.content.entities.geburah.respawn_point_setter_block;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.util.rendering.renderers.QuadRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class GeburahRespiteBlockEntityRenderer implements BlockEntityRenderer<GeburahRespiteBlockEntity> {

    public static final ResourceLocation RESOURCE_LOCATION = FDBosses.location("textures/misc/geburah_respawn_point_setter.png");

    public GeburahRespiteBlockEntityRenderer(BlockEntityRendererProvider.Context ctx){

    }

    @Override
    public void render(GeburahRespiteBlockEntity tile, float partialTicks, PoseStack matrices, MultiBufferSource src, int light, int overlay) {

        var vertexConsumer = src.getBuffer(RenderType.eyes(RESOURCE_LOCATION));

        var state = tile.getBlockState();
        Direction direction = state.getValue(GeburahRespiteBlock.FACING);

        float time = tile.getLevel().getGameTime() + partialTicks;

        matrices.pushPose();

        matrices.translate(0.5,0.5,0.5);

        QuadRenderer.start(vertexConsumer)
                .direction(new Vec3(-direction.getStepX(),-direction.getStepY(),-direction.getStepZ()))
                .offsetOnDirection(1f)
                .rotationDegrees(time)
                .pose(matrices)
                .renderBack()
                .size(1.1f)
                .render();

        QuadRenderer.start(vertexConsumer)
                .direction(new Vec3(-direction.getStepX(),-direction.getStepY(),-direction.getStepZ()))
                .offsetOnDirection(0.75f)
                .rotationDegrees(-time)
                .pose(matrices)
                .renderBack()
                .size(0.6f)
                .render();

        matrices.popPose();

    }

}
