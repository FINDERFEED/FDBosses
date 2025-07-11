package com.finderfeed.fdbosses.client.particles.stripe_particle;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdlib.util.FDByteBufCodecs;
import com.finderfeed.fdlib.util.FDCodecs;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class StripeParticleOptions implements ParticleOptions {

    public static final StreamCodec<? super RegistryFriendlyByteBuf, List<Vec3>> VEC3LIST = new StreamCodec<RegistryFriendlyByteBuf, List<Vec3>>() {
        @Override
        public List<Vec3> decode(RegistryFriendlyByteBuf buf) {
            int amount = buf.readInt();
            List<Vec3> v = new ArrayList<>();
            for (int i = 0; i < amount; i++){
                v.add(FDByteBufCodecs.VEC3.decode(buf));
            }
            return v;
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, List<Vec3> list) {
            buf.writeInt(list.size());
            for (Vec3 v : list){
                FDByteBufCodecs.VEC3.encode(buf, v);
            }
        }
    };

    public static final MapCodec<StripeParticleOptions> CODEC = RecordCodecBuilder.mapCodec(p->p.group(
            FDCodecs.COLOR.fieldOf("color").forGetter(v->v.color),
            Codec.INT.fieldOf("lifetime").forGetter(v->v.lifetime),
            Codec.FLOAT.fieldOf("scale").forGetter(v->v.scale),
            Codec.list(FDCodecs.VEC3).fieldOf("offsets").forGetter(v->v.offsets)
    ).apply(p, StripeParticleOptions::new));

    public static final StreamCodec<? super RegistryFriendlyByteBuf, StripeParticleOptions> STREAM_CODEC = StreamCodec.composite(
            FDByteBufCodecs.COLOR,v->v.color,
            ByteBufCodecs.INT,v->v.lifetime,
            ByteBufCodecs.FLOAT,v->v.scale,
            VEC3LIST,v->v.offsets,
            StripeParticleOptions::new
    );

    private FDColor color;
    private int lifetime;
    private List<Vec3> offsets;
    private float scale;

    public StripeParticleOptions(FDColor color, int lifetime, float scale, Vec3... offsets){
        this.offsets = List.of(offsets);
        this.color = color;
        this.lifetime = lifetime;
        this.scale = scale;
    }

    public StripeParticleOptions(FDColor color, int lifetime,float scale, List<Vec3> offsets){
        this(color,lifetime,scale);
        this.offsets = offsets;
    }

    public static StripeParticleOptions createHorizontalCircling(FDColor color, Vec3 startingHorizontalDirection,float scale, int lifetime, float verticalDistance, float radius, float circlesAmount){
        startingHorizontalDirection = startingHorizontalDirection.multiply(1,0,1).normalize();

        float step = FDMathUtil.FPI / 8f;

        float wholeRotation = circlesAmount * FDMathUtil.FPI * 2;

        List<Vec3> positions = new ArrayList<>();

        for (float i = 0; i < wholeRotation; i+= step){

            float p = 1 - i / wholeRotation;

            float currentRadius = p * radius;

            float currentVerticalOffset = verticalDistance * p;

            Vec3 dir = startingHorizontalDirection.multiply(currentRadius,currentRadius,currentRadius).yRot(i);

            positions.add(dir.add(0.001f,currentVerticalOffset,0.001f));

        }
        return new StripeParticleOptions(color, lifetime, scale, positions);
    }

    public float getScale() {
        return scale;
    }

    public FDColor getColor() {
        return color;
    }

    public int getLifetime() {
        return lifetime;
    }

    public List<Vec3> getOffsets() {
        return offsets;
    }

    @Override
    public ParticleType<?> getType() {
        return BossParticles.STRIPE_PARTICLE.get();
    }

}
