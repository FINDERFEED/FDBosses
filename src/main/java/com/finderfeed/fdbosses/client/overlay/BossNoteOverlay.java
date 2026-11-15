package com.finderfeed.fdbosses.client.overlay;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.boss_screen.BaseBossScreen;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = FDBosses.MOD_ID, value = Dist.CLIENT)
public class BossNoteOverlay implements LayeredDraw.Layer {

    private static final ResourceLocation TEXTURE = FDBosses.location("textures/gui/boss_note.png");

    private static Component text;
    private static int age;
    private static int displayTime;

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event){
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null){
            text = null;
            age = 0;
            return;
        }
        if (text == null || minecraft.isPaused()) return;
        age++;
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker tracker){
        Minecraft minecraft = Minecraft.getInstance();
        if (text == null || minecraft.player == null) return;

        int textureWidth = 12;
        int textureHeight = 61;

        int staticWidth = 12;
        int staticHeight = 29;
        int staticDecorationHeight = 3;
        int staticTextureHeight = staticHeight + staticDecorationHeight;
        int movingWidth = 11;
        int movingHeight = 29;
        int movingOffset = 6;

        int maxWidth = 110;
        int padding = 2;
        int screenEdge = 5;
        int screenTop = 2;
        int screenHeight = 25;
        int offsetFromTop = 12;
        float defaultTextScale = 0.75f;

        int slideInTime = 5;
        int openTime = 12;
        int closeTime = 12;
        int slideOutTime = 12;
        int textAppearTime = 8;

        if (age >= slideInTime + openTime + textAppearTime + displayTime + closeTime + slideOutTime){
            text = null;
            return;
        }
        if (minecraft.options.hideGui) return;

        float time = age + tracker.getGameTimeDeltaPartialTick(false);
        float closeStart = slideInTime + openTime + textAppearTime + displayTime;
        float closing = Mth.clamp((time - closeStart) / closeTime, 0, 1);
        float slideOut = Mth.clamp((time - closeStart - closeTime) / slideOutTime, 0, 1);
        float slide = FDEasings.easeIn(Mth.clamp(time / slideInTime, 0, 1)) * (1 - FDEasings.easeOut(slideOut));
        float expansion = FDEasings.easeOut(Mth.clamp((time - slideInTime) / openTime, 0, 1)) * (1 - FDEasings.easeIn(closing));

        float width = Math.min(maxWidth, graphics.guiWidth() - staticWidth - movingOffset - 8);
        float openWidth = width + movingOffset;
        float staticX = graphics.guiWidth() - staticWidth * slide + movingOffset * (1 - slide);
        float movingX = staticX - width * expansion - movingOffset;
        float left = movingX + screenEdge;
        float screenWidth = staticX + screenEdge - left;

        FDRenderUtil.fill(graphics.pose(), left, offsetFromTop + screenTop, screenWidth, screenHeight, 0.3f, 0.2f, 0, 0.85f);

        FDRenderUtil.bindTexture(TEXTURE);
        FDRenderUtil.blitWithBlend(graphics.pose(), movingX, offsetFromTop, movingWidth, movingHeight, 0, staticTextureHeight, movingWidth, movingHeight, textureWidth, textureHeight, 0, 1);
        FDRenderUtil.blitWithBlend(graphics.pose(), staticX, offsetFromTop - staticDecorationHeight, staticWidth, staticTextureHeight, 0, 0, staticWidth, staticTextureHeight, textureWidth, textureHeight, 0, 1);

        float appear = Mth.clamp((time - slideInTime - openTime) / textAppearTime, 0, 1);
        int textAlpha = (int)(FDEasings.easeInOut(appear) * (1 - FDEasings.easeInOut(closing)) * 255);
        if (textAlpha < 4) return;

        float textLeft = staticX - openWidth + movingWidth + padding;
        float textRight = staticX - padding;
        float centerX = (textLeft + textRight) / 2;
        int textWidth = (int)((textRight - textLeft) / defaultTextScale);
        float clipLeft = movingX + movingWidth + padding;

        FDRenderUtil.Scissor.pushScissors(graphics.pose(), clipLeft, offsetFromTop + screenTop + padding, Math.max(0, textRight - clipLeft), screenHeight - padding * 2);

        FDRenderUtil.renderFullyCenteredText(graphics, centerX, offsetFromTop + screenTop + screenHeight / 2f, textWidth, defaultTextScale, true, (textAlpha << 24) | 0xff9462, text);

        FDRenderUtil.Scissor.popScissors();

    }

    public static void show(Component component){
        Component message = component.copy();
        text = message;
        age = 0;
        displayTime = (int) Math.ceil(message.getString().length() * 2.5f);
    }

}
