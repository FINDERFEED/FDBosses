package com.finderfeed.fdbosses.content.entities.geburah.particles.geburah_ray;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.content.entities.geburah.particles.geburah_ray.GeburahRayOptions;
import com.finderfeed.fdlib.systems.particle.EmptyParticleProcessor;
import com.finderfeed.fdlib.systems.particle.ParticleProcessor;
import com.finderfeed.fdlib.util.FDByteBufCodecs;
import com.finderfeed.fdlib.util.FDCodecs;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.client.particles.options.AlphaOptions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public class GeburahRayOptions implements ParticleOptions {

    public static final Codec<GeburahRayOptions> CODEC = RecordCodecBuilder.create(p->p.group(
            ParticleProcessor.CODEC.fieldOf("particleProcessor").forGetter(v->v.particleProcessor),
            FDCodecs.VEC3.fieldOf("rayEnd").forGetter(v->v.rayEnd),
            AlphaOptions.CODEC.fieldOf("alpha").forGetter(v->v.rayOptions),
            FDCodecs.COLOR.fieldOf("color").forGetter(v->v.color),
            Codec.FLOAT.fieldOf("rayWidth").forGetter(v->v.rayWidth)
    ).apply(p,(particleProcessor,rayEnd,alpha,color,width)->{
        GeburahRayOptions ray = new GeburahRayOptions();
        ray.particleProcessor = particleProcessor;
        ray.rayOptions = alpha;
        ray.color = color;
        ray.rayWidth = width;
        ray.rayEnd = rayEnd;
        return ray;
    }));

    public static MapCodec<GeburahRayOptions> mapCodec(){
        return CODEC.xmap(x->x,x->x).fieldOf("options");
    }

    public static final StreamCodec<FriendlyByteBuf,GeburahRayOptions> STREAM_CODEC = StreamCodec.composite(
            ParticleProcessor.STREAM_CODEC,v->v.particleProcessor,
            FDByteBufCodecs.VEC3, v->v.rayEnd,
            AlphaOptions.STREAM_CODEC,v->v.rayOptions,
            FDByteBufCodecs.COLOR,v->v.color,
            ByteBufCodecs.FLOAT, v->v.rayWidth,
            (particleProcessor,rayEnd,alpha,color,width)->{
                GeburahRayOptions ray = new GeburahRayOptions();
                ray.particleProcessor = particleProcessor;
                ray.rayOptions = alpha;
                ray.color = color;
                ray.rayWidth = width;
                ray.rayEnd = rayEnd;
                return ray;
            }
    );

    public ParticleProcessor<?> particleProcessor = new EmptyParticleProcessor();
    public Vec3 rayEnd = Vec3.ZERO;
    public AlphaOptions rayOptions = AlphaOptions.builder()
            .in(3)
            .stay(10)
            .out(3)
            .build();
    public FDColor color = new FDColor(1f,0f,0f,1f);
    public float rayWidth = 0.25f;


    @Override
    public ParticleType<?> getType() {
        return BossParticles.GEBURAH_RAY_ATTACK.get();
    }

    public static GeburahRayOptions.Builder builder(){
        return new GeburahRayOptions.Builder();
    }

    public static class Builder {

        private GeburahRayOptions rayOptions = new GeburahRayOptions();

        public Builder(){}

        public GeburahRayOptions.Builder processor(ParticleProcessor<?> processor){
            rayOptions.particleProcessor = processor;
            return this;
        }

        public GeburahRayOptions.Builder end(Vec3 rayEnd){
            rayOptions.rayEnd = rayEnd;
            return this;
        }
        public GeburahRayOptions.Builder in(int in){
            rayOptions.rayOptions.inTime = in;
            return this;
        }
        public GeburahRayOptions.Builder out(int out){
            rayOptions.rayOptions.outTime = out;
            return this;
        }

        public GeburahRayOptions.Builder time(int in, int stay, int out){
            rayOptions.rayOptions.inTime = in;
            rayOptions.rayOptions.stayTime = stay;
            rayOptions.rayOptions.outTime = out;
            return this;
        }

        public GeburahRayOptions.Builder stay(int stay){
            rayOptions.rayOptions.stayTime = stay;
            return this;
        }
        public GeburahRayOptions.Builder color(FDColor color){
            rayOptions.color = color;
            return this;
        }

        public GeburahRayOptions.Builder color(float r, float g, float b, float a){
            return this.color(new FDColor(r,g,b,a));
        }

        public GeburahRayOptions.Builder color(float r, float g, float b){
            return this.color(new FDColor(r,g,b,1));
        }

        public GeburahRayOptions.Builder color(int r, int g, int b, int a){
            return this.color(new FDColor(r/255f,g/255f,b/255f,a/255f));
        }

        public GeburahRayOptions.Builder color(int r, int g, int b){
            return this.color(new FDColor(r/255f,g/255f,b/255f,1));
        }


        public GeburahRayOptions.Builder width(float lwidth){
            rayOptions.rayWidth = lwidth;
            return this;
        }

        public GeburahRayOptions build(){
            return this.rayOptions;
        }



    }



}