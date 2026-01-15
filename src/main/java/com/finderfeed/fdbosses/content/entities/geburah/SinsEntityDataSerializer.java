package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSin;
import com.finderfeed.fdbosses.init.BossRegistries;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.ArrayList;
import java.util.List;

public class SinsEntityDataSerializer implements EntityDataSerializer<List<PlayerSin>> {

    public static final NetworkCodec<List<PlayerSin>> STREAM_CODEC = NetworkCodec.listOf(NetworkCodec.registry(()->BossRegistries.PLAYER_SINS.get()));


    @Override
    public void write(FriendlyByteBuf buf, List<PlayerSin> players) {
        STREAM_CODEC.toNetwork(buf, players);
    }

    @Override
    public List<PlayerSin> read(FriendlyByteBuf buf) {
        return STREAM_CODEC.fromNetwork(buf);
    }

    @Override
    public List<PlayerSin> copy(List<PlayerSin> sins) {
        return new ArrayList<>(sins);
    }

}
