package com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.BossRenderUtil;
import com.finderfeed.fdbosses.client.util.BossRenderTypes;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDFreeEntityRenderer;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.HashMap;

public class ClockWyvernRenderer implements FDFreeEntityRenderer<ClockWyvern> {

    @Override
    public void render(ClockWyvern clockWyvern, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int i) {

        FDModel model = ClockWyvern.model;
        FDModel animatedModel = ClockWyvern.animatedModel;
        clockWyvern.getAnimationSystem().applyAnimations(animatedModel, pticks);

        VertexConsumer vertex = src.getBuffer(BossRenderTypes.ENTITY_TRANSLUCENT_TRIANGLES.apply(FDBosses.location("textures/entities/netzach/clock_wyvern_scale.png"), false));

        // This is....VERY UGLYYYYYYYY. But the result is pretty :>
        WingBone wing1_end = WingBone.make("wing1_end", true);
        WingBone wlev1 = WingBone.make("wlev1", true);
        WingBone wlev2 = WingBone.make("wlev2", false);
        WingBone wlev3 = WingBone.make("wlev3", false);

        renderWingPart(matrices, vertex, animatedModel, model,
                wing1_end, wlev2, wlev1,
                wlev3, wlev2, wlev1
        );

        WingBone wlev4 = WingBone.make("wlev4", false);
        WingBone wlev5 = WingBone.make("wlev5", false);
        renderWingPart(matrices, vertex, animatedModel, model,
                wing1_end, wlev2, wlev4,
                wlev3, wlev2, wlev4,
                wlev4,wlev3, wlev5
        );

        WingBone wlev6 = WingBone.make("wlev6", false);
        WingBone wlev7 = WingBone.make("wlev7", false);
        renderWingPart(matrices, vertex, animatedModel, model,
                wing1_end, wlev4, wlev6,
                wlev5, wlev4, wlev6,
                wlev7, wlev6, wlev5
        );

        WingBone wlev9 = WingBone.make("wlev9", false);
        WingBone wing1_mid = WingBone.make("wing1_mid", false);

        renderWingPart(matrices, vertex, animatedModel, model,
                wing1_mid, wing1_end, wlev6,
                wlev6, wlev7, wlev9,
                wlev6, wing1_mid, wlev9
        );
        wlev9.fixed = false;

        WingBone wing1_start = WingBone.make("wing1_start", true);
        WingBone wlev8 = WingBone.make("wlev8", false);

        renderWingPart(matrices, vertex, animatedModel, model,
                wing1_start, wing1_mid, wlev9,
                wing1_start, wlev9, wlev8
        );



        WingBone wing1_end2 = WingBone.make("wing1_end2", true);
        WingBone wright1 = WingBone.make("wright1", true);
        WingBone wright2 = WingBone.make("wright2", false);
        WingBone wright3 = WingBone.make("wright3", false);

        renderWingPart(matrices, vertex, animatedModel, model,
                wing1_end2, wright2, wright1,
                wright3, wright2, wright1
        );

        WingBone wright4 = WingBone.make("wright4", false);
        WingBone wright5 = WingBone.make("wright5", false);
        renderWingPart(matrices, vertex, animatedModel, model,
                wright3, wright2, wright4,
                wing1_end2, wright2, wright4,
                wright3, wright4, wright5
        );

        WingBone wright6 = WingBone.make("wright6", false);
        WingBone wright7 = WingBone.make("wright7", false);
        renderWingPart(matrices, vertex, animatedModel, model,
                wright6, wright5, wright4,
                wing1_end2, wright4, wright6,
                wright7, wright5, wright6
        );

        WingBone wing1_mid2 = WingBone.make("wing1_mid2", false);
        WingBone wright9 = WingBone.make("wright9", false);
        renderWingPart(matrices, vertex, animatedModel, model,
                wright7, wright6, wright9,
                wright9, wright6, wing1_mid2,
                wing1_mid2, wing1_end2, wright6
        );
        wright9.fixed = false;

        WingBone wing1_start2 = WingBone.make("wing1_start2", true);
        WingBone wright8 = WingBone.make("wright8", false);
        renderWingPart(matrices, vertex, animatedModel, model,
                wing1_start2, wing1_mid2, wright9,
                wing1_start2, wright9, wright8
        );
//        renderTriangle(matrices, vertex, model, animatedModel, "wing1_start2","wing1_mid2", "wright9", i, -1, boneTransforms);
//        renderTriangle(matrices, vertex, model, animatedModel, "wing1_start2","wright9", "wright8", i, -1, boneTransforms);


    }

    private static void renderWingPart(PoseStack matrices, VertexConsumer vertex, FDModel animatedModel, FDModel staticModel, WingBone... wingBones){

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

            vertex.addVertex(mat, globalPos1.x, globalPos1.y, globalPos1.z).setColor(1f,1f,1f,1f).setUv(uv1.x, uv1.y).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(nrm.x, nrm.y, nrm.z);
            vertex.addVertex(mat, globalPos2.x, globalPos2.y, globalPos2.z).setColor(1f,1f,1f,1f).setUv(uv2.x, uv2.y).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(nrm.x, nrm.y, nrm.z);
            vertex.addVertex(mat, globalPos3.x, globalPos3.y, globalPos3.z).setColor(1f,1f,1f,1f).setUv(uv3.x, uv3.y).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(nrm.x, nrm.y, nrm.z);

            vertex.addVertex(mat, globalPos3.x, globalPos3.y, globalPos3.z).setColor(1f,1f,1f,1f).setUv(uv3.x, uv3.y).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(-nrm.x, -nrm.y, -nrm.z);
            vertex.addVertex(mat, globalPos2.x, globalPos2.y, globalPos2.z).setColor(1f,1f,1f,1f).setUv(uv2.x, uv2.y).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(-nrm.x, -nrm.y, -nrm.z);
            vertex.addVertex(mat, globalPos1.x, globalPos1.y, globalPos1.z).setColor(1f,1f,1f,1f).setUv(uv1.x, uv1.y).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(-nrm.x, -nrm.y, -nrm.z);
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
