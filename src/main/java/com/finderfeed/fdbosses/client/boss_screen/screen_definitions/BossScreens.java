package com.finderfeed.fdbosses.client.boss_screen.screen_definitions;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.boss_screen.BaseBossScreen;
import com.finderfeed.fdbosses.client.boss_screen.ChesedBossScreen;
import com.finderfeed.fdbosses.init.BossEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.function.Supplier;

public class BossScreens {

    private static final HashMap<EntityType<?>, Supplier<BaseBossScreen>> BOSS_SCREEN_OPTIONS = new HashMap<>();

    public static final Supplier<BaseBossScreen> CHESED = register(BossEntities.CHESED.get(),()->{
        return new ChesedBossScreen(new BossScreenOptions()
                .setBossDescription(Component.translatable("fdbosses.bosses.description.chesed"))
                .setEntityType(BossEntities.CHESED.get())
                .addSkill(new BossSkill(
                        FDBosses.location("textures/gui/skills/chesed/mightiness.png"),
                        Component.translatable("fdbosses.skills.chesed.mightiness"),
                        null,
                        Component.translatable("fdbosses.skills.chesed.mightiness_description")
                ))
                .addSkill(new BossSkill(
                        FDBosses.location("textures/gui/skills/chesed/monoliths.png"),
                        Component.translatable("fdbosses.skills.chesed.monoliths"),
                        Component.translatable("fdbosses.skills.chesed.monoliths_stats"),
                        Component.translatable("fdbosses.skills.chesed.monoliths_description")
                ))
                .addSkill(new BossSkill(
                        FDBosses.location("textures/gui/skills/chesed/crystals.png"),
                        Component.translatable("fdbosses.skills.chesed.crystals"),
                        Component.translatable("fdbosses.skills.chesed.crystals_stats"),
                        Component.translatable("fdbosses.skills.chesed.crystals_description")
                ))
                .addSkill(new BossSkill(
                        FDBosses.location("textures/gui/skills/chesed/lightning_ray.png"),
                        Component.translatable("fdbosses.skills.chesed.lightning_ray"),
                        Component.translatable("fdbosses.skills.chesed.lightning_ray_stats"),
                        Component.translatable("fdbosses.skills.chesed.lightning_ray_description")
                ))
                .addSkill(new BossSkill(
                        FDBosses.location("textures/gui/skills/chesed/blocks.png"),
                        Component.translatable("fdbosses.skills.chesed.blocks"),
                        Component.translatable("fdbosses.skills.chesed.blocks_stats"),
                        Component.translatable("fdbosses.skills.chesed.blocks_description")
                ))
                .addSkill(new BossSkill(
                        FDBosses.location("textures/gui/skills/chesed/earthquake.png"),
                        Component.translatable("fdbosses.skills.chesed.earthquake"),
                        Component.translatable("fdbosses.skills.chesed.earthquake_stats"),
                        Component.translatable("fdbosses.skills.chesed.earthquake_description")
                ))
                .addSkill(new BossSkill(
                        FDBosses.location("textures/gui/skills/chesed/espheres.png"),
                        Component.translatable("fdbosses.skills.chesed.espheres"),
                        Component.translatable("fdbosses.skills.chesed.espheres_stats"),
                        Component.translatable("fdbosses.skills.chesed.espheres_description")
                ))
                .addSkill(new BossSkill(
                        FDBosses.location("textures/gui/skills/chesed/roll.png"),
                        Component.translatable("fdbosses.skills.chesed.roll"),
                        Component.translatable("fdbosses.skills.chesed.roll_stats"),
                        Component.translatable("fdbosses.skills.chesed.roll_description")
                ))
                .addSkill(new BossSkill(
                        FDBosses.location("textures/gui/skills/chesed/rockfall.png"),
                        Component.translatable("fdbosses.skills.chesed.rockfall"),
                        Component.translatable("fdbosses.skills.chesed.rockfall_stats"),
                        Component.translatable("fdbosses.skills.chesed.rockfall_description")
                ))
                .addSkill(new BossSkill(
                        FDBosses.location("textures/gui/skills/chesed/final.png"),
                        Component.translatable("fdbosses.skills.chesed.final"),
                        Component.translatable("fdbosses.skills.chesed.final_stats"),
                        Component.translatable("fdbosses.skills.chesed.final_description")
                ))
        );
    });

    public static Supplier<BaseBossScreen> register(EntityType<?> entityType, Supplier<BaseBossScreen> factory){
        BOSS_SCREEN_OPTIONS.put(entityType,factory);
        return factory;
    }

}
