package com.finderfeed.fdbosses.client.particles.arc_lightning;

import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArcLightningParticle extends Particle {

    private ArcLightningOptions options;
    private Vec3 end;
    private Vec3 oldEnd;

    public ArcLightningParticle(ArcLightningOptions options,ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z, xd, yd, zd);
        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.options = options;
        this.lifetime = options.lifetime;
        this.end = this.options.end;
        this.oldEnd = this.options.end;
        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        super.tick();

        this.oldEnd = end;
        this.end = this.end.add(options.endSpeed);

        this.setBoundingBox(new AABB(x,y,z,end.x,end.y,end.z));
    }

    @Override
    public void render(VertexConsumer vertex, Camera camera, float partialTicks) {

        Vec3 pos = new Vec3(
                Mth.lerp(partialTicks,this.xo,this.x),
                Mth.lerp(partialTicks,this.yo,this.y),
                Mth.lerp(partialTicks,this.zo,this.z)
        ).subtract(camera.getPosition());

        Vec3 endi = FDMathUtil.interpolateVectors(oldEnd,this.end,partialTicks);

        Vec3 between = endi.subtract(camera.getPosition()).subtract(pos);

        double horLen = Math.sqrt(between.x * between.x + between.z * between.z);
        double verticalLength = between.y;

        List<Vec3> positions = new ArrayList<>();
        positions.add(Vec3.ZERO);

        int circleSegmentsAmount = 10;

        float circleOffset = options.circleOffset;

        Vec3 end = new Vec3(horLen,verticalLength,0);
        Vec3 b = end.normalize();
        Vec3 center = end.multiply(0.5,0.5,0);

        Vec3 circleCenter = center.add(b.zRot(-(float)Math.PI / 2).multiply(circleOffset,circleOffset,0));


        Vec3 v1 = end.subtract(circleCenter);
        Vec3 v2 = circleCenter.reverse();
        double angle;
        if (circleOffset >= 0){
            angle = (Math.PI * 2 - FDMathUtil.angleBetweenVectors(v1,v2)) / circleSegmentsAmount;
        }else{
            angle = (FDMathUtil.angleBetweenVectors(v1,v2)) / circleSegmentsAmount;
        }
        Matrix4f rot = new Matrix4f().identity().rotateZ(-(float)angle);
        Vector4f v = new Vector4f((float)v2.x,(float)v2.y,0,1);
        for (int i = 0; i < circleSegmentsAmount - 1;i++){
            v = rot.transform(v);
            v.x /= v.w;
            v.y /= v.w;
            v.w = 1;

            positions.add(new Vec3(v.x + circleCenter.x,v.y + circleCenter.y,0));

        }

        positions.add(end);


        int lightningCounts = options.lightningSegments;
        var path = buildPath(level.getGameTime(), options.lightningRandomSpread, options.seed,lightningCounts,positions);

        Matrix4f mat = new Matrix4f();
        mat.translate(
                (float)pos.x,
                (float)pos.y,
                (float)pos.z
        );
        mat.rotateY(-(float)Math.atan2(between.z,between.x));

        FDColor color = options.color;

        drawLightning(mat,vertex,path,positions,options.lightningWidth,color.r,color.g,color.b,1f);

        mat.translate(0,0,0.001f);
        drawLightning(mat,vertex,path,positions,options.lightningWidth * 0.15f,1f,1,1f,1f);
        mat.translate(0,0,-0.002f);
        drawLightning(mat,vertex,path,positions,options.lightningWidth * 0.15f,1f,1,1f,1f);

    }

    public static void fullLightningImmediateDraw(long time,int seed,int lightningBreakCount,Matrix4f transform, List<Vec3> path, float lightningWidth, float lightningRandomSpread,float r,float g,float b,float a){

        Tesselator tesselator = RenderSystem.renderThreadTesselator();

        var builder = RENDER_TYPE.begin(tesselator, Minecraft.getInstance().getTextureManager());

        List<Vec3> positions = buildPath(time,lightningRandomSpread,seed,lightningBreakCount,path);

        drawLightning(transform,builder,positions,path,lightningWidth,r,g,b,a);

        BufferUploader.drawWithShader(builder.build());

    }
    public static void fullLightningDraw(long time,int seed,int lightningBreakCount,Matrix4f transform,VertexConsumer vertexConsumer, List<Vec3> path, float lightningWidth, float lightningRandomSpread,float r,float g,float b,float a){

        List<Vec3> positions = buildPath(time,lightningRandomSpread,seed,lightningBreakCount,path);

        drawLightning(transform,vertexConsumer,positions,path,lightningWidth,r,g,b,a);

    }


    public static void drawLightning(Matrix4f transform,VertexConsumer vertex,List<Vec3> path,List<Vec3> positions,float lightningWidth,float r,float g,float b,float a){
        Vec3 previousCenteredVector = new Vec3(0,1,0);
        Vec3 prevPoint = null;
        double previousw = 0;

        for (int i = 1; i < path.size() - 1;i++){
            Vec3 p1 = path.get(i - 1);
            Vec3 p2 = path.get(i);
            Vec3 p3 = path.get(i + 1);
            Vec3 v1 = p2.subtract(p1);
            Vec3 v2 = p3.subtract(p2);
            double dot = v1.reverse().dot(v2);
            double angle = Math.acos(dot / (v1.length() * v2.length()));
            double sin;
            if (angle != 0){
                sin = Math.sin(angle / 2);
            }else{
                sin = 1;
            }


            Vec3 v = findCenteredVector(v1,v2);
            if (v.dot(previousCenteredVector) < 0){
                v = v.reverse();
            }

            double w = lightningWidth / sin;

            vertex.addVertex(transform,
                    (float) (p1.x),
                    (float) (p1.y),
                    0
            ).setColor(r,g,b,i == 1 ? 0 : a);
            vertex.addVertex(transform,
                    (float) (p1.x + previousCenteredVector.x * previousw),
                    (float) (p1.y + previousCenteredVector.y * previousw),
                    0
            ).setColor(r,g,b,0f);
            vertex.addVertex(transform,
                    (float) (p2.x + v.x * w),
                    (float) (p2.y + v.y * w),
                    0
            ).setColor(r,g,b,0f);
            vertex.addVertex(transform,
                    (float) (p2.x),
                    (float) (p2.y),
                    0
            ).setColor(r,g,b,a);

            vertex.addVertex(transform,
                    (float) (p1.x),
                    (float) (p1.y),
                    0
            ).setColor(r,g,b,i == 1 ? 0 : a);
            vertex.addVertex(transform,
                    (float) (p1.x - previousCenteredVector.x * previousw),
                    (float) (p1.y - previousCenteredVector.y * previousw),
                    0
            ).setColor(r,g,b,0f);
            vertex.addVertex(transform,
                    (float) (p2.x - v.x * w),
                    (float) (p2.y - v.y * w),
                    0
            ).setColor(r,g,b,0f);
            vertex.addVertex(transform,
                    (float) (p2.x),
                    (float) (p2.y),
                    0
            ).setColor(r,g,b,a);

            prevPoint = p2;
            previousw = w;
            previousCenteredVector = v;
        }


        Vec3 lastPos = positions.getLast();


        vertex.addVertex(transform,
                (float) (prevPoint.x),
                (float) (prevPoint.y),
                0
        ).setColor(r,g,b,a);
        vertex.addVertex(transform,
                (float) (prevPoint.x + previousCenteredVector.x * previousw),
                (float) (prevPoint.y + previousCenteredVector.y * previousw),
                0
        ).setColor(r,g,b,0f);
        vertex.addVertex(transform,
                (float) (lastPos.x),
                (float) (lastPos.y),
                0
        ).setColor(r,g,b,0f);
        vertex.addVertex(transform,
                (float) (lastPos.x),
                (float) (lastPos.y),
                0
        ).setColor(r,g,b,a);

        vertex.addVertex(transform,
                (float) (prevPoint.x),
                (float) (prevPoint.y),
                0
        ).setColor(r,g,b,a);
        vertex.addVertex(transform,
                (float) (prevPoint.x - previousCenteredVector.x * previousw),
                (float) (prevPoint.y - previousCenteredVector.y * previousw),
                0
        ).setColor(r,g,b,0f);
        vertex.addVertex(transform,
                (float) (lastPos.x),
                (float) (lastPos.y),
                0
        ).setColor(r,g,b,0f);
        vertex.addVertex(transform,
                (float) (lastPos.x),
                (float) (lastPos.y),
                0
        ).setColor(r,g,b,a);


    }

    public static List<Vec3> buildPath(long time,float lightningRandomSpread,int seed,int lightningCounts, List<Vec3> positions){

        float step = 1f / lightningCounts;

        Random r = new Random(time * seed);

        List<Vec3> path = new ArrayList<>();
        path.add(positions.get(0));
        float lightningSpread = lightningRandomSpread;

        for (float i = step; i <= 1 - step/2; i += step){
            float gl = i * (positions.size() - 1);
            float lp = gl - (int) gl;
            Vec3 current = positions.get((int)gl);
            Vec3 next = positions.get((int)gl + 1);

            Vec3 b = next.subtract(current);
            Vec3 nb = b.normalize();
            float rmod = r.nextFloat() * lightningSpread * 2 - lightningSpread;
            Vec3 point = current.add(b.multiply(lp,lp,0))
                    .add(nb.zRot((float)Math.PI / 2).multiply(rmod,rmod,rmod));
            path.add(point);
        }
        path.add(positions.getLast());
        return path;
    }


    private static Vec3 getDirectionFromPositions(Vec3[] positions, int index){
        if (index >= positions.length - 1){
            return positions[positions.length - 1].subtract(positions[positions.length - 2]);
        }else if (index < 0){
            return positions[1].subtract(positions[0]);
        }
        return positions[index + 1].subtract(positions[index]);

    }

    private static Vec3 findCenteredVector(Vec3 v1, Vec3 v2){
        v1 = v1.reverse().normalize();
        v2 = v2.normalize();
        return v1.add(v2).normalize();
    }




    public static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Nullable
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager manager) {
            RenderSystem.depthMask(true);
            RenderSystem.enableBlend();
            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::getRendertypeLightningShader);
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        }

        @Override
        public boolean isTranslucent() {
            return true;
        }

        @Override
        public String toString() {
            return "ARC_LIGHTNING_RENDER_TYPE";
        }
    };




    @Override
    public ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }


    public static class Factory implements ParticleProvider<ArcLightningOptions>{

        @Nullable
        @Override
        public Particle createParticle(ArcLightningOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new ArcLightningParticle(options,level,x,y,z,xd,yd,zd);
        }
    }

}

//    private Vec3[] placeVertices(VertexConsumer vertex,Matrix4f transform,Vec3 v1,Vec3 v2,Vec3 v3,Vec3 prevBelow,Vec3 prevAbove){
//        Vec3 b = v2.subtract(v1);
//        Vec3 b1 = v3.subtract(v2);
//        Vec3 rotated = b.normalize().multiply(lightningWidth,lightningWidth,0).zRot((float) Math.PI / 2);
//        Vec3 rrotated = rotated.reverse();
//        Vec3 rotated1 = b1.normalize().multiply(lightningWidth,lightningWidth,0).zRot((float) Math.PI / 2);
//        Vec3 rrotated1 = rotated1.reverse();
//        Vec3 pb1 = v1.add(rotated);
//        Vec3 pb2 = v2.add(rotated);
//
//        Vec3 pb3 = v2.add(rotated1);
//        Vec3 pb4 = v3.add(rotated1);
//
//        Vec3 pa1 = v1.add(rrotated);
//        Vec3 pa2 = v2.add(rrotated);
//
//        Vec3 pa3 = v2.add(rrotated1);
//        Vec3 pa4 = v3.add(rrotated1);
//        double kb1 = (pb2.y - pb1.y) / (pb2.x - pb1.x);
//        double bb1 = -(pb1.x * pb2.y - pb2.x * pb1.y) / (pb2.x - pb1.x);
//
//        double kb2 = (pb4.y - pb3.y) / (pb4.x - pb3.x);
//        double bb2 = -(pb3.x * pb4.y - pb4.x * pb3.y) / (pb4.x - pb3.x);
//
//        double xb = (bb2 - bb1) / (kb1 - kb2);
//
//        double ka1 = (pa2.y - pa1.y) / (pa2.x - pa1.x);
//        double ba1 = -(pa1.x * pa2.y - pa2.x * pa1.y) / (pa2.x - pa1.x);
//
//        double ka2 = (pa4.y - pa3.y) / (pa4.x - pa3.x);
//        double ba2 = -(pa3.x * pa4.y - pa4.x * pa3.y) / (pa4.x - pa3.x);
//
//        double xa = (ba2 - ba1) / (ka1 - ka2);
//        Vec3 pBelow = new Vec3(
//                xb,
//                kb1 * xb + bb1,
//                0
//        );
//        Vec3 pAbove = new Vec3(
//                xa,
//                ka1 * xa + ba1,
//                0
//        );
//        vertex.addVertex(transform,(float) prevBelow.x,(float) prevBelow.y,0).setColor(1f,1f,1f,1f);
//        vertex.addVertex(transform,(float) pBelow.x,(float) pBelow.y,0).setColor(1f,1f,1f,1f);
//        vertex.addVertex(transform,(float) pAbove.x,(float) pAbove.y,0).setColor(1f,1f,1f,1f);
//        vertex.addVertex(transform,(float) prevAbove.x,(float) prevAbove.y,0).setColor(1f,1f,1f,1f);
//        return new Vec3[]{pBelow,pAbove};
//    }

//        float step = 0.01f;
//
//        Vec3 v1 = catmullromPositions[0];
//        Vec3 v2 = FDMathUtil.catmullRom(catmullromPositions,step);
//        Vec3 v3 = FDMathUtil.catmullRom(catmullromPositions,step * 2);
//        Vec3 prevBelow = new Vec3(0,-lightningWidth,0);
//        Vec3 prevAbove = new Vec3(0,lightningWidth,0);
//        for (float i = step * 3; i <= 1;i += step){
//
//            vertex.addVertex(matrix4f,(float)v1.x,(float)v1.y,0).setColor(1f,0f,0f,1f);
//            vertex.addVertex(matrix4f,(float)v2.x,(float)v2.y,0).setColor(1f,0f,0f,1f);
//
//            vertex.addVertex(matrix4f,(float)v2.x,(float)v2.y,0.1f).setColor(1f,1f,0f,1f);
//            vertex.addVertex(matrix4f,(float)v3.x,(float)v3.y,0.1f).setColor(1f,1f,0f,1f);
//            var vecs = this.placeVertices(vertex,matrix4f,v1,v2,v3,prevBelow,prevAbove);
//            prevBelow = vecs[0];
//            prevAbove = vecs[1];
//            v1 = v2;
//            v2 = v3;
//            v3 = FDMathUtil.catmullRom(catmullromPositions,i);
//        }

//        this.placeVertices(vertex,matrix4f,v1,v2,v3,prevBelow,prevAbove);
