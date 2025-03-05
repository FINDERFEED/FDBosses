package com.finderfeed.fdbosses.client.boss_screen.text_block.interactions;

import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockWidget;

@FunctionalInterface
public interface TextBlockScroll {

    void scroll(TextBlockWidget textBlock, float mx, float my, float scrollX, float scrollY);

}
