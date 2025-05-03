package com.finderfeed.fdbosses.client.boss_screen.widget;

import com.finderfeed.fdbosses.client.BossRenderUtil;
import com.finderfeed.fdbosses.client.boss_screen.BaseBossScreen;
import com.finderfeed.fdlib.systems.simple_screen.FDWidget;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.text_block.TextBlockWidget;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DidntReadSkillWarningWidget extends FDWidget {



    public DidntReadSkillWarningWidget(BaseBossScreen screen, float x, float y, float width, float height) {
        super(screen, x, y, width, height);
    }


    @Override
    public void renderWidget(GuiGraphics guiGraphics, float v, float v1, float v2) {

        BossRenderUtil.renderBossScreenTooltip(guiGraphics.pose(), this.getX(), this.getY(), this.getWidth(), this.getHeight());

        FDRenderUtil.renderFullyCenteredText(guiGraphics, this.getX() + this.getWidth() / 2,this.getY() + 30, Math.round(this.getWidth() - 20),1f,true, ((BaseBossScreen)this.widgetOwner).getBaseStringColor(),
                Component.translatable("fdbosses.word.didnt_read_skills"));

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
