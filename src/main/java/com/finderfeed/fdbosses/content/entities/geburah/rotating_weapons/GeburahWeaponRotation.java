package com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons;

import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;

public abstract class GeburahWeaponRotation {

    protected GeburahRotatingWeaponsHandler rotatingWeaponsHandler;

    public GeburahWeaponRotation(GeburahRotatingWeaponsHandler rotationHandler){
        this.rotatingWeaponsHandler = rotationHandler;
    }

    public abstract void tick();

    public abstract boolean finishedRotation();

}
