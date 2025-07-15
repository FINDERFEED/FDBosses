package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_fireball;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdlib.systems.shapes.FD2DShape;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.renderers.ShapeOnCurveRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector3f;

public class MalkuthFireballRenderer extends EntityRenderer<MalkuthFireball> {



    public MalkuthFireballRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public void render(MalkuthFireball fireball, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {
        matrices.pushPose();
        float ri;
        float gi;
        float bi;
        float r;
        float g;
        float b;

        if (fireball.getAttackType().isFire()) {
            Vector3f color = MalkuthEntity.getMalkuthAttackPreparationParticleColor(fireball.getAttackType());
            ri = Math.clamp(color.x * 1.5f, 0, 1);
            gi = Math.clamp(color.y * 1.5f, 0, 1);
            bi = Math.clamp(color.z * 1.5f, 0, 1);
            r = color.x;
            g = color.y;
            b = color.z;
        }else{
            Vector3f color = MalkuthEntity.getMalkuthAttackPreparationParticleColor(fireball.getAttackType());
            r = 0.7f;
            g = 0.75f;
            b = 0.75f;
            ri = color.x;
            gi = color.y;
            bi = color.z;
        }

        Random random = new Random(fireball.getId() * 21L);
        int dir = random.nextInt(2) == 1 ? -1 : 1;
        int dir2 = random.nextInt(2) == 1 ? -1 : 1;

        float time = 20 * (fireball.tickCount + pticks);
        float time1 = 5 * (fireball.tickCount + pticks);

        matrices.translate(0,fireball.getBbHeight()/2,0);

        matrices.mulPose(Axis.ZP.rotationDegrees(time * dir));
        matrices.mulPose(Axis.XP.rotationDegrees(time1 * dir2));

        this.renderCube(matrices, src.getBuffer(RenderType.lightning()), 0.9f, r,g,b,0.5f,2);
        this.renderCube(matrices, src.getBuffer(RenderType.lightning()), 1.5f * 0.9f, ri,gi,bi,0.5f,2);

        matrices.popPose();
    }

    private void renderCube(PoseStack matrices, VertexConsumer vertex, float radius, float r, float g, float b, float a, int timesRendered){
        matrices.pushPose();

        float half = radius / 2;

        for (int i = 0; i < 4; i++){
            matrices.pushPose();

            matrices.mulPose(Axis.YP.rotationDegrees(i * 90));

            Matrix4f mat = matrices.last().pose();

            for(int k = 0; k < timesRendered; k++) {
                vertex.addVertex(mat, -half, -half, -half).setColor(r, g, b, a);
                vertex.addVertex(mat, -half, -half, half).setColor(r, g, b, a);
                vertex.addVertex(mat, -half, half, half).setColor(r, g, b, a);
                vertex.addVertex(mat, -half, half, -half).setColor(r, g, b, a);
            }

            matrices.popPose();
        }


        Matrix4f mat = matrices.last().pose();
        for (int k = 0; k < timesRendered;k++){

            vertex.addVertex(mat, -half, -half, -half).setColor(r,g,b,a);
            vertex.addVertex(mat, half, -half, -half).setColor(r,g,b,a);
            vertex.addVertex(mat, half, -half, half).setColor(r,g,b,a);
            vertex.addVertex(mat, -half, -half, half).setColor(r,g,b,a);


            vertex.addVertex(mat, -half, half, half).setColor(r,g,b,a);
            vertex.addVertex(mat, half, half, half).setColor(r,g,b,a);
            vertex.addVertex(mat, half, half, -half).setColor(r,g,b,a);
            vertex.addVertex(mat, -half, half, -half).setColor(r,g,b,a);
        }

        matrices.popPose();
    }


    @Override
    public ResourceLocation getTextureLocation(MalkuthFireball p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

}
