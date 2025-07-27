package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon;

import com.finderfeed.fdbosses.init.BossItems;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDFreeEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;

public class MalkuthCannonRenderer implements FDFreeEntityRenderer<MalkuthCannonEntity> {

    @Override
    public void render(MalkuthCannonEntity malkuthCannonEntity, float v, float pticks, PoseStack matrices, MultiBufferSource multiBufferSource, int light) {

        if (malkuthCannonEntity.requiresRepair()) {
            matrices.pushPose();

            var cannonType = malkuthCannonEntity.getCannonType();

            Item item = cannonType.isFire() ? BossItems.MALKUTH_CANNON_REPAIR_MATERIAL_FIRE.get() : BossItems.MALKUTH_CANNON_REPAIR_MATERIAL_ICE.get();

            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

            matrices.mulPose(Axis.YP.rotationDegrees(-v));


            float time = malkuthCannonEntity.tickCount + pticks;

            float vt = (float) Math.sin(time / 30f);
            float vt2 = (float) Math.sin(time / 20f);

            matrices.translate(0, 4 + vt * 0.125f, 0);
            matrices.mulPose(Axis.YP.rotationDegrees(vt * 5));
            matrices.mulPose(Axis.XP.rotationDegrees(vt2 * 5));

            renderer.renderStatic(item.getDefaultInstance(), ItemDisplayContext.FIXED, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, matrices, multiBufferSource, malkuthCannonEntity.level(), 0);

            matrices.popPose();
        }

    }

}
