package com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.rotations;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:geburah_weapon_rotation")
public class StartGeburahWeaponRotationPacket extends FDPacket {

    private GeburahWeaponRotation geburahWeaponRotation;

    private int entityId;

    public StartGeburahWeaponRotationPacket(GeburahEntity geburah, GeburahWeaponRotation geburahWeaponRotation){
        this.geburahWeaponRotation = geburahWeaponRotation.rotationType().copyHandler.apply(geburahWeaponRotation); //Because in singleplayer it doesn't write anything to buf
        this.entityId = geburah.getId();
    }

    public StartGeburahWeaponRotationPacket(RegistryFriendlyByteBuf buf){
        var e = buf.readEnum(GeburahWeaponRotation.Type.class);
        this.geburahWeaponRotation = e.codec.decode(buf);
        this.entityId = buf.readInt();
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeEnum(geburahWeaponRotation.rotationType());
        this.useCodec(geburahWeaponRotation.rotationType().codec, registryFriendlyByteBuf, geburahWeaponRotation);
        registryFriendlyByteBuf.writeInt(entityId);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        BossClientPackets.startGeburahWeaponRotation(entityId, geburahWeaponRotation);
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }


    private <T extends GeburahWeaponRotation> void useCodec(StreamCodec<FriendlyByteBuf, T> codec, FriendlyByteBuf buf, GeburahWeaponRotation weaponRotation){
        codec.encode(buf,(T) weaponRotation);
    }

}
