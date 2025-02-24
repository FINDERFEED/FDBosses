package com.finderfeed.fdbosses.client.boss_screen.text_block.interactions;

import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlock;

@FunctionalInterface
public interface TextBlockClick {

    void click(TextBlock block,float mouseX, float mouseY, int key);

}
