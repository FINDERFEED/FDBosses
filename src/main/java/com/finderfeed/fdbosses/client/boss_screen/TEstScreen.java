package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlock;
import com.finderfeed.fdbosses.client.boss_screen.text_block.interactions.TextBlockEntryInteraction;
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
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TEstScreen extends Screen {
    public TEstScreen() {
        super(Component.empty());
    }


    @Override
    protected void init() {
        super.init();

        Component component = Component.literal("PIZDEC NAHOY BLYET").withStyle(ChatFormatting.AQUA)
                .append(Component.literal(" ZHOPA BOLIT BLYAD ").withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" HU HI HI HA HIHI HU HIHIHI HA HIHI"));


        TextBlock textBlock = new TextBlock(100,100,100,100)
                .addTextBlockEntry(new SimpleTextEntry(component,1f, TextBlockEntryInteraction.hoverOver(((textBlock1, graphics, mx, my) -> {
                    graphics.renderTooltip(font, Items.ACACIA_BOAT.getDefaultInstance(),(int)mx,(int)my);
                }))))
                .addTextBlockEntry(new SimpleTextEntry(Component.literal("TEST TEST TEST TEST TEST TEST TEST TEST TEST TESTTESTTESTTESTTESTTESTTESTTESTTEST")
                        .withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE).withUnderlined(true)),1f,
                        TextBlockEntryInteraction.click(((block, mouseX, mouseY, key) -> {
                            Minecraft.getInstance().player.sendSystemMessage(Component.literal("You have clicked this shit"));
                }))));

        this.addRenderableWidget(textBlock);
    }

    @Override
    protected void renderBlurredBackground(float p_330683_) {

    }

    @Override
    public void render(GuiGraphics graphics, int mx, int my, float pticks) {
        super.render(graphics, mx, my, pticks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }






    @SubscribeEvent
    public static void key(InputEvent.Key event){
        if (Minecraft.getInstance().level == null) return;
        if (event.getKey() == GLFW.GLFW_KEY_M){
//            Minecraft.getInstance().setScreen(new TEstScreen());
        }
    }
}
