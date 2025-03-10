package com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_parser.processors;

import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockEntry;
import com.finderfeed.fdbosses.client.boss_screen.text_block.interactions.TextBlockEntryInteraction;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_entries.ItemStackEntry;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_parser.TextBlockProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.List;

public class ItemTextBlockProcessor extends TextBlockProcessor {
    @Override
    public List<TextBlockEntry> parse(float textScale, boolean renderShadow, int textColor, HashMap<String, String> arguments) {

        if (!arguments.containsKey("item")){
            throw new RuntimeException("No item provided!");
        }

        String itemId = arguments.get("item");
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));

        ItemStackEntry itemStackEntry = new ItemStackEntry(item.getDefaultInstance(), textScale,
                TextBlockEntryInteraction.hoverOver(((textBlock, graphics, mx, my) -> {
                    graphics.renderTooltip(Minecraft.getInstance().font,item.getDefaultInstance(),(int) mx,(int) my);
                })));

        return List.of(itemStackEntry);
    }
}
