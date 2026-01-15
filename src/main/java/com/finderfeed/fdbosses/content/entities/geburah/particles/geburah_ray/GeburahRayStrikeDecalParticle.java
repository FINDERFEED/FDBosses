package com.finderfeed.fdbosses.content.entities.geburah.particles.geburah_ray;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.particles.DecalParticle;
import com.finderfeed.fdbosses.client.particles.DecalParticleOptions;
import com.finderfeed.fdlib.systems.particle.FDParticleRenderType;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class GeburahRayStrikeDecalParticle extends DecalParticle {

    public static final ResourceLocation LOCATION = FDBosses.location("textures/particle/geburah/ray_strike_decal.png");

    public GeburahRayStrikeDecalParticle(DecalParticleOptions decalParticleOptions, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(decalParticleOptions, level, x, y, z, xd, yd, zd);
        this.roll = level.random.nextInt(4) * 90;
        this.oRoll = roll;
    }

    @Override
    protected int getLightColor(float p_107249_) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }


    public static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder tesselator, TextureManager p_107437_) {

            if (Minecraft.useShaderTransparency()){
                Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
            }

            RenderSystem.enableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.defaultBlendFunc();

            RenderSystem.setShader(GameRenderer::getParticleShader);
            FDRenderUtil.bindTexture(LOCATION);
            tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator p_107438_) {
            if (Minecraft.useShaderTransparency()) {
                Minecraft.getInstance().levelRenderer.getParticlesTarget().copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
                Minecraft.getInstance().levelRenderer.getParticlesTarget().bindWrite(false);
            }
            RenderSystem.disableBlend();
        }

    };

    public static class Factory implements ParticleProvider<DecalParticleOptions>{

        @Nullable
        @Override
        public Particle createParticle(DecalParticleOptions decalParticleOptions, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new GeburahRayStrikeDecalParticle(decalParticleOptions, level, x, y, z, xd, yd, zd);
        }
    }

}
