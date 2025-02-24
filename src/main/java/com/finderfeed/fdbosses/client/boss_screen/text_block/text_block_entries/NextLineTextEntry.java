package com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_entries;

import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlock;
import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockCursor;
import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class NextLineTextEntry implements TextBlockEntry {

    private int lineHeight;

    public NextLineTextEntry(int lineHeight){
        this.lineHeight = lineHeight;
    }

    @Override
    public void render(GuiGraphics graphics, TextBlock textBlock, TextBlockCursor cursor,float mx,float my,float pticks) {
        cursor.nextLine(lineHeight);
    }
}
