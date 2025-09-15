package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.GravityOptionsParticleType;
import com.finderfeed.fdbosses.client.particles.GravityParticleOptions;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticleOptions;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthDamageSource;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.init.BossDamageSources;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdbosses.packets.SlamParticlesPacket;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.util.FDProjectile;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class MalkuthCannonProjectile extends FDProjectile implements AutoSerializable {

    public static final int PROJECITLE_INVULNERABILITY_TIME = 10;

    public static final EntityDataAccessor<MalkuthAttackType> MALKUTH_ATTACK_TYPE = SynchedEntityData.defineId(MalkuthCannonProjectile.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());

    @SerializableField
    private MalkuthAttackType malkuthAttackType = MalkuthAttackType.FIRE;

    @SerializableField
    private int reversedAge = 3000;

    @SerializableField
    private float damage;

    public MalkuthCannonProjectile(EntityType<? extends AbstractHurtingProjectile> type, Level level) {
        super(type, level);
        this.setNoGravity(false);
    }

    public static MalkuthCannonProjectile summon(Level level, Vec3 pos, Vec3 speed, int lifetime, MalkuthAttackType projectileDamageType, float damage){
        MalkuthCannonProjectile malkuthCannonProjectile = new MalkuthCannonProjectile(BossEntities.MALKUTH_CANNON_PROJECTILE.get(), level);
        malkuthCannonProjectile.setPos(pos);
        malkuthCannonProjectile.setDeltaMovement(speed);
        malkuthCannonProjectile.reversedAge = lifetime;
        malkuthCannonProjectile.setMalkuthAttackType(projectileDamageType);
        malkuthCannonProjectile.damage = damage;
        level.addFreshEntity(malkuthCannonProjectile);
        return malkuthCannonProjectile;
    }

    public void setMalkuthAttackType(MalkuthAttackType malkuthAttackType){
        this.malkuthAttackType = malkuthAttackType;
        this.entityData.set(MALKUTH_ATTACK_TYPE, malkuthAttackType);
    }

    public MalkuthAttackType getMalkuthAttackType(){
        return this.entityData.get(MALKUTH_ATTACK_TYPE);
    }

    @Override
    public void tick() {
        if (!level().isClientSide){
            this.setMalkuthAttackType(this.malkuthAttackType);
            this.applyGravity();
            if (reversedAge-- <= 0){
                this.explode(this.position());
            }
        }
        super.tick();
        if (level().isClientSide){
            this.spawnParticles();
        }
    }

    private void spawnParticles(){

        Vec3 between = new Vec3(
                 xo - this.getX() ,
                 yo - this.getY() ,
                 zo - this.getZ()
        );

        float dist = (float) between.length();

        between = between.normalize();

        for (float i = 0; i <= dist;i+=0.5f){

            float  p = i / dist;

            for (int count = 0; count < 4;count++){
                float r;
                float g;
                float b;
                if (this.getMalkuthAttackType().isFire()){
                    r = 0.8f + random.nextFloat() * 0.2f;
                    g = 0.3f + random.nextFloat() * 0.5f;
                    b = 0.1f + random.nextFloat() * 0.05f;
                }else{
                    r = 0.1f + random.nextFloat() * 0.05f;
                    g = 0.8f - random.nextFloat() * 0.3f;
                    b = 0.8f + random.nextFloat() * 0.2f;
                }

                ParticleOptions options;

                if (random.nextFloat() > 0.5){
                    options = BallParticleOptions.builder()
                            .size(0.15f + random.nextFloat() * 0.1f)
                            .color(r,g,b)
                            .scalingOptions(0,0,10)
                            .build();
                }else{

                    if (this.getMalkuthAttackType().isFire()){
                        options = ParticleTypes.SMOKE;
                    }else{
                        options = new GravityParticleOptions(BossParticles.ICE_CHUNK.get(),10,0.25f, 0, 5f, true);
                    }


                }

                level().addParticle(options,true,
                        this.getX() + between.x * 1.5f + (between.x) * p + random.nextFloat() - .5f,
                        this.getY() + between.y * 1.5f + (between.y) * p + random.nextFloat() - .5f,
                        this.getZ() + between.z * 1.5f + (between.z) * p + random.nextFloat() - .5f,
                        0,0,0
                );
            }

            float c = random.nextFloat() * 0.4f + 0.2f;

            BigSmokeParticleOptions bigSmokeParticleOptions = BigSmokeParticleOptions.builder()
                    .color(c,c,c)
                    .lifetime(0,0,10)
                    .size(1f + 0.1f * random.nextFloat())
                    .build();
            level().addParticle(bigSmokeParticleOptions,true,
                    this.getX() + between.x * p + between.x,
                    this.getY() + between.y * p + between.y,
                    this.getZ() + between.z * p + between.z,
                    0,0,0
            );
        }


    }

    @Override
    protected void onHitBlock(BlockHitResult res) {
        super.onHitBlock(res);
        if (!level().isClientSide && this.tickCount > PROJECITLE_INVULNERABILITY_TIME){
            this.explode(res.getLocation());
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult res) {
        super.onHitEntity(res);
        if (!level().isClientSide && this.tickCount > PROJECITLE_INVULNERABILITY_TIME){
            this.explode(res.getLocation());
        }
    }

    private void explode(Vec3 pos){
        if (!level().isClientSide) {

            float c = random.nextFloat() * 0.2f + 0.2f;

            BigSmokeParticleOptions bigSmokeParticleOptions = BigSmokeParticleOptions.builder()
                    .color(c,c,c)
                    .lifetime(0,0,10)
                    .size(1f + 0.1f * random.nextFloat())
                    .friction(0.7f)
                    .build();
            ((ServerLevel)level()).sendParticles(bigSmokeParticleOptions,
                    pos.x,pos.y,pos.z,20,
                    0,0,0,0.25f
            );


            SlamParticlesPacket packet = new SlamParticlesPacket(
                    new SlamParticlesPacket.SlamData(new BlockPos((int)pos.x,(int)pos.y,(int)pos.z),pos.add(0,0,0),new Vec3(1,0,0))
                            .maxAngle(FDMathUtil.FPI * 2)
                            .maxSpeed(0.3f)
                            .collectRadius(2)
                            .maxParticleLifetime(30)
                            .count(20)
                            .maxVerticalSpeedEdges(0.15f)
                            .maxVerticalSpeedCenter(0.15f)
            );
            PacketDistributor.sendToPlayersTrackingEntity(this,packet);

            BossUtil.malkuthFireballExplosionParticles((ServerLevel) level(), pos, this.getMalkuthAttackType());

            var targets = BossTargetFinder.getEntitiesInSphere(LivingEntity.class, level(), pos, 2.5f, target -> !(target instanceof MalkuthEntity));
            var malkuth = BossTargetFinder.getEntitiesInSphere(MalkuthEntity.class, level(), pos, 10);

            for (var m : malkuth){
                m.hurtBoss(1);
            }

            for (var target : targets){
                if (damage != 0) {
                    target.hurt(new MalkuthDamageSource(BossDamageSources.MALKUTH_CANNONS_SOURCE, this.getMalkuthAttackType(), 100), this.damage);
                }
            }

            this.remove(RemovalReason.DISCARDED);

        }
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
    }

    @Override
    protected double getDefaultGravity() {
        return LivingEntity.DEFAULT_BASE_GRAVITY;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MALKUTH_ATTACK_TYPE, MalkuthAttackType.FIRE);
    }
}
