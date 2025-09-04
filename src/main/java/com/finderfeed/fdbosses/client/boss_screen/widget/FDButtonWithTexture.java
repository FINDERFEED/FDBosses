package com.finderfeed.fdbosses.client.boss_screen.widget;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.FDButton;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public class FDButtonWithTexture extends FDButton {

    private ResourceLocation location;

    public FDButtonWithTexture(Screen screen, float x, float y, float width, float height, ResourceLocation location) {
        super(screen, x, y, width, height);
        this.location = location;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, float mx, float my, float pticks) {
        super.renderWidget(graphics, mx, my, pticks);
        FDRenderUtil.bindTexture(location);
        FDRenderUtil.blitWithBlend(graphics.pose(), this.getX() + 8, this.getY() + 8, 16, 16, 0, 0, 1, 1, 1, 1, 0, 1f);
    }

}
