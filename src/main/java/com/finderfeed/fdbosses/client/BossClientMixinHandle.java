package com.finderfeed.fdbosses.client;

import com.finderfeed.fdbosses.BossClientEvents;
import com.finderfeed.fdbosses.init.BossEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class BossClientMixinHandle {

    public static void darknessCalculate(LivingEntity entity, float pticks, CallbackInfoReturnable<Float> cir){

        if (entity.hasEffect(BossEffects.CHESED_DARKEN.get()) || entity.hasEffect(BossEffects.CHESED_GAZE.get())) {
//            float value = cir.getReturnValue();

            //Hope it won't change in the future...
            MobEffectInstance mobeffectinstance = entity.getEffect(MobEffects.NIGHT_VISION);
            float value = !mobeffectinstance.endsWithin(200) ? 1.0F : 0.7F + Mth.sin(((float)mobeffectinstance.getDuration() - pticks) * (float) Math.PI * 0.2F) * 0.3F;

            float percent = 1 - Math.max(BossClientEvents.getChesedGazePercent(pticks), BossClientEvents.getChesedDarkenPercent(pticks));
            cir.setReturnValue(value * percent);
        }
    }

}
