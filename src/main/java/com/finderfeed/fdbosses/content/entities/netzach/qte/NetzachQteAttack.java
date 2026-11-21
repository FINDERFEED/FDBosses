package com.finderfeed.fdbosses.content.entities.netzach.qte;

import com.finderfeed.fdbosses.content.entities.FDOwnableEntity;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.init.FDEDataSerializers;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NetzachQteAttack extends FDOwnableEntity {

    public static final EntityDataAccessor<Vec3> OFFSET = SynchedEntityData.defineId(NetzachQteAttack.class, FDEDataSerializers.VEC3.get());

    public static final int FADE_OUT_TIME = 5;
    public static final int FADE_IN_TIME = 5;

    public static void summon(LivingEntity target, Vec3 offset){
        offset = offset.multiply(1,0,1);

        NetzachQteAttack attack = new NetzachQteAttack(BossEntities.NETZACH_QTE.get(), target.level());
        attack.setPos(target.position().add(offset));
        attack.setOffset(offset);
        attack.setOwner(target);
        attack.lookAt(EntityAnchorArgument.Anchor.FEET, attack.position().add(offset.scale(-100)));
        target.level().addFreshEntity(attack);
    }

    public NetzachQteAttack(EntityType<?> type, Level level) {
        super(type, level);
        this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.NETZACH_QTE)
                        .setTransitionTime(0)
                .build());
    }

    @Override
    public void tick() {
        super.tick();
        var owner = this.getOwner();

        if (owner == null){
            this.setRemoved(RemovalReason.DISCARDED);
            return;
        }

        Vec3 pos = owner.position().add(this.getOffset());
        this.moveTo(pos);

        if (!level().isClientSide){
            if (!owner.isAlive() || this.tickCount > BossAnims.NETZACH_QTE.get().getAnimTime() + FADE_OUT_TIME){
                this.setRemoved(RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    public boolean syncsOwner() {
        return true;
    }

    public void setOffset(Vec3 offset){
        this.getEntityData().set(OFFSET, offset);
    }

    public Vec3 getOffset(){
        return this.getEntityData().get(OFFSET);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {
        super.defineSynchedData(p_326003_);
        p_326003_.define(OFFSET, new Vec3(1,1,0));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        Vec3 offset = this.getOffset();
        tag.putDouble("offsetX", offset.x);
        tag.putDouble("offsetY", offset.y);
        tag.putDouble("offsetZ", offset.z);
    }

}
