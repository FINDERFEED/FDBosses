package com.finderfeed.fdbosses.content.entities.netzach.netzach_ghost_crush;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.AnimationContext;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class NetzachGhostCrushRenderer extends EntityRenderer<NetzachGhostCrush> {

    private static final ResourceLocation BODY = FDBosses.location("textures/entities/netzach/netzach_ghost_transparent.png");
    private static final int LINE_SEGMENTS = 16;

    public NetzachGhostCrushRenderer(EntityRendererProvider.Context context){
        super(context);
    }

    @Override
    public void render(NetzachGhostCrush entity, float yaw, float partialTick, PoseStack matrices, MultiBufferSource buffers, int light){
        float age = entity.tickCount + partialTick;

        int duration = entity.getDuration();
        int descent = Math.min(NetzachGhostCrush.DESCENT_TICKS, duration);
        int ascent = duration - descent;

        float rise = Mth.clamp(age / Math.max(1, ascent), 0, 1);
        float fall = Mth.clamp((age - ascent) / Math.max(1, descent), 0, 1);

        float height = (2 + 2 * rise) * (1 - fall);
        float offset = -4 * (1 - fall);

        float frame = age < ascent ? Mth.lerp(rise, 3f, 22f) : Mth.lerp(fall, 30f, 40f);
        float fade = Mth.clamp((age - duration) / NetzachGhostCrush.FADE_TICKS, 0, 1);

        float alpha = Mth.clamp(age / NetzachGhostCrush.FADE_TICKS, 0, 1) * (1 - fade);

        matrices.pushPose();
        matrices.mulPose(Axis.YN.rotationDegrees(entity.getYRot()));

        if (age < duration){
            var pose = matrices.last();
            var line = buffers.getBuffer(RenderType.lines());
            float endY = height + 1.5f;
            for (int i = 0; i < LINE_SEGMENTS; i++){
                float start = (float)i / LINE_SEGMENTS;
                float end = (float)(i + 1) / LINE_SEGMENTS;
                float blue = 0.5f + 0.5f * Mth.sin((float)(4 * Math.PI) * start + age * 0.4f);
                line.addVertex(pose.pose(), 0, endY * start, offset * start).setColor(1f, 1f, blue, alpha).setNormal(pose, 0, endY, offset);
                line.addVertex(pose.pose(), 0, endY * end, offset * end).setColor(1f, 1f, blue, alpha).setNormal(pose, 0, endY, offset);
            }
        }

        matrices.translate(0, height, offset);
        matrices.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));

        var animation = BossAnims.NETZACH_CRUSH.get();
        var context = new AnimationContext(animation, Animation.LoopMode.HOLD_ON_LAST_FRAME);

        FDModel body = NetzachGhostCrush.getBodyModel();
        FDModel layer = NetzachGhostCrush.getLayerModel();

        body.resetTransformations();
        layer.resetTransformations();

        animation.applyAnimation(context, body, frame, 1);
        animation.applyAnimation(context, layer, frame, 1);

        layer.render(matrices, buffers.getBuffer(RenderType.EYES.apply(
                BODY,
                RenderStateShard.LIGHTNING_TRANSPARENCY)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, alpha, alpha, alpha, alpha * alpha);
        body.render(matrices, buffers.getBuffer(RenderType.entityTranslucent(FDBosses.location("textures/entities/netzach/netzach_ghost.png"))), light, OverlayTexture.NO_OVERLAY, 1, 1, 1f, alpha * 0.75f);

        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(NetzachGhostCrush entity){
        return BODY;
    }
}
