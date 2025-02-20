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


    private static EntityDamageSource CHESED_ATTACK_SOURCE;

    public static DamageSource chesedAttack(Entity attacker){
        return CHESED_ATTACK_SOURCE.create(attacker);
    }

    @SubscribeEvent
    public static void registerDamageTypes(ServerStartedEvent event){
        RegistryAccess access = event.getServer().registryAccess();

        CHESED_ATTACK_SOURCE = new EntityDamageSource(access.holderOrThrow(CHESED_ATTACK));

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
