package com.finderfeed.fdbosses.content.entities.netzach.qte;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDFreeEntityRenderer;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class NetzachQteRenderer implements FDFreeEntityRenderer<NetzachQteAttack> {

    private static final ResourceLocation MARKER = FDBosses.location("textures/entities/netzach/netzach_qte.png");

    @Override
    public void render(NetzachQteAttack attack, float yaw, float partialTick, PoseStack matrices, MultiBufferSource buffers, int light) {
        if (attack.isResolved()) return;
        float alpha = attack.getAlpha(partialTick);

        int frame;
        if (attack.tickCount < NetzachQteAttack.PARRY_START_TICK){
            float progress = (attack.tickCount + partialTick) / NetzachQteAttack.PARRY_START_TICK;
            frame = Math.min(5, (int) (FDEasings.easeIn(progress) * 6));
        }else if (attack.tickCount < NetzachQteAttack.STRIKE_TICK){
            frame = 6 - (attack.tickCount - NetzachQteAttack.PARRY_START_TICK) % 2;
        }else{
            frame = 5;
        }

        float u0 = frame / 7f;
        float u1 = (frame + 1) / 7f;
        float radius = attack.getBbWidth() / 2;
        matrices.pushPose();
        matrices.translate(0, attack.getBbHeight()/2,0);
        matrices.mulPose(Axis.YN.rotationDegrees(yaw));
        matrices.mulPose(Axis.XP.rotationDegrees(attack.getViewXRot(partialTick)));
        matrices.translate(0, 0, attack.getBbHeight()/2);
        var pose = matrices.last();

        var vertex = buffers.getBuffer(RenderType.text(MARKER));
        vertex.addVertex(pose, -radius, -radius, -0.01f).setColor(1f, 1f, 1f, alpha).setUv(u0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0, 0, 1);
        vertex.addVertex(pose, radius, -radius, -0.01f).setColor(1f, 1f, 1f, alpha).setUv(u1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0, 0, 1);
        vertex.addVertex(pose, radius, radius, -0.01f).setColor(1f, 1f, 1f, alpha).setUv(u1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0, 0, 1);
        vertex.addVertex(pose, -radius, radius, -0.01f).setColor(1f, 1f, 1f, alpha).setUv(u0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0, 0, 1);

        vertex = buffers.getBuffer(RenderType.EYES.apply(MARKER, RenderStateShard.LIGHTNING_TRANSPARENCY));
        vertex.addVertex(pose, -radius, -radius, 0).setColor(1f, 1f, 1f, alpha).setUv(u0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0, 0, 1);
        vertex.addVertex(pose, radius, -radius, 0).setColor(1f, 1f, 1f, alpha).setUv(u1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0, 0, 1);
        vertex.addVertex(pose, radius, radius, 0).setColor(1f, 1f, 1f, alpha).setUv(u1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0, 0, 1);
        vertex.addVertex(pose, -radius, radius, 0).setColor(1f, 1f, 1f, alpha).setUv(u0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0, 0, 1);

        matrices.popPose();
    }

}
