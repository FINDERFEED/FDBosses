package com.finderfeed.fdbosses.client.boss_screen.widget;

import com.finderfeed.fdbosses.client.boss_screen.screen_definitions.BossSkill;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.FDButton;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class FDSkillButton extends FDButton {

    private BossSkill bossSkill;

    public FDSkillButton(Screen screen, float x, float y, float width, float height, BossSkill skill) {
        super(screen, x, y, width, height);
        this.bossSkill = skill;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, float mx, float my, float pticks) {
        super.renderWidget(graphics, mx, my, pticks);

        FDRenderUtil.bindTexture(bossSkill.getSkillIcon());
        FDRenderUtil.blitWithBlend(graphics.pose(),this.getX() + 8,this.getY() + 8,16,16,0,0,1,1,1,1,0,1f);

    }

    public BossSkill getBossSkill() {
        return bossSkill;
    }

    public void setBossSkill(BossSkill bossSkill) {
        this.bossSkill = bossSkill;
    }
}
