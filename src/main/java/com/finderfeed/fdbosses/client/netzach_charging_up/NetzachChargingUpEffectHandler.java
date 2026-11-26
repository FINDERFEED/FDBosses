package com.finderfeed.fdbosses.client.netzach_charging_up;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.netzach.NetzachEntity;
import com.finderfeed.fdlib.systems.post_shaders.FDPostShaderInitializeEvent;
import com.finderfeed.fdlib.systems.post_shaders.FDRenderPostShaderEvent;
import com.finderfeed.fdlib.util.client.FDLevelDepthBufferExtractor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.io.IOException;

@EventBusSubscriber(modid = FDBosses.MOD_ID, value = Dist.CLIENT)
public class NetzachChargingUpEffectHandler {

    public static final int WATCHDOG_TIMEOUT = 4;
    private static final int MAX_STRENGTH_TICKS = 20;

    private static PostChain netzachChargingUp;
    private static NetzachEntity netzach;

    private static int watchdogTicks;
    private static int strengthTicks;
    private static int previousStrengthTicks;
    private static boolean shouldRender;

    public static void kickWatchdog(NetzachEntity netzach){
        NetzachChargingUpEffectHandler.netzach = netzach;
        watchdogTicks = WATCHDOG_TIMEOUT;
    }

    private static void resetEffect(){
        netzach = null;
        watchdogTicks = 0;
        previousStrengthTicks = 0;
        strengthTicks = 0;
        shouldRender = false;
    }

    @SubscribeEvent
    public static void loggingOut(ClientPlayerNetworkEvent.LoggingOut event){
        resetEffect();
    }

    @SubscribeEvent
    public static void framePre(RenderLevelStageEvent event){
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES && netzach != null){
            FDLevelDepthBufferExtractor.requestDepthBuffer();
        }
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event){
        if (netzach == null) return;
        var minecraft = Minecraft.getInstance();
        if (netzach.level() != minecraft.level){
            resetEffect();
            return;
        }
        if (minecraft.isPaused()) return;

        previousStrengthTicks = strengthTicks;
        if (watchdogTicks > 0){
            watchdogTicks--;
            strengthTicks = Math.min(strengthTicks + 1, MAX_STRENGTH_TICKS);
        }else if (strengthTicks > 0){
            strengthTicks--;
        }else{
            resetEffect();
        }
    }

    @SubscribeEvent
    public static void registerPostShader(FDPostShaderInitializeEvent event){
        netzachChargingUp = null;
        shouldRender = false;
        event.registerPostChain((textureManager, resourceProvider, renderTarget) -> {
            try {
                return new PostChain(textureManager, resourceProvider, renderTarget,
                        FDBosses.location("shaders/post/netzach_charging_up.json"));
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }, postChain -> netzachChargingUp = postChain);
    }

    @SubscribeEvent
    public static void renderLevelStage(RenderLevelStageEvent event){
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        shouldRender = false;
        if (netzachChargingUp == null || netzach == null || netzach.level() != Minecraft.getInstance().level) return;

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 position = netzach.getBodyPosition(partialTick);

        float effectStrength = Mth.lerp(partialTick, (float) previousStrengthTicks, strengthTicks) / MAX_STRENGTH_TICKS;
        float time = (float) ((netzach.tickCount + (double) partialTick) / 20.0);

        Vec3 cameraPosition = event.getCamera().getPosition();
        var relativePosition = position.subtract(cameraPosition).toVector3f();

        for (var pass : netzachChargingUp.passes){
            var shader = pass.getEffect();
            shader.safeGetUniform("projection").set(event.getProjectionMatrix());
            shader.safeGetUniform("modelView").set(event.getModelViewMatrix());
            shader.safeGetUniform("effectRelativePosition").set(relativePosition);
            shader.safeGetUniform("effectRadius").set(7f);
            shader.safeGetUniform("innerRadius").set(4f);
            shader.safeGetUniform("time").set(time);
            shader.safeGetUniform("effectStrength").set(effectStrength);
        }
        shouldRender = true;
    }

    @SubscribeEvent
    public static void renderShader(FDRenderPostShaderEvent.Level event){
        if (netzachChargingUp == null || netzach == null || !shouldRender) return;

        event.doDefaultShaderBeforeShaderStuff();

        var depth = FDLevelDepthBufferExtractor.getDepthBuffer();
        if (depth != null){
            netzachChargingUp.passes.getFirst().getEffect().setSampler("WorldDepth", depth::getDepthTextureId);
        }


        netzachChargingUp.process(event.getDeltaTracker().getGameTimeDeltaPartialTick(false));
        shouldRender = false;
    }

}
