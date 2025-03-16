package com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_parser.processors;

import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockEntry;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_entries.SimpleTextEntry;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_parser.TextBlockProcessor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;

import java.util.HashMap;
import java.util.List;

public class EntityBaseAttributeValueProcessor extends TextBlockProcessor {
    @Override
    public List<TextBlockEntry> parse(float textScale, boolean renderShadow, int textColor, HashMap<String, String> arguments) {

        String attributeId = arguments.get("attribute");
        String entityType = arguments.get("type");

        EntityType<? extends LivingEntity> type = (EntityType<? extends LivingEntity>) BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(entityType));
        Holder<Attribute> attribute = BuiltInRegistries.ATTRIBUTE.getHolderOrThrow(ResourceKey.create(Registries.ATTRIBUTE,ResourceLocation.parse(attributeId)));

        float value = (float) DefaultAttributes.getSupplier(type).getBaseValue(attribute);

        SimpleTextEntry entry = new SimpleTextEntry(Component.literal("%.1f".formatted(value)),textScale,renderShadow,textColor);

        return List.of(entry);
    }
}
