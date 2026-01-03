package com.finderfeed.fdbosses.content.entities.base;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.packets.OpenBossDossierPacket;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public abstract class BossSpawnerEntity extends FDEntity {

    public static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(BossSpawnerEntity.class, EntityDataSerializers.BOOLEAN);

    public BossSpawnerEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {

        if (!this.isActive()){
            return InteractionResult.PASS;
        }

        if (hand == InteractionHand.MAIN_HAND && this.isActive()){
            if (!level().isClientSide) {
                PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenBossDossierPacket(this));
            }
            return InteractionResult.SUCCESS;
        }

        return super.interact(player, hand);
    }

    public void spawn(){
        if (this.isActive()){

            PacketDistributor.sendToPlayersTrackingEntity(this, new ForceDossierClosePacket());

            var type = this.getBossEntityType();

            var entity = type.create(level());

            entity.setYRot(0);
            entity.setYBodyRot(0);
            entity.setYHeadRot(0);

            entity.setSpawnedBy(this);

            entity.setPos(this.position());

            level().addFreshEntity(entity);

            this.setActive(false);

        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    public void setActive(boolean state){
        this.entityData.set(ACTIVE, state);
    }

    public boolean isActive(){
        return this.entityData.get(ACTIVE);
    }

    public abstract EntityType<? extends BossSpawnerContextAssignable> getBossEntityType();

    public abstract Vec3 getPlayerItemsDropPosition(ServerPlayer player, Vec3 deathPosition);

    @Override
    public boolean hurt(DamageSource src, float damage) {
        return src.is(DamageTypes.GENERIC_KILL) || src.is(DamageTypes.FELL_OUT_OF_WORLD);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ACTIVE,true);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("active")) {
            this.setActive(tag.getBoolean("active"));
        }else{
            this.setActive(true);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("active", this.isActive());
    }

    public abstract boolean canInteractWithBlockPos(BlockPos blockPos);

    public abstract Component onArenaDestructionMessage();

}
