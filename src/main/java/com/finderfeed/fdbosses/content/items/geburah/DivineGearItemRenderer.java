package com.finderfeed.fdbosses.content.items.geburah;

import com.finderfeed.fdbosses.init.BossDataComponents;
import com.finderfeed.fdlib.FDClientHelpers;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.FDFreeItemRenderer;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class DivineGearItemRenderer implements FDFreeItemRenderer {

    @Override
    public void render(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack matrices, MultiBufferSource multiBufferSource, int i, int i1) {

        var data = itemStack.get(BossDataComponents.DIVINE_GEAR_COMPONENT);

        if (data == null || FDClientHelpers.getClientLevel() == null) return;

        int charge = data.getCharge();
        int color = charge == 0 ? 0xff1111 : 0x00aaff;
        String s = String.valueOf(charge);
        Font font = Minecraft.getInstance().font;

        if (itemDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || itemDisplayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {

            float pticks = FDRenderUtil.getPartialTickWithPause();
            float time = FDClientHelpers.getClientLevel().getGameTime() + pticks;
            float sin = (float) Math.sin(time / 10) * 0.01f;

            matrices.pushPose();
            float scale = 0.01f;


            float width = font.width(s) * scale;

            matrices.mulPose(Axis.YP.rotationDegrees(180));
            matrices.mulPose(Axis.ZP.rotationDegrees(180));

            matrices.translate(-width / 2, -0.85 + sin, -0.5);
            if (itemDisplayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
                matrices.translate(0.525, 0, 0);
            } else {
                matrices.translate(0.5, 0, 0);
            }
            matrices.scale(scale, scale, -scale);


            font.drawInBatch(String.valueOf(charge), 0, 0, color, true, matrices.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
            matrices.popPose();

        }else if (itemDisplayContext == ItemDisplayContext.GUI){

            float scale = 0.05f;


            matrices.mulPose(Axis.YP.rotationDegrees(180));
            matrices.mulPose(Axis.ZP.rotationDegrees(180));
            float width = font.width(s) * scale;
            matrices.translate(-width / 2 + 0.8, -0.9, -1);
            matrices.scale(scale, scale, -scale);


            font.drawInBatch(String.valueOf(charge), 0, 0, color, true, matrices.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);


        }


    }

}
