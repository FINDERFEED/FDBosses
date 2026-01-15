package com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.rotations;

import com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.GeburahWeaponRotationController;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import com.finderfeed.fdlib.util.math.FDMathUtil;

public class GeburahLerpingRotation extends GeburahWeaponRotation {

    public static final NetworkCodec<GeburahLerpingRotation> STREAM_CODEC = NetworkCodec.composite(
            NetworkCodec.FLOAT, v->v.rotationSnapshot,
            NetworkCodec.FLOAT, v->v.maxSpeed,
            NetworkCodec.FLOAT, v->v.angle,
            (rotationSnapshot,maxspeed,angle)->{
                GeburahLerpingRotation lerpingRotation = new GeburahLerpingRotation(null, maxspeed, angle);
                lerpingRotation.rotationSnapshot = rotationSnapshot;
                return lerpingRotation;
            }
    );

    private float rotationSnapshot;
    private float maxSpeed;
    private float angle;

    private boolean finished = false;

    public GeburahLerpingRotation(GeburahWeaponRotationController controller, float maxSpeed, float moveAngle){
        if (controller != null) {
            this.rotationSnapshot = controller.getCurrentRotation();
        }
        this.maxSpeed = maxSpeed;
        this.angle = moveAngle;
    }

    public GeburahLerpingRotation(GeburahLerpingRotation rotation){
        this.rotationSnapshot = rotation.rotationSnapshot;
        this.maxSpeed = rotation.maxSpeed;
        this.angle = rotation.angle;
        this.finished = rotation.finished;
    }

    @Override
    public void tick() {
        var handler = this.rotatingWeaponsHandler;

        float targetRotation = rotationSnapshot + angle;
        float remainingRotation = targetRotation - handler.currentRotation;

        float speed = FDMathUtil.lerp(0, maxSpeed, Math.max(0,remainingRotation / angle));

        if (speed < 0.01){
            finished = true;
        }

        if (angle > 0) {
            handler.currentRotation += speed;
        }else{
            handler.currentRotation -= speed;
        }

    }

    @Override
    public boolean finishedRotation() {
        return finished;
    }

    @Override
    public boolean shouldPlayRotationSound() {
        return true;
    }

    @Override
    public Type rotationType() {
        return Type.LERPING_ROTATE_BY;
    }

}
