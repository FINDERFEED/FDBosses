package com.finderfeed.fdbosses.client;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;

public class BossRenderUtil {

    public static final ResourceLocation BOSS_TOOLTIP = FDBosses.location("textures/gui/boss_screen_tooltip.png");


    public static Vector2f renderBossScreenTooltip(GuiGraphics graphics, Component component,float x,float y, float desiredTextWidth,int textColor, float textScale) {
        Window window = Minecraft.getInstance().getWindow();

        return renderBossScreenTooltip(graphics,component,x,y,desiredTextWidth,textColor,textScale,0,0, window.getGuiScaledWidth(), window.getGuiScaledHeight());
    }

    public static Vector2f renderBossScreenTooltip(GuiGraphics graphics, Component component,float x,float y, float desiredTextWidth, int textColor, float textScale,float borderXStart, float borderYStart, float borderXEnd, float borderYEnd) {
        Font font = Minecraft.getInstance().font;

        float lineHeight = font.lineHeight * textScale;

        List<FormattedText> charSequences = font.getSplitter().splitLines(component, Math.round(desiredTextWidth / textScale), Style.EMPTY);

        float height = charSequences.size() * lineHeight;

        float width = 0;

        for (FormattedText sequence : charSequences) {
            width = Math.max(font.width(sequence),width);
        }

        Vector2f v = renderBossScreenTooltip(graphics.pose(), x, y, width + lineHeight, height + lineHeight, borderXStart, borderYStart, borderXEnd, borderYEnd);

        int index = 0;
        for (FormattedText sequence : charSequences) {

            FDRenderUtil.renderScaledText(graphics, Language.getInstance().getVisualOrder(sequence), v.x + lineHeight / 2, v.y + index * lineHeight + lineHeight / 2, textScale, true, textColor);

            index++;
        }
        return v;
    }

    /**
     * Returns where the content should be rendered
     */
    public static Vector2f renderBossScreenTooltip(PoseStack matrices, float x, float y, float inWidth, float inHeight){
        Window window = Minecraft.getInstance().getWindow();
        return renderBossScreenTooltip(matrices,x,y,inWidth,inHeight,0,0,window.getGuiScaledWidth(),window.getGuiScaledHeight());
    }


    public static Vector2f renderBossScreenTooltip(PoseStack matrices, float x, float y, float inWidth, float inHeight, float borderXStart, float borderYStart, float borderXEnd, float borderYEnd){
        FDRenderUtil.bindTexture(BOSS_TOOLTIP);

        float edgeWidth = 7;
        float edgeHeight = 7;
        float texWidth = 16;
        float texHeight = 16;

        float xRenderStart = Mth.clamp(x,borderXStart,borderXEnd - inWidth - edgeWidth * 2);
        float yRenderStart = Mth.clamp(y,borderYStart,borderYEnd - inHeight - edgeHeight * 2);


        //corners
        FDRenderUtil.blitWithBlend(matrices, xRenderStart, yRenderStart, edgeWidth,edgeHeight, 0,0,edgeWidth,edgeHeight,texWidth,texHeight,0,1f);

        FDRenderUtil.blitWithBlend(matrices, xRenderStart + inWidth + edgeWidth, yRenderStart, edgeWidth,edgeHeight, edgeWidth,0,edgeWidth,edgeHeight,texWidth,texHeight,0,1f);

        FDRenderUtil.blitWithBlend(matrices, xRenderStart, yRenderStart + inHeight + edgeHeight, edgeWidth,edgeHeight, 0,edgeHeight,edgeWidth,edgeHeight,texWidth,texHeight,0,1f);

        FDRenderUtil.blitWithBlend(matrices, xRenderStart + inWidth + edgeWidth, yRenderStart + inHeight + edgeHeight, edgeWidth,edgeHeight, edgeWidth,edgeHeight,edgeWidth,edgeHeight,texWidth,texHeight,0,1f);


        //borders
        FDRenderUtil.blitWithBlend(matrices,xRenderStart + edgeWidth, yRenderStart, inWidth, 7, 14,0,1,7,texWidth,texHeight,0,1f);

        FDRenderUtil.blitWithBlend(matrices,xRenderStart + edgeWidth, yRenderStart + inHeight + edgeHeight, inWidth, 7, 15,0,1,7,texWidth,texHeight,0,1f);

        FDRenderUtil.blitWithBlend(matrices, xRenderStart, yRenderStart + edgeHeight,7, inHeight, 0,14,7,1,texWidth,texHeight,0,1f);

        FDRenderUtil.blitWithBlend(matrices, xRenderStart + edgeWidth + inWidth, yRenderStart + edgeHeight,7, inHeight, 0,15,7,1,texWidth,texHeight,0,1f);


        //innards
        FDRenderUtil.blitWithBlend(matrices,xRenderStart + edgeWidth,yRenderStart + edgeHeight, inWidth,inHeight,15,0,1,1,texWidth,texHeight,0,1f);


        Vector2f toReturn = new Vector2f(xRenderStart + edgeWidth, yRenderStart + edgeHeight);

        return toReturn;
    }

}
