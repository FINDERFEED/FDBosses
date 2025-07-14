package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_platform;

import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class MalkuthPlatform extends FDEntity {

    public MalkuthPlatform(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
    }

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


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {

    }
}
