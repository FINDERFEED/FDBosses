package com.finderfeed.fdbosses.client.particles;

import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import com.finderfeed.fdlib.util.client.particles.options.AlphaOptions;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

public class GravityParticleOptions implements ParticleOptions {

    public static final Deserializer<GravityParticleOptions> DESERIALIZER = new Deserializer<GravityParticleOptions>() {
        @Override
        public GravityParticleOptions fromCommand(ParticleType<GravityParticleOptions> p_123733_, StringReader p_123734_) throws CommandSyntaxException {
            return null;
        }

        @Override
        public GravityParticleOptions fromNetwork(ParticleType<GravityParticleOptions> p_123735_, FriendlyByteBuf p_123736_) {
            return STREAM_CODEC.fromNetwork(p_123736_);
        }
    };

    public static final Codec<GravityParticleOptions> CODEC = RecordCodecBuilder.create(p->p.group(
            BuiltInRegistries.PARTICLE_TYPE.byNameCodec().fieldOf("type").forGetter(v->v.particleType),
            Codec.INT.fieldOf("lifetime").forGetter(v->v.lifetime),
            Codec.FLOAT.fieldOf("quadSize").forGetter(v->v.quadSize),
            Codec.FLOAT.fieldOf("gravity").forGetter(v->v.gravity),
            Codec.FLOAT.fieldOf("rotationModifier").forGetter(v->v.rotationModifier),
            Codec.BOOL.fieldOf("fadeOut").forGetter(v->v.fadeOut)
    ).apply(p,GravityParticleOptions::new));

    public static final NetworkCodec<GravityParticleOptions> STREAM_CODEC = NetworkCodec.composite(
            NetworkCodec.registry(()->ForgeRegistries.PARTICLE_TYPES), v->v.particleType,
            NetworkCodec.INT,v->v.lifetime,
            NetworkCodec.FLOAT,v->v.quadSize,
            NetworkCodec.FLOAT,v->v.gravity,
            NetworkCodec.FLOAT,v->v.rotationModifier,
            NetworkCodec.BOOL,v->v.fadeOut,
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

    @Override
    public void writeToNetwork(FriendlyByteBuf p_123732_) {
        STREAM_CODEC.toNetwork(p_123732_,this);
    }

    @Override
    public String writeToString() {
        return "";
    }

}
