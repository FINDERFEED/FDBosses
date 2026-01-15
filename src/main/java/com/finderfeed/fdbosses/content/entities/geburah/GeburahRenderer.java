package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSin;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdbosses.init.BossRegistries;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDFreeEntityRenderer;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.List;

public class GeburahRenderer implements FDFreeEntityRenderer<GeburahEntity> {

    public static final ResourceLocation HALO_EXPLOSION = FDBosses.location("textures/entities/geburah/geburah_halo_explosion.png");
    public static final ResourceLocation HALO = FDBosses.location("textures/entities/geburah/geburah_halo.png");
    public static final ResourceLocation RAY_PREPARE_FRAME = FDBosses.location("textures/entities/geburah/geburah_ray_preparing.png");

    public static final int RAY_ATTACK_PREPARATION_TIME = 30;

    public static final int HAMMER_AMOUNT = 8;
    private static final int IMPACT_TIME = 5;
    private static final int END_PREPARING_SIN_PUNISHMENT = GeburahEntity.SIN_PUNISHMENT_ATTACK_DURATION - IMPACT_TIME;

    private static final ComplexEasingFunction GEBURAH_SIN_PUNISHMENT_EASING = ComplexEasingFunction.builder()
            .addArea(END_PREPARING_SIN_PUNISHMENT, FDEasings::linear)
            .addArea(IMPACT_TIME, FDEasings::linear)
            .build();

    private static FDModel hammerModel;

    public GeburahRenderer(){

    }

    @Override
    public void render(GeburahEntity geburah, float v, float v1, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        if (hammerModel == null){
            hammerModel = new FDModel(BossModels.JUSTICE_HAMMER.get());
        }
        this.renderLasers(geburah,v,v1,poseStack,multiBufferSource,i);
        this.renderSins(geburah,v,v1,poseStack,multiBufferSource,i);
        this.renderRayPreparations(geburah, v, v1, poseStack, multiBufferSource, i);
        this.renderSinPunishmentAttackEffect(geburah, v, v1, poseStack, multiBufferSource, i);
        this.renderStartOperating(geburah, v, v1, poseStack, multiBufferSource, i);
        this.renderSecondPhase(geburah, v, v1, poseStack, multiBufferSource, i);
        this.renderDeath(geburah, v, v1, poseStack, multiBufferSource, i);
        this.renderRoundAndRoundRayPreparators(geburah, v, v1, poseStack, multiBufferSource, i);

    }


    private void renderRoundAndRoundRayPreparators(GeburahEntity geburah, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light){

        int tick = geburah.rayAttackPreparationTicker;
        if (tick == -1) return;

        float time = Mth.clamp(RAY_ATTACK_PREPARATION_TIME - tick + pticks,0, RAY_ATTACK_PREPARATION_TIME);

        VertexConsumer vertex = src.getBuffer(RenderType.text(RAY_PREPARE_FRAME));

        float frameTime = 10;
        for (var cannon : geburah.getCannonsPositionAndDirection(pticks)){

            Vec3 pos = cannon.first;
            Vec3 direction = cannon.second;

            Vec3 offs = pos.subtract(geburah.getPosition(pticks));

            matrices.pushPose();
            matrices.translate(offs.x,offs.y,offs.z);

            for (int i = 0; i < 6; i++){
                int startTime2 = i * 3;
                if (time > startTime2 && time <= startTime2 + frameTime) {
                    float p = FDEasings.easeIn((time - startTime2) / frameTime);
                    p *= p;
                    QuadRenderer.start(vertex)
                            .renderBack()
                            .offsetOnDirection(-0.25f + p * 0.5f)
                            .direction(direction)
                            .size(1 - p)
                            .color(1f,1f,1f,p)
                            .pose(matrices)
                            .render();
                }
            }

            matrices.popPose();
        }


    }

    private void renderSecondPhase(GeburahEntity geburah, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light){

        if (!geburah.getEntityData().get(GeburahEntity.SECOND_PHASE)) return;

        float time = geburah.clientSecondPhaseTicker + pticks;
        Vec3 corePos = geburah.getCorePosition().subtract(geburah.position());


        float haloRot = time / 10;

        float haloExplosionTime = 35;
        if (time <= haloExplosionTime) {
            float p = Mth.clamp(time / haloExplosionTime, 0, 1);


            QuadRenderer.start(src.getBuffer(RenderType.text(HALO_EXPLOSION)))
                    .pose(matrices)
                    .translate(0, (float) corePos.y, 0)
                    .size(FDEasings.easeOut(FDEasings.easeOut(p)) * 32)
                    .rotationDegrees(FDEasings.easeOut(FDEasings.easeOut(p)) * 20)
                    .color(1f, 1f, 1f, (1 - p) * 0.8f )
                    .renderBack()
                    .render();

            QuadRenderer.start(src.getBuffer(RenderType.text(HALO)))
                    .pose(matrices)
                    .direction(new Vec3(0,-1,0))
                    .translate(0, (float) corePos.y + 3 * FDEasings.easeOut(p), 0)
                    .size(FDEasings.easeOut(FDEasings.easeOut(p)) * 21)
                    .rotationDegrees(-haloRot + 20 - FDEasings.easeOut(p) * 20)
                    .color(1f, 1f, 1f, p * 0.75f )
                    .renderBack()
                    .render();

        }else{
            QuadRenderer.start(src.getBuffer(RenderType.text(HALO)))
                    .pose(matrices)
                    .direction(new Vec3(0,-1,0))
                    .translate(0, (float) corePos.y + 3, 0)
                    .size(21)
                    .rotationDegrees(-haloRot)
                    .color(1f, 1f, 1f, 0.75f)
                    .renderBack()
                    .render();
        }



    }

    private void renderStartOperating(GeburahEntity geburah, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light){

        if (!geburah.getEntityData().get(GeburahEntity.OPERATING)) return;

        float time = geburah.clientOperatingTicks + pticks;
        Vec3 corePos = geburah.getCorePosition().subtract(geburah.position());

        float haloExplosionTime = 35;
        if (time <= haloExplosionTime) {
            float p = Mth.clamp(time / haloExplosionTime, 0, 1);


            QuadRenderer.start(src.getBuffer(RenderType.text(HALO_EXPLOSION)))
                    .pose(matrices)
                    .translate(0, (float) corePos.y, 0)
                    .size(FDEasings.easeOut(FDEasings.easeOut(p)) * 26)
                    .rotationDegrees(FDEasings.easeOut(FDEasings.easeOut(p)) * 20)
                    .color(1f, 1f, 1f, (1 - p) * 0.8f )
                    .renderBack()
                    .render();

        }




    }

    private void renderSinPunishmentAttackEffect(GeburahEntity geburah, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light){

        int tick = geburah.sinPunishmentAttackTicker;
        if (tick == -1) return;



        float time = Mth.clamp((GeburahEntity.SIN_PUNISHMENT_ATTACK_DURATION - tick) + pticks, 0, GeburahEntity.SIN_PUNISHMENT_ATTACK_DURATION);

        float maxHammerHeight = 22;
        float hammerScale = 1f;

        float hammerHeight = maxHammerHeight;
        float hammerRotation = 360;
        float alpha = 1;


        float localP = GEBURAH_SIN_PUNISHMENT_EASING.apply(time);

        if (time < END_PREPARING_SIN_PUNISHMENT){
            alpha = localP;
            hammerRotation = 360 * FDEasings.easeOut(Mth.clamp(localP * 1.1f,0,1));
            hammerScale = localP;
        }else{
            hammerHeight = maxHammerHeight * (1 - FDEasings.easeIn(localP));
        }

        float offset = GeburahEntity.BELL_ATTACK_HAMMER_OFFSET;
        float angle = 360f / HAMMER_AMOUNT;

        for (int i = 0; i < HAMMER_AMOUNT; i++){
            matrices.pushPose();

            matrices.mulPose(Axis.YP.rotationDegrees(angle * i));
            matrices.translate(offset,hammerHeight,0);
            matrices.mulPose(Axis.YP.rotationDegrees(hammerRotation + 90));
            matrices.mulPose(Axis.XP.rotationDegrees(180));
            matrices.scale(hammerScale,hammerScale,hammerScale);

            hammerModel.render(matrices,src.getBuffer(RenderType.lightning()), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0.3f,0.7f,1f,alpha * 0.75f);

            matrices.popPose();
        }

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

                vertex.vertex(mat, -wsize, l2, 0).color(0.3f, 0.5f, 1f, 0.25f * alpha).endVertex();
                vertex.vertex(mat, -wsize, l, 0).color(0.3f, 0.5f, 1f, 0.25f * alpha).endVertex();
                vertex.vertex(mat, wsize, l, 0).color(0.3f, 0.5f, 1f, 0.25f * alpha).endVertex();
                vertex.vertex(mat, wsize, l2, 0).color(0.3f, 0.5f, 1f, 0.25f * alpha).endVertex();


                vertex.vertex(mat, -wsize, 0, 0).color(0.3f, 1f, 1f, 0.5f * alpha).endVertex();
                vertex.vertex(mat, -wsize, l2, 0).color(0.3f, 1f, 1f, 0.5f * alpha).endVertex();
                vertex.vertex(mat, wsize, l2, 0).color(0.3f, 1f, 1f, 0.5f * alpha).endVertex();
                vertex.vertex(mat, wsize, 0, 0).color(0.5f, 1f, 1f, 0.5f * alpha).endVertex();

                matrices.popPose();
            }

        }


    }

    private void renderSins(GeburahEntity geburah, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light){

        List<PlayerSin> playerSins = geburah.getEntityData().get(GeburahEntity.ACTIVE_SINS);

        if (playerSins.isEmpty()){
            this.renderSinsAtScreen(null, 1, geburah, pticks, matrices, src);
            this.renderSinsAtScreen(null, 2, geburah, pticks, matrices, src);
            this.renderSinsAtScreen(null, 3, geburah, pticks, matrices, src);
            this.renderSinsAtScreen(null, 4, geburah, pticks, matrices, src);
        }else{

            for (int i = 0; i < 4; i++){
                int screenId = i + 1;
                this.renderSinsAtScreen(playerSins,screenId,geburah,pticks,matrices,src);
            }

        }

    }

    private void renderSinsAtScreen(List<PlayerSin> sins, int screenId, GeburahEntity geburah, float pticks, PoseStack matrices, MultiBufferSource src){
        matrices.pushPose();

        float time = geburah.clientOperatingTicks + pticks;

        float screenStartup = 11f;
        float alpha = 0f;
        if (geburah.getEntityData().get(GeburahEntity.OPERATING)){
            if (time <= screenStartup){
                alpha = ((float)Math.sin(time / screenStartup * FDMathUtil.FPI * 8f) + 1) / 2f * 0.75f + 0.25f;
            }else{
                alpha = 1f;
            }
        }


        Matrix4f tv_1 = geburah.getModelPartTransformation("tv_1_"+screenId,GeburahEntity.getClientModel(), pticks);
        matrices.mulPoseMatrix(tv_1);

        VertexConsumer vrtx;

        if (time <= screenStartup){
            vrtx = src.getBuffer(RenderType.entityTranslucent(FDBosses.location("textures/entities/geburah/screen_sin/base_screen.png")));
        }else{
            vrtx = src.getBuffer(RenderType.entityCutout(FDBosses.location("textures/entities/geburah/screen_sin/base_screen.png")));
        }
        QuadRenderer.start(vrtx)
                .pose(matrices)
                .sizeY(0.562f)
                .sizeX(1.185f)
                .light(LightTexture.FULL_BRIGHT)
                .offsetOnDirection(0.01f)
                .color(1f,1f,1f,alpha)
                .direction(new Vec3(-1,0,0))
                .render();

        if (sins != null) {

            float sinSize = 0.5f;

            int size = sins.size();

            float startOffset = 0;

            if (size % 2 == 0){
                startOffset = sinSize + (size / 2f - 1) * sinSize * 2;
            }else{
                startOffset = (size / 2) * sinSize;
            }

            for (int i = 0; i < size; i++) {
                var sin = sins.get(i);

                var key = BossRegistries.PLAYER_SINS.get().getKey(sin);
                var idStart = key.getPath();
                QuadRenderer.start(src.getBuffer(RenderType.entityCutout(FDBosses.location("textures/entities/geburah/screen_sin/sin_" + idStart + ".png"))))
                        .pose(matrices)
                        .size(sinSize)
                        .light(LightTexture.FULL_BRIGHT)
                        .offsetOnDirection(0.011f)
                        .translate(0,0,-startOffset + sinSize * i * 2)
                        .color(1f, 1f, 1f, 1f)
                        .direction(new Vec3(-1, 0, 0))
                        .render();
            }
        }

        matrices.popPose();

    }

    private void renderLasers(GeburahEntity geburah, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light){

        float alpha = geburah.getLaserVisualAlpha(pticks);


        if (alpha == 0){
            return;
        }

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

            Vec3 posEnd = pos.add(dir.scale(GeburahEntity.MAX_LASERS_RADIUS - 5));

            ClipContext clipContext = new ClipContext(pos,posEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null);
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

            float w = 0.3f * p;

            Matrix4f mat = matrices.last().pose();

            vertex.vertex(mat, 0, 0, 0).color(r, g, b, alpha).endVertex();
            vertex.vertex(mat, w, 0, 0).color(r, g, b, 0f).endVertex();
            vertex.vertex(mat, w, (float) len, 0).color(r, g, b, 0f).endVertex();
            vertex.vertex(mat, 0, (float) len, 0).color(r, g, b, alpha).endVertex();

            vertex.vertex(mat, 0, (float) len, 0).color(r, g, b, alpha).endVertex();
            vertex.vertex(mat, -w, (float) len, 0).color(r, g, b, 0f).endVertex();
            vertex.vertex(mat, -w, 0, 0).color(r, g, b, 0f).endVertex();
            vertex.vertex(mat, 0, 0, 0).color(r, g, b, alpha).endVertex();


            matrices.translate(0,0,0.005f);

            vertex.vertex(mat,0,0,0f).color(1f,1f,1f,alpha).endVertex();
            vertex.vertex(mat,w * 0.15f,0,0f).color(1f,1f,1f,0f).endVertex();
            vertex.vertex(mat,w * 0.15f,(float)len,0f).color(1f,1f,1f,0f).endVertex();
            vertex.vertex(mat,0,(float)len,0f).color(1f,1f,1f,alpha).endVertex();

            vertex.vertex(mat,0,(float)len,0f).color(1f,1f,1f,alpha).endVertex();
            vertex.vertex(mat,-w * 0.15f,(float)len,0f).color(1f,1f,1f,0f).endVertex();
            vertex.vertex(mat,-w * 0.15f,0,0f).color(1f,1f,1f,0f).endVertex();
            vertex.vertex(mat,0,0,0f).color(1f,1f,1f,alpha).endVertex();

            matrices.popPose();
        }

        matrices.popPose();;

    }

    private void renderDeath(GeburahEntity geburah, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light){

        if (!geburah.isDeadOrDying()) return;

        int tick = geburah.deathTime;

        int c = 8;
        int c2 = 4;

        float angle = FDMathUtil.FPI * 2 / c;
        float angle2 = FDMathUtil.FPI * 2 / c2;

        for (int i = 0; i < c; i++) {
            this.renderDeathHammer(tick, i * 10, 130, 60, angle * i + angle / 2, geburah,3,pticks,matrices,src,light);
        }

        for (int i = 0; i < c2; i++) {
            this.renderDeathHammer(tick, i * 20, 130, 60, angle2 * i + angle2 / 2, geburah,13,pticks,matrices,src,light);
        }

    }

    private void renderDeathHammer(int currentTick, int appearTick, int strikeTick, int hammerPrepareTime, float angle, GeburahEntity geburah, float originOffset, float pticks, PoseStack matrices, MultiBufferSource src, int light){

        float tick = Mth.clamp(currentTick - appearTick + pticks,0,Integer.MAX_VALUE);

        Vec3 v = new Vec3(1,0,0).yRot(angle);

        ComplexEasingFunction hammerEasing = ComplexEasingFunction.builder()
                .addArea(hammerPrepareTime, FDEasings::easeOut)
                .addArea(strikeTick - hammerPrepareTime - appearTick, FDEasings::one)
                .addArea(7,FDEasings::reversedEaseInOut)
                .build();

        ComplexEasingFunction hammerAlpha = ComplexEasingFunction.builder()
                .addArea(hammerPrepareTime, FDEasings::easeIn)
                .build();


        float s = (float) Math.sin(tick / 10) * 0.25f;


        matrices.pushPose();

        matrices.translate(0,originOffset,0);

        Vec3 dir = v.add(0,-0.5,0);

        FDRenderUtil.applyMovementMatrixRotations(matrices,dir);

        float offset = hammerEasing.apply(tick);

        matrices.translate(0,-offset * 20 + s,0);


        matrices.mulPose(Axis.YP.rotationDegrees(offset * 360));

        matrices.scale(0.75f,0.75f,0.75f);

        float alpha = hammerAlpha.apply(tick);

        hammerModel.render(matrices,src.getBuffer(RenderType.lightning()), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0.3f,0.7f,1f,alpha * 0.75f);

        matrices.popPose();

    }


}
