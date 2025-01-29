package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.effects.ChesedDarkenEffect;
import com.finderfeed.fdbosses.effects.ChesedGazeEffect;
import com.finderfeed.fdbosses.FDBosses;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BossEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, FDBosses.MOD_ID);

    public static final Holder<MobEffect> CHESED_GAZE = MOB_EFFECTS.register("chesed_gaze", ChesedGazeEffect::new);
    public static final Holder<MobEffect> CHESED_DARKEN = MOB_EFFECTS.register("chesed_darken", ChesedDarkenEffect::new);


}
