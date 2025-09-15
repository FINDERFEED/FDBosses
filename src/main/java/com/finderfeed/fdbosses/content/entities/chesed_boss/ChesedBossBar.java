package com.finderfeed.fdbosses.content.entities.chesed_boss;

import com.finderfeed.fdbosses.client.particles.arc_lightning.ArcLightningParticle;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.hud.bossbars.FDBossBarInterpolated;
import com.finderfeed.fdlib.init.FDCoreShaders;
import com.finderfeed.fdlib.systems.screen.screen_particles.FDTexturedSParticle;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticle;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.finderfeed.fdlib.util.rendering.FDShaderRenderer;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ChesedBossBar extends FDBossBarInterpolated {

    public static final int HIT_EVENT = 0;
    public static final int MAX_HIT_TIME = 20;

    private static final Random r = new Random();

    public static final ResourceLocation TEXTURE = ResourceLocation.tryBuild(FDBosses.MOD_ID,"textures/boss_bars/chesed_bossbar.png");

    private int previousHitStrength = 0;
    private int time = 0;

    private int hitTime = 0;
    private int hitTimeO = 0;


    public ChesedBossBar(UUID uuid, int entityId) {
        super(uuid, entityId,10);
    }

    //254 59 (54)
    @Override
    public void renderInterpolatedBossBar(GuiGraphics graphics, float partialTicks,float interpolatedPercentage) {

        PoseStack matrices = graphics.pose();


        matrices.pushPose();

        if (hitTime != 0) {

            float str = 1 + Math.min(previousHitStrength / 5f,1.5f);

            float t = FDMathUtil.lerp(hitTimeO, hitTime, partialTicks) / MAX_HIT_TIME;
            t *= t;
            str *= t;

            long rndOffset = 2343;

            Random shakeRnd = new Random((time + 1) * rndOffset);
            float tx = shakeRnd.nextFloat() * 2.5f - 1.25f;
            float ty = shakeRnd.nextFloat() * 2.5f - 1.25f;

            matrices.translate(tx * str, ty * str, 0);
        }

        FDRenderUtil.bindTexture(TEXTURE);
        FDRenderUtil.blitWithBlend(matrices,-254/2f,0,254,54,0,0,
                254,54,254,59,0,1f);

        float hpPosX = -254/2f + 31;
        float hpPosY = 26;
        float hpW = 192f;

        FDRenderUtil.blitWithBlend(matrices,hpPosX,hpPosY,hpW * interpolatedPercentage,5,0,54,
                hpW * interpolatedPercentage,5,254,59,0,1f);




        float shaderTime = (time + partialTicks) / 4000f;


        FDRenderUtil.Scissor.pushScissors(matrices,hpPosX,hpPosY,hpW * interpolatedPercentage,5);
        FDShaderRenderer.start(graphics,FDCoreShaders.NOISE)
                .position(hpPosX,hpPosY,0)
                .setResolution(hpW,5)
                .uvSpan(0.5f,1)
                .setUpColor(0.1f,0.8f,0.8f,0f)
                .setDownColor(0.5f,1f,1f,0.8f)
                .setShaderUniform("size",hpW,5)
                .setShaderUniform("xyOffset",0,shaderTime)
                .setShaderUniform("sections",100)
                .setShaderUniform("octaves",4)
                .setShaderUniform("time",shaderTime)
                .end();
        FDRenderUtil.Scissor.popScissors();


        this.renderLightning(matrices,hpPosX,hpPosY,hpW,interpolatedPercentage,partialTicks);

        matrices.popPose();
    }

    private void renderLightning(PoseStack matrices,float hpPosX,float hpPosY,float hpW,float interpolatedPercentage,float partialTicks){
        if (hitTime != 0) {
            float t = FDMathUtil.lerp(hitTimeO, hitTime, partialTicks) / MAX_HIT_TIME;
            t *= t;

            hpW *= interpolatedPercentage;

            List<Vec3> path = List.of(
                    new Vec3(hpPosX-2, hpPosY + 2.5f, 0),
                    new Vec3(hpPosX + hpW+2, hpPosY + 2.5f, 0)
            );
            List<Vec3> path2 = List.of(
                    new Vec3(hpPosX-2, hpPosY + 2.5f, 0),
                    new Vec3(hpPosX + hpW+2, hpPosY + 2.5f, 0)
            );
            float lw = 1.75f;
            float lspread = 4;
            int lightningBreaks = Math.max(2, Math.round(interpolatedPercentage * 20));
            ArcLightningParticle.fullLightningImmediateDraw(time, 32423543, lightningBreaks, matrices.last().pose(), path, lw, lspread, 0.2f, 1f, 1f, t);
            ArcLightningParticle.fullLightningImmediateDraw(time, 32423543, lightningBreaks, matrices.last().pose(), path, lw / 2, lspread, 0.7f, 1f, 1f, t);

            ArcLightningParticle.fullLightningImmediateDraw(time, 5353543, lightningBreaks, matrices.last().pose(), path2, lw, lspread, 0.2f, 1f, 1f, t);
            ArcLightningParticle.fullLightningImmediateDraw(time, 5353543, lightningBreaks, matrices.last().pose(), path2, lw / 2, lspread, 0.7f, 1f, 1f, t);
        }
    }

    @Override
    public void tick(float topOffset) {
        super.tick(topOffset);
        time++;
        hitTimeO = hitTime;
        hitTime = Mth.clamp(hitTime - 1,0,MAX_HIT_TIME);

        Window window = Minecraft.getInstance().getWindow();

        float w = window.getGuiScaledWidth();
        float h = window.getGuiScaledHeight();


        if (time % 4 == 0) {
            float randomOffset = r.nextFloat() * 200 - 100;
            float randomOffset2 = r.nextFloat() * 200 - 100;
            float x = w / 2 + randomOffset;
            float x2 = w / 2 + randomOffset2;
            float y = 28;
            FDTexturedSParticle.create(FDRenderUtil.ParticleRenderTypesS.TEXTURES_BLUR_ADDITIVE, BallParticle.LOCATION)
                    .setPos(x, y, true)
                    .setMaxQuadSize(3.5f)
                    .setSpeed(0, -0.1)
                    .setFriction(0.99f)
                    .color(
                            0.1f + r.nextFloat() * 0.1f - 0.05f,
                            0.8f + r.nextFloat() * 0.1f - 0.05f,
                            0.8f + r.nextFloat() * 0.1f - 0.05f,
                            0.8f
                    )
                    .setLifetime(30)
                    .setDefaultScaleInOut()
                    .sendToOverlay();
            FDTexturedSParticle.create(FDRenderUtil.ParticleRenderTypesS.TEXTURES_BLUR_ADDITIVE, BallParticle.LOCATION)
                    .setPos(x2, y + 7, true)
                    .setMaxQuadSize(3.5f)
                    .setSpeed(0, 0.1)
                    .setFriction(0.99f)
                    .color(
                            0.1f + r.nextFloat() * 0.1f - 0.05f,
                            0.8f + r.nextFloat() * 0.1f - 0.05f,
                            0.8f + r.nextFloat() * 0.1f - 0.05f,
                            0.8f
                    )
                    .setLifetime(30)
                    .setDefaultScaleInOut()
                    .sendToOverlay();
        }


    }

    @Override
    public float height() {
        return 50;
    }

    @Override
    public void hanldeBarEvent(int eventId, int data) {

        if (eventId == HIT_EVENT){
            this.previousHitStrength = data;
            this.hitTime = MAX_HIT_TIME;
            this.hitTimeO = MAX_HIT_TIME;
        }

    }
}
