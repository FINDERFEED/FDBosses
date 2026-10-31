package com.finderfeed.fdbosses.content.entities.netzach.distorted_slam;

import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class DistortedSlamEffect {

    public int currentTime = 0;
    public Vec3 position;
    public int effectTime;
    public float sphereRadius;
    public float yFloorPos;

    public DistortedSlamEffect(Vec3 position, int effectTime, float sphereMaxRadius, float yFloorPos){
        this.position = position;
        this.effectTime = effectTime;
        this.sphereRadius = sphereMaxRadius;
        this.yFloorPos = yFloorPos;
    }

    public boolean tick(){
        currentTime = Mth.clamp(currentTime + 1, 0, effectTime);
        return currentTime == effectTime;
    }

    public float getSphereRadius(float pticks){
        float p = FDEasings.easeOut(FDEasings.easeOut(this.getPercent(pticks)));
        return p * sphereRadius;
    }

    public float getEffectStrength(float pticks){
        float p = FDEasings.easeOut(1 - this.getPercent(pticks));
        return p;
    }

    public float getPercent(float pticks){
        float p = Mth.clamp((currentTime + pticks) / effectTime, 0, 1);
        return p;
    }

}
