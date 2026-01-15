package com.finderfeed.fdbosses.content.items.malkuth;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@RegisterFDPacket("fdbosses:player_force_attack_entity_packet")
public class PlayerForceAttackEntityPacket extends FDPacket {

    private int entityId;

    public PlayerForceAttackEntityPacket(LivingEntity entity){
        this.entityId = entity.getId();
    }

    public PlayerForceAttackEntityPacket(FriendlyByteBuf buf){
        this.entityId = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(entityId);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> supplier) {
        BossClientPackets.forceAttackEntity(entityId);
    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> supplier) {

    }

}
