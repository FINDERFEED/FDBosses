package com.finderfeed.fdbosses.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class BossRenderTypes extends RenderType{

    public BossRenderTypes(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }

    public static final RenderType LIGHTNING_NO_CULL = create(
            "lightning_no_cull",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .setOutputState(WEATHER_TARGET)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(false)
    );


    public static final RenderStateShard.TransparencyStateShard FD_ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard(
            "fd_additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }
    );

    public static Function<ResourceLocation, RenderType> TEXT_ADDITIVE = Util.memoize(BossRenderTypes::textAdditive);

    public static RenderType textAdditive(ResourceLocation locationIn) {
        var rendertype$state = RenderType.CompositeState.builder()
                .setShaderState(RenderType.RENDERTYPE_TEXT_SHADER)
                .setTextureState(new TextureStateShard(locationIn,false,false))
                .setTransparencyState(FD_ADDITIVE_TRANSPARENCY)
                .setLightmapState(RenderType.LIGHTMAP)
                .createCompositeState(false);
        return RenderType.create("neoforge_text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, rendertype$state);
    }


}
