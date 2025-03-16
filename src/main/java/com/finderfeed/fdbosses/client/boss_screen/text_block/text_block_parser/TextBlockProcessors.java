package com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_parser;

import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_parser.processors.EntityBaseAttributeValueProcessor;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_parser.processors.ImageTextBlockProcessor;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_parser.processors.ItemTextBlockProcessor;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_parser.processors.ReferenceTextBlockProcessor;
import com.finderfeed.fdlib.FDLib;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class TextBlockProcessors {

    private static final HashMap<String,TextBlockProcessor> TEXT_PROCESSORS = new HashMap<>();

    public static final ReferenceTextBlockProcessor REFERENCE_TEXT_BLOCK_PROCESSOR = register(FDLib.location("reference"),new ReferenceTextBlockProcessor());
    public static final ItemTextBlockProcessor ITEM_TEXT_BLOCK_PROCESSOR = register(FDLib.location("item"),new ItemTextBlockProcessor());
    public static final ImageTextBlockProcessor IMAGE_TEXT_BLOCK_PROCESSOR = register(FDLib.location("image"),new ImageTextBlockProcessor());
    public static final EntityBaseAttributeValueProcessor ENTITY_BASE_ATTRIBUTE_VALUE_PROCESSOR = register(FDLib.location("attribute"),new EntityBaseAttributeValueProcessor());

    public static <T extends TextBlockProcessor> T register(ResourceLocation name, T textBlockProcessor){
        TEXT_PROCESSORS.put(name.toString(),textBlockProcessor);
        return textBlockProcessor;
    }

    public static TextBlockProcessor getProcessor(ResourceLocation id){
        return TEXT_PROCESSORS.get(id.toString());
    }

}
