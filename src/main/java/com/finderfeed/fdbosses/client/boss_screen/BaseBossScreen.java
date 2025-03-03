package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdlib.systems.simple_screen.SimpleFDScreen;
import com.finderfeed.fdlib.util.FDUtil;
import com.finderfeed.fdlib.util.InterpolatedValue;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.joml.Vector2f;

public abstract class BaseBossScreen extends SimpleFDScreen {

    public BaseBossScreen() {
        super();
    }

    @Override
    protected void init() {
        super.init();



    }

    @Override
    public void render(GuiGraphics graphics, int mx, int my, float pticks) {


        this.renderBlurredBackground(pticks);


        this.renderBoss(graphics,mx,my,pticks);

        this.renderBack(graphics,mx,my,pticks);

        for (Renderable renderable : this.renderables) {
            renderable.render(graphics, mx, my, pticks);
        }

        this.renderFront(graphics,mx,my,pticks);
    }



    protected void renderBack(GuiGraphics graphics, float mx, float my, float pticks){

        //----------------------------backgrounds------------------------------------------
        Vector2f anchor = this.getAnchor(0.5f,0f);
        Vector2f anchorEnd = this.getAnchor(1f,1f);

        float offs = 5;

        float xs = anchor.x;
        float ys = anchor.y + offs;

        float width = anchorEnd.x - offs - xs;
        float height = anchorEnd.y - offs - ys;

        PoseStack matrices = graphics.pose();

        FDRenderUtil.fill(matrices,xs,ys,width,height,0.1f,0.1f,0.3f,1f);


        //----------------------------boss name-----------------------------------------------
        Component bossName = this.getBossName();

        float bossNameScale = 2.5f;
        float bossNameWidth = font.width(bossName) * bossNameScale;
        float bossNameHeight = font.lineHeight * bossNameScale;
        float bossNameOffset = 10;
        float gradientOffset = 2;
        float gradientWidthAddition = 20;
        float r = ((this.getBaseStringColor() & 0xff0000) >> 16) / 255f;
        float g = ((this.getBaseStringColor() & 0x00ff00) >> 8) / 255f;
        float b = ((this.getBaseStringColor() & 0x0000ff)) / 255f;


        FDRenderUtil.fill(matrices,xs + bossNameOffset - gradientOffset,ys + bossNameOffset - gradientOffset, bossNameWidth + gradientOffset * 2 + gradientWidthAddition,bossNameHeight + gradientOffset * 2,
                r,g,b,0.5f,
                r,g,b,0f,
                r,g,b,0f,
                r,g,b,0.5f
        );

        FDRenderUtil.renderScaledText(graphics,bossName,xs + bossNameOffset,ys + bossNameOffset,bossNameScale,true,this.getBaseStringColor());

    }



    protected void renderFront(GuiGraphics graphics, float mx, float my, float pticks){

    }

    public abstract Component getBossName();

    public abstract int getBaseStringColor();

    protected abstract void renderBoss(GuiGraphics graphics, float mx, float my, float pticks);

}
