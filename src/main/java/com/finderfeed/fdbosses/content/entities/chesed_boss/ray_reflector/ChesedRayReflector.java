package com.finderfeed.fdbosses.content.entities.chesed_boss.ray_reflector;

import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEffects;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.TransitionAnimation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class ChesedRayReflector extends FDEntity {

    public static final String ACTIVE_LAYER = "ACTIVATION";

    public static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(ChesedRayReflector.class, EntityDataSerializers.BOOLEAN);

    private static float ANIMATION_SPEED = 1;

    private int activeTicker = 0;
    public int activeTickerO;

    private static FDModel clientModel;

    public ChesedRayReflector(EntityType<?> type, Level level) {
        super(type, level);
        if (clientModel == null && level.isClientSide){
            clientModel = new FDModel(BossModels.CHESED_RAY_REFLECTOR.get());
        }
    }

    @Override
    public void tick() {
        super.tick();
        activeTickerO = activeTicker;
        if (!level().isClientSide){
            this.handleActivation(20);
            this.handleAnimation();
        }else{
            this.handleClientTicker();
            this.activationParticles();
        }
    }

    private void activationParticles(){
        float modelScale = 0.8f;
        var animTime = BossAnims.RAY_REFLECTOR_ACTIVATE.get().getAnimTime();
        int connectTime = animTime / 2 + 2;
        if (this.isActivating()){



            if (activeTicker <= connectTime){

                float speed = 0;
                float friction = 1;
                int count = 10;
                float particleSize = 0.1f;
                if (activeTicker == connectTime){
                    speed = 10f;
                    count = 100;
                    particleSize = 0.15f;
                    friction = 0.7f;
                }

                Vector3f pos1 = this.getModelPartPosition(this,"bone",  clientModel).mul(modelScale);
                Vector3f pos2 = this.getModelPartPosition(this,"p1",  clientModel).mul(modelScale);

                Vector3f dir1 = this.transformPoint(this,new Vector3f(1,0f,0f),"bone",clientModel).mul(modelScale).sub(pos1).mul(-1);
                Vector3f dir2 = this.transformPoint(this,new Vector3f(1,0f,0f),"p1",clientModel).mul(modelScale).sub(pos2);

                Vector3f hdir1 = this.transformPoint(this,new Vector3f(0,1f,0f),"bone",clientModel).mul(modelScale).sub(pos1);
                Vector3f hdir2 = this.transformPoint(this,new Vector3f(0,1f,0f),"p1",clientModel).mul(modelScale).sub(pos2);

                float voffs = 1.25f;
                float voffs2 = 1.4f;
                for (int i = 0; i < count;i++) {

                    float r1 = random.nextFloat() * voffs * 2 - voffs;
                    float r2 = random.nextFloat() * voffs2 * 2 - voffs2;

                    float r1hmod = 1 - Math.abs(r1) / voffs;
                    float r2hmod = 1 - Math.abs(r2) / voffs;

                    float r1hrandom = random.nextFloat() * 0.5f - 0.4f;
                    float r2hrandom = random.nextFloat() * 0.5f - 0.4f;

                    Vec3 speed1 = new Vec3(
                            -hdir1.x * speed,
                            0,
                            -hdir1.z * speed
                    ).normalize();
                    Vec3 speed2 = new Vec3(
                            -hdir2.x * speed,
                            0,
                            -hdir2.z * speed
                    ).normalize();

                    level().addParticle(BallParticleOptions.builder()
                                    .size(particleSize)
                                    .friction(friction * random.nextFloat())
                                    .scalingOptions(0, 0, 10 + random.nextInt(5))
                                    .color(100, 230, 255)
                            .build(),true,
                            pos1.x + this.getX() + dir1.x * r1 - hdir1.x * (voffs - 1) + hdir1.x * voffs * r1hmod +  hdir1.x * r1hrandom,
                            pos1.y + this.getY() + dir1.y * r1 - hdir1.y * (voffs - 1) + hdir1.y * voffs * r1hmod +  hdir1.y * r1hrandom,
                            pos1.z + this.getZ() + dir1.z * r1 - hdir1.z * (voffs - 1) + hdir1.z * voffs * r1hmod +  hdir1.z * r1hrandom,
                            speed1.x,
                            speed1.y,
                            speed1.z
                    );

                    level().addParticle(BallParticleOptions.builder()
                                    .size(particleSize)
                                    .friction(friction * random.nextFloat())
                                    .scalingOptions(0, 0, 10 + random.nextInt(5))
                                    .color(100, 230, 255)
                            .build(),true,
                            pos2.x + this.getX() + dir2.x * r2 - hdir2.x * (voffs - 1) + hdir2.x * voffs2 * r2hmod +  hdir2.x * r2hrandom,
                            pos2.y + this.getY() + dir2.y * r2 - hdir2.y * (voffs - 1) + hdir2.y * voffs2 * r2hmod +  hdir2.y * r2hrandom,
                            pos2.z + this.getZ() + dir2.z * r2 - hdir2.z * (voffs - 1) + hdir2.z * voffs2 * r2hmod +  hdir2.z * r2hrandom,
                            speed2.x,
                            speed2.y,
                            speed2.z
                    );
                }
            }
        }
    }

    public boolean isActivating(){

        var ticker = this.getAnimationSystem().getTicker(ACTIVE_LAYER);
        if (ticker == null) return false;

        var animation = ticker.getAnimation();
        if (animation instanceof TransitionAnimation animation1){
            animation = animation1.getTransitionTo();
        }

        if (animation != BossAnims.RAY_REFLECTOR_ACTIVATE.get()){
            return false;
        }

        return !ticker.isReversed();
    }

    public int getAnimationLength(){
        return Math.round(BossAnims.RAY_REFLECTOR_ACTIVATE.get().getAnimTime() / ANIMATION_SPEED);
    }

    private void handleClientTicker(){
        var system = this.getAnimationSystem();
        int activationAnimLength = this.getAnimationLength();
        var ticker = system.getTicker(ACTIVE_LAYER);
        if (ticker == null){
            activeTicker = Mth.clamp(activeTicker - 1,0,activationAnimLength);
            return;
        }

        var animation = ticker.getAnimation();
        if (animation instanceof TransitionAnimation transitionAnimation){
            animation = transitionAnimation.getTransitionTo();
        }

        if (animation == BossAnims.RAY_REFLECTOR_ACTIVATE.get()){
            if (ticker.isReversed()){
                activeTicker = Mth.clamp(activeTicker - 1,0,activationAnimLength);
            }else{
                activeTicker = Mth.clamp(activeTicker + 1,0,activationAnimLength);
            }
        }else{
            activeTicker = Mth.clamp(activeTicker - 1,0,activationAnimLength);
        }
    }

    private void handleAnimation(){
        var system = this.getAnimationSystem();
        int activationAnimLength = this.getAnimationLength();
        if (this.isActive()){
            var animTicker = system.getTicker(ACTIVE_LAYER);
            if (animTicker == null){
                system.startAnimation(ACTIVE_LAYER, AnimationTicker.builder(BossAnims.RAY_REFLECTOR_ACTIVATE)
                                .setSpeed(ANIMATION_SPEED)
                                .setToNullTransitionTime(0)
                        .build());
            }
            activeTicker = Mth.clamp(activeTicker + 1, 0, activationAnimLength);
        }else{
            if (activeTicker != 0){
                if (activeTicker != activationAnimLength) {
                    activeTicker = Mth.clamp(activeTicker + 1, 0, activationAnimLength);
                }else{
                    system.startAnimation(ACTIVE_LAYER, AnimationTicker.builder(BossAnims.RAY_REFLECTOR_ACTIVATE)
                            .setSpeed(ANIMATION_SPEED)

                            .setToNullTransitionTime(0)
                                    .reversed()
                                    .setLoopMode(Animation.LoopMode.ONCE)
                            .build());
                    activeTicker = 0;
                }
            }
        }
    }

    private void handleActivation(float detectionRadius){

        AABB box = new AABB(-detectionRadius,-1,-detectionRadius,detectionRadius,3,detectionRadius)
                .move(this.position());

        Vec3 hpos = this.position().multiply(1,0,1);

        List<Player> players = this.level().getEntitiesOfClass(Player.class, box, player->{
            if (!player.hasEffect(BossEffects.CHESED_ENERGIZED.get()) || player.isSpectator()){
                return false;
            }
            Vec3 playerPos = player.position().multiply(1,0,1);
            double distance = playerPos.distanceTo(hpos);
            return distance <= detectionRadius;
        });

        if (players.isEmpty()){
            this.setActive(false);
        }else{
            this.setActive(true);
        }

    }

    public int getActiveTicker() {
        return activeTicker;
    }

    public void setActive(boolean active){
        this.entityData.set(ACTIVE,active);
    }

    public boolean isActive(){
        return this.entityData.get(ACTIVE);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ACTIVE,false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    public boolean isPickable() {
        return true;
    }
}
