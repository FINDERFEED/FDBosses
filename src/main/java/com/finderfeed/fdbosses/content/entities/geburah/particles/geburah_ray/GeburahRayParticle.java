package com.finderfeed.fdbosses.content.entities.geburah.particles.geburah_ray;

import com.finderfeed.fdlib.systems.particle.FDParticleRenderType;
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
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

public class GeburahRayParticle extends Particle {

    public Vec3 rayEnd;
    private GeburahRayOptions options;
    private ComplexEasingFunction easingFunction;

    public GeburahRayParticle(GeburahRayOptions rayOptions, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z, xd, yd, zd);
        this.options = rayOptions;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.rayEnd = rayOptions.rayEnd;
        this.setBoundingBox(new AABB(
                x,y,z,
                rayEnd.x,rayEnd.y,rayEnd.z
        ));
        this.lifetime = options.rayOptions.inTime + options.rayOptions.stayTime + options.rayOptions.outTime;
        easingFunction = ComplexEasingFunction.builder()
                .addArea(options.rayOptions.inTime, FDEasings::easeIn)
                .addArea(options.rayOptions.stayTime,FDEasings::one)
                .addArea(options.rayOptions.outTime,FDEasings::reversedEaseOut)
                .build();
        options.particleProcessor.init(this);
    }

    @Override
    public void tick() {

        options.particleProcessor.processParticle(this);

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {

            this.x += xd;
            this.y += yd;
            this.z += zd;

        }

        this.setBoundingBox(new AABB(
                x,y,z,
                rayEnd.x,rayEnd.y,rayEnd.z
        ));
    }

    @Override
    public void render(VertexConsumer vertex, Camera camera, float pticks) {

        Vec3 pos = new Vec3(
                Mth.lerp(pticks,this.xo,this.x),
                Mth.lerp(pticks,this.yo,this.y),
                Mth.lerp(pticks,this.zo,this.z)
        );
        Vec3 b = rayEnd.subtract(pos.x,pos.y,pos.z);
        pos = pos.subtract(camera.getPosition());
        double len = b.length();

        Matrix4f mat = new Matrix4f();
        mat.translate((float)pos.x,(float)pos.y,(float)pos.z);
        FDRenderUtil.applyMovementMatrixRotations(mat,b);





        Vec3 n = FDMathUtil.getNormalVectorFromLineToPoint(pos,b.add(pos),Vec3.ZERO);


        Matrix4f mt2 = new Matrix4f();
        FDRenderUtil.applyMovementMatrixRotations(mt2,b);
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

        float p = easingFunction.apply(this.age + pticks);

        float alpha = p * options.color.a;


        float w = options.rayWidth * p;
        for (int zh = 0; zh < 2; zh++) {
            vertex.vertex(mat, 0, 0, 0).color(options.color.r, options.color.g, options.color.b, alpha).endVertex();
            vertex.vertex(mat, w, 0, 0).color(options.color.r, options.color.g, options.color.b, 0f).endVertex();
            vertex.vertex(mat, w, (float) len, 0).color(options.color.r, options.color.g, options.color.b, 0f).endVertex();
            vertex.vertex(mat, 0, (float) len, 0).color(options.color.r, options.color.g, options.color.b, alpha).endVertex();

            vertex.vertex(mat, 0, 0, 0).color(options.color.r, options.color.g, options.color.b, alpha).endVertex();
            vertex.vertex(mat, -w, 0, 0).color(options.color.r, options.color.g, options.color.b, 0f).endVertex();
            vertex.vertex(mat, -w, (float) len, 0).color(options.color.r, options.color.g, options.color.b, 0f).endVertex();
            vertex.vertex(mat, 0, (float) len, 0).color(options.color.r, options.color.g, options.color.b, alpha).endVertex();
        }

        mat.translate(0,0,0.01f);
        vertex.vertex(mat,0,0,0f).color(1f,1f,1f,alpha).endVertex();
        vertex.vertex(mat,w * 0.15f,0,0f).color(1f,1f,1f,0f).endVertex();
        vertex.vertex(mat,w * 0.15f,(float)len,0f).color(1f,1f,1f,0f).endVertex();
        vertex.vertex(mat,0,(float)len,0f).color(1f,1f,1f,alpha).endVertex();

        vertex.vertex(mat,0,0,0f).color(1f,1f,1f,alpha).endVertex();
        vertex.vertex(mat,-w * 0.15f,0,0f).color(1f,1f,1f,0f).endVertex();
        vertex.vertex(mat,-w * 0.15f,(float)len,0f).color(1f,1f,1f,0f).endVertex();
        vertex.vertex(mat,0,(float)len,0f).color(1f,1f,1f,alpha).endVertex();

        mat.translate(0,0,0.01f);
        this.renderInitialEffect(vertex, mat, 0,pticks, 4, (float) len, this.options.rayWidth * 0.15f);

        mat.translate(0,0,0.01f);
        this.renderInitialEffect(vertex, mat, 2,pticks, 4, (float) len, this.options.rayWidth * 0.3f);


    }

    private void renderInitialEffect(VertexConsumer vertex, Matrix4f mat, int ageDelay,float pticks, int effectLength, float rayLength, float effectWidth){

        if (this.age < ageDelay){
            return;
        }

        float effectP = FDMathUtil.clamp(this.age - ageDelay + pticks, 0, effectLength) / effectLength;

        effectP = FDEasings.easeIn(effectP);

        float maxXBorder = this.options.rayWidth;

        float currentXStartPos = Math.max(0,(maxXBorder + effectWidth) * effectP - effectWidth);
        float currentXEndPos = Math.min(maxXBorder * effectP + effectWidth,maxXBorder);

        vertex.vertex(mat,currentXStartPos,0,0f).color(1f,1f,1f,1f).endVertex();
        vertex.vertex(mat,currentXEndPos,0,0f).color(1f,1f,1f,1f).endVertex();
        vertex.vertex(mat,currentXEndPos,(float)rayLength,0f).color(1f,1f,1f,1f).endVertex();
        vertex.vertex(mat,currentXStartPos,(float)rayLength,0f).color(1f,1f,1f,1f).endVertex();

        vertex.vertex(mat,-currentXStartPos,0,0f).color(1f,1f,1f,1f).endVertex();
        vertex.vertex(mat,-currentXEndPos,0,0f).color(1f,1f,1f,1f).endVertex();
        vertex.vertex(mat,-currentXEndPos,(float)rayLength,0f).color(1f,1f,1f,1f).endVertex();
        vertex.vertex(mat,-currentXStartPos,(float)rayLength,0f).color(1f,1f,1f,1f).endVertex();

    }

    @Override
    public ParticleRenderType getRenderType() {
        return ADDITIVE_TRANSLUCENT;
    }


    public static final ParticleRenderType ADDITIVE_TRANSLUCENT = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder tesselator, TextureManager p_107437_) {
            RenderSystem.depthMask(true);
            RenderSystem.enableBlend();
            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::getRendertypeLightningShader);
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE);
            tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        }

        @Override
        public void end(Tesselator p_107438_) {
            p_107438_.end();
            RenderSystem.setShader(GameRenderer::getParticleShader);
        }

        public String toString() {
            return "fdbosses:geburah_ray";
        }
    };


    public static class Factory implements ParticleProvider<GeburahRayOptions> {

        @Nullable
        @Override
        public Particle createParticle(GeburahRayOptions type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new GeburahRayParticle(type,level,x,y,z,xd,yd,zd);
        }
    }

}
