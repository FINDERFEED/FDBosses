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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class BossRenderUtil {

    public static final ResourceLocation BOSS_TOOLTIP = FDBosses.location("textures/gui/boss_screen_tooltip.png");

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

        float xRenderStart = Mth.clamp(x,borderXStart,borderXEnd);
        float yRenderStart = Mth.clamp(y,borderYStart,borderYEnd);


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
