package com.finderfeed.fdbosses.client.particles;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class GravityOptionsParticleType extends ParticleType<GravityParticleOptions> {


    public GravityOptionsParticleType(boolean overrideLimiter) {
        super(overrideLimiter);

    }

    @Override
    public MapCodec<GravityParticleOptions> codec() {
        return GravityParticleOptions.CODEC;
    }

    @Override
    public StreamCodec<? super FriendlyByteBuf, GravityParticleOptions> streamCodec() {
        return GravityParticleOptions.STREAM_CODEC;
    }
}
