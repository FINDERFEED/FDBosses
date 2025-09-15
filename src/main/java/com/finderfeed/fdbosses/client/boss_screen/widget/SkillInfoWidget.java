package com.finderfeed.fdbosses.client.boss_screen.widget;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.simple_screen.FDWidget;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SkillInfoWidget extends FDWidget {

    public static final ResourceLocation MIDDLE = FDBosses.location("textures/gui/ability_part_middle.png");
    public static final ResourceLocation DOWN = FDBosses.location("textures/gui/ability_part_down.png");
    public static final ResourceLocation UP = FDBosses.location("textures/gui/ability_part_up.png");

    private Component skillName = Component.empty();
    private int color = 0xffffff;
    private Either<ResourceLocation, ItemStack> skillImage;

    public SkillInfoWidget(Screen screen, float x, float y, float width, float height, Component skillName, int color) {
        super(screen, x, y, width, height);
        this.skillName = skillName;
        this.color = color;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, float mx, float my, float pticks) {

        PoseStack matrices = guiGraphics.pose();

        //up + down
        float cumulativeHeight = 103 + 53;

        FDRenderUtil.bindTexture(UP);
        FDRenderUtil.blitWithBlend(matrices,this.getX(),this.getY(),this.getWidth(),103,0,0,1,1,1,1,0,1f);

        float middleHeight = Math.max(0,this.getHeight() - cumulativeHeight);


        if (middleHeight != 0){
            FDRenderUtil.bindTexture(MIDDLE);
            FDRenderUtil.blitWithBlend(matrices,this.getX(),this.getY() + 103,this.getWidth(),middleHeight,0,0,1,1,1,1,0,1f);
        }


        FDRenderUtil.bindTexture(DOWN);
        FDRenderUtil.blitWithBlend(matrices,this.getX(),this.getY() + 103 + middleHeight,this.getWidth(),53,0,0,1,1,1,1,0,1f);

        //skill name
        float scale = 1.5f;
        Font font = Minecraft.getInstance().font;
        float textWidth = font.width(skillName) * scale;
        float textX = this.getX() + this.getWidth() / 2 + 7f - textWidth / 2;
        float textY = 61;

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


        //skill image
        if (skillImage != null) {
            var item = skillImage.right();
            var rl = skillImage.left();

            float offs = 20;
            if (rl.isPresent()) {
                FDRenderUtil.bindTexture(rl.get());
                FDRenderUtil.blitWithBlend(matrices, this.getX() + 97f, this.getY() + offs + 7, 16, 16,
                        0, 0, 1, 1, 1, 1, 0, 1f);
            }else if (item.isPresent()){
                FDRenderUtil.renderScaledItemStack(guiGraphics, this.getX() + 97f, this.getY() + offs + 7, 1f, item.get());
            }
        }

        Component close = Component.translatable("fdbosses.close_skill_info");
        FDRenderUtil.renderScaledText(guiGraphics,close,
                this.getX() + this.getWidth() / 2 - font.width(close) * 0.25f + 6,
                this.getY() + this.getHeight() - 21,
                0.5f,
                true,
                color
        );

    }


    public void setSkillName(Component skillName, int color) {
        this.skillName = skillName;
        this.color = color;
    }

    public void setSkillImage(Either<ResourceLocation, ItemStack> location){
        this.skillImage = location;
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
    public boolean onMouseScroll(float v, float v1, float v3) {
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
