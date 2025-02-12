package com.finderfeed.fdbosses.packets;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:chesed_ray_reflect")
public class ChesedRayReflectPacket extends FDPacket {

    public ChesedRayReflectPacket(){

    }

    public ChesedRayReflectPacket(FriendlyByteBuf buf){

    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {

    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        BossClientPackets.chesedRayReflectParticles();
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }
}
