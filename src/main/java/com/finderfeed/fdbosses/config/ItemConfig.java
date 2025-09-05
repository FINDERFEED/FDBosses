package com.finderfeed.fdbosses.config;

import com.finderfeed.fdlib.systems.config.Comment;
import com.finderfeed.fdlib.systems.config.ConfigValue;

public class ItemConfig {

    @ConfigValue
    public float lightningStrikeDamagePercent = 100;

    @ConfigValue
    public float chanceToSummonLightningStrike = 33;

    @ConfigValue
    @Comment("Time in ticks")
    public int lightningStrikeShockDuration = 200;

    @ConfigValue
    public float playerMalkuthFireballToolDamagePercent = 200;

    @ConfigValue
    public int playerMalkuthFireballAbilityCooldown = 200;

}
