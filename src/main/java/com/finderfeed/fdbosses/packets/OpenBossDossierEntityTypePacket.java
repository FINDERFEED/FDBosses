package com.finderfeed.fdbosses.packets;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

@RegisterFDPacket("fdbosses:open_boss_dossier_entity_type")
public class OpenBossDossierEntityTypePacket extends FDPacket {

    private static final NetworkCodec<List<Item>> ITEM_CODEC = NetworkCodec.listOf(NetworkCodec.registry(()-> ForgeRegistries.ITEMS));

    private List<Item> drops;
    private EntityType<?> entityType;

    public OpenBossDossierEntityTypePacket(FriendlyByteBuf buf){
        this.drops = ITEM_CODEC.fromNetwork(buf);
        this.entityType = BuiltInRegistries.ENTITY_TYPE.get(buf.readResourceLocation());
    }

    public OpenBossDossierEntityTypePacket(Level level, EntityType<?> bossEntityType){
        var fakeEntity = (LivingEntity) bossEntityType.create(level);

        var lootTable = fakeEntity.getLootTable();

        var server = ((ServerLevel)level).getServer();

        List<Item> items = BossUtil.getItemsFromLootTable(server, lootTable);

        this.entityType = bossEntityType;
        this.drops = items;
    }

    @Override
    public void write(FriendlyByteBuf registryFriendlyByteBuf) {
        ITEM_CODEC.toNetwork(registryFriendlyByteBuf, this.drops);
        var key = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        registryFriendlyByteBuf.writeResourceLocation(key);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> supplier) {
        BossClientPackets.openBossDossierScreenEntityType(entityType, drops);
    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> supplier) {

    }

}