package com.finderfeed.fdbosses.content.entities.base;

public interface BossSpawnerContextAssignable {

    void setSpawnedBy(BossSpawnerEntity bossSpawnerEntity);

    BossSpawnerEntity getSpawner();

}
