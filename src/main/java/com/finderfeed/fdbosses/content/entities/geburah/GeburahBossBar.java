package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.hud.bossbars.FDBossBar;
import com.finderfeed.fdlib.systems.screen.screen_particles.FDTexturedSParticle;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticle;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Random;
import java.util.UUID;

public class GeburahBossBar extends FDBossBar {
    public static final int HIT_EVENT = 0;
    public static final int MAX_HIT_TIME = 20;
    public static final ResourceLocation GEBURAH_BOSSBAR = FDBosses.location("textures/boss_bars/geburah_bossbar.png");
    private static final Random r = new Random();
    private int time = 0;

    private int hitTime = 0;
    private int hitTimeO = 0;

    private int lastSinnedTimes = -1;

    public GeburahBossBar(UUID uuid, int entityId) {
        super(uuid, entityId);
    }

    @Override
    public void render(GuiGraphics guiGraphics, float v) {

        PoseStack matrices = guiGraphics.pose();
        matrices.pushPose();

        if (hitTime != 0) {

            float str = 1;

            float t = FDMathUtil.lerp(hitTimeO, hitTime, v) / MAX_HIT_TIME;
            t *= t;
            str *= t;

            long rndOffset = 2343;

            Random shakeRnd = new Random((time + 1) * rndOffset);
            float tx = shakeRnd.nextFloat() * 2.5f - 1.25f;
            float ty = shakeRnd.nextFloat() * 2.5f - 1.25f;

            matrices.translate(tx * str, ty * str, 0);
        }

        int hp = (int) Math.ceil(this.getPercentage() * 10);

        FDRenderUtil.bindTexture(GEBURAH_BOSSBAR);

        float scale = 1.25f;
        float width = 287/scale;
        float height = 70/scale;

        FDRenderUtil.blitWithBlend(guiGraphics.pose(), -width/2,0,width,height,0,0,1,1,1,1,0,1f);

        FDRenderUtil.bindTexture(FDBosses.location("textures/gui/geburah_sin_scales.png"));
        float scalesWidth = 18;
        float scalesOffset = 24;

        int hp1 = Math.min(hp,5);

        for (int i = 0; i < 5; i++) {

            float xOffs = 9 / scale + -width / 2 + i * scalesOffset / scale;
            float yOffs = 30 / scale;

            if (hitTime == MAX_HIT_TIME - 1 && lastSinnedTimes != -1) {
                if (i == lastSinnedTimes) {
                    Matrix4f p = guiGraphics.pose().last().pose();
                    Vector3f pos = p.transformPosition(new Vector3f());
                    this.particleExplosion(pos.x + xOffs + scalesWidth / scale / 2, pos.y + yOffs + 15 / scale / 2 - scale * 2);
                    lastSinnedTimes = -1;
                }
            }

            if (i < hp1) {
                FDRenderUtil.blitWithBlend(guiGraphics.pose(), xOffs, yOffs, scalesWidth / scale, 15 / scale, 0, 0, 1, 1, 1, 1, 0, 1);
            }
        }

        RenderSystem.disableCull();

        int hp2 = Math.min(hp - 5,5);
        for (int i = 0; i < 5; i++){

            float xOffs = -8/scale + width/2 - i * scalesOffset / scale;
            float yOffs = 30 / scale;

            if (hitTime == MAX_HIT_TIME - 1 && lastSinnedTimes != -1) {
                if (i + 5 == lastSinnedTimes) {
                    Matrix4f p = guiGraphics.pose().last().pose();
                    Vector3f pos = p.transformPosition(new Vector3f());
                    this.particleExplosion(pos.x + xOffs - scalesWidth / scale / 2, pos.y + yOffs + 15 / scale / 2 - scale * 2);
                    lastSinnedTimes = -1;
                }
            }
            if (i < hp2) {
                FDRenderUtil.blitWithBlend(guiGraphics.pose(), xOffs, yOffs, -scalesWidth / scale, 15 / scale, 0, 0, 1, 1, 1, 1, 0, 1);
            }
        }
        RenderSystem.enableCull();

        matrices.popPose();

    }

    @Override
    public void tick(float v) {
        time++;
        hitTimeO = hitTime;
        hitTime = Mth.clamp(hitTime - 1,0,MAX_HIT_TIME);
    }

    @Override
    public float height() {
        return 70;
    }

    @Override
    public void hanldeBarEvent(int eventId, int timesSinned) {
        if (eventId == HIT_EVENT){
            this.hitTime = MAX_HIT_TIME;
            this.hitTimeO = MAX_HIT_TIME;
            this.lastSinnedTimes = timesSinned;
        }
    }

    private void particleExplosion(float x, float y){

        for (int i = 0; i < 30; i++){

            Vec3 initialSpeed = new Vec3(1f + r.nextFloat() * 1f,0,0).zRot(r.nextFloat() * FDMathUtil.FPI * 2);

            float red;
            float green;
            float blue;

            if (r.nextBoolean()){
                float col = r.nextFloat() * 0.1f + 0.2f;
                red = col;
                green = col;
                blue = col;
            }else{
                red = 0.9f + r.nextFloat() * 0.1f;
                green = 0.6f + r.nextFloat() * 0.2f;
                blue = 0.1f;
            }

            FDTexturedSParticle fdTexturedSParticle = FDTexturedSParticle.create(FDRenderUtil.ParticleRenderTypesS.TEXTURES_BLUR_ADDITIVE, BallParticle.LOCATION)
                    .setColor(red,green,blue,1f)
                    .sendToOverlay()
                    .setMaxQuadSize(5)
                    .setDefaultScaleOut()
                    .setLifetime(30)
                    .setPos(x + r.nextFloat() * 6 - 3f,y  + r.nextFloat() * 6 - 3f,true)
                    .setSpeed(initialSpeed.x,initialSpeed.y)
                    .setAcceleration(0,0.25f)
                    .sendToScreen();
        }

    }

}
