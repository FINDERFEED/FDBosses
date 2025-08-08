package com.finderfeed.fdbosses.content.entities.base;

import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimatedObject;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.ModelSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.entity_model_system.EntityModelSystem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class BossMonsterMob extends Monster implements AnimatedObject {

    private EntityModelSystem<?> modelSystem;

    public BossMonsterMob(EntityType<? extends Monster> etype, Level level) {
        super(etype, level);
        modelSystem = EntityModelSystem.create(this);
    }

    @Override
    public void tick() {
        super.tick();
        this.tickModelSystem();
    }

    @Override
    public ModelSystem getModelSystem() {
        return this.modelSystem;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.modelSystem.asServerside().syncToPlayer(player);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.modelSystem.saveAttachments(this.level().registryAccess(), tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.modelSystem.loadAttachments(this.level().registryAccess(), tag);
    }

}
