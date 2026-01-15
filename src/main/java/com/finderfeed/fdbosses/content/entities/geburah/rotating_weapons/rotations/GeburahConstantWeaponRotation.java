package com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.rotations;

import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;

public class GeburahConstantWeaponRotation extends GeburahWeaponRotation {

    public static final NetworkCodec<GeburahConstantWeaponRotation> STREAM_CODEC = NetworkCodec.composite(
            NetworkCodec.FLOAT, v->v.rotationSpeed,
            (speed)->{
                GeburahConstantWeaponRotation geburahConstantWeaponRotation = new GeburahConstantWeaponRotation(speed);
                return geburahConstantWeaponRotation;
            }
    );

    private float rotationSpeed;

    private boolean playSound = false;

    public GeburahConstantWeaponRotation(GeburahConstantWeaponRotation other){
        this.rotationSpeed = other.rotationSpeed;
    }

    public GeburahConstantWeaponRotation(float speed) {
        this.rotationSpeed = speed;
    }

    @Override
    public void tick() {
        var handler = this.rotatingWeaponsHandler;
        handler.currentRotation += rotationSpeed;
    }

    @Override
    public boolean finishedRotation() {
        return false;
    }

    @Override
    public boolean shouldPlayRotationSound() {
        return playSound;
    }

    @Override
    public Type rotationType() {
        return Type.CONSTANT_ROTATION;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public void setPlaySound(boolean playSound) {
        this.playSound = playSound;
    }
}
