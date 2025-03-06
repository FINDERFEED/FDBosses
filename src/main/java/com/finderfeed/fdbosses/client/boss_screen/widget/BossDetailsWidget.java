package com.finderfeed.fdbosses.client.boss_screen.widget;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.simple_screen.FDWidget;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BossDetailsWidget extends FDWidget {

    private Component bossName = Component.empty();

    private int textColor = 0xffffff;

    public BossDetailsWidget(Screen screen, float x, float y, float width, float height) {
        super(screen, x, y, width, height);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, float v, float v1, float v2) {
        PoseStack matrices = guiGraphics.pose();
        FDRenderUtil.bindTexture(FDBosses.location("textures/gui/boss_detail.png"));
        FDRenderUtil.blitWithBlend(matrices, this.getX(),this.getY(),this.getWidth(),this.getHeight(),0,0,1f,1f,1f,1f,0,1f);


        Font font = Minecraft.getInstance().font;

        float bossNameScale = 2f;
        float bossNameWidth = font.width(bossName) * bossNameScale;
        float bossNameHeight = font.lineHeight * bossNameScale;
        float bossNameOffsetX = this.getWidth()/2 - bossNameWidth / 2;
        float bossNameOffsetY = 35;
        float gradientOffset = 2;
        float gradientWidthAddition = 20;
        float r = ((textColor & 0xff0000) >> 16) / 255f;
        float g = ((textColor & 0x00ff00) >> 8) / 255f;
        float b = ((textColor & 0x0000ff)) / 255f;


        FDRenderUtil.fill(matrices,this.getX() + bossNameOffsetX - gradientOffset + bossNameWidth/2,this.getY() + bossNameOffsetY - gradientOffset, bossNameWidth + gradientOffset * 2 + gradientWidthAddition - bossNameWidth/2,bossNameHeight + gradientOffset * 2,
                r,g,b,0.3f,
                r,g,b,0f,
                r,g,b,0f,
                r,g,b,0.3f
        );

        FDRenderUtil.fill(matrices,this.getX() + bossNameOffsetX - gradientOffset * 2 - gradientWidthAddition,this.getY() + bossNameOffsetY - gradientOffset, bossNameWidth / 2 + gradientWidthAddition + gradientOffset,bossNameHeight + gradientOffset * 2,
                r,g,b,0f,
                r,g,b,0.3f,
                r,g,b,0.3f,
                r,g,b,0f
        );

        FDRenderUtil.renderScaledText(guiGraphics,bossName,this.getX() + bossNameOffsetX,this.getY() + bossNameOffsetY,bossNameScale,true,textColor);

    }

    public BossDetailsWidget setBossName(Component bossName, int textColor){
        this.bossName = bossName;
        this.textColor = textColor;
        return this;
    }

    @Override
    public boolean onMouseClick(float v, float v1, int i) {
        return false;
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
