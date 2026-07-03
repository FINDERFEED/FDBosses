package com.finderfeed.fdbosses.packets;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

@RegisterFDPacket("fdbosses:open_boss_dossier_entity_type")
public class OpenBossDossierEntityTypePacket extends FDPacket {

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Item>> ITEM_CODEC = ByteBufCodecs.registry(Registries.ITEM).apply(ByteBufCodecs.list());

    private List<Item> drops;
    private EntityType<?> entityType;

    public OpenBossDossierEntityTypePacket(RegistryFriendlyByteBuf buf){
        this.drops = ITEM_CODEC.decode(buf);
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
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        ITEM_CODEC.encode(registryFriendlyByteBuf, this.drops);
        var key = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        registryFriendlyByteBuf.writeResourceLocation(key);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        BossClientPackets.openBossDossierScreenEntityType(entityType, drops);
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }
}