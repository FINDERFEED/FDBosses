package com.finderfeed.fdbosses.content.entities.malkuth_boss.packets;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:set_malkuth_weakness_amount")
public class SetClientMalkuthWeaknessAmountPacket extends FDPacket {

    private int amount;

    public SetClientMalkuthWeaknessAmountPacket(int amount){
        this.amount = amount;
    }

    public SetClientMalkuthWeaknessAmountPacket(RegistryFriendlyByteBuf buf){
        this.amount = buf.readInt();
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(this.amount);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        BossClientPackets.setPlayerMalkuthWeaknessAmount(amount);
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }
}
