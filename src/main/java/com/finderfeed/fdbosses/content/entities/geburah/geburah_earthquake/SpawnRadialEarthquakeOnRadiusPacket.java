package com.finderfeed.fdbosses.content.entities.geburah.geburah_earthquake;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@RegisterFDPacket("fdbosses:spawn_radial_earthquake_on_radius")
public class SpawnRadialEarthquakeOnRadiusPacket extends FDPacket {

    private int entityId;
    private int radius;
    private Vec3 direction;
    private float angle;

    public SpawnRadialEarthquakeOnRadiusPacket(GeburahEarthquake entity, int radius, Vec3 direction, float angle){
        this.entityId = entity.getId();
        this.radius = radius;
        this.direction = direction;
        this.angle = angle;
    }

    public SpawnRadialEarthquakeOnRadiusPacket(FriendlyByteBuf buf){
        this.entityId = buf.readInt();
        this.radius = buf.readInt();
        this.direction = new Vec3(
                buf.readDouble(),
                0,
                buf.readDouble()
        );
        this.angle = buf.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(entityId);
        registryFriendlyByteBuf.writeInt(radius);
        registryFriendlyByteBuf.writeDouble(this.direction.x);
        registryFriendlyByteBuf.writeDouble(this.direction.z);
        registryFriendlyByteBuf.writeFloat(this.angle);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> supplier) {
        BossClientPackets.geburahEarthquakeSpawnEarthshatters(entityId, direction, radius, angle);
    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> supplier) {

    }

}
