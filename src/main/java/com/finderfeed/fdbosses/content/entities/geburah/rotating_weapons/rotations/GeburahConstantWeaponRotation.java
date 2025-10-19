package com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.rotations;

import com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.GeburahRotatingWeaponsHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class GeburahConstantWeaponRotation extends GeburahWeaponRotation {

    public static final StreamCodec<FriendlyByteBuf, GeburahConstantWeaponRotation> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, v->v.rotationSpeed,
            (speed)->{
                GeburahConstantWeaponRotation geburahConstantWeaponRotation = new GeburahConstantWeaponRotation(speed);
                return geburahConstantWeaponRotation;
            }
    );

    private float rotationSpeed;

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
    public Type rotationType() {
        return Type.CONSTANT_ROTATION;
    }

}
