package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.effects.ChesedDarkenEffect;
import com.finderfeed.fdbosses.effects.SimpleEffect;
import com.finderfeed.fdbosses.FDBosses;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BossEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, FDBosses.MOD_ID);

    public static final Holder<MobEffect> CHESED_GAZE = MOB_EFFECTS.register("chesed_gaze", ()->new SimpleEffect(MobEffectCategory.NEUTRAL,0x000000));
    public static final Holder<MobEffect> CHESED_DARKEN = MOB_EFFECTS.register("chesed_darken", ()->new SimpleEffect(MobEffectCategory.NEUTRAL,0x000000));
    public static final Holder<MobEffect> CHESED_ENERGIZED = MOB_EFFECTS.register("chesed_energized", ()->new SimpleEffect(MobEffectCategory.NEUTRAL,0x36bfff));


}
