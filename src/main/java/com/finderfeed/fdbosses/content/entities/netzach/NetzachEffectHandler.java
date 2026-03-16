package com.finderfeed.fdbosses.content.entities.netzach;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.FDClientHelpers;
import com.finderfeed.fdlib.systems.post_shaders.FDPostShaderInitializeEvent;
import com.finderfeed.fdlib.systems.post_shaders.FDRenderPostShaderEvent;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.io.IOException;

@EventBusSubscriber(modid = FDBosses.MOD_ID, value = Dist.CLIENT)
public class NetzachEffectHandler {

    public static PostChain EFFECT;

    private static int effectActiveTime;
    private static int maxFlashTime;
    private static int flashTime;
    private static int flashTimeO;

    @SubscribeEvent
    public static void clientTickEvent(ClientTickEvent.Pre event) {
        if (!Minecraft.getInstance().isPaused()) {
            flashTimeO = flashTime;
            flashTime = Mth.clamp(flashTime - 1, 0, Integer.MAX_VALUE);
            effectActiveTime = Mth.clamp(effectActiveTime - 1, 0, Integer.MAX_VALUE);
        }
    }

    @SubscribeEvent
    public static void registerShader(FDPostShaderInitializeEvent event){
        event.registerPostChain(((textureManager, resourceProvider, renderTarget) -> {
            try {
                return new PostChain(textureManager, resourceProvider, renderTarget, FDBosses.location("shaders/post/netzach_mexico.json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }), chain -> {
            EFFECT = chain;
        });
    }

    @SubscribeEvent
    public static void processEffect(FDRenderPostShaderEvent.Level event) {
        if (effectActiveTime > 0) {
            event.doDefaultShaderBeforeShaderStuff();
            float time = FDMathUtil.lerp(flashTimeO, flashTime, event.getDeltaTracker().getGameTimeDeltaPartialTick(false));
            float p = FDEasings.easeIn(time / maxFlashTime);

            EFFECT.setUniform("percent", p);

            EFFECT.process(event.getDeltaTracker().getGameTimeDeltaPartialTick(false));
        }
    }

    public static void flash(int effectTime, int flashTime){
        maxFlashTime = flashTime;
        effectActiveTime = effectTime;
        NetzachEffectHandler.flashTime = flashTime;
        NetzachEffectHandler.flashTimeO = flashTime;
    }

}
