package com.finderfeed.fdbosses.packets;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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
    public void clientAction(Supplier<NetworkEvent.Context> iPayloadContext) {
        BossClientPackets.chesedRayReflectParticles();
    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> iPayloadContext) {

    }
}
