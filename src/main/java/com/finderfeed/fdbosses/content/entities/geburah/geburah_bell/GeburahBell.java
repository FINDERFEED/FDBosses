package com.finderfeed.fdbosses.content.entities.geburah.geburah_bell;

import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDLivingEntity;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class GeburahBell extends FDLivingEntity {

    public static final EntityDataAccessor<Boolean> IS_RED = SynchedEntityData.defineId(GeburahBell.class, EntityDataSerializers.BOOLEAN);

    private UUID geburah;

    private boolean alreadyRang = false;

    public static GeburahBell summon(GeburahEntity geburah, Vec3 pos, Vec3 lookAt, boolean isRed){
        GeburahBell bell = new GeburahBell(BossEntities.GEBURAH_BELL.get(), geburah.level());

        bell.setPos(pos);
        bell.lookAt(EntityAnchorArgument.Anchor.FEET, lookAt);
        bell.getEntityData().set(IS_RED, isRed);
        bell.geburah = geburah.getUUID();

        geburah.level().addFreshEntity(bell);
        return bell;
    }

    public GeburahBell(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }


    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            if (geburah == null){
                this.setRemoved(RemovalReason.DISCARDED);
            }
        }else{

            if (this.tickCount > 10 && !this.isDeadOrDying()) {



                for (int i = 0; i < 2; i++) {

                    Vec3 offset = new Vec3(1,0,0).yRot(i * FDMathUtil.FPI + this.tickCount * FDMathUtil.FPI / 32);

                    BallParticleOptions options;

                    if (this.isRed()) {
                        options = BallParticleOptions.builder()
                                .brightness(4)
                                .color(1f, 0.3f, 0.1f, 1f)
                                .scalingOptions(0, 0, 20)
                                .size(0.1f + random.nextFloat() * 0.2f)
                                .build();
                    } else {
                        options = BallParticleOptions.builder()
                                .brightness(4)
                                .color(0.1f, 0.8f, 1f, 1f)
                                .scalingOptions(0, 0, 20)
                                .size(0.1f + random.nextFloat() * 0.2f)
                                .build();
                    }

                    level().addParticle(options,
                            this.getX() + offset.x,
                            this.getY() + 0.5,
                            this.getZ() + offset.z,
                            random.nextFloat() * 0.01 - 0.005,
                            random.nextFloat() * 0.01 - 0.005,
                            random.nextFloat() * 0.01 - 0.005
                    );

                }
            }

        }
    }

    @Override
    protected void tickDeath() {
        if (!level().isClientSide) {
            if (this.deathTime == 0) {
                this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.GEBURAH_BELL_RING)
                        .build());
            }
        }
        this.deathTime++;
        if (this.deathTime >= BossAnims.GEBURAH_BELL_RING.get().getAnimTime() && !this.level().isClientSide() && !this.isRemoved()) {
            this.remove(RemovalReason.KILLED);
        }
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {

        if (source.getEntity() instanceof ServerPlayer serverPlayer){
            if (!alreadyRang){
                var geburah = this.getGeburah();
                if (geburah != null){
                    geburah.bellRang(this);
                    alreadyRang = true;
                }
            }
            return super.hurt(source, 10);
        }

        if (source.is(DamageTypes.GENERIC_KILL) || source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            return super.hurt(source, damage);
        }else{
            return false;
        }

    }

    public boolean isRed(){
        return this.getEntityData().get(IS_RED);
    }

    public void setState(boolean isRed){
        this.getEntityData().set(IS_RED, isRed);
    }

    public GeburahEntity getGeburah(){
        if (level() instanceof ServerLevel serverLevel && serverLevel.getEntity(geburah) instanceof GeburahEntity redMist){
            return redMist;
        }else{
            return null;
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.getEntityData().set(IS_RED, tag.getBoolean("is_red"));
        if (tag.contains("geburah")){
            this.geburah = tag.getUUID("geburah");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("is_red",this.getEntityData().get(IS_RED));
        tag.putUUID("geburah", geburah);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_RED, true);
    }

    @Override
    public void push(Entity p_21294_) {

    }

    @Override
    public void push(double p_20286_, double p_20287_, double p_20288_) {

    }

    @Override
    public void push(Vec3 p_347665_) {

    }

    @Override
    protected void pushEntities() {

    }


    @Override
    public void setDeltaMovement(double p_20335_, double p_20336_, double p_20337_) {
        super.setDeltaMovement(0,0,0);
    }

    @Override
    public void setDeltaMovement(Vec3 p_20257_) {
        super.setDeltaMovement(Vec3.ZERO);
    }

}
