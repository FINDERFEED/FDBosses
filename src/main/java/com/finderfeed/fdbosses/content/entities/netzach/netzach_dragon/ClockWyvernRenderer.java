package com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.BossRenderUtil;
import com.finderfeed.fdbosses.client.util.BossRenderTypes;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDFreeEntityRenderer;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
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
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.Math;
import java.util.HashMap;

public class ClockWyvernRenderer implements FDFreeEntityRenderer<ClockWyvern> {

    public static Vector3f BALL_OFFSET = new Vector3f(0,-0.6f,1.4f);

    public static ResourceLocation WYVERN_FIREBALL = FDBosses.location("textures/entities/netzach/clock_wyvern_fireball.png");

    @Override
    public void render(ClockWyvern clockWyvern, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {

        FDModel model = ClockWyvern.model;
        FDModel animatedModel = ClockWyvern.animatedModel;
        clockWyvern.getAnimationSystem().applyAnimations(animatedModel, pticks);

        VertexConsumer vertex = src.getBuffer(BossRenderTypes.ENTITY_TRANSLUCENT_TRIANGLES.apply(FDBosses.location("textures/entities/netzach/clock_wyvern_scale.png"), false));

        matrices.pushPose();

        matrices.mulPose(Axis.YP.rotationDegrees(-yaw));

        // This is....VERY UGLYYYYYYYY. But the result is pretty :>
        WingBone wing1_end = WingBone.make("wing1_end", true);
        WingBone wlev1 = WingBone.make("wlev1", true);
        WingBone wlev2 = WingBone.make("wlev2", false);
        WingBone wlev3 = WingBone.make("wlev3", false);

        renderWingPart(matrices, vertex, animatedModel, model, light,
                wing1_end, wlev2, wlev1,
                wlev3, wlev2, wlev1
        );

        WingBone wlev4 = WingBone.make("wlev4", false);
        WingBone wlev5 = WingBone.make("wlev5", false);
        renderWingPart(matrices, vertex, animatedModel, model, light,
                wing1_end, wlev2, wlev4,
                wlev3, wlev2, wlev4,
                wlev4,wlev3, wlev5
        );

        WingBone wlev6 = WingBone.make("wlev6", false);
        WingBone wlev7 = WingBone.make("wlev7", false);
        renderWingPart(matrices, vertex, animatedModel, model, light,
                wing1_end, wlev4, wlev6,
                wlev5, wlev4, wlev6,
                wlev7, wlev6, wlev5
        );

        WingBone wlev9 = WingBone.make("wlev9", false);
        WingBone wing1_mid = WingBone.make("wing1_mid", false);

        renderWingPart(matrices, vertex, animatedModel, model, light,
                wing1_mid, wing1_end, wlev6,
                wlev6, wlev7, wlev9,
                wlev6, wing1_mid, wlev9
        );
        wlev9.fixed = false;

        WingBone wing1_start = WingBone.make("wing1_start", true);
        WingBone wlev8 = WingBone.make("wlev8", false);

        renderWingPart(matrices, vertex, animatedModel, model, light,
                wing1_start, wing1_mid, wlev9,
                wing1_start, wlev9, wlev8
        );

        WingBone wing1_end2 = WingBone.make("wing1_end2", true);
        WingBone wright1 = WingBone.make("wright1", true);
        WingBone wright2 = WingBone.make("wright2", false);
        WingBone wright3 = WingBone.make("wright3", false);

        renderWingPart(matrices, vertex, animatedModel, model, light,
                wing1_end2, wright2, wright1,
                wright3, wright2, wright1
        );

        WingBone wright4 = WingBone.make("wright4", false);
        WingBone wright5 = WingBone.make("wright5", false);
        renderWingPart(matrices, vertex, animatedModel, model, light,
                wright3, wright2, wright4,
                wing1_end2, wright2, wright4,
                wright3, wright4, wright5
        );

        WingBone wright6 = WingBone.make("wright6", false);
        WingBone wright7 = WingBone.make("wright7", false);
        renderWingPart(matrices, vertex, animatedModel, model, light,
                wright6, wright5, wright4,
                wing1_end2, wright4, wright6,
                wright7, wright5, wright6
        );

        WingBone wing1_mid2 = WingBone.make("wing1_mid2", false);
        WingBone wright9 = WingBone.make("wright9", false);
        renderWingPart(matrices, vertex, animatedModel, model, light,
                wright7, wright6, wright9,
                wright9, wright6, wing1_mid2,
                wing1_mid2, wing1_end2, wright6
        );
        wright9.fixed = false;

        WingBone wing1_start2 = WingBone.make("wing1_start2", true);
        WingBone wright8 = WingBone.make("wright8", false);
        renderWingPart(matrices, vertex, animatedModel, model, light,
                wing1_start2, wing1_mid2, wright9,
                wing1_start2, wright9, wright8
        );

        vertex = src.getBuffer(BossRenderTypes.entityTranslucentCull(FDBosses.location("textures/entities/netzach/clock_wyvern_scale_end.png")));
        renderWingEnds(matrices, vertex, animatedModel, model, light, "wright3", "wright1", "wing1_end2");
        renderWingEnds(matrices, vertex, animatedModel, model, light, "wright5", "wright3", "wing1_end2");
        renderWingEnds(matrices, vertex, animatedModel, model, light, "wright7", "wright5", "wing1_end2");
        renderWingEnds(matrices, vertex, animatedModel, model, light, "wright9", "wright7", "wing1_end2");
        renderWingEnds(matrices, vertex, animatedModel, model, light, "wright8", "wright9", "wing1_start2");

        matrices.popPose();

        timedRayAttack(clockWyvern, matrices, src, animatedModel, pticks, yaw, light);
    }

    private static final ComplexEasingFunction WYVERN_RAY_SIZE_MOD = ComplexEasingFunction.builder()
            .addArea(121, FDEasings::zero)
            .addArea(3, FDEasings::easeIn)
            .addArea(10, FDEasings::one)
            .addArea(8, FDEasings::reversedEaseOut)
            .build();

    private static final ComplexEasingFunction WYVERN_CUBE_SIZE_MOD = ComplexEasingFunction.builder()
            .addArea(20, FDEasings::zero)
            .addArea(100, FDEasings::easeOut)
            .addArea(20, FDEasings::one)
            .addArea(5, FDEasings::reversedLinear)
            .build();

    private static void timedRayAttack(ClockWyvern wyvern, PoseStack matrices, MultiBufferSource src, FDModel animatedModel, float pticks, float yaw, int light){

        var ticker = wyvern.getAnimationSystem().getTicker(ClockWyvern.MAIN);
        if (ticker == null) return;

        var anim = ticker.getAnimation();

        if (anim == BossAnims.CLOCK_WYVERN_TIMED_RAY_ATTACK.get()){

            float tickerTime = ticker.getTime(pticks);
            float cubeSizeMod = WYVERN_CUBE_SIZE_MOD.apply(tickerTime);

            Matrix4f mat = animatedModel.getModelPartTransformation("head");
            matrices.pushPose();

            matrices.mulPose(Axis.YN.rotationDegrees(yaw));

            Vector3f pos = mat.transformPosition(new Vector3f(BALL_OFFSET));
            matrices.translate(pos.x, pos.y, pos.z);

            VertexConsumer vertex = src.getBuffer(RenderType.lightning());
            float rotation = (float) Math.max(0, tickerTime - 20 + pticks) * 60;

            matrices.mulPose(Axis.YP.rotationDegrees(rotation));
            matrices.mulPose(Axis.XP.rotationDegrees(rotation));

            float baseCubeSize = 0.85f;

            renderCube(matrices, vertex, (baseCubeSize - 0.25f) * cubeSizeMod, 1f, 1f, 1f, 1f, 1, false);
            renderCube(matrices, vertex, (baseCubeSize - 0.15f) * cubeSizeMod, 0.8f, 0.6f, 0.2f, 1f, 1, false);

            vertex = src.getBuffer(RenderType.text(WYVERN_FIREBALL));
            renderCube(matrices, vertex, baseCubeSize * cubeSizeMod, 1f, 1f, 1f, 1f, 1, true);


            matrices.popPose();

            float raySizeMod = WYVERN_RAY_SIZE_MOD.apply(tickerTime);

            renderRay(wyvern, matrices, src, mat, pticks, yaw, 0.5f * raySizeMod);
        }
    }

    public static void renderRay(ClockWyvern clockWyvern, PoseStack matrices, MultiBufferSource src, Matrix4f headTransformation, float pticks, float yaw, float raySize){

        Vector3f headPos = headTransformation.transformPosition(new Vector3f(BALL_OFFSET));

        Vec3 target = clockWyvern.getWyvernRayTarget();
        Vec3 wyvernPos = clockWyvern.getPosition(pticks);
        Vec3 betweenTargetAndWyvern = target.subtract(wyvernPos);

        Matrix4f headPosTransform = new Matrix4f();
        headPosTransform.rotateY(-(float) Math.toRadians(yaw));
        headPosTransform.translate(headPos.x, headPos.y, headPos.z);

        Vector3f realHeadPos = headPosTransform.transformPosition(new Vector3f());
        Vec3 betweenTargetAndHead = betweenTargetAndWyvern.subtract(
                realHeadPos.x, realHeadPos.y, realHeadPos.z
        );
        float len = (float) betweenTargetAndHead.length() + 1;

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        matrices.pushPose();

        matrices.translate(realHeadPos.x, realHeadPos.y, realHeadPos.z);
        BossRenderUtil.rotateRay(matrices, camera.getPosition().subtract(
                wyvernPos.x + realHeadPos.x,
                wyvernPos.y + realHeadPos.y,
                wyvernPos.z + realHeadPos.z
        ), betweenTargetAndHead);

        VertexConsumer vertex = src.getBuffer(RenderType.lightning());

        float trembleStaticPercent = 0.5f;
        float time = clockWyvern.tickCount + pticks;
        float tremblingRay = ((float) Math.sin(time * 8) + 1) * 0.5f * raySize * (1 - trembleStaticPercent) + trembleStaticPercent * raySize;

        float finalRaySize = tremblingRay;
        float finalWhiteRaySize = tremblingRay * 0.5f;

        float rayColorR = 0.8f;
        float rayColorG = 0.6f;
        float rayColorB = 0.2f;
        float rayColorA = 0.8f;

        Matrix4f m = matrices.last().pose();
        vertex.addVertex(m, finalRaySize, 0, 0).setColor(rayColorR,rayColorG,rayColorB,rayColorA);
        vertex.addVertex(m, finalRaySize, len, 0).setColor(rayColorR,rayColorG,rayColorB,rayColorA);
        vertex.addVertex(m, -finalRaySize, len, 0).setColor(rayColorR,rayColorG,rayColorB,rayColorA);
        vertex.addVertex(m, -finalRaySize, 0, 0).setColor(rayColorR,rayColorG,rayColorB,rayColorA);

        vertex.addVertex(m, finalWhiteRaySize, 0, 0.01f).setColor(1f,1f,1f,rayColorA);
        vertex.addVertex(m, finalWhiteRaySize, len, 0.01f).setColor(1f,1f,1f,rayColorA);
        vertex.addVertex(m, -finalWhiteRaySize, len, 0.01f).setColor(1f,1f,1f,rayColorA);
        vertex.addVertex(m, -finalWhiteRaySize, 0, 0.01f).setColor(1f,1f,1f,rayColorA);
        matrices.popPose();
    }

    public static void renderCube(PoseStack matrices, VertexConsumer vertex, float radius, float r, float g, float b, float a, int timesRendered, boolean renderBack){
        matrices.pushPose();

        float half = radius / 2;

        for (int i = 0; i < 4; i++){
            matrices.pushPose();

            matrices.mulPose(Axis.YP.rotationDegrees(i * 90));

            Matrix4f mat = matrices.last().pose();

            for(int k = 0; k < timesRendered; k++) {
                vertex.addVertex(mat, -half, -half, -half).setColor(r, g, b, a).setUv(0,0).setLight(LightTexture.FULL_BRIGHT);
                vertex.addVertex(mat, -half, -half, half).setColor(r, g, b, a).setUv(0,1).setLight(LightTexture.FULL_BRIGHT);
                vertex.addVertex(mat, -half, half, half).setColor(r, g, b, a).setUv(1,1).setLight(LightTexture.FULL_BRIGHT);
                vertex.addVertex(mat, -half, half, -half).setColor(r, g, b, a).setUv(1,0).setLight(LightTexture.FULL_BRIGHT);
                if (renderBack){
                    vertex.addVertex(mat, -half, half, -half).setColor(r, g, b, a).setUv(1,0).setLight(LightTexture.FULL_BRIGHT);
                    vertex.addVertex(mat, -half, half, half).setColor(r, g, b, a).setUv(1,1).setLight(LightTexture.FULL_BRIGHT);
                    vertex.addVertex(mat, -half, -half, half).setColor(r, g, b, a).setUv(0,1).setLight(LightTexture.FULL_BRIGHT);
                    vertex.addVertex(mat, -half, -half, -half).setColor(r, g, b, a).setUv(0,0).setLight(LightTexture.FULL_BRIGHT);
                }
            }

            matrices.popPose();
        }


        Matrix4f mat = matrices.last().pose();
        for (int k = 0; k < timesRendered;k++){
            vertex.addVertex(mat, -half, -half, -half).setColor(r,g,b,a).setUv(0,0).setLight(LightTexture.FULL_BRIGHT);
            vertex.addVertex(mat, half, -half, -half).setColor(r,g,b,a).setUv(0,1).setLight(LightTexture.FULL_BRIGHT);
            vertex.addVertex(mat, half, -half, half).setColor(r,g,b,a).setUv(1,1).setLight(LightTexture.FULL_BRIGHT);
            vertex.addVertex(mat, -half, -half, half).setColor(r,g,b,a).setUv(1,0).setLight(LightTexture.FULL_BRIGHT);

            vertex.addVertex(mat, -half, half, half).setColor(r,g,b,a).setUv(0,0).setLight(LightTexture.FULL_BRIGHT);
            vertex.addVertex(mat, half, half, half).setColor(r,g,b,a).setUv(0,1).setLight(LightTexture.FULL_BRIGHT);
            vertex.addVertex(mat, half, half, -half).setColor(r,g,b,a).setUv(1,1).setLight(LightTexture.FULL_BRIGHT);
            vertex.addVertex(mat, -half, half, -half).setColor(r,g,b,a).setUv(1,0).setLight(LightTexture.FULL_BRIGHT);

            if (renderBack){
                vertex.addVertex(mat, -half, -half, half).setColor(r,g,b,a).setUv(1,0).setLight(LightTexture.FULL_BRIGHT);
                vertex.addVertex(mat, half, -half, half).setColor(r,g,b,a).setUv(1,1).setLight(LightTexture.FULL_BRIGHT);
                vertex.addVertex(mat, half, -half, -half).setColor(r,g,b,a).setUv(0,1).setLight(LightTexture.FULL_BRIGHT);
                vertex.addVertex(mat, -half, -half, -half).setColor(r,g,b,a).setUv(0,0).setLight(LightTexture.FULL_BRIGHT);

                vertex.addVertex(mat, -half, half, -half).setColor(r,g,b,a).setUv(1,0).setLight(LightTexture.FULL_BRIGHT);
                vertex.addVertex(mat, half, half, -half).setColor(r,g,b,a).setUv(1,1).setLight(LightTexture.FULL_BRIGHT);
                vertex.addVertex(mat, half, half, half).setColor(r,g,b,a).setUv(0,1).setLight(LightTexture.FULL_BRIGHT);
                vertex.addVertex(mat, -half, half, half).setColor(r,g,b,a).setUv(0,0).setLight(LightTexture.FULL_BRIGHT);
            }
        }

        matrices.popPose();
    }

    public static void renderWingEnds(PoseStack matrices, VertexConsumer vertex, FDModel animatedModel, FDModel staticModel, int light, String bone1, String bone2, String supportingBone){
        Vector3f bone1pos = animatedModel.getModelPartTransformation(bone1).transformPosition(new Vector3f());
        Vector3f bone2pos = animatedModel.getModelPartTransformation(bone2).transformPosition(new Vector3f());
        Vector3f supportingBonePos = animatedModel.getModelPartTransformation(supportingBone).transformPosition(new Vector3f());

        Vector3f normal = BossRenderUtil.planeNormal(bone1pos, bone2pos, supportingBonePos);

        Vector3f between = bone2pos.sub(bone1pos, new Vector3f());
        Quaternionf quaternionf = new Quaternionf().rotateAxis(-FDMathUtil.FPI / 2, normal);
        Vector3f center = bone1pos.add(between.mul(0.5f, new Vector3f()), new Vector3f());

        float length = between.length();

        between.normalize();

        Vector3f rbetween = between.rotate(quaternionf, new Vector3f());


        float md = 2;
        Matrix4f mat = matrices.last().pose();

        float textureHalfLength = 1;
        float realLength = Math.min(textureHalfLength, length / 2);

        float pos1X = center.x - between.x * realLength;
        float pos1Y = center.y - between.y * realLength;
        float pos1Z = center.z - between.z * realLength;

        float pos2X = center.x + between.x * realLength;
        float pos2Y = center.y + between.y * realLength;
        float pos2Z = center.z + between.z * realLength;

        float u1 = 0.5f - realLength / textureHalfLength * 0.5f;
        float u2 = 0.5f + realLength / textureHalfLength * 0.5f;

        //TODO: Fix triangles bug
        vertex.addVertex(mat, pos1X + rbetween.x * md, pos1Y + rbetween.y * md, pos1Z + rbetween.z * md).setColor(1f, 1f, 1f, 1f).setUv(u1,0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), normal.x, normal.y, normal.z);
        vertex.addVertex(mat, pos2Y + rbetween.x * md, pos2Y + rbetween.y * md, pos2Z + rbetween.z * md).setColor(1f, 1f, 1f, 1f).setUv(u2,0).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), normal.x, normal.y, normal.z);
        vertex.addVertex(mat, pos2X, pos2Y, pos2Z).setColor(1f, 1f, 1f, 1f).setUv(u2,1).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), normal.x, normal.y, normal.z);
        vertex.addVertex(mat, pos1X, pos1Y, pos1Z).setColor(1f, 1f, 1f, 1f).setUv(u1, 1).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), normal.x, normal.y, normal.z);
    }

    private static void renderWingPart(PoseStack matrices, VertexConsumer vertex, FDModel animatedModel, FDModel staticModel, int light, WingBone... wingBones){

        if (wingBones.length % 3 != 0) {
            System.out.println("Not enough wing bones! Everything is in triangles!");
        }

        HashMap<WingBone, Vector3f> globalPositions = new HashMap<>();
        HashMap<WingBone, Vector2f> uv = new HashMap<>();

        Matrix4f mat = matrices.last().pose();

        for (int i = 0; i < wingBones.length; i += 3){
            WingBone b1 = wingBones[i];
            WingBone b2 = wingBones[i + 1];
            WingBone b3 = wingBones[i + 2];

            if (b1.fixed){
                putIfAbsent(b1, animatedModel, staticModel, globalPositions, uv);
            }
            if (b2.fixed){
                putIfAbsent(b2, animatedModel, staticModel, globalPositions, uv);
            }
            if (b3.fixed){
                putIfAbsent(b3, animatedModel, staticModel, globalPositions, uv);
            }

            int s = (b1.fixed ? 1 : 0) + (b2.fixed ? 1 : 0) + (b3.fixed ? 1 : 0);
            if (s != 2){
                continue;
            }

            WingBone fixed1;
            WingBone fixed2;
            WingBone unfixed;

            if (b1.fixed && b2.fixed){
                fixed1 = b1;
                fixed2 = b2;
                unfixed = b3;
            }else if (b1.fixed){
                fixed1 = b1;
                fixed2 = b3;
                unfixed = b2;
            }else{
                fixed1 = b2;
                fixed2 = b3;
                unfixed = b1;
            }

            putIfAbsent(unfixed, animatedModel, null, globalPositions, null);

            Vector3f globalPos1 = globalPositions.get(fixed1);
            Vector3f globalPos2 = globalPositions.get(fixed2);
            Vector3f globalPos3 = globalPositions.get(unfixed);
            Vector2f uv1 = uv.get(fixed1);
            Vector2f uv2 = uv.get(fixed2);
            Vector3f uv3 = findThirdPointXZ(globalPos1, globalPos2, globalPos3, uv1, uv2, true);
            uv.put(unfixed, new Vector2f(uv3.x, uv3.z));
            unfixed.fixed = true;
            i = 0;
        }

        for (int i = 0; i < wingBones.length; i += 3){
            WingBone b1 = wingBones[i];
            WingBone b2 = wingBones[i + 1];
            WingBone b3 = wingBones[i + 2];
            Vector3f globalPos1 = globalPositions.get(b1);
            Vector3f globalPos2 = globalPositions.get(b2);
            Vector3f globalPos3 = globalPositions.get(b3);
            Vector2f uv1 = uv.get(b1);
            Vector2f uv2 = uv.get(b2);
            Vector2f uv3 = uv.get(b3);

            Vector3f nrm = BossRenderUtil.planeNormal(globalPos1, globalPos2, globalPos3);

            vertex.addVertex(mat, globalPos1.x, globalPos1.y, globalPos1.z).setColor(1f,1f,1f,1f).setUv(uv1.x, uv1.y).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), nrm.x, nrm.y, nrm.z);
            vertex.addVertex(mat, globalPos2.x, globalPos2.y, globalPos2.z).setColor(1f,1f,1f,1f).setUv(uv2.x, uv2.y).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), nrm.x, nrm.y, nrm.z);
            vertex.addVertex(mat, globalPos3.x, globalPos3.y, globalPos3.z).setColor(1f,1f,1f,1f).setUv(uv3.x, uv3.y).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), nrm.x, nrm.y, nrm.z);

            vertex.addVertex(mat, globalPos3.x, globalPos3.y, globalPos3.z).setColor(1f,1f,1f,1f).setUv(uv3.x, uv3.y).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), -nrm.x, -nrm.y, -nrm.z);
            vertex.addVertex(mat, globalPos2.x, globalPos2.y, globalPos2.z).setColor(1f,1f,1f,1f).setUv(uv2.x, uv2.y).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), -nrm.x, -nrm.y, -nrm.z);
            vertex.addVertex(mat, globalPos1.x, globalPos1.y, globalPos1.z).setColor(1f,1f,1f,1f).setUv(uv1.x, uv1.y).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), -nrm.x, -nrm.y, -nrm.z);
        }
    }

    private static void putIfAbsent(WingBone fixed, FDModel animatedModel, FDModel staticModel, HashMap<WingBone, Vector3f> globalPositions, HashMap<WingBone, Vector2f> uv){
        if (!globalPositions.containsKey(fixed)){
            Matrix4f transformAnimated = animatedModel.getModelPartTransformation(fixed.boneName);
            Vector3f globalPos = transformAnimated.transformPosition(new Vector3f());
            globalPositions.put(fixed, globalPos);

            if (staticModel != null){
                Matrix4f transformStatic = staticModel.getModelPartTransformation(fixed.boneName);
                Vector3f staticPos = transformStatic.transformPosition(new Vector3f());
                uv.put(fixed, new Vector2f(staticPos.x, staticPos.z));
            }
        }
    }



    private static final class WingBone {


        private String boneName;

        private boolean fixed;

        private static WingBone make(String bone, boolean fixed){
            WingBone wingPart = new WingBone();
            wingPart.boneName = bone;
            wingPart.fixed = fixed;
            return wingPart;
        }



    }

    public static Vector3f findThirdPointXZ(Vector3f a, Vector3f b, Vector3f c, Vector2f newA, Vector2f newB, boolean positiveY) {
        float ux = b.x - a.x, uy = b.y - a.y, uz = b.z - a.z;
        float vx = c.x - a.x, vy = c.y - a.y, vz = c.z - a.z;

        float dx = newB.x - newA.x, dz = newB.y - newA.y;

        float dot = ux * vx + uy * vy + uz * vz;
        float uu = ux * ux + uy * uy + uz * uz;
        float vv = vx * vx + vy * vy + vz * vz;

        float h = (float) Math.sqrt(Math.max(0f, uu * vv - dot * dot));
        if (!positiveY) h = -h;

        float inv = 1f / (dx * dx + dz * dz);

        return new Vector3f(
                newA.x + (dx * dot + dz * h) * inv,
                0f,
                newA.y + (dz * dot - dx * h) * inv
        );
    }

    private static void renderTriangle(PoseStack matrices, VertexConsumer vertex, FDModel model, FDModel animatedModel, String bone1, String bone2, String bone3, int i, int normalMod, HashMap<String, Matrix4f> boneTransforms){

        Vector3f part1 = boneTransforms.computeIfAbsent(bone1, (b) -> animatedModel.getModelPartTransformation(bone1)).transformPosition(new Vector3f());
        Vector3f part2 = boneTransforms.computeIfAbsent(bone2, (b) -> animatedModel.getModelPartTransformation(bone2)).transformPosition(new Vector3f());
        Vector3f part3 = boneTransforms.computeIfAbsent(bone3, (b) -> animatedModel.getModelPartTransformation(bone3)).transformPosition(new Vector3f());

        Vector3f part1NonTransformed = boneTransforms.computeIfAbsent("non_transformed" + bone1, (b) -> model.getModelPartTransformation(bone1)).transformPosition(new Vector3f());
        Vector3f part2NonTransformed = boneTransforms.computeIfAbsent("non_transformed" + bone2, (b) -> model.getModelPartTransformation(bone2)).transformPosition(new Vector3f());
        Vector3f part3NonTransformed = boneTransforms.computeIfAbsent("non_transformed" + bone3, (b) -> model.getModelPartTransformation(bone3)).transformPosition(new Vector3f());

        Vector3f b1 = part1.sub(part3, new Vector3f());
        Vector3f b2 = part1.sub(part2, new Vector3f());
        Vector3f normal = b1.cross(b2).normalize();

        Matrix4f mat = matrices.last().pose();

        Vector3f uv1 = part1NonTransformed;
        Vector3f uv2 = part2NonTransformed;
        Vector3f uv3 = part3NonTransformed;

        vertex.addVertex(mat, part1.x, part1.y, part1.z).setColor(1f,1f,1f,1f).setUv(uv1.x, uv1.z).setLight(i).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), normalMod * normal.x, normalMod * normal.y, normalMod * normal.z);
        vertex.addVertex(mat, part2.x, part2.y, part2.z).setColor(1f,1f,1f,1f).setUv(uv2.x, uv2.z).setLight(i).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), normalMod * normal.x, normalMod * normal.y, normalMod * normal.z);
        vertex.addVertex(mat, part3.x, part3.y, part3.z).setColor(1f,1f,1f,1f).setUv(uv3.x, uv3.z).setLight(i).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), normalMod * normal.x, normalMod * normal.y, normalMod * normal.z);

        vertex.addVertex(mat, part3.x, part3.y, part3.z).setColor(1f,1f,1f,1f).setUv(uv3.x, uv3.z).setLight(i).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), -normalMod * normal.x, -normalMod * normal.y, -normalMod * normal.z);
        vertex.addVertex(mat, part2.x, part2.y, part2.z).setColor(1f,1f,1f,1f).setUv(uv2.x, uv2.z).setLight(i).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), -normalMod * normal.x, -normalMod * normal.y, -normalMod * normal.z);
        vertex.addVertex(mat, part1.x, part1.y, part1.z).setColor(1f,1f,1f,1f).setUv(uv1.x, uv1.z).setLight(i).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), -normalMod * normal.x, -normalMod * normal.y, -normalMod * normal.z);

    }

}
