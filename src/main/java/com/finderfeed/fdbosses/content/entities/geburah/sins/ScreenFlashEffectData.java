package com.finderfeed.fdbosses.content.entities.geburah.sins;

import com.finderfeed.fdlib.systems.screen.screen_effect.ScreenEffectData;
import com.finderfeed.fdlib.util.FDByteBufCodecs;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.client.particles.options.AlphaOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ScreenFlashEffectData extends ScreenEffectData {

    public static final StreamCodec<RegistryFriendlyByteBuf, ScreenFlashEffectData> STREAM_CODEC = StreamCodec.composite(
            FDByteBufCodecs.COLOR, v->v.color,
            ByteBufCodecs.FLOAT, v->v.flashSizePercent,
            ScreenFlashEffectData::new
    );

    protected FDColor color;
    protected float flashSizePercent;

    public ScreenFlashEffectData(FDColor color, float flashSizePercent){
        this.color = color;
        this.flashSizePercent = flashSizePercent;
    }

}
