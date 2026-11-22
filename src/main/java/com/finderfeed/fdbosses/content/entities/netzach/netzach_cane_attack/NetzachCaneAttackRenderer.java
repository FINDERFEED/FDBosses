package com.finderfeed.fdbosses.content.entities.netzach.netzach_cane_attack;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class NetzachCaneAttackRenderer extends EntityRenderer<NetzachCaneAttack> {

    private static final ResourceLocation TEXTURE = FDBosses.location("textures/entities/netzach/netzach_cane.png");
    private static final ResourceLocation TEXTURE_TRANSPARENT = FDBosses.location("textures/entities/netzach/netzach_cane_layer.png");

    public NetzachCaneAttackRenderer(EntityRendererProvider.Context context){
        super(context);
    }

    @Override
    public void render(NetzachCaneAttack entity, float yaw, float partialTick, PoseStack matrices, MultiBufferSource buffers, int light){
        float age = entity.tickCount + partialTick;

        float fall = Mth.clamp(age / NetzachCaneAttack.FALL_TICKS, 0, 1);
        float tilt = Mth.clamp((age - NetzachCaneAttack.FALL_TICKS / 3f) / 10, 0, 1);
        float fade = Mth.clamp((age - entity.getExplosionTick()) / NetzachCaneAttack.FADE_TICKS, 0, 1);

        matrices.pushPose();

        matrices.translate(0, 12 * (1 - FDEasings.easeIn(fall)), 0);
        matrices.mulPose(Axis.YN.rotationDegrees(entity.getYRot()));
        matrices.mulPose(Axis.ZP.rotationDegrees(entity.tilt * FDEasings.easeInOutBack(tilt)));
        matrices.scale(15, 15, 15);

        FDModel cane = NetzachCaneAttack.getCaneModel();
        FDModel caneLayer = NetzachCaneAttack.getCaneLayerModel();

        caneLayer.render(matrices, buffers.getBuffer(RenderType.EYES.apply(
                TEXTURE_TRANSPARENT,
                RenderStateShard.LIGHTNING_TRANSPARENCY)), light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1 - fade);

        cane.render(matrices, buffers.getBuffer(RenderType.entityTranslucent(TEXTURE)), light, OverlayTexture.NO_OVERLAY, 1, 1, 1, fall * (1 - fade));

        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(NetzachCaneAttack entity){
        return TEXTURE;
    }
}
