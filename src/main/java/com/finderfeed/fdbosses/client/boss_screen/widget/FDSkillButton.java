package com.finderfeed.fdbosses.client.boss_screen.widget;

import com.finderfeed.fdbosses.client.boss_screen.screen_definitions.BossInfo;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.FDButton;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

public class FDSkillButton extends FDButton {

    private BossInfo bossInfo;

    public FDSkillButton(Screen screen, float x, float y, float width, float height, BossInfo bossInfo) {
        super(screen, x, y, width, height);
        this.bossInfo = bossInfo;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, float mx, float my, float pticks) {
        super.renderWidget(graphics, mx, my, pticks);


        var item = bossInfo.getInfoIcon().right();
        var rl = bossInfo.getInfoIcon().left();

        if (rl.isPresent()) {
            FDRenderUtil.bindTexture(rl.get());
            FDRenderUtil.blitWithBlend(graphics.pose(), this.getX() + 8, this.getY() + 8, 16, 16, 0, 0, 1, 1, 1, 1, 0, 1f);
        }else if (item.isPresent()){
            ItemStack stack = item.get();
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.translate(0,0,-100);
            FDRenderUtil.renderScaledItemStack(graphics,this.getX() + 8, this.getY() + 8, 1f, stack);
            poseStack.popPose();
        }
    }

    public BossInfo getBossInfo() {
        return bossInfo;
    }

    public void setBossInfo(BossInfo bossInfo) {
        this.bossInfo = bossInfo;
    }
}
