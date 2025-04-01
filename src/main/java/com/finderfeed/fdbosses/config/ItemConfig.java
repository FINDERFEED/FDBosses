package com.finderfeed.fdbosses.config;

import com.finderfeed.fdlib.systems.config.Comment;
import com.finderfeed.fdlib.systems.config.ConfigValue;

public class ItemConfig {

    @ConfigValue
    public float flyingSwordDamagePercent = 50;

    @ConfigValue
    public float chanceToSummonFlyingSword = 10;

    @ConfigValue
    @Comment("Time in ticks")
    public int flyingSwordShockDuration = 20;

}
