package com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_entries;

import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockWidget;
import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockCursor;
import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockEntry;
import net.minecraft.client.gui.GuiGraphics;

public class NextLineTextEntry implements TextBlockEntry {

    private float lineHeight;

    public NextLineTextEntry(float lineHeight){
        this.lineHeight = lineHeight;
    }

    @Override
    public void render(GuiGraphics graphics, TextBlockWidget textBlock, TextBlockCursor cursor, float mx, float my, float pticks, boolean last) {
        cursor.nextLine(lineHeight);
    }
}
