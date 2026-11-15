package com.finderfeed.fdbosses.packets;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.content.boss_notes.BossNote;
import com.finderfeed.fdbosses.init.BossRegistries;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@RegisterFDPacket("fdbosses:boss_note")
public class BossNotePacket extends FDPacket {

    private static final StreamCodec<RegistryFriendlyByteBuf, BossNote> CODEC = ByteBufCodecs.registry(BossRegistries.BOSS_NOTE);

    private final BossNote bossNote;

    public BossNotePacket(BossNote bossNote){
        this.bossNote = bossNote;
    }

    public BossNotePacket(RegistryFriendlyByteBuf buf){
        this.bossNote = CODEC.decode(buf);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf){
        CODEC.encode(buf, this.bossNote);
    }

    @Override
    public void clientAction(IPayloadContext context){
        BossClientPackets.showBossNote(this.bossNote);
    }

    @Override
    public void serverAction(IPayloadContext context){

    }

}
