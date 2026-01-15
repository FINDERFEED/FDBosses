package com.finderfeed.fdbosses.content.entities.geburah.sins;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;


@RegisterFDPacket("fdbosses:")
public class GeburahTriggerSinEffectPacket extends FDPacket {

    private float soundPitch;

    public GeburahTriggerSinEffectPacket(float soundPitch){
        this.soundPitch = soundPitch;
    }

    public GeburahTriggerSinEffectPacket(FriendlyByteBuf buf){
        this.soundPitch = buf.readFloat();
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeFloat(this.soundPitch);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        BossClientPackets.triggerSinEffect(soundPitch);
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }

}
