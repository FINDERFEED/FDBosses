package com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity;


import com.finderfeed.fdbosses.init.BossEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class EarthShatterEntity extends Entity {

    public static final EntityDataAccessor<BlockState> STATE = SynchedEntityData.defineId(EarthShatterEntity.class, EntityDataSerializers.BLOCK_STATE);

    public EarthShatterSettings settings;

    public EarthShatterEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public static EarthShatterEntity summon(Level level, BlockPos pos,EarthShatterSettings settings){
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return null;
        if (!level.getEntitiesOfClass(EarthShatterEntity.class,new AABB(
                pos.getX() + 0.1,
                pos.getY(),
                pos.getZ() + 0.1,
                pos.getX() + 0.9,
                pos.getY() + 0.9,
                pos.getZ() + 0.9
        )).isEmpty()){
            return null;
        }

        EarthShatterEntity entity = new EarthShatterEntity(BossEntities.EARTH_SHATTER.get(),level);

        entity.setPos(pos.getX() + 0.5,pos.getY(),pos.getZ() + 0.5);
        entity.settings = settings;
        entity.setBlockState(state);

        level.addFreshEntity(entity);

        return entity;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > settings.getLifetime()){
            this.remove(RemovalReason.DISCARDED);
        }

    }

    public BlockState getBlockState(){
        return this.entityData.get(STATE);
    }

    public void setBlockState(BlockState state){
        this.entityData.set(STATE,state);
    }

    public Vec3 getShatterDirection(){
        return this.settings.direction;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(STATE, Blocks.STONE.defaultBlockState());
    }

    @Override
    public void sendPairingData(ServerPlayer player, Consumer<CustomPacketPayload> bundleBuilder) {
        super.sendPairingData(player, bundleBuilder);
        bundleBuilder.accept(new EarthShatterEntitySpawnPacket(this,this.settings));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.settings = new EarthShatterSettings();
        this.settings.autoLoad("settings",tag);
        this.setBlockState(NbtUtils.readBlockState(level().holderLookup(Registries.BLOCK),tag));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        this.settings.autoSave("settings",tag);
        tag.put("state", NbtUtils.writeBlockState(this.getBlockState()));
    }


    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return super.shouldRender(p_20296_, p_20297_, p_20298_);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        return true;
    }
}
