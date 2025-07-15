package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_floor;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_earthquake.MalkuthEarthquakeSegment;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class MalkuthFloorRenderer extends EntityRenderer<MalkuthFloorEntity> {

    public MalkuthFloorRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public void render(MalkuthFloorEntity floor, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {
        matrices.pushPose();

        Random random = new Random(floor.getId() * 23L);

        float segmentSize = 0.9f;

        float radius = MalkuthFloorEntity.RADIUS;

        float time = floor.tickCount + pticks;


        for (float currentRad = segmentSize; currentRad <= radius; currentRad += segmentSize){

            float currentArcLength = FDMathUtil.FPI * currentRad;

            for (float i = 0; i <= currentArcLength; i+= segmentSize){


                int riseTime = 5 + random.nextInt(5);

                float downPercent = 1;

                if (floor.isDead()){
                    downPercent = ((floor.deathTicks - pticks)/MalkuthFloorEntity.DEATH_TME);
                }

                float riseP = Math.clamp(time / riseTime,0, 1) * downPercent;

                float p = i / currentArcLength;

                float currentAngle = FDMathUtil.FPI * p;

                Vec3 currentOffset = new Vec3(currentRad,0,0)
                        .yRot(currentAngle);

                float rndYRot = random.nextFloat() * 360;

                float rndRot = random.nextFloat() * 10f + 10f;

                matrices.pushPose();

                matrices.translate(currentOffset.x,0,currentOffset.z);

                matrices.mulPose(Axis.YP.rotationDegrees(rndYRot));
                matrices.mulPose(Axis.XP.rotationDegrees(rndRot));

                float currentHeight = -2 + FDEasings.easeOutBack(riseP) * 2;

                matrices.translate(0,currentHeight,0);

                MalkuthEarthquakeSegment.Type type = MalkuthEarthquakeSegment.Type.getRandomBaseSegment(random);

                FDModel model = MalkuthEarthquakeSegment.getBaseModel();

                model.render(matrices, src.getBuffer(RenderType.text(type.getTexture())), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

                matrices.popPose();
            }

        }

        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(MalkuthFloorEntity p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public boolean shouldRender(MalkuthFloorEntity entity, Frustum frustum, double p_114493_, double p_114494_, double p_114495_) {

        return frustum.isVisible(new AABB(-MalkuthFloorEntity.RADIUS,-4,-MalkuthFloorEntity.RADIUS,MalkuthFloorEntity.RADIUS,4,MalkuthFloorEntity.RADIUS).move(entity.position()));
    }
}
