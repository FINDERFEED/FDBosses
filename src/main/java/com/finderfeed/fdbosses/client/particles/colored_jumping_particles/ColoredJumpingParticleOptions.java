package com.finderfeed.fdbosses.client.particles.colored_jumping_particles;

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
import net.minecraft.world.entity.player.Player;

public class ColoredJumpingParticleOptions implements ParticleOptions {

    public static final StreamCodec<RegistryFriendlyByteBuf, ColoredJumpingParticleOptions> STREAM_CODEC = FDByteBufCodecs.composite(
                    FDByteBufCodecs.COLOR, v -> v.colorStart,
                    FDByteBufCodecs.COLOR, v -> v.colorEnd,
                    ByteBufCodecs.INT, v -> v.maxPointsInTrail,
                    ByteBufCodecs.FLOAT, v -> v.reflectionStrength,
                    ByteBufCodecs.FLOAT, v -> v.gravity,
                    ByteBufCodecs.INT, v -> v.lifetime,
                    ByteBufCodecs.FLOAT, v -> v.size,
                    ByteBufCodecs.INT, v -> v.maxJumpAmount,
                    ColoredJumpingParticleOptions::new
            );

    public static final MapCodec<ColoredJumpingParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    FDCodecs.COLOR.fieldOf("colorStart").forGetter(o -> o.colorStart),
                    FDCodecs.COLOR.fieldOf("colorEnd").forGetter(o -> o.colorEnd),
                    Codec.INT.fieldOf("max_points_in_trail").forGetter(o -> o.maxPointsInTrail),
                    Codec.FLOAT.fieldOf("reflection_strength").forGetter(o -> o.reflectionStrength),
                    Codec.FLOAT.fieldOf("gravity").forGetter(o -> o.gravity),
                    Codec.INT.fieldOf("lifetime").forGetter(o -> o.lifetime),
                    Codec.FLOAT.fieldOf("size").forGetter(o -> o.size),
                    Codec.INT.fieldOf("max_jump_amount").forGetter(o -> o.maxJumpAmount)
            ).apply(instance, ColoredJumpingParticleOptions::new)
    );


    public FDColor colorStart;
    public FDColor colorEnd;
    public int maxPointsInTrail;
    public float reflectionStrength;
    public float gravity;
    public int lifetime;
    public float size;
    public int maxJumpAmount;


    public ColoredJumpingParticleOptions(FDColor colorStart, FDColor colorEnd, int maxPointsInTrail, float reflectionStrength, float gravity, int lifetime, float size, int maxJumpAmount) {
        this.colorStart = colorStart;
        this.colorEnd = colorEnd;
        this.maxPointsInTrail = maxPointsInTrail;
        this.reflectionStrength = reflectionStrength;
        this.gravity = gravity;
        this.lifetime = lifetime;
        this.size = size;
        this.maxJumpAmount = maxJumpAmount;
    }

    public ColoredJumpingParticleOptions(FDColor colorStart, FDColor colorEnd, int maxPointsInTrail, float reflectionStrength, float gravity, int lifetime) {
        this(colorStart, colorEnd, maxPointsInTrail, reflectionStrength, gravity, lifetime, 0.1f, 3);
    }

    public ColoredJumpingParticleOptions(FDColor colorStart, FDColor colorEnd, int maxPointsInTrail, float reflectionStrength, float gravity) {
        this(colorStart, colorEnd, maxPointsInTrail, reflectionStrength, gravity, 200, 0.1f, 3);
    }

    public ColoredJumpingParticleOptions(FDColor colorStart, FDColor colorEnd, int maxPointsInTrail, float reflectionStrength) {
        this(colorStart, colorEnd, maxPointsInTrail, reflectionStrength, 1f, 200, 0.1f, 3);
    }

    @Override
    public ParticleType<?> getType() {
        return BossParticles.COLORED_JUMPING_PARTICLE.get();
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private FDColor colorStart;
        private FDColor colorEnd;
        private int maxPointsInTrail = 20;
        private float reflectionStrength = 0.5f;
        private float gravity = 1.0f;
        private int lifetime = 200;
        private float size = 0.1f;
        private int maxJumpAmount = 3;

        public Builder colorStart(FDColor colorStart) {
            this.colorStart = colorStart;
            return this;
        }

        public Builder colorEnd(FDColor colorEnd) {
            this.colorEnd = colorEnd;
            return this;
        }

        public Builder maxPointsInTrail(int value) {
            this.maxPointsInTrail = value;
            return this;
        }

        public Builder reflectionStrength(float value) {
            this.reflectionStrength = value;
            return this;
        }

        public Builder gravity(float value) {
            this.gravity = value;
            return this;
        }

        public Builder lifetime(int value) {
            this.lifetime = value;
            return this;
        }

        public Builder size(float value) {
            this.size = value;
            return this;
        }

        public Builder maxJumpAmount(int value) {
            this.maxJumpAmount = value;
            return this;
        }

        public ColoredJumpingParticleOptions build() {
            return new ColoredJumpingParticleOptions(
                    colorStart, colorEnd, maxPointsInTrail,
                    reflectionStrength, gravity, lifetime, size, maxJumpAmount
            );
        }
    }

}
