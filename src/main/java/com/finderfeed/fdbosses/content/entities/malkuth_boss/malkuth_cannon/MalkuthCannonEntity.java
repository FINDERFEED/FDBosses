package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticleOptions;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.FDTagHelper;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDLivingEntity;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class MalkuthCannonEntity extends FDLivingEntity implements AutoSerializable {

    private static FDModel CLIENT_MODEL;

    public static final EntityDataAccessor<MalkuthAttackType> MALKUTH_ATTACK_TYPE = SynchedEntityData.defineId(MalkuthCannonEntity.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());

    public static final EntityDataAccessor<Boolean> PLAYER_CONTROLLED = SynchedEntityData.defineId(MalkuthCannonEntity.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<Boolean> BROKEN = SynchedEntityData.defineId(MalkuthCannonEntity.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<Boolean> BROKEN_REQUIRES_MATERIALS = SynchedEntityData.defineId(MalkuthCannonEntity.class, EntityDataSerializers.BOOLEAN);

    private List<Vec3> shootTargets = new ArrayList<>();


    @SerializableField
    private MalkuthAttackType malkuthCannonType = MalkuthAttackType.FIRE;

    @SerializableField
    private int shootTickCount = 0;

    public MalkuthCannonEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        malkuthCannonType = MalkuthAttackType.ICE;
        this.setNoGravity(true);
        this.getAnimationSystem().startAnimation("SUMMON", AnimationTicker.builder(BossAnims.MALKUTH_CANNON_SUMMON).build());
        if (level.isClientSide){
            CLIENT_MODEL = new FDModel(BossModels.MALKUTH_CANNON.get());
        }
    }

    public static MalkuthCannonEntity summon(Level level, Vec3 pos, Vec3 lookAt, MalkuthAttackType malkuthAttackType){
        MalkuthCannonEntity malkuthCannonEntity = new MalkuthCannonEntity(BossEntities.MALKUTH_CANNON.get(), level);
        malkuthCannonEntity.setPos(pos);
        malkuthCannonEntity.setCannonType(malkuthAttackType);
        malkuthCannonEntity.lookAt(EntityAnchorArgument.Anchor.FEET, lookAt);
        level.addFreshEntity(malkuthCannonEntity);
        return malkuthCannonEntity;
    }

    public void shoot(List<Vec3> targets){
        if (level().isClientSide) return;
        if (shootTargets.isEmpty()){
            this.shootTargets = new ArrayList<>(targets);
            this.shootTickCount = 20;
        }
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {

        if (!level().isClientSide && hand == InteractionHand.MAIN_HAND && this.isPlayerControlled()){
            if (this.isBroken()) {
                return InteractionResult.SUCCESS;
            }

            MalkuthAttackType attackType = this.getCannonType();

            if (attackType.isFire()) {
                Vec3 fireOffset = new Vec3(6.5, 14.0, 55);
                this.shoot(List.of(fireOffset.add(this.position())));
            }else {
                Vec3 iceOffset = new Vec3(-6.5, 14.0, 55);
                this.shoot(List.of(iceOffset.add(this.position())));
            }
        }

        return super.interactAt(player, vec, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            if (!shootTargets.isEmpty()){

                if (shootTickCount == 20){
                    this.getAnimationSystem().startAnimation("SHOOT", AnimationTicker.builder(BossAnims.MALKUTH_CANNON_SHOOT).build());
                }else if (shootTickCount == 16){

                    Vec3 frwd = this.getForward().multiply(1,0,1).normalize();

                    Vec3 summonPos = this.position().add(frwd.x * 1.2,1.75,frwd.z * 1.2);

                    BossUtil.malkuthCannonShoot((ServerLevel) level(), this.malkuthCannonType, summonPos, frwd.add(0,0.6f,0), 100);

                    for (Vec3 target : this.shootTargets){
                        Vec3 speed = BossUtil.calculateMortarProjectileVelocity(summonPos, target, -(float)LivingEntity.DEFAULT_BASE_GRAVITY, 35 + random.nextInt(10));
                        MalkuthCannonProjectile.summon(level(), summonPos, speed, 1000, malkuthCannonType);
                    }


                }else if (shootTickCount == 5) {
                    if (this.isPlayerControlled()){
                        this.setBroken(true);
                    }
                }else if (shootTickCount <= 0){
                    this.shootTargets.clear();
                }

                shootTickCount--;
            }
        }else{
            this.brokenParticles();
        }
    }

    private void brokenParticles(){

        if (this.level().getGameTime() % 10 == 0 && this.isBroken()) {

            Matrix4f t = this.getModelPartTransformation(this, "cannon2", CLIENT_MODEL);

            Vector3f position = t.transformPosition(0, 0, -1.25f, new Vector3f()).add(
                    (float) this.getX(),
                    (float) this.getY(),
                    (float) this.getZ()
            );

            float r = random.nextFloat() * 0.2f + 0.2f;

            BigSmokeParticleOptions options = BigSmokeParticleOptions.builder()
                    .color(r, r, r)
                    .size(1f)
                    .lifetime(0, 0, 60)
                    .build();

            level().addParticle(options, position.x, position.y, position.z, 0, 0.05f, 0);
            level().addParticle(options, position.x, position.y, position.z, 0, 0.05f, 0);

        }
    }

    public void setBrokenRequiresMaterials(boolean broken){
        this.entityData.set(BROKEN_REQUIRES_MATERIALS, broken);
        if (broken) {
            this.getAnimationSystem().startAnimation("BROKE", AnimationTicker.builder(BossAnims.MALKUTH_CANNON_BREAK).build());
            this.entityData.set(BROKEN, false);
        }else{
            this.getAnimationSystem().startAnimation("BROKE", AnimationTicker.builder(BossAnims.MALKUTH_CANNON_REPAIR).build());
        }
    }

    public void setBroken(boolean broken) {
        this.entityData.set(BROKEN, broken);
        if (broken) {
            this.getAnimationSystem().startAnimation("BROKE", AnimationTicker.builder(BossAnims.MALKUTH_CANNON_BREAK).build());
            this.entityData.set(BROKEN_REQUIRES_MATERIALS, false);
        }else{
            this.getAnimationSystem().startAnimation("BROKE", AnimationTicker.builder(BossAnims.MALKUTH_CANNON_REPAIR).build());
        }
    }

    public boolean isBroken(){
        return this.entityData.get(BROKEN) || this.entityData.get(BROKEN_REQUIRES_MATERIALS);
    }

    public boolean isPlayerControlled(){
        return this.entityData.get(PLAYER_CONTROLLED);
    }

    public void setPlayerControlled(boolean controlled){
        this.entityData.set(PLAYER_CONTROLLED, controlled);
    }

    private void setCannonType(MalkuthAttackType malkuthAttackType){
        this.entityData.set(MALKUTH_ATTACK_TYPE,malkuthAttackType);
        this.malkuthCannonType = malkuthAttackType;
    }

    public MalkuthAttackType getCannonType(){
        return this.entityData.get(MALKUTH_ATTACK_TYPE);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MALKUTH_ATTACK_TYPE, MalkuthAttackType.FIRE);
        builder.define(BROKEN_REQUIRES_MATERIALS, false);
        builder.define(PLAYER_CONTROLLED, false);
        builder.define(BROKEN, false);
    }

    @Override
    public boolean hurt(DamageSource src, float damage) {
        if (src.is(DamageTypes.FELL_OUT_OF_WORLD) || src.is(DamageTypes.GENERIC_KILL)) return super.hurt(src,damage);
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void push(Vec3 p_347665_) {

    }

    @Override
    public void push(Entity p_21294_) {
    }

    @Override
    public void push(double p_20286_, double p_20287_, double p_20288_) {

    }

    @Override
    protected void pushEntities() {

    }

    @Override
    public void setDeltaMovement(Vec3 p_20257_) {

    }

    @Override
    public void setDeltaMovement(double p_20335_, double p_20336_, double p_20337_) {

    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
        this.shootTargets = FDTagHelper.loadVec3List(tag, "targets");
        this.entityData.set(BROKEN,tag.getBoolean("broken"));
        this.entityData.set(BROKEN_REQUIRES_MATERIALS,tag.getBoolean("broken_materials"));
        this.setCannonType(this.malkuthCannonType);

        if (this.isBroken()){
            this.getAnimationSystem().startAnimation("BROKEN", AnimationTicker.builder(BossAnims.MALKUTH_CANNON_BREAK).build());
        }

    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        FDTagHelper.saveVec3List(tag, "targets", this.shootTargets);
        this.autoSave(tag);
        this.setCannonType(this.malkuthCannonType);
        tag.putBoolean("broken",this.entityData.get(BROKEN));
        tag.putBoolean("broken_materials",this.entityData.get(BROKEN_REQUIRES_MATERIALS));
    }

}
