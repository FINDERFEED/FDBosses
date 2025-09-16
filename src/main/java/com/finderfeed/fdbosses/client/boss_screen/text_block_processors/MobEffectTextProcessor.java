package com.finderfeed.fdbosses.client.boss_screen.text_block_processors;

import com.finderfeed.fdbosses.client.BossRenderUtil;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.text_block.TextBlockEntry;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.text_block.interactions.TextBlockEntryInteraction;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.text_block.text_block_entries.SimpleTextEntry;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.text_block.text_block_entries.image_entry.ImageInText;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.text_block.text_block_entries.image_entry.ImageTextEntry;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.text_block.text_block_parser.TextBlockProcessor;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;

public class MobEffectTextProcessor extends TextBlockProcessor {

    @Override
    public List<TextBlockEntry> parse(float textScale, boolean renderShadow, int textColor, HashMap<String, String> arguments) {

        if (!arguments.containsKey("id")) throw new RuntimeException("Couldn't find 'id' on mob effect text processor");

        String id = arguments.get("id");
        ResourceLocation effectId = ResourceLocation.tryParse(id);

        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectId);

        if (effect == null) throw new RuntimeException("Unknown effect provided in mob effect text processor: " + id);

        int color = effect.getColor();

        MobEffectTextureManager mobeffecttexturemanager = Minecraft.getInstance().getMobEffectTextures();

        TextureAtlasSprite sprite = mobeffecttexturemanager.get(effect);

        TextureAtlas atlas = mobeffecttexturemanager.textureAtlas;

        ImageInText imageInText = new ImageInText(sprite.atlasLocation(),sprite.getU0(),sprite.getV0(),sprite.getU1(),sprite.getV1(),atlas.width,atlas.height);


        ResourceLocation location = ForgeRegistries.MOB_EFFECTS.getKey(effect);

        String key = Util.makeDescriptionId("effect_description",location);

        TextBlockEntryInteraction interaction = TextBlockEntryInteraction.hoverOver(((textBlock, graphics, mx, my) -> {
            PoseStack matrices = graphics.pose();
            matrices.pushPose();
            matrices.translate(0,0,100);
            BossRenderUtil.renderBossScreenTooltip(graphics,Component.translatable(key),mx,my,200,textColor,1f);
            matrices.popPose();
        }));

        ImageTextEntry imageTextEntry = new ImageTextEntry(imageInText,textScale,interaction);


        MutableComponent displayName = effect.getDisplayName().copy();

        if (arguments.containsKey("level")){
            int level = Integer.parseInt(arguments.get("level"));
            String s = this.levelToString(level);
            if (!s.isEmpty()){
                displayName = displayName.append(" " + s);
            }
        }

        SimpleTextEntry text = new SimpleTextEntry(displayName.withStyle(Style.EMPTY.withColor(color).withUnderlined(true)),textScale,renderShadow,textColor, interaction);

        return List.of(imageTextEntry,text);
    }

    private String levelToString(int level){
        switch (level){
            case 1 -> {
                return "II";
            }
            case 2 -> {
                return "III";
            }
            case 3 -> {
                return "IV";
            }
            case 4 -> {
                return "V";
            }
            case 5 -> {
                return "VI";
            }
            case 6 -> {
                return "VII";
            }
            case 7 -> {
                return "VIII";
            }
            default -> {
                return "";
            }
        }
    }
}
