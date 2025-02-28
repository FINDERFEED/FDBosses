package com.finderfeed.fdbosses.client;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Quaternionf;

public class BossRenderUtil {

    public static void renderFDModelInScreen(PoseStack matrices, FDModel model, float x, float y,float rotX,float rotY,float rotZ,float scale){
        matrices.pushPose();

        VertexConsumer builder = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.entityCutout(FDBosses.location("textures/entities/chesed.png")));

        matrices.translate(x,y,0);

        matrices.mulPose(new Quaternionf().rotationXYZ(rotX,rotY,rotZ));

        matrices.scale(-scale,-scale,-scale);

        Lighting.setupForEntityInInventory();

        model.render(matrices,builder, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,1f,1f,1f,1f);

        Minecraft.getInstance().renderBuffers().bufferSource().endLastBatch();

        Lighting.setupFor3DItems();

        matrices.popPose();
    }

}
