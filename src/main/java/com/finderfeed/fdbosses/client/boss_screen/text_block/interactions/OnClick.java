package com.finderfeed.fdbosses.client.boss_screen.text_block.interactions;

@FunctionalInterface
public interface OnClick {

    void click(float mouseX,float mouseY, int key, int action);

}
