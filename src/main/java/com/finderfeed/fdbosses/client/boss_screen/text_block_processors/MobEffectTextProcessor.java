package com.finderfeed.fdbosses.client.boss_screen.text_block_processors;

import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockEntry;
import com.finderfeed.fdbosses.client.boss_screen.text_block.interactions.TextBlockEntryInteraction;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_entries.image_entry.ImageInText;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_entries.image_entry.ImageTextEntry;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_entries.SimpleTextEntry;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_parser.TextBlockProcessor;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.HashMap;
import java.util.List;

public class MobEffectTextProcessor extends TextBlockProcessor {

    @Override
    public List<TextBlockEntry> parse(float textScale,boolean renderShadow, int textColor, HashMap<String, String> arguments) {

        if (!arguments.containsKey("id")) throw new RuntimeException("Couldn't find 'id' on mob effect text processor");

        String id = arguments.get("id");
        ResourceLocation effectId = ResourceLocation.parse(id);

        MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(effectId);

        if (effect == null) throw new RuntimeException("Unknown effect provided in mob effect text processor: " + id);

        int color = effect.getColor();

        MobEffectTextureManager mobeffecttexturemanager = Minecraft.getInstance().getMobEffectTextures();

        TextureAtlasSprite sprite = mobeffecttexturemanager.get(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect));

        TextureAtlas atlas = mobeffecttexturemanager.textureAtlas;

        ImageInText imageInText = new ImageInText(sprite.atlasLocation(),sprite.getU0(),sprite.getV0(),sprite.getU1(),sprite.getV1(),atlas.width,atlas.height);


        ResourceLocation location = BuiltInRegistries.MOB_EFFECT.getKey(effect);

        String key = Util.makeDescriptionId("effect_description",location);

        TextBlockEntryInteraction interaction = TextBlockEntryInteraction.hoverOver(((textBlock, graphics, mx, my) -> {
            graphics.renderTooltip(Minecraft.getInstance().font, Minecraft.getInstance().font.split(Component.translatable(key),100),(int)mx,(int)my);
        }));

        ImageTextEntry imageTextEntry = new ImageTextEntry(imageInText,textScale,interaction);

        SimpleTextEntry text = new SimpleTextEntry(effect.getDisplayName().copy().withStyle(Style.EMPTY.withColor(color).withUnderlined(true)),textScale,renderShadow,textColor, interaction);

        return List.of(imageTextEntry,text);
    }
}
