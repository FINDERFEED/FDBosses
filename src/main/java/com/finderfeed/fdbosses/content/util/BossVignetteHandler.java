package com.finderfeed.fdbosses.content.util;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.post_shaders.FDPostShaderInitializeEvent;
import com.finderfeed.fdlib.systems.post_shaders.FDRenderPostShaderEvent;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.util.Mth;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;

@EventBusSubscriber(modid = FDBosses.MOD_ID)
public class BossVignetteHandler {

    public static int currentEffectTime = -1;
    public static int maxEffectTime = -1;

    public static FDColor vignetteColor;

    private static ComplexEasingFunction easingFunction;

    public static PostChain VIGNETTE;

    @SubscribeEvent
    public static void registerShader(FDPostShaderInitializeEvent event){
        event.registerPostChain(((textureManager, resourceProvider, renderTarget) -> {
            try {
                return new PostChain(textureManager, resourceProvider, renderTarget, FDBosses.location("shaders/post/vignette.json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }), postChain -> {
            VIGNETTE = postChain;
        });
    }

    @SubscribeEvent
    public static void clientTickEvent(ClientTickEvent.Pre event){
        if (currentEffectTime != -1 && !Minecraft.getInstance().isPaused()){
            if (currentEffectTime >= maxEffectTime){
                currentEffectTime = -1;
                return;
            }
            currentEffectTime = Mth.clamp(currentEffectTime + 1,0, maxEffectTime);
        }
    }

    @SubscribeEvent
    public static void renderShader(FDRenderPostShaderEvent.Level event){
        if (currentEffectTime >= 0) {
            event.doDefaultShaderBeforeShaderStuff();

            float pticks = event.getDeltaTracker().getGameTimeDeltaPartialTick(false);

            float alpha = easingFunction.apply(currentEffectTime + pticks);
            for (var pass : VIGNETTE.passes) {
                var uniform = pass.getEffect().getUniform("vignetteColor");
                if (uniform != null) {
                    uniform.set(new Vector4f(vignetteColor.r, vignetteColor.g, vignetteColor.b, vignetteColor.a * alpha));
                }
            }

            VIGNETTE.setUniform("size", 0.5f);

            VIGNETTE.process(pticks);

        }
    }

    public static void setCurrentVignette(int inTime, int stayTime, int outTime, float r, float g, float b, float a){
        vignetteColor = new FDColor(r,g,b,a);
        var builder = ComplexEasingFunction.builder();

        if (inTime > 0){
            builder.addArea(inTime, FDEasings::easeOut);
        }
        builder.addArea(stayTime, FDEasings::one);
        if (outTime > 0){
            builder.addArea(outTime, FDEasings::reversedEaseOut);
        }
        easingFunction = builder.build();
        maxEffectTime = inTime + stayTime + outTime;
        currentEffectTime = 0;
    }

}
