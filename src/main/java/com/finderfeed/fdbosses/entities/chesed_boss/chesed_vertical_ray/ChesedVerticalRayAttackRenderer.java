package com.finderfeed.fdbosses.entities.chesed_boss.chesed_vertical_ray;

import com.finderfeed.fdbosses.client.particles.arc_lightning.ArcLightningParticle;
import com.finderfeed.fdbosses.client.util.BossRenderTypes;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.List;

public class ChesedVerticalRayAttackRenderer extends EntityRenderer<ChesedVerticalRayAttack> {

    public ChesedVerticalRayAttackRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }


    @Override
    public void render(ChesedVerticalRayAttack entity, float yaw, float partialTicks, PoseStack matrices, MultiBufferSource src, int light) {
        super.render(entity, yaw, partialTicks, matrices, src, light);

        matrices.pushPose();

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        Vec3 camPos = camera.getPosition();
        Vec3 thisPos = entity.position();
        Vec3 b = thisPos.subtract(camPos);
        double angle = Math.atan2(b.x,b.z);
        matrices.mulPose(Axis.YP.rotation(FDMathUtil.FPI + (float)angle));


        float height = entity.getHeight();
        int breaks = (int) Math.max (2,height / 4);


        VertexConsumer vertex = src.getBuffer(BossRenderTypes.LIGHTNING_NO_CULL);

        float width = 0.75f;


        matrices.pushPose();
        matrices.translate(0,0,-0.01f);

        ArcLightningParticle.fullLightningDraw(entity.level().getGameTime(),entity.getId() * 35535,
                breaks,matrices.last().pose(), vertex,
                List.of(Vec3.ZERO,Vec3.ZERO.add(0,height,0)),
                0.25f,width * 1.5f,0.3f, 0.7f, 1f,1f
        );

        matrices.popPose();

        vertex = src.getBuffer(RenderType.lightning());

        Matrix4f m = matrices.last().pose();



        vertex.addVertex(m,0,0,0f).setColor(0.3f, 1f, 1f,1);
        vertex.addVertex(m,0,height,0f).setColor(0.3f, 1f, 1f,1);
        vertex.addVertex(m,-width,height,0f).setColor(0.3f, 1f, 1f,0);
        vertex.addVertex(m,-width,0,0f).setColor(0.3f, 1f, 1f,0);


        vertex.addVertex(m,width,0,0f).setColor(0.3f, 1f, 1f,0);
        vertex.addVertex(m,width,height,0f).setColor(0.3f, 1f, 1f,0);
        vertex.addVertex(m,0,height,0f).setColor(0.3f, 1f, 1f,1);
        vertex.addVertex(m,0,0,0f).setColor(0.3f, 1f, 1f,1);

        vertex.addVertex(m,0,0,0.01f).setColor(1f, 1f, 1f,1);
        vertex.addVertex(m,0,height,0.01f).setColor(1f, 1f, 1f,1);
        vertex.addVertex(m,-width/4f,height,0.01f).setColor(1f, 1f, 1f,0);
        vertex.addVertex(m,-width/4f,0,0.01f).setColor(1f, 1f, 1f,0);


        vertex.addVertex(m,width/4f,0,0.01f).setColor(1f, 1f, 1f,0);
        vertex.addVertex(m,width/4f,height,0.01f).setColor(1f, 1f, 1f,0);
        vertex.addVertex(m,0,height,0.01f).setColor(1f, 1f, 1f,1);
        vertex.addVertex(m,0,0,0.01f).setColor(1f, 1f, 1f,1);


        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ChesedVerticalRayAttack rayAttack) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public boolean shouldRender(ChesedVerticalRayAttack p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return true;
    }


}
