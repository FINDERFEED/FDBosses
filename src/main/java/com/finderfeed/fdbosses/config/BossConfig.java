package com.finderfeed.fdbosses.config;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.config.Comment;
import com.finderfeed.fdlib.systems.config.ConfigValue;
import com.finderfeed.fdlib.systems.config.ReflectiveJsonConfig;
import net.minecraft.resources.ResourceLocation;

public class BossConfig extends ReflectiveJsonConfig {

    @ConfigValue
    public float peacefulDifficultyBossDamageMuliplier = 0.25f;

    @ConfigValue
    public float easyDifficultyBossDamageMultiplier = 0.5f;

    @ConfigValue
    public float normalDifficultyBossDamageMultiplier = 0.75f;

    @ConfigValue
    public float hardDifficultyBossDamageMultiplier = 1f;

    @ConfigValue
    public ChesedConfig chesedConfig = new ChesedConfig();

    @ConfigValue
    public EffectConfig effectConfig = new EffectConfig();

    @ConfigValue
    public ItemConfig itemConfig = new ItemConfig();

    public BossConfig() {
        super(FDBosses.location("bosses"));
    }

    @Override
    public boolean isClientside() {
        return false;
    }
}
