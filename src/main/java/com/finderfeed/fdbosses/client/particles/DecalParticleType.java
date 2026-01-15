package com.finderfeed.fdbosses.client.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;

public class DecalParticleType extends ParticleType<DecalParticleOptions> {

    public DecalParticleType(boolean p_123740_) {
        super(p_123740_, DecalParticleOptions.DESERIALIZER);
    }

    @Override
    public Codec<DecalParticleOptions> codec() {
        return DecalParticleOptions.CODEC;
    }


}
