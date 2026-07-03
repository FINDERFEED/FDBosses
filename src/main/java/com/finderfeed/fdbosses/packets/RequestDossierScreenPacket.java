package com.finderfeed.fdbosses.packets;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

@RegisterFDPacket("fdbosses:request_dossier_screen")
public class RequestDossierScreenPacket extends FDPacket {

    private EntityType<?> entityType;

    public RequestDossierScreenPacket(RegistryFriendlyByteBuf buf){
        this.entityType = BuiltInRegistries.ENTITY_TYPE.get(buf.readResourceLocation());
    }

    public RequestDossierScreenPacket(EntityType<?> bossEntityType){
        this.entityType = bossEntityType;
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        var key = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        registryFriendlyByteBuf.writeResourceLocation(key);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {
        if (entityType != null){
            var player = iPayloadContext.player();
            if (player instanceof ServerPlayer serverPlayer){
                PacketDistributor.sendToPlayer(serverPlayer, new OpenBossDossierEntityTypePacket(serverPlayer.level(), entityType));
            }
        }
    }

}