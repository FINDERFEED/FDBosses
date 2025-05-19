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
    public static final Supplier<SoundEvent> CHESED_CRYSTAL_HIT = SOUNDS.register("chesed_crystal_hit",()->SoundEvent.createVariableRangeEvent(FDBosses.location("chesed_crystal_hit")));
    public static final Supplier<SoundEvent> RUMBLING_SHORT = SOUNDS.register("rumbling_short",()->SoundEvent.createVariableRangeEvent(FDBosses.location("rumbling_short")));
    public static final Supplier<SoundEvent> GHOST_WEAPON_FLY_OUT = SOUNDS.register("ghost_weapon_fly_out",()->SoundEvent.createVariableRangeEvent(FDBosses.location("ghost_weapon_fly_out")));
    public static final Supplier<SoundEvent> LIGHTNING_RAY_PASS = SOUNDS.register("lightning_ray_pass",()->SoundEvent.createVariableRangeEvent(FDBosses.location("lightning_ray_pass")));
    public static final Supplier<SoundEvent> ELECTRIC_HUM = SOUNDS.register("electric_spheres_release",()->SoundEvent.createVariableRangeEvent(FDBosses.location("electric_spheres_release")));
    public static final Supplier<SoundEvent> ELECTRIC_SPHERES_CAST = SOUNDS.register("electric_spheres_start",()->SoundEvent.createVariableRangeEvent(FDBosses.location("electric_spheres_start")));
    public static final Supplier<SoundEvent> ELECTRIC_SPHERES_STORM = SOUNDS.register("electric_sphere_storm",()->SoundEvent.createVariableRangeEvent(FDBosses.location("electric_sphere_storm")));
    public static final Supplier<SoundEvent> ELECTRIC_SPHERES_STORM_END = SOUNDS.register("electric_sphere_storm_end",()->SoundEvent.createVariableRangeEvent(FDBosses.location("electric_sphere_storm_end")));
    public static final Supplier<SoundEvent> CHESED_DEATH = SOUNDS.register("chesed_death",()->SoundEvent.createVariableRangeEvent(FDBosses.location("chesed_death")));
    public static final Supplier<SoundEvent> ATTACK_DING = SOUNDS.register("attack_ding",()->SoundEvent.createVariableRangeEvent(FDBosses.location("attack_ding")));
    public static final Supplier<SoundEvent> CHESED_RAY_CHARGE = SOUNDS.register("chesed_ray_charge",()->SoundEvent.createVariableRangeEvent(FDBosses.location("chesed_ray_charge")));
    public static final Supplier<SoundEvent> CHESED_RAY_CHARGE_FAST = SOUNDS.register("chesed_ray_charge_fast",()->SoundEvent.createVariableRangeEvent(FDBosses.location("chesed_ray_charge_fast")));
    public static final Supplier<SoundEvent> MONOLITH_HIT = SOUNDS.register("monolith_hit",()->SoundEvent.createVariableRangeEvent(FDBosses.location("monolith_hit")));
    public static final Supplier<SoundEvent> CHESED_IDLE = SOUNDS.register("chesed_idle",()->SoundEvent.createVariableRangeEvent(FDBosses.location("chesed_idle")));


}
