package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_earthquake;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public class MalkuthEarthquakeRenderer extends EntityRenderer<MalkuthEarthquake> {

    public MalkuthEarthquakeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(MalkuthEarthquake entity, float yaw, float pticks, PoseStack matrices, MultiBufferSource src, int light) {



    }

    @Override
    public ResourceLocation getTextureLocation(MalkuthEarthquake texture) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
