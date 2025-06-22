package com.finderfeed.fdbosses.mixin;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_chain.MalkuthChainEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "wantsToStopRiding", at = @At("HEAD"), cancellable = true)
    public void wantsToStopRiding(CallbackInfoReturnable<Boolean> cir){
        if (((Player)(Object)this).getVehicle() instanceof MalkuthChainEntity malkuthChainEntity){
            cir.setReturnValue(false);
        }
    }

}
