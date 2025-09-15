package com.finderfeed.fdbosses.client.particles.stripe_particle;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import com.finderfeed.fdlib.util.FDCodecs;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class StripeParticleOptions implements ParticleOptions {

    public static final Deserializer<StripeParticleOptions> DESERIALIZER = new Deserializer<StripeParticleOptions>() {
        @Override
        public StripeParticleOptions fromCommand(ParticleType<StripeParticleOptions> p_123733_, StringReader p_123734_) throws CommandSyntaxException {
            return new StripeParticleOptions();
        }

        @Override
        public StripeParticleOptions fromNetwork(ParticleType<StripeParticleOptions> p_123735_, FriendlyByteBuf p_123736_) {
            return STREAM_CODEC.fromNetwork(p_123736_);
        }
    };


    public static final Codec<StripeParticleOptions> CODEC = RecordCodecBuilder.create(p->p.group(
            FDCodecs.COLOR.fieldOf("startColor").forGetter(v->v.startColor),
            FDCodecs.COLOR.fieldOf("endColor").forGetter(v->v.endColor),
            Codec.INT.fieldOf("lifetime").forGetter(v->v.lifetime),
            Codec.INT.fieldOf("shapeVerties").forGetter(v->v.shapeVertices),
            Codec.INT.fieldOf("lod").forGetter(v->v.lod),
            Codec.FLOAT.fieldOf("scale").forGetter(v->v.scale),
            Codec.FLOAT.fieldOf("stripePercentLength").forGetter(v->v.stripePercentLength),
            Codec.FLOAT.fieldOf("startInPercent").forGetter(v->v.startInPercent),
            Codec.FLOAT.fieldOf("endOutPercent").forGetter(v->v.endOutPercent),
            Codec.list(FDCodecs.VEC3).fieldOf("offsets").forGetter(v->v.offsets)
    ).apply(p, (startColor,endColor,lifetime, shapeVertices,lod,scale,stripePercentLength,startInPercent,endOutPercent,offsets)->{
        return StripeParticleOptions.builder()
                .startColor(startColor)
                .endColor(endColor)
                .shapeVertices(shapeVertices)
                .lifetime(lifetime)
                .scale(scale)
                .stripePercentLength(stripePercentLength)
                .startInPercent(startInPercent)
                .endOutPercent(endOutPercent)
                .offsets(offsets)
                .build();
    }));

    public static final NetworkCodec<StripeParticleOptions> STREAM_CODEC = NetworkCodec.composite(
            NetworkCodec.COLOR,v->v.startColor,
            NetworkCodec.COLOR,v->v.endColor,
            NetworkCodec.INT,v->v.lifetime,
            NetworkCodec.INT,v->v.shapeVertices,
            NetworkCodec.INT,v->v.lod,
            NetworkCodec.FLOAT,v->v.scale,
            NetworkCodec.FLOAT,v->v.stripePercentLength,
            NetworkCodec.FLOAT,v->v.startInPercent,
            NetworkCodec.FLOAT,v->v.endOutPercent,
            NetworkCodec.listOf(NetworkCodec.VEC3),v->v.offsets,
            (startColor,endColor,lifetime, shapeVertices,lod,scale,stripePercentLength,startInPercent,endOutPercent,offsets)->{
                return StripeParticleOptions.builder()
                        .startColor(startColor)
                        .endColor(endColor)
                        .lifetime(lifetime)
                        .scale(scale)
                        .stripePercentLength(stripePercentLength)
                        .startInPercent(startInPercent)
                        .endOutPercent(endOutPercent)
                        .shapeVertices(shapeVertices)
                        .offsets(offsets)
                        .build();
            }
    );

    private float stripePercentLength;

    private int shapeVertices = 3;
    private FDColor startColor = new FDColor(1,1,1,1);
    private FDColor endColor = new FDColor(1,1,1,1);
    private int lifetime = 20;
    private List<Vec3> offsets = new ArrayList<>();
    private float scale = 1;
    private int lod = 25;
    private float startInPercent = 0.5f;
    private float endOutPercent = 0.5f;

    private StripeParticleOptions(){};

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

    public static StripeParticleOptions createHorizontalCircling(FDColor startColor, FDColor endColor, Vec3 direction,float startingAngle, float scale, int lifetime, int lod, float verticalDistance, float radius, float circlesAmount, float stripePercentLength, boolean circleDirection, boolean in){
        direction = direction.normalize();

        Vec3 startingHorizontalDirection = new Vec3(1,0,0).yRot(startingAngle);

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

        FDRenderUtil.applyMovementMatrixRotations(transform, direction);
        for (int i = 0; i < positions.size(); i++){

            Vec3 oldpos = positions.get(i);

            Vector3f v = transform.transformPosition((float) oldpos.x,(float) oldpos.y,(float) oldpos.z, new Vector3f());

            positions.set(i,new Vec3(v.x,v.y,v.z));

        }

        return new StripeParticleOptions(startColor, endColor, lifetime, lod, scale,stripePercentLength, positions);
    }

    public float getEndOutPercent() {
        return endOutPercent;
    }

    public float getStartInPercent() {
        return startInPercent;
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

    public int getShapeVertices() {
        return shapeVertices;
    }

    @Override
    public ParticleType<?> getType() {
        return BossParticles.STRIPE_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf p_123732_) {
        STREAM_CODEC.toNetwork(p_123732_,this);
    }

    @Override
    public String writeToString() {
        return "zhopa";
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {

        private StripeParticleOptions stripeParticleOptions;


        public Builder shapeVertices(int shapeVertices){
            this.stripeParticleOptions.shapeVertices = shapeVertices;
            return this;
        }

        public Builder(){
            this.stripeParticleOptions = new StripeParticleOptions();
        }

        public Builder stripePercentLength(float stripePercentLength){
            this.stripeParticleOptions.stripePercentLength = stripePercentLength;
            return this;
        }

        public Builder startColor(FDColor color){
            this.stripeParticleOptions.startColor = color;
            return this;
        }

        public Builder endColor(FDColor color){
            this.stripeParticleOptions.endColor = color;
            return this;
        }

        public Builder lifetime(int lifetime){
            this.stripeParticleOptions.lifetime = lifetime;
            return this;
        }

        public Builder offsets(List<Vec3> offsets){
            this.stripeParticleOptions.offsets = offsets;
            return this;
        }

        public Builder offsets(Vec3... offsets){
            this.stripeParticleOptions.offsets = List.of(offsets);
            return this;
        }

        public Builder scale(float scale){
            this.stripeParticleOptions.scale = scale;
            return this;
        }

        public Builder lod(int lod){
            this.stripeParticleOptions.lod = lod;
            return this;
        }

        public Builder startInPercent(float startPercent){
            this.stripeParticleOptions.startInPercent = startPercent;
            return this;
        }

        public Builder endOutPercent(float endPercent){
            this.stripeParticleOptions.endOutPercent = endPercent;
            return this;
        }

        public StripeParticleOptions build(){
            return stripeParticleOptions;
        }

    }

}
