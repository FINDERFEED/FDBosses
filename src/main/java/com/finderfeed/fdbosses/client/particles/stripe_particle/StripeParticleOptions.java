package com.finderfeed.fdbosses.client.particles.stripe_particle;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdlib.util.FDByteBufCodecs;
import com.finderfeed.fdlib.util.FDCodecs;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
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
            FDCodecs.COLOR.fieldOf("startColor").forGetter(v->v.startColor),
            FDCodecs.COLOR.fieldOf("endColor").forGetter(v->v.endColor),
            Codec.INT.fieldOf("lifetime").forGetter(v->v.lifetime),
            Codec.INT.fieldOf("lod").forGetter(v->v.lod),
            Codec.FLOAT.fieldOf("scale").forGetter(v->v.scale),
            Codec.FLOAT.fieldOf("stripePercentLength").forGetter(v->v.stripePercentLength),
            Codec.list(FDCodecs.VEC3).fieldOf("offsets").forGetter(v->v.offsets)
    ).apply(p, StripeParticleOptions::new));

    public static final StreamCodec<? super RegistryFriendlyByteBuf, StripeParticleOptions> STREAM_CODEC = FDByteBufCodecs.composite(
            FDByteBufCodecs.COLOR,v->v.startColor,
            FDByteBufCodecs.COLOR,v->v.endColor,
            ByteBufCodecs.INT,v->v.lifetime,
            ByteBufCodecs.INT,v->v.lod,
            ByteBufCodecs.FLOAT,v->v.scale,
            ByteBufCodecs.FLOAT,v->v.stripePercentLength,
            VEC3LIST,v->v.offsets,
            StripeParticleOptions::new
    );

    private float stripePercentLength;

    private FDColor startColor;
    private FDColor endColor;
    private int lifetime;
    private List<Vec3> offsets;
    private float scale;
    private int lod;

    public StripeParticleOptions(FDColor startColor,FDColor endColor, int lifetime, int lod,  float scale, float stripePercentLength, Vec3... offsets){
        this.offsets = List.of(offsets);
        this.endColor = endColor;
        this.startColor = startColor;
        this.lifetime = lifetime;
        this.scale = scale;
        this.lod = lod;
        this.stripePercentLength = stripePercentLength;
    }

    public StripeParticleOptions(FDColor startColor, FDColor endColor, int lifetime, int lod, float scale,float stripePercentLength, List<Vec3> offsets){
        this(startColor, endColor,lifetime, lod, scale,stripePercentLength);
        this.offsets = offsets;
    }

    public static StripeParticleOptions createHorizontalCircling(FDColor startColor, FDColor endColor, Vec3 startingDirection,float scale, int lifetime, int lod, float verticalDistance, float radius, float circlesAmount, float stripePercentLength, boolean circleDirection, boolean in){
        startingDirection = startingDirection.normalize();

        Vec3 startingHorizontalDirection = startingDirection.multiply(1,0,1).normalize();

        float step = FDMathUtil.FPI / 8f;

        float wholeRotation = circlesAmount * FDMathUtil.FPI * 2;

        List<Vec3> positions = new ArrayList<>();

        for (float i = 0; i < wholeRotation; i+= step){

            float p;

            if (in){
                p = 1 - i / wholeRotation;
            }else{
                p = i / wholeRotation;
            }

            float currentRadius = p * radius;

            float currentVerticalOffset = verticalDistance * p;

            Vec3 dir = startingHorizontalDirection.multiply(currentRadius,currentRadius,currentRadius).yRot(circleDirection ? i : -i);

            positions.add(dir.add(0.001f,currentVerticalOffset,0.001f));

        }

        Matrix4f transform = new Matrix4f();

        Vec3 left = startingDirection.cross(new Vec3(0,1,0));
        Vec3 transformDirection = left.cross(startingDirection);

        FDRenderUtil.applyMovementMatrixRotations(transform, transformDirection);
        for (int i = 0; i < positions.size(); i++){

            Vec3 oldpos = positions.get(i);

            Vector3f v = transform.transformPosition((float) oldpos.x,(float) oldpos.y,(float) oldpos.z, new Vector3f());

            positions.set(i,new Vec3(v.x,v.y,v.z));

        }

        return new StripeParticleOptions(startColor, endColor, lifetime, lod, scale,stripePercentLength, positions);
    }

    public int getLOD(){
        return lod;
    }

    public float getStripePercentLength() {
        return stripePercentLength;
    }

    public float getScale() {
        return scale;
    }

    public FDColor getStartColor() {
        return startColor;
    }

    public FDColor getEndColor() {
        return endColor;
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
