package com.finderfeed.fdbosses.content.items.malkuth;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

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
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(entityId);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        BossClientPackets.forceAttackEntity(entityId);
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }
}
