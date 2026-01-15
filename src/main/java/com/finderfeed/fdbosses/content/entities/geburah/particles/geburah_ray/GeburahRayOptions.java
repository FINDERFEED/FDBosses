package com.finderfeed.fdbosses.content.entities.geburah.particles.geburah_ray;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdlib.systems.particle.EmptyParticleProcessor;
import com.finderfeed.fdlib.systems.particle.ParticleProcessor;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import com.finderfeed.fdlib.util.FDCodecs;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.client.particles.options.AlphaOptions;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class GeburahRayOptions implements ParticleOptions {

    public static final Deserializer<GeburahRayOptions> DESERIALIZER = new Deserializer<GeburahRayOptions>() {
        @Override
        public GeburahRayOptions fromCommand(ParticleType<GeburahRayOptions> p_123733_, StringReader p_123734_) throws CommandSyntaxException {
            return GeburahRayOptions.builder().build();
        }

        @Override
        public GeburahRayOptions fromNetwork(ParticleType<GeburahRayOptions> p_123735_, FriendlyByteBuf p_123736_) {
            return STREAM_CODEC.fromNetwork(p_123736_);
        }
    };

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

    public static final NetworkCodec<GeburahRayOptions> STREAM_CODEC = NetworkCodec.composite(
            ParticleProcessor.STREAM_CODEC,v->v.particleProcessor,
            NetworkCodec.VEC3, v->v.rayEnd,
            AlphaOptions.STREAM_CODEC,v->v.rayOptions,
            NetworkCodec.COLOR,v->v.color,
            NetworkCodec.FLOAT, v->v.rayWidth,
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

    @Override
    public void writeToNetwork(FriendlyByteBuf p_123732_) {
        STREAM_CODEC.toNetwork(p_123732_, this);
    }

    @Override
    public String writeToString() {
        return "";
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {

        private GeburahRayOptions rayOptions = new GeburahRayOptions();

        public Builder(){}

        public Builder processor(ParticleProcessor<?> processor){
            rayOptions.particleProcessor = processor;
            return this;
        }

        public Builder end(Vec3 rayEnd){
            rayOptions.rayEnd = rayEnd;
            return this;
        }
        public Builder in(int in){
            rayOptions.rayOptions.inTime = in;
            return this;
        }
        public Builder out(int out){
            rayOptions.rayOptions.outTime = out;
            return this;
        }

        public Builder time(int in, int stay, int out){
            rayOptions.rayOptions.inTime = in;
            rayOptions.rayOptions.stayTime = stay;
            rayOptions.rayOptions.outTime = out;
            return this;
        }

        public Builder stay(int stay){
            rayOptions.rayOptions.stayTime = stay;
            return this;
        }
        public Builder color(FDColor color){
            rayOptions.color = color;
            return this;
        }

        public Builder color(float r, float g, float b, float a){
            return this.color(new FDColor(r,g,b,a));
        }

        public Builder color(float r, float g, float b){
            return this.color(new FDColor(r,g,b,1));
        }

        public Builder color(int r, int g, int b, int a){
            return this.color(new FDColor(r/255f,g/255f,b/255f,a/255f));
        }

        public Builder color(int r, int g, int b){
            return this.color(new FDColor(r/255f,g/255f,b/255f,1));
        }


        public Builder width(float lwidth){
            rayOptions.rayWidth = lwidth;
            return this;
        }

        public GeburahRayOptions build(){
            return this.rayOptions;
        }



    }



}