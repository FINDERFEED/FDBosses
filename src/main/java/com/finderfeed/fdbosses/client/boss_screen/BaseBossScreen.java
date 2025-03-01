package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdlib.systems.simple_screen.SimpleFDScreen;
import com.finderfeed.fdlib.util.InterpolatedValue;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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
            renderable.render(graphics, mx, mx, pticks);
        }

        this.renderFront(graphics,mx,my,pticks);
    }



    protected abstract void renderBoss(GuiGraphics graphics, float mx, float my, float pticks);
    protected abstract void renderBack(GuiGraphics graphics, float mx, float my, float pticks);
    protected abstract void renderFront(GuiGraphics graphics, float mx, float my, float pticks);

}
