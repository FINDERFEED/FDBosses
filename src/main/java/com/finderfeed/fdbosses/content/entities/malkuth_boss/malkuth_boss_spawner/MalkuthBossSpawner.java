package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_boss_spawner;

import com.finderfeed.fdbosses.content.entities.base.BossSpawnerContextAssignable;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class MalkuthBossSpawner extends BossSpawnerEntity {

    public MalkuthBossSpawner(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public EntityType<? extends BossSpawnerContextAssignable> getBossEntityType() {
        return BossEntities.MALKUTH.get();
    }

    @Override
    public boolean canInteractWithBlockPos(BlockPos blockPos) {
        return true;
    }

    @Override
    public Component onArenaDestructionMessage() {
        return null;
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        return true;
    }
}
