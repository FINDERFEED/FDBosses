package com.finderfeed.fdbosses.content.entities.chesed_boss.radial_earthquake;

import com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity.EarthShatterEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity.EarthShatterSettings;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;

public class RadialEarthquakeRenderer extends EntityRenderer<RadialEarthquakeEntity> {

    public RadialEarthquakeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }


    @Override
    public void render(RadialEarthquakeEntity entity, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {
        super.render(entity, yaw, pticks, matrices, src, light);

        var map = entity.clientEarthShatters;

        for (var inst : map.entrySet()){
            this.renderEarthShatter(entity,yaw,pticks,matrices,src,light,inst.getValue(), inst.getKey());
        }

    }

    private void renderEarthShatter(RadialEarthquakeEntity entity, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light, RadialEarthquakeEntity.EarthShatterInstance instance, BlockPos shatterPos){

        BlockPos bpoffset = shatterPos.subtract(entity.getOnPos());

        Vec3 offset = new Vec3(bpoffset.getX(), -0.6, bpoffset.getZ());




        BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();
        BlockState state = instance.getBlockState();



        EarthShatterSettings settings = instance.getSettings();

        if (instance.getTickCount() < settings.delay){
            return;
        }
        matrices.pushPose();

        matrices.translate(offset.x,offset.y,offset.z);

        ComplexEasingFunction function = ComplexEasingFunction.builder()
                .addArea(settings.upTime, FDEasings::easeOut)
                .addArea(settings.stayTime,f->1f)
                .addArea(settings.downTime,FDEasings::reversedEaseOut)
                .build();

        float p = function.apply(instance.getTickCount() + pticks - settings.delay);

        Vec3 dir = settings.direction;

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


        renderer.renderSingleBlock(state,matrices,src,light, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

        matrices.popPose();





    }

    @Override
    public ResourceLocation getTextureLocation(RadialEarthquakeEntity p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

}
