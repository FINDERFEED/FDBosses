package com.finderfeed.fdbosses.mixin;

import com.finderfeed.fdbosses.BossMixinHandler;
import com.finderfeed.fdbosses.content.entities.geburah.chain_trap.GeburahChainTrapEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_chain.MalkuthChainEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {


    @Inject(method = "wantsToStopRiding", at = @At("HEAD"), cancellable = true)
    public void wantsToStopRiding(CallbackInfoReturnable<Boolean> cir){

        Entity vehicle = ((Player)(Object)this).getVehicle();

        if (vehicle instanceof MalkuthChainEntity malkuthChainEntity || vehicle instanceof GeburahChainTrapEntity chainTrapEntity){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isInvulnerableTo(Lnet/minecraft/world/damagesource/DamageSource;)Z", shift = At.Shift.AFTER))
    public void actuallyHurt(DamageSource damageSource, float p_36313_, CallbackInfo ci){

        Player player = ((Player)(Object)this);
        BossMixinHandler.actuallyHurtPlayerMixin(damageSource, player);

    }

}
