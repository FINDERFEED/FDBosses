package com.finderfeed.fdbosses.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.ParticleType;
import org.jetbrains.annotations.Nullable;

public class FlameWithStoneParticle extends GravityOptionsParticle{

    public FlameWithStoneParticle(GravityParticleOptions gravityParticleOptions, ClientLevel leve, double x, double y, double z, double xd, double yd, double zd) {
        super(gravityParticleOptions, leve, x, y, z, xd, yd, zd);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    protected int getLightColor(float p_107249_) {
        return LightTexture.FULL_BRIGHT;
    }

    public static class Factory implements ParticleProvider<GravityParticleOptions> {

        private SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet){
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(GravityParticleOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            FlameWithStoneParticle flameWithStoneParticle = new FlameWithStoneParticle(options,level,x,y,z,xd,yd,zd);
            flameWithStoneParticle.pickSprite(spriteSet);
            return flameWithStoneParticle;
        }

    }

}
