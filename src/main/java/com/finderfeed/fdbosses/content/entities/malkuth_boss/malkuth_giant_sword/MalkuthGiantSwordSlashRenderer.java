package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_giant_sword;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_earthquake.MalkuthEarthquakeSegment;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
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

import java.util.Random;

public class MalkuthGiantSwordSlashRenderer extends EntityRenderer<MalkuthGiantSwordSlash> {

    private static FDModel model;

    public MalkuthGiantSwordSlashRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        model = new FDModel(BossModels.MALKUTH_SWORD.get());
    }

    @Override
    public void render(MalkuthGiantSwordSlash slash, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {

        matrices.pushPose();

        float dissolveTime = slash.getDissolveTime(pticks);
        float dissolvePercent = 1 - dissolveTime / MalkuthGiantSwordSlash.DISSOLVE_TIME;
        float riseTime = slash.getCurrentMoveUpTime(pticks);
        float hitTime = slash.getCurrentHitTime(pticks);

        float riseP = FDEasings.easeOutBack(riseTime / MalkuthGiantSwordSlash.TIME_TO_RISE);
        float risePn = FDEasings.easeOut(riseTime / MalkuthGiantSwordSlash.TIME_TO_RISE);
        float hitP = BossUtil.easeInBack(hitTime / MalkuthGiantSwordSlash.TIME_TO_HIT);

        float scale = 14;

        float riseHeight = 60;

        matrices.mulPose(Axis.YN.rotationDegrees(yaw));

        matrices.translate(0, riseP * riseHeight - riseHeight + 2,0);

        float rotation = hitP * 90 + (float) Math.sin(risePn * FDMathUtil.FPI * 2) * 2f;

        matrices.mulPose(Axis.XP.rotationDegrees(rotation));

        matrices.mulPose(Axis.YP.rotationDegrees(180));

        matrices.scale(scale,scale,scale);

        model.render(matrices, src.getBuffer(RenderType.entityTranslucent(MalkuthEntity.MALKUTH_SWORD_SOLID)), light, OverlayTexture.NO_OVERLAY, 1f ,1f ,1f ,dissolvePercent);

        if (slash.getAttackType().isFire()){
            model.render(matrices, src.getBuffer(RenderType.text(MalkuthEntity.MALKUTH_FIRE_SWORD)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1f ,1f ,1f ,dissolvePercent);
        }else{
            model.render(matrices, src.getBuffer(RenderType.text(MalkuthEntity.MALKUTH_ICE_SWORD)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1f ,1f ,1f ,dissolvePercent);
        }


        matrices.popPose();


        if (dissolveTime == 0) return;


        matrices.pushPose();

        matrices.mulPose(Axis.YN.rotationDegrees(yaw));

        Random random = new Random(slash.getId() * 424L);

        float xStart = 10;
        float xEnd = 40;

        var model = MalkuthEarthquakeSegment.getBaseModel();

        VertexConsumer vertexConsumer;

        int rows = 6;





        float maxRowScale = 3;

        for (int g = 0; g < rows; g++) {
            float rowp = g / (rows - 1f);

            float timeOffset = rowp * 5f;

            float offsetMax = MalkuthGiantSwordSlash.DISSOLVE_TIME - timeOffset;

            float t = Math.clamp(dissolveTime - timeOffset, 0, offsetMax);

            float upTime = offsetMax / 8f;
            float downTime = offsetMax - upTime;

            var func = ComplexEasingFunction.builder()
                    .addArea(upTime,FDEasings::easeIn)
                    .addArea(downTime,FDEasings::reversedEaseOut)
                    .build();

            float earthquakesP = func.apply(t);

            float sc = maxRowScale - rowp * 2.5f;

            for (float i = xStart; i < xEnd; i+=sc) {



                MalkuthEarthquakeSegment.Type type = MalkuthEarthquakeSegment.Type.getRandomBaseSegment(slash.getAttackType(), random, true);
                MalkuthEarthquakeSegment.Type type2 = MalkuthEarthquakeSegment.Type.getRandomBaseSegment(slash.getAttackType(), random, true);

                vertexConsumer = src.getBuffer(RenderType.text(type.getTexture()));



                float translation = 2.5f - earthquakesP * 2.5f;
//                translation = 0;

                float xtranslation = g + (maxRowScale - sc) + random.nextFloat() * 0.25f;

                float zpos = random.nextFloat() * sc / 2 + i;
                matrices.pushPose();
                matrices.translate(xtranslation, 0, zpos);
                matrices.mulPose(Axis.YP.rotationDegrees(90));
                matrices.mulPose(Axis.XP.rotationDegrees(35 + random.nextFloat() * 10f));
                matrices.scale(sc,sc,sc);
                matrices.translate(0,-translation,0);
                model.render(matrices, vertexConsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1f,1f, 1f, 1f);
                matrices.popPose();

                vertexConsumer = src.getBuffer(RenderType.text(type2.getTexture()));
                matrices.pushPose();
                zpos = random.nextFloat() * sc / 2 + i;
                matrices.translate(-xtranslation, 0, zpos);
                matrices.mulPose(Axis.YP.rotationDegrees(-90));
                matrices.mulPose(Axis.XP.rotationDegrees(35 + random.nextFloat() * 10f));
                matrices.scale(sc,sc,sc);
                matrices.translate(0,-translation,0);
                model.render(matrices, vertexConsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1f,1f, 1f, 1f);
                matrices.popPose();

            }
        }

        matrices.popPose();



    }

    @Override
    public ResourceLocation getTextureLocation(MalkuthGiantSwordSlash slash) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public boolean shouldRender(MalkuthGiantSwordSlash p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return true;
    }

}
