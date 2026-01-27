package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@RegisterFDPacket("fdbosses:geburah_sync_ray_visuals")
public class GeburahSyncRayVisualsPacket extends FDPacket {

    private int geburah;
    private boolean visualsActive;

    public GeburahSyncRayVisualsPacket(GeburahEntity geburah, boolean visualsActive){
        this.geburah = geburah.getId();
        this.visualsActive = visualsActive;
    }

    public GeburahSyncRayVisualsPacket(FriendlyByteBuf buf){
        this.visualsActive = buf.readBoolean();
        this.geburah = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeBoolean(visualsActive);
        registryFriendlyByteBuf.writeInt(geburah);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> supplier) {
        BossClientPackets.geburahSyncRayVisuals(geburah, visualsActive);
    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> supplier) {

    }

}
