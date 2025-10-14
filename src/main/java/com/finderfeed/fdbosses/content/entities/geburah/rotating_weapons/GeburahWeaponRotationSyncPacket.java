package com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:weapon_rotation_sync")
public class GeburahWeaponRotationSyncPacket extends FDPacket {

    private int entityId;
    private float currentRotation;

    public GeburahWeaponRotationSyncPacket(GeburahEntity geburah){
        GeburahRotatingWeaponsHandler rotatingWeaponsHandler = geburah.getRotatingWeaponsHandler();
        float currentRotation = rotatingWeaponsHandler.getCurrentRotation();
        this.currentRotation = currentRotation;
        this.entityId = geburah.getId();
    }

    public GeburahWeaponRotationSyncPacket(RegistryFriendlyByteBuf buf){
        this.entityId = buf.readInt();
        this.currentRotation = buf.readFloat();
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(this.entityId);
        registryFriendlyByteBuf.writeFloat(this.currentRotation);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        BossClientPackets.handleGeburahWeaponRotationSync(entityId, currentRotation);
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }
}
