package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedBossBar;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthBossBar;
import com.finderfeed.fdlib.systems.FDRegistries;
import com.finderfeed.fdlib.systems.hud.bossbars.FDBossBarFactory;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossBars {

    public static final DeferredRegister<FDBossBarFactory<?>> BOSS_BARS = DeferredRegister.create(FDRegistries.FD_BOSS_BARS, FDBosses.MOD_ID);

    public static final RegistryObject<FDBossBarFactory<ChesedBossBar>> CHESED_BOSS_BAR = BOSS_BARS.register("chesed_boss_bar",()->ChesedBossBar::new);
    public static final RegistryObject<FDBossBarFactory<MalkuthBossBar>> MALKUTH_BOSS_BAR = BOSS_BARS.register("malkuth_boss_bar",()->MalkuthBossBar::new);

}
