package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_fireball;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdlib.util.FDProjectile;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class MalkuthFireball extends FDProjectile {

    public static final EntityDataAccessor<MalkuthAttackType> ATTACK_TYPE = SynchedEntityData.defineId(MalkuthFireball.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());


    public MalkuthFireball(EntityType<? extends FDProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > 1000){
            this.remove(RemovalReason.DISCARDED);
        }

        if (level().isClientSide){
            this.particles();
        }

    }

    private void particles(){

        Vec3 pos = this.position().add(0, this.getBbHeight()  / 2, 0);

        for (int i = 0; i < 100; i ++){

            Vector3f color = MalkuthEntity.getAndRandomizeColor(this.getAttackType(), random);

            BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                    .color(color.x,color.y,color.z)
                    .scalingOptions(0,0,10 + random.nextInt(4))
                    .brightness(1)
                    .build();

            float rndOut = 0.5f + random.nextFloat() * 0.75f;

            Vec3 rndOffs = new Vec3(
                    random.nextFloat() * 2 - 1,
                    random.nextFloat() * 2 - 1,
                    random.nextFloat() * 2 - 1
            ).normalize().multiply(rndOut,rndOut,rndOut);

            Vec3 ppos = pos.add(rndOffs);

            level().addParticle(ballParticleOptions, true, ppos.x,ppos.y,ppos.z,0,0,0);

        }

    }

    public MalkuthAttackType getAttackType(){
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(MalkuthAttackType type){
        this.entityData.set(ATTACK_TYPE,type);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_TYPE, MalkuthAttackType.ICE);
    }


    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("mtype", this.entityData.get(ATTACK_TYPE).name());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("mtype")) {
            this.entityData.set(ATTACK_TYPE, MalkuthAttackType.valueOf(tag.getString("mtype")));
        }
    }


}
