package com.finderfeed.fdbosses.client.boss_screen.screen_definitions;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.boss_screen.BaseBossScreen;
import com.finderfeed.fdbosses.client.boss_screen.ChesedBossScreen;
import com.finderfeed.fdbosses.client.boss_screen.MalkuthBossScreen;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossItems;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.function.Function;

public class BossScreens {

    private static final HashMap<EntityType<?>, Function<Integer, BaseBossScreen>> BOSS_SCREEN_OPTIONS = new HashMap<>();

    public static final Function<Integer, BaseBossScreen> CHESED = register(BossEntities.CHESED.get(),(id)->{
        return new ChesedBossScreen(id,new BossScreenOptions()
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
                //drops
                .addDrop(new BossInfo(
                        BossItems.LIGHTNING_CORE.get().getDefaultInstance(),
                        Component.translatable("fdbosses.drops.chesed.lightning_core_stats"),
                        Component.translatable("fdbosses.drops.chesed.lightning_core_description")
                ))
                .addDrop(new BossInfo(
                        BossItems.CHESED_TROPHY.get().getDefaultInstance(),
                        null,
                        Component.translatable("fdbosses.drops.chesed.trophy_description")
                ))
                .setTLDRComponent(Component.translatable("fdbosses.tldr.chesed"))
        );
    });

    public static final Function<Integer, BaseBossScreen> MALKUTH_BOSS_SCREEN = register(BossEntities.MALKUTH.get(), id -> {
        return new MalkuthBossScreen(id, new BossScreenOptions()
                .setEntityType(BossEntities.MALKUTH.get())
                .setBossDescription(Component.translatable("fdbosses.bosses.description.malkuth"))
                .setTLDRComponent(Component.translatable("fdbosses.tldr.malkuth"))
                .addSkill(new BossInfo(
                        TextureAtlas.LOCATION_BLOCKS,
                        Component.translatable("fdbosses.skills.malkuth.fire_and_ice"),
                        Component.translatable("fdbosses.skills.malkuth.fire_and_ice_stats"),
                        Component.translatable("fdbosses.skills.malkuth.fire_and_ice_description")
                ))
                .addSkill(new BossInfo(
                        TextureAtlas.LOCATION_BLOCKS,
                        Component.translatable("fdbosses.skills.malkuth.fair_duel"),
                        null,
                        Component.translatable("fdbosses.skills.malkuth.fair_duel_description")
                ))
                .addSkill(new BossInfo(
                        TextureAtlas.LOCATION_BLOCKS,
                        Component.translatable("fdbosses.skills.malkuth.slash"),
                        Component.translatable("fdbosses.skills.malkuth.slash_stats"),
                        Component.translatable("fdbosses.skills.malkuth.slash_description")
                ))
        );
    });

    public static BaseBossScreen getScreen(EntityType<?> type, int bossSpawnerId){
        if (BOSS_SCREEN_OPTIONS.containsKey(type)){
            return BOSS_SCREEN_OPTIONS.get(type).apply(bossSpawnerId);
        }
        return null;
    }

    public static Function<Integer, BaseBossScreen> register(EntityType<?> entityType, Function<Integer, BaseBossScreen> factory){
        BOSS_SCREEN_OPTIONS.put(entityType,factory);
        return factory;
    }

}
