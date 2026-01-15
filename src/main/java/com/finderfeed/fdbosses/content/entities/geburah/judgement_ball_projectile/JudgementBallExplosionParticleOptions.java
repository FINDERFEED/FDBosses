package com.finderfeed.fdbosses.content.entities.geburah.judgement_ball_projectile;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import com.finderfeed.fdlib.util.FDByteBufCodecs;
import com.finderfeed.fdlib.util.FDCodecs;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public class JudgementBallExplosionParticleOptions implements ParticleOptions {

    public static final Deserializer<JudgementBallExplosionParticleOptions> DESERIALIZER = new Deserializer<JudgementBallExplosionParticleOptions>() {
        @Override
        public JudgementBallExplosionParticleOptions fromCommand(ParticleType<JudgementBallExplosionParticleOptions> p_123733_, StringReader p_123734_) throws CommandSyntaxException {
            return new JudgementBallExplosionParticleOptions(new Vec3(0,1,0),10,1);
        }

        @Override
        public JudgementBallExplosionParticleOptions fromNetwork(ParticleType<JudgementBallExplosionParticleOptions> p_123735_, FriendlyByteBuf p_123736_) {
            return STREAM_CODEC.fromNetwork(p_123736_);
        }
    };

    public static final Codec<JudgementBallExplosionParticleOptions> MAP_CODEC = RecordCodecBuilder.create(v->v.group(
            FDCodecs.VEC3.fieldOf("movement").forGetter(a->a.movement),
            Codec.INT.fieldOf("lifetime").forGetter(a->a.lifetime),
            Codec.FLOAT.fieldOf("size").forGetter(a->a.size)
    ).apply(v, JudgementBallExplosionParticleOptions::new));

    public static final NetworkCodec<JudgementBallExplosionParticleOptions> STREAM_CODEC = NetworkCodec.composite(
            NetworkCodec.VEC3, v->v.movement,
            NetworkCodec.INT,v->v.lifetime,
            NetworkCodec.FLOAT,v->v.size,
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

    @Override
    public void writeToNetwork(FriendlyByteBuf p_123732_) {
        STREAM_CODEC.toNetwork(p_123732_, this);
    }

    @Override
    public String writeToString() {
        return "";
    }

}
