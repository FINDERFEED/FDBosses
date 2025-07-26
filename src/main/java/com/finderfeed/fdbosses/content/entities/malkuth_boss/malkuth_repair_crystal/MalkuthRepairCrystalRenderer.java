package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_repair_crystal;

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

public class MalkuthRepairCrystalRenderer implements FDFreeEntityRenderer<MalkuthRepairCrystal> {

    @Override
    public void render(MalkuthRepairCrystal malkuthRepairCrystal, float v, float pticks, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        poseStack.pushPose();

        var cannonType = malkuthRepairCrystal.getCrystalType();

        Item item = cannonType.isFire() ? BossItems.MALKUTH_CANNON_REPAIR_MATERIAL_FIRE.get() : BossItems.MALKUTH_CANNON_REPAIR_MATERIAL_ICE.get();

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

        float time = malkuthRepairCrystal.tickCount + pticks;

        poseStack.translate(0, 2.5f + Math.sin(time / 40f) * 0.05f, 0);

        poseStack.mulPose(Axis.YP.rotationDegrees(time / 5f));
        poseStack.mulPose(Axis.XP.rotationDegrees((float) Math.sin( time / 20f)));

        poseStack.scale(0.75f,0.75f,0.75f);

        renderer.renderStatic(item.getDefaultInstance(), ItemDisplayContext.FIXED, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, malkuthRepairCrystal.level(), 0);

        poseStack.popPose();
    }

}
