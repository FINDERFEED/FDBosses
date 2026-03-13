package com.finderfeed.fdbosses.content.entities.netzach.sector_attack;

import com.finderfeed.fdbosses.client.util.BossRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class SectorAttackRenderer extends EntityRenderer<SectorAttack> {

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

        matrices.pushPose();


//        matrices.translate(0,0.01,0);
        matrices.translate(offset.x,0.01,offset.z);

        Matrix4f mat = matrices.last().pose();
        for (var triangle : triangulated){
            var points = triangle.getPoints();
            var p1 = points.get(0);
            var p2 = points.get(1);
            var p3 = points.get(2);
            vertex.addVertex(mat, p1.x, p1.y, p1.z).setColor(1f,1f,1f,1f);
            vertex.addVertex(mat, p2.x, p2.y, p2.z).setColor(1f,1f,1f,1f);
            vertex.addVertex(mat, p3.x, p3.y, p3.z).setColor(1f,1f,1f,1f);
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
