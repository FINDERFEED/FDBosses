package com.finderfeed.fdbosses.content.entities.geburah.sins.attachment;

import com.finderfeed.fdbosses.content.util.WorldBox;
import com.finderfeed.fdbosses.init.BossRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ActivePlayerSinInstance {

    public static final StreamCodec<RegistryFriendlyByteBuf, ActivePlayerSinInstance> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(BossRegistries.PLAYER_SIN),v->v.sin,
            WorldBox.STREAM_CODEC,v->v.sinActiveBox,
            ByteBufCodecs.INT,v->v.activeSinTime,
            ByteBufCodecs.INT,v->v.customData,
            ActivePlayerSinInstance::new
    );

    public static final Codec<ActivePlayerSinInstance> CODEC = RecordCodecBuilder.create(p->p.group(
            BossRegistries.PLAYER_SINS.byNameCodec().fieldOf("sin").forGetter(v->v.sin),
            WorldBox.CODEC.fieldOf("worldBox").forGetter(v->v.sinActiveBox),
            Codec.INT.fieldOf("activeSinTime").fieldOf("activeSinTime").forGetter(v->v.activeSinTime),
            Codec.INT.fieldOf("customData").forGetter(v->v.customData)
    ).apply(p, ActivePlayerSinInstance::new));

    
    private PlayerSin sin;

    private WorldBox sinActiveBox;

    private int activeSinTime;

    private int customData;

    public ActivePlayerSinInstance(PlayerSin playerSin, WorldBox sinActiveBox, int customData){
        this.sin = playerSin;
        activeSinTime = 0;
        this.sinActiveBox = sinActiveBox;
        this.customData = customData;
    }
    
    public ActivePlayerSinInstance(PlayerSin sin, WorldBox sinActiveBox, int activeSinTime, int customData){
        this.activeSinTime = activeSinTime;
        this.sin = sin;
        this.sinActiveBox = sinActiveBox;
        this.customData = customData;
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

    public int getCustomData() {
        return customData;
    }

    public void setCustomData(int customData) {
        this.customData = customData;
    }

    public WorldBox getSinActiveBox() {
        return sinActiveBox;
    }

}
