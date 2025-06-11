package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthBossBuddy;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdlib.FDHelpers;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.util.FDProjectile;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class MalkuthSlashProjectile extends FDProjectile implements AutoSerializable {

    public static final EntityDataAccessor<Float> SLASH_SIZE = SynchedEntityData.defineId(MalkuthSlashProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<MalkuthAttackType> ATTACK_TYPE = SynchedEntityData.defineId(MalkuthSlashProjectile.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());

    @SerializableField
    private float damage = 2;

    public MalkuthSlashProjectile(EntityType<? extends AbstractHurtingProjectile> type, Level level) {
        super(type, level);
    }

    public static MalkuthSlashProjectile summon(Level level, Vec3 pos, Vec3 speed, MalkuthAttackType malkuthAttackType, float damage, float slashSize){
        MalkuthSlashProjectile malkuthSlashProjectile = new MalkuthSlashProjectile(BossEntities.MALKUTH_SLASH.get(), level);
        malkuthSlashProjectile.setPos(pos);
        malkuthSlashProjectile.setDeltaMovement(speed);
        malkuthSlashProjectile.setAttackType(malkuthAttackType);
        malkuthSlashProjectile.setSlashSize(slashSize);
        malkuthSlashProjectile.damage = damage;
        level.addFreshEntity(malkuthSlashProjectile);
        return malkuthSlashProjectile;
    }

    @Override
    public void tick() {

        super.tick();
        if (!level().isClientSide){
            this.tickDamage();
        }else{
            this.tickParticles();
        }
    }

    private void tickParticles(){

        Vec3 pos = FDMathUtil.interpolateVectors(this.position(),new Vec3(xo,yo,zo),0.5f).add(0,this.getBbHeight()/2,0);
        Vec3 movement = this.getDeltaMovement();

        if (movement.equals(Vec3.ZERO)) return;

        Vec3 left = movement.cross(new Vec3(0,1,0)).normalize();

        float r;
        float g;
        float b;

        Vec3 nmovement = movement.normalize();


        float slashLeftOffset = 0.3f;

        float height = this.getSlashSize() * 0.3461538f;

        for (float i = 0; i <= this.getSlashSize(); i += slashLeftOffset){

            Vec3 offsetPos = pos.add(left.multiply(i,i,i));
            Vec3 offsetPos2 = pos.add(left.multiply(-i,-i,-i));

            float p = FDEasings.easeOut(1 - i / this.getSlashSize());

            for (float l = 0; l < movement.length(); l+= 0.3f) {

                Vec3 noffset = nmovement.multiply(height * p,height * p,height * p);

                Vec3 n = nmovement.multiply(-l,-l,-l).add(noffset).add(nmovement.reverse().multiply(height,height,height));

                float lmult = random.nextFloat() * slashLeftOffset - slashLeftOffset/2;
                Vec3 offsetPos12 = offsetPos.add(n)
                        .add(
                                left.multiply(lmult,lmult,lmult)
                        );
                Vec3 offsetPos22 = offsetPos2.add(n).add(
                        left.multiply(lmult,lmult,lmult)
                );

                if (this.getAttackType() == MalkuthAttackType.FIRE) {
                    r = 0.8f + 0.2f * random.nextFloat();
                    g = 0.3f + 0.2f * random.nextFloat();
                    b = 0.05f + 0.05f * random.nextFloat();
                } else {
                    r = 0.2f + 0.2f * random.nextFloat();
                    g = 0.8f + 0.2f * random.nextFloat();
                    b = 0.8f + 0.2f * random.nextFloat();
                }

                BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                        .size(0.05f)
                        .scalingOptions(0,0,3)
                        .color(r,g,b)
                        .build();

                level().addParticle(ballParticleOptions, offsetPos12.x,offsetPos12.y,offsetPos12.z,
                        random.nextFloat() * 0.05f - 0.025f,
                        random.nextFloat() * 0.05f - 0.025f,
                        random.nextFloat() * 0.05f - 0.025f
                );
                level().addParticle(ballParticleOptions, offsetPos22.x,offsetPos22.y,offsetPos22.z,
                        random.nextFloat() * 0.05f - 0.025f,
                        random.nextFloat() * 0.05f - 0.025f,
                        random.nextFloat() * 0.05f - 0.025f
                );

            }


        }

    }

    private void tickDamage(){

        Vec3 pos = this.position().add(0,this.getBbHeight()/2,0);
        Vec3 movement = this.getDeltaMovement();

        if (movement.equals(Vec3.ZERO)) return;

        Vec3 left = movement.cross(new Vec3(0,1,0)).normalize();

        List<Entity> damagedEntities = new ArrayList<>();

        for (float i = 0; i < this.getSlashSize(); i+= 0.2f){

            Vec3 offsetPos = pos.add(left.multiply(i,i,i));
            Vec3 offsetPos2 = pos.add(left.multiply(-i,-i,-i));

            var targets = FDHelpers.traceEntities(level(), offsetPos, offsetPos.add(movement), 0, (e)->{
                return !(e instanceof MalkuthBossBuddy);
            });


            var targets2 = FDHelpers.traceEntities(level(), offsetPos2, offsetPos2.add(movement), 0, (e)->{
                return !(e instanceof MalkuthBossBuddy);
            });

            targets.addAll(targets2);

            for (Entity e : targets){

                if (!damagedEntities.contains(e)){

                    if (e instanceof LivingEntity livingEntity){

                        livingEntity.hurt(level().damageSources().magic(), this.getDamage());

                    }

                    damagedEntities.add(e);
                }

            }

        }

    }

    @Override
    protected void onHitBlock(BlockHitResult p_37258_) {
        super.onHitBlock(p_37258_);
        this.remove(RemovalReason.KILLED);
    }

    public float getSlashSize(){
        return this.entityData.get(SLASH_SIZE);
    }

    public void setSlashSize(float size){
        this.entityData.set(SLASH_SIZE,size);
    }

    public MalkuthAttackType getAttackType(){
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(MalkuthAttackType malkuthAttackType){
        this.entityData.set(ATTACK_TYPE,malkuthAttackType);
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326181_) {
        super.defineSynchedData(p_326181_);
        p_326181_.define(SLASH_SIZE, 1f);
        p_326181_.define(ATTACK_TYPE, MalkuthAttackType.FIRE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("slashSize",this.getSlashSize());
        tag.putString("attackType",this.getAttackType().name());
        this.autoSave(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSlashSize(tag.getFloat("slashSize"));
        if (tag.contains("attackType")) {
            this.setAttackType(MalkuthAttackType.valueOf(tag.getString("attackType")));
        }
        this.autoLoad(tag);
    }

}
