package com.finderfeed.fdbosses.content.items.chesed;

import com.finderfeed.fdbosses.init.BossCoreShaders;
import com.finderfeed.fdlib.util.rendering.FDShaderRenderer;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

public class ChesedItemOverlay implements LayeredDraw.Layer {

    @Override
    public void render(GuiGraphics graphics, DeltaTracker tracker) {

        if (Minecraft.getInstance().level == null) return;

        var shader = BossCoreShaders.CHESED_ITEM_OVERLAY;
        Window window = Minecraft.getInstance().getWindow();

        float width = window.getGuiScaledWidth();
        float height = window.getGuiScaledHeight();

        float pticks = tracker.getGameTimeDeltaPartialTick(false);
        float gametime = Minecraft.getInstance().level.getGameTime();
        float time = gametime + pticks;

        FDShaderRenderer.start(graphics, shader)
                .setShaderUniform("size", width, height)
                .setShaderUniform("time",time / 200)
                .setResolution(width,height)
                .end();


    }

}
