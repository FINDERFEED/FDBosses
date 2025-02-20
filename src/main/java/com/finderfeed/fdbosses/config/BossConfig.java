package com.finderfeed.fdbosses.config;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.config.ReflectiveJsonConfig;
import net.minecraft.resources.ResourceLocation;

public class BossConfig extends ReflectiveJsonConfig {

    public ChesedConfig chesedConfig = new ChesedConfig();

    public BossConfig() {
        super(FDBosses.location("bosses"));
    }

}
