package com.finderfeed.fdbosses.client.particles;

import com.finderfeed.fdlib.util.client.particles.options.AlphaOptions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class GravityParticleOptions implements ParticleOptions {

    public static final MapCodec<GravityParticleOptions> CODEC = RecordCodecBuilder.mapCodec(p->p.group(
            BuiltInRegistries.PARTICLE_TYPE.byNameCodec().fieldOf("type").forGetter(v->v.particleType),
            Codec.INT.fieldOf("lifetime").forGetter(v->v.lifetime),
            Codec.FLOAT.fieldOf("quadSize").forGetter(v->v.quadSize),
            Codec.FLOAT.fieldOf("gravity").forGetter(v->v.gravity),
            Codec.FLOAT.fieldOf("rotationModifier").forGetter(v->v.rotationModifier),
            Codec.BOOL.fieldOf("fadeOut").forGetter(v->v.fadeOut)
    ).apply(p,GravityParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GravityParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.PARTICLE_TYPE),v->v.particleType,
            ByteBufCodecs.INT,v->v.lifetime,
            ByteBufCodecs.FLOAT,v->v.quadSize,
            ByteBufCodecs.FLOAT,v->v.gravity,
            ByteBufCodecs.FLOAT,v->v.rotationModifier,
            ByteBufCodecs.BOOL,v->v.fadeOut,
            GravityParticleOptions::new
    );

    private ParticleType<?> particleType;
    private int lifetime;
    private float gravity;
    private float rotationModifier;
    private float quadSize;
    private boolean fadeOut;

    public GravityParticleOptions(ParticleType<?> particleType,int lifetime, float quadSize, float gravity, float rotationModifier, boolean fadeOut){
        this.particleType = particleType;
        this.gravity = gravity;
        this.rotationModifier = rotationModifier;
        this.quadSize = quadSize;
        this.fadeOut = fadeOut;
        this.lifetime = lifetime;
    }

    public float getGravity() {
        return gravity;
    }

    public float getQuadSize() {
        return quadSize;
    }

    public float getRotationModifier() {
        return rotationModifier;
    }

    public boolean isFadeOut() {
        return fadeOut;
    }

    public int getLifetime() {
        return lifetime;
    }

    @Override
    public ParticleType<?> getType() {
        return particleType;
    }

}
