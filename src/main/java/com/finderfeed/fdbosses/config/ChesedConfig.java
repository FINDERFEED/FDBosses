package com.finderfeed.fdbosses.config;

import com.finderfeed.fdlib.systems.config.ConfigValue;

public class ChesedConfig  {

    @ConfigValue
    public int chesedMaxHits = 10;

    @ConfigValue
    public float electricSphereDamage = 10f;

    @ConfigValue
    public float rockfallRayDamage = 10f;

    @ConfigValue
    public float rockfallRockDamage = 5f;

    @ConfigValue
    public float earthquakeRayDamage = 5f;

    @ConfigValue
    public float eartquakeDamage = 5f;

    @ConfigValue
    public float rollAttackDamage = 8f;

    @ConfigValue
    public float blockAttackDamage = 10;

    @ConfigValue
    public float rayDamage = 10;

    @ConfigValue
    public float finalAttackDamagePercentPerMonolith = 25f;

}
