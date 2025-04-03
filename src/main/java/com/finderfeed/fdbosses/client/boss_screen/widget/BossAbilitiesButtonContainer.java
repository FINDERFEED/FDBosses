package com.finderfeed.fdbosses.client.boss_screen.widget;

import com.finderfeed.fdlib.systems.simple_screen.FDScrollableWidget;
import com.finderfeed.fdlib.systems.simple_screen.FDWidget;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class BossAbilitiesButtonContainer extends FDScrollableWidget {

    private float maxScroll = 0;

    public BossAbilitiesButtonContainer(Screen screen, float x, float y, float width, float height) {
        super(screen, x, y, width, height);
    }



    @Override
    public void renderWidget(GuiGraphics guiGraphics, float v, float v1, float v2) {
//        FDRenderUtil.fill(guiGraphics.pose(), this.getX(),this.getY(),this.getWidth(),this.getHeight(), 1f,1f,1f,0.5f);
    }

    @Override
    public void addChild(String id, FDWidget widget) {
        super.addChild(id, widget);

        //74

        maxScroll = Math.max(maxScroll,widget.getY() - (this.getParent().getY() + 27) + widget.getHeight() + 5);

    }


    @Override
    public void useScissor() {
        FDRenderUtil.Scissor.pushScissors(this.getX(),this.getY(),this.getWidth(),this.getHeight());
    }

    @Override
    public void endScissor() {
        FDRenderUtil.Scissor.popScissors();
    }


    @Override
    public float getMaxScroll() {
        return Math.max(maxScroll - 74, 0);
    }

    @Override
    public void onScroll(float v) {
        this.moveChildren(0,-v);
    }

    @Override
    public float scrollAmount() {
        return 5;
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
