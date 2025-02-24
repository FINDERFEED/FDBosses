package com.finderfeed.fdbosses.client.boss_screen.text_block;

import net.minecraft.client.gui.GuiGraphics;

public interface TextBlockEntry {

    void render(GuiGraphics graphics, TextBlock textBlock, TextBlockCursor cursor, float mouseX,float mouseY,float pticks);

}
