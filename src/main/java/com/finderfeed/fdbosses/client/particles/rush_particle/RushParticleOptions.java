package com.finderfeed.fdbosses.client.particles.rush_particle;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdlib.util.FDByteBufCodecs;
import com.finderfeed.fdlib.util.FDCodecs;
import com.finderfeed.fdlib.util.FDColor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public class RushParticleOptions implements ParticleOptions {

    public static final MapCodec<RushParticleOptions> CODEC = RecordCodecBuilder.mapCodec(p->p.group(
            FDCodecs.VEC3.fieldOf("rushDirection").forGetter(v->v.rushDirection),
            FDCodecs.COLOR.fieldOf("color").forGetter(v->v.color),
            Codec.INT.fieldOf("lifetime").forGetter(v->v.lifetime)
    ).apply(p,RushParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RushParticleOptions> STREAM_CODEC = StreamCodec.composite(
            FDByteBufCodecs.VEC3,v->v.rushDirection,
            FDByteBufCodecs.COLOR,v->v.color,
            ByteBufCodecs.INT, v->v.lifetime,
            RushParticleOptions::new
    );

    private Vec3 rushDirection;
    private FDColor color;
    private int lifetime;

    public RushParticleOptions(Vec3 rushDirection, FDColor color, int lifetime){
        this.rushDirection = rushDirection;
        this.color = color;
        this.lifetime = lifetime;
    }

    @Override
    public ParticleType<?> getType() {
        return BossParticles.RUSH_PARTICLE.get();
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
}
