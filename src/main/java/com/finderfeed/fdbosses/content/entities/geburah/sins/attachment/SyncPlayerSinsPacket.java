package com.finderfeed.fdbosses.content.entities.geburah.sins.attachment;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@RegisterFDPacket("fdbosses:sync_player_sins_packet")
public class SyncPlayerSinsPacket extends FDPacket {

    private PlayerSins playerSins;

    public SyncPlayerSinsPacket(PlayerSins playerSins){
        this.playerSins = new PlayerSins(playerSins);
    }

    public SyncPlayerSinsPacket(FriendlyByteBuf friendlyByteBuf){
        this.playerSins = PlayerSins.STREAM_CODEC.fromNetwork(friendlyByteBuf);
    }

    @Override
    public void write(FriendlyByteBuf registryFriendlyByteBuf) {
        PlayerSins.STREAM_CODEC.toNetwork(registryFriendlyByteBuf, playerSins);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> supplier) {
        BossClientPackets.syncPlayerSinsPacket(playerSins);
    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> supplier) {

    }

}
