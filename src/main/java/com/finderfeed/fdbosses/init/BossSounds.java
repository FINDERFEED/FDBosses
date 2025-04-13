package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, FDBosses.MOD_ID);


    public static final Supplier<SoundEvent> CHESED_RAY = SOUNDS.register("ray_attack",()->SoundEvent.createVariableRangeEvent(FDBosses.location("ray_attack")));
    public static final Supplier<SoundEvent> ROCKFALL = SOUNDS.register("rockfall",()->SoundEvent.createVariableRangeEvent(FDBosses.location("rockfall")));
    public static final Supplier<SoundEvent> RUMBLING = SOUNDS.register("rumbling",()->SoundEvent.createVariableRangeEvent(FDBosses.location("rumbling")));
    public static final Supplier<SoundEvent> CHESED_LIGHTNING_RAY = SOUNDS.register("lightning_attack",()->SoundEvent.createVariableRangeEvent(FDBosses.location("lightning_attack")));
    public static final Supplier<SoundEvent> CHESED_FINAL_ATTACK_EXPLOSION = SOUNDS.register("chesed_boom_explosion",()->SoundEvent.createVariableRangeEvent(FDBosses.location("chesed_boom_explosion")));
    public static final Supplier<SoundEvent> CHESED_FINAL_ATTACK_EXPLOSION_BIGGER = SOUNDS.register("chesed_boom_explosion_bigger",()->SoundEvent.createVariableRangeEvent(FDBosses.location("chesed_boom_explosion_bigger")));
    public static final Supplier<SoundEvent> CHESED_FINAL_ATTACK_EXPLOSION_PREPARE = SOUNDS.register("chesed_boom_explosion_prepare",()->SoundEvent.createVariableRangeEvent(FDBosses.location("chesed_boom_explosion_prepare")));
    public static final Supplier<SoundEvent> CHESED_FINAL_ATTACK_RAY = SOUNDS.register("chesed_boom_ray",()->SoundEvent.createVariableRangeEvent(FDBosses.location("chesed_boom_ray")));
    public static final Supplier<SoundEvent> ROCK_IMPACT = SOUNDS.register("rock_impact",()->SoundEvent.createVariableRangeEvent(FDBosses.location("rock_impact")));
    public static final Supplier<SoundEvent> CHESED_FLOOR_SMASH = SOUNDS.register("chesed_floor_smash",()->SoundEvent.createVariableRangeEvent(FDBosses.location("chesed_floor_smash")));
    public static final Supplier<SoundEvent> FAST_LIGHTNING_STRIKE = SOUNDS.register("fast_lightning_strike",()->SoundEvent.createVariableRangeEvent(FDBosses.location("fast_lightning_strike")));
    public static final Supplier<SoundEvent> THROW_STUFF = SOUNDS.register("throw_stuff",()->SoundEvent.createVariableRangeEvent(FDBosses.location("throw_stuff")));
    public static final Supplier<SoundEvent> CHESED_OPEN = SOUNDS.register("chesed_open",()->SoundEvent.createVariableRangeEvent(FDBosses.location("chesed_open")));
    public static final Supplier<SoundEvent> CHESED_FINAL_ATTACK_CHARGE = SOUNDS.register("final_attack_charge",()->SoundEvent.createVariableRangeEvent(FDBosses.location("final_attack_charge")));


}
