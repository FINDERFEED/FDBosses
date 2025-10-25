package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.geburah.SinsEntityDataSerializer;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSin;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash.MalkuthSlashTypeSerializer;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public class BossEntityDataSerializers {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, FDBosses.MOD_ID);

    public static final DeferredHolder<EntityDataSerializer<?>, MalkuthSlashTypeSerializer> MALKUTH_ATTACK_TYPE = ENTITY_DATA_SERIALIZERS.register("malkuth_attack", MalkuthSlashTypeSerializer::new);

    public static final DeferredHolder<EntityDataSerializer<?>, SinsEntityDataSerializer> SINS = ENTITY_DATA_SERIALIZERS.register("sins", SinsEntityDataSerializer::new);

}
