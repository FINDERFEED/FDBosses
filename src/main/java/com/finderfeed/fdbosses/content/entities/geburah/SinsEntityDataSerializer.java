package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSin;
import com.finderfeed.fdbosses.init.BossRegistries;
import com.finderfeed.fdbosses.init.GeburahSins;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.ArrayList;
import java.util.List;

public class SinsEntityDataSerializer implements EntityDataSerializer<List<PlayerSin>> {

    public static final StreamCodec<RegistryFriendlyByteBuf, List<PlayerSin>> STREAM_CODEC = ByteBufCodecs.registry(BossRegistries.PLAYER_SIN).apply(ByteBufCodecs.list());

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, List<PlayerSin>> codec() {
        return STREAM_CODEC;
    }

    @Override
    public List<PlayerSin> copy(List<PlayerSin> p_135023_) {
        return new ArrayList<>(p_135023_);
    }

}
