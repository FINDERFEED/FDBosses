package com.finderfeed.fdbosses.mixin;

import com.finderfeed.fdbosses.content.items.chesed.ChesedItem;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    @Shadow public Input input;


    @Shadow protected int sprintTriggerTime;

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;canStartSprinting()Z", shift = At.Shift.BEFORE))
    public void aiStep(CallbackInfo ci){
        Player player = (Player) (Object) this;
        if (player.getUseItem().getItem() instanceof ChesedItem){
            this.input.leftImpulse /= 0.2F;
            this.input.forwardImpulse /= 0.2F;
            this.sprintTriggerTime = 7;
        }
    }

}
