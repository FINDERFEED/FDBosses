package com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.rotations;

import com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.GeburahWeaponRotationController;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.util.Mth;

public class GeburahWeaponsRotateTo extends GeburahWeaponRotation {

    public static final NetworkCodec<GeburahWeaponsRotateTo> STREAM_CODEC = NetworkCodec.composite(
            NetworkCodec.FLOAT, v->v.rotationSnapshot,
            NetworkCodec.FLOAT, v->v.rotationTarget,
            NetworkCodec.INT, v->v.rotationTime,
            NetworkCodec.INT, v->v.currentRotationTime,
            NetworkCodec.BOOL, v->v.easeInOut,
            ((aFloat, aFloat2, integer, integer2, easeInOut) -> {
                GeburahWeaponsRotateTo rotateTo = new GeburahWeaponsRotateTo(null, aFloat2, integer, easeInOut);
                rotateTo.currentRotationTime = integer2;
                rotateTo.rotationSnapshot = aFloat;
                return rotateTo;
            })
    );

    private float rotationSnapshot;
    private float rotationTarget;
    private int rotationTime;
    private boolean easeInOut;

    private int currentRotationTime = 0;

    public GeburahWeaponsRotateTo(GeburahWeaponRotationController rotationHandler, float rotationTarget, int rotationTime, boolean easeInOut) {
        if (rotationHandler != null) {
            this.rotationSnapshot = rotationHandler.currentRotation;
        }
        this.rotationTarget = rotationTarget;
        this.rotationTime = rotationTime;
        this.easeInOut = easeInOut;
    }

    public GeburahWeaponsRotateTo(GeburahWeaponRotationController rotationHandler, float rotationTarget, int rotationTime) {
        this(rotationHandler,rotationTarget,rotationTime,true);
    }

    public GeburahWeaponsRotateTo(GeburahWeaponsRotateTo other){
        this.rotationSnapshot = other.rotationSnapshot;
        this.rotationTarget = other.rotationTarget;
        this.currentRotationTime = other.currentRotationTime;
        this.rotationTime = other.rotationTime;
        this.easeInOut = other.easeInOut;
    }

    @Override
    public void tick() {

        currentRotationTime = Mth.clamp(currentRotationTime + 1,0,rotationTime);

        float p = currentRotationTime / (float) rotationTime;
        if (easeInOut){
            p = FDEasings.easeInOut(p);
        }else{
            p = FDEasings.easeOut(p);
        }

        float rotation = FDMathUtil.lerp(rotationSnapshot, rotationTarget, p);

        rotatingWeaponsHandler.currentRotation = rotation;

    }

    @Override
    public boolean finishedRotation() {
        return rotationTime == currentRotationTime;
    }

    @Override
    public boolean shouldPlayRotationSound() {
        return true;
    }

    @Override
    public Type rotationType() {
        return Type.ROTATE_TO;
    }

}
