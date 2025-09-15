package com.finderfeed.fdbosses.content.entities.malkuth_boss.packets;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@RegisterFDPacket("fdbosses:set_malkuth_weakness_amount")
public class SetClientMalkuthWeaknessAmountPacket extends FDPacket {

    private int amount;

    public SetClientMalkuthWeaknessAmountPacket(int amount){
        this.amount = amount;
    }

    public SetClientMalkuthWeaknessAmountPacket(FriendlyByteBuf buf){
        this.amount = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf FriendlyByteBuf) {
        FriendlyByteBuf.writeInt(this.amount);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> iPayloadContext) {
        BossClientPackets.setPlayerMalkuthWeaknessAmount(amount);
    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> iPayloadContext) {

    }
}
