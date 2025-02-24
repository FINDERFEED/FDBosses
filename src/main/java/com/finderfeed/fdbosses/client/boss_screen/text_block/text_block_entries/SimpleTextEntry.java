package com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_entries;

import com.finderfeed.fdbosses.client.boss_screen.TEstScreen;
import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlock;
import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockCursor;
import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockEntry;
import com.finderfeed.fdbosses.client.boss_screen.text_block.interactions.TextBlockEntryInteraction;
import com.finderfeed.fdbosses.client.boss_screen.text_block.interactions.InteractionBox;
import com.finderfeed.fdlib.data_structures.Pair;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class SimpleTextEntry implements TextBlockEntry {

    private Component component;
    private float textScale;
    private TextBlockEntryInteraction interaction;

    public SimpleTextEntry(Component component, float textScale, TextBlockEntryInteraction interaction){
        this.component = component;
        this.textScale = textScale;
        this.interaction = interaction;
    }
    public SimpleTextEntry(Component component, float textScale){
        this(component,textScale, TextBlockEntryInteraction.empty());
    }

    @Override
    public void render(GuiGraphics graphics, TextBlock textBlock, TextBlockCursor cursor,float mx,float my,float pticks) {
        if (component.getString().isEmpty()) return;

        Font font = Minecraft.getInstance().font;

        float lineHeight = font.lineHeight * textScale;

        int remainingWidth = (int) cursor.remainingWidth(textBlock.getBorderX());

        if (remainingWidth > 0) {
            Pair<FormattedText, FormattedText> texts = TEstScreen.splitOneTime(component, Math.round(remainingWidth / textScale));
            FormattedText first = texts.first;

            FDRenderUtil.renderScaledText(graphics, Language.getInstance().getVisualOrder(first),cursor.x,cursor.y,textScale,true,0xffffff);

            float width = font.width(first) * textScale;

            textBlock.addInteractionBox(new InteractionBox(cursor.x,cursor.y,width,lineHeight,interaction));

            cursor.addX(width);


            if (texts.second != null){
                cursor.nextLine(font.lineHeight * textScale);
                this.renderNormalText(texts.second,graphics,textBlock,cursor,textScale);
            }
        }else{
            cursor.nextLine(font.lineHeight * textScale);
            renderNormalText(component,graphics,textBlock,cursor,textScale);
        }
    }

    private void renderNormalText(FormattedText text,GuiGraphics graphics, TextBlock textBlock, TextBlockCursor cursor,float scale){
        Font font = Minecraft.getInstance().font;

        float lineHeight = font.lineHeight * textScale;

        List<FormattedCharSequence> sequences = font.split(text,Math.round(textBlock.width / scale));
        for (FormattedCharSequence sequence : sequences){
            FDRenderUtil.renderScaledText(graphics, sequence,cursor.x,cursor.y,textScale,true,0xffffff);
            textBlock.addInteractionBox(new InteractionBox(cursor.x,cursor.y,font.width(sequence) * textScale,lineHeight,interaction));
            cursor.nextLine(lineHeight);
        }
        cursor.nextLine(-lineHeight);
        cursor.addX(font.width(sequences.getLast()) * scale);
    }
}
