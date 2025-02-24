package com.finderfeed.fdbosses.client.boss_screen.text_block.interactions;

import net.minecraft.client.gui.GuiGraphics;

@FunctionalInterface
public interface OnHover {

    void onHoverOver(GuiGraphics graphics, float mx, float my);

}
