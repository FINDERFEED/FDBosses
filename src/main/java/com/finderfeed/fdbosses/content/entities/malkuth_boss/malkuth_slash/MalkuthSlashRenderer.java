package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdlib.util.rendering.renderers.QuadRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class MalkuthSlashRenderer extends EntityRenderer<MalkuthSlashProjectile> {

    public static final ResourceLocation FIRE_LOCATION = FDBosses.location("textures/entities/malkuth/fire_slash.png");
    public static final ResourceLocation ICE_LOCATION = FDBosses.location("textures/entities/malkuth/ice_slash.png");

    public MalkuthSlashRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(MalkuthSlashProjectile projectile, float idk, float pticks, PoseStack matrices, MultiBufferSource src, int light) {


        float slashSize = projectile.getSlashSize();


        QuadRenderer.start(src.getBuffer(RenderType.eyes(this.getTextureLocation(projectile))))
                .direction(projectile.getDeltaMovement().reverse())
                .sizeY(slashSize * 0.3461538f)
                .sizeX(slashSize)
                .pose(matrices)
                .renderBack()
                .verticalRendering()
                .render();



    }

    @Override
    public ResourceLocation getTextureLocation(MalkuthSlashProjectile projectile) {
        return projectile.getAttackType() == MalkuthAttackType.FIRE ? FIRE_LOCATION : ICE_LOCATION;
    }

}
