package com.finderfeed.fdbosses.client.particles.arc_preparation_particle;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdlib.util.FDByteBufCodecs;
import com.finderfeed.fdlib.util.FDCodecs;
import com.finderfeed.fdlib.util.FDColor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public class ArcAttackPreparationParticleOptions implements ParticleOptions {

    public static final StreamCodec<FriendlyByteBuf, ArcAttackPreparationParticleOptions> STREAM_CODEC = FDByteBufCodecs.composite(
            FDByteBufCodecs.VEC3,v->v.horizontalDirection,
            ByteBufCodecs.FLOAT,v->v.length,
            ByteBufCodecs.FLOAT,v->v.halfAttackAngle,
            ByteBufCodecs.INT,v->v.attackChargeTime,
            ByteBufCodecs.INT,v->v.fadeIn,
            ByteBufCodecs.INT,v->v.fadeOut,
            FDByteBufCodecs.COLOR,v->v.color,
            ((vec3, aFloat, aFloat2, integer, integer2, integer3, fdColor) -> {
                ArcAttackPreparationParticleOptions options = new ArcAttackPreparationParticleOptions(vec3,aFloat,aFloat2,integer, integer2, integer3, fdColor.r,fdColor.g,fdColor.b,fdColor.a);
                return options;
            })
    );

    public static final MapCodec<ArcAttackPreparationParticleOptions> CODEC = RecordCodecBuilder.mapCodec(p->p.group(
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
}
