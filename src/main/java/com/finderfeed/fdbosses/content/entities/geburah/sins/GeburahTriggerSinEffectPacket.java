package com.finderfeed.fdbosses.content.entities.geburah.sins;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;


@RegisterFDPacket("fdbosses:")
public class GeburahTriggerSinEffectPacket extends FDPacket {

    public GeburahTriggerSinEffectPacket(){

    }

    public GeburahTriggerSinEffectPacket(FriendlyByteBuf buf){

    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {

    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        BossClientPackets.triggerSinEffect();
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }

}
