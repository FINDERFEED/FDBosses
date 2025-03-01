package com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_parser.processors;

import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockEntry;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_entries.SimpleTextEntry;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_parser.TextBlockProcessor;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReferenceTextBlockProcessor extends TextBlockProcessor {
    @Override
    public List<TextBlockEntry> parse(float textScale, HashMap<String, String> arguments) {

        List<TextBlockEntry> entries = new ArrayList<>();

        if (!arguments.containsKey("id")){
            throw new RuntimeException("Couldn't find the id argument in reference text processor");
        }

        String translationId = arguments.get("id");

        entries.add(new SimpleTextEntry(Component.translatable(translationId),textScale));

        return entries;
    }
}
