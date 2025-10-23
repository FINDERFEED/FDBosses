package com.finderfeed.fdbosses.mixin;

import com.finderfeed.fdbosses.BossClientEvents;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class InputMixin {

    @Inject(method = "tick",at = @At("TAIL"))
    public void tick(boolean p_234118_, float p_234119_, CallbackInfo ci){

        Input input = ((Input) (Object) this);

        if (BossClientEvents.hasPlayerJumpedUnderSinEffect){
            input.jumping = false;
        }

    }

}
