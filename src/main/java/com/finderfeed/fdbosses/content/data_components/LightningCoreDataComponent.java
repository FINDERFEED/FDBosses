package com.finderfeed.fdbosses.content.data_components;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class LightningCoreDataComponent {

    public static final Codec<LightningCoreDataComponent> CODEC = Codec.unit(new LightningCoreDataComponent());

    public static final StreamCodec<RegistryFriendlyByteBuf, LightningCoreDataComponent> STREAM_CODEC = StreamCodec.unit(new LightningCoreDataComponent());

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
