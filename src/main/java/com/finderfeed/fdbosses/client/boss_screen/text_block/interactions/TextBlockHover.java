package com.finderfeed.fdbosses.client.boss_screen.text_block.interactions;

import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockWidget;
import net.minecraft.client.gui.GuiGraphics;

@FunctionalInterface
public interface TextBlockHover {

    void onHoverOver(TextBlockWidget textBlock, GuiGraphics graphics, float mx, float my);

}
