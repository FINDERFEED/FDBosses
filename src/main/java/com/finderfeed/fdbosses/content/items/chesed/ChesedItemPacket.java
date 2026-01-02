package com.finderfeed.fdbosses.content.items.chesed;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:update_player_abilities_packet")
public class ChesedItemPacket extends FDPacket {

    private CompoundTag abilities;
    private boolean noPhysics;

    public ChesedItemPacket(Player player){
        var tag = new CompoundTag();
        player.getAbilities().addSaveData(tag);
        this.abilities = tag;
        this.noPhysics = player.noPhysics;
    }

    public ChesedItemPacket(FriendlyByteBuf buf){
        this.abilities = buf.readNbt();
        this.noPhysics = buf.readBoolean();
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeNbt(abilities);
        registryFriendlyByteBuf.writeBoolean(noPhysics);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        BossClientPackets.chesedItemUse(abilities, noPhysics);
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }
}
