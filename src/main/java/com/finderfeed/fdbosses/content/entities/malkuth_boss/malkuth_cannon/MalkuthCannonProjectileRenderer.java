package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.ModelData;

public class MalkuthCannonProjectileRenderer extends EntityRenderer<MalkuthCannonProjectile> {

    public static final ResourceLocation FIRE_TEXTURE = FDBosses.location("textures/entities/malkuth/malkuth_cannon_fire_projectile.png");
    public static final ResourceLocation ICE_TEXTURE = FDBosses.location("textures/entities/malkuth/malkuth_cannon_ice_projectile.png");

    private final FDModel model;

    public MalkuthCannonProjectileRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
        this.model = new FDModel(BossModels.MALKUTH_CANNON_PROJECTILE.get());
    }

    @Override
    public void render(MalkuthCannonProjectile projectile, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {

        VertexConsumer vertexConsumer = src.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(projectile)));

        matrices.pushPose();

        matrices.translate(0,projectile.getBbHeight()/2,0);

        FDRenderUtil.applyMovementMatrixRotations(matrices, projectile.getDeltaMovement());

        int rotationDir = projectile.getId() % 2 == 0 ? 1 : -1;

        matrices.mulPose(Axis.YP.rotationDegrees(rotationDir * 10 * (projectile.tickCount + pticks) + projectile.getId() * 434.5435f ));

        BlockRenderDispatcher blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();

        if (projectile.getMalkuthAttackType().isFire()) {
            matrices.pushPose();
            matrices.mulPose(Axis.ZN.rotationDegrees(180));
            matrices.translate(-0.5, 0.5, -0.5);
            blockRenderDispatcher.renderSingleBlock(Blocks.FIRE.defaultBlockState(), matrices, src, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
            matrices.popPose();
        }
        this.model.render(matrices, vertexConsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f ,1f);

        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(MalkuthCannonProjectile proj) {
        return proj.getMalkuthAttackType().isIce() ? ICE_TEXTURE : FIRE_TEXTURE;
    }

}
