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

@EventBusSubscriber(modid = FDBosses.MOD_ID,bus = EventBusSubscriber.Bus.GAME)
public class BossDamageSources {

    public static final ResourceKey<DamageType> CHESED_ATTACK = key("chesed_attack");
    public static final ResourceKey<DamageType> CHESED_ELECTRIC_SPHERE = key("electric_sphere");
    public static final ResourceKey<DamageType> CHESED_FALLING_BLOCK = key("chesed_falling_block");


    private static EntityDamageSource CHESED_ATTACK_SOURCE;
    private static EntityDamageSource CHESED_ELECTRIC_SPHERE_SOURCE;
    public static DamageSource CHESED_FALLING_BLOCK_SOURCE;

    public static DamageSource chesedAttack(Entity attacker){
        return CHESED_ATTACK_SOURCE.create(attacker);
    }

    public static DamageSource electricSphere(Entity attacker){
        return CHESED_ELECTRIC_SPHERE_SOURCE.create(attacker);
    }

    @SubscribeEvent
    public static void registerDamageTypes(ServerStartedEvent event){
        RegistryAccess access = event.getServer().registryAccess();

        CHESED_ATTACK_SOURCE = new EntityDamageSource(access.holderOrThrow(CHESED_ATTACK));
        CHESED_ELECTRIC_SPHERE_SOURCE = new EntityDamageSource(access.holderOrThrow(CHESED_ELECTRIC_SPHERE));
        CHESED_FALLING_BLOCK_SOURCE = new DamageSource(access.holderOrThrow(CHESED_FALLING_BLOCK));
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
