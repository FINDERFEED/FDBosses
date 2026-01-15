package com.finderfeed.fdbosses.mixin;

import com.finderfeed.fdbosses.BossMixinHandler;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_chain.MalkuthChainEntity;
import com.finderfeed.fdbosses.content.items.chesed.PhaseSphereHandler;
import com.finderfeed.fdbosses.content.util.Undismountable;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Player.class)
public class PlayerMixin {


    @Inject(method = "wantsToStopRiding", at = @At("HEAD"), cancellable = true)
    public void wantsToStopRiding(CallbackInfoReturnable<Boolean> cir){

        Entity vehicle = ((Player)(Object)this).getVehicle();


        if (vehicle instanceof MalkuthChainEntity malkuthChainEntity || vehicle instanceof Undismountable){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSpectator()Z", ordinal = 1))
    public void tick(CallbackInfo ci){
        PhaseSphereHandler.onChesedItemUse((Player) (Object) this);
    }

//    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isInvulnerableTo(Lnet/minecraft/world/damagesource/DamageSource;)Z", shift = At.Shift.AFTER))
//    public void actuallyHurt(DamageSource damageSource, float p_36313_, CallbackInfo ci){
//
//        Player player = ((Player)(Object)this);
//        BossMixinHandler.actuallyHurtPlayerMixin(damageSource, player);
//
//    }

}
