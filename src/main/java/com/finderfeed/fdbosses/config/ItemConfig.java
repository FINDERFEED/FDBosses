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
    public int playerMalkuthFireballAbilityCooldown = 100;

    @ConfigValue
    public int justiceCoreDamageReduction = 15;

    @ConfigValue
    public int phaseSphereUseDuration = 200;

    @ConfigValue
    public int phaseSphereCooldown = 100;

    @ConfigValue
    public int iceFireGauntletCooldown = 60;

    @ConfigValue
    public int iceFireGauntletCrushDamage = 15;

    @ConfigValue
    public int divineGearAttackCooldown = 60;

    @ConfigValue
    public int divineGearDamage = 20;

    @ConfigValue
    public int divineGearLifetime = 500;

}
