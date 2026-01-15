package com.finderfeed.fdbosses.content.entities.geburah.geburah_bell;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDFreeEntityRenderer;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.renderers.QuadRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class GeburahBellRenderer implements FDFreeEntityRenderer<GeburahBell> {

    public static final ResourceLocation EFFECT_LOCATION = FDBosses.location("textures/entities/geburah/geburah_bell_ring_effect.png");


    public static final ComplexEasingFunction EFFECT1_ALPHA = ComplexEasingFunction.builder()
            .addArea(10, FDEasings::easeOut)
            .addArea(10, FDEasings::reversedEaseOut)
            .build();

    public static final ComplexEasingFunction EFFECT1_POS = ComplexEasingFunction.builder()
            .addArea(20, FDEasings::easeOut)
            .build();


    public static final ComplexEasingFunction EFFECT2_ALPHA = ComplexEasingFunction.builder()
            .addArea(7, (p)->{
                return 0f;
            })
            .addArea(5, FDEasings::easeOut)
            .addArea(15, FDEasings::reversedEaseOut)
            .build();
    public static final ComplexEasingFunction EFFECT2_POS = ComplexEasingFunction.builder()
            .addArea(7, (p)->{
                return 0f;
            })
            .addArea(20, FDEasings::easeOut)
            .build();

    @Override
    public void render(GeburahBell geburahBell, float v, float pticks, PoseStack matrices, MultiBufferSource multiBufferSource, int i) {

        if (geburahBell.isDeadOrDying()) {


            float time = geburahBell.deathTime + pticks;

            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.text(EFFECT_LOCATION));

            float effect1Alpha = EFFECT1_ALPHA.apply(time);
            float effect1Pos = EFFECT1_POS.apply(time);

            float effect2Alpha = EFFECT2_ALPHA.apply(time);
            float effect2Pos = EFFECT2_POS.apply(time);

            FDColor color = geburahBell.isRed() ? new FDColor(1f,0.3f,0.1f,1f) : new FDColor(0.3f,0.8f,1f,1f);

            float effectDisplace = 1.25f;

            matrices.pushPose();

            matrices.translate(0,geburahBell.getBbHeight()/2,0);
            matrices.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());


            QuadRenderer.start(vertexConsumer)
                    .verticalRendering()
                    .renderBack()
                    .translate(-effectDisplace * effect1Pos,0,0)
                    .pose(matrices)
                    .size(effect1Pos * 0.75f)
                    .color(color.r,color.g,color.b, effect1Alpha * 0.25f)
                    .render();

            QuadRenderer.start(vertexConsumer)
                    .verticalRendering()
                    .renderBack()
                    .rotationDegrees(180)
                    .translate(effectDisplace * effect1Pos,0,0)
                    .pose(matrices)
                    .size(effect1Pos * 0.75f)
                    .color(color.r,color.g,color.b, effect1Alpha * 0.25f)
                    .render();


            QuadRenderer.start(vertexConsumer)
                    .verticalRendering()
                    .renderBack()
                    .translate(-effectDisplace * effect2Pos,0,0)
                    .pose(matrices)
                    .size(effect2Pos * 0.75f)
                    .color(color.r,color.g,color.b, effect2Alpha * 0.25f)
                    .render();

            QuadRenderer.start(vertexConsumer)
                    .verticalRendering()
                    .renderBack()
                    .rotationDegrees(180)
                    .translate(effectDisplace * effect2Pos,0,0)
                    .pose(matrices)
                    .size(effect2Pos * 0.75f)
                    .color(color.r,color.g,color.b, effect2Alpha * 0.25f)
                    .render();
            matrices.popPose();


        }


    }

}
