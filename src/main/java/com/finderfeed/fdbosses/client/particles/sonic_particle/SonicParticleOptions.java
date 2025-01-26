package com.finderfeed.fdbosses.client.particles.sonic_particle;

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
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public class SonicParticleOptions implements ParticleOptions {


    public static final Codec<SonicParticleOptions> CODEC = RecordCodecBuilder.create(p->p.group(
            FDCodecs.COLOR.fieldOf("color").forGetter(v->v.color),
            AlphaOptions.CODEC.fieldOf("alphaOptions").forGetter(v->v.alphaOptions),
            FDCodecs.VEC3.fieldOf("facing").forGetter(v->v.facingDirection),
            Codec.FLOAT.fieldOf("startSize").forGetter(v->v.startSize),
            Codec.FLOAT.fieldOf("endSize").forGetter(v->v.endSize),
            Codec.FLOAT.fieldOf("resizeSpeed").forGetter(v->v.resizeSpeed),
            Codec.FLOAT.fieldOf("resizeAcceleration").forGetter(v->v.resizeAcceleration),
            Codec.INT.fieldOf("lifetime").forGetter(v->v.lifetime)
    ).apply(p,(color,alpha,facing,startSize,endSize,resizeSpeed,resizeAcceleration,lifetime)->{
        SonicParticleOptions o = new SonicParticleOptions();
        o.color = color;
        o.lifetime = lifetime;
        o.alphaOptions = alpha;
        o.facingDirection = facing;
        o.startSize = startSize;
        o.resizeSpeed = resizeSpeed;
        o.resizeAcceleration = resizeAcceleration;
        o.endSize = endSize;
        return o;
    }));


    public static final StreamCodec<FriendlyByteBuf,SonicParticleOptions> STREAM_CODEC = FDByteBufCodecs.composite(
            FDByteBufCodecs.COLOR,v->v.color,
            AlphaOptions.STREAM_CODEC,v->v.alphaOptions,
            FDByteBufCodecs.VEC3,v->v.facingDirection,
            ByteBufCodecs.FLOAT,v->v.startSize,
            ByteBufCodecs.FLOAT,v->v.endSize,
            ByteBufCodecs.FLOAT,v->v.resizeSpeed,
            ByteBufCodecs.FLOAT,v->v.resizeAcceleration,
            ByteBufCodecs.INT,v->v.lifetime,
    (color,alpha,facing,startSize,endSize,resizeSpeed,resizeAcceleration,lifetime)->{
        SonicParticleOptions o = new SonicParticleOptions();
        o.color = color;
        o.lifetime = lifetime;
        o.alphaOptions = alpha;
        o.facingDirection = facing;
        o.startSize = startSize;
        o.resizeSpeed = resizeSpeed;
        o.resizeAcceleration = resizeAcceleration;
        o.endSize = endSize;
        return o;
    });



    public FDColor color = new FDColor(1,1,1,1);
    public AlphaOptions alphaOptions = new AlphaOptions();
    public Vec3 facingDirection = new Vec3(0,1,0);
    public float startSize = 1f;
    public float endSize = 0.0f;
    public float resizeSpeed = -0.1f;
    public float resizeAcceleration = 0;
    public int lifetime = 60;


    public static MapCodec<SonicParticleOptions> createCodec(){
        return CODEC.fieldOf("options");
    }



    @Override
    public ParticleType<?> getType() {
        return BossParticles.SONIC_PARTICLE.get();
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {

        private SonicParticleOptions options = new SonicParticleOptions();

        public Builder lifetime(int lifetime){
            options.lifetime = lifetime;
            return this;
        }

        public Builder endSize(float endSize){
            options.endSize = endSize;
            return this;
        }

        public Builder startSize(float startSize){
            options.startSize = startSize;
            return this;
        }

        public Builder resizeSpeed(float resizeSpeed){
            options.resizeSpeed = resizeSpeed;
            return this;
        }

        public Builder resizeAcceleration(float resizeAcceleration){
            options.resizeAcceleration = resizeAcceleration;
            return this;
        }

        public Builder facing(Vec3 facing){
            options.facingDirection = facing;
            return this;
        }

        public Builder facing(double x,double y,double z){
            return this.facing(new Vec3(x,y,z));
        }

        public Builder alpha(AlphaOptions options){
            this.options.alphaOptions = options;
            return this;
        }

        public Builder color(float r,float g,float b){
            options.color.r = r;
            options.color.g = g;
            options.color.b = b;
            return this;
        }
        public Builder color(int r,int g,int b){
            options.color.r = r/255f;
            options.color.g = g/255f;
            options.color.b = b/255f;
            return this;
        }

        public Builder color(float r,float g,float b,float a){
            options.color.r = r;
            options.color.g = g;
            options.color.b = b;
            options.alphaOptions.maxAlpha = a;
            return this;
        }

        public SonicParticleOptions build(){
            return options;
        }


    }

}
