package com.finderfeed.fdbosses.client.particles.malkuth_slash;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash.MalkuthSlashProjectile;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash.MalkuthSlashRenderer;
import com.finderfeed.fdlib.init.FDCoreShaders;
import com.finderfeed.fdlib.systems.particle.FDParticleRenderType;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.renderers.QuadRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MalkuthHorizontalSlashParticle extends Particle {

    private MalkuthHorizontalSlashOptions options;

    public MalkuthHorizontalSlashParticle(MalkuthHorizontalSlashOptions options, ClientLevel level, double x, double y, double z,double xd,double yd,double zd) {
        super(level, x, y, z,xd,yd,zd);
        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.options = options;
        this.lifetime = options.getLifetime();
    }

    @Override
    public void render(VertexConsumer vertex, Camera camera, float pticks) {

        float p = Mth.clamp((this.age + pticks) / (this.lifetime - 1),0,1);

        Vec3 thisPos = new Vec3(
                FDMathUtil.lerp(this.xo,this.x,pticks),
                FDMathUtil.lerp(this.yo,this.y,pticks),
                FDMathUtil.lerp(this.zo,this.z,pticks)
        );

        Vec3 camPos = camera.getPosition();

        Vec3 actualPos = thisPos.subtract(camPos);

        QuadRenderer.start(vertex)
                .translate((float)actualPos.x,(float)actualPos.y,(float)actualPos.z)
                .sizeY(options.getSlashWidth() * MalkuthSlashProjectile.HEIGHT_SIZE_MODIFIER)
                .sizeX(options.getSlashWidth())
                .direction(options.getSlashDirection().reverse())
                .verticalRendering()
                .renderBack()
                .setTexColor()
                .rotationDegrees(options.getRotation())
                .color(1f,1f,1f,(0.8f - p * 0.8f))
                .render();


    }

    @Override
    public ParticleRenderType getRenderType() {
        switch (options.getAttackType()){
            case ICE -> {
                return ICE_RENDER_TYPE;
            }
            case FIRE -> {
                return FIRE_RENDER_TYPE;
            }
            default -> {
                return FIRE_RENDER_TYPE;
            }
        }
    }

    public static final ParticleRenderType ICE_RENDER_TYPE = new ParticleRenderType(){
        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
        }

        @Nullable
        @Override
        public void begin(BufferBuilder tesselator, TextureManager textureManager) {
            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, MalkuthSlashRenderer.ICE_LOCATION);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getParticleShader);
            tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }
    };

    public static final ParticleRenderType FIRE_RENDER_TYPE = new ParticleRenderType(){
        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
        }

        @Nullable
        @Override
        public void begin(BufferBuilder tesselator, TextureManager textureManager) {
            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, MalkuthSlashRenderer.FIRE_LOCATION);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getParticleShader);
            tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }
    };

    @Override
    protected int getLightColor(float p_107249_) {
        return LightTexture.FULL_BRIGHT;
    }

    public static class Factory implements ParticleProvider<MalkuthHorizontalSlashOptions>{
        @Nullable
        @Override
        public Particle createParticle(MalkuthHorizontalSlashOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new MalkuthHorizontalSlashParticle(options, level,x,y,z,xd,yd,zd);
        }
    }

}
