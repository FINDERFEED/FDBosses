package com.finderfeed.fdbosses.client.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public class GravityOptionsParticleType extends ParticleType<GravityParticleOptions> {


    public GravityOptionsParticleType(boolean overrideLimiter) {
        super(overrideLimiter, GravityParticleOptions.DESERIALIZER);

    }

    @Override
    public Codec<GravityParticleOptions> codec() {
        return GravityParticleOptions.CODEC;
    }


}
