package com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons;

import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.util.Mth;

public class GeburahWeaponsRotateTo extends GeburahWeaponRotation {

    private float rotationSnapshot;
    private float rotationTarget;
    private int rotationTime;

    private int currentRotationTime = 0;

    public GeburahWeaponsRotateTo(GeburahRotatingWeaponsHandler rotationHandler, float rotationTarget, int rotationTime) {
        super(rotationHandler);
        this.rotationSnapshot = rotationHandler.currentRotation;
        this.rotationTarget = rotationTarget;
        this.rotationTime = rotationTime;
    }

    @Override
    public void tick() {

        currentRotationTime = Mth.clamp(currentRotationTime + 1,0,rotationTime);

        float p = FDEasings.easeOut(currentRotationTime / (float) rotationTime);

        float rotation = FDMathUtil.lerp(rotationSnapshot, rotationTarget, p);

        rotatingWeaponsHandler.currentRotation = rotation;

    }

    @Override
    public boolean finishedRotation() {
        return rotationTime == currentRotationTime;
    }
}
