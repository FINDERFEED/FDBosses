package com.finderfeed.fdbosses.mixin;


import com.finderfeed.fdbosses.BossClientEvents;
import com.finderfeed.fdlib.ClientMixinHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "getNightVisionScale",at = @At("RETURN"), cancellable = true)
    private static void nightVisionScale(LivingEntity entity, float pticks, CallbackInfoReturnable<Float> cir){
        Player player = Minecraft.getInstance().player;
        if (player != null && player.hasEffect(MobEffects.NIGHT_VISION)) {
            float value = cir.getReturnValue();

            float percent = 1 - Math.max(BossClientEvents.getChesedGazePercent(pticks),BossClientEvents.getChesedDarkenPercent(pticks));

            cir.setReturnValue(value * percent);
        }
    }


}
