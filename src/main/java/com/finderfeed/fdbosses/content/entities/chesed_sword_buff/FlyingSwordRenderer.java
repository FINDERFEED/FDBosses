package com.finderfeed.fdbosses.content.entities.chesed_sword_buff;

import com.finderfeed.fdlib.FDClientHelpers;
import com.finderfeed.fdlib.util.client.ColoredVertexConsumer;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class FlyingSwordRenderer extends EntityRenderer<FlyingSwordEntity> {

    public FlyingSwordRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(FlyingSwordEntity entity, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {
        matrices.pushPose();
        float halfHeight = entity.getBbHeight() / 2;
        matrices.translate(0,halfHeight,0);


        int targetId = entity.getTargetIdForClient();
        ClientLevel clientLevel = (ClientLevel) FDClientHelpers.getClientLevel();

        if (clientLevel.getEntity(targetId) instanceof LivingEntity target) {
            Vec3 thisPos = entity.position().add(0,entity.getBbHeight()/2,0);
            Vec3 thatPos = entity.getTargetPos(target,pticks);
            Vec3 between = thatPos.subtract(thisPos);
            FDRenderUtil.applyMovementMatrixRotations(matrices, between);
        }

        float time = entity.tickCount + pticks;
        float p = Math.clamp(time / FlyingSwordEntity.DELAY_UNTIL_LAUNCH,0,1);
        float offset = -Mth.sin(p * (FDMathUtil.FPI)) * 2;
        matrices.translate(0,offset,0);

        matrices.mulPose(Axis.YP.rotationDegrees(FDEasings.easeOut(p) * 720));



        matrices.mulPose(Axis.ZN.rotationDegrees(45));

        float scale = Math.clamp(p *2,0,1);
        matrices.scale(scale,scale,scale);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.render(entity.getItem(), ItemDisplayContext.FIXED, false, matrices, ColoredVertexConsumer.wrapBufferSource(src,5, 209, 255,200),
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                itemRenderer.getModel(entity.getItem(),entity.level(),Minecraft.getInstance().player,0));

        matrices.popPose();

    }

    @Override
    public ResourceLocation getTextureLocation(FlyingSwordEntity p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
