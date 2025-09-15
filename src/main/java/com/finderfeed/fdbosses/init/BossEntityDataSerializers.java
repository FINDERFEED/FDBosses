package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash.MalkuthSlashTypeSerializer;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class BossEntityDataSerializers {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, FDBosses.MOD_ID);

    public static final RegistryObject<EntityDataSerializer<?>, MalkuthSlashTypeSerializer> MALKUTH_ATTACK_TYPE = ENTITY_DATA_SERIALIZERS.register("malkuth_attack", MalkuthSlashTypeSerializer::new);

}
