package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_boulder;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.GravityParticleOptions;
import com.finderfeed.fdbosses.client.particles.stripe_particle.StripeParticleOptions;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthBossBuddy;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthDamageSource;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.init.BossDamageSources;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdbosses.packets.SlamParticlesPacket;
import com.finderfeed.fdlib.FDHelpers;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.FDProjectile;
import com.finderfeed.fdlib.util.ProjectileMovementPath;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;

public class MalkuthBoulderEntity extends FDProjectile implements AutoSerializable {

    public static final EntityDataAccessor<MalkuthAttackType> MALKUTH_ATTACK_TYPE = SynchedEntityData.defineId(MalkuthBoulderEntity.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());
    public static final EntityDataAccessor<Integer> PREPARE_TIME = SynchedEntityData.defineId(MalkuthBoulderEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> PREPARE_HEIGHT = SynchedEntityData.defineId(MalkuthBoulderEntity.class, EntityDataSerializers.FLOAT);

    @SerializableField
    private ProjectileMovementPath movementPath;

    @SerializableField
    private boolean shouldMoveToTarget = false;

    @SerializableField
    private float damage = 0;

    private boolean removeNextTick = false;

    public static MalkuthBoulderEntity summon(Level level, Vec3 pos, int prepareTime, float prepareHeight, ProjectileMovementPath movementPath, MalkuthAttackType malkuthAttackType, float damage){
        MalkuthBoulderEntity malkuthBoulderEntity = new MalkuthBoulderEntity(BossEntities.MALKUTH_BOULDER.get(), level);
        malkuthBoulderEntity.setPos(pos);
        malkuthBoulderEntity.movementPath = movementPath;
        malkuthBoulderEntity.entityData.set(PREPARE_TIME, prepareTime);
        malkuthBoulderEntity.entityData.set(PREPARE_HEIGHT, prepareHeight);
        malkuthBoulderEntity.setMalkuthAttackType(malkuthAttackType);
        malkuthBoulderEntity.damage = damage;
        level.addFreshEntity(malkuthBoulderEntity);
        return malkuthBoulderEntity;
    }

    public MalkuthBoulderEntity(EntityType<? extends AbstractHurtingProjectile> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        if (!level().isClientSide && removeNextTick){
            this.remove(RemovalReason.DISCARDED);
        }
        super.tick();
        if (level().isClientSide){

            Vec3 between = this.position().subtract(new Vec3(
                    xo,yo,zo
            ));
            Vec3 nb = between.normalize();

            double dist = 0;
            double b = between.length();
            do {
                if (this.tickCount > this.getPrepareTime() / 2) {

                    int particleCount = this.getDeltaMovement().length() > 0 ? 20 : 5;

                    for (int i = 0; i < particleCount; i++) {
                        ParticleOptions options;

                        if (this.getMalkuthAttackType().isFire()) {
                            float rnd = random.nextFloat();
                            if (rnd > 0.5) {
                                options = new GravityParticleOptions(BossParticles.FLAME_WITH_STONE.get(), 20 + random.nextInt(4), 0.15f + random.nextFloat() * 0.2f,
                                        (float) Mob.DEFAULT_BASE_GRAVITY * 20, 2f, true);
                            } else {
                                options = ParticleTypes.LAVA;
                            }
                        } else {
                            options = new GravityParticleOptions(BossParticles.ICE_CHUNK.get(), 20 + random.nextInt(4), 0.5f + random.nextFloat() * 0.2f,
                                    (float) Mob.DEFAULT_BASE_GRAVITY * 20, 2f, true);
                        }

                        if (this.getDeltaMovement().length() > 0 && random.nextFloat() > 0.3f){
                            if (this.getMalkuthAttackType().isIce()){
                                float rc = random.nextFloat() * 0.2f;
                                float gc = 0.7f + random.nextFloat() * 0.1f;
                                float bc = 0.9f + random.nextFloat() * 0.1f;

                                options = BallParticleOptions.builder()
                                        .color(rc,gc,bc)
                                        .size(0.2f + random.nextFloat() * 0.2f)
                                        .scalingOptions(0,0,20)
                                        .friction(0.7f)
                                        .build();
                            }else{
                                float rc = 0.9f + random.nextFloat() * 0.1f;
                                float gc = 0.2f + random.nextFloat() * 0.2f;
                                float bc = random.nextFloat() * 0.2f;
                                options = BallParticleOptions.builder()
                                        .color(rc,gc,bc)
                                        .size(0.2f + random.nextFloat() * 0.2f)
                                        .scalingOptions(0,0,20)
                                        .friction(0.8f)
                                        .build();
                            }
                        }

                        Vec3 rnd = this.position().add(
                                random.nextFloat() * 3.5 - 1.75,
                                random.nextFloat() * 3.5 - 1.75,
                                random.nextFloat() * 3.5 - 1.75
                        ).add(nb.reverse().multiply(dist,dist,dist));

                        Vec3 speed = this.getDeltaMovement().multiply(0.15f,0.15f,0.15f);

                        level().addParticle(options, rnd.x, rnd.y, rnd.z, speed.x,speed.y,speed.z);
                    }
                }
                dist += 1.75f;
            } while (dist < b);

            if (tickCount == 1) {
                for (int i = 0; i < 10; i++) {
                    this.stripeParticles(MalkuthAttackType.FIRE);
                    this.stripeParticles(MalkuthAttackType.ICE);
                }
            }

        }else{
            if (this.isAllowedToMoveToTarget()){
                if (!this.movementPath.isFinished()) {
                    this.movementPath.tick(this);
                }else{
                    this.removeNextTick = true;
                }
                this.tickDamage();
            }



            if (tickCount == 1){
                SlamParticlesPacket packet = new SlamParticlesPacket(
                        new SlamParticlesPacket.SlamData(this.getOnPos(),this.position().add(0,0.5f,0),new Vec3(1,0,0))
                                .maxAngle(FDMathUtil.FPI * 2)
                                .maxSpeed(0.3f)
                                .collectRadius(2)
                                .maxParticleLifetime(30)
                                .count(20)
                                .maxVerticalSpeedEdges(0.15f)
                                .maxVerticalSpeedCenter(0.15f)
                );
                PacketDistributor.sendToPlayersTrackingEntity(this,packet);

            }
        }
    }



    private void tickDamage(){

        Vec3 movement = this.getDeltaMovement();
        if (movement.equals(Vec3.ZERO)) return;

        float bbwidthinflate = this.getBbWidth()/2;
        float bbHeight = this.getBbHeight();

        Vec3 startPos = this.position().add(0,bbHeight/2,0);

        Vec3 endPos = startPos.add(movement);

        var entities = FDHelpers.traceEntities(level(), startPos, endPos, bbwidthinflate, v->{
            return !(v instanceof MalkuthBossBuddy);
        });

        for (var target : entities){
            if (target instanceof LivingEntity livingEntity){

                target.hurt(new MalkuthDamageSource(BossDamageSources.MALKUTH_SIDE_ROCKS_SOURCE, this.getMalkuthAttackType(), 34), this.damage);

            }
        }

    }


    private void stripeParticles(MalkuthAttackType type){


        float rndRadius = 2 + FDEasings.easeOut(random.nextFloat()) * 2f;
        Vec3 rnd = new Vec3(rndRadius,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());

        Vec3 dir = rnd.normalize();

        float startOffsetRand = 0.1f + random.nextFloat() * 0.5f;
        Vec3 startOffset = dir.multiply(startOffsetRand,startOffsetRand,startOffsetRand);

        Vec3 stripePos = this.position().add(startOffset);

        Vector3f colFire = MalkuthEntity.getMalkuthAttackPreparationParticleColor(type);

        FDColor fireColorStart = new FDColor(colFire.x,colFire.y - random.nextFloat() * 0.1f - 0.3f,colFire.z,0.5f);
        FDColor fireColor = new FDColor(colFire.x,colFire.y + random.nextFloat() * 0.1f,colFire.z,1f);

        float firstPointOffset = 1f + random.nextFloat() * 1f;


        StripeParticleOptions stripeParticleOptions = StripeParticleOptions.builder()
                .endColor(fireColor)
                .startColor(fireColorStart)
                .lifetime(5 + random.nextInt(5))
                .lod(50)
                .scale(0.1f)
                .stripePercentLength(0.75f)
                .endOutPercent(0.25f)
                .startInPercent(0.25f)
                .offsets(new Vec3(0.01f,0,0),
                        dir.multiply(firstPointOffset,0,firstPointOffset).add(0,0.5,0),
                        rnd.add(0,1.5f + random.nextFloat() * 2,0))
                .build();

        level().addParticle(stripeParticleOptions, true, stripePos.x,stripePos.y,stripePos.z,0,0,0);

    }

    public void setShouldMoveToTarget(boolean shouldMoveToTarget) {
        this.shouldMoveToTarget = shouldMoveToTarget;
    }

    public boolean isAllowedToMoveToTarget() {
        return shouldMoveToTarget;
    }

    public void setMalkuthAttackType(MalkuthAttackType malkuthAttackType){
        this.entityData.set(MALKUTH_ATTACK_TYPE, malkuthAttackType);
    }

    public MalkuthAttackType getMalkuthAttackType(){
        return this.entityData.get(MALKUTH_ATTACK_TYPE);
    }

    protected int getPrepareTime(){
        return this.entityData.get(PREPARE_TIME);
    }

    protected void setPrepareTime(int time){
        this.entityData.set(PREPARE_TIME, time);
    }

    public float getPrepareHeight() {
        return this.entityData.get(PREPARE_HEIGHT);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326181_) {
        super.defineSynchedData(p_326181_);
        p_326181_.define(MALKUTH_ATTACK_TYPE, MalkuthAttackType.FIRE);
        p_326181_.define(PREPARE_TIME, 20);
        p_326181_.define(PREPARE_HEIGHT, 3f);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
        if (tag.contains("mtype")){
            this.setMalkuthAttackType(MalkuthAttackType.valueOf(tag.getString("mtype")));
        }
        this.entityData.set(PREPARE_TIME, tag.getInt("prepareTime"));
        this.entityData.set(PREPARE_HEIGHT, tag.getFloat("prepareHeight"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
        tag.putString("mtype",this.getMalkuthAttackType().name());
        tag.putInt("prepareTime", this.entityData.get(PREPARE_TIME));
        tag.putFloat("prepareHeight", this.entityData.get(PREPARE_HEIGHT));
    }
}
