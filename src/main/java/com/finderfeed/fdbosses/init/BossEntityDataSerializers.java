package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity.EarthShatterEntityDataSerializer;
import com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity.EarthShatterSettings;
import com.finderfeed.fdbosses.content.entities.geburah.SinsEntityDataSerializer;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash.MalkuthSlashTypeSerializer;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class BossEntityDataSerializers {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, FDBosses.MOD_ID);

    public static final RegistryObject<MalkuthSlashTypeSerializer> MALKUTH_ATTACK_TYPE = ENTITY_DATA_SERIALIZERS.register("malkuth_attack", MalkuthSlashTypeSerializer::new);

    public static final RegistryObject<EarthShatterEntityDataSerializer> EARTH_SHATTER_SETTINGS = ENTITY_DATA_SERIALIZERS.register("earthshatter_settings", EarthShatterEntityDataSerializer::new);

    public static final RegistryObject<SinsEntityDataSerializer> SINS = ENTITY_DATA_SERIALIZERS.register("sins", SinsEntityDataSerializer::new);


}
