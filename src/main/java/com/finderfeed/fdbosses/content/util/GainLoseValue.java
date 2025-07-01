package com.finderfeed.fdbosses.content.util;

import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.util.Mth;

public class GainLoseValue {

    private int maxValue;
    private int currentValue;
    private int oldValue;

    public GainLoseValue(int initValue, int maxValue){
        this.maxValue = maxValue;
        this.oldValue = initValue;
        this.currentValue = initValue;
    }

    public void forward(){
        oldValue = this.currentValue;
        this.currentValue = Mth.clamp(currentValue + 1, 0, maxValue);
    }

    public void backward(){
        oldValue = this.currentValue;
        this.currentValue = Mth.clamp(currentValue - 1, 0, maxValue);
    }

    public void moveValue(boolean forward){
        if (forward){
            this.forward();
        }else{
            this.backward();
        }
    }

    public int getCurrentValue(){
        return currentValue;
    }

    public float getPercent(float pticks){
        return FDMathUtil.lerp(oldValue,currentValue,pticks) / maxValue;
    }

    public void reset(){
        this.currentValue = 0;
    }

}
