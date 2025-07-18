package com.finderfeed.fdbosses.content.entities.malkuth_boss;

import net.minecraft.util.RandomSource;

public enum MalkuthAttackType {

    ICE,
    FIRE;

    public static MalkuthAttackType getOpposite(MalkuthAttackType original){
        if (original.isIce()){
            return FIRE;
        }else{
            return ICE;
        }
    }

    public boolean isFire(){
        return this == FIRE;
    }

    public boolean isIce(){
        return this == ICE;
    }

    public static MalkuthAttackType getRandom(RandomSource randomSource){
        return MalkuthAttackType.values()[randomSource.nextInt(MalkuthAttackType.values().length)];
    }

}
