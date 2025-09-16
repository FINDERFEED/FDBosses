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

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), FDBosses.location("malkuth_boss_bar"), DefaultVertexFormat.POSITION_TEX_COLOR), (inst) -> {
            MALKUTH_BOSS_BAR = inst;
        });
    }

}
