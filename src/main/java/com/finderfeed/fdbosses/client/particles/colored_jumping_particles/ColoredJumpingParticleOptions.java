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

    public static final StreamCodec<RegistryFriendlyByteBuf, ColoredJumpingParticleOptions> STREAM_CODEC = StreamCodec.composite(
                    FDByteBufCodecs.COLOR, v -> v.color,
                    ByteBufCodecs.INT, v -> v.maxPointsInTrail,
                    ByteBufCodecs.FLOAT, v -> v.reflectionStrength,
                    ByteBufCodecs.FLOAT, v -> v.gravity,
                    ByteBufCodecs.INT, v -> v.lifetime,
                    ColoredJumpingParticleOptions::new
            );

    public static final MapCodec<ColoredJumpingParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    FDCodecs.COLOR.fieldOf("color").forGetter(o -> o.color),
                    Codec.INT.fieldOf("max_points_in_trail").forGetter(o -> o.maxPointsInTrail),
                    Codec.FLOAT.fieldOf("reflection_strength").forGetter(o -> o.reflectionStrength),
                    Codec.FLOAT.fieldOf("gravity").forGetter(o -> o.gravity),
                    Codec.INT.fieldOf("lifetime").forGetter(o -> o.lifetime)
            ).apply(instance, ColoredJumpingParticleOptions::new)
    );

    public FDColor color;
    public int maxPointsInTrail;
    public float reflectionStrength;
    public float gravity;
    public int lifetime;

    public ColoredJumpingParticleOptions(FDColor color, int maxPointsInTrail, float reflectionStrength, float gravity, int lifetime) {
        this.color = color;
        this.maxPointsInTrail = maxPointsInTrail;
        this.reflectionStrength = reflectionStrength;
        this.gravity = gravity;
        this.lifetime = lifetime;
    }

    public ColoredJumpingParticleOptions(FDColor color, int maxPointsInTrail, float reflectionStrength, float gravity) {
        this(color, maxPointsInTrail, reflectionStrength, gravity, 200);
    }

    public ColoredJumpingParticleOptions(FDColor color, int maxPointsInTrail, float reflectionStrength) {
        this(color, maxPointsInTrail, reflectionStrength, 1f, 200);
    }


    @Override
    public ParticleType<?> getType() {
        return BossParticles.COLORED_JUMPING_PARTICLE.get();
    }

}
