package com.finderfeed.fdbosses.content.entities.netzach.sector_attack;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.util.BossRenderTypes;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.renderers.QuadRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Random;

public class SectorAttackRenderer extends EntityRenderer<SectorAttack> {

    //16 97
    public static final ResourceLocation CLOCK_ARROW = FDBosses.location("textures/entities/netzach/clockarrow.png");

    public static final ComplexEasingFunction ALPHA = ComplexEasingFunction.builder()
                        .addArea(SectorAttack.ATTACK_TIME, FDEasings::easeOut)
                        .addArea(20, FDEasings::one)
                        .addArea(20, FDEasings::reversedEaseOut)
                        .build();

    public SectorAttackRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public void render(SectorAttack entity, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int p_114490_) {
        var triangulated = entity.triangulatedForRendering;
        if (triangulated.isEmpty()) return;
        var owner = entity.getOwner();
        if (Minecraft.getInstance().player != owner) return;

        var vertex = src.getBuffer(BossRenderTypes.LIGHTNING_NO_CULL_TRIANGLES);

        Vec3 ownerPos = owner.getPosition(pticks);
        Vec3 thisPos = entity.getPosition(pticks);
        Vec3 offset = ownerPos.subtract(thisPos);

        if (!entity.isFollowingOwner()){
            var p = entity.getOwnerPos();
            if (p != null){
                offset = p.subtract(thisPos);
            }
        }

        matrices.pushPose();

        matrices.translate(offset.x,0.01,offset.z);

        if (entity.isFollowingOwner()) {
            Matrix4f mat = matrices.last().pose();
            for (var triangle : triangulated) {
                var points = triangle.getPoints();
                var p1 = points.get(0);
                var p2 = points.get(1);
                var p3 = points.get(2);
                vertex.addVertex(mat, p1.x, p1.y, p1.z).setColor(0.743f, 0.478f, 0.239f, 0.6f);
                vertex.addVertex(mat, p2.x, p2.y, p2.z).setColor(0.743f, 0.478f, 0.239f, 0.6f);
                vertex.addVertex(mat, p3.x, p3.y, p3.z).setColor(0.743f, 0.478f, 0.239f, 0.6f);
            }
        }

        var shape = entity.getAttackShape();

//        vertex.addVertex(mat, shape.getMinBoundX(),0.005f, shape.getMinBoundZ()).setColor(1f,1f,1f,0.1f);
//        vertex.addVertex(mat, shape.getMaxBoundX(),0.005f, shape.getMinBoundZ()).setColor(1f,1f,1f,0.1f);
//        vertex.addVertex(mat, shape.getMaxBoundX(),0.005f, shape.getMaxBoundZ()).setColor(1f,1f,1f,0.1f);
//
//        vertex.addVertex(mat, shape.getMinBoundX(),0.005f, shape.getMinBoundZ()).setColor(1f,1f,1f,0.1f);
//        vertex.addVertex(mat, shape.getMinBoundX(),0.005f, shape.getMaxBoundZ()).setColor(1f,1f,1f,0.1f);
//        vertex.addVertex(mat, shape.getMaxBoundX(),0.005f, shape.getMaxBoundZ()).setColor(1f,1f,1f,0.1f);

        if (!entity.isFollowingOwner()) {

            var offsets = entity.getAttackVisualOffsets();
            vertex = src.getBuffer(RenderType.text(CLOCK_ARROW));
            float xSize = 0.75f;
            float ySize = xSize / 0.1649484f;

            int i = 0;
            for (var offs : offsets) {
                matrices.pushPose();


                Random random = entity.generateRandomForAttackVisual(i);

                int startTime = entity.getAttackDelayForAttackVisual(random);







                float time = entity.ticksAfterAttack + pticks - startTime;
                float alphaVal = ALPHA.apply(time);
                float offsetY = Mth.clamp(FDEasings.easeIn(1 - Mth.clamp(time / SectorAttack.ATTACK_TIME,0,1)), 0, 1) * 10;

                float shake = 0f;
                int shakeTime = 4;
                if (time >= SectorAttack.ATTACK_TIME && time <= shakeTime){
                    float p = FDEasings.squareHill(time / shakeTime);
                    shake = (float) (Math.sin(time * 20) * 5f * p);
                }

                matrices.translate(offs.x, 0, offs.y);

                matrices.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360));
                matrices.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 20f + shake));

                matrices.translate(0,ySize - 1 + offsetY,0);


                QuadRenderer.start(vertex)
                        .pose(matrices)
                        .verticalRendering()
                        .sizeX(xSize)
                        .sizeY(ySize)
                        .rotationDegrees(random.nextFloat() * 360)
                        .renderBack()
                        .color(1f, 1f, 1f, alphaVal)
                        .render();
                matrices.popPose();
                i++;
            }







        }




        matrices.popPose();




    }

    @Override
    public ResourceLocation getTextureLocation(SectorAttack p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public boolean shouldRender(SectorAttack p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return true;
    }

}
