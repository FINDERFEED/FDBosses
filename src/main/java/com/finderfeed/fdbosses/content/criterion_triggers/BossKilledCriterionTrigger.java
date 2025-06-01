package com.finderfeed.fdbosses.content.criterion_triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public class BossKilledCriterionTrigger extends SimpleCriterionTrigger<BossKilledCriterionTrigger.Instance> {


    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player, EntityType<?> bossEntityType){
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(bossEntityType);
        this.trigger(player,inst->inst.bossEntityType.equals(key.toString()));
    }

    public void trigger(ServerPlayer player, Entity entity){
        this.trigger(player,entity.getType());
    }

    public static record Instance(Optional<ContextAwarePredicate> player, String bossEntityType) implements SimpleInstance{

        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(p->p.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),
                Codec.STRING.fieldOf("boss_entity_type").forGetter(Instance::bossEntityType)
        ).apply(p,Instance::new));

    }

}
