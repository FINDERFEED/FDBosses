package com.finderfeed.fdbosses.content.entities.base;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.client.boss_screen.BaseBossScreen;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@RegisterFDPacket("fdbosses:force_dossier_screen_close_packet")
public class ForceDossierClosePacket extends FDPacket {

    public ForceDossierClosePacket(){

    }

    public ForceDossierClosePacket(FriendlyByteBuf buf){
    }

    @Override
    public void write(FriendlyByteBuf FriendlyByteBuf) {

    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> iPayloadContext) {
        BossClientPackets.closeDossierScreen();
    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> iPayloadContext) {

    }
}
