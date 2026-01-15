package com.finderfeed.fdbosses.content.items.geburah;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.particles.stripe_particle.StripeParticleOptions;
import com.finderfeed.fdbosses.content.entities.geburah.particles.geburah_ray.GeburahRayOptions;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.FDTargetFinder;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

public class DivineGear extends FDEntity implements AutoSerializable {

    @SerializableField
    private int cooldown = -1;

    private int attackTime = 0;

    @SerializableField
    private int age = 0;

    private UUID owner;

    public static void summon(Player player, Vec3 pos){
        DivineGear divineGear = new DivineGear(BossEntities.DIVINE_GEAR.get(), player.level());
        divineGear.owner = player.getUUID();
        divineGear.setPos(pos);
        player.level().addFreshEntity(divineGear);
    }

    public DivineGear(EntityType<?> type, Level level) {
        super(type, level);
        this.getAnimationSystem().startAnimation("SUMMON", AnimationTicker.builder(BossAnims.DIVINE_GEAR_SUMMON)
                        .setToNullTransitionTime(0)
                .build());
        this.getAnimationSystem().startAnimation("IDLE", AnimationTicker.builder(BossAnims.DIVINE_GEAR_IDLE)
                        .setToNullTransitionTime(0)
                .build());
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 corePos = this.position().add(0,1.65,0);

        if (!level().isClientSide){

            var owner = this.getOwner();

            if (owner == null){
                this.setRemoved(RemovalReason.DISCARDED);
                return;
            }

            if (cooldown == -1){
                this.cooldown = 20;
            }

            int lifetime = BossConfigs.BOSS_CONFIG.get().itemConfig.divineGearLifetime;

            if (age > BossAnims.DIVINE_GEAR_SUMMON.get().getAnimTime() && age < lifetime){

                if (cooldown > 0){
                    cooldown--;
                }else{
                    var target = this.getTarget();
                    if (target != null){
                        if (attackTime == 0){
                            this.sendChargeParticles(10);
                        }else if (attackTime > 10){


                            Vec3 targetLocation = target.getBoundingBox().getCenter();
                            Vec3 between = corePos.subtract(targetLocation);

                            var options = GeburahRayOptions.builder()
                                    .end(targetLocation)
                                    .color(1f,0.8f,0.2f)
                                    .time(0,2,7)
                                    .width(0.25f)
                                    .build();

                            FDLibCalls.sendParticles((ServerLevel) level(), options, corePos, 120);

                            BossUtil.divineGearRayParticles((ServerLevel) level(), targetLocation, 120, between);
                            PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                                    .amplitude(1f)
                                    .stayTime(0)
                                    .outTime(10)
                                    .frequency(10)
                                    .build(), targetLocation, 40);
                            level().playSound(null, corePos.x,corePos.y,corePos.z, BossSounds.GEBURAH_CORE_RAY_STRIKE.get(), SoundSource.HOSTILE, 4f, 1f);

                            target.invulnerableTime = 0;
                            target.hurt(level().damageSources().playerAttack(owner), BossConfigs.BOSS_CONFIG.get().itemConfig.divineGearDamage);
                            attackTime = -1;
                            this.setOnCooldown();
                        }
                        attackTime++;
                    }else{
                        attackTime = 0;
                    }
                }

            }else if (age == lifetime){
                this.sendChargeParticles(10);
            }

            int animTime = BossAnims.DIVINE_GEAR_DEATH.get().getAnimTime() - 10;

            if (age > lifetime && age < lifetime + animTime){
                this.getAnimationSystem().startAnimation("SUMMON", AnimationTicker.builder(BossAnims.DIVINE_GEAR_DEATH)
                        .setLoopMode(Animation.LoopMode.HOLD_ON_LAST_FRAME)
                        .setToNullTransitionTime(0)
                        .build());
            }else if (age >= lifetime + animTime){
                BallParticleOptions ballParticle = BallParticleOptions.builder()
                        .color(1f,0.7f, 0.2f)
                        .scalingOptions(0, 0, 20)
                        .brightness(2)
                        .size(1f)
                        .friction(0.7f)
                        .build();

                ((ServerLevel)level()).sendParticles(ballParticle, corePos.x,corePos.y,corePos.z, 30,0.05f,0.05f,0.05f,0.5f);
                ballParticle.size = 0.5f;
                ((ServerLevel)level()).sendParticles(ballParticle, corePos.x,corePos.y,corePos.z, 30,0.05f,0.05f,0.05f,0.5f);

                ballParticle.size = 0.25f;
                ((ServerLevel)level()).sendParticles(ballParticle, corePos.x,corePos.y,corePos.z, 30,0.05f,0.05f,0.05f,0.5f);


                this.setRemoved(RemovalReason.DISCARDED);
            }

            age++;


        }else{
            if (tickCount % 2 == 0) {
                float size = Mth.clamp(tickCount / (float) BossAnims.DIVINE_GEAR_SUMMON.get().getAnimTime() * 2,0,1);
                BallParticleOptions ballParticle = BallParticleOptions.builder()
                        .color(1f,0.8f, 0.3f)
                        .scalingOptions(0, 0, 10)
                        .brightness(1)
                        .size(size)
                        .build();

                level().addParticle(ballParticle, true, corePos.x, corePos.y, corePos.z, 0, 0, 0);
            }
        }
    }

    public Player getOwner(){
        if (owner != null && level() instanceof ServerLevel serverLevel){
            return serverLevel.getPlayerByUUID(owner);
        }
        return null;
    }

    public void setOnCooldown(){
        this.cooldown =  BossConfigs.BOSS_CONFIG.get().itemConfig.divineGearAttackCooldown;
    }

    private LivingEntity getTarget() {
        var entities = FDTargetFinder.getEntitiesInSphere(Mob.class, level(), this.getCorePos(), BossConfigs.BOSS_CONFIG.get().itemConfig.divineGearAttackRadius, (living) -> {
            return true;
        });

        entities = new ArrayList<>(entities.stream().filter(living -> {
            if (living.isDeadOrDying()){
                return false;
            }
            ClipContext clipContext = new ClipContext(this.getCorePos(), living.getBoundingBox().getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
            var res = level().clip(clipContext);
            return res.getType() == HitResult.Type.MISS;
        }).toList());

        if (entities.isEmpty()) return null;

        var owner = getOwner();
        if (entities.contains(owner.getLastHurtMob())) {
            return owner.getLastHurtMob();
        } else {
            entities.sort(Comparator.comparingDouble(living -> living.getBoundingBox().getCenter().distanceTo(this.getCorePos())));
            return entities.get(0);
        }
    }

    private Vec3 getCorePos(){
        Vec3 corePos = this.position().add(0,1.65,0);
        return corePos;
    }

    private void sendChargeParticles(int prepareTime){
        for (int i = 0; i < 3; i++) {
            StripeParticleOptions stripeParticleOptions = StripeParticleOptions.createHorizontalCircling(
                    new FDColor(1f, 0.3f, 0.2f, 1f), new FDColor(1f, 0.6f, 0.2f, 1f),
                    new Vec3(0, 1, 0), i / 3f * FDMathUtil.FPI * 2, 0.1f, prepareTime, 40, 0, 3, 0.5f,
                    0.75f, true, true
            );


            FDLibCalls.sendParticles((ServerLevel) level(), stripeParticleOptions, this.getCorePos(), 120);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
        if (tag.contains("playerOwner")){
            this.owner = tag.getUUID("playerOwner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
        if (owner != null){
            tag.putUUID("playerOwner", owner);
        }
    }

}
