package com.finderfeed.fdbosses.client.boss_screen.screen_definitions;

import com.finderfeed.fdbosses.client.boss_screen.BaseBossScreen;

import java.util.function.Function;
import java.util.function.Supplier;

public class BossScreenEntry<T extends BaseBossScreen> {

    private Supplier<T> screenFactory;

    public BossScreenEntry(Supplier<T> screenFactory){
        this.screenFactory = screenFactory;
    }

}
