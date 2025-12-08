package com.finderfeed.fdbosses.config;

import com.finderfeed.fdlib.systems.config.ConfigValue;
import com.finderfeed.fdlib.systems.config.ReflectiveJsonConfig;
import net.minecraft.resources.ResourceLocation;

public class GeburahConfig {

    @ConfigValue
    public float coreRayStrikeDamage = 40;

    @ConfigValue
    public float rotatingLaserAttackDamage = 10;

    @ConfigValue
    public float predictiveLaserAttackDamage = 50;

    @ConfigValue
    public float justiceHammerDamage = 40;

    @ConfigValue
    public float judgementBirdDamage = 10;

    @ConfigValue
    public float earthquakeDamage = 20;

    @ConfigValue
    public int maxPlayerSins = 5;

}
