package com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.rotations;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@RegisterFDPacket("fdbosses:geburah_weapon_rotation")
public class StartGeburahWeaponRotationPacket extends FDPacket {

    private GeburahWeaponRotation geburahWeaponRotation;

    private int entityId;

    public StartGeburahWeaponRotationPacket(GeburahEntity geburah, GeburahWeaponRotation geburahWeaponRotation){
        this.geburahWeaponRotation = geburahWeaponRotation.rotationType().copyHandler.apply(geburahWeaponRotation); //Because in singleplayer it doesn't write anything to buf
        this.entityId = geburah.getId();
    }

    public StartGeburahWeaponRotationPacket(FriendlyByteBuf buf){
        var e = buf.readEnum(GeburahWeaponRotation.Type.class);
        this.geburahWeaponRotation = e.codec.fromNetwork(buf);
        this.entityId = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeEnum(geburahWeaponRotation.rotationType());
        this.useCodec(geburahWeaponRotation.rotationType().codec, registryFriendlyByteBuf, geburahWeaponRotation);
        registryFriendlyByteBuf.writeInt(entityId);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> supplier) {
        BossClientPackets.startGeburahWeaponRotation(entityId, geburahWeaponRotation);
    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> supplier) {

    }

    private <T extends GeburahWeaponRotation> void useCodec(NetworkCodec<T> codec, FriendlyByteBuf buf, GeburahWeaponRotation weaponRotation){
        codec.toNetwork(buf,(T) weaponRotation);
    }

}
