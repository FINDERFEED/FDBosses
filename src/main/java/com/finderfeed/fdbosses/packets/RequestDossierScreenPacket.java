package com.finderfeed.fdbosses.packets;

import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.FDPacketHandler;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@RegisterFDPacket("fdbosses:request_dossier_screen")
public class RequestDossierScreenPacket extends FDPacket {

    private EntityType<?> entityType;

    public RequestDossierScreenPacket(FriendlyByteBuf buf){
        this.entityType = BuiltInRegistries.ENTITY_TYPE.get(buf.readResourceLocation());
    }

    public RequestDossierScreenPacket(EntityType<?> bossEntityType){
        this.entityType = bossEntityType;
    }

    @Override
    public void write(FriendlyByteBuf registryFriendlyByteBuf) {
        var key = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        registryFriendlyByteBuf.writeResourceLocation(key);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> supplier) {

    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> supplier) {
        if (entityType != null){
            var player = supplier.get().getSender();
            FDPacketHandler.INSTANCE.sendTo(new OpenBossDossierEntityTypePacket(player.level(), entityType), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }



}