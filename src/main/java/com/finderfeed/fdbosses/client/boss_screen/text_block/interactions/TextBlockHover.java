package com.finderfeed.fdbosses.client.boss_screen.text_block.interactions;

import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlock;
import net.minecraft.client.gui.GuiGraphics;

@FunctionalInterface
public interface TextBlockHover {

    void onHoverOver(TextBlock textBlock, GuiGraphics graphics, float mx, float my);

}
