package com.finderfeed.fdbosses.client.boss_screen.text_block.interactions;

import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockWidget;

@FunctionalInterface
public interface TextBlockClick {

    void click(TextBlockWidget block, float mouseX, float mouseY, int key);

}
