package com.finderfeed.fdbosses.config;

import com.finderfeed.fdlib.systems.config.Comment;
import com.finderfeed.fdlib.systems.config.ConfigValue;

public class ItemConfig {

    @ConfigValue
    public float flyingSwordDamagePercent = 50;

    @ConfigValue
    public float chanceToSummonFlyingSword = 75;

    @ConfigValue
    @Comment("Time in ticks")
    public int flyingSwordShockDuration = 200;

    @ConfigValue
    public float playerMalkuthFireballToolDamagePercent = 200;

    @ConfigValue
    public float playerMalkuthFireballAbilityCooldown = 200;

}
