package com.finderfeed.fdbosses.client.overlay;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.BossRenderUtil;
import com.finderfeed.fdbosses.content.boss_notes.BossNote;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = FDBosses.MOD_ID, value = Dist.CLIENT)
public class BossNoteOverlay implements LayeredDraw.Layer {

    private static Component text;
    private static BossNote.Boss boss;
    private static int age;
    private static int displayTime;

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event){
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null){
            text = null;
            boss = null;
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

        int textureWidth = 174;
        int textureHeight = 87;
        int frameSize = 29;
        int fillTextureWidth = textureWidth;
        int frameCount = 5;
        int frameTime = 1;

        int maxWidth = 180;
        int padding = 2;
        int offsetFromBottom = 70;
        float textScale = 0.75f;

        int iconTime = frameCount * frameTime;
        int openTime = 12;
        int closeTime = 12;
        int textAppearTime = 4;
        int closeStart = iconTime + openTime + textAppearTime + displayTime;
        int reverseStart = closeStart + closeTime;

        if (age >= reverseStart + iconTime){
            text = null;
            boss = null;
            return;
        }
        if (minecraft.options.hideGui) return;

        Window window = Minecraft.getInstance().getWindow();
        float windowWidth = window.getGuiScaledWidth();
        float windowHeight = window.getGuiScaledHeight();

        float time = age + tracker.getGameTimeDeltaPartialTick(false);
        float closing = Mth.clamp((time - closeStart) / closeTime, 0, 1);
        float expansion = FDEasings.easeOut(Mth.clamp((time - iconTime) / openTime, 0, 1)) * (1 - FDEasings.easeIn(closing));

        int iconFrame = frameCount - 1;
        if (age < iconTime){
            iconFrame = age / frameTime;
        }else if (age >= reverseStart){
            iconFrame = frameCount - 1 - (age - reverseStart) / frameTime;
        }

        float width = Math.min(maxWidth, windowWidth - frameSize - padding * 2);
        float centerX = windowWidth / 2f;
        float top = windowHeight - offsetFromBottom;
        float halfWidth = width * expansion / 2;
        float leftX = centerX - frameSize / 2f - halfWidth;
        float rightX = centerX - frameSize / 2f + halfWidth;

        float shadowScale = (iconFrame + 1f) / frameCount;
        float shadowWidth = width * expansion + shadowScale * frameSize / 2f;
        float shadowHeight = shadowScale * frameSize / 1.75f;
        BossRenderUtil.renderCenteredShadowedBox(graphics, centerX, top + frameSize / 2f, shadowWidth, shadowHeight, shadowScale * 10, Math.round(shadowScale * 6));

        FDRenderUtil.bindTexture(boss.getTexture());

        if (age >= iconTime && age < reverseStart){
            float fillLeft = centerX - halfWidth;
            float fillRight = centerX + halfWidth;
            float firstTileX = centerX - (float)Math.ceil(halfWidth / fillTextureWidth) * fillTextureWidth;
            for (float tileX = firstTileX; tileX < fillRight; tileX += fillTextureWidth){
                float partX = Math.max(tileX, fillLeft);
                float partWidth = Math.min(tileX + fillTextureWidth, fillRight) - partX;
                FDRenderUtil.blitWithBlend(graphics.pose(), partX, top, partWidth, frameSize, partX - tileX, textureHeight - frameSize, partWidth, frameSize, textureWidth, textureHeight, 0, 1);
            }
            FDRenderUtil.blitWithBlend(graphics.pose(), rightX, top, frameSize, frameSize, 0, frameSize, frameSize, frameSize, textureWidth, textureHeight, 0, 1);
        }

        FDRenderUtil.blitWithBlend(graphics.pose(), leftX, top, frameSize, frameSize, iconFrame * frameSize, 0, frameSize, frameSize, textureWidth, textureHeight, 0, 1);

        float appear = Mth.clamp((time - openTime) / textAppearTime, 0, 1);
        int textAlpha = (int)(FDEasings.easeInOut(appear) * (1 - FDEasings.easeInOut(closing)) * 255);

        if (textAlpha < 4) return;

        int textWidth = (int)((width - frameSize - padding * 2) / textScale);
        float clipLeft = leftX + frameSize + padding;
        float clipRight = rightX - padding;

        FDRenderUtil.Scissor.pushScissors(graphics.pose(), clipLeft, top + padding, Math.max(0, clipRight - clipLeft), frameSize - padding * 2);

        Component coloredText = BossRenderUtil.textColorWave(text, time * 0.4f, 0.25f,0x4488de, 0xffa60a);
        FDRenderUtil.renderFullyCenteredText(graphics, centerX, top + frameSize / 2f + 0.5f, textWidth, textScale, true, (textAlpha << 24) | 0xffffff, coloredText);

        FDRenderUtil.Scissor.popScissors();

    }

    public static void show(Component component, BossNote.Boss boss){
        Component message = component.copy();
        text = message;
        BossNoteOverlay.boss = boss;
        age = 0;
        displayTime = (int) Math.ceil(message.getString().length() * 2.5f);
    }

}
