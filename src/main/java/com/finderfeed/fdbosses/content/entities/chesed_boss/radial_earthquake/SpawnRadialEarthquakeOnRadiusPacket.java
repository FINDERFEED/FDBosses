package com.finderfeed.fdbosses.content.entities.chesed_boss.radial_earthquake;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:spawn_radial_earthquake_on_radius_chesed")
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
    public void write(RegistryFriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(entityId);
        friendlyByteBuf.writeInt(radius);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        BossClientPackets.spawnRadialEarthquakeShatters(entityId, radius);
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }
}
