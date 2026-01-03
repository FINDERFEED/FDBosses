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
public class PhaseSpherePacket extends FDPacket {

    private CompoundTag abilities;
    private boolean noPhysics;
    private boolean startedUsing;

    public PhaseSpherePacket(Player player, boolean startedUsing){
        var tag = new CompoundTag();
        player.getAbilities().addSaveData(tag);
        this.abilities = tag;
        this.noPhysics = player.noPhysics;
        this.startedUsing = startedUsing;
    }

    public PhaseSpherePacket(FriendlyByteBuf buf){
        this.abilities = buf.readNbt();
        this.noPhysics = buf.readBoolean();
        this.startedUsing = buf.readBoolean();
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeNbt(abilities);
        registryFriendlyByteBuf.writeBoolean(noPhysics);
        registryFriendlyByteBuf.writeBoolean(startedUsing);
    }

    @Override
    public void clientAction(IPayloadContext iPayloadContext) {
        BossClientPackets.chesedItemUse(abilities, noPhysics, startedUsing);
    }

    @Override
    public void serverAction(IPayloadContext iPayloadContext) {

    }
}
