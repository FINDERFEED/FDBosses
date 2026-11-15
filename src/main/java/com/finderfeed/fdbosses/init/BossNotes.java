package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.boss_notes.BossNote;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BossNotes {

    public static final DeferredRegister<BossNote> BOSS_NOTES = DeferredRegister.create(BossRegistries.BOSS_NOTE, FDBosses.MOD_ID);

}
