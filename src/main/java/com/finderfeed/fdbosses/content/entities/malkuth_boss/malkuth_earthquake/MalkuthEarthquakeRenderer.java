package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_earthquake;

import com.finderfeed.fdlib.util.math.FDMathUtil;
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
import net.minecraft.world.phys.Vec3;

public class MalkuthEarthquakeRenderer extends EntityRenderer<MalkuthEarthquake> {

    public MalkuthEarthquakeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(MalkuthEarthquake entity, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {

        super.render(entity,yaw,pticks,matrices,src,light);

        matrices.pushPose();

        var segments = entity.getSegments();

        for (var segment : segments){

            matrices.pushPose();

            Vec3 offset = segment.getOffset();
            float angle = segment.getAngle();

            float hangle = (float) Math.atan2(offset.x,offset.z);

            matrices.translate(offset.x,offset.y,offset.z);
            matrices.mulPose(Axis.YP.rotation(hangle));
            matrices.mulPose(Axis.XP.rotation(angle));

            float p = segment.getUpPercent(pticks);

            float up = -2f + 2f * p;

            matrices.translate(0, up,0);

            MalkuthEarthquakeSegment.Type type = segment.getType();



            if (type.isModel()){
                var model = type.getModel().get();

                VertexConsumer vertexConsumer = src.getBuffer(RenderType.entityTranslucent(type.getTexture()));

                model.render(matrices, vertexConsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,1f ,1f ,1f, 1f);
            }else{

                Vec3 cr = offset.normalize().cross(entity.getDirectionAndLength().normalize());


                if (cr.y < 0) {
                    matrices.mulPose(Axis.YP.rotationDegrees(10));
                }else{
                    matrices.mulPose(Axis.YP.rotationDegrees(-10));
                }
                VertexConsumer vertexConsumer = src.getBuffer(RenderType.text(type.getTexture()));


                QuadRenderer.start(vertexConsumer)
                        .pose(matrices)
                        .translate(0,1,0)
                        .size(1.5f)
                        .light(LightTexture.FULL_BRIGHT)
                        .renderBack()
                        .direction(new Vec3(1,0,0))
                        .render();
            }

            matrices.popPose();
        }

        matrices.popPose();

    }

    @Override
    public ResourceLocation getTextureLocation(MalkuthEarthquake texture) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public boolean shouldRender(MalkuthEarthquake p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return true;
    }
}
