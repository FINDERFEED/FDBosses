package com.finderfeed.fdbosses.content.entities.geburah.distortion_sphere;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:start_geburah_distortion_effect")
public class StartGeburahDistortionEffectPacket extends FDPacket {

    public int entityId;

    public StartGeburahDistortionEffectPacket(FriendlyByteBuf buf){
        this.entityId = buf.readInt();
    }

    public StartGeburahDistortionEffectPacket(GeburahEntity geburah){
        this.entityId = geburah.getId();
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(entityId);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        BossClientPackets.startGeburahDistortionEffect(entityId);
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }

}
