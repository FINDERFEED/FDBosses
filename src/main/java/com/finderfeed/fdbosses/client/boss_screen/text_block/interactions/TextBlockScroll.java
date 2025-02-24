package com.finderfeed.fdbosses.client.boss_screen.text_block.interactions;

import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlock;

@FunctionalInterface
public interface TextBlockScroll {

    void scroll(TextBlock textBlock, float mx, float my, float scrollX, float scrollY);

}
