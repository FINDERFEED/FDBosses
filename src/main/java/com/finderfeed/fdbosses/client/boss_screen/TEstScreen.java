package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlock;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_entries.NextLineTextEntry;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_entries.SimpleTextEntry;
import com.finderfeed.fdlib.data_structures.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = FDBosses.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class TEstScreen extends Screen {
    public TEstScreen() {
        super(Component.empty());
    }


    @Override
    public void render(GuiGraphics graphics, int mx, int my, float pticks) {
        super.render(graphics, mx, my, pticks);

        Component component = Component.literal("PIZDEC NAHOY BLYET").withStyle(ChatFormatting.AQUA)
                .append(Component.literal(" ZHOPA BOLIT BLYAD ").withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" HU HI HI HA HIHI HU HIHIHI HA HIHI").withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,"asd"))));


        TextBlock textBlock = new TextBlock(10,10,100,100)
                .addTextBlockEntry(new SimpleTextEntry(component,0.5f))
                .addTextBlockEntry(new NextLineTextEntry(9))
                .addTextBlockEntry(new SimpleTextEntry(Component.literal("TEST TEST TEST TEST TESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTEST"),0.5f));

        textBlock.render(graphics,mx,my,pticks);

    }

    @Override
    public boolean isPauseScreen() {
        return false;
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




    @SubscribeEvent
    public static void key(InputEvent.Key event){
        if (Minecraft.getInstance().level == null) return;
        if (event.getKey() == GLFW.GLFW_KEY_M){
            Minecraft.getInstance().setScreen(new TEstScreen());
        }
    }
}
