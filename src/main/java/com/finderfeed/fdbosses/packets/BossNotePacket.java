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
    private final BossNote.Boss boss;

    public BossNotePacket(BossNote bossNote, BossNote.Boss boss){
        this.bossNote = bossNote;
        this.boss = boss;
    }

    public BossNotePacket(RegistryFriendlyByteBuf buf){
        this.bossNote = CODEC.decode(buf);
        this.boss = buf.readEnum(BossNote.Boss.class);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf){
        CODEC.encode(buf, this.bossNote);
        buf.writeEnum(this.boss);
    }

    @Override
    public void clientAction(IPayloadContext context){
        BossClientPackets.showBossNote(this.bossNote, this.boss);
    }

    @Override
    public void serverAction(IPayloadContext context){

    }

}
