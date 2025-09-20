package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.config.BossClientConfig;
import com.finderfeed.fdbosses.config.BossConfig;
import com.finderfeed.fdlib.systems.FDRegistries;
import com.finderfeed.fdlib.systems.config.JsonConfig;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossConfigs {

    public static final DeferredRegister<JsonConfig> CONFIGS = DeferredRegister.create(FDRegistries.CONFIGS_KEY, FDBosses.MOD_ID);

    public static final Supplier<BossConfig> BOSS_CONFIG = CONFIGS.register("bosses", BossConfig::new);

    public static final Supplier<BossClientConfig> BOSS_CONFIG_CLIENT = CONFIGS.register("bosses_clientside", BossClientConfig::new);

}
