package com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons;

public class GeburahConstantWeaponRotation extends GeburahWeaponRotation {

    private float rotationSpeed;

    public GeburahConstantWeaponRotation(GeburahRotatingWeaponsHandler rotationHandler, float speed) {
        super(rotationHandler);
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

}
