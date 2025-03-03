package com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_parser;

import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockEntry;

import java.util.HashMap;
import java.util.List;

public abstract class TextBlockProcessor {

    public abstract List<TextBlockEntry> parse(float textScale, boolean renderShadow, int textColor, HashMap<String,String> arguments);

}
