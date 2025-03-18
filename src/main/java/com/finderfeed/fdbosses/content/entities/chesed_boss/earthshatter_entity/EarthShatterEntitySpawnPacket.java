package com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity;



import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:earth_shatter_entity_spawn")
public class EarthShatterEntitySpawnPacket extends FDPacket {


    public int entityId;
    public EarthShatterSettings settings;


    public EarthShatterEntitySpawnPacket(FriendlyByteBuf buf){
        this.entityId = buf.readInt();
        this.settings = EarthShatterSettings.NETWORK_CODEC.decode(buf);
    }

    public EarthShatterEntitySpawnPacket(EarthShatterEntity entity, EarthShatterSettings settings){
        this.entityId = entity.getId();
        this.settings = settings;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        EarthShatterSettings.NETWORK_CODEC.encode(buf,settings);
    }

    @Override
    public void clientAction(IPayloadContext context) {

        BossClientPackets.handleEarthShatterSpawnPacket(entityId,settings);

    }

    @Override
    public void serverAction(IPayloadContext context) {

    }
}
