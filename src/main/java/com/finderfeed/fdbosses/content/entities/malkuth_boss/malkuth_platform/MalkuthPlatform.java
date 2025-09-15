package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_platform;

import com.finderfeed.fdbosses.client.particles.stripe_particle.StripeParticleOptions;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class MalkuthPlatform extends FDEntity {

    public static final EntityDataAccessor<Boolean> CAN_BE_COLLIDED_WITH = SynchedEntityData.defineId(MalkuthPlatform.class, EntityDataSerializers.BOOLEAN);

    public MalkuthPlatform(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            if (tickCount > 20){
                this.entityData.set(CAN_BE_COLLIDED_WITH, true);
            }
        }else{
            if (tickCount > 40) {

                if (tickCount % 10 == 0) {
                    Vector3f colFire = MalkuthEntity.getMalkuthAttackPreparationParticleColor(MalkuthAttackType.FIRE);

                    FDColor fireColorStart = new FDColor(colFire.x, colFire.y - random.nextFloat() * 0.1f - 0.3f, colFire.z, 0.5f);
                    FDColor fireColor = new FDColor(colFire.x, colFire.y + random.nextFloat() * 0.1f, colFire.z, 1f);

                    StripeParticleOptions stripeParticleOptions = StripeParticleOptions.createHorizontalCircling(fireColorStart, fireColor, new Vec3(0.01f, 1, 0),
                            random.nextFloat() * FDMathUtil.FPI * 2, 0.2f, 20, 50, 6, 4, 2, 0.5f, false, false);

                    level().addParticle(stripeParticleOptions, true, this.getX(),this.getY() - 6, this.getZ(),0,0,0);
                }
            }
        }
    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
    }

    @Override
    public void onClientRemoval() {
        super.onClientRemoval();
        for (int i = 0; i < 150; i ++){

            Vector3f color = MalkuthEntity.getAndRandomizeColor(MalkuthAttackType.FIRE,random);

            BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                    .color(color.x,color.y,color.z)
                    .scalingOptions(0,0,20)
                    .size(0.25f + random.nextFloat() * 0.1f)
                    .friction(0.8f)
                    .brightness(2)
                    .build();

            float p = FDEasings.easeOut(random.nextFloat());

            float h = p * 6;

            float v = p * 4;

            Vec3 offs = new Vec3(v,0,0).yRot(random.nextFloat() * FDMathUtil.FPI * 2);


            Vec3 ppos = this.position()
                    .add(offs.x,-6 + h,offs.z);


            level().addParticle(ballParticleOptions,ppos.x,ppos.y,ppos.z,
                    random.nextFloat() - 0.5,
                    random.nextFloat() - 0.5,
                    random.nextFloat() - 0.5
            );

        }
    }

    @Override
    public void kill() {
        super.kill();
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
        return this.entityData.get(CAN_BE_COLLIDED_WITH);
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
    protected void defineSynchedData() {
        this.entityData.define(CAN_BE_COLLIDED_WITH, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        this.entityData.set(CAN_BE_COLLIDED_WITH, tag.getBoolean("can_collide"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("can_collide", this.entityData.get(CAN_BE_COLLIDED_WITH));
    }
}
