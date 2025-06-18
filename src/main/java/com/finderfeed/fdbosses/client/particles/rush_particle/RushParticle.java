package com.finderfeed.fdbosses.client.particles.rush_particle;

import com.finderfeed.fdlib.systems.particle.FDParticleRenderType;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
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
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class RushParticle extends Particle {

    private RushParticleOptions rushParticleOptions;

    public RushParticle(RushParticleOptions rushParticleOptions, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z, xd,yd,zd);
        this.rushParticleOptions = rushParticleOptions;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.x = x;
        this.y = y;
        this.z = z;
        this.lifetime = rushParticleOptions.getLifetime();

    }

    @Override
    public void render(VertexConsumer vertex, Camera camera, float pticks) {
        Vec3 pos = new Vec3(
                Mth.lerp(pticks,this.xo,this.x),
                Mth.lerp(pticks,this.yo,this.y),
                Mth.lerp(pticks,this.zo,this.z)
        ).subtract(camera.getPosition());

        float p = (this.age + pticks) / lifetime;

        Matrix4f mat = new Matrix4f();
        mat.translate((float)pos.x,(float)pos.y,(float)pos.z);
        FDRenderUtil.applyMovementMatrixRotations(mat,rushParticleOptions.getRushDirection());

        Vec3 n = FDMathUtil.getNormalVectorFromLineToPoint(pos,rushParticleOptions.getRushDirection().add(pos),Vec3.ZERO);

        Matrix4f mt2 = new Matrix4f();
        FDRenderUtil.applyMovementMatrixRotations(mt2,rushParticleOptions.getRushDirection());
        Vector4f up = new Vector4f(0,0,1,1); mt2.transform(up);
        Vector4f left = new Vector4f(1,0,0,1); mt2.transform(left);
        Vec3 vup = new Vec3(up.x / up.w,up.y / up.w,up.z / up.w);
        Vec3 vleft = new Vec3(left.x,left.y,left.z);
        float angle = (float) FDMathUtil.angleBetweenVectors(n,vup);
        if (vleft.dot(n) > 0) {
            mat.rotateY(angle);
        }else{
            mat.rotateY(-angle);
        }

        float w = rushParticleOptions.getWidth()/2;
        float l = rushParticleOptions.getLength();
        FDColor color = rushParticleOptions.getColor();

        float addl = FDEasings.easeIn(p) * l;

        if (addl >= l){
            return;
        }

        vertex.addVertex(mat, -w/2, addl, 0).setColor(color.r,color.g,color.b,color.a);
        vertex.addVertex(mat, w/2, addl, 0).setColor(color.r,color.g,color.b,color.a);
        vertex.addVertex(mat, w/2, l, 0).setColor(color.r,color.g,color.b,color.a);
        vertex.addVertex(mat, -w/2, l, 0).setColor(color.r,color.g,color.b,color.a);


    }

    @Override
    public ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    @Override
    protected int getLightColor(float p_107249_) {
        return LightTexture.FULL_BRIGHT;
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
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
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
