package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.config.BossConfig;
import com.finderfeed.fdlib.systems.FDRegistries;
import com.finderfeed.fdlib.systems.config.JsonConfig;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossConfigs {

    public static final DeferredRegister<JsonConfig> CONFIGS = DeferredRegister.create(FDRegistries.CONFIGS, FDBosses.MOD_ID);

    public static final Supplier<BossConfig> BOSS_CONFIG = CONFIGS.register("bosses", BossConfig::new);

}
