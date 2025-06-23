package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_chain;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.UUID;

public class MalkuthChainEntity extends LivingEntity implements AutoSerializable {

    private static final EntityDataAccessor<Integer> MALKUTH_ID = SynchedEntityData.defineId(MalkuthChainEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<MalkuthAttackType> MALKUTH_ATTACK_TYPE = SynchedEntityData.defineId(MalkuthChainEntity.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());

    @SerializableField
    private MalkuthAttackType malkuthAttackType;

    @SerializableField
    private UUID malkuthUUID;

    @SerializableField
    private UUID targetUUID;

    @SerializableField
    private int pullTime;

    @SerializableField
    private int catchTime;

    @SerializableField
    private Vec3 chainPullToPos;

    @SerializableField
    private Vec3 startingPos;

    public static MalkuthChainEntity summon(Level level, MalkuthEntity owner, MalkuthAttackType malkuthAttackType, Vec3 chainPullToPos, LivingEntity target, int pullTime, int catchTime){
        MalkuthChainEntity chainEntity = new MalkuthChainEntity(BossEntities.MALKUTH_CHAIN.get(), level);

        chainEntity.setMalkuthAttackType(malkuthAttackType);

        chainEntity.targetUUID = target.getUUID();

        chainEntity.chainPullToPos = chainPullToPos;

        chainEntity.startingPos = chainEntity.getTargetAttachmentPos(target);

        chainEntity.setPos(chainPullToPos);

        chainEntity.pullTime = pullTime;

        chainEntity.catchTime = catchTime;

        chainEntity.malkuthUUID = owner.getUUID();

        level.addFreshEntity(chainEntity);

        return chainEntity;
    }

    public MalkuthChainEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            MalkuthEntity malkuthEntity = this.getMalkuth();

            if (malkuthEntity != null){
                this.entityData.set(MALKUTH_ID, malkuthEntity.getId());
            }

            if (malkuthEntity == null || this.getTarget() == null){
                this.setRemoved(RemovalReason.DISCARDED);
            }

            this.moveToTargetEndPoint();
        }else{

        }
    }

    private void moveToTargetEndPoint(){

        if (tickCount > this.catchTime && tickCount - catchTime <= this.pullTime) {

            float p = (this.tickCount - catchTime) / (float) this.pullTime;

            Vec3 targetPoint = FDMathUtil.interpolateVectors(startingPos, chainPullToPos,p);

            Vec3 current = this.position();

            Vec3 deltaMovement = targetPoint.subtract(current);

            this.setDeltaMovement(deltaMovement);

            LivingEntity target = this.getTarget();
            if (target != null && !this.hasPassenger(target)){
                target.startRiding(this,true);
            }

        }else{
            if (this.tickCount <= catchTime){
                LivingEntity target = this.getTarget();
                if (target == null) return;

                Vec3 targetAttachmentPos = this.getTargetAttachmentPos(target);

                float p = this.tickCount / (float) this.pullTime;

                Vec3 targetPoint = FDMathUtil.interpolateVectors(chainPullToPos,targetAttachmentPos,p);

                Vec3 current = this.position();

                Vec3 deltaMovement = targetPoint.subtract(current);

                this.setDeltaMovement(deltaMovement);

                if (tickCount == catchTime){
                    this.startingPos = targetAttachmentPos;
                    this.setPos(targetAttachmentPos);
                }

            }else {
                this.setDeltaMovement(Vec3.ZERO);
            }
        }
    }



    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    public Vec3 getTargetAttachmentPos(LivingEntity target){
        return target.position().add(0,target.getBbHeight()/2 - 1,0);
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity entity) {
        return this.position().add(0,-entity.getBbHeight()/2 + 0.25,0);
    }

    public LivingEntity getTarget(){
        if (targetUUID != null && !level().isClientSide){
            if (((ServerLevel)level()).getEntity(targetUUID) instanceof LivingEntity livingEntity){
                return livingEntity;
            }
        }
        return null;
    }

    public MalkuthEntity getMalkuth(){
        if (!level().isClientSide){
            if (malkuthUUID != null){
                if (((ServerLevel)level()).getEntity(malkuthUUID) instanceof MalkuthEntity malkuthEntity){
                    return malkuthEntity;
                }else{
                    return null;
                }
            }else{
                return null;
            }
        }else{
            if (level().getEntity(this.entityData.get(MALKUTH_ID)) instanceof MalkuthEntity malkuthEntity){
                return malkuthEntity;
            }else{
                return null;
            }
        }
    }

    protected Vec3 getMalkuthHandPos(float pticks){

        MalkuthEntity malkuthEntity = this.getMalkuth();

        if (malkuthEntity != null){

            String name = this.entityData.get(MALKUTH_ATTACK_TYPE).isFire() ? "fire_sword_place" : "ice_sword_place";

            Matrix4f mt = malkuthEntity.getModelPartTransformation(malkuthEntity, name, MalkuthEntity.getClientModel(), pticks);

            Vector3f p = mt.transformPosition(new Vector3f());

            Vec3 mpos = malkuthEntity.getPosition(pticks);

            return new Vec3(
                    p.x + mpos.x,
                    p.y + mpos.y,
                    p.z + mpos.z
            );

        }

        return null;
    }

    public void setMalkuthAttackType(MalkuthAttackType malkuthAttackType) {
        this.malkuthAttackType = malkuthAttackType;
        this.entityData.set(MALKUTH_ATTACK_TYPE, malkuthAttackType);
    }

    private void setCatchTime(int catchTime){
        this.catchTime = catchTime;
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.LEFT;
    }

    @Override
    public boolean hurt(DamageSource src, float p_21017_) {
        if (!src.is(DamageTypes.GENERIC_KILL)){
            return false;
        }
        return super.hurt(src, p_21017_);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot p_21127_) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot p_21036_, ItemStack p_21037_) {

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MALKUTH_ID, -1);
        builder.define(MALKUTH_ATTACK_TYPE, MalkuthAttackType.FIRE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
        this.setMalkuthAttackType(this.malkuthAttackType);
        this.setCatchTime(this.catchTime);
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        return true;
    }


    @Override
    public void push(double p_20286_, double p_20287_, double p_20288_) {

    }

    @Override
    public void push(Entity p_21294_) {

    }

    @Override
    public void push(Vec3 p_347665_) {

    }

    @Override
    protected void pushEntities() {

    }

}
