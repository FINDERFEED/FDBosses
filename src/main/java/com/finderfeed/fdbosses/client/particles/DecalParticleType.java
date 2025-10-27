package com.finderfeed.fdbosses.client.particles;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class DecalParticleType extends ParticleType<DecalParticleOptions> {

    public DecalParticleType(boolean p_123740_) {
        super(p_123740_);
    }

    @Override
    public MapCodec<DecalParticleOptions> codec() {
        return DecalParticleOptions.CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, DecalParticleOptions> streamCodec() {
        return DecalParticleOptions.STREAM_CODEC;
    }
}
