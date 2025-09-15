package com.finderfeed.fdbosses.client.particles.arc_preparation_particle;

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

public class ArcAttackPreparationParticleOptions implements ParticleOptions {

    public static final Deserializer<ArcAttackPreparationParticleOptions> DESERIALIZER = new Deserializer<ArcAttackPreparationParticleOptions>() {
        @Override
        public ArcAttackPreparationParticleOptions fromCommand(ParticleType<ArcAttackPreparationParticleOptions> p_123733_, StringReader p_123734_) throws CommandSyntaxException {
            return new ArcAttackPreparationParticleOptions(null,1,1,1,1,1,1,1,1,1);
        }

        @Override
        public ArcAttackPreparationParticleOptions fromNetwork(ParticleType<ArcAttackPreparationParticleOptions> p_123735_, FriendlyByteBuf p_123736_) {
            return ArcAttackPreparationParticleOptions.STREAM_CODEC.fromNetwork(p_123736_);
        }
    };

    public static final NetworkCodec<ArcAttackPreparationParticleOptions> STREAM_CODEC = NetworkCodec.composite(
            NetworkCodec.VEC3,v->v.horizontalDirection,
            NetworkCodec.FLOAT,v->v.length,
            NetworkCodec.FLOAT,v->v.halfAttackAngle,
            NetworkCodec.INT,v->v.attackChargeTime,
            NetworkCodec.INT,v->v.fadeIn,
            NetworkCodec.INT,v->v.fadeOut,
            NetworkCodec.COLOR,v->v.color,
            ((vec3, aFloat, aFloat2, integer, integer2, integer3, fdColor) -> {
                ArcAttackPreparationParticleOptions options = new ArcAttackPreparationParticleOptions(vec3,aFloat,aFloat2,integer, integer2, integer3, fdColor.r,fdColor.g,fdColor.b,fdColor.a);
                return options;
            })
    );

    public static final Codec<ArcAttackPreparationParticleOptions> CODEC = RecordCodecBuilder.create(p->p.group(
            FDCodecs.VEC3.fieldOf("horizontal_direction").forGetter(v->v.horizontalDirection),
            Codec.FLOAT.fieldOf("length").forGetter(v->v.length),
            Codec.FLOAT.fieldOf("halfAttackAngle").forGetter(v->v.halfAttackAngle),
            Codec.INT.fieldOf("attachChargeTime").forGetter(v->v.attackChargeTime),
            Codec.INT.fieldOf("fadeIn").forGetter(v->v.fadeIn),
            Codec.INT.fieldOf("fadeOut").forGetter(v->v.fadeOut),
            FDCodecs.COLOR.fieldOf("color").forGetter(v->v.color)
    ).apply(p,((vec3, aFloat, aFloat2, integer, integer2, integer3, fdColor) -> {
        ArcAttackPreparationParticleOptions options = new ArcAttackPreparationParticleOptions(vec3,aFloat,aFloat2,integer, integer2, integer3, fdColor.r,fdColor.g,fdColor.b,fdColor.a);
        return options;
    })));

    private Vec3 horizontalDirection;
    private float length;
    private float halfAttackAngle;
    private int attackChargeTime;
    private int fadeIn;
    private int fadeOut;
    private FDColor color;

    public ArcAttackPreparationParticleOptions(Vec3 horizontalDirection, float length, float halfAttackAngle, int attackChargeTime, int fadeIn, int fadeOut, float r, float g, float b, float a){
        this.horizontalDirection = horizontalDirection;
        this.length = length;
        this.halfAttackAngle = halfAttackAngle;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.attackChargeTime = attackChargeTime;
        this.color = new FDColor(r,g,b,a);
    }

    public FDColor getColor() {
        return color;
    }

    public float getLength() {
        return length;
    }

    public float getHalfAttackAngle() {
        return halfAttackAngle;
    }

    public Vec3 getHorizontalDirection() {
        return horizontalDirection;
    }

    public int getAttackChargeTime() {
        return attackChargeTime;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public int getFadeOut() {
        return fadeOut;
    }

    @Override
    public ParticleType<?> getType() {
        return BossParticles.ARC_ATTACK_PREPARATION_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf p_123732_) {
        STREAM_CODEC.toNetwork(p_123732_, this);
    }

    @Override
    public String writeToString() {
        return "zhopa";
    }
}
