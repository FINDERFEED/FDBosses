package com.finderfeed.fdbosses.content.entities.geburah.scales_controller;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:geburah_scales_controller_set_displacement")
public class GeburahScalesControllerSetDisplacement extends FDPacket {

    private int entityId;
    private int displacement;
    private int displacementTime;

    public GeburahScalesControllerSetDisplacement(GeburahEntity geburah, int displacement, int displacementTime){
        this.entityId = geburah.getId();
        this.displacement = displacement;
        this.displacementTime = displacementTime;
    }

    public GeburahScalesControllerSetDisplacement(FriendlyByteBuf buf){
        this.entityId = buf.readInt();
        this.displacement = buf.readInt();
        this.displacementTime = buf.readInt();
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(this.entityId);
        registryFriendlyByteBuf.writeInt(displacement);
        registryFriendlyByteBuf.writeInt(displacementTime);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        BossClientPackets.geburahScalesControllerSetDisplacement(entityId, displacement, displacementTime);
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }
}