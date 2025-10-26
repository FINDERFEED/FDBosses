package com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons;

import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.rotations.GeburahConstantWeaponRotation;
import com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.rotations.GeburahWeaponRotation;
import com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.rotations.GeburahWeaponsRotateTo;
import com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.rotations.StartGeburahWeaponRotationPacket;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class GeburahWeaponRotationController {

    private GeburahEntity geburah;

    public float currentRotation = 0;
    protected float oldRotation = 0;

    protected GeburahWeaponRotation weaponRotation;

    public GeburahWeaponRotationController(GeburahEntity geburah){
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

    public void rotateWeapons(GeburahWeaponRotation geburahWeaponRotation){
        geburahWeaponRotation.setRotatingWeaponsHandler(this);
        if (!geburah.level().isClientSide){
            PacketDistributor.sendToPlayersTrackingEntity(geburah, new StartGeburahWeaponRotationPacket(this.geburah, geburahWeaponRotation));
        }
        this.weaponRotation = geburahWeaponRotation;
    }

    public void rotateWeaponsBy(float rotationDelta, int rotationTime){
        this.rotateWeapons(new GeburahWeaponsRotateTo(this,
                this.currentRotation + rotationDelta, rotationTime
        ));
    }

    public void startConstantRotation(float rotationSpeed) {
        if (this.weaponRotation instanceof GeburahConstantWeaponRotation constantWeaponRotation) {
            float currentRotSpeed = constantWeaponRotation.getRotationSpeed();
            if (currentRotSpeed == rotationSpeed){
                return;
            }
        }
        this.rotateWeapons(new GeburahConstantWeaponRotation(rotationSpeed));
    }

    public void stopRotation(){
        this.weaponRotation = null;
        if (!geburah.level().isClientSide) {
            PacketDistributor.sendToPlayersTrackingEntity(geburah, new StopGeburahWeaponRotationPacket(geburah));
            this.trySendRotationSyncPacket();
        }
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

    public void onStartSeeingGeburah(ServerPlayer serverPlayer){
        GeburahWeaponRotationSyncPacket syncPacket = new GeburahWeaponRotationSyncPacket(geburah);
        PacketDistributor.sendToPlayer(serverPlayer, syncPacket);

        if (this.weaponRotation != null){
            StartGeburahWeaponRotationPacket geburahWeaponRotationPacket = new StartGeburahWeaponRotationPacket(geburah, this.weaponRotation);
            PacketDistributor.sendToPlayer(serverPlayer, geburahWeaponRotationPacket);
        }
    }

    private void trySendRotationSyncPacket(){
        if (!geburah.level().isClientSide){
            PacketDistributor.sendToPlayersTrackingEntity(geburah, new GeburahWeaponRotationSyncPacket(geburah));
        }
    }

}
