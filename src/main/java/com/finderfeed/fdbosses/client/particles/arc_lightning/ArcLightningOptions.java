package com.finderfeed.fdbosses.client.particles.arc_lightning;

import com.finderfeed.fdlib.util.FDByteBufCodecs;
import com.finderfeed.fdlib.util.FDCodecs;
import com.finderfeed.fdlib.util.FDColor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Random;
import org.joml.Vector3f;

public class ArcLightningOptions implements ParticleOptions {

    private static final Codec<ArcLightningOptions> CODEC = RecordCodecBuilder.create(p->p.group(
            FDCodecs.VEC3.fieldOf("end").forGetter(v->v.end),
            FDCodecs.VEC3.fieldOf("endSpeed").forGetter(v->v.end),
            Codec.INT.fieldOf("lifetime").forGetter(v->v.lifetime),
            Codec.INT.fieldOf("seed").forGetter(v->v.seed),
            Codec.INT.fieldOf("lightningSegments").forGetter(v->v.lightningSegments),
            Codec.FLOAT.fieldOf("lightningRandomSpread").forGetter(v->v.lightningRandomSpread),
            Codec.FLOAT.fieldOf("lightningWidth").forGetter(v->v.lightningWidth),
            Codec.FLOAT.fieldOf("circleOffset").forGetter(v->v.circleOffset),
            FDCodecs.COLOR.fieldOf("color").forGetter(v->v.color)
            ).apply(p,ArcLightningOptions::new)
    );

    private static final StreamCodec<FriendlyByteBuf,ArcLightningOptions> STREAM_CODEC = FDByteBufCodecs.composite(
            FDByteBufCodecs.VEC3, v->v.end,
            FDByteBufCodecs.VEC3, v->v.endSpeed,
            ByteBufCodecs.INT,v->v.lifetime,
            ByteBufCodecs.INT,v->v.seed,
            ByteBufCodecs.INT,v->v.lightningSegments,
            ByteBufCodecs.FLOAT,v->v.lightningRandomSpread,
            ByteBufCodecs.FLOAT,v->v.lightningWidth,
            ByteBufCodecs.FLOAT,v->v.circleOffset,
            FDByteBufCodecs.COLOR,v->v.color,
            ArcLightningOptions::new
    );

    public static MapCodec<ArcLightningOptions> createCodec(ParticleType<?> type){
        return CODEC.xmap(a->new ArcLightningOptions(type,a.end,a.endSpeed,a.lifetime,a.seed,a.lightningSegments,a.lightningRandomSpread,a.lightningWidth,a.circleOffset,a.color),f->f).fieldOf("options");
    }

    public static StreamCodec<FriendlyByteBuf,ArcLightningOptions> createStreamCodec(ParticleType<?> type){
        return STREAM_CODEC.map(a->new ArcLightningOptions(type,a.end,a.endSpeed,a.lifetime,a.seed,a.lightningSegments,a.lightningRandomSpread,a.lightningWidth,a.circleOffset,a.color),opt->opt);
    }

    public ParticleType<?> particleType;
    public Vec3 end;
    public Vec3 endSpeed;
    public int lifetime;
    public int seed;
    public int lightningSegments;
    public float lightningWidth;
    public float lightningRandomSpread;
    public float circleOffset;
    public FDColor color;

    public ArcLightningOptions(ParticleType<?> particleType, Vec3 end,Vec3 endSpeed, int lifetime,int seed,int segments,float lightningRandomSpread, float lightningWidth,float circleOffset, FDColor color){
        this.particleType = particleType;
        this.endSpeed = endSpeed;
        this.lightningWidth = lightningWidth;
        this.color = color;
        this.seed = seed;
        this.end = end;
        this.lifetime = lifetime;
        this.lightningSegments = segments;
        this.circleOffset = circleOffset;
        this.lightningRandomSpread = lightningRandomSpread;
    }

    public ArcLightningOptions(Vec3 end,Vec3 endSpeed, int lifetime,int seed, int segments,float lightningRandomSpread, float lightningWidth,float circleOffset, FDColor color){
        this(null,end,endSpeed,lifetime,seed,segments,lightningRandomSpread,lightningWidth,circleOffset,color);
    }

    public static Builder builder(ParticleType<?> type){
        return new Builder(type);
    }


    @Override
    public ParticleType<?> getType() {
        return particleType;
    }

    public static class Builder {

        private ArcLightningOptions options = new ArcLightningOptions(Vec3.ZERO,Vec3.ZERO,10,2313,10,0.5f,0.25f,0,new FDColor(0,0,1,1));

        public Builder(ParticleType<?> type){
            options.seed = new Random().nextInt(2543532) + 134234;
            options.particleType = type;
        }

        public Builder seed(int seed){
            options.seed = seed;
            return this;
        }

        public Builder circleOffset(float offset){
            this.options.circleOffset = offset;
            return this;
        }

        public Builder color(int r,int g,int b,int a){
            return this.color(new FDColor(r/255f,g/255f,b/255f,a/255f));
        }

        public Builder color(int r,int g,int b){
            return this.color(new FDColor(r/255f,g/255f,b/255f,1));
        }

        public Builder color(float r,float g,float b){
            return this.color(new FDColor(r,g,b,1));
        }

        public Builder color(float r,float g,float b,float a){
            return this.color(new FDColor(r,g,b,a));
        }

        public Builder color(FDColor color){
            this.options.color = color;
            return this;
        }

        public Builder width(float width){
            this.options.lightningWidth = width;
            return this;
        }

        public Builder lightningSpread(float spread){
            this.options.lightningRandomSpread = spread;
            return this;
        }

        public Builder segments(int segments){
            options.lightningSegments = Mth.clamp(segments,2,Integer.MAX_VALUE);
            return this;
        }

        public Builder lifetime(int lifetime){
            this.options.lifetime = lifetime;
            return this;
        }

        public Builder end(double x,double y,double z){
            return this.end(new Vec3(x,y,z));
        }

        public Builder end(Vec3 end){
            this.options.end = end;
            return this;
        }

        public Builder endSpeed(double x,double y,double z){
            return this.endSpeed(new Vec3(x,y,z));
        }

        public Builder endSpeed(Vec3 end){
            this.options.endSpeed = end;
            return this;
        }

        public ArcLightningOptions build(){
            return options;
        }

    }
}
