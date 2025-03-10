package com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_entries;

import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockCursor;
import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockEntry;
import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockWidget;
import com.finderfeed.fdbosses.client.boss_screen.text_block.interactions.InteractionBox;
import com.finderfeed.fdbosses.client.boss_screen.text_block.interactions.TextBlockEntryInteraction;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class ItemStackEntry implements TextBlockEntry {

    private float textScale;
    private ItemStack itemStack;
    private TextBlockEntryInteraction interaction;

    public ItemStackEntry(ItemStack itemStack, float textScale, TextBlockEntryInteraction interaction){
        this.itemStack = itemStack;
        this.textScale = textScale;
        this.interaction = interaction;
    }

    @Override
    public void render(GuiGraphics graphics, TextBlockWidget textBlock, TextBlockCursor cursor, float mouseX, float mouseY, float pticks, boolean lastEntry) {

        Font font = Minecraft.getInstance().font;
        float fontHeight = font.lineHeight * this.textScale;
        float scale = fontHeight / 16;

        float width = scale * 16;
        if (cursor.shouldGoToNextLine(width,textBlock.getBorderX())){
            cursor.nextLine(fontHeight);
        }

        float x = cursor.x;
        float y = cursor.y;

        FDRenderUtil.renderScaledItemStack(graphics, x,y,scale,itemStack);
        textBlock.addInteractionBox(new InteractionBox(x,y,width, width,interaction));

        cursor.addX(width);
        if (lastEntry){
            cursor.nextLine(scale * 16);
        }
    }
}
