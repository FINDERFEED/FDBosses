package com.finderfeed.fdbosses.content.entities.chesed_boss.radial_earthquake;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@RegisterFDPacket("fdbosses:spawn_radial_earthquake_on_radius")
public class SpawnRadialEarthquakeOnRadiusPacket extends FDPacket {

    private int entityId;
    private int radius;

    public SpawnRadialEarthquakeOnRadiusPacket(RadialEarthquakeEntity entity, int radius){
        this.entityId = entity.getId();
        this.radius = radius;
    }

    public SpawnRadialEarthquakeOnRadiusPacket(FriendlyByteBuf buf){
        this.entityId = buf.readInt();
        this.radius = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(entityId);
        friendlyByteBuf.writeInt(radius);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> supplier) {
        BossClientPackets.spawnRadialEarthquakeShatters(entityId, radius);
    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> supplier) {

    }
}
