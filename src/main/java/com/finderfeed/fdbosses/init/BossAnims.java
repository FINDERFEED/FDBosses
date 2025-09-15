package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.FDRegistries;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class BossAnims {


    public static DeferredRegister<Animation> ANIMATIONS = DeferredRegister.create(FDRegistries.ANIMATIONS_KEY, FDBosses.MOD_ID);

    public static RegistryObject<Animation> CHESED_IDLE = ANIMATIONS.register("chesed_idle", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static RegistryObject<Animation> CHESED_BOOM_ATTACK = ANIMATIONS.register("chesed_boom_attack", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static RegistryObject<Animation> CHESED_ATTACK = ANIMATIONS.register("chesed_attack", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static RegistryObject<Animation> CHESED_ROLL_UP = ANIMATIONS.register("chesed_roll_up", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static RegistryObject<Animation> CHESED_ROLL_UP_END = ANIMATIONS.register("chesed_roll_up_end", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static RegistryObject<Animation> CHESED_SECOND_PHASE_PREPARE = ANIMATIONS.register("chesed_second_phase_prepare", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static RegistryObject<Animation> CHESED_SECOND_PHASE_STRIKE_WALLS = ANIMATIONS.register("chesed_second_phase_strike_walls", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static RegistryObject<Animation> CHESED_ROLL_UP_JUST_END /*_ALREADY YOU_ROLLING_BASTARD*/ = ANIMATIONS.register("chesed_roll_up_just_end", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static RegistryObject<Animation> CHESED_ROLL = ANIMATIONS.register("chesed_roll", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static RegistryObject<Animation> CHESED_ROLL_ROLL = ANIMATIONS.register("chesed_roll_roll", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static RegistryObject<Animation> CHESED_CAST = ANIMATIONS.register("chesed_cast", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static RegistryObject<Animation> CHESED_EARTHQUAKE_CAST = ANIMATIONS.register("chesed_earthquake_cast", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static RegistryObject<Animation> CHESED_ROCKFALL_CAST = ANIMATIONS.register("chesed_rockfall_cast", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static RegistryObject<Animation> ELECTRIC_ORB_IDLE = ANIMATIONS.register("electric_orb_idle", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"electric_orb"));
    });
    public static RegistryObject<Animation> CHESED_CRYSTAL_SPAWN = ANIMATIONS.register("chesed_crystal_spawn", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed_crystal_animations"));
    });
    public static RegistryObject<Animation> CHESED_MONOLITH_IDLE = ANIMATIONS.register("chesed_monolith_idle", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed_monolith"));
    });
    public static RegistryObject<Animation> CHESED_MONOLITH_TURN_OFF = ANIMATIONS.register("chesed_monolith_turn_off", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed_monolith"));
    });
    public static RegistryObject<Animation> CHESED_KINETIC_FIELD_SPAWN = ANIMATIONS.register("kinetic_field_spawn", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"kinetic_field"));
    });
    public static RegistryObject<Animation> CHESED_APPEAR = ANIMATIONS.register("chesed_appear", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static RegistryObject<Animation> CHESED_SCREEN_APPEAR = ANIMATIONS.register("chesed_screen_appear", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static RegistryObject<Animation> CHESED_DEATH = ANIMATIONS.register("chesed_death", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed"));
    });
    public static RegistryObject<Animation> RAY_REFLECTOR_ACTIVATE = ANIMATIONS.register("ray_reflector_activate", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"chesed_ray_reflector_animations"));
    });


    //MALKUTH
    public static RegistryObject<Animation> MALKUTH_TEST = ANIMATIONS.register("test_malkuth", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_IDLE = ANIMATIONS.register("malkuth_idle", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_SLASH_FIRE = ANIMATIONS.register("malkuth_slash_fire", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_SLASH_ICE = ANIMATIONS.register("malkuth_slash_ice", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_JUMP_FORWARD_PREPARE = ANIMATIONS.register("malkuth_jump_forward_prepare", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_JUMP_CRUSH_ATTACK_START = ANIMATIONS.register("malkuth_jump_crush_attack_start", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_JUMP_CRUSH_ATTACK_MIDAIR = ANIMATIONS.register("malkuth_jump_crush_attack_midair", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_JUMP_CRUSH_ATTACK_END = ANIMATIONS.register("malkuth_jump_crush_attack_end", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_PULL_AND_PUNCH = ANIMATIONS.register("malkuth_pull_and_punch", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_SCREEN_APPEAR = ANIMATIONS.register("malkuth_screen_appear", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_SCREEN_IDLE = ANIMATIONS.register("malkuth_screen_idle", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_CRUSH_ATTACK_CRUSH = ANIMATIONS.register("malkuth_crush_attack_attack", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_crush_attack"));
    });

    public static RegistryObject<Animation> MALKUTH_CANNON_SHOOT = ANIMATIONS.register("malkuth_cannon_shoot", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_cannon"));
    });

    public static RegistryObject<Animation> MALKUTH_CANNON_BREAK = ANIMATIONS.register("malkuth_cannon_break", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_cannon"));
    });

    public static RegistryObject<Animation> MALKUTH_CANNON_REPAIR = ANIMATIONS.register("malkuth_cannon_repair", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_cannon"));
    });

    public static RegistryObject<Animation> MALKUTH_CANNON_SUMMON = ANIMATIONS.register("malkuth_cannon_summon", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_cannon"));
    });

    public static RegistryObject<Animation> MALKUTH_JUMP_AND_LAND = ANIMATIONS.register("malkuth_jump_and_land", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_JUMP_AND_CRUSH = ANIMATIONS.register("malkuth_jump_with_crush", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_CRUSH_ATTACK_FULL = ANIMATIONS.register("malkuth_jump_crush_attack_full", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_SWORD_FORWARD = ANIMATIONS.register("malkuth_sword_forward", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_CAROUSEL_SLASH_1 = ANIMATIONS.register("malkuth_carousel_slash_1", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_GIANT_SWORD_ATTACK = ANIMATIONS.register("malkuth_giant_sword_attack", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_SINGLE_EARTHQUAKE_FIRE = ANIMATIONS.register("malkuth_single_earthquake_fire", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_SINGLE_EARTHQUAKE_ICE = ANIMATIONS.register("malkuth_single_earthquake_ice", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_SUMMON_THROW_SIDE_STONES = ANIMATIONS.register("malkuth_summon_throw_side_stones", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_JUMP_TO_FLOAT = ANIMATIONS.register("malkuth_jump_to_float", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_FLOAT = ANIMATIONS.register("malkuth_float", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_SUMMON_AND_FIRE_FIREBALLS = ANIMATIONS.register("malkuth_summon_fireballs", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_REPAIR_CRYSTAL_SUMMON = ANIMATIONS.register("malkuth_crystal_summon", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_repair_crystal"));
    });

    public static RegistryObject<Animation> MALKUTH_SUMMON_ANIM = ANIMATIONS.register("malkuth_summon_anim", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

    public static RegistryObject<Animation> MALKUTH_WARRIOR_IDLE = ANIMATIONS.register("malkuth_warrior_idle", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_warrior"));
    });

    public static RegistryObject<Animation> MALKUTH_WARRIOR_ATTACK = ANIMATIONS.register("malkuth_warrior_attack", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_warrior"));
    });

    public static RegistryObject<Animation> MALKUTH_WARRIOR_WALK = ANIMATIONS.register("malkuth_warrior_walk", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_warrior"));
    });

    public static RegistryObject<Animation> MALKUTH_WARRIOR_SLAM_EARTH = ANIMATIONS.register("malkuth_warrior_slam_earth", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_warrior"));
    });

    public static RegistryObject<Animation> MALKUTH_WARRIOR_DIE = ANIMATIONS.register("malkuth_warrior_die", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth_warrior"));
    });

    public static RegistryObject<Animation> MALKUTH_DEATH = ANIMATIONS.register("malkuth_death", ()->{
        return new Animation(ResourceLocation.tryBuild(FDBosses.MOD_ID,"malkuth"));
    });

}
