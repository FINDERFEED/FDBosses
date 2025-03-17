package com.finderfeed.fdbosses.client.boss_screen.text_block_processors;

import com.finderfeed.fdbosses.config.BossConfig;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.text_block.TextBlockEntry;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.text_block.text_block_entries.SimpleTextEntry;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.text_block.text_block_parser.TextBlockProcessor;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

public class BossConfigFloatValueProcessor extends TextBlockProcessor {
    @Override
    public List<TextBlockEntry> parse(float textScale, boolean renderShadow, int textColor, HashMap<String, String> arguments) {

        String configName = arguments.get("config");
        String valueName = arguments.get("value");
        float finalValue;
        try {
            BossConfig bossConfig = BossConfigs.BOSS_CONFIG.get();
            Field field = BossConfig.class.getField(configName);

            Object config = field.get(bossConfig);
            Field fieldWithValue = config.getClass().getField(valueName);

            Object obj = fieldWithValue.get(config);

            if (!(obj instanceof Number)){
                throw new RuntimeException("A value " + valueName + " in " + configName + " is not float/int!");
            }else{
                finalValue = (float) obj;
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        SimpleTextEntry textEntry = new SimpleTextEntry(Component.literal("%.1f".formatted(finalValue)),textScale,renderShadow,textColor);
        return List.of(textEntry);
    }
}
