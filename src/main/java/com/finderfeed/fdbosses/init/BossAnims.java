package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.FDRegistries;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BossAnims {


    public static DeferredRegister<Animation> ANIMATIONS = DeferredRegister.create(FDRegistries.ANIMATIONS, FDBosses.MOD_ID);

    public static DeferredHolder<Animation,Animation> CHESED_IDLE = ANIMATIONS.register("chesed_idle", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_BOOM_ATTACK = ANIMATIONS.register("chesed_boom_attack", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_ATTACK = ANIMATIONS.register("chesed_attack", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_ROLL_UP = ANIMATIONS.register("chesed_roll_up", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_ROLL_UP_END = ANIMATIONS.register("chesed_roll_up_end", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_SECOND_PHASE_PREPARE = ANIMATIONS.register("chesed_second_phase_prepare", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_SECOND_PHASE_STRIKE_WALLS = ANIMATIONS.register("chesed_second_phase_strike_walls", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_ROLL_UP_JUST_END /*_ALREADY YOU_ROLLING_BASTARD*/ = ANIMATIONS.register("chesed_roll_up_just_end", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_ROLL = ANIMATIONS.register("chesed_roll", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_ROLL_ROLL = ANIMATIONS.register("chesed_roll_roll", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_CAST = ANIMATIONS.register("chesed_cast", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_EARTHQUAKE_CAST = ANIMATIONS.register("chesed_earthquake_cast", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_ROCKFALL_CAST = ANIMATIONS.register("chesed_rockfall_cast", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static DeferredHolder<Animation,Animation> ELECTRIC_ORB_IDLE = ANIMATIONS.register("electric_orb_idle", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"electric_orb"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_CRYSTAL_SPAWN = ANIMATIONS.register("chesed_crystal_spawn", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed_crystal_animations"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_MONOLITH_IDLE = ANIMATIONS.register("chesed_monolith_idle", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed_monolith"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_MONOLITH_TURN_OFF = ANIMATIONS.register("chesed_monolith_turn_off", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed_monolith"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_KINETIC_FIELD_SPAWN = ANIMATIONS.register("kinetic_field_spawn", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"kinetic_field"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_APPEAR = ANIMATIONS.register("chesed_appear", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_SCREEN_APPEAR = ANIMATIONS.register("chesed_screen_appear", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static DeferredHolder<Animation,Animation> CHESED_DEATH = ANIMATIONS.register("chesed_death", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static DeferredHolder<Animation,Animation> RAY_REFLECTOR_ACTIVATE = ANIMATIONS.register("ray_reflector_activate", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed_ray_reflector_animations"));
    });


    //MALKUTH
    public static DeferredHolder<Animation,Animation> MALKUTH_TEST = ANIMATIONS.register("test_malkuth", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static DeferredHolder<Animation,Animation> MALKUTH_IDLE = ANIMATIONS.register("malkuth_idle", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static DeferredHolder<Animation,Animation> MALKUTH_SLASH_FIRE = ANIMATIONS.register("malkuth_slash_fire", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static DeferredHolder<Animation,Animation> MALKUTH_SLASH_ICE = ANIMATIONS.register("malkuth_slash_ice", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static DeferredHolder<Animation,Animation> MALKUTH_JUMP_FORWARD_PREPARE = ANIMATIONS.register("malkuth_jump_forward_prepare", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static DeferredHolder<Animation,Animation> MALKUTH_JUMP_CRUSH_ATTACK_START = ANIMATIONS.register("malkuth_jump_crush_attack_start", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static DeferredHolder<Animation,Animation> MALKUTH_JUMP_CRUSH_ATTACK_MIDAIR = ANIMATIONS.register("malkuth_jump_crush_attack_midair", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static DeferredHolder<Animation,Animation> MALKUTH_JUMP_CRUSH_ATTACK_END = ANIMATIONS.register("malkuth_jump_crush_attack_end", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static DeferredHolder<Animation,Animation> MALKUTH_PULL_AND_PUNCH = ANIMATIONS.register("malkuth_pull_and_punch", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static DeferredHolder<Animation,Animation> MALKUTH_CRUSH_ATTACK_CRUSH = ANIMATIONS.register("malkuth_crush_attack_attack", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_crush_attack"));
    });

    public static DeferredHolder<Animation,Animation> MALKUTH_CANNON_SHOOT = ANIMATIONS.register("malkuth_cannon_shoot", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_cannon"));
    });

    public static DeferredHolder<Animation,Animation> MALKUTH_CANNON_SUMMON = ANIMATIONS.register("malkuth_cannon_summon", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_cannon"));
    });

    public static DeferredHolder<Animation,Animation> MALKUTH_JUMP_AND_LAND = ANIMATIONS.register("malkuth_jump_and_land", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static DeferredHolder<Animation,Animation> MALKUTH_JUMP_AND_CRUSH = ANIMATIONS.register("malkuth_jump_with_crush", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static DeferredHolder<Animation,Animation> MALKUTH_CRUSH_ATTACK_FULL = ANIMATIONS.register("malkuth_jump_crush_attack_full", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static DeferredHolder<Animation,Animation> MALKUTH_SWORD_FORWARD = ANIMATIONS.register("malkuth_sword_forward", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static DeferredHolder<Animation,Animation> MALKUTH_CAROUSEL_SLASH_1 = ANIMATIONS.register("malkuth_carousel_slash_1", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

}
