package com.finderfeed.fdbosses.client.boss_screen.screen_definitions;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;

public class BossScreens {

    private static final HashMap<EntityType<?>, BossScreenOptions> BOSS_SCREEN_OPTIONS = new HashMap<>();

    public static final BossScreenOptions CHESED = register(BossEntities.CHESED.get(),
            new BossScreenOptions()
                    .setBossDescription(Component.translatable("fdbosses.bosses.description.chesed"))
                    .addSkill(new BossSkill(
                            FDBosses.location("textures/gui/skills/chesed/crystals.png"),
                            Component.translatable("fdbosses.skills.chesed.crystals"),
                            Component.translatable("fdbosses.skills.chesed.crystals_description")
                    ))
    );

    public static BossScreenOptions register(EntityType<?> entityType, BossScreenOptions options){
        BOSS_SCREEN_OPTIONS.put(entityType,options.setEntityType(entityType));
        return options;
    }

}
