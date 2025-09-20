package com.finderfeed.fdbosses.config;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.config.Comment;
import com.finderfeed.fdlib.systems.config.ConfigValue;
import com.finderfeed.fdlib.systems.config.ReflectiveJsonConfig;

public class BossClientConfig extends ReflectiveJsonConfig {

    @ConfigValue
    @Comment("Decreases the amount of particles that some attacks spawn")
    public boolean lessParticles = false;

    public BossClientConfig() {
        super(FDBosses.location("bosses_clientside"));
    }

    @Override
    public boolean isClientside() {
        return true;
    }
}
