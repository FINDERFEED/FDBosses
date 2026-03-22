package com.finderfeed.fdbosses.content.entities.netzach.netzach_minigame;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.simple_screen.SimpleFDScreen;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

public class NetzachMinigameScreen extends SimpleFDScreen {

    //232 * 232
    public static final ResourceLocation GUI = FDBosses.location("textures/gui/netzach_minigame/netzach_minigame.png");

    //Hours * Minutes * angle
    public static float MAX_ROTATION = 12 * 60 * 360;

    private float currentRotation = 0;
    private float oldRotation = 0;

    private int rotationStrength = 0;

    private float rotating = 0;

    public NetzachMinigameScreen(){

    }

    @Override
    public void render(GuiGraphics graphics, int mx, int my, float pticks) {
        this.renderBlurredBackground(pticks);

        PoseStack matrices = graphics.pose();

        var anchor = this.getAnchor(0.5f,0.5f);

        var window = Minecraft.getInstance().getWindow();

        float scale = 1;

        float guiSize = 232 * scale;

        FDRenderUtil.bindTexture(GUI);

        FDRenderUtil.blitWithBlendCentered(matrices, anchor.x, anchor.y, guiSize, guiSize, 0,0,232,232, 512,512,0,1f);



        float rotation = this.getCurrentRotation(FDRenderUtil.tryGetPartialTickIgnorePause());



        //20 80 hour arrow
        float hourRotation = rotation / 12;
        float hourArrowWidth = 20 * scale;
        float hourArrowWHeight = 80 * scale;
        float hourArrowOffset = 33 * scale;

        matrices.pushPose();
        matrices.translate(anchor.x, anchor.y, 0);
        matrices.mulPose(Axis.ZP.rotationDegrees(hourRotation));
        FDRenderUtil.blitWithBlendCentered(matrices, 0, -hourArrowOffset, hourArrowWidth, hourArrowWHeight,232,114,20,80,512,512,0,1);
        matrices.popPose();


        //44 114 minute arrow
        float minuteArrowWidth = 44 * scale;
        float minuteArrowWHeight = 114 * scale;
        float minuteArrowOffset = 35 * scale;

        matrices.pushPose();
        matrices.translate(anchor.x, anchor.y, 0);
        matrices.mulPose(Axis.ZP.rotationDegrees(rotation));
        FDRenderUtil.blitWithBlendCentered(matrices, 0, -minuteArrowOffset, minuteArrowWidth, minuteArrowWHeight,232,0,44,114,512,512,0,1);
        matrices.popPose();




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
            rotation = MAX_ROTATION - rotation;
        }

        return rotation;
    }

    public float getCurrentRotation(float pticks) {
        return FDMathUtil.lerp(oldRotation, currentRotation, pticks);
    }

    @Override
    public void tick() {
        super.tick();

        int maxRotationStrengthTick = 5;
        if (rotating == 0){
            if (rotationStrength > 0){
                rotationStrength = Mth.clamp(rotationStrength - 2,0,maxRotationStrengthTick);
            }else{
                rotationStrength = Mth.clamp(rotationStrength + 2,-maxRotationStrengthTick,0);
            }
        }else{
            if (rotating > 0){
                rotationStrength = Mth.clamp(rotationStrength + 1,-maxRotationStrengthTick,maxRotationStrengthTick);
            }else{
                rotationStrength = Mth.clamp(rotationStrength - 1,-maxRotationStrengthTick,maxRotationStrengthTick);
            }

        }
        float rotationStr = (float) rotationStrength / maxRotationStrengthTick;

        oldRotation = currentRotation;

        float rotationSpeed = 20;
        currentRotation += rotationSpeed * rotationStr;

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
