package com.finderfeed.fdbosses.content.entities.malkuth_boss.packets;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_crush.MalkuthCrushAttack;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;


@RegisterFDPacket("fdbosses:malkuth_charge_sword")
public class MalkuthChargeSwordPacket extends FDPacket {

    private int entityId;
    private MalkuthAttackType malkuthAttackType;

    public MalkuthChargeSwordPacket(MalkuthEntity malkuthEntity, MalkuthAttackType malkuthAttackType){
        this.entityId = malkuthEntity.getId();
        this.malkuthAttackType = malkuthAttackType;
    }

    public MalkuthChargeSwordPacket(FriendlyByteBuf buf){
        this.entityId = buf.readInt();
        this.malkuthAttackType = buf.readEnum(MalkuthAttackType.class);
    }

    @Override
    public void write(FriendlyByteBuf FriendlyByteBuf) {
        FriendlyByteBuf.writeInt(this.entityId);
        FriendlyByteBuf.writeEnum(this.malkuthAttackType);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> iPayloadContext) {
        BossClientPackets.malkuthSwordCharge(this.malkuthAttackType,this.entityId);
    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> iPayloadContext) {

    }
}
