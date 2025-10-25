package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDFreeEntityRenderer;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.finderfeed.fdlib.util.rendering.renderers.QuadRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class GeburahRenderer implements FDFreeEntityRenderer<GeburahEntity> {

    @Override
    public void render(GeburahEntity geburah, float v, float v1, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {

        if (geburah.isLaserVisualActive()){
            this.renderLasers(geburah,v,v1,poseStack,multiBufferSource,i);
        }

        this.renderSins(geburah, v, v1, poseStack, multiBufferSource, i);

    }

    private void renderSins(GeburahEntity geburah, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light){

        matrices.pushPose();

        Matrix4f tv_1 = geburah.getModelPartTransformation("tv_1_1",GeburahEntity.getClientModel());
        matrices.mulPose(tv_1);

        QuadRenderer.start(src.getBuffer(RenderType.text(FDBosses.location("textures/entities/geburah/screen_sin/base_screen.png"))))
                .pose(matrices)
                .sizeY(0.562f)
                .sizeX(1.185f)
                .offsetOnDirection(0.01f)
                .color(1f,1f,1f,0.85f)
                .direction(new Vec3(-1,0,0))
                .render();

        QuadRenderer.start(src.getBuffer(RenderType.text(FDBosses.location("textures/entities/geburah/screen_sin/sin_crystal_of_sin.png"))))
                .pose(matrices)
                .size(0.56f)
                .offsetOnDirection(0.011f)
                .color(1f,1f,1f,1f)
                .direction(new Vec3(-1,0,0))
                .render();

        matrices.popPose();
    }

    private void renderLasers(GeburahEntity geburah, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light){


        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        VertexConsumer vertex = src.getBuffer(RenderType.lightning());

        matrices.pushPose();



        float r = 0.3f;
        float g = 0.7f;
        float b = 1f;


        for (var pair : geburah.getCannonsPositionAndDirection(pticks)){

            matrices.pushPose();

            Vec3 dir = pair.second;

            Vec3 pos = pair.first;

            Vec3 posEnd = pos.add(dir.scale(GeburahEntity.MAX_LASERS_RADIUS));

            ClipContext clipContext = new ClipContext(pos,posEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
            var res = geburah.level().clip(clipContext);
            Vec3 location = res.getLocation();

            double len = location.distanceTo(pos);


            Vec3 offsetFromOrigin = pos.subtract(geburah.position());

            matrices.translate(offsetFromOrigin.x, offsetFromOrigin.y,offsetFromOrigin.z);

            FDRenderUtil.applyMovementMatrixRotations(matrices,dir);



            Vec3 n = FDMathUtil.getNormalVectorFromLineToPoint(pos,pos.add(dir.multiply(len,len,len)),camera.getPosition());


            Matrix4f mt2 = new Matrix4f();
            FDRenderUtil.applyMovementMatrixRotations(mt2,dir);
            Vector4f up = new Vector4f(0,0,1,1); mt2.transform(up);
            Vector4f left = new Vector4f(1,0,0,1); mt2.transform(left);
            Vec3 vup = new Vec3(up.x / up.w,up.y / up.w,up.z / up.w);
            Vec3 vleft = new Vec3(left.x,left.y,left.z);


            float betweenVectors = (float) FDMathUtil.angleBetweenVectors(n,vup);

            if (vleft.dot(n) > 0) {
                matrices.mulPose(Axis.YP.rotation(betweenVectors));
            }else{
                matrices.mulPose(Axis.YP.rotation(-betweenVectors));
            }

            float time = (geburah.tickCount + pticks) * 50;

            float p = (float) (Math.sin(time) + 1) / 2f;

            p = p * 0.4f + 0.6f;

            float alpha = 1f;


            float w = 0.3f * p;

            Matrix4f mat = matrices.last().pose();

            vertex.addVertex(mat, 0, 0, 0).setColor(r, g, b, alpha);
            vertex.addVertex(mat, w, 0, 0).setColor(r, g, b, 0f);
            vertex.addVertex(mat, w, (float) len, 0).setColor(r, g, b, 0f);
            vertex.addVertex(mat, 0, (float) len, 0).setColor(r, g, b, alpha);

            vertex.addVertex(mat, 0, (float) len, 0).setColor(r, g, b, alpha);
            vertex.addVertex(mat, -w, (float) len, 0).setColor(r, g, b, 0f);
            vertex.addVertex(mat, -w, 0, 0).setColor(r, g, b, 0f);
            vertex.addVertex(mat, 0, 0, 0).setColor(r, g, b, alpha);


            matrices.translate(0,0,0.005f);

            vertex.addVertex(mat,0,0,0f).setColor(1f,1f,1f,alpha);
            vertex.addVertex(mat,w * 0.15f,0,0f).setColor(1f,1f,1f,0f);
            vertex.addVertex(mat,w * 0.15f,(float)len,0f).setColor(1f,1f,1f,0f);
            vertex.addVertex(mat,0,(float)len,0f).setColor(1f,1f,1f,alpha);

            vertex.addVertex(mat,0,(float)len,0f).setColor(1f,1f,1f,alpha);
            vertex.addVertex(mat,-w * 0.15f,(float)len,0f).setColor(1f,1f,1f,0f);
            vertex.addVertex(mat,-w * 0.15f,0,0f).setColor(1f,1f,1f,0f);
            vertex.addVertex(mat,0,0,0f).setColor(1f,1f,1f,alpha);

            matrices.popPose();
        }

        matrices.popPose();;

    }


}
