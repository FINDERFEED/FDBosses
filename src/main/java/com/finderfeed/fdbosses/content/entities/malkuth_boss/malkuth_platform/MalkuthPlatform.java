package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_platform;

import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class MalkuthPlatform extends Entity {



    public MalkuthPlatform(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
//        this.setPos(this.position().add(this.getDeltaMovement()));

        if (!level().isClientSide){
            double s = Math.sin(level().getGameTime() / 100f * FDMathUtil.FPI * 2);
            this.setDeltaMovement(0,s * 0.2f,0);

        }
        for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0,0.1,0))){
            Vec3 m = entity.getDeltaMovement();
//            entity.setPos(entity.getX(),this.getY() + this.getBbHeight() + 0.2f,entity.getZ());
//            entity.setDeltaMovement(m.add(0,0.1f,0));

        }

    }

//    @Override
//    protected void pushEntities() {
//
//    }

    @Override
    public void push(double p_20286_, double p_20287_, double p_20288_) {

    }

    @Override
    public void push(Vec3 p_347665_) {

    }

    @Override
    public void push(Entity p_20293_) {

    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }


//    @Override
//    public HumanoidArm getMainArm() {
//        return HumanoidArm.LEFT;
//    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
//        super.defineSynchedData(builder);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {

    }

//    @Override
//    public Iterable<ItemStack> getArmorSlots() {
//        return new ArrayList<>();
//    }
//
//    @Override
//    public ItemStack getItemBySlot(EquipmentSlot p_21127_) {
//        return ItemStack.EMPTY;
//    }
//
//    @Override
//    public void setItemSlot(EquipmentSlot p_21036_, ItemStack p_21037_) {
//
//    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {

    }
}
