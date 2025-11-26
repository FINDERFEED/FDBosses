package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.content.entities.base.BossSpawnerContextAssignable;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GeburahBossSpawner extends BossSpawnerEntity {

    public GeburahBossSpawner(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    public EntityType<? extends BossSpawnerContextAssignable> getBossEntityType() {
        return BossEntities.GEBURAH.get();
    }

    @Override
    public Vec3 getPlayerItemsDropPosition(Vec3 deathPosition) {
        return deathPosition;
    }

    @Override
    public boolean canInteractWithBlockPos(BlockPos blockPos) {
        return true;
    }

    @Override
    public Component onArenaDestructionMessage() {
        return Component.translatable("fdbosses.word.tried_to_break_arena");
    }
}
