package com.finderfeed.fdbosses.content.entities.chesed_boss.falling_block;

import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class ChesedFallingBlockRenderer extends EntityRenderer<ChesedFallingBlock> {
    public ChesedFallingBlockRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }


    @Override
    public void render(ChesedFallingBlock entity, float idk, float partialTicks, PoseStack matrices, MultiBufferSource src, int light) {
        matrices.pushPose();

        BlockState state = entity.getBlockState();
        BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();

        matrices.translate(-0.5,-0.5,-0.5);
        matrices.translate(0.5,0.5,0.5);

        matrices.mulPose(Axis.YP.rotationDegrees(entity.getId() % 500 * 432));

        float time = (entity.tickCount + partialTicks) + entity.getId() % 500 * 323;
        matrices.mulPose(FDRenderUtil.rotationDegrees(FDRenderUtil.XP(),time * 4));

        matrices.translate(-.5,-.5,-.5);
        renderer.renderSingleBlock(state,matrices,src,light, OverlayTexture.NO_OVERLAY, ModelData.EMPTY,null);


        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ChesedFallingBlock p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}