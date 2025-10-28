package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSin;
import com.finderfeed.fdbosses.init.BossRegistries;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDFreeEntityRenderer;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.finderfeed.fdlib.util.rendering.renderers.QuadRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.List;

public class GeburahRenderer implements FDFreeEntityRenderer<GeburahEntity> {

    @Override
    public void render(GeburahEntity geburah, float v, float v1, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {

        if (geburah.isLaserVisualActive()){
            this.renderLasers(geburah,v,v1,poseStack,multiBufferSource,i);
        }

        this.renderSins(geburah,v,v1,poseStack,multiBufferSource,i);

        this.renderRayPreparations(geburah, v, v1, poseStack, multiBufferSource, i);

    }

    private void renderRayPreparations(GeburahEntity geburah, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light){


        var attackPreparator = geburah.laserAttackPreparator;

        if (attackPreparator.time != -1) {

            var cannons = geburah.getCannonsPositionAndDirection(pticks);

            float time = attackPreparator.time - attackPreparator.currentTime + pticks;


            float centerOffset = 4.75f;
            float length = GeburahEntity.ARENA_RADIUS - centerOffset;
            int fadeIn = 10;
            float stayTime = attackPreparator.time - fadeIn - GeburahLaserAttackPreparator.FADE_OUT;
            int fadeOut = GeburahLaserAttackPreparator.FADE_OUT;

            ComplexEasingFunction lengthEasingFunction = ComplexEasingFunction.builder()
                    .addArea(fadeIn, FDEasings::easeOut)
                    .addArea(stayTime + fadeOut, FDEasings::one)
                    .build();

            ComplexEasingFunction upperLengthEasingFunction = ComplexEasingFunction.builder()
                    .addArea(fadeIn + stayTime, FDEasings::linear)
                    .addArea(fadeOut, FDEasings::one)
                    .build();


            ComplexEasingFunction alphaEasingFunction = ComplexEasingFunction.builder()
                    .addArea(fadeIn, FDEasings::easeOut)
                    .addArea(stayTime, FDEasings::one)
                    .addArea(fadeOut, FDEasings::reversedEaseOut)
                    .build();

            VertexConsumer vertex = src.getBuffer(RenderType.lightning());

            float l = length * lengthEasingFunction.apply(time);
            float l2 = length * upperLengthEasingFunction.apply(time);
            float alpha = alphaEasingFunction.apply(time);

            float wsize = 0.5f;

            for (var pair : cannons) {
                matrices.pushPose();
                Vec3 direction = pair.second;

                matrices.translate(direction.x * centerOffset, GeburahEntity.LASERS_PREPARATION_OFFSET, direction.z * centerOffset);
                FDRenderUtil.applyMovementMatrixRotations(matrices, direction);

                Matrix4f mat = matrices.last().pose();

                vertex.addVertex(mat, -wsize, l2, 0).setColor(0.3f, 0.5f, 1f, 0.25f * alpha);
                vertex.addVertex(mat, -wsize, l, 0).setColor(0.3f, 0.5f, 1f, 0.25f * alpha);
                vertex.addVertex(mat, wsize, l, 0).setColor(0.3f, 0.5f, 1f, 0.25f * alpha);
                vertex.addVertex(mat, wsize, l2, 0).setColor(0.3f, 0.5f, 1f, 0.25f * alpha);


                vertex.addVertex(mat, -wsize, 0, 0).setColor(0.3f, 1f, 1f, 0.5f * alpha);
                vertex.addVertex(mat, -wsize, l2, 0).setColor(0.3f, 1f, 1f, 0.5f * alpha);
                vertex.addVertex(mat, wsize, l2, 0).setColor(0.3f, 1f, 1f, 0.5f * alpha);
                vertex.addVertex(mat, wsize, 0, 0).setColor(0.5f, 1f, 1f, 0.5f * alpha);

                matrices.popPose();
            }

        }


    }

    private void renderSins(GeburahEntity geburah, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light){

        List<PlayerSin> playerSins = geburah.getEntityData().get(GeburahEntity.ACTIVE_SINS);

        if (playerSins.isEmpty()){
            this.renderSinAtScreen(null, 1, geburah, pticks, matrices, src);
            this.renderSinAtScreen(null, 2, geburah, pticks, matrices, src);
            this.renderSinAtScreen(null, 3, geburah, pticks, matrices, src);
            this.renderSinAtScreen(null, 4, geburah, pticks, matrices, src);
        }else{

            for (int i = 0; i < 4; i++){
                int id = i % playerSins.size();
                PlayerSin sin = playerSins.get(id);
                int screenId = i + 1;
                this.renderSinAtScreen(sin,screenId,geburah,pticks,matrices,src);
            }

        }

    }

    private void renderSinAtScreen(PlayerSin sin, int screenId, GeburahEntity geburah, float pticks, PoseStack matrices, MultiBufferSource src){
        matrices.pushPose();

        Matrix4f tv_1 = geburah.getModelPartTransformation("tv_1_"+screenId,GeburahEntity.getClientModel());
        matrices.mulPose(tv_1);
        

        QuadRenderer.start(src.getBuffer(RenderType.entityCutout(FDBosses.location("textures/entities/geburah/screen_sin/base_screen.png"))))
                .pose(matrices)
                .sizeY(0.562f)
                .sizeX(1.185f)
                .light(LightTexture.FULL_BRIGHT)
                .offsetOnDirection(0.01f)
                .color(1f,1f,1f,0.85f)
                .direction(new Vec3(-1,0,0))
                .render();

        if (sin != null) {
            var key = BossRegistries.PLAYER_SINS.getKey(sin);
            var idStart = key.getPath();
            QuadRenderer.start(src.getBuffer(RenderType.entityCutout(FDBosses.location("textures/entities/geburah/screen_sin/sin_" + idStart + ".png"))))
                    .pose(matrices)
                    .size(0.56f)
                    .light(LightTexture.FULL_BRIGHT)
                    .offsetOnDirection(0.011f)
                    .color(1f, 1f, 1f, 1f)
                    .direction(new Vec3(-1, 0, 0))
                    .render();
        }

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
