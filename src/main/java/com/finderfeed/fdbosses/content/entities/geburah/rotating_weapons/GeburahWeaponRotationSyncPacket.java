package com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@RegisterFDPacket("fdbosses:weapon_rotation_sync")
public class GeburahWeaponRotationSyncPacket extends FDPacket {

    private int entityId;
    private float currentRotation;

    public GeburahWeaponRotationSyncPacket(GeburahEntity geburah){
        GeburahWeaponRotationController rotatingWeaponsHandler = geburah.getWeaponRotationController();
        float currentRotation = rotatingWeaponsHandler.getCurrentRotation();
        this.currentRotation = currentRotation;
        this.entityId = geburah.getId();
    }

    public GeburahWeaponRotationSyncPacket(FriendlyByteBuf buf){
        this.entityId = buf.readInt();
        this.currentRotation = buf.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(this.entityId);
        registryFriendlyByteBuf.writeFloat(this.currentRotation);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> supplier) {
        BossClientPackets.handleGeburahWeaponRotationSync(entityId, currentRotation);
    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> supplier) {

    }

}
