package com.finderfeed.fdbosses.content.entities.geburah.judgement_ball_projectile;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdlib.util.FDByteBufCodecs;
import com.finderfeed.fdlib.util.FDCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public class JudgementBallExplosionParticleOptions implements ParticleOptions {

    public static final MapCodec<JudgementBallExplosionParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(v->v.group(
            FDCodecs.VEC3.fieldOf("movement").forGetter(a->a.movement),
            Codec.INT.fieldOf("lifetime").forGetter(a->a.lifetime),
            Codec.FLOAT.fieldOf("size").forGetter(a->a.size)
    ).apply(v, JudgementBallExplosionParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, JudgementBallExplosionParticleOptions> STREAM_CODEC = StreamCodec.composite(
            FDByteBufCodecs.VEC3, v->v.movement,
            ByteBufCodecs.INT,v->v.lifetime,
            ByteBufCodecs.FLOAT,v->v.size,
            JudgementBallExplosionParticleOptions::new
    );

    public Vec3 movement;
    public float size;
    public int lifetime;

    public JudgementBallExplosionParticleOptions(Vec3 movement,int lifetime, float size){
        this.movement = movement;
        this.size = size;
        this.lifetime = lifetime;
    }

    @Override
    public ParticleType<?> getType() {
        return BossParticles.JUDGEMENT_BALL_EXPLOSION.get();
    }

}
