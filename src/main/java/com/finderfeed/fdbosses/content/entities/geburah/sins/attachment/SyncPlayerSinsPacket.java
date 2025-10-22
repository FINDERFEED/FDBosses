package com.finderfeed.fdbosses.content.entities.geburah.sins.attachment;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:sync_player_sins_packet")
public class SyncPlayerSinsPacket extends FDPacket {

    private PlayerSins playerSins;

    public SyncPlayerSinsPacket(PlayerSins playerSins){
        this.playerSins = new PlayerSins(playerSins);
    }

    public SyncPlayerSinsPacket(RegistryFriendlyByteBuf friendlyByteBuf){
        this.playerSins = PlayerSins.STREAM_CODEC.decode(friendlyByteBuf);
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        PlayerSins.STREAM_CODEC.encode(registryFriendlyByteBuf, playerSins);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        BossClientPackets.syncPlayerSinsPacket(playerSins);
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }

}
