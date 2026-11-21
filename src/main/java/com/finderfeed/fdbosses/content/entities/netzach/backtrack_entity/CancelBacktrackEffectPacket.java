package com.finderfeed.fdbosses.content.entities.netzach.backtrack_entity;

import com.finderfeed.fdbosses.content.entities.netzach.NetzachEffectOverlay;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:cancel_backtrack_effect")
public class CancelBacktrackEffectPacket extends FDPacket {

    public CancelBacktrackEffectPacket(){
    }

    public CancelBacktrackEffectPacket(RegistryFriendlyByteBuf buf){
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf){
    }

    @Override
    public void clientAction(IPayloadContext context){
        NetzachEffectOverlay.cancelEffect();
    }

    @Override
    public void serverAction(IPayloadContext context){
    }
}
