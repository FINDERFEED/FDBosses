package com.finderfeed.fdbosses.content.entities.geburah.sins;

import com.finderfeed.fdlib.systems.screen.screen_effect.ScreenEffectData;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import com.finderfeed.fdlib.util.FDColor;

public class ScreenFlashEffectData extends ScreenEffectData {

    public static final NetworkCodec<ScreenFlashEffectData> STREAM_CODEC = NetworkCodec.composite(
            NetworkCodec.COLOR, v->v.color,
            NetworkCodec.FLOAT, v->v.flashSizePercent,
            ScreenFlashEffectData::new
    );

    protected FDColor color;
    protected float flashSizePercent;

    public ScreenFlashEffectData(FDColor color, float flashSizePercent){
        this.color = color;
        this.flashSizePercent = flashSizePercent;
    }

}
