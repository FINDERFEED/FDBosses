package com.finderfeed.fdbosses.content.entities.netzach.netzach_gear_crush;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossModels;
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
import net.minecraft.util.Mth;

public class NetzachGearCrushAttackRenderer extends EntityRenderer<NetzachGearCrushAttack> {

    private FDModel fdModel;

    public NetzachGearCrushAttackRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
        this.fdModel = new FDModel(BossModels.GEAR_ATTACK.get());
    }


    @Override
    public void render(NetzachGearCrushAttack entity, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {

        this.renderGear(entity,yaw,pticks,matrices,src,(-yaw + 180) + 0);
        this.renderGear(entity,yaw,pticks,matrices,src,(-yaw + 180) + 90);
        this.renderGear(entity,yaw,pticks,matrices,src,(-yaw + 180) + 180);
        this.renderGear(entity,yaw,pticks,matrices,src,(-yaw + 180) + 270);

    }

    private void renderGear(NetzachGearCrushAttack entity, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, float yAngle){
        int tick = entity.tickCount;
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(yAngle));
        matrices.mulPose(Axis.XN.rotationDegrees(90));

        int preparationTime = NetzachGearCrushAttack.PREPARATION_TIME;
        int hitTime = NetzachGearCrushAttack.HIT_TIME;

        float diff = hitTime - preparationTime;

        int gearsCrushTime = 2;


        float baseX = 2;
        float baseY = 1.25f;

        float yDiff = 0.5f;
        float xDiff = 1.5f;
        float firstDiffTravelPercent = 0.8f;

        float yOffset = (float) Math.sin((tick + pticks) / 10f + yAngle) * 0.1f;

        float alpha = 1f;

        if (tick < preparationTime){
            float p = FDEasings.easeIn(Mth.clamp((tick + pticks) / preparationTime,0,1));
            alpha = p;
            matrices.translate(baseX + xDiff * p * firstDiffTravelPercent, 0,baseY + yDiff * p * firstDiffTravelPercent + yOffset);
            matrices.mulPose(Axis.YN.rotationDegrees(20 * p * firstDiffTravelPercent));
            matrices.mulPose(Axis.ZP.rotationDegrees(360 * p * firstDiffTravelPercent));
        }else if (tick < hitTime) {
            tick = tick - preparationTime;
            float p = FDEasings.easeOut(Mth.clamp((tick + pticks) / (diff - 1),0,1));

            matrices.translate(
                    xDiff * firstDiffTravelPercent + baseX + (1 - firstDiffTravelPercent) * xDiff * p,
                    0,
                    yDiff * firstDiffTravelPercent + baseY +  (1 - firstDiffTravelPercent) * yDiff * p + yOffset);
            matrices.mulPose(Axis.YN.rotationDegrees(20 * firstDiffTravelPercent +  (1 - firstDiffTravelPercent) * 20 * p));
            matrices.mulPose(Axis.ZP.rotationDegrees(360 * firstDiffTravelPercent +  (1 - firstDiffTravelPercent) * 360 * p));


        }else{
            tick = tick - hitTime;
            float p = FDEasings.easeIn(Mth.clamp((tick + pticks) / gearsCrushTime,0,1));

            float xOffset = baseX + xDiff;
            matrices.translate(xOffset - (xOffset - 0.5) * p, 0,baseY + yDiff - 0.5f * p + yOffset * (1 - p));
            matrices.mulPose(Axis.YN.rotationDegrees(20 - p * 10));
            matrices.mulPose(Axis.XN.rotationDegrees(-20 * p));
            matrices.mulPose(Axis.ZP.rotationDegrees(360 - p * 360));

            alpha = Mth.clamp(1 - (tick + pticks) / 20,0,1);
        }

        var vertex = src.getBuffer(RenderType.entityTranslucentCull(FDBosses.location("textures/entities/netzach/gear_attack.png")));

        fdModel.render(matrices, vertex, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, alpha);


        matrices.popPose();
    }



    @Override
    public ResourceLocation getTextureLocation(NetzachGearCrushAttack p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public boolean shouldRender(NetzachGearCrushAttack p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return true;
    }

}
