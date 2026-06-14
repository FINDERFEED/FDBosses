package com.finderfeed.fdbosses.client.boss_codex;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.simple_screen.FDWidget;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.FDButton;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class StarButton extends FDWidget {

    public static final ResourceLocation STAR = FDBosses.location("textures/gui/star/star.png");

    private int currentFrame;
    private int tick = 0;
    private float startingAngle = 0;

    private int activationTime;

    private Random random;

    public StarButton(Screen screen, float x, float y, float width, float height, int startFrame, float startingAngle, int activationTime) {
        super(screen, x, y, width, height);
        this.currentFrame = Mth.clamp(startFrame,0,10);
        this.startingAngle = startingAngle;
        this.activationTime = activationTime;
        this.random = new Random();
    }

    @Override
    public void tick() {
        super.tick();
        if (tick >= activationTime) {
            BossCodexScreen bossCodexScreen = (BossCodexScreen) this.widgetOwner;
            var particleEngine = bossCodexScreen.screenParticleEngine;
            if (tick == activationTime) {



            }


            if (tick % 2 == 0) {
                float speed = random.nextFloat() * 0.5f + 0.5f;
                Vec3 rnd = new Vec3(speed,0,0).zRot(random.nextFloat() * FDMathUtil.FPI * 2);

                FlashyColoredQuadParticle flashyColoredQuadParticle = new FlashyColoredQuadParticle()
                        .setPos(this.getX(),this.getY(), true)
                        .setColor(1f,1f,0.25f,1f)
                        .setQuadSize(0.5f)
                        .setFlashFrequency(0.75f)
                        .setSpeed(rnd.x,rnd.y)
                        .setFriction(0.95f)
                        .setLifetime(40);

                particleEngine.addParticle(flashyColoredQuadParticle);
            }
        }
        tick++;
        if (tick % 2 == 0){
            currentFrame = (currentFrame + 1) % 11;
        }
    }

    public boolean isActivated(){
        return this.tick > activationTime;
    }


    @Override
    public void renderWidget(GuiGraphics guiGraphics, float v, float v1, float v2) {

        FDRenderUtil.bindTexture(STAR);

        PoseStack matrices = guiGraphics.pose();

        matrices.pushPose();

        matrices.translate(this.getX(),this.getY(), 0);


            if (this.tick > activationTime) {

                float t = tick + FDRenderUtil.tryGetPartialTickIgnorePause();

                if (startingAngle > 0) {
                    matrices.mulPose(Axis.ZP.rotationDegrees(t + startingAngle));
                } else {
                    matrices.mulPose(Axis.ZP.rotationDegrees(-t + startingAngle));
                }

                FDRenderUtil.blitWithBlendCentered(guiGraphics.pose(),
                        0, 0,
                        64, 64,
                        0, currentFrame,
                        1, 1,
                        1, 11,
                        0, 1);

            } else {
                FDRenderUtil.blitWithBlendRgb(guiGraphics.pose(),
                        -32, -32,
                        64, 64,
                        0, 0,
                        1, 1, 1, 11,
                        0, 1, 0.25f, 0.25f, 0.25f);
            }


        matrices.popPose();
    }

    @Override
    public boolean onMouseClick(float v, float v1, int i) {
        System.out.println("Zhopa");
        return true;
    }

    @Override
    public boolean onMouseRelease(float v, float v1, int i) {
        return false;
    }

    @Override
    public boolean onMouseScroll(float v, float v1, float v2, float v3) {
        return false;
    }

    @Override
    public boolean onCharTyped(char c, int i) {
        return false;
    }

    @Override
    public boolean onKeyPress(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean onKeyRelease(int i, int i1, int i2) {
        return false;
    }

}
