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

public class BossAbilitesWidget extends FDScrollableWidget {

    private int stringColor;
    private float maxScroll = 0;

    public BossAbilitesWidget(Screen screen, float x, float y, int stringColor) {
        super(screen, x, y, 237,114);
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

        Component abilities = Component.translatable("fdbosses.word.abilities");
        float abilitieswidth = font.width(abilities);

        FDRenderUtil.renderScaledText(guiGraphics,abilities, bossAbilitiesXs + this.getWidth() / 2 - abilitieswidth / 2 - 3,bossAbilitiesYs + 7,1f,true,stringColor);

        float p = this.getCurrentScroll() / this.getMaxScroll();
        if (Float.isNaN(p)){
            p = 0;
        }

        //5 8
        FDRenderUtil.bindTexture(FDBosses.location("textures/gui/scroller.png"));
        FDRenderUtil.blitWithBlend(matrices, this.getX() + 211, this.getY() + 28 + p * 62,5,8,0,0,1,1,1,1,0,1);

    }

    @Override
    public void addChild(String id, FDWidget widget) {
        super.addChild(id, widget);

        //74

        maxScroll = Math.max(maxScroll,widget.getY() - (this.getY() + 27) + widget.getHeight() + 5);

    }

    @Override
    public void useScissor() {
        FDRenderUtil.Scissor.pushScissors(this.getX(),this.getY() + 26,this.getWidth(),this.getHeight() - 40);
    }

    @Override
    public void endScissor() {
        FDRenderUtil.Scissor.popScissors();
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

    @Override
    public float getMaxScroll() {
        return Math.max(maxScroll - 74, 0);
    }

    @Override
    public void onScroll(float v) {
        this.moveChildren(0,-v);
    }

    @Override
    public float scrollAmount() {
        return 5;
    }
}
