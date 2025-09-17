package com.finderfeed.fdbosses.client.particles.square_preparation_particle;


import com.finderfeed.fdlib.systems.particle.FDParticleRenderType;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.lwjgl.opengl.GL11;

public class RectanglePreparationParticle extends Particle {

    private RectanglePreparationParticleOptions options;

    public RectanglePreparationParticle(RectanglePreparationParticleOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z, xd, yd, zd);
        this.options = options;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.lifetime = options.getAttackChargeTime() + options.getFadeOut();
    }

    @Override
    public void render(VertexConsumer vertex, Camera camera, float pticks) {

        Vec3 thisPos = FDMathUtil.interpolateVectors(new Vec3(xo,yo,zo),new Vec3(x,y,z),pticks);
        Vec3 camPos = camera.getPosition();
        Vec3 pos = thisPos.subtract(camPos);

        Vec3 hdir = options.getHorizontalDirection().multiply(1,0,1).normalize();

        float distanceModifier = FDEasings.easeOut(FDMathUtil.clamp ((this.age + pticks) / (this.options.getFadeIn()), 0, 1));
        float attackPreparationModifier = FDMathUtil.clamp ((this.age + pticks) / (this.options.getAttackChargeTime()), 0, 1);

        float width = this.options.getWidth();
        float length = this.options.getLength();

        Vec3 pos1 = pos.add(hdir.yRot(FDMathUtil.FPI / 2).multiply(width,width,width));
        Vec3 pos2 = pos.add(hdir.yRot(-FDMathUtil.FPI / 2).multiply(width,width,width));

        Vec3 pos3 = pos2.add(hdir.multiply(length * distanceModifier,length * distanceModifier,length * distanceModifier));
        Vec3 pos4 = pos1.add(hdir.multiply(length * distanceModifier,length * distanceModifier,length * distanceModifier));

        Vec3 pos1p = pos.add(hdir.yRot(FDMathUtil.FPI / 2).multiply(width,width,width));
        Vec3 pos2p = pos.add(hdir.yRot(-FDMathUtil.FPI / 2).multiply(width,width,width));

        Vec3 pos3p = pos2.add(hdir.multiply(length * attackPreparationModifier,length * attackPreparationModifier,length * attackPreparationModifier));
        Vec3 pos4p = pos1.add(hdir.multiply(length * attackPreparationModifier,length * attackPreparationModifier,length * attackPreparationModifier));

        FDColor color = options.getColor();

        float fadeOutP = 1;
        if (this.age - this.options.getAttackChargeTime() >= 0){
            fadeOutP = 1 - FDMathUtil.clamp((this.age + pticks - this.options.getAttackChargeTime()) / (float) this.options.getFadeOut(),0, 1);
        }

        float colmod = 0.75f;

        float endcolr = FDMathUtil.clamp(color.r * colmod * 2f, 0, 1);
        float endcolg = FDMathUtil.clamp(color.g * colmod * 2f, 0, 1);
        float endcolb = FDMathUtil.clamp(color.b * colmod * 2f, 0, 1);

        vertex.vertex((float)pos1.x,(float)pos1.y,(float)pos1.z).color(color.r * colmod,color.g * colmod,color.b * colmod,color.a * fadeOutP).endVertex();
        vertex.vertex((float)pos2.x,(float)pos2.y,(float)pos2.z).color(color.r * colmod,color.g * colmod,color.b * colmod,color.a * fadeOutP).endVertex();
        vertex.vertex((float)pos3.x,(float)pos3.y,(float)pos3.z).color(endcolr,endcolg,endcolb,color.a * fadeOutP).endVertex();
        vertex.vertex((float)pos4.x,(float)pos4.y,(float)pos4.z).color(endcolr,endcolg,endcolb,color.a * fadeOutP).endVertex();
//

        float endcolr2 = FDMathUtil.clamp(color.r * 2f, 0, 1);
        float endcolg2 = FDMathUtil.clamp(color.g * 2f, 0, 1);
        float endcolb2 = FDMathUtil.clamp(color.b * 2f, 0, 1);
        vertex.vertex((float)pos1p.x,(float)pos1p.y + 0.005f,(float)pos1p.z).color(color.r,color.g,color.b,color.a * fadeOutP).endVertex();
        vertex.vertex((float)pos2p.x,(float)pos2p.y + 0.005f,(float)pos2p.z).color(color.r,color.g,color.b,color.a * fadeOutP).endVertex();
        vertex.vertex((float)pos3p.x,(float)pos3p.y + 0.005f,(float)pos3p.z).color(endcolr2,endcolg2,endcolb2,color.a * fadeOutP).endVertex();
        vertex.vertex((float)pos4p.x,(float)pos4p.y + 0.005f,(float)pos4p.z).color(endcolr2,endcolg2,endcolb2,color.a * fadeOutP).endVertex();

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
        public void end(Tesselator tesselator) {
            tesselator.end();
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableCull();
        }

        @Nullable
        @Override
        public void begin(BufferBuilder tesselator, TextureManager textureManager) {

            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.disableCull();

            tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        }
    };

    @Override
    public boolean shouldCull() {
        return false;
    }

    public static class Factory implements ParticleProvider<RectanglePreparationParticleOptions> {
        @Nullable
        @Override
        public Particle createParticle(RectanglePreparationParticleOptions options, ClientLevel level, double px, double py, double pz, double xd, double yd, double zd) {
            return new RectanglePreparationParticle(options,level,px,py,pz,xd,yd,zd);
        }
    }

}