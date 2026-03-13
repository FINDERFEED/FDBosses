package com.finderfeed.fdbosses.content.entities.netzach.sector_attack;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public class SectorAttackRenderer extends EntityRenderer<SectorAttack> {

    public SectorAttackRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public void render(SectorAttack entity, float yaw, float pticks, PoseStack matrices, MultiBufferSource p_114489_, int p_114490_) {
    }

    @Override
    public ResourceLocation getTextureLocation(SectorAttack p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

}
