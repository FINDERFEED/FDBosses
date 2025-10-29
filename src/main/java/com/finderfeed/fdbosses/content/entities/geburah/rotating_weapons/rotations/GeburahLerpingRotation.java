package com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.rotations;

import com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.GeburahWeaponRotationController;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class GeburahLerpingRotation extends GeburahWeaponRotation {

    public static final StreamCodec<FriendlyByteBuf, GeburahLerpingRotation> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, v->v.rotationSnapshot,
            ByteBufCodecs.FLOAT, v->v.maxSpeed,
            ByteBufCodecs.FLOAT, v->v.angle,
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
    public Type rotationType() {
        return Type.LERPING_ROTATE_BY;
    }

}
