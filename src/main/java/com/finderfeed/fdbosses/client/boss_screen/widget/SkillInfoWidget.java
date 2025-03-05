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
import net.minecraft.resources.ResourceLocation;

public class SkillInfoWidget extends FDWidget {

    public static final ResourceLocation MIDDLE = FDBosses.location("textures/gui/ability_part_middle.png");
    public static final ResourceLocation DOWN = FDBosses.location("textures/gui/ability_part_down.png");
    public static final ResourceLocation UP = FDBosses.location("textures/gui/ability_part_up.png");

    private Component skillName = Component.empty();
    private int color = 0xffffff;

    public SkillInfoWidget(Screen screen, float x, float y, float width, float height, Component skillName, int color) {
        super(screen, x, y, width, height);
        this.skillName = skillName;
        this.color = color;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, float mx, float my, float pticks) {

        PoseStack matrices = guiGraphics.pose();

        //up + down
        float cumulativeWidth = 52 + 53;

        FDRenderUtil.bindTexture(UP);
        FDRenderUtil.blitWithBlend(matrices,this.getX(),this.getY(),this.getWidth(),52,0,0,1,1,1,1,0,1f);

        float middleHeight = Math.max(0,this.getHeight() - cumulativeWidth);


        if (middleHeight != 0){
            FDRenderUtil.bindTexture(MIDDLE);
            FDRenderUtil.blitWithBlend(matrices,this.getX(),this.getY() + 52,this.getWidth(),middleHeight,0,0,1,1,1,1,0,1f);
        }


        FDRenderUtil.bindTexture(DOWN);
        FDRenderUtil.blitWithBlend(matrices,this.getX(),this.getY() + 52 + middleHeight,this.getWidth(),53,0,0,1,1,1,1,0,1f);

        float scale = 1.5f;
        Font font = Minecraft.getInstance().font;
        float textWidth = font.width(skillName) * scale;

        float textX = this.getX() + this.getWidth() / 2 + 7f - textWidth / 2;
        float textY = 58;

        float r = (color & 0xff0000) >> 16; r /= 255f;
        float g = (color & 0x00ff00) >> 8; g /= 255f;
        float b = (color & 0x0000ff); b /= 255f;

        FDRenderUtil.fill(guiGraphics.pose(),textX - 20,textY - 2,textWidth / 2 + 20,font.lineHeight * scale + 3,
                r,g,b,0f,
                r,g,b,0.3f,
                r,g,b,0.3f,
                r,g,b,0f
        );

        FDRenderUtil.fill(guiGraphics.pose(),textX + textWidth / 2,textY - 2,textWidth / 2 + 20,font.lineHeight * scale + 3,
                r,g,b,0.3f,
                r,g,b,0f,
                r,g,b,0f,
                r,g,b,0.3f
        );

        FDRenderUtil.renderScaledText(guiGraphics,skillName,textX, textY, scale, true, color);

    }


    public void setSkillName(Component skillName, int color) {
        this.skillName = skillName;
        this.color = color;
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
