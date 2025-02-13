package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedBossBar;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.FDRegistries;
import com.finderfeed.fdlib.systems.hud.bossbars.FDBossBarFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossBars {

    public static final DeferredRegister<FDBossBarFactory<?>> BOSS_BARS = DeferredRegister.create(FDRegistries.FD_BOSS_BARS, FDBosses.MOD_ID);

    public static final DeferredHolder<FDBossBarFactory<?>,FDBossBarFactory<ChesedBossBar>> CHESED_BOSS_BAR = BOSS_BARS.register("chesed_boss_bar",()->ChesedBossBar::new);

}
