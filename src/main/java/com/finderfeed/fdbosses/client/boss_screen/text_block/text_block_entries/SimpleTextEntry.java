package com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_entries;

import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlock;
import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockCursor;
import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockEntry;
import com.finderfeed.fdbosses.client.boss_screen.text_block.interactions.TextBlockEntryInteraction;
import com.finderfeed.fdbosses.client.boss_screen.text_block.interactions.InteractionBox;
import com.finderfeed.fdlib.data_structures.Pair;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleTextEntry implements TextBlockEntry {

    private FormattedText component;
    private float textScale;
    private TextBlockEntryInteraction interaction;
    private Style styleOverride;
    private boolean renderShadow;
    private int textColor;

    public SimpleTextEntry(FormattedText component, float textScale, boolean renderShadow, int textColor, TextBlockEntryInteraction interaction){
        this.component = component;
        this.textScale = textScale;
        this.renderShadow = renderShadow;
        this.interaction = interaction;
        this.textColor = textColor;
    }

    public SimpleTextEntry(FormattedText component, float textScale, boolean renderShadow, int textColor){
        this(component,textScale,renderShadow, textColor, TextBlockEntryInteraction.empty());
    }

    public SimpleTextEntry chatFormattingOverride(Style chatFormatting){
        this.styleOverride = chatFormatting;
        return this;
    }

    @Override
    public void render(GuiGraphics graphics, TextBlock textBlock, TextBlockCursor cursor,float mx,float my,float pticks) {
        if (component.getString().isEmpty()) return;

        Font font = Minecraft.getInstance().font;

        float lineHeight = font.lineHeight * textScale;

        int remainingWidth = (int) cursor.remainingWidth(textBlock.getBorderX());

        if (remainingWidth > 0) {
            Pair<FormattedText, FormattedText> texts = splitOneTime(component, Math.round(remainingWidth / textScale));
            FormattedText first = texts.first;

            if (styleOverride != null){
                first = FormattedText.of(first.getString(),styleOverride);
            }

            FDRenderUtil.renderScaledText(graphics, Language.getInstance().getVisualOrder(first),cursor.x,cursor.y,textScale,renderShadow,textColor);

            float width = font.width(first) * textScale;

            textBlock.addInteractionBox(new InteractionBox(cursor.x,cursor.y,width,lineHeight,interaction));

            cursor.addX(width);


            if (texts.second != null){

                FormattedText second = texts.second;

                if (styleOverride != null){
                    second = FormattedText.of(second.getString(),styleOverride);
                }

                cursor.nextLine(font.lineHeight * textScale);
                this.renderNormalText(second,graphics,textBlock,cursor,textScale);
            }
        }else{
            cursor.nextLine(font.lineHeight * textScale);
            renderNormalText(component,graphics,textBlock,cursor,textScale);
        }
    }

    private void renderNormalText(FormattedText text,GuiGraphics graphics, TextBlock textBlock, TextBlockCursor cursor,float scale){
        Font font = Minecraft.getInstance().font;

        float lineHeight = font.lineHeight * textScale;

        StringSplitter splitter = font.getSplitter();



        List<FormattedText> sequences = splitter.splitLines(text,Math.round(textBlock.getWidth() / scale),Style.EMPTY);
        for (FormattedText formattedText : sequences){

            if (styleOverride != null){
                formattedText = FormattedText.of(formattedText.getString(),styleOverride);
            }

            FDRenderUtil.renderScaledText(graphics, Language.getInstance().getVisualOrder(formattedText),cursor.x,cursor.y,textScale,renderShadow,textColor);
            textBlock.addInteractionBox(new InteractionBox(cursor.x,cursor.y,font.width(formattedText) * textScale,lineHeight,interaction));
            cursor.nextLine(lineHeight);
        }
        cursor.nextLine(-lineHeight);
        cursor.addX(font.width(sequences.getLast()) * scale);
    }

    public static Pair<FormattedText,FormattedText> splitOneTime(FormattedText text, int pixels){

        StringSplitter splitter = Minecraft.getInstance().font.getSplitter();

        String str = text.getString();

        int lineBreak = splitter.findLineBreak(str,pixels, Style.EMPTY);

        if (lineBreak >= str.length()){
            return new Pair<>(text,null);
        }

        List<Pair<Style, String>> styleString = new ArrayList<>();

        text.visit((string, style) -> {
            styleString.add(new Pair<>(string, style));
            return Optional.empty();
        }, Style.EMPTY);

        int currentIndex = 0;

        FormattedText before = FormattedText.EMPTY;
        FormattedText after = FormattedText.EMPTY;
        boolean wasSplit = false;
        boolean shouldDeleteSpace = false;

        for (var pair : styleString) {

            Style style = pair.first;
            String s = pair.second;

            int stringLength = s.length();

            if (wasSplit) {
                if (shouldDeleteSpace) {
                    if (s.charAt(0) == ' ' || s.charAt(0) == '\n') {
                        s = s.substring(1);
                    }
                    shouldDeleteSpace = false;
                }
                after = FormattedText.composite(after, FormattedText.of(s, style));
            } else {
                if (stringLength + currentIndex < lineBreak) {
                    currentIndex += stringLength;
                    before = FormattedText.composite(before, FormattedText.of(s, style));
                } else {

                    int substrlength = lineBreak - currentIndex;

                    String first = s.substring(0, substrlength);
                    String second = s.substring(substrlength);

                    before = FormattedText.composite(before, FormattedText.of(first, style));

                    if (!second.isEmpty()) {
                        if (second.charAt(0) == ' ' || second.charAt(0) == '\n') {
                            second = second.substring(1);
                        }
                        after = FormattedText.composite(after, FormattedText.of(second, style));
                    }else{
                        shouldDeleteSpace = true;
                    }
                    wasSplit = true;
                }
            }
        }

        return new Pair<>(before,after);
    }
}
