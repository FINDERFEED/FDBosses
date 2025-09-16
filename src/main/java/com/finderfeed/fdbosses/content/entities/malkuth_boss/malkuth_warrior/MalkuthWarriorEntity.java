package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_warrior;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.base.BossMonsterMob;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthBossBuddy;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthDamageSource;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_earthquake.MalkuthEarthquake;
import com.finderfeed.fdbosses.init.*;
import com.finderfeed.fdbosses.packets.SlamParticlesPacket;
import com.finderfeed.fdlib.network.FDPacketHandler;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.TransitionAnimation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.head.HeadControllerContainer;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.head.IHasHead;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackChain;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackInstance;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.joml.Math;

import java.util.List;


public class MalkuthWarriorEntity extends BossMonsterMob implements IHasHead<MalkuthWarriorEntity>, MalkuthBossBuddy {

    public static float DISTANCE_TO_ATTACK = 1.75f;

    public static final EntityDataAccessor<MalkuthAttackType> WARRIOR_TYPE = SynchedEntityData.defineId(MalkuthWarriorEntity.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());

    public static final String SIMPLE_HIT = "simple_axe_hit";
    public static final String EARTH_SLAM_ATTACK = "earth_slam_attack";

    public static final String MAIN_LAYER = "main";

    private static FDModel CLIENT_MODEL;

    private boolean wasNormalAttackCancelled = false;

    private int slamCooldown = 0;

    public AttackChain attackChain;

    public MalkuthWarriorEntity(EntityType<? extends BossMonsterMob> type, Level level, MalkuthAttackType initialType) {
        super(type, level);

        this.xpReward = 20;

        this.entityData.set(WARRIOR_TYPE, initialType);

        if (level.isClientSide && CLIENT_MODEL == null){
            CLIENT_MODEL = new FDModel(BossModels.MALKUTH_WARRIOR.get());
        }

        this.lookControl = new HeadControllerContainer<>(this)
                .addHeadController(CLIENT_MODEL, "head");

        this.attackChain = new AttackChain(this.level().random)
                .registerAttack(SIMPLE_HIT, this::simpleAxeAttack)
                .registerAttack(EARTH_SLAM_ATTACK, this::earthSlamAttack)
                .addAlwaysTryCastAttack(this::canCastSlamAttack, EARTH_SLAM_ATTACK)
                .addAttack(0, SIMPLE_HIT)
        ;

        this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_WARRIOR_IDLE).build());

    }

    public boolean earthSlamAttack(AttackInstance instance){

        var target = this.getTarget();
        int stage = instance.stage;

        int tick = instance.tick;

        if (stage == 0){
            if (target == null) return true;

            this.getNavigation().stop();

            this.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_WARRIOR_SLAM_EARTH)
                            .important()
                            .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_WARRIOR_IDLE).build())
                    .build());
            instance.nextStage();
            this.getHeadControllerContainer().setControllersMode(HeadControllerContainer.Mode.ANIMATION);
        }else if (stage == 1){

            if (tick == 14){
                if (target != null) {
                    this.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
                }
            } else if (tick == 17){

                double dist = 13;
                Vec3 forward = this.getForward().multiply(1,0,1).normalize().multiply(dist,dist,dist);

                SlamParticlesPacket packet = new SlamParticlesPacket(
                        new SlamParticlesPacket.SlamData(this.getOnPos(),this.position().add(0,0.5f,0),forward.normalize())
                                .maxAngle(FDMathUtil.FPI)
                                .maxSpeed(0.5f)
                                .collectRadius(2)
                                .maxParticleLifetime(30)
                                .count(40)
                                .maxVerticalSpeedEdges(0.15f)
                                .maxVerticalSpeedCenter(0.4f)
                );
                FDPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(()->this),packet);

                level().playSound(null, this.getX(), this.getY() + this.getBbHeight()/2f, this.getZ(), BossSounds.MALKUTH_SWORD_EARTH_IMPACT.get(), SoundSource.HOSTILE, 1f, 1f);



                float damage = BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().malkuthConfig.malkuthWarriorEarthSlamDamage);

                MalkuthEarthquake malkuthEarthquake = MalkuthEarthquake.summon(level(), this.entityData.get(WARRIOR_TYPE), this.position(), forward, 20, FDMathUtil.FPI / 6, damage);

            }else if (tick >= 25){
                wasNormalAttackCancelled = false;
                slamCooldown = 20;
                this.getHeadControllerContainer().setControllersMode(HeadControllerContainer.Mode.LOOK);
                return true;
            }
        }


        return false;
    }

    public boolean canCastSlamAttack(){

        var target = this.getTarget();

        if (target != null){

            float distance = target.distanceTo(this);

            if (distance <= DISTANCE_TO_ATTACK || distance >= 10) return false;

            double yDiff = this.getY() - target.getY();

            if (yDiff > -0.1f && yDiff < 1.5f){
                return wasNormalAttackCancelled && slamCooldown <= 0;
            }
        }

        return false;
    }


    public boolean simpleAxeAttack(AttackInstance attackInstance){

        var target = this.getTarget();

        int stage = attackInstance.stage;
        int tick = attackInstance.tick;

        if (stage == 0) {
            wasNormalAttackCancelled = false;
            if (target != null) {
                Vec3 targetPos = target.position();
                double dist = this.position().distanceTo(targetPos);
                if (dist > DISTANCE_TO_ATTACK) {

                    if (level().random.nextFloat() < 0.015){
                        wasNormalAttackCancelled = true;
                        return true;
                    }

                    if (level().getGameTime() % 5 == 0) {
                        this.getNavigation().moveTo(target, 1);
                    }
                }else{
                    this.getNavigation().stop();
                    this.getHeadControllerContainer().setControllersMode(HeadControllerContainer.Mode.ANIMATION);
                    this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_WARRIOR_ATTACK)
                                    .important()
                                    .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_WARRIOR_IDLE).build())
                            .build());

                    attackInstance.nextStage();
                }
                this.getLookControl().setLookAt(target);
            }
        }else if (stage == 1){

            if (tick == 0 && target != null){
                this.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
            }

            if (tick == 5) {
                level().playSound(null, this.getX(), this.getY() + this.getBbHeight()/2f, this.getZ(), BossSounds.MALKUTH_SLASH.get(), SoundSource.HOSTILE, 1f, 1f + random.nextFloat() * 0.5f);
            }else if (tick == 10){
                float damage = BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().malkuthConfig.malkuthWarriorDamage);

                var type = this.entityData.get(WARRIOR_TYPE);

                for (var t : this.getTargetsForAxeAttack()){

                    t.hurt(new MalkuthDamageSource(level().damageSources().generic(), type, 50), damage);

                }

            }else if (tick >= 15){
                this.getHeadControllerContainer().setControllersMode(HeadControllerContainer.Mode.LOOK);
                return true;
            }
        }
        return false;
    }

    private List<LivingEntity> getTargetsForAxeAttack(){

        Vec3 forward = this.getForward().multiply(1,0,1).normalize();

        float cylinderOffset = 1.25f;

        Vec3 cylinderStart = this.position().add(forward.multiply(cylinderOffset,0,cylinderOffset)).add(0,-1.01,0);

        var list = BossTargetFinder.getEntitiesInCylinder(LivingEntity.class, level(),  cylinderStart, 2.5f, 0.75f, entity -> !(entity instanceof MalkuthBossBuddy));

        Vec2 direction = new Vec2((float)forward.x,(float)forward.z);

        var list2 = BossTargetFinder.getEntitiesInArc(LivingEntity.class, level(),  this.position().add(0,-1.01,0), direction,
                FDMathUtil.FPI/2, 2.5f, 1f, entity -> !(entity instanceof MalkuthBossBuddy));

        list.addAll(list2);

        return list;
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WARRIOR_TYPE, MalkuthAttackType.FIRE);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();


        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0){

            @Override
            public void tick() {
                super.tick();
                Vec3 lookAt = this.mob.position().add(this.mob.getForward().multiply(1,0,1).normalize().multiply(100,100,100));
                this.mob.getLookControl().setLookAt(lookAt);
            }

            @Override
            public boolean canUse() {
                return super.canUse() && MalkuthWarriorEntity.super.getTarget() == null;
            }
        });
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F){
            @Override
            public boolean canUse() {
                return super.canUse() && MalkuthWarriorEntity.super.getTarget() == null;
            }
        });
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this){
            @Override
            public boolean canUse() {
                return super.canUse() && MalkuthWarriorEntity.super.getTarget() == null;
            }
        });

        this.goalSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));

    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide){

            if (!this.isDeadOrDying()) {


                this.getHeadControllerContainer().clientTick();

                var ticker = this.getAnimationSystem().getTicker(MAIN_LAYER);

                if (ticker == null) {
                    this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_WARRIOR_IDLE).build());
                } else {

                    Animation anim = ticker.getAnimation();

                    if (anim instanceof TransitionAnimation tr) {
                        anim = tr.getTransitionTo();
                    }

                    if (anim == BossAnims.MALKUTH_WARRIOR_IDLE.get()) {
                        if (this.walkAnimation.speed() > 0.05f) {
                            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_WARRIOR_WALK)
                                    .setToNullTransitionTime(5)
                                    .setSpeed(2f)
                                    .build());
                        }
                    } else if (anim == BossAnims.MALKUTH_WARRIOR_WALK.get()) {
                        if (this.walkAnimation.speed() < 0.05f) {
                            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_WARRIOR_IDLE).build());
                        }
                    }
                }
            }
        }else{

            if (tickCount == 1){

                Vec3 forward = this.getForward().multiply(1,0,1).normalize().multiply(100,100,100);

                this.getLookControl().setLookAt(this.position().add(forward).add(0,this.getEyeHeight(),0));
            }

            if (!this.isDeadOrDying()) {
                slamCooldown = FDMathUtil.clamp(slamCooldown - 1, 0, Integer.MAX_VALUE);

                this.attackChain.tick();

                this.setYRot(this.yBodyRot);
            }

        }
    }

    @Override
    public void die(DamageSource p_21014_) {
        super.die(p_21014_);
        if (!level().isClientSide){
            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_WARRIOR_DIE)
                    .build());
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return BossSounds.MALKUTH_WARRIOR_HIT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return BossSounds.MALKUTH_WARRIOR_HIT.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("mtype", this.entityData.get(WARRIOR_TYPE).name());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("mtype")){
            this.entityData.set(WARRIOR_TYPE, MalkuthAttackType.valueOf(tag.getString("mtype")));
        }
    }

    @Override
    public HeadControllerContainer<MalkuthWarriorEntity> getHeadControllerContainer() {
        return (HeadControllerContainer<MalkuthWarriorEntity>) this.lookControl;
    }

    @Override
    public boolean canBeLeashed(Player p_21418_) {
        return false;
    }
}
