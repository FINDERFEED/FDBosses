package com.finderfeed.fdbosses.content.entities.chesed_boss;

import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.particle.CircleParticleProcessor;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ChesedFireTrailEntity extends Entity implements AutoSerializable {

    @SerializableField
    private float damage = 0;

    @SerializableField
    private int lifetime = 0;

    public ChesedFireTrailEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        float radius = 1;
        if (!level().isClientSide){
            if (tickCount > lifetime){
                this.remove(RemovalReason.DISCARDED);
            }

            this.doDamage(radius,0.25f);

        }else{
            this.spawnParticles(radius,1);
        }
    }

    private void spawnParticles(float radius,int amountPerTick){
        for (int i = 0; i < amountPerTick;i++){

            Vec3 v = new Vec3(random.nextFloat() * radius,0,0).yRot(random.nextFloat() * FDMathUtil.FPI * 2);
            Vec3 ppos = this.position().add(v);
            float spm = 0.025f * random.nextFloat();
            Vec3 speed = new Vec3(0,spm,0);
            BallParticleOptions options = BallParticleOptions.builder()
                    .color(1 + random.nextInt(40), 183 + random.nextInt(60), 165 + random.nextInt(60))
                    .size(0.1f)
                    .scalingOptions(2,10,10)
                    .build();
            level().addParticle(options,ppos.x,ppos.y,ppos.z,speed.x,speed.y,speed.z);

        }
    }

    private void doDamage(float radius,float damageBoxHeight){
        var list = level().getEntitiesOfClass(LivingEntity.class,new AABB(-radius,0,-radius,radius,damageBoxHeight,radius).move(this.position()),(e)->{
            return !(e instanceof ChesedBossBuddy) && e.position().multiply(1,0,1).distanceTo(this.position().multiply(1,0,1)) < radius;
        });

        for (LivingEntity entity : list){

            entity.hurt(level().damageSources().magic(),this.damage);
        }

    }

    public static ChesedFireTrailEntity summon(Level level, Vec3 pos, float damage, int lifetime){
        ChesedFireTrailEntity fireTrailEntity = new ChesedFireTrailEntity(BossEntities.CHESED_FIRE_TRAIL.get(),level);
        fireTrailEntity.setPos(pos);
        fireTrailEntity.damage = damage;
        fireTrailEntity.lifetime = lifetime;
        level.addFreshEntity(fireTrailEntity);
        return fireTrailEntity;
    }



    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.autoLoad(tag);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        this.autoSave(tag);
    }
}
