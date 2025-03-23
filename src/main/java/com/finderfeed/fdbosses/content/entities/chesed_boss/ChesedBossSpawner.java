package com.finderfeed.fdbosses.content.entities.chesed_boss;

import com.finderfeed.fdbosses.content.entities.base.BossSpawnerContextAssignable;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ChesedBossSpawner extends BossSpawnerEntity {

    public ChesedBossSpawner(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public EntityType<? extends BossSpawnerContextAssignable> getBossEntityType() {
        return BossEntities.CHESED.get();
    }

}
