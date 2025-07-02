package com.finderfeed.fdbosses.client.particles.arc_preparation_particle;

import com.finderfeed.fdbosses.client.particles.FlameWithStoneParticle;
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
import org.lwjgl.opengl.GL11;

public class ArcAttackPreparationParticle extends Particle {

    private ArcAttackPreparationParticleOptions options;

    public ArcAttackPreparationParticle(ArcAttackPreparationParticleOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
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

        Vec3 hdir = options.getHorizontalDirection().multiply(1,0,1).normalize().multiply(options.getLength(),options.getLength(),options.getLength());


        float distanceModifier = FDEasings.easeOut(Math.clamp ((this.age + pticks) / (this.options.getFadeIn()), 0, 1));
        float attackPreparationModifier = Math.clamp ((this.age + pticks) / (this.options.getAttackChargeTime()), 0, 1);


        Vec3 toPos2 = hdir.yRot(options.getHalfAttackAngle());
        Vec3 toPos3 = hdir.yRot(-options.getHalfAttackAngle());
        Vec3 toPos2p = hdir.yRot(options.getHalfAttackAngle());
        Vec3 toPos3p = hdir.yRot(-options.getHalfAttackAngle());



        Vec3 pos2 = pos.add(toPos2.multiply(distanceModifier,distanceModifier,distanceModifier));
        Vec3 pos3 = pos.add(toPos3.multiply(distanceModifier,distanceModifier,distanceModifier));

        Vec3 pos2p = pos.add(toPos2p.multiply(attackPreparationModifier,attackPreparationModifier,attackPreparationModifier));
        Vec3 pos3p = pos.add(toPos3p.multiply(attackPreparationModifier,attackPreparationModifier,attackPreparationModifier));

        Vec3 center = pos.add(hdir.multiply(distanceModifier,distanceModifier,distanceModifier));
        Vec3 centerp = pos.add(hdir.multiply(attackPreparationModifier,attackPreparationModifier,attackPreparationModifier));

        FDColor color = options.getColor();

        float fadeOutP = 1;
        if (this.age - this.options.getAttackChargeTime() >= 0){
            fadeOutP = 1 - Math.clamp((this.age + pticks - this.options.getAttackChargeTime()) / (float) this.options.getFadeOut(),0, 1);
        }

        float colmod = 0.75f;
        vertex.addVertex((float) pos.x,(float) pos.y,(float) pos.z).setColor(color.r * colmod,color.g * colmod,color.b * colmod,color.a * fadeOutP);
        vertex.addVertex((float) pos2.x,(float) pos2.y,(float) pos2.z).setColor(color.r * colmod,color.g * colmod,color.b * colmod,color.a * fadeOutP);
        vertex.addVertex((float) center.x,(float) center.y,(float) center.z).setColor(color.r * colmod,color.g * colmod,color.b * colmod,color.a * fadeOutP);

        vertex.addVertex((float) pos.x,(float) pos.y,(float) pos.z).setColor(color.r * colmod,color.g * colmod,color.b * colmod,color.a * fadeOutP);
        vertex.addVertex((float) pos3.x,(float) pos3.y,(float) pos3.z).setColor(color.r * colmod,color.g * colmod,color.b * colmod,color.a * fadeOutP);
        vertex.addVertex((float) center.x,(float) center.y,(float) center.z).setColor(color.r * colmod,color.g * colmod,color.b * colmod,color.a * fadeOutP);

        vertex.addVertex((float) pos.x,(float) pos.y + 0.005f,(float) pos.z).setColor(color.r,color.g,color.b,color.a * fadeOutP);
        vertex.addVertex((float) pos2p.x,(float) pos.y + 0.005f,(float) pos2p.z).setColor(color.r,color.g,color.b,color.a * fadeOutP);
        vertex.addVertex((float) centerp.x,(float) pos.y + 0.005f,(float) centerp.z).setColor(color.r,color.g,color.b,color.a * fadeOutP);

        vertex.addVertex((float) pos.x,(float) pos.y + 0.005f,(float) pos.z).setColor(color.r,color.g,color.b,color.a * fadeOutP);
        vertex.addVertex((float) pos3p.x,(float) pos.y + 0.005f,(float) pos3p.z).setColor(color.r,color.g,color.b,color.a * fadeOutP);
        vertex.addVertex((float) centerp.x,(float) pos.y + 0.005f,(float) centerp.z).setColor(color.r,color.g,color.b,color.a * fadeOutP);

    }


    @Override
    protected int getLightColor(float p_107249_) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    public static final ParticleRenderType RENDER_TYPE = new FDParticleRenderType() {
        @Override
        public void end() {
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableCull();
        }

        @Nullable
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {

            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.disableCull();

            return tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        }
    };

    @Override
    public AABB getRenderBoundingBox(float partialTicks) {
        return new AABB(
                -this.options.getLength(),
                -this.options.getLength(),
                -this.options.getLength(),
                this.options.getLength(),
                this.options.getLength(),
                this.options.getLength()
        ).move(x,y,z);
    }

    public static class Factory implements ParticleProvider<ArcAttackPreparationParticleOptions>{
        @Nullable
        @Override
        public Particle createParticle(ArcAttackPreparationParticleOptions options, ClientLevel level, double px, double py, double pz, double xd, double yd, double zd) {
            return new ArcAttackPreparationParticle(options,level,px,py,pz,xd,yd,zd);
        }
    }

}
