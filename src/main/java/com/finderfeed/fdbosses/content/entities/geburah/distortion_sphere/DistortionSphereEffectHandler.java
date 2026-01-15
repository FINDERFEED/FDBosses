package com.finderfeed.fdbosses.content.entities.geburah.distortion_sphere;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.post_shaders.FDPostShaderInitializeEvent;
import com.finderfeed.fdlib.systems.post_shaders.FDRenderPostShaderEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;

@EventBusSubscriber(modid = FDBosses.MOD_ID, value = Dist.CLIENT)
public class DistortionSphereEffectHandler {

    private static PostChain sphericalDistortionEffect;

    private static DistortionSphereEffect currentEffect;

    public static void setDistortionSphereEffect(DistortionSphereEffect effect){
        currentEffect = effect;
    }

    public static DistortionSphereEffect getCurrentEffect() {
        return currentEffect;
    }

    @SubscribeEvent
    public static void loggingOut(ClientPlayerNetworkEvent.LoggingOut event){
        currentEffect = null;
    }

    @SubscribeEvent
    public static void clientTickEvent(ClientTickEvent.Pre event){
        if (currentEffect != null && !Minecraft.getInstance().isPaused()){
            if (currentEffect.tick()){
                currentEffect = null;
            }
        }
    }

    @SubscribeEvent
    public static void registerPostShader(FDPostShaderInitializeEvent event){
        event.registerPostChain(((textureManager, resourceProvider, renderTarget) -> {
            try {
                return new PostChain(textureManager,resourceProvider, renderTarget, FDBosses.location("shaders/post/spherical_distortion_field.json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }), postChain -> {
            sphericalDistortionEffect = postChain;
        });
    }


    @SubscribeEvent
    public static void renderLevelStageEvent(RenderLevelStageEvent event){

        if (sphericalDistortionEffect == null) return;

        var stage = event.getStage();
        if (stage != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        if (currentEffect == null) return;


        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();


        Vec3 spherePos = currentEffect.position;
        Vector3f relativeSpherePos = spherePos.subtract(camPos).toVector3f();

        Matrix4f mat = new Matrix4f(event.getModelViewMatrix());
        Matrix4f projection = new Matrix4f(event.getProjectionMatrix());

        float pticks = event.getPartialTick().getGameTimeDeltaPartialTick(false);

        float radius = currentEffect.getSphereRadius(pticks);
        float innerRadius = currentEffect.getInnerSphereRadius(pticks);
        float strength = currentEffect.getEffectStrength(pticks);

        float y = currentEffect.yFloorPos;
        float floorOffset = y - (float)spherePos.y;

        Matrix4f invertedProjection = projection.invert(new Matrix4f());
        Matrix4f invertedModelview = mat.invert(new Matrix4f());

        for (var pass : sphericalDistortionEffect.passes){
            var effect = pass.getEffect();
            effect.safeGetUniform("inverseProjection").set(invertedProjection);
            effect.safeGetUniform("inverseModelview").set(invertedModelview);
            effect.safeGetUniform("sphereRelativePosition").set(relativeSpherePos);
            effect.safeGetUniform("sphereRadius").set(radius);
            effect.safeGetUniform("innerSphereRadius").set(innerRadius);
            effect.safeGetUniform("floorOffset").set(floorOffset);
            effect.safeGetUniform("effectStrength").set(strength * 2);
        }


    }

    @SubscribeEvent
    public static void renderShader(FDRenderPostShaderEvent.Level event) {
        if (sphericalDistortionEffect == null) return;
        if (currentEffect != null) {
            Vec3 position = currentEffect.position;

            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            Vec3 camPos = camera.getPosition();

            float renderDistance = Minecraft.getInstance().options.renderDistance().get() * 16;


            if (camPos.distanceTo(position) < renderDistance) {
                event.doDefaultShaderBeforeShaderStuff();
                sphericalDistortionEffect.process(event.getDeltaTracker().getGameTimeDeltaPartialTick(false));
            }

        }
    }

}
