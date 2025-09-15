package com.finderfeed.fdbosses.client.particles.rush_particle;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import com.finderfeed.fdlib.util.FDCodecs;
import com.finderfeed.fdlib.util.FDColor;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.stringtemplate.v4.ST;

public class RushParticleOptions implements ParticleOptions {

    public static final Deserializer<RushParticleOptions> DESERIALIZER = new Deserializer<RushParticleOptions>() {
        @Override
        public RushParticleOptions fromCommand(ParticleType<RushParticleOptions> p_123733_, StringReader p_123734_) throws CommandSyntaxException {
            return new RushParticleOptions(null,null,1,1,1);
        }

        @Override
        public RushParticleOptions fromNetwork(ParticleType<RushParticleOptions> p_123735_, FriendlyByteBuf p_123736_) {
            return STREAM_CODEC.fromNetwork(p_123736_);
        }
    };

    public static final Codec<RushParticleOptions> CODEC = RecordCodecBuilder.create(p->p.group(
            FDCodecs.VEC3.fieldOf("rushDirection").forGetter(v->v.rushDirection),
            FDCodecs.COLOR.fieldOf("color").forGetter(v->v.color),
            Codec.FLOAT.fieldOf("length").forGetter(v->v.length),
            Codec.FLOAT.fieldOf("width").forGetter(v->v.width),
            Codec.INT.fieldOf("lifetime").forGetter(v->v.lifetime)
    ).apply(p,RushParticleOptions::new));

    public static final NetworkCodec<RushParticleOptions> STREAM_CODEC = NetworkCodec.composite(
            NetworkCodec.VEC3,v->v.rushDirection,
            NetworkCodec.COLOR,v->v.color,
            NetworkCodec.FLOAT,v->v.length,
            NetworkCodec.FLOAT,v->v.width,
            NetworkCodec.INT, v->v.lifetime,
            RushParticleOptions::new
    );

    private Vec3 rushDirection;
    private FDColor color;
    private int lifetime;
    private float length;
    private float width;

    public RushParticleOptions(Vec3 rushDirection, FDColor color, float length, float width, int lifetime){
        this.rushDirection = rushDirection;
        this.color = color;
        this.lifetime = lifetime;
        this.length = length;
        this.width = width;
    }

    @Override
    public ParticleType<?> getType() {
        return BossParticles.RUSH_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf p_123732_) {
        STREAM_CODEC.toNetwork(p_123732_,this);
    }

    @Override
    public String writeToString() {
        return "zhopa";
    }

    public float getLength() {
        return length;
    }

    public Vec3 getRushDirection() {
        return rushDirection;
    }

    public int getLifetime() {
        return lifetime;
    }

    public FDColor getColor() {
        return color;
    }

    public float getWidth() {
        return width;
    }

}
