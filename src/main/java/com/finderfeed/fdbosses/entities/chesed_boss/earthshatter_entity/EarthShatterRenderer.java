package com.finderfeed.fdbosses.entities.chesed_boss.earthshatter_entity;

import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;

public class EarthShatterRenderer extends EntityRenderer<EarthShatterEntity> {

    public EarthShatterRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(EarthShatterEntity entity, float idk, float pticks, PoseStack matrices, MultiBufferSource src, int light) {
        BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();
        BlockState state = entity.getBlockState();



        EarthShatterSettings settings = entity.settings;
        if (entity.tickCount < settings.delay){
            return;
        }
        matrices.pushPose();
        ComplexEasingFunction function = ComplexEasingFunction.builder()
                .addArea(settings.upTime, FDEasings::easeOut)
                .addArea(settings.stayTime,f->1f)
                .addArea(settings.downTime,FDEasings::reversedEaseOut)
                .build();

        float p = function.apply(entity.tickCount + pticks - settings.delay);

        Vec3 dir = entity.getShatterDirection();

        Vec3 init = new Vec3(0,1,0);

        dir = new Vec3(
                FDMathUtil.lerp((float) init.x,(float) dir.x,p),
                FDMathUtil.lerp((float) init.y,(float) dir.y,p),
                FDMathUtil.lerp((float) init.z,(float) dir.z,p)
        );

        matrices.translate(-0.5,p * settings.upDistance,-0.5);

        matrices.translate(0.5,0.5,0.5);
        matrices.scale(1.1f,1.1f,1.1f);
        FDRenderUtil.applyMovementMatrixRotations(matrices,dir);
        matrices.translate(-0.5,-0.5,-0.5);




        BlockPos pos = entity.getOnPos().above(2);
        light = LightTexture.pack(this.getBlockLightLevel(entity, pos), this.getSkyLightLevel(entity, pos));


        renderer.renderSingleBlock(state,matrices,src,light,OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EarthShatterEntity p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }


    @Override
    public boolean shouldRender(EarthShatterEntity p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return super.shouldRender(p_114491_, p_114492_, p_114493_, p_114494_, p_114495_);
    }
}
