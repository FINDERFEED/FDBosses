package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_giant_sword;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
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

public class MalkuthGiantSwordSlashRenderer extends EntityRenderer<MalkuthGiantSwordSlash> {

    private static FDModel model;

    public MalkuthGiantSwordSlashRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        model = new FDModel(BossModels.MALKUTH_SWORD.get());
    }

    @Override
    public void render(MalkuthGiantSwordSlash slash, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {

        matrices.pushPose();

        float dissolvePercent = 1 - slash.getDissolveTime(pticks) / MalkuthGiantSwordSlash.DISSOLVE_TIME;
        float riseTime = slash.getCurrentMoveUpTime(pticks);
        float hitTime = slash.getCurrentHitTime(pticks);

        float riseP = FDEasings.easeOutBack(riseTime / MalkuthGiantSwordSlash.TIME_TO_RISE);
        float risePn = FDEasings.easeOut(riseTime / MalkuthGiantSwordSlash.TIME_TO_RISE);
        float hitP = BossUtil.easeInBack(hitTime / MalkuthGiantSwordSlash.TIME_TO_HIT);

        float scale = 14;

        float riseHeight = 12;

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



        matrices.pushPose();



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
