package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.geburah.sins.ScreenFlashEffect;
import com.finderfeed.fdbosses.content.entities.geburah.sins.ScreenFlashEffectData;
import com.finderfeed.fdlib.systems.FDRegistries;
import com.finderfeed.fdlib.systems.screen.screen_effect.ScreenEffectType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossScreenEffects {

    public static final DeferredRegister<ScreenEffectType<?, ?>> SCREEN_EFFECTS = DeferredRegister.create(FDRegistries.SCREEN_EFFECTS, FDBosses.MOD_ID);

    public static final Supplier<ScreenEffectType<ScreenFlashEffectData, ScreenFlashEffect>> SCREEN_FLASH = SCREEN_EFFECTS.register("screen_flash", ()->ScreenEffectType.create(
            ScreenFlashEffect::new, ScreenFlashEffectData.STREAM_CODEC
    ));

}
