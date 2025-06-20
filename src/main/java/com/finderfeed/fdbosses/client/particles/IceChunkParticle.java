package com.finderfeed.fdbosses.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import org.jetbrains.annotations.Nullable;

public class IceChunkParticle extends GravityOptionsParticle{

    public IceChunkParticle(GravityParticleOptions gravityParticleOptions, ClientLevel leve, double x, double y, double z, double xd, double yd, double zd) {
        super(gravityParticleOptions, leve, x, y, z, xd, yd, zd);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }


    public static class Factory implements ParticleProvider<GravityParticleOptions>{

        private SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet){
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(GravityParticleOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            IceChunkParticle iceChunkParticle = new IceChunkParticle(options,level,x,y,z,xd,yd,zd);
            iceChunkParticle.pickSprite(spriteSet);
            return iceChunkParticle;
        }
    }

}
