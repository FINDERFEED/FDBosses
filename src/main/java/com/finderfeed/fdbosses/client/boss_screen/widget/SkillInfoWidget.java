package com.finderfeed.fdbosses.client.boss_screen.widget;

import com.finderfeed.fdlib.systems.simple_screen.FDWidget;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class SkillInfoWidget extends FDWidget {



    public SkillInfoWidget(Screen screen, float x, float y, float width, float height) {
        super(screen, x, y, width, height);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, float mx, float my, float pticks) {

        PoseStack matrices = guiGraphics.pose();

        FDRenderUtil.fill(matrices,this.getX(),this.getY(),this.getWidth(),this.getHeight(),1f,1f,1f,1f);


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
