package com.finderfeed.fdbosses.client.particles.vanilla_like;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ASParticleType extends ParticleType<SpriteParticleOptions> {

    public ASParticleType(boolean overrideLimiter) {
        super(overrideLimiter);
    }

    @Override
    public MapCodec<SpriteParticleOptions> codec() {
        return SpriteParticleOptions.codec(this);
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, SpriteParticleOptions> streamCodec() {
        return SpriteParticleOptions.streamCodec(this);
    }

}
