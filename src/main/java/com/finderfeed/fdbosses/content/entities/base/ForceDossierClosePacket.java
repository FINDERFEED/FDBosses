package com.finderfeed.fdbosses.content.entities.base;

import com.finderfeed.fdbosses.client.boss_screen.BaseBossScreen;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:force_dossier_screen_close_packet")
public class ForceDossierClosePacket extends FDPacket {

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {

    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        if (Minecraft.getInstance().screen instanceof BaseBossScreen baseBossScreen){
            Minecraft.getInstance().setScreen(null);
        }
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }
}
