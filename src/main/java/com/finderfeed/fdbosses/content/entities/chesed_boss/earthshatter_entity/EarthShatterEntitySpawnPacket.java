package com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity;



import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@RegisterFDPacket("fdbosses:earth_shatter_entity_spawn")
public class EarthShatterEntitySpawnPacket extends FDPacket {


    public int entityId;
    public EarthShatterSettings settings;


    public EarthShatterEntitySpawnPacket(FriendlyByteBuf buf){
        this.entityId = buf.readInt();
        this.settings = EarthShatterSettings.NETWORK_CODEC.fromNetwork(buf);
    }

    public EarthShatterEntitySpawnPacket(EarthShatterEntity entity, EarthShatterSettings settings){
        this.entityId = entity.getId();
        this.settings = settings;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        EarthShatterSettings.NETWORK_CODEC.toNetwork(buf,settings);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> context) {

        BossClientPackets.handleEarthShatterSpawnPacket(entityId,settings);

    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> context) {

    }
}
