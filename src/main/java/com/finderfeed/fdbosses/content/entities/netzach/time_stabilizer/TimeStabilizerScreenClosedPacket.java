package com.finderfeed.fdbosses.content.entities.netzach.time_stabilizer;

import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:close_time_stabilizer")
public class TimeStabilizerScreenClosedPacket extends FDPacket {

    private int timeStabilizer;
    private boolean wasSolved;
    private float currentRotation;

    public TimeStabilizerScreenClosedPacket(int timeStabilizer, boolean solved, float currentRotation){
        this.wasSolved = solved;
        this.currentRotation = currentRotation;
        this.timeStabilizer = timeStabilizer;
    }

    public TimeStabilizerScreenClosedPacket(RegistryFriendlyByteBuf byteBuf){
        this.timeStabilizer = byteBuf.readInt();
        this.wasSolved = byteBuf.readBoolean();
        this.currentRotation = byteBuf.readFloat();
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(this.timeStabilizer);
        registryFriendlyByteBuf.writeBoolean(this.wasSolved);
        registryFriendlyByteBuf.writeFloat(this.currentRotation);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {

    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {
        var player = iPayloadContext.player();
        var level = player.level();
        if (level.getEntity(timeStabilizer) instanceof TimeStabilizer timeStabilizer){
            if (timeStabilizer.distanceTo(player) > TimeStabilizer.TERMINAL_DISTANCE){
                return;
            }
            timeStabilizer.screenWasClosed(this.currentRotation);
        }

    }

}
