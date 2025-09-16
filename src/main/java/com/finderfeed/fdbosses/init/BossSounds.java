package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class BossSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, FDBosses.MOD_ID);


    public static final Supplier<SoundEvent> BUTTON_CLICK = SOUNDS.register("button_click",()->SoundEvent.createVariableRangeEvent(FDBosses.location("button_click")));
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

    public static final Supplier<SoundEvent> MALKUTH_THEME_INTRO = SOUNDS.register("malkuth_theme_intro",()->SoundEvent.createVariableRangeEvent(FDBosses.location("malkuth_theme_intro")));
    public static final Supplier<SoundEvent> MALKUTH_THEME_MAIN = SOUNDS.register("malkuth_theme_main",()->SoundEvent.createVariableRangeEvent(FDBosses.location("malkuth_theme_main")));
    public static final Supplier<SoundEvent> MALKUTH_CANNON_SHOOT = SOUNDS.register("malkuth_cannon_shoot",()->SoundEvent.createVariableRangeEvent(FDBosses.location("malkuth_cannon_shoot")));
    public static final Supplier<SoundEvent> MALKUTH_EARTHQUAKE_SPIKE = SOUNDS.register("malkuth_earthquake_spike",()->SoundEvent.createVariableRangeEvent(FDBosses.location("malkuth_earthquake_spike")));
    public static final Supplier<SoundEvent> MALKUTH_EARTHQUAKE_ROLLING = SOUNDS.register("malkuth_earthquake_rolling",()->SoundEvent.createVariableRangeEvent(FDBosses.location("malkuth_earthquake_rolling")));
    public static final Supplier<SoundEvent> MALKUTH_SWORD_EARTH_IMPACT = SOUNDS.register("malkuth_sword_earth_impact",()->SoundEvent.createVariableRangeEvent(FDBosses.location("malkuth_sword_earth_impact")));
    public static final Supplier<SoundEvent> MALKUTH_SWORD_SLASH = SOUNDS.register("malkuth_sword_slash",()->SoundEvent.createVariableRangeEvent(FDBosses.location("malkuth_sword_slash")));
    public static final Supplier<SoundEvent> MALKUTH_PUNCH = SOUNDS.register("malkuth_punch",()->SoundEvent.createVariableRangeEvent(FDBosses.location("malkuth_punch")));
    public static final Supplier<SoundEvent> MALKUTH_CHAIN_PULL = SOUNDS.register("malkuth_chain_pull",()->SoundEvent.createVariableRangeEvent(FDBosses.location("malkuth_chain_pull")));
    public static final Supplier<SoundEvent> MALKUTH_FIREBALL_LAUNCH = SOUNDS.register("malkuth_fireball_launch",()->SoundEvent.createVariableRangeEvent(FDBosses.location("malkuth_fireball_launch")));
    public static final Supplier<SoundEvent> MALKUTH_FIREBALL_EXPLOSION = SOUNDS.register("malkuth_fireball_explosion",()->SoundEvent.createVariableRangeEvent(FDBosses.location("malkuth_fireball_explosion")));
    public static final Supplier<SoundEvent> MALKUTH_ICE_FIREBALL_EXPLOSION = SOUNDS.register("malkuth_ice_fireball_explosion",()->SoundEvent.createVariableRangeEvent(FDBosses.location("malkuth_ice_fireball_explosion")));
    public static final Supplier<SoundEvent> MALKUTH_SLASH = SOUNDS.register("malkuth_slash",()->SoundEvent.createVariableRangeEvent(FDBosses.location("malkuth_slash")));
    public static final Supplier<SoundEvent> MALKUTH_VOLCANO_ERRUPTION = SOUNDS.register("malkuth_volcano_erruption",()->SoundEvent.createVariableRangeEvent(FDBosses.location("malkuth_volcano_erruption")));
    public static final Supplier<SoundEvent> MALKUTH_VOLCANO_RUMBLING_LOOP = SOUNDS.register("volcano_rumbling_loop",()->SoundEvent.createVariableRangeEvent(FDBosses.location("volcano_rumbling_loop")));
    public static final Supplier<SoundEvent> MALKUTH_HIT = SOUNDS.register("malkuth_hit",()->SoundEvent.createVariableRangeEvent(FDBosses.location("malkuth_hit")));
    public static final Supplier<SoundEvent> MALKUTH_WARRIOR_HIT = SOUNDS.register("malkuth_warrior_hit",()->SoundEvent.createVariableRangeEvent(FDBosses.location("malkuth_warrior_hit")));
    public static final Supplier<SoundEvent> MALKUTH_SWORD_ULTIMATE_IMPACT = SOUNDS.register("malkuth_sword_ultimate_impact",()->SoundEvent.createVariableRangeEvent(FDBosses.location("malkuth_sword_ultimate_impact")));


}
