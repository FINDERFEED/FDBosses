package com.finderfeed.fdbosses.client.particles.rush_particle;

import com.finderfeed.fdlib.systems.particle.FDParticleRenderType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import org.jetbrains.annotations.Nullable;

public class RushParticle extends Particle {

    private RushParticleOptions rushParticleOptions;

    public RushParticle(RushParticleOptions rushParticleOptions, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z, xd,yd,zd);
        this.rushParticleOptions = rushParticleOptions;
    }

    @Override
    public void render(VertexConsumer vertex, Camera camera, float pticks) {


    }

    @Override
    public ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    public static final FDParticleRenderType RENDER_TYPE = new FDParticleRenderType() {
        @Override
        public void end() {

        }

        @Nullable
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);

            BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            return builder;
        }
    };

    public static class Factory implements ParticleProvider<RushParticleOptions>{
        @Nullable
        @Override
        public Particle createParticle(RushParticleOptions rushParticleOptions, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new RushParticle(rushParticleOptions, level,x,y,z,xd,yd,zd);
        }
    }

}
