package com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@RegisterFDPacket("fdbosses:stop_geburah_weapon_rotation")
public class StopGeburahWeaponRotationPacket extends FDPacket {

    private int entityId;

    public StopGeburahWeaponRotationPacket(GeburahEntity geburah){
        this.entityId = geburah.getId();
    }

    public StopGeburahWeaponRotationPacket(FriendlyByteBuf buf){
        this.entityId = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(entityId);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> supplier) {
        BossClientPackets.stopWeaponRotation(entityId);
    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> supplier) {

    }

}
