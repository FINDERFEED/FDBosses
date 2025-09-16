package com.finderfeed.fdbosses.content.criterion_triggers;

import com.finderfeed.fdbosses.FDBosses;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class BossKilledCriterionTrigger extends SimpleCriterionTrigger<BossKilledCriterionTrigger.Instance> {


    public void trigger(ServerPlayer player, EntityType<?> bossEntityType){
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(bossEntityType);
        this.trigger(player,inst->inst.bossEntityType.equals(key.toString()));
    }

    public void trigger(ServerPlayer player, Entity entity){
        this.trigger(player,entity.getType());
    }

    private ResourceLocation id;

    public BossKilledCriterionTrigger(){
        this.id = FDBosses.location("boss_killed");
    }

    @Override
    protected Instance createInstance(JsonObject json, ContextAwarePredicate predicate, DeserializationContext ctx) {
        return new Instance(this.getId(),predicate,json.get("boss_entity_type").getAsString());
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public static class Instance extends AbstractCriterionTriggerInstance {

        private String bossEntityType;

        public Instance(ResourceLocation location, ContextAwarePredicate predicate, String bossEntityType) {
            super(location, predicate);
            this.bossEntityType = bossEntityType;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext ctx) {
            JsonObject jsonObject = super.serializeToJson(ctx);
            jsonObject.addProperty("boss_entity_type",this.bossEntityType);
            return jsonObject;
        }
    }

}
