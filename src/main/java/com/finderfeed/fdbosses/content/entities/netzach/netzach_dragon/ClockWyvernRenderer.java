package com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.util.BossRenderTypes;
import com.finderfeed.fdbosses.content.items.malkuth.MalkuthFistChain;
import com.finderfeed.fdlib.data_structures.SOTriple;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDFreeEntityRenderer;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.bedrock.models.FDModelPart;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.HashMap;

public class ClockWyvernRenderer implements FDFreeEntityRenderer<ClockWyvern> {

    @Override
    public void render(ClockWyvern clockWyvern, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int i) {

        FDModel model = ClockWyvern.clientModel;
        clockWyvern.getAnimationSystem().applyAnimations(model, pticks);

        VertexConsumer vertex = src.getBuffer(BossRenderTypes.ENTITY_TRANSLUCENT_TRIANGLES.apply(FDBosses.location("textures/entities/netzach/clock_wyvern_scale.png"), false));

        HashMap<String, Matrix4f> boneTransforms = new HashMap<>();

        renderTriangle(matrices, vertex, model, "wing1_end","wlev2", "wlev1", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, "wlev3","wlev2", "wlev1", i, 1, boneTransforms);

        renderTriangle(matrices, vertex, model, "wing1_end","wlev2", "wlev4", i, 1, boneTransforms);
        renderTriangle(matrices, vertex, model, "wlev3","wlev2", "wlev4", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, "wlev3","wlev4", "wlev5", i, -1, boneTransforms);

        renderTriangle(matrices, vertex, model, "wing1_end","wlev4", "wlev6", i, 1, boneTransforms);
        renderTriangle(matrices, vertex, model, "wlev5","wlev4", "wlev6", i, -1, boneTransforms);
        renderTriangle(matrices, vertex, model, "wlev7", "wlev5","wlev6", i, -1, boneTransforms);

        renderTriangle(matrices, vertex, model, "wing1_mid","wing1_end", "wlev6", i, 1, boneTransforms);
        renderTriangle(matrices, vertex, model, "wing1_mid","wlev6", "wlev7", i, 1, boneTransforms);
        renderTriangle(matrices, vertex, model, "wing1_mid","wlev7", "wlev8", i, 1, boneTransforms);
        renderTriangle(matrices, vertex, model, "wing1_start","wing1_mid", "wlev8", i, 1, boneTransforms);

    }

    private static void renderTriangle(PoseStack matrices, VertexConsumer vertex, FDModel model, String bone1, String bone2, String bone3, int i, int normalMod, HashMap<String, Matrix4f> boneTransforms){

        Vector3f part1 = boneTransforms.computeIfAbsent(bone1, (b) -> model.getModelPartTransformation(bone1)).transformPosition(new Vector3f());
        Vector3f part2 = boneTransforms.computeIfAbsent(bone2, (b) -> model.getModelPartTransformation(bone2)).transformPosition(new Vector3f());
        Vector3f part3 = boneTransforms.computeIfAbsent(bone3, (b) -> model.getModelPartTransformation(bone3)).transformPosition(new Vector3f());

        Vector3f normal = new Vector3f();
        SOTriple<Vector3f> first = projectToStablePlane(part1, part2, part3, normal);

        Matrix4f mat = matrices.last().pose();

        Vector3f uv1 = first.getLeft();
        Vector3f uv2 = first.getCenter();
        Vector3f uv3 = first.getRight();

        vertex.addVertex(mat, part1.x, part1.y, part1.z).setColor(1f,1f,1f,1f).setUv(uv1.x, uv1.z).setLight(i).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), normalMod * normal.x, normalMod * normal.y, normalMod * normal.z);
        vertex.addVertex(mat, part2.x, part2.y, part2.z).setColor(1f,1f,1f,1f).setUv(uv2.x, uv2.z).setLight(i).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), normalMod * normal.x, normalMod * normal.y, normalMod * normal.z);
        vertex.addVertex(mat, part3.x, part3.y, part3.z).setColor(1f,1f,1f,1f).setUv(uv3.x, uv3.z).setLight(i).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(matrices.last(), normalMod * normal.x, normalMod * normal.y, normalMod * normal.z);

    }

    public static SOTriple<Vector3f> projectToStablePlane(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f normalOut) {
        Vector3f uAxis = new Vector3f(p2)
                .sub(p1)
                .normalize();

        Vector3f edge2 = new Vector3f(p3).sub(p1);

        Vector3f normal = new Vector3f(uAxis)
                .cross(edge2)
                .normalize();

        normalOut.set(normal);

        Vector3f vAxis = new Vector3f(normal)
                .cross(uAxis)
                .normalize();

        Vector3f uv1 = projectPoint(p1, p1, uAxis, vAxis);
        Vector3f uv2 = projectPoint(p2, p1, uAxis, vAxis);
        Vector3f uv3 = projectPoint(p3, p1, uAxis, vAxis);

        return new SOTriple<>(uv1, uv2, uv3);
    }

    private static Vector3f projectPoint(Vector3f point, Vector3f origin, Vector3f uAxis, Vector3f vAxis) {
        Vector3f delta = new Vector3f(point).sub(origin);

        float u = delta.dot(uAxis);
        float v = delta.dot(vAxis);

        return new Vector3f(u, 0, v);
    }

}
