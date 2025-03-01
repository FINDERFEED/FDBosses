package com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_parser;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class FDString {

    private TextBlockProcessor processor;
    private HashMap<String,String> arguments;

    public FDString(TextBlockProcessor textBlockProcessor, HashMap<String,String> arguments){
        this.processor = textBlockProcessor;
        this.arguments = arguments;
    }

    public HashMap<String, String> getArguments() {
        return arguments;
    }

    public TextBlockProcessor getProcessor() {
        return processor;
    }
}
