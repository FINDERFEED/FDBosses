package com.finderfeed.fdbosses.client.particles.smoke_particle;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdlib.util.client.particles.options.AlphaOptions;
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

public class BigSmokeParticleOptions implements ParticleOptions {

    public static final Codec<BigSmokeParticleOptions> CODEC = RecordCodecBuilder.create(p->p.group(
            AlphaOptions.CODEC.fieldOf("inOutOptions").forGetter(v->v.intOut),
            FDCodecs.COLOR.fieldOf("color").forGetter(v->v.color),
            Codec.FLOAT.fieldOf("minSpeed").forGetter(v->v.minSpeed),
            Codec.FLOAT.fieldOf("size").forGetter(v->v.size),
            Codec.FLOAT.fieldOf("friction").forGetter(v->v.friction)
    ).apply(p,(alpha,color,minSpeed,size,friction)->{
        BigSmokeParticleOptions d = new BigSmokeParticleOptions();
        d.minSpeed = minSpeed;
        d.intOut = alpha;
        d.color = color;
        d.size = size;
        d.friction = friction;
        return d;
    }));

    public static final MapCodec<BigSmokeParticleOptions> MAP_CODEC = CODEC.fieldOf("options");

    public static final StreamCodec<FriendlyByteBuf,BigSmokeParticleOptions> STREAM_CODEC = StreamCodec.composite(
            AlphaOptions.STREAM_CODEC,v->v.intOut,
            FDByteBufCodecs.COLOR, v->v.color,
            ByteBufCodecs.FLOAT,v->v.minSpeed,
            ByteBufCodecs.FLOAT,v->v.size,
            ByteBufCodecs.FLOAT,v->v.friction,
            (alpha,color,minSpeed,size,friction)->{
                BigSmokeParticleOptions d = new BigSmokeParticleOptions();
                d.intOut = alpha;
                d.color = color;
                d.size = size;
                d.minSpeed = minSpeed;
                d.friction = friction;
                return d;
            }
    );

    public AlphaOptions intOut = new AlphaOptions();
    public FDColor color = new FDColor(1f,1f,1f,1f);
    public float minSpeed = 0;
    public float size = 1;
    public float friction = 1;

    public static Builder builder(){
        return new Builder();
    }

    @Override
    public ParticleType<?> getType() {
        return BossParticles.BIS_SMOKE.get();
    }

    public static class Builder{

        private BigSmokeParticleOptions options = new BigSmokeParticleOptions();

        public Builder lifetime(int in,int stay,int out){
            this.options.intOut.inTime = in;
            this.options.intOut.stayTime = stay;
            this.options.intOut.outTime = out;
            return this;
        }

        public Builder size(float size){
            this.options.size = size;
            return this;
        }

        public Builder minSpeed(float minSpeed){
            this.options.minSpeed = minSpeed;
            return this;
        }


        public Builder friction(float friction){
            this.options.friction = friction;
            return this;
        }

        public Builder color(int r, int g, int b, int a){
            return this.color(new FDColor(r/255f,g/255f,b/255f,a/255f));
        }

        public Builder color(int r, int g, int b){
            return this.color(new FDColor(r/255f,g/255f,b/255f,1));
        }

        public Builder color(float r, float g, float b){
            return this.color(new FDColor(r,g,b,1));
        }

        public Builder color(float r, float g, float b, float a){
            return this.color(new FDColor(r,g,b,a));
        }

        public Builder color(FDColor color){
            this.options.color = color;
            return this;
        }

        public BigSmokeParticleOptions build(){
            return options;
        }

    }

}
