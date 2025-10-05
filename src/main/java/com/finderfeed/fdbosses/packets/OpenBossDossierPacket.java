package com.finderfeed.fdbosses.packets;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

@RegisterFDPacket("fdbosses:open_boss_dossier")
public class OpenBossDossierPacket extends FDPacket {

    private static final NetworkCodec<List<Item>> ITEM_CODEC = NetworkCodec.listOf(NetworkCodec.registry(()->ForgeRegistries.ITEMS));

    private List<Item> drops;
    private int bossSpawnerId;

    public OpenBossDossierPacket(FriendlyByteBuf buf){
        this.drops = ITEM_CODEC.fromNetwork(buf);
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
    public void write(FriendlyByteBuf registryFriendlyByteBuf) {
        ITEM_CODEC.toNetwork(registryFriendlyByteBuf, this.drops);
        registryFriendlyByteBuf.writeInt(bossSpawnerId);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> supplier) {
        BossClientPackets.openBossDossierScreen(bossSpawnerId, drops);
    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> supplier) {

    }

}
