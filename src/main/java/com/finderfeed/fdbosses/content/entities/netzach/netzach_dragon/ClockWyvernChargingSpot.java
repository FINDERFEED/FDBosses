package com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.vanilla_like.SpriteParticleOptions;
import com.finderfeed.fdbosses.content.entities.FDOwnableEntity;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ClockWyvernChargingSpot extends FDOwnableEntity implements AutoSerializable {

    public static ClockWyvernChargingSpot summon(ClockWyvern clockWyvern){
        ClockWyvernChargingSpot chargingSpot = new ClockWyvernChargingSpot(BossEntities.CLOCK_WYVERN_CHARGING_SPOT.get(), clockWyvern.level());
        chargingSpot.setOwner(clockWyvern);
        chargingSpot.setPos(clockWyvern.position());
        clockWyvern.level().addFreshEntity(chargingSpot);
        chargingSpot.setOnWyvernPosition();

        chargingSpot.getAnimationSystem().startAnimation("START", AnimationTicker.builder(BossAnims.CLOCK_WYVERN_CHARGING_BODY_START)
                .setTransitionTime(0)
                .build());
        return chargingSpot;
    }

    private static FDModel model;

    @SerializableField
    private int despawnTicker = -1;

    private int watchdog = 2;

    public ClockWyvernChargingSpot(EntityType<? extends ClockWyvernChargingSpot> type, Level level) {
        super(type, level);
        if (model == null){
            model = new FDModel(BossModels.CLOCK_WYVERN_CHARGING_SPOT.get());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.CLOCK_WYVERN_CHARGING_BODY_WROOM_WROOM)
                            .setSpeed(1.5f)
                    .build());
            if (despawnTicker == -1){
                var owner = this.getOwner();
                if (owner instanceof ClockWyvern clockWyvern){
                    if (watchdog-- == 0 || clockWyvern.isDeadOrDying() || !clockWyvern.isChargingSpotAlive()){
                        this.kill();
                    }
                    this.setOnWyvernPosition();
                }else{
                    this.kill();
                }
            }else{
                this.getAnimationSystem().startAnimation("END", AnimationTicker.builder(BossAnims.CLOCK_WYVERN_CHARGING_BODY_START)
                        .setLoopMode(Animation.LoopMode.HOLD_ON_LAST_FRAME)
                                .setSpeed(0.5f)
                        .reversed()
                        .build());
                despawnTicker = Mth.clamp(despawnTicker - 1, 0, Integer.MAX_VALUE);
                if (despawnTicker == 0){
                    this.setRemoved(RemovalReason.DISCARDED);
                }
            }
        }else{
            this.wroomWroomParticles();
        }
    }

    private void wroomWroomParticles(){
        var animSystem = this.getAnimationSystem();
        if (animSystem.getTickerAnimation("END") != null || animSystem.getTickerAnimation("START") != null) return;

        if (this.getOwner() instanceof ClockWyvern clockWyvern){

            Matrix4f mat = clockWyvern.getModelPartTransformation(clockWyvern, "body_back", ClockWyvern.getServerAnimatedModel(), 1);

            for (int i = 1; i <= 6; i++){
                var transform = this.getModelPartTransformation(this, "inner_pump" + i, model, 1);
                mat.mul(transform, transform);


                Vector3f pos = transform.transformPosition(new Vector3f(0,0.5f,0));
                Vector3f direction = transform.transformDirection(new Vector3f(0,1,0));

                Vec3 worldPos = clockWyvern.position().add(
                        pos.x, pos.y, pos.z
                );

                SpriteParticleOptions options = SpriteParticleOptions.builder(BossParticles.YELLOW_SPARK)
                        .size(0.25f)
                        .friction(0.7f)
                        .alphaDecreasing()
                        .quadSizeDecreasing()
                        .xyzRotationSpeed(10 * BossUtil.randomPlusMinus(), 0, 0)
                        .frictionAffectsRotation()
                        .build();

                level().addParticle(options, true,
                        worldPos.x,
                        worldPos.y,
                        worldPos.z,
                        direction.x * 0.5f + random.nextFloat() * 0.2f - 0.1f,
                        direction.y * 0.5f + random.nextFloat() * 0.2f - 0.1f,
                        direction.z * 0.5f + random.nextFloat() * 0.2f - 0.1f
                );

            }
        }


    }

    @Override
    public boolean hurt(DamageSource src, float damage) {
        if (!level().isClientSide){
            if (this.getOwner() instanceof ClockWyvern clockWyvern){
                this.kill();
                clockWyvern.destroyChargingSpot();
            }
        }
        return super.hurt(src, damage);
    }

    public void setOnWyvernPosition(){
        var owner = this.getOwner();
        if (owner instanceof ClockWyvern clockWyvern){
            Matrix4f mat = clockWyvern.getModelPartTransformation(clockWyvern, "body_back", ClockWyvern.getServerAnimatedModel(), 1);
            Vector3f pos = mat.transformPosition(new Vector3f(0, 0, -0.5f));
            var worldPos = clockWyvern.position().add(
                pos.x, pos.y, pos.z
            );
            this.setPos(worldPos);
        }
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return this.getAnimationSystem().getTickerAnimation("END") == null;
    }

    @Override
    public int getTeamColor() {
        return 0xf7363d;
    }

    public void kill(){
        if (this.despawnTicker == -1){
            this.despawnTicker = 25;
        }
    }

    public void feedWatchdog(){
        watchdog = 2;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean syncsOwner() {
        return true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
    }

}
