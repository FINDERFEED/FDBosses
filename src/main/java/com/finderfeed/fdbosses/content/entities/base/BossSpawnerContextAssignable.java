package com.finderfeed.fdbosses.content.entities.base;

import net.minecraft.world.phys.Vec3;

public interface BossSpawnerContextAssignable {

    void setSpawnedBy(BossSpawnerEntity bossSpawnerEntity);

    void setSpawnPosition(Vec3 spawnPosition);

    BossSpawnerEntity getSpawner();

}
