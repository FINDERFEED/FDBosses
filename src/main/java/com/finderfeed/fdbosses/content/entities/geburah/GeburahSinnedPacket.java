package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@RegisterFDPacket("fdbosses:geburah_sinned_packet")
public class GeburahSinnedPacket extends FDPacket {

    private int entityId;

    public GeburahSinnedPacket(GeburahEntity geburah){
        this.entityId = geburah.getId();
    }

    public GeburahSinnedPacket(FriendlyByteBuf buf){
        this.entityId = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(this.entityId);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> supplier) {
        BossClientPackets.geburahSinnedPacket(this.entityId);
    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> supplier) {

    }
}
