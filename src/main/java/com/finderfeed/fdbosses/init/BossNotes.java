package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.boss_notes.BossNote;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossNotes {

    public static final DeferredRegister<BossNote> BOSS_NOTES = DeferredRegister.create(BossRegistries.BOSS_NOTE, FDBosses.MOD_ID);

    public static final Supplier<BossNote> CHESED_NOTE_MONOLITHS = BOSS_NOTES.register("chesed_note_monoliths", () -> new BossNote(Component.translatable("note.chesed_note_monoliths")));
    public static final Supplier<BossNote> CHESED_NOTE_EVADE_RAY = BOSS_NOTES.register("chesed_note_evade_ray", () -> new BossNote(Component.translatable("note.chesed_note_evade_ray")));
    public static final Supplier<BossNote> CHESED_NOTE_RUN_TO_REFLECTOR = BOSS_NOTES.register("chesed_run_to_reflector", () -> new BossNote(Component.translatable("note.chesed_run_to_reflector")));

    public static final Supplier<BossNote> MALKUTH_NOTE_FIRE_CANNONS = BOSS_NOTES.register("malkuth_fire_cannons", () -> new BossNote(Component.translatable("note.malkuth_fire_cannons")));
    public static final Supplier<BossNote> MALKUTH_NOTE_SLASH_CRYSTALS = BOSS_NOTES.register("malkuth_slash_crystals", () -> new BossNote(Component.translatable("note.malkuth_slash_crystals")));

    public static final Supplier<BossNote> GEBURAH_JUMP_SIN = BOSS_NOTES.register("geburah_jump", () -> new BossNote(Component.translatable("note.geburah_jump")));
    public static final Supplier<BossNote> GEBURAH_CLOCKMOVE_SIN = BOSS_NOTES.register("geburah_clockmove", () -> new BossNote(Component.translatable("note.geburah_clockmove")));
    public static final Supplier<BossNote> GEBURAH_BUTTONS_SIN = BOSS_NOTES.register("geburah_buttons", () -> new BossNote(Component.translatable("note.geburah_buttons")));
    public static final Supplier<BossNote> GEBURAH_CRYSTALS_SIN = BOSS_NOTES.register("geburah_crystals", () -> new BossNote(Component.translatable("note.geburah_crystals")));
    public static final Supplier<BossNote> GEBURAH_KILLING_SIN = BOSS_NOTES.register("geburah_killing", () -> new BossNote(Component.translatable("note.geburah_killing")));
    public static final Supplier<BossNote> GEBURAH_STILLNESS_SIN = BOSS_NOTES.register("geburah_stillness", () -> new BossNote(Component.translatable("note.geburah_stillness")));
    public static final Supplier<BossNote> GEBURAH_BELLS = BOSS_NOTES.register("geburah_bells", () -> new BossNote(Component.translatable("note.geburah_bells")));

}
