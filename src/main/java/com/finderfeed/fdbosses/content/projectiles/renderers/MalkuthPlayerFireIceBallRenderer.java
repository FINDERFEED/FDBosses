package com.finderfeed.fdbosses.content.projectiles.renderers;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_fireball.MalkuthFireball;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_fireball.MalkuthFireballRenderer;
import com.finderfeed.fdbosses.content.projectiles.MalkuthPlayerFireIceBall;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector3f;

public class MalkuthPlayerFireIceBallRenderer extends EntityRenderer<MalkuthPlayerFireIceBall> {



    public MalkuthPlayerFireIceBallRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public void render(MalkuthPlayerFireIceBall fireball, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {
        matrices.pushPose();

        float scale = FDMathUtil.clamp((fireball.tickCount + pticks) / 5,0,1);

        matrices.scale(scale,scale,scale);

        float ri;
        float gi;
        float bi;
        float r;
        float g;
        float b;

        if (fireball.getAttackType().isFire()) {
            Vector3f color = MalkuthEntity.getMalkuthAttackPreparationParticleColor(fireball.getAttackType());
            ri = FDMathUtil.clamp(color.x * 1.5f, 0, 1);
            gi = FDMathUtil.clamp(color.y * 1.5f, 0, 1);
            bi = FDMathUtil.clamp(color.z * 1.5f, 0, 1);
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

        MalkuthFireballRenderer.renderCube(matrices, src.getBuffer(RenderType.lightning()), 0.6f, r,g,b,0.5f,2);
        MalkuthFireballRenderer.renderCube(matrices, src.getBuffer(RenderType.lightning()), 0.9f, ri,gi,bi,0.5f,2);

        matrices.popPose();
    }



    @Override
    public ResourceLocation getTextureLocation(MalkuthPlayerFireIceBall p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

}
