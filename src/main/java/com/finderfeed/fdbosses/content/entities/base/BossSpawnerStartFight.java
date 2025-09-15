package com.finderfeed.fdbosses.content.entities.base;

import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

@RegisterFDPacket("fdbosses:start_boss_fight")
public class BossSpawnerStartFight extends FDPacket {

    private int spawnerEntityId;

    public BossSpawnerStartFight(int spawnerEntityId){
        this.spawnerEntityId = spawnerEntityId;
    }

    public BossSpawnerStartFight(FriendlyByteBuf buf){
        this.spawnerEntityId = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf FriendlyByteBuf) {
        FriendlyByteBuf.writeInt(spawnerEntityId);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> iPayloadContext) {

    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> iPayloadContext) {
        Player player = iPayloadContext.player();
        if (player.level().getEntity(spawnerEntityId) instanceof BossSpawnerEntity bossSpawnerEntity){
            bossSpawnerEntity.spawn();
        }
    }
}
