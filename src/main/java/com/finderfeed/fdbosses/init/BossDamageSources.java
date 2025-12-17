package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber(modid = FDBosses.MOD_ID)
public class BossDamageSources {

    public static final ResourceKey<DamageType> CHESED_ATTACK = key("chesed_attack");
    public static final ResourceKey<DamageType> CHESED_LOR_EASTER_EGG_ATTACK = key("chesed_lor_attack");
    public static final ResourceKey<DamageType> CHESED_BA_EASTER_EGG_ATTACK = key("chesed_ba_attack");
    public static final ResourceKey<DamageType> CHESED_ELECTRIC_SPHERE = key("electric_sphere");
    public static final ResourceKey<DamageType> CHESED_FALLING_BLOCK = key("chesed_falling_block");
    public static final ResourceKey<DamageType> CHESED_VERTICAL_RAY = key("chesed_vertical_ray");
    public static final ResourceKey<DamageType> CHESED_EARTHQUAKE = key("chesed_earthquake");
    public static final ResourceKey<DamageType> CHESED_ROLL = key("chesed_roll_attack");
    public static final ResourceKey<DamageType> CHESED_BLOCK_ATTACK = key("chesed_rock_attack");

    public static final ResourceKey<DamageType> MALKUTH_COWARDICE = key("malkuth_cowardice");
    public static final ResourceKey<DamageType> MALKUTH_CANNONS = key("malkuth_cannons");
    public static final ResourceKey<DamageType> MALKUTH_CHAINPUNCH = key("malkuth_chainpunch");
    public static final ResourceKey<DamageType> MALKUTH_EARTHSHATTER = key("malkuth_earthshatter");
    public static final ResourceKey<DamageType> MALKUTH_HELLSHAPER = key("malkuth_hellshaper");
    public static final ResourceKey<DamageType> MALKUTH_IMPALING_DOOM = key("malkuth_impaling_doom");
    public static final ResourceKey<DamageType> MALKUTH_SIDE_ROCKS = key("malkuth_side_rocks");
    public static final ResourceKey<DamageType> MALKUTH_SLASHES = key("malkuth_slashes");
    public static final ResourceKey<DamageType> MALKUTH_TSARS_WRATH = key("malkuth_tsars_wrath");

    public static final ResourceKey<DamageType> GEBURAH_JUDGEMENT_BALL = key("geburah_judgement_ball");
    public static final ResourceKey<DamageType> GEBURAH_RAY_STRIKE = key("geburah_ray_strike");
    public static final ResourceKey<DamageType> GEBURAH_LASER_STRIKE = key("geburah_laser_strike");
    public static final ResourceKey<DamageType> GEBURAH_EARTHQUAKE = key("geburah_earthquake");
    public static final ResourceKey<DamageType> GEBURAH_JUSTICE_HAMMER = key("geburah_justice_hammer");
    public static final ResourceKey<DamageType> GEBURAH_SINNED_TOO_MUCH = key("sinned_too_much");


    private static EntityDamageSource CHESED_ATTACK_SOURCE;
    private static EntityDamageSource CHESED_LOR_ATTACK_SOURCE;
    private static EntityDamageSource CHESED_BA_ATTACK_SOURCE;
    public static DamageSource CHESED_ELECTRIC_SPHERE_SOURCE;
    public static DamageSource CHESED_FALLING_BLOCK_SOURCE;
    public static DamageSource CHESED_VERTICAL_RAY_SOURCE;
    public static DamageSource CHESED_EARTHQUAKE_SOURCE;
    public static DamageSource CHESED_ROLL_SOURCE;
    public static DamageSource CHESED_BLOCK_ATTACK_SOURCE;

    public static DamageSource MALKUTH_COWARDICE_SOURCE;
    public static DamageSource MALKUTH_CANNONS_SOURCE;
    public static DamageSource MALKUTH_CHAINPUNCH_SOURCE;
    public static DamageSource MALKUTH_EARTHSHATTER_SOURCE;
    public static DamageSource MALKUTH_HELLSHAPER_SOURCE;
    public static DamageSource MALKUTH_IMPALING_DOOM_SOURCE;
    public static DamageSource MALKUTH_SIDE_ROCKS_SOURCE;
    public static DamageSource MALKUTH_SLASHES_SOURCE;
    public static DamageSource MALKUTH_TSARS_WRATH_SOURCE;


    public static DamageSource GEBURAH_JUDGEMENT_BALL_SOURCE;
    public static DamageSource GEBURAH_RAY_STRIKE_SOURCE;
    public static DamageSource GEBURAH_LASER_STRIKE_SOURCE;
    public static DamageSource GEBURAH_EARTHQUAKE_SOURCE;
    public static DamageSource GEBURAH_SINNED_TOO_MUCH_SOURCE;
    public static DamageSource GEBURAH_JUSTICE_HAMMER_SOURCE;

    public static DamageSource chesedAttack(Entity attacker){
        return CHESED_ATTACK_SOURCE.create(attacker);
    }

    public static DamageSource chesedLorAttack(Entity attacker){
        return CHESED_LOR_ATTACK_SOURCE.create(attacker);
    }

    public static DamageSource chesedBaAttack(Entity attacker){
        return CHESED_BA_ATTACK_SOURCE.create(attacker);
    }

    @SubscribeEvent
    public static void registerDamageTypes(ServerStartedEvent event){
        RegistryAccess access = event.getServer().registryAccess();



        CHESED_ATTACK_SOURCE = new EntityDamageSource(access.holderOrThrow(CHESED_ATTACK));
        CHESED_LOR_ATTACK_SOURCE = new EntityDamageSource(access.holderOrThrow(CHESED_LOR_EASTER_EGG_ATTACK));
        CHESED_BA_ATTACK_SOURCE = new EntityDamageSource(access.holderOrThrow(CHESED_BA_EASTER_EGG_ATTACK));
        CHESED_ELECTRIC_SPHERE_SOURCE = new DamageSource(access.holderOrThrow(CHESED_ELECTRIC_SPHERE));
        CHESED_FALLING_BLOCK_SOURCE = new DamageSource(access.holderOrThrow(CHESED_FALLING_BLOCK));
        CHESED_VERTICAL_RAY_SOURCE = new DamageSource(access.holderOrThrow(CHESED_VERTICAL_RAY));
        CHESED_EARTHQUAKE_SOURCE = new DamageSource(access.holderOrThrow(CHESED_EARTHQUAKE));
        CHESED_ROLL_SOURCE = new DamageSource(access.holderOrThrow(CHESED_ROLL));
        CHESED_BLOCK_ATTACK_SOURCE = new DamageSource(access.holderOrThrow(CHESED_BLOCK_ATTACK));

        MALKUTH_COWARDICE_SOURCE = new DamageSource(access.holderOrThrow(MALKUTH_COWARDICE));
        MALKUTH_CANNONS_SOURCE = new DamageSource(access.holderOrThrow(MALKUTH_CANNONS));
        MALKUTH_CHAINPUNCH_SOURCE = new DamageSource(access.holderOrThrow(MALKUTH_CHAINPUNCH));
        MALKUTH_HELLSHAPER_SOURCE = new DamageSource(access.holderOrThrow(MALKUTH_HELLSHAPER));
        MALKUTH_IMPALING_DOOM_SOURCE = new DamageSource(access.holderOrThrow(MALKUTH_IMPALING_DOOM));
        MALKUTH_SIDE_ROCKS_SOURCE = new DamageSource(access.holderOrThrow(MALKUTH_SIDE_ROCKS));
        MALKUTH_SLASHES_SOURCE = new DamageSource(access.holderOrThrow(MALKUTH_SLASHES));
        MALKUTH_TSARS_WRATH_SOURCE = new DamageSource(access.holderOrThrow(MALKUTH_TSARS_WRATH));
        MALKUTH_EARTHSHATTER_SOURCE = new DamageSource(access.holderOrThrow(MALKUTH_EARTHSHATTER));

        GEBURAH_JUDGEMENT_BALL_SOURCE = new DamageSource(access.holderOrThrow(GEBURAH_JUDGEMENT_BALL));
        GEBURAH_RAY_STRIKE_SOURCE = new DamageSource(access.holderOrThrow(GEBURAH_RAY_STRIKE));
        GEBURAH_LASER_STRIKE_SOURCE = new DamageSource(access.holderOrThrow(GEBURAH_LASER_STRIKE));
        GEBURAH_EARTHQUAKE_SOURCE = new DamageSource(access.holderOrThrow(GEBURAH_EARTHQUAKE));
        GEBURAH_SINNED_TOO_MUCH_SOURCE = new DamageSource(access.holderOrThrow(GEBURAH_SINNED_TOO_MUCH));
        GEBURAH_JUSTICE_HAMMER_SOURCE = new DamageSource(access.holderOrThrow(GEBURAH_JUSTICE_HAMMER));

    }


    private static ResourceKey<DamageType> key(String name){
        return ResourceKey.create(Registries.DAMAGE_TYPE,FDBosses.location(name));
    }

    public static class EntityDamageSource{
        private Holder<DamageType> damageTypeHolder;

        public EntityDamageSource(Holder<DamageType> typeHolder){
            this.damageTypeHolder = typeHolder;
        }

        public DamageSource create(Entity attacker){
            return new DamageSource(damageTypeHolder,attacker);
        }
    }

}
