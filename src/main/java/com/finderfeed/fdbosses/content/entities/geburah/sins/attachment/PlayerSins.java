package com.finderfeed.fdbosses.content.entities.geburah.sins.attachment;

import com.finderfeed.fdbosses.init.BossRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public class PlayerSins {

    public static final Codec<PlayerSins> CODEC = RecordCodecBuilder.create(p->p.group(
            Codec.list(ActivePlayerSinInstance.CODEC).fieldOf("active_sins").forGetter(v->v.activeSins),
            Codec.INT.fieldOf("sinned_times").forGetter(v->v.sinnedTimes)
    ).apply(p, PlayerSins::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerSins> STREAM_CODEC = StreamCodec.composite(
            ActivePlayerSinInstance.STREAM_CODEC.apply(ByteBufCodecs.list()),v->v.activeSins,
            ByteBufCodecs.INT,v -> v.sinnedTimes,
            PlayerSins::new
    );

    private List<ActivePlayerSinInstance> activeSins = new ArrayList<>();

    private int sinnedTimes = 0;

    public PlayerSins(){}

    private PlayerSins(List<ActivePlayerSinInstance> activeSins, int sinnedTimes){
        this(activeSins);
        this.sinnedTimes = sinnedTimes;
    }

    public PlayerSins(PlayerSins other){
        this.activeSins = new ArrayList<>(other.activeSins);
    }

    public PlayerSins(List<ActivePlayerSinInstance> activeSins){
        this.activeSins = new ArrayList<>(activeSins);
    }

}
