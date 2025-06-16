package com.finderfeed.fdbosses;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;

public class NoCooldownLookControl extends LookControl {

    public NoCooldownLookControl(Mob p_24945_) {
        super(p_24945_);
    }


    @Override
    public void tick() {
        super.tick();
        lookAtCooldown = 0;
    }
}
