package com.finderfeed.fdbosses.content.entities;

import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.UUID;

public abstract class FDOwnableEntity extends FDEntity {

    public static final EntityDataAccessor<Integer> OWNER = SynchedEntityData.defineId(FDOwnableEntity.class, EntityDataSerializers.INT);

    private UUID owner;

    public FDOwnableEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide){
            if (this.syncsOwner()){
                Entity owner = this.getOwner();
                if (owner != null){
                    this.entityData.set(OWNER, owner.getId());
                }
            }
        }
    }

    public Entity getOwner(){
        if (!level().isClientSide){
            ServerLevel serverLevel = (ServerLevel) level();
            if (this.owner != null){
                Entity entity = serverLevel.getEntity(this.owner);
                return entity;
            }
        }else{
            if (level().getEntity(this.entityData.get(OWNER)) instanceof Entity entity){
                return entity;
            }
        }
        return null;
    }

    public void setOwner(Entity owner) {
        this.owner = owner.getUUID();
        this.entityData.set(OWNER, owner.getId());
    }

    public boolean syncsOwner(){
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {
        p_326003_.define(OWNER, -1);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.owner != null){
            tag.putUUID("owner", this.owner);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("owner")){
            this.owner = tag.getUUID("owner");
        }
    }

}
