package com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons;

import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.neoforged.neoforge.network.PacketDistributor;

public class GeburahRotatingWeaponsHandler {

    private GeburahEntity geburah;

    protected float currentRotation = 0;
    protected float oldRotation = 0;

    protected GeburahWeaponRotation weaponRotation;

    public GeburahRotatingWeaponsHandler(GeburahEntity geburah){
        this.geburah = geburah;
    }

    public void tick(){
        this.oldRotation = currentRotation;
        if (weaponRotation != null){
            weaponRotation.tick();
            if (weaponRotation.finishedRotation()){
                weaponRotation = null;
                this.trySendRotationSyncPacket();
            }
        }
    }

    private void rotateWeapons(GeburahWeaponRotation geburahWeaponRotation){
        this.weaponRotation = geburahWeaponRotation;
    }

    public void rotateWeaponsBy(float rotationDelta, int rotationTime){
        this.rotateWeapons(new GeburahWeaponsRotateTo(this,
                this.currentRotation + rotationDelta, rotationTime
        ));
    }

    public float getCurrentRotation() {
        return currentRotation;
    }

    public float getOldRotation() {
        return oldRotation;
    }

    //Not advised to use
    public void setRotation(float rotation){
        this.oldRotation = currentRotation;
        this.currentRotation = rotation;
        this.trySendRotationSyncPacket();
    }

    public float getLerpedRotation(float pticks){
        return FDMathUtil.lerp(oldRotation,currentRotation,pticks);
    }

    public boolean finishedRotation(){
        return weaponRotation == null || weaponRotation.finishedRotation();
    }

    private void trySendRotationSyncPacket(){
        if (!geburah.level().isClientSide){
            PacketDistributor.sendToPlayersTrackingEntity(geburah, new GeburahWeaponRotationSyncPacket(geburah));
        }
    }

}
