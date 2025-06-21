package com.finderfeed.fdbosses.content.entities.malkuth_boss.packets;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_crush.MalkuthCrushAttack;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;


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
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(this.entityId);
        registryFriendlyByteBuf.writeEnum(this.malkuthAttackType);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        BossClientPackets.malkuthSwordCharge(this.malkuthAttackType,this.entityId);
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }
}
