package com.finderfeed.fdbosses.content.entities;

import com.finderfeed.fdbosses.init.BossConfigs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class BossDespawner<T extends Mob> {

    private T boss;
    private AABB bossEntitiesDespawnBox;
    private int bossNoTargetTime;
    private List<Class<? extends Entity>> entityTypesToDelete;

    private int currentNoTargetTime = 0;

    public BossDespawner(T boss, AABB entityDespawnBox, int bossNoTargetTime, Class<? extends Entity>... entityTypesToDelete){
        this.boss = boss;
        this.bossEntitiesDespawnBox = entityDespawnBox;
        this.bossNoTargetTime = bossNoTargetTime;
        this.entityTypesToDelete = List.of(entityTypesToDelete);
    }

    public void tick(){
        if (BossConfigs.BOSS_CONFIG.get().bossesDespawn) {
            if (!boss.isRemoved()) {
                if (boss.getTarget() != null) {
                    currentNoTargetTime = 0;
                } else {
                    currentNoTargetTime++;
                }
                if (currentNoTargetTime > bossNoTargetTime) {
                    this.removeAllEntities();
                }
            }
        }
    }

    private void removeAllEntities(){

        if (boss instanceof FDDespawnable fdDespawnable){
            if (!fdDespawnable.onFDDespawn()){
                return;
            }
        }

        boss.setRemoved(Entity.RemovalReason.DISCARDED);

        AABB box = bossEntitiesDespawnBox.move(boss.position());

        Level level = boss.level();

        var list = level.getEntitiesOfClass(Entity.class, box, (entity)->{
            var clazz = entity.getClass();
            return entityTypesToDelete.stream().anyMatch(clazz::isAssignableFrom);
        });

        for (var entity : list){
            entity.setRemoved(Entity.RemovalReason.DISCARDED);
        }

    }

    public T getBoss() {
        return boss;
    }

    public AABB getBossEntitiesDespawnBox() {
        return bossEntitiesDespawnBox;
    }

    public int getBossNoTargetTime() {
        return bossNoTargetTime;
    }

}
