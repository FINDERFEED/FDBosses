package com.finderfeed.fdbosses.client.particles.chesed_attack_ray;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdlib.systems.particle.EmptyParticleProcessor;
import com.finderfeed.fdlib.systems.particle.ParticleProcessor;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import com.finderfeed.fdlib.util.client.particles.options.AlphaOptions;
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

public class ChesedRayOptions implements ParticleOptions {

    public static final Deserializer<ChesedRayOptions> DESERIALIZER = new Deserializer<ChesedRayOptions>() {
        @Override
        public ChesedRayOptions fromCommand(ParticleType<ChesedRayOptions> p_123733_, StringReader p_123734_) throws CommandSyntaxException {
            return new ChesedRayOptions();
        }

        @Override
        public ChesedRayOptions fromNetwork(ParticleType<ChesedRayOptions> p_123735_, FriendlyByteBuf p_123736_) {
            return STREAM_CODEC.fromNetwork(p_123736_);
        }
    };

    public static final Codec<ChesedRayOptions> CODEC = RecordCodecBuilder.create(p->p.group(
            ParticleProcessor.CODEC.fieldOf("particleProcessor").forGetter(v->v.particleProcessor),
            FDCodecs.VEC3.fieldOf("rayEnd").forGetter(v->v.rayEnd),
            AlphaOptions.CODEC.fieldOf("alpha").forGetter(v->v.rayOptions),
            FDCodecs.COLOR.fieldOf("color").forGetter(v->v.color),
            FDCodecs.COLOR.fieldOf("lightningColor").forGetter(v->v.color),
            Codec.FLOAT.fieldOf("rayWidth").forGetter(v->v.rayWidth)
    ).apply(p,(particleProcessor,rayEnd,alpha,color,lcolor,width)->{
        ChesedRayOptions ray = new ChesedRayOptions();
        ray.particleProcessor = particleProcessor;
        ray.rayOptions = alpha;
        ray.color = color;
        ray.lightningColor = lcolor;
        ray.rayWidth = width;
        ray.rayEnd = rayEnd;
        return ray;
    }));

    public static final NetworkCodec<ChesedRayOptions> STREAM_CODEC = NetworkCodec.composite(
            ParticleProcessor.STREAM_CODEC,v->v.particleProcessor,
            NetworkCodec.VEC3,v->v.rayEnd,
            AlphaOptions.STREAM_CODEC,v->v.rayOptions,
            NetworkCodec.COLOR,v->v.color,
            NetworkCodec.COLOR,v->v.lightningColor,
            NetworkCodec.FLOAT,v->v.rayWidth,
            (particleProcessor,rayEnd,alpha,color,lcolor,width)->{
                ChesedRayOptions ray = new ChesedRayOptions();
                ray.particleProcessor = particleProcessor;
                ray.rayOptions = alpha;
                ray.color = color;
                ray.lightningColor = lcolor;
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
    public FDColor lightningColor = new FDColor(1f,0f,0f,1f);
    public float rayWidth = 0.25f;


    @Override
    public ParticleType<?> getType() {
        return BossParticles.CHESED_RAY_ATTACK.get();
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

        private ChesedRayOptions rayOptions = new ChesedRayOptions();

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

        public Builder time(int in,int stay,int out){
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

        public Builder color(float r,float g,float b,float a){
            return this.color(new FDColor(r,g,b,a));
        }

        public Builder color(float r,float g,float b){
            return this.color(new FDColor(r,g,b,1));
        }

        public Builder color(int r,int g,int b,int a){
            return this.color(new FDColor(r/255f,g/255f,b/255f,a/255f));
        }

        public Builder color(int r,int g,int b){
            return this.color(new FDColor(r/255f,g/255f,b/255f,1));
        }

        public Builder lightningColor(FDColor color){
            rayOptions.lightningColor = color;
            return this;
        }

        public Builder lightningColor(float r,float g,float b,float a){
            return this.lightningColor(new FDColor(r,g,b,a));
        }

        public Builder lightningColor(float r,float g,float b){
            return this.lightningColor(new FDColor(r,g,b,1));
        }

        public Builder lightningColor(int r,int g,int b,int a){
            return this.lightningColor(new FDColor(r/255f,g/255f,b/255f,a/255f));
        }

        public Builder lightningColor(int r,int g,int b){
            return this.lightningColor(new FDColor(r/255f,g/255f,b/255f,1));
        }

        public Builder width(float lwidth){
            rayOptions.rayWidth = lwidth;
            return this;
        }

        public ChesedRayOptions build(){
            return this.rayOptions;
        }



    }



}
