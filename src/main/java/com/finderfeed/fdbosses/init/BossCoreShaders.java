package com.finderfeed.fdbosses.init;


import com.finderfeed.fdbosses.FDBosses;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = FDBosses.MOD_ID,bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BossCoreShaders {

    public static ShaderInstance MALKUTH_BOSS_BAR;
    public static ShaderInstance PARTICLE_NO_DISCARD;
    public static ShaderInstance CHESED_ITEM_OVERLAY;

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), FDBosses.location("malkuth_boss_bar"), DefaultVertexFormat.POSITION_TEX_COLOR), (inst) -> {
            MALKUTH_BOSS_BAR = inst;
        });
        event.registerShader(new ShaderInstance(event.getResourceProvider(), FDBosses.location("particle_no_discard"), DefaultVertexFormat.PARTICLE), (shaderInstance -> {
            PARTICLE_NO_DISCARD = shaderInstance;
        }));
        event.registerShader(new ShaderInstance(event.getResourceProvider(), FDBosses.location("chesed_item_overlay"), DefaultVertexFormat.POSITION_TEX_COLOR), (shaderInstance -> {
            CHESED_ITEM_OVERLAY = shaderInstance;
        }));
    }

}
