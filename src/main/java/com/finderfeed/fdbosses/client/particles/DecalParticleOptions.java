package com.finderfeed.fdbosses.client.particles;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import com.finderfeed.fdlib.util.FDCodecs;
import com.finderfeed.fdlib.util.client.particles.options.AlphaOptions;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class DecalParticleOptions implements ParticleOptions {

    public static final Deserializer<DecalParticleOptions> DESERIALIZER = new Deserializer<DecalParticleOptions>() {
        @Override
        public DecalParticleOptions fromCommand(ParticleType<DecalParticleOptions> particleType, StringReader stringReader) throws CommandSyntaxException {
            return new DecalParticleOptions(BossParticles.GEBURAH_RAY_DECAL.get(), new Vec3(0,1,0), new AlphaOptions(),0.25f);
        }

        @Override
        public DecalParticleOptions fromNetwork(ParticleType<DecalParticleOptions> particleType, FriendlyByteBuf friendlyByteBuf) {
            return STREAM_CODEC.fromNetwork(friendlyByteBuf);
        }
    };

    public static final Codec<DecalParticleOptions> CODEC = RecordCodecBuilder.create(p->p.group(
            BuiltInRegistries.PARTICLE_TYPE.byNameCodec().fieldOf("particle_type").forGetter(v->v.particleType),
            FDCodecs.VEC3.fieldOf("direction").forGetter(v->v.direction),
            AlphaOptions.CODEC.fieldOf("alphaOptions").forGetter(v->v.alphaOptions),
            Codec.FLOAT.fieldOf("size").forGetter(v->v.size)
    ).apply(p, DecalParticleOptions::new));

    public static final NetworkCodec<DecalParticleOptions> STREAM_CODEC = NetworkCodec.composite(
            NetworkCodec.registry(()->ForgeRegistries.PARTICLE_TYPES), v->v.particleType,
            NetworkCodec.VEC3,v->v.direction,
            AlphaOptions.STREAM_CODEC,v->v.alphaOptions,
            NetworkCodec.FLOAT,v->v.size,
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

    @Override
    public void writeToNetwork(FriendlyByteBuf friendlyByteBuf) {
        STREAM_CODEC.toNetwork(friendlyByteBuf,this);
    }

    @Override
    public String writeToString() {
        return "";
    }

}
