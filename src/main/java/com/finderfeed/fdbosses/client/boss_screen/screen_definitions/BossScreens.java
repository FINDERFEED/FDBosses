package com.finderfeed.fdbosses.client.boss_screen.screen_definitions;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.boss_screen.BaseBossScreen;
import com.finderfeed.fdbosses.client.boss_screen.ChesedBossScreen;
import com.finderfeed.fdbosses.client.boss_screen.GeburahBossScreen;
import com.finderfeed.fdbosses.client.boss_screen.MalkuthBossScreen;
import com.finderfeed.fdbosses.init.BossEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

public class BossScreens {

    private static final HashMap<EntityType<?>, BiFunction<Integer, List<Item>, BaseBossScreen>> BOSS_SCREEN_OPTIONS = new HashMap<>();

    public static final BiFunction<Integer, List<Item>, BaseBossScreen> CHESED = register(BossEntities.CHESED.get(),(id, drops)->{
        return new ChesedBossScreen(id, drops, new BossScreenOptions()
                .setBossDescription(Component.translatable("fdbosses.bosses.description.chesed"))
                .setEntityType(BossEntities.CHESED.get())
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/chesed/mightiness.png"),
                        Component.translatable("fdbosses.skills.chesed.mightiness"),
                        null,
                        Component.translatable("fdbosses.skills.chesed.mightiness_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/mob_effect/shocked.png"),
                        Component.translatable("fdbosses.skills.chesed.electrified_air"),
                        Component.translatable("fdbosses.skills.chesed.electrified_air_stats"),
                        Component.translatable("fdbosses.skills.chesed.electrified_air_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/chesed/monoliths.png"),
                        Component.translatable("fdbosses.skills.chesed.monoliths"),
                        Component.translatable("fdbosses.skills.chesed.monoliths_stats"),
                        Component.translatable("fdbosses.skills.chesed.monoliths_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/chesed/crystals.png"),
                        Component.translatable("fdbosses.skills.chesed.crystals"),
                        Component.translatable("fdbosses.skills.chesed.crystals_stats"),
                        Component.translatable("fdbosses.skills.chesed.crystals_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/chesed/lightning_ray.png"),
                        Component.translatable("fdbosses.skills.chesed.lightning_ray"),
                        Component.translatable("fdbosses.skills.chesed.lightning_ray_stats"),
                        Component.translatable("fdbosses.skills.chesed.lightning_ray_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/chesed/blocks.png"),
                        Component.translatable("fdbosses.skills.chesed.blocks"),
                        Component.translatable("fdbosses.skills.chesed.blocks_stats"),
                        Component.translatable("fdbosses.skills.chesed.blocks_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/chesed/earthquake.png"),
                        Component.translatable("fdbosses.skills.chesed.earthquake"),
                        Component.translatable("fdbosses.skills.chesed.earthquake_stats"),
                        Component.translatable("fdbosses.skills.chesed.earthquake_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/chesed/espheres.png"),
                        Component.translatable("fdbosses.skills.chesed.espheres"),
                        Component.translatable("fdbosses.skills.chesed.espheres_stats"),
                        Component.translatable("fdbosses.skills.chesed.espheres_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/chesed/roll.png"),
                        Component.translatable("fdbosses.skills.chesed.roll"),
                        Component.translatable("fdbosses.skills.chesed.roll_stats"),
                        Component.translatable("fdbosses.skills.chesed.roll_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/chesed/rockfall.png"),
                        Component.translatable("fdbosses.skills.chesed.rockfall"),
                        Component.translatable("fdbosses.skills.chesed.rockfall_stats"),
                        Component.translatable("fdbosses.skills.chesed.rockfall_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/chesed/kinetic_field.png"),
                        Component.translatable("fdbosses.skills.chesed.kinetic_field"),
                        Component.translatable("fdbosses.skills.chesed.kinetic_field_stats"),
                        Component.translatable("fdbosses.skills.chesed.kinetic_field_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/chesed/final.png"),
                        Component.translatable("fdbosses.skills.chesed.final"),
                        Component.translatable("fdbosses.skills.chesed.final_stats"),
                        Component.translatable("fdbosses.skills.chesed.final_description")
                ))
                .setTLDRComponent(Component.translatable("fdbosses.tldr.chesed"))
        );
    });

    public static final BiFunction<Integer, List<Item>, BaseBossScreen> MALKUTH_BOSS_SCREEN = register(BossEntities.MALKUTH.get(), (id, drops) -> {
        return new MalkuthBossScreen(id, drops, new BossScreenOptions()
                .setEntityType(BossEntities.MALKUTH.get())
                .setBossDescription(Component.translatable("fdbosses.bosses.description.malkuth"))
                .setTLDRComponent(Component.translatable("fdbosses.tldr.malkuth"))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/malkuth/divine_armor.png"),
                        Component.translatable("fdbosses.skills.malkuth.armor"),
                        null,
                        Component.translatable("fdbosses.skills.malkuth.armor_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/malkuth/fire_and_ice.png"),
                        Component.translatable("fdbosses.skills.malkuth.fire_and_ice"),
                        Component.translatable("fdbosses.skills.malkuth.fire_and_ice_stats"),
                        Component.translatable("fdbosses.skills.malkuth.fire_and_ice_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/mob_effect/knight.png"),
                        Component.translatable("fdbosses.skills.malkuth.knights_duty"),
                        null,
                        Component.translatable("fdbosses.skills.malkuth.knights_duty_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/malkuth/fair_duel.png"),
                        Component.translatable("fdbosses.skills.malkuth.fair_duel"),
                        null,
                        Component.translatable("fdbosses.skills.malkuth.fair_duel_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/malkuth/slashes.png"),
                        Component.translatable("fdbosses.skills.malkuth.slash"),
                        Component.translatable("fdbosses.skills.malkuth.slash_stats"),
                        Component.translatable("fdbosses.skills.malkuth.slash_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/malkuth/cannonade.png"),
                        Component.translatable("fdbosses.skills.malkuth.cannons"),
                        Component.translatable("fdbosses.skills.malkuth.cannons_stats"),
                        Component.translatable("fdbosses.skills.malkuth.cannons_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/malkuth/arcslash.png"),
                        Component.translatable("fdbosses.skills.malkuth.arcslash"),
                        Component.translatable("fdbosses.skills.malkuth.arcslash_stats"),
                        Component.translatable("fdbosses.skills.malkuth.arcslash_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/malkuth/boulder_volley.png"),
                        Component.translatable("fdbosses.skills.malkuth.side_rocks"),
                        Component.translatable("fdbosses.skills.malkuth.side_rocks_stats"),
                        Component.translatable("fdbosses.skills.malkuth.side_rocks_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/malkuth/impaling_doom.png"),
                        Component.translatable("fdbosses.skills.malkuth.impaling_doom"),
                        Component.translatable("fdbosses.skills.malkuth.impaling_doom_stats"),
                        Component.translatable("fdbosses.skills.malkuth.impaling_doom_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/malkuth/earthshatter.png"),
                        Component.translatable("fdbosses.skills.malkuth.earthshatter"),
                        Component.translatable("fdbosses.skills.malkuth.earthshatter_stats"),
                        Component.translatable("fdbosses.skills.malkuth.earthshatter_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/malkuth/chainpunch.png"),
                        Component.translatable("fdbosses.skills.malkuth.chainpunch"),
                        Component.translatable("fdbosses.skills.malkuth.chainpunch_stats"),
                        Component.translatable("fdbosses.skills.malkuth.chainpunch_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/malkuth/tsars_wrath.png"),
                        Component.translatable("fdbosses.skills.malkuth.tsars_wrath"),
                        Component.translatable("fdbosses.skills.malkuth.tsars_wrath_stats"),
                        Component.translatable("fdbosses.skills.malkuth.tsars_wrath_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/malkuth/hellshaper.png"),
                        Component.translatable("fdbosses.skills.malkuth.hellshaper"),
                        Component.translatable("fdbosses.skills.malkuth.hellshaper_stats"),
                        Component.translatable("fdbosses.skills.malkuth.hellshaper_description")
                ))
        );
    });




    public static final BiFunction<Integer, List<Item>, BaseBossScreen> GEBURAH_BOSS_SCREEN = register(BossEntities.GEBURAH.get(), (id, drops) -> {
        return new GeburahBossScreen(id, drops, new BossScreenOptions()
                .setEntityType(BossEntities.GEBURAH.get())
                .setBossDescription(Component.translatable("fdbosses.bosses.description.geburah"))
                .setTLDRComponent(Component.translatable("fdbosses.tldr.geburah"))

                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/geburah/sinner.png"),
                        Component.translatable("fdbosses.skills.sinner.sinner"),
                        Component.translatable("fdbosses.skills.sinner.sinner_stats"),
                        Component.translatable("fdbosses.skills.sinner.sinner_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/geburah/birds_of_justice.png"),
                        Component.translatable("fdbosses.skills.geburah.birds_of_justice"),
                        Component.translatable("fdbosses.skills.geburah.birds_of_justice_stats"),
                        Component.translatable("fdbosses.skills.geburah.birds_of_justice_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/geburah/traps.png"),
                        Component.translatable("fdbosses.skills.geburah.traps"),
                        null,
                        Component.translatable("fdbosses.skills.geburah.traps_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/geburah/roundstrike.png"),
                        Component.translatable("fdbosses.skills.geburah.roundstrike"),
                        Component.translatable("fdbosses.skills.geburah.roundstrike_stats"),
                        Component.translatable("fdbosses.skills.geburah.roundstrike_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/geburah/ray.png"),
                        Component.translatable("fdbosses.skills.geburah.ray"),
                        Component.translatable("fdbosses.skills.geburah.ray_stats"),
                        Component.translatable("fdbosses.skills.geburah.ray_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/geburah/earthquake.png"),
                        Component.translatable("fdbosses.skills.geburah.earthquake"),
                        Component.translatable("fdbosses.skills.geburah.earthquake_stats"),
                        Component.translatable("fdbosses.skills.geburah.earthquake_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/geburah/laser_beam.png"),
                        Component.translatable("fdbosses.skills.geburah.laser_beam"),
                        Component.translatable("fdbosses.skills.geburah.laser_beam_stats"),
                        Component.translatable("fdbosses.skills.geburah.laser_beam_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/geburah/round_and_round_laser.png"),
                        Component.translatable("fdbosses.skills.geburah.round_and_round_laser"),
                        Component.translatable("fdbosses.skills.geburah.round_and_round_laser_stats"),
                        Component.translatable("fdbosses.skills.geburah.round_and_round_laser_description")
                ))

                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/geburah/justice_hammer.png"),
                        Component.translatable("fdbosses.skills.geburah.justice_hammer"),
                        Component.translatable("fdbosses.skills.geburah.justice_hammer_stats"),
                        Component.translatable("fdbosses.skills.geburah.justice_hammer_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/gui/skills/geburah/divine_judgement.png"),
                        Component.translatable("fdbosses.skills.geburah.divine_judgement"),
                        Component.translatable("fdbosses.skills.geburah.divine_judgement_stats"),
                        Component.translatable("fdbosses.skills.geburah.divine_judgement_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/entities/geburah/screen_sin/sin_crystal_of_sin.png"),
                        Component.translatable("fdbosses.skills.geburah.sin_crystal_of_sin"),
                        null,
                        Component.translatable("fdbosses.skills.geburah.sin_crystal_of_sin_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/entities/geburah/screen_sin/sin_jumping_sin.png"),
                        Component.translatable("fdbosses.skills.geburah.sin_jumping_sin"),
                        null,
                        Component.translatable("fdbosses.skills.geburah.sin_jumping_sin_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/entities/geburah/screen_sin/sin_kill_entity_sin.png"),
                        Component.translatable("fdbosses.skills.geburah.sin_kill_entity_sin"),
                        null,
                        Component.translatable("fdbosses.skills.geburah.sin_kill_entity_sin_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/entities/geburah/screen_sin/sin_move_clockwise_sin.png"),
                        Component.translatable("fdbosses.skills.geburah.sin_move_clockwise_sin"),
                        null,
                        Component.translatable("fdbosses.skills.geburah.sin_move_clockwise_sin_description")
                ))
                .addSkill(new BossInfo(
                        FDBosses.location("textures/entities/geburah/screen_sin/sin_pressed_too_many_buttons.png"),
                        Component.translatable("fdbosses.skills.geburah.sin_pressed_too_many_buttons"),
                        null,
                        Component.translatable("fdbosses.skills.geburah.sin_pressed_too_many_buttons_description")
                ))


        );
    });

    public static BaseBossScreen getScreen(EntityType<?> type, int bossSpawnerId, List<Item> drops){
        if (BOSS_SCREEN_OPTIONS.containsKey(type)){
            return BOSS_SCREEN_OPTIONS.get(type).apply(bossSpawnerId, drops);
        }
        return null;
    }

    public static BiFunction<Integer, List<Item>, BaseBossScreen> register(EntityType<?> entityType, BiFunction<Integer, List<Item>, BaseBossScreen> factory){
        BOSS_SCREEN_OPTIONS.put(entityType,factory);
        return factory;
    }

}
