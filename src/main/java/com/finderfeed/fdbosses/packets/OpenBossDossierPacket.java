package com.finderfeed.fdbosses.packets;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

@RegisterFDPacket("fdbosses:open_boss_dossier")
public class OpenBossDossierPacket extends FDPacket {

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Item>> ITEM_CODEC = ByteBufCodecs.registry(Registries.ITEM).apply(ByteBufCodecs.list());

    private List<Item> drops;
    private int bossSpawnerId;

    public OpenBossDossierPacket(RegistryFriendlyByteBuf buf){
        this.drops = ITEM_CODEC.decode(buf);
        this.bossSpawnerId = buf.readInt();
    }

    public OpenBossDossierPacket(BossSpawnerEntity bossSpawner){
        EntityType<?> bossEntityType = bossSpawner.getBossEntityType();
        this.bossSpawnerId = bossSpawner.getId();


        var fakeEntity = (LivingEntity) bossEntityType.create(bossSpawner.level());

        var lootTable = fakeEntity.getLootTable();

        var server = ((ServerLevel)bossSpawner.level()).getServer();

        List<Item> items = BossUtil.getItemsFromLootTable(server, lootTable);

        this.drops = items;
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        ITEM_CODEC.encode(registryFriendlyByteBuf, this.drops);
        registryFriendlyByteBuf.writeInt(bossSpawnerId);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        BossClientPackets.openBossDossierScreen(bossSpawnerId, drops);
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }
}
