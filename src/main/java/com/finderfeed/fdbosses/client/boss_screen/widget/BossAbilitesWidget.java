package com.finderfeed.fdbosses.client.boss_screen.widget;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.simple_screen.FDScrollableWidget;
import com.finderfeed.fdlib.systems.simple_screen.FDWidget;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BossAbilitesWidget extends FDWidget {

    private int stringColor;
    public BossAbilitiesButtonContainer bossAbilitiesButtonContainer;

    private int tickCount = 0;

    public BossAbilitesWidget(Screen screen, float x, float y, int stringColor) {
        super(screen, x, y, 231,120);
        bossAbilitiesButtonContainer = new BossAbilitiesButtonContainer(screen, 14,26,195,74);
        this.addChild("infoContainer",bossAbilitiesButtonContainer);
        this.stringColor = stringColor;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, float mx, float my, float pticks) {

        PoseStack matrices = guiGraphics.pose();

        Font font = Minecraft.getInstance().font;


        float bossAbilitiesXs = this.getX();
        float bossAbilitiesYs = this.getY();

        FDRenderUtil.bindTexture(FDBosses.location("textures/gui/boss_abilities.png"));
        FDRenderUtil.blitWithBlend(matrices, bossAbilitiesXs,bossAbilitiesYs,this.getWidth(),this.getHeight(),0,0,1f,1f,1f,1f,0,1f);

        float p = bossAbilitiesButtonContainer.getCurrentScroll() / bossAbilitiesButtonContainer.getMaxScroll();
        if (Float.isNaN(p)){
            p = 0;
        }

        //5 8
        FDRenderUtil.bindTexture(FDBosses.location("textures/gui/scroller.png"));
        FDRenderUtil.blitWithBlend(matrices, this.getX() + 211, this.getY() + 28 + p * 62,5,8,0,0,1,1,1,1,0,1);

        if (this.bossAbilitiesButtonContainer.getChildren().size() > 10 && this.bossAbilitiesButtonContainer.getCurrentScroll() == 0){

            float time = tickCount + pticks;
            float offsetY = (float) Math.sin(time / 2f);

            this.renderArrow(matrices, 15,95 + offsetY);

        }

    }

    private void renderArrow(PoseStack matrices, float offsetX, float offsetY){
        FDRenderUtil.fill(matrices, this.getX() + offsetX, this.getY() + offsetY, 1,1,1f,1f,1f,1f);
        FDRenderUtil.fill(matrices, this.getX() + offsetX + 1, this.getY() + offsetY + 1, 1,1,1f,1f,1f,1f);
        FDRenderUtil.fill(matrices, this.getX() + offsetX + 2, this.getY() + offsetY, 1,1,1f,1f,1f,1f);

    }

    @Override
    public void tick() {
        super.tick();
        tickCount++;
    }

    @Override
    public boolean onMouseClick(float v, float v1, int i) {
        return true;
    }

    @Override
    public boolean onMouseRelease(float v, float v1, int i) {
        return false;
    }

    @Override
    public boolean onMouseScroll(float v, float v1, float v2, float v3) {
        return true;
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
