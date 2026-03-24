package com.finderfeed.fdbosses.content.entities.netzach.netzach_minigame;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.particles.SparkScreenParticle;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.FDClientHelpers;
import com.finderfeed.fdlib.systems.screen.screen_particles.FDScreenParticle;
import com.finderfeed.fdlib.systems.screen.screen_particles.FDTexturedSParticle;
import com.finderfeed.fdlib.systems.screen.screen_particles.ScreenParticleEngine;
import com.finderfeed.fdlib.systems.simple_screen.SimpleFDScreen;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.github.L_Ender.cataclysm.init.ModSounds;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

public class NetzachMinigameScreen extends SimpleFDScreen {

    //232 * 232
    public static final ResourceLocation GUI = FDBosses.location("textures/gui/netzach_minigame/netzach_minigame.png");

    //Hours * Minutes * angle
    public static float MAX_ROTATION = 12 * 360;

    private float currentRotation = 0;
    private float oldRotation = 0;

    private int rotationStrength = 0;
    private float rotating = 0;
    private float targetRotation = MAX_ROTATION / 2f;

    private static final int GAME_ENDING_TICK_TIME = 5;
    private int gameEndingTick = 0;

    private int tickerForStuff = 0;
    private int randomnessTicker = 0;
    private Random random = new Random();
    private RandomSource randomSource = new XoroshiroRandomSource(252345435L);
    private DeltaTracker.Timer timerForRandomness = new DeltaTracker.Timer(80,0,f->f);

    private ScreenParticleEngine screenParticleEngine = new ScreenParticleEngine();

    public NetzachMinigameScreen(){
        targetRotation = 1000;
    }

    @Override
    public void render(GuiGraphics graphics, int mx, int my, float pticks) {

        randomnessTicker += timerForRandomness.advanceTime(Util.getMillis(), true);

        pticks = FDRenderUtil.tryGetPartialTickIgnorePause();

        this.renderBlurredBackground(pticks);

        PoseStack matrices = graphics.pose();

        matrices.pushPose();

        this.gameEndShake(matrices, pticks,435, true);

        var anchor = this.getAnchor(0.5f,0.5f);
        float scale = 1;
        float guiSize = 232 * scale;

        FDRenderUtil.bindTexture(GUI);

        FDRenderUtil.blitWithBlendCentered(matrices, anchor.x, anchor.y, guiSize, guiSize, 0,0,232,232, 512,512,0,1f);

        float rotation = this.getCurrentRotation(pticks);

        this.screenParticleEngine.render(graphics, pticks);

        float hourRotation = rotation / 12;
        float hourArrowWidth = 20 * scale;
        float hourArrowWHeight = 80 * scale;
        float hourArrowOffset = 33 * scale;

        float minuteArrowWidth = 44 * scale;
        float minuteArrowWHeight = 114 * scale;
        float minuteArrowOffset = 35 * scale;

        matrices.translate(anchor.x, anchor.y, 0);


        matrices.pushPose();
        matrices.mulPose(Axis.ZP.rotationDegrees(targetRotation / 12));
        FDRenderUtil.blitWithBlendRgb(matrices, -hourArrowWidth / 2, -hourArrowOffset - hourArrowWHeight / 2, hourArrowWidth, hourArrowWHeight,232 + 20,114,20,80,512,512,0,0.25f, 1,0,0);
        matrices.popPose();

        matrices.pushPose();
        matrices.mulPose(Axis.ZP.rotationDegrees(targetRotation));
        FDRenderUtil.blitWithBlendRgb(matrices, -minuteArrowWidth / 2, -minuteArrowOffset - minuteArrowWHeight / 2, minuteArrowWidth, minuteArrowWHeight,232 + 44,0,44,114,512,512,0,0.25f,1,0,0);
        matrices.popPose();



        //20 80 hour arrow
        matrices.pushPose();
        matrices.mulPose(Axis.ZP.rotationDegrees(hourRotation));
        FDRenderUtil.blitWithBlendCentered(matrices, 0, -hourArrowOffset, hourArrowWidth, hourArrowWHeight,232,114,20,80,512,512,0,1);
        matrices.popPose();

        //44 114 minute arrow
        matrices.pushPose();
        matrices.mulPose(Axis.ZP.rotationDegrees(rotation));
        FDRenderUtil.blitWithBlendCentered(matrices, 0, -minuteArrowOffset, minuteArrowWidth, minuteArrowWHeight,232,0,44,114,512,512,0,1);
        matrices.popPose();


        matrices.popPose();

        float wholeOffset = -20;
        float keysOffset = 160;

        matrices.pushPose();
        int shiftLeft = 0;
        if (rotating < 0){
            shiftLeft = 58;
            this.gameEndShake(matrices, pticks, 5345,false);
        }else{
            this.gameEndShake(matrices, pticks, 5345,true);
        }
        FDRenderUtil.blitWithBlendCentered(matrices, anchor.x - keysOffset, anchor.y + wholeOffset, 58, 57, 396 + shiftLeft,0,58,57,512,512,0,1f);
        matrices.popPose();

        matrices.pushPose();
        int shiftRight = 0;
        if (rotating > 0){
            shiftRight = 58;
            this.gameEndShake(matrices, pticks, 8745,false);
        }else{
            this.gameEndShake(matrices, pticks, 8745,true);
        }
        FDRenderUtil.blitWithBlendCentered(matrices, anchor.x + keysOffset, anchor.y + wholeOffset, 58, 57, 396 + shiftRight,57,58,57,512,512,0,1f);
        matrices.popPose();


        int arrowsOffset = 57;
        //48 32
        matrices.pushPose();
        this.gameEndShake(matrices, pticks, 302,true);
        FDRenderUtil.blitWithBlendCentered(matrices, anchor.x + keysOffset, anchor.y + wholeOffset + arrowsOffset, 48,32, 348, 32, 48, 32, 512, 512,0, 1f);
        matrices.popPose();

        matrices.pushPose();
        this.gameEndShake(matrices, pticks, 1302,true);
        FDRenderUtil.blitWithBlendCentered(matrices, anchor.x - keysOffset, anchor.y + wholeOffset + arrowsOffset, 48,32, 348, 0, 48, 32, 512, 512,0, 1f);
        matrices.popPose();

    }

    private void gameEndShake(PoseStack matrices, float pticks, int seed, boolean doRotatingShake){
        if (gameEndingTick > 0) {

            float p = (float) Mth.clamp(GAME_ENDING_TICK_TIME - gameEndingTick + pticks, 0, GAME_ENDING_TICK_TIME) / GAME_ENDING_TICK_TIME;

            p = 1 - FDEasings.easeIn(p);

            randomSource.setSeed(randomnessTicker * 232L + seed);


            float ampl = 5f;
            Vec3 dir = new Vec3(ampl * p,0,0).zRot((float) (randomSource.nextGaussian() * FDMathUtil.FPI * 2));

//            float rx = ( random2.nextFloat() * ampl * 2 - ampl) * p;
//            float ry = ( random2.nextFloat() * ampl * 2 - ampl) * p;

            float rx = (float) dir.x;
            float ry = (float) dir.y;


            matrices.translate(
                    rx,
                    ry,
                    0
            );


        }else if (rotationStrength != 0 && doRotatingShake){
            float p = Math.abs(rotationStrength / 5f);

            randomSource.setSeed(randomnessTicker * 632L + seed);

            float ampl = 0.5f;
            float rx = ( randomSource.nextFloat() * ampl * 2 - ampl) * p;
            float ry = ( randomSource.nextFloat() * ampl * 2 - ampl) * p;

            matrices.translate(
                    rx,
                    ry,
                    0
            );

        }
    }


    @Override
    public boolean keyPressed(int keyCode, int p_96553_, int p_96554_) {
        if (keyCode == GLFW.GLFW_KEY_A || keyCode == GLFW.GLFW_KEY_LEFT){
            rotating = -1;
        }else if (keyCode == GLFW.GLFW_KEY_D || keyCode == GLFW.GLFW_KEY_RIGHT){
            rotating = 1;
        }
        return super.keyPressed(keyCode, p_96553_, p_96554_);
    }

    @Override
    public boolean keyReleased(int keyCode, int p_94716_, int p_94717_) {
        if (rotating < 0 && (keyCode == GLFW.GLFW_KEY_A || keyCode == GLFW.GLFW_KEY_LEFT) || rotating > 0 &&  (keyCode == GLFW.GLFW_KEY_D || keyCode == GLFW.GLFW_KEY_RIGHT)){
            rotating = 0;
        }
        return super.keyReleased(keyCode, p_94716_, p_94717_);
    }

    public float normalizeRotation(float rotation){

        rotation = rotation % MAX_ROTATION;

        if (rotation < 0){
            rotation = MAX_ROTATION + rotation;
        }

        return rotation;
    }

    public float getCurrentRotation(float pticks) {
        return FDMathUtil.lerp(oldRotation, currentRotation, pticks);
    }

    @Override
    public void tick() {
        super.tick();

        this.screenParticleEngine.tick();

        tickerForStuff++;

        gameEndingTick = Mth.clamp(gameEndingTick - 1,0,Integer.MAX_VALUE);

        this.tickRotation();


    }

    private void tickRotation(){

        if (rotationStrength != 0){
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.MACE_SMASH_GROUND_HEAVY, 1.5f, 0.05f));
        }

        int maxRotationStrengthTick = 5;
        if (rotating == 0){
            if (rotationStrength > 0){
                rotationStrength = Mth.clamp(rotationStrength - 2,0,maxRotationStrengthTick);
            }else{
                rotationStrength = Mth.clamp(rotationStrength + 2,-maxRotationStrengthTick,0);
            }
        }else{
            if (rotating > 0){
                this.rotationParticles(2, -FDMathUtil.FPI / 4, FDMathUtil.FPI / 6, 2f, 8f, 4,4);
                rotationStrength = Mth.clamp(rotationStrength + 1,-maxRotationStrengthTick,maxRotationStrengthTick);
            }else{
                this.rotationParticles(2, FDMathUtil.FPI / 4, FDMathUtil.FPI / 6, 2f, 8f, 4,4);
                rotationStrength = Mth.clamp(rotationStrength - 1,-maxRotationStrengthTick,maxRotationStrengthTick);
            }

        }
        float rotationStr = (float) rotationStrength / maxRotationStrengthTick;

        oldRotation = currentRotation;
        float rotationSpeed = 20;
        float nextRotation = currentRotation + rotationSpeed * rotationStr;

        float nrmrTarget = this.normalizeRotation(targetRotation);
        float nrmrCurrent = this.normalizeRotation(nextRotation);
        float nrmrOld = this.normalizeRotation(currentRotation);

        if (rotationStr != 0) {
            float rotation = nextRotation;
            if (rotationStr > 0) {
                if (nrmrCurrent >= nrmrTarget && (nrmrOld <= nrmrTarget || nrmrOld > nrmrCurrent)){
                    rotation = targetRotation;
                    oldRotation = rotation;
                    this.gameCompleted();
                }
            } else {
                if (nrmrOld >= nrmrTarget && (nrmrCurrent <= nrmrTarget || nrmrCurrent > nrmrOld)){
                    rotation = targetRotation;
                    oldRotation = rotation;
                    this.gameCompleted();
                }
            }
            currentRotation = rotation;
        }
    }

    public void rotationParticles(int count, float initialAngle, float radius, float minSpeed, float maxSpeed, int minLifetime, int maxLifetime){
        var anchor = this.getAnchor(0.5f,0.5f);
        for (int i = 0; i < count;i++) {
            float speed = minSpeed + random.nextFloat() * (maxSpeed - minSpeed);

            Vector3f v = new Vector3f(0, -speed * 2, 0).rotateZ(initialAngle + random.nextFloat() * radius * 2 - radius);

            int lifetime = minLifetime;
            if (maxLifetime != minLifetime){
                lifetime += random.nextInt(maxLifetime - minLifetime);
            }

            SparkScreenParticle sparkScreenParticle = new SparkScreenParticle(0.25f,1)
                    .setPos(anchor.x, anchor.y, true)
                    .setColor(1f,0.8f,0.1f,1f)
                    .setEndColor(1f,0.4f,0.1f,1f)
                    .setSpeed(v.x,v.y)
                    .setLifetime(lifetime)
                    .setAcceleration(0,2);
            screenParticleEngine.addParticle(sparkScreenParticle);

        }
    }

    public void gameCompleted(){
        targetRotation = this.targetRotation + BossUtil.randomPlusMinus() * (500 + FDClientHelpers.getClientLevel().random.nextFloat() * 500);
        this.rotationStrength = 0;
        this.gameEndingTick = GAME_ENDING_TICK_TIME;
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.MACE_SMASH_GROUND, 1f));

        this.rotationParticles(30, 0, FDMathUtil.FPI / 2, 2f, 10f, 4,10);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public float getScreenWidth() {
        return 0;
    }

    @Override
    public float getScreenHeight() {
        return 0;
    }

}
