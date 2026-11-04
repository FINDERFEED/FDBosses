package com.finderfeed.fdbosses.content.entities.netzach.backtrack_entity;

import com.finderfeed.fdbosses.content.entities.netzach.NetzachEffectOverlay;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:backtrack_effect")
public class BacktrackEffectPacket extends FDPacket {

    private int effectTime;
    private int flashTime;

    public BacktrackEffectPacket(int effectTime, int flashTime){
        this.effectTime = effectTime;
        this.flashTime = flashTime;
    }

    public BacktrackEffectPacket(RegistryFriendlyByteBuf friendlyByteBuf){
        this.effectTime = friendlyByteBuf.readInt();
        this.flashTime = friendlyByteBuf.readInt();
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(effectTime);
        registryFriendlyByteBuf.writeInt(flashTime);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        NetzachEffectOverlay.flash(effectTime, flashTime);
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }
}
