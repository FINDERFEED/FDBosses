package com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;

public class ClockWyvernLookControl extends LookControl {
    public ClockWyvernLookControl(Mob p_24945_) {
        super(p_24945_);
    }

    public void tick() {
        if (this.resetXRotOnTick()) {
            this.mob.setXRot(0.0F);
        }

        this.getYRotD().ifPresent(p_287447_ -> this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, p_287447_, this.yMaxRotSpeed));
        this.getXRotD().ifPresent(p_352768_ -> this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), p_352768_, this.xMaxRotAngle)));

        this.clampHeadRotationToBody();
    }

}
