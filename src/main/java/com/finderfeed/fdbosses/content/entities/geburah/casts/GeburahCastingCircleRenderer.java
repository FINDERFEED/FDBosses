package com.finderfeed.fdbosses.content.entities.geburah.casts;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
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
import net.minecraft.world.phys.Vec3;

public class GeburahCastingCircleRenderer extends EntityRenderer<GeburahCastingCircle> {

    public static final ResourceLocation GEBURAH_CASTING_CIRCLE_ROTATING = FDBosses.location("textures/entities/geburah/geburah_casting_circle_rotating.png");
    public static final ResourceLocation GEBURAH_CASTING_CIRCLE = FDBosses.location("textures/entities/geburah/geburah_casting_circle_static.png");

    public GeburahCastingCircleRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public void render(GeburahCastingCircle entity, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {
        matrices.pushPose();

        ComplexEasingFunction complexEasingFunction = ComplexEasingFunction.builder()
                .addArea(GeburahCastingCircle.FADE_IN, FDEasings::easeOut)
                .addArea(entity.getDuration() - GeburahCastingCircle.FADE_IN - GeburahCastingCircle.FADE_OUT, FDEasings::one)
                .addArea(GeburahCastingCircle.FADE_IN, FDEasings::reversedEaseOut)
                .build();

        float rotation = 5 * (entity.tickCount + pticks);
        float scale = complexEasingFunction.apply(entity.tickCount + pticks);

        Vec3 direction = entity.getCastDirection();
        FDColor color = FDColor.decode(entity.getEntityData().get(GeburahCastingCircle.COLOR));

        VertexConsumer vertexConsumer = src.getBuffer(RenderType.text(GEBURAH_CASTING_CIRCLE));

        float size = scale * 1f;

        QuadRenderer.start(vertexConsumer)
                .pose(matrices)
                .direction(direction)
                .renderBack()
                .color(color.r,color.g,color.b,color.a)
                .light(LightTexture.FULL_BRIGHT)
                .size(size)
                .renderBack()
                .render();


        vertexConsumer = src.getBuffer(RenderType.text(GEBURAH_CASTING_CIRCLE_ROTATING));

        QuadRenderer.start(vertexConsumer)
                .pose(matrices)
                .direction(direction)
                .rotationDegrees(rotation)
                .renderBack()
                .color(color.r,color.g,color.b,color.a)
                .light(LightTexture.FULL_BRIGHT)
                .size(size)
                .renderBack()
                .render();

        matrices.popPose();

    }

    @Override
    public ResourceLocation getTextureLocation(GeburahCastingCircle p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

}
