package com.finderfeed.fdbosses.content.entities.base;

import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:start_boss_fight")
public class BossSpawnerStartFight extends FDPacket {

    private int spawnerEntityId;

    public BossSpawnerStartFight(int spawnerEntityId){
        this.spawnerEntityId = spawnerEntityId;
    }

    public BossSpawnerStartFight(RegistryFriendlyByteBuf buf){
        this.spawnerEntityId = buf.readInt();
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(spawnerEntityId);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {

    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {
        Player player = iPayloadContext.player();
        if (player.level().getEntity(spawnerEntityId) instanceof BossSpawnerEntity bossSpawnerEntity){
            bossSpawnerEntity.spawn();
        }
    }
}
