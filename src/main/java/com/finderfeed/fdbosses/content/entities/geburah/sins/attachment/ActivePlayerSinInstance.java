package com.finderfeed.fdbosses.content.entities.geburah.sins.attachment;

import com.finderfeed.fdbosses.init.BossRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ActivePlayerSinInstance {

    public static final StreamCodec<RegistryFriendlyByteBuf, ActivePlayerSinInstance> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(BossRegistries.PLAYER_SIN),v->v.sin,
            ByteBufCodecs.INT,v->v.activeSinTime,
            ActivePlayerSinInstance::new
    );

    public static final Codec<ActivePlayerSinInstance> CODEC = RecordCodecBuilder.create(p->p.group(
            BossRegistries.PLAYER_SINS.byNameCodec().fieldOf("sin").forGetter(v->v.sin),
            Codec.INT.fieldOf("activeSinTime").fieldOf("activeSinTime").forGetter(v->v.activeSinTime)
    ).apply(p, ActivePlayerSinInstance::new));
    
    private PlayerSin sin;
    
    private int activeSinTime;

    public ActivePlayerSinInstance(PlayerSin playerSin){
        this.sin = playerSin;
        activeSinTime = 0;
    }
    
    public ActivePlayerSinInstance(PlayerSin sin, int activeSinTime){
        this.activeSinTime = activeSinTime;
        this.sin = sin;
    }

    public int getActiveSinTime() {
        return activeSinTime;
    }

    public PlayerSin getSin() {
        return sin;
    }

    public void setActiveSinTime(int activeSinTime) {
        this.activeSinTime = activeSinTime;
    }

    public void setSin(PlayerSin sin) {
        this.sin = sin;
    }

}
