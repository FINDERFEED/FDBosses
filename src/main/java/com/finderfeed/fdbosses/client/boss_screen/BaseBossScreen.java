package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdbosses.FDBosses;
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


        PoseStack matrices = graphics.pose();

        Vector2f anchorEnd = this.getAnchor(1f,1f);

        FDRenderUtil.fill(matrices,0,0,anchorEnd.x,anchorEnd.y,0f,0f,0f,0.6f);

        this.renderBoss(graphics,mx,my,pticks);

        this.renderBack(graphics,mx,my,pticks);

        for (Renderable renderable : this.renderables) {
            renderable.render(graphics, mx, my, pticks);
        }

        this.renderFront(graphics,mx,my,pticks);
    }



    protected void renderBack(GuiGraphics graphics, float mx, float my, float pticks){

        PoseStack matrices = graphics.pose();

        Vector2f anchorEnd = this.getAnchor(1f,1f);

        //----------------------------backgrounds------------------------------------------
        Vector2f anchor = this.getAnchor(1f,0f);

        float bossInfoWidth = 237;
        float bossInfoHeight = 125;

        float bossInfoXs = anchor.x - bossInfoWidth;
        float bossInfoYs = 10;






        FDRenderUtil.bindTexture(FDBosses.location("textures/gui/boss_detail.png"));
        FDRenderUtil.blitWithBlend(matrices, bossInfoXs,bossInfoYs,bossInfoWidth,bossInfoHeight,0,0,1f,1f,1f,1f,0,1f);


        //----------------------------boss name-----------------------------------------------
        Component bossName = this.getBossName();

        float bossNameScale = 2f;
        float bossNameWidth = font.width(bossName) * bossNameScale;
        float bossNameHeight = font.lineHeight * bossNameScale;
        float bossNameOffsetX = bossInfoWidth/2 - bossNameWidth / 2;
        float bossNameOffsetY = 35;
        float gradientOffset = 2;
        float gradientWidthAddition = 20;
        float r = ((this.getBaseStringColor() & 0xff0000) >> 16) / 255f;
        float g = ((this.getBaseStringColor() & 0x00ff00) >> 8) / 255f;
        float b = ((this.getBaseStringColor() & 0x0000ff)) / 255f;


        FDRenderUtil.fill(matrices,bossInfoXs + bossNameOffsetX - gradientOffset + bossNameWidth/2,bossInfoYs + bossNameOffsetY - gradientOffset, bossNameWidth + gradientOffset * 2 + gradientWidthAddition - bossNameWidth/2,bossNameHeight + gradientOffset * 2,
                r,g,b,0.3f,
                r,g,b,0f,
                r,g,b,0f,
                r,g,b,0.3f
        );

        FDRenderUtil.fill(matrices,bossInfoXs + bossNameOffsetX - gradientOffset * 2 - gradientWidthAddition,bossInfoYs + bossNameOffsetY - gradientOffset, bossNameWidth / 2 + gradientWidthAddition + gradientOffset,bossNameHeight + gradientOffset * 2,
                r,g,b,0f,
                r,g,b,0.3f,
                r,g,b,0.3f,
                r,g,b,0f
        );

        FDRenderUtil.renderScaledText(graphics,bossName,bossInfoXs + bossNameOffsetX,bossInfoYs + bossNameOffsetY,bossNameScale,true,this.getBaseStringColor());




        //----------------------------abilities-----------------------------------------------

        float bossAbilitiesHeight = 114;
        float bossAbilitiesWidth = 237;


        float bossAbilitiesXs = anchor.x - bossInfoWidth;
        float bossAbilitiesYs = anchorEnd.y - bossAbilitiesHeight - 10;

        FDRenderUtil.bindTexture(FDBosses.location("textures/gui/boss_abilities.png"));
        FDRenderUtil.blitWithBlend(matrices, bossAbilitiesXs,bossAbilitiesYs,bossAbilitiesWidth,bossAbilitiesHeight,0,0,1f,1f,1f,1f,0,1f);

        Component abilities = Component.literal("Abilities");
        float abilitieswidth = font.width(abilities);

        FDRenderUtil.renderScaledText(graphics,abilities, bossAbilitiesXs + bossAbilitiesWidth / 2 - abilitieswidth / 2 - 3,bossAbilitiesYs + 7,1f,true,this.getBaseStringColor());

    }



    protected void renderFront(GuiGraphics graphics, float mx, float my, float pticks){

    }

    public abstract Component getBossName();

    public abstract int getBaseStringColor();

    protected abstract void renderBoss(GuiGraphics graphics, float mx, float my, float pticks);

}
