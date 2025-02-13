package com.finderfeed.fdbosses.content.entities.chesed_boss.flying_block_entity;

import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;

public class FlyingBlockEntityRenderer extends EntityRenderer<FlyingBlockEntity> {
    public FlyingBlockEntityRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }


    @Override
    public void render(FlyingBlockEntity entity, float idk, float partialTicks, PoseStack matrices, MultiBufferSource src, int light) {
        matrices.pushPose();

        BlockState state = entity.getBlockState();
        BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();

        matrices.translate(-0.5,0,-0.5);
        matrices.translate(0.5,0.5,0.5);
        Vec3 horizontal = entity.getDeltaMovement().multiply(1,0,1).normalize();

        FDRenderUtil.applyMovementMatrixRotations(matrices,horizontal);

        float time = (entity.tickCount + partialTicks) * entity.getRotationSpeed() + entity.getId() % 500 * 323;
        matrices.mulPose(FDRenderUtil.rotationDegrees(FDRenderUtil.XP(),time));

        matrices.translate(-.5,-.5,-.5);
        renderer.renderSingleBlock(state,matrices,src,light, OverlayTexture.NO_OVERLAY, ModelData.EMPTY,null);


        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(FlyingBlockEntity p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
