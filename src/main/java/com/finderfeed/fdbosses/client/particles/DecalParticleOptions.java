package com.finderfeed.fdbosses.client.particles;

import com.finderfeed.fdlib.util.FDByteBufCodecs;
import com.finderfeed.fdlib.util.FDCodecs;
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
import net.minecraft.world.phys.Vec3;

public class DecalParticleOptions implements ParticleOptions {

    public static final MapCodec<DecalParticleOptions> CODEC = RecordCodecBuilder.mapCodec(p->p.group(
            BuiltInRegistries.PARTICLE_TYPE.byNameCodec().fieldOf("particle_type").forGetter(v->v.particleType),
            FDCodecs.VEC3.fieldOf("direction").forGetter(v->v.direction),
            AlphaOptions.CODEC.fieldOf("alphaOptions").forGetter(v->v.alphaOptions),
            Codec.FLOAT.fieldOf("size").forGetter(v->v.size)
    ).apply(p, DecalParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DecalParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.PARTICLE_TYPE),v->v.particleType,
            FDByteBufCodecs.VEC3,v->v.direction,
            AlphaOptions.STREAM_CODEC,v->v.alphaOptions,
            ByteBufCodecs.FLOAT,v->v.size,
            DecalParticleOptions::new
    );

    private ParticleType<?> particleType;
    private Vec3 direction;
    private AlphaOptions alphaOptions;
    private float size;

    public DecalParticleOptions(ParticleType<?> particleType, Vec3 direction, AlphaOptions alphaOptions, float size){
        this.particleType = particleType;
        this.direction = direction;
        this.alphaOptions = alphaOptions;
        this.size = size;
    }

    public float getSize() {
        return size;
    }

    public Vec3 getDirection() {
        return direction;
    }

    public AlphaOptions getAlphaOptions() {
        return alphaOptions;
    }

    public ParticleType<?> getParticleType() {
        return particleType;
    }

    @Override
    public ParticleType<?> getType() {
        return particleType;
    }

}
