package com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.util.BossRenderTypes;
import com.finderfeed.fdlib.data_structures.Pair;
import com.finderfeed.fdlib.data_structures.SOTriple;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDFreeEntityRenderer;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

        HashMap<String, Matrix4f> boneTransforms = new HashMap<>();

        renderTriangle(matrices, vertex, model, animatedModel, "wing1_end","wlev2", "wlev1", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, animatedModel, "wlev3","wlev2", "wlev1", i, -1, boneTransforms);

        renderTriangle(matrices, vertex, model, animatedModel, "wing1_end","wlev2", "wlev4", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, animatedModel, "wlev3","wlev2", "wlev4", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, animatedModel, "wlev3","wlev4", "wlev5", i, -1, boneTransforms);

        renderTriangle(matrices, vertex, model, animatedModel, "wing1_end","wlev4", "wlev6", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, animatedModel, "wlev5","wlev4", "wlev6", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, animatedModel, "wlev7", "wlev5","wlev6", i, -1, boneTransforms);

        renderTriangle(matrices, vertex, model, animatedModel, "wing1_mid","wing1_end", "wlev6", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, animatedModel, "wlev6","wing1_mid", "wlev9", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, animatedModel, "wlev6","wlev7", "wlev9", i, -1, boneTransforms);

        renderTriangle(matrices, vertex, model, animatedModel, "wing1_start","wing1_mid", "wlev9", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, animatedModel, "wing1_start","wlev9", "wlev8", i, -1, boneTransforms);







        renderTriangle(matrices, vertex, model, animatedModel, "wing1_end2","wright2", "wright1", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, animatedModel, "wright3","wright2", "wright1", i, -1, boneTransforms);

        renderTriangle(matrices, vertex, model, animatedModel, "wing1_end2","wright2", "wright4", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, animatedModel, "wright3","wright2", "wright4", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, animatedModel, "wright3","wright4", "wright5", i, -1, boneTransforms);

        renderTriangle(matrices, vertex, model, animatedModel, "wing1_end2","wright4", "wright6", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, animatedModel, "wright5","wright4", "wright6", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, animatedModel, "wright7", "wright5","wright6", i, -1, boneTransforms);

        renderTriangle(matrices, vertex, model, animatedModel, "wing1_mid2","wing1_end2", "wright6", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, animatedModel, "wright6","wing1_mid2", "wright9", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, animatedModel, "wright6","wright7", "wright9", i, -1, boneTransforms);

        renderTriangle(matrices, vertex, model, animatedModel, "wing1_start2","wing1_mid2", "wright9", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, animatedModel, "wing1_start2","wright9", "wright8", i, -1, boneTransforms);


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
