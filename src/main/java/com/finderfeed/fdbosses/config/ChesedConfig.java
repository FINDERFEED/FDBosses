package com.finderfeed.fdbosses.config;

import com.finderfeed.fdlib.systems.config.Comment;
import com.finderfeed.fdlib.systems.config.ConfigValue;

public class ChesedConfig  {

    @ConfigValue
    public int chesedMaxHits = 10;

    @ConfigValue
    @Comment("Defence from flight")
    public float electrifiedAirCurrentHealthDamagePercent = 33f;

    @ConfigValue
    @Comment("Immediately kills the player if HP is lower than value")
    public float electrifiedAirInstadeathHP = 2f;

    @ConfigValue
    public float electricSphereDamage = 10f;

    @ConfigValue
    public float rockfallRayDamage = 10f;

    @ConfigValue
    public float kineticFieldRayDamage = 10f;

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
    public float fireTrailDamage = 2;

    @ConfigValue
    public float finalAttackDamagePercentPerMonolith = 25f;

    @ConfigValue
    @Comment("If health is greater than value, sets the health equal to the value")
    public float healthAfterSecondPhaseTransition = 10f;

    @ConfigValue
    @Comment("Monolith hp gain when second phase starts")
    public float additionalSecondPhaseMonolithHP = 50f;

}
