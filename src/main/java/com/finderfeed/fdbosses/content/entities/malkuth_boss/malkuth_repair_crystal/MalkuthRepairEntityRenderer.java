package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_repair_crystal;

import com.finderfeed.fdbosses.init.BossItems;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;

public class MalkuthRepairEntityRenderer extends EntityRenderer<MalkuthRepairEntity> {

    public MalkuthRepairEntityRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public void render(MalkuthRepairEntity entity, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {

        var type = entity.getEntityData().get(MalkuthRepairEntity.ATTACK_TYPE);

        Item item = type.isFire() ? BossItems.MALKUTH_CANNON_REPAIR_MATERIAL_FIRE.get() : BossItems.MALKUTH_CANNON_REPAIR_MATERIAL_ICE.get();

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

        matrices.pushPose();

        FDRenderUtil.applyMovementMatrixRotations(matrices, entity.getDeltaMovement());

        float time = entity.tickCount + pticks;

        int dir = entity.getEntityData().get(MalkuthRepairEntity.RANDOMIZED_DIRECTION);

        matrices.mulPose(Axis.YP.rotationDegrees(time * 10 * dir));

        renderer.renderStatic(item.getDefaultInstance(), ItemDisplayContext.FIXED, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, matrices, src, entity.level(), 0);

        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(MalkuthRepairEntity p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
