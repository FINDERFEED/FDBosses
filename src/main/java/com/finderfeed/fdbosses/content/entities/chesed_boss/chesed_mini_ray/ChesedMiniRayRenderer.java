package com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_mini_ray;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.renderers.QuadRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ChesedMiniRayRenderer extends EntityRenderer<ChesedMiniRay> {

    public static final ResourceLocation CHESED_RAY_PREPARE = FDBosses.location("textures/util/chesed_ray_prepare.png");

    public ChesedMiniRayRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public void render(ChesedMiniRay ray, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {
        super.render(ray, yaw, pticks, matrices, src, light);

        LivingEntity target = ray.getTarget();
        if (target == null) return;

        Vec3 pos = ray.getTargetPos(target, pticks);
        Vec3 thisPos = ray.getPosition(pticks);

        Vec3 dir = pos.subtract(thisPos);

        float size1 = 0.8f;
        float size2 = 0.5f;

        float rotation = 0;

        float alpha = 1f;

        int time1 = ray.tickCount;

        int time2 = time1 - ChesedMiniRay.TIME_UNTIL_ATTACK;

        if (time2 < 0) {
            float elapsedTime = Mth.clamp(time1 + pticks, 0, ChesedMiniRay.TIME_UNTIL_ATTACK);
            float p = elapsedTime / ChesedMiniRay.TIME_UNTIL_ATTACK;
            alpha = FDEasings.easeOut(p);
            rotation = FDEasings.easeOutBack(p) * 180;
        }else{
            float elapsedTime = Mth.clamp(time2 + pticks, 0, ChesedMiniRay.ATTACK_TIME);
            float p = FDEasings.easeOut(elapsedTime / ChesedMiniRay.ATTACK_TIME);
            alpha = 1 - p;
            size1 += p;
            size2 += p;
        }


        VertexConsumer consumer = src.getBuffer(RenderType.text(CHESED_RAY_PREPARE));

        QuadRenderer.start(consumer)
                .light(LightTexture.FULL_BRIGHT)
                .color(1f,1f,1f,alpha)
                .pose(matrices)
                .size(size1)
                .direction(dir)
                .rotationDegrees(rotation)
                .render();

        QuadRenderer.start(consumer)
                .light(LightTexture.FULL_BRIGHT)
                .color(1f,1f,1f,alpha)
                .pose(matrices)
                .size(size2)
                .direction(dir)
                .offsetOnDirection(-0.75f)
                .rotationDegrees(-rotation)
                .render();

        QuadRenderer.start(consumer)
                .light(LightTexture.FULL_BRIGHT)
                .color(1f,1f,1f,alpha)
                .pose(matrices)
                .size(size2)
                .direction(dir)
                .offsetOnDirection(0.75f)
                .rotationDegrees(-rotation)
                .render();



    }

    @Override
    public ResourceLocation getTextureLocation(ChesedMiniRay p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

}
