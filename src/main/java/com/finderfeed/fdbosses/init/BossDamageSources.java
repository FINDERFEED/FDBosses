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
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FDBosses.MOD_ID,bus = Mod.EventBusSubscriber.Bus.FORGE)
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

        var registry = access.registryOrThrow(Registries.DAMAGE_TYPE);


        CHESED_ATTACK_SOURCE = new EntityDamageSource(registry.getHolderOrThrow(CHESED_ATTACK));
        CHESED_LOR_ATTACK_SOURCE = new EntityDamageSource(registry.getHolderOrThrow(CHESED_LOR_EASTER_EGG_ATTACK));
        CHESED_BA_ATTACK_SOURCE = new EntityDamageSource(registry.getHolderOrThrow(CHESED_BA_EASTER_EGG_ATTACK));
        CHESED_ELECTRIC_SPHERE_SOURCE = new DamageSource(registry.getHolderOrThrow(CHESED_ELECTRIC_SPHERE));
        CHESED_FALLING_BLOCK_SOURCE = new DamageSource(registry.getHolderOrThrow(CHESED_FALLING_BLOCK));
        CHESED_VERTICAL_RAY_SOURCE = new DamageSource(registry.getHolderOrThrow(CHESED_VERTICAL_RAY));
        CHESED_EARTHQUAKE_SOURCE = new DamageSource(registry.getHolderOrThrow(CHESED_EARTHQUAKE));
        CHESED_ROLL_SOURCE = new DamageSource(registry.getHolderOrThrow(CHESED_ROLL));
        CHESED_BLOCK_ATTACK_SOURCE = new DamageSource(registry.getHolderOrThrow(CHESED_BLOCK_ATTACK));

        MALKUTH_COWARDICE_SOURCE = new DamageSource(registry.getHolderOrThrow(MALKUTH_COWARDICE));
        MALKUTH_CANNONS_SOURCE = new DamageSource(registry.getHolderOrThrow(MALKUTH_CANNONS));
        MALKUTH_CHAINPUNCH_SOURCE = new DamageSource(registry.getHolderOrThrow(MALKUTH_CHAINPUNCH));
        MALKUTH_HELLSHAPER_SOURCE = new DamageSource(registry.getHolderOrThrow(MALKUTH_HELLSHAPER));
        MALKUTH_IMPALING_DOOM_SOURCE = new DamageSource(registry.getHolderOrThrow(MALKUTH_IMPALING_DOOM));
        MALKUTH_SIDE_ROCKS_SOURCE = new DamageSource(registry.getHolderOrThrow(MALKUTH_SIDE_ROCKS));
        MALKUTH_SLASHES_SOURCE = new DamageSource(registry.getHolderOrThrow(MALKUTH_SLASHES));
        MALKUTH_TSARS_WRATH_SOURCE = new DamageSource(registry.getHolderOrThrow(MALKUTH_TSARS_WRATH));
        MALKUTH_EARTHSHATTER_SOURCE = new DamageSource(registry.getHolderOrThrow(MALKUTH_EARTHSHATTER));

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
