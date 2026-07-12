package com.finderfeed.fdbosses.content.entities.netzach.time_stabilizer;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:time_stabilizer_screen_opened")
public class OpenTimeStabilizerScreenPacket extends FDPacket {

    private int timeStabilizer;
    private float currentDisplacement;
    private float targetDisplacement;

    public OpenTimeStabilizerScreenPacket(TimeStabilizer timeStabilizer, float currentDisplacement, float targetDisplacement){
        this.currentDisplacement = currentDisplacement;
        this.timeStabilizer = timeStabilizer.getId();
        this.targetDisplacement = targetDisplacement;
    }

    public OpenTimeStabilizerScreenPacket(RegistryFriendlyByteBuf byteBuf){
        this.timeStabilizer = byteBuf.readInt();
        this.currentDisplacement = byteBuf.readFloat();
        this.targetDisplacement = byteBuf.readFloat();
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(timeStabilizer);
        registryFriendlyByteBuf.writeFloat(this.currentDisplacement);
        registryFriendlyByteBuf.writeFloat(this.targetDisplacement);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        BossClientPackets.openTimeStabilizerScreen(this.timeStabilizer, this.currentDisplacement, this.targetDisplacement);
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }

}
