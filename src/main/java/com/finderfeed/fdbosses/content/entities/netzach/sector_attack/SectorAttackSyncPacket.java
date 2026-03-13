package com.finderfeed.fdbosses.content.entities.netzach.sector_attack;

import com.finderfeed.fdlib.FDClientHelpers;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;


@RegisterFDPacket("fdbosses:sector_attack_sync")
public class SectorAttackSyncPacket extends FDPacket {

    private int id;
    protected String shapeId;

    public SectorAttackSyncPacket(SectorAttack sectorAttack){
        this.id = sectorAttack.getId();
        this.shapeId = sectorAttack.getAttackShapeId();
    }

    public SectorAttackSyncPacket(FriendlyByteBuf buf){
        this.id = buf.readInt();
        this.shapeId = buf.readUtf();
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(id);
        registryFriendlyByteBuf.writeUtf(shapeId);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        if (FDClientHelpers.getClientLevel().getEntity(id) instanceof SectorAttack sectorAttack){
            sectorAttack.sync(this);
        }
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }
}
