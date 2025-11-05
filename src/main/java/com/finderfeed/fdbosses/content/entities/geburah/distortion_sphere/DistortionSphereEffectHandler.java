package com.finderfeed.fdbosses.content.entities.geburah.distortion_sphere;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.FDClientHelpers;
import com.finderfeed.fdlib.systems.post_shaders.FDPostShaderInitializeEvent;
import com.finderfeed.fdlib.systems.post_shaders.FDRenderPostShaderEvent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;

@EventBusSubscriber(modid = FDBosses.MOD_ID, value = Dist.CLIENT)
public class DistortionSphereEffectHandler {

    private static PostChain sphericalDistortionEffect;
    private static boolean shouldRenderEffect = false;

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


        var matrices = event.getPoseStack();

        double time = FDClientHelpers.getClientLevel().getGameTime() + event.getPartialTick().getGameTimeDeltaPartialTick(false);
        time /= 10f;

        Vec3 spherePos = new Vec3( 0.5,100.5,0.5);

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();

        Vector3f relativeSpherePos = spherePos.subtract(camPos).toVector3f();

        shouldRenderEffect = relativeSpherePos.length() < 100;

        Matrix4f mat = new Matrix4f(event.getModelViewMatrix());
        Matrix4f projection = new Matrix4f(event.getProjectionMatrix());

        float floorY = 101f;

        float floorOffset = (float) (floorY - camPos.y);


        for (var pass : sphericalDistortionEffect.passes){
            var effect = pass.getEffect();
            effect.safeGetUniform("inverseProjection").set(projection.invert(new Matrix4f()));
            effect.safeGetUniform("inverseModelview").set(mat.invert(new Matrix4f()));
            effect.safeGetUniform("sphereRelativePosition").set(relativeSpherePos);
            effect.safeGetUniform("sphereRadius").set(5f);
            effect.safeGetUniform("innerSphereRadius").set(4f);
            effect.safeGetUniform("floorOffset").set(-2.5f);
        }


    }

    @SubscribeEvent
    public static void renderShader(FDRenderPostShaderEvent.Level event) {
        if (sphericalDistortionEffect == null) return;
        if (shouldRenderEffect) {
            event.doDefaultShaderBeforeShaderStuff();
            sphericalDistortionEffect.process(event.getDeltaTracker().getGameTimeDeltaPartialTick(false));
        }
    }

}
