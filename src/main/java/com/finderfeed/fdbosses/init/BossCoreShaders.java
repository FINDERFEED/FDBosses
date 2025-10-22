package com.finderfeed.fdbosses.init;


import com.finderfeed.fdbosses.FDBosses;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(modid = FDBosses.MOD_ID, value = Dist.CLIENT)
public class BossCoreShaders {

    public static ShaderInstance MALKUTH_BOSS_BAR;
    public static ShaderInstance PARTICLE_NO_DISCARD;

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), FDBosses.location("malkuth_boss_bar"), DefaultVertexFormat.POSITION_TEX_COLOR), (inst) -> {
            MALKUTH_BOSS_BAR = inst;
        });
        event.registerShader(new ShaderInstance(event.getResourceProvider(), FDBosses.location("particle_no_discard"), DefaultVertexFormat.PARTICLE), (shaderInstance -> {
            PARTICLE_NO_DISCARD = shaderInstance;
        }));
    }

}
