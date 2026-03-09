package com.finderfeed.fdbosses.content.entities.netzach.backtrack_entity;

import com.finderfeed.fdbosses.content.entities.FDOwnableEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.UUID;

public class BacktrackEntity extends FDOwnableEntity implements AutoSerializable {

    @SerializableField
    private int backtrackTime;

    public static void summon(LivingEntity entityToBacktrack, int backtrackTime){
        BacktrackEntity backtrackEntity = new BacktrackEntity(BossEntities.BACKTRACK_ENTITY.get(), entityToBacktrack.level());
        backtrackEntity.setPos(entityToBacktrack.position());
        backtrackEntity.backtrackTime = backtrackTime;
        backtrackEntity.setOwner(entityToBacktrack);
        backtrackEntity.setYRot(entityToBacktrack.getYRot());
        entityToBacktrack.level().addFreshEntity(backtrackEntity);
    }

    public BacktrackEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){

            var owner = this.getOwner();
            if (owner == null){
                this.setRemoved(RemovalReason.DISCARDED);
                return;
            }
            if (owner instanceof LivingEntity livingEntity) {
                if (livingEntity.isDeadOrDying()){
                    this.setRemoved(RemovalReason.DISCARDED);
                    return;
                }
                if (this.backtrackTime-- < 0) {
                    livingEntity.teleportTo((ServerLevel) this.level(), this.getX(), this.getY(), this.getZ(), new HashSet<>(), this.getYRot(),0);
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        }
    }

    @Override
    public boolean syncsOwner() {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
    }

}
