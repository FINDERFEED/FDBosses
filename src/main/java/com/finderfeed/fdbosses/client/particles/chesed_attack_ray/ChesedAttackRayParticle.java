package com.finderfeed.fdbosses.client.particles.chesed_attack_ray;

import com.finderfeed.fdbosses.client.particles.arc_lightning.ArcLightningParticle;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

public class ChesedAttackRayParticle extends Particle {

    public Vec3 rayEnd;
    private ChesedRayOptions options;
    private ComplexEasingFunction easingFunction;

    public ChesedAttackRayParticle(ChesedRayOptions rayOptions,ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
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

        float p = easingFunction.apply(this.age);

        float alpha = p * options.color.a;


        float w = options.rayWidth * p;
        vertex.addVertex(mat,0,0,0).setColor(options.color.r,options.color.g,options.color.b,alpha);
        vertex.addVertex(mat,w,0,0).setColor(options.color.r,options.color.g,options.color.b,0f);
        vertex.addVertex(mat,w,(float)len,0).setColor(options.color.r,options.color.g,options.color.b,0f);
        vertex.addVertex(mat,0,(float)len,0).setColor(options.color.r,options.color.g,options.color.b,alpha);

        vertex.addVertex(mat,0,0,0).setColor(options.color.r,options.color.g,options.color.b,alpha);
        vertex.addVertex(mat,-w,0,0).setColor(options.color.r,options.color.g,options.color.b,0f);
        vertex.addVertex(mat,-w,(float)len,0).setColor(options.color.r,options.color.g,options.color.b,0f);
        vertex.addVertex(mat,0,(float)len,0).setColor(options.color.r,options.color.g,options.color.b,alpha);

        mat.translate(0,0,0.01f);
        vertex.addVertex(mat,0,0,0f).setColor(1f,1f,1f,alpha);
        vertex.addVertex(mat,w * 0.15f,0,0f).setColor(1f,1f,1f,0f);
        vertex.addVertex(mat,w * 0.15f,(float)len,0f).setColor(1f,1f,1f,0f);
        vertex.addVertex(mat,0,(float)len,0f).setColor(1f,1f,1f,alpha);

        vertex.addVertex(mat,0,0,0f).setColor(1f,1f,1f,alpha);
        vertex.addVertex(mat,-w * 0.15f,0,0f).setColor(1f,1f,1f,0f);
        vertex.addVertex(mat,-w * 0.15f,(float)len,0f).setColor(1f,1f,1f,0f);
        vertex.addVertex(mat,0,(float)len,0f).setColor(1f,1f,1f,alpha);




            var positions = List.of(
                    Vec3.ZERO,new Vec3(0,len,0)
            );
            var path = ArcLightningParticle.buildPath(level.getGameTime(), w * 2, 342332, Math.max((int) Math.round(len / (2 * w * 2)), 2), positions);
            mat.translate(0, 0, 0.02f);
            ArcLightningParticle.drawLightning(mat, vertex, path, positions, w * 0.5f, options.lightningColor.r, options.lightningColor.g, options.lightningColor.b, alpha);

            mat.translate(0, 0, 0.02f);
            ArcLightningParticle.drawLightning(mat, vertex, path, positions, w * 0.5f * 0.15f, 1f,1f, 1f, alpha);
            mat.translate(0, 0, -0.06f);
            ArcLightningParticle.drawLightning(mat, vertex, path, positions, w * 0.5f * 0.15f, 1f, 1f, 1f, alpha);

    }

    @Override
    public ParticleRenderType getRenderType() {
        return ArcLightningParticle.RENDER_TYPE;
    }


    public static class Factory implements ParticleProvider<ChesedRayOptions>{

        @Nullable
        @Override
        public Particle createParticle(ChesedRayOptions type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new ChesedAttackRayParticle(type,level,x,y,z,xd,yd,zd);
        }
    }

}
