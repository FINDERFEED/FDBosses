package com.finderfeed.fdbosses.content.entities.malkuth_boss;

import net.minecraft.world.damagesource.DamageSource;

public class MalkuthDamageSource extends DamageSource {

    private MalkuthAttackType malkuthAttackType;
    private int malkuthAttackAmount;

    public MalkuthDamageSource(DamageSource damageSource, MalkuthAttackType attackType, int malkuthAttackAmount) {
        super(damageSource.typeHolder(), damageSource.getDirectEntity(), damageSource.getEntity(), damageSource.sourcePositionRaw());
        this.malkuthAttackType = attackType;
        this.malkuthAttackAmount = malkuthAttackAmount;
    }

    public int getMalkuthAttackAmount() {
        return malkuthAttackAmount;
    }

    public MalkuthAttackType getMalkuthAttackType() {
        return malkuthAttackType;
    }

}
