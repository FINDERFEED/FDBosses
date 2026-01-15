package com.finderfeed.fdbosses.content.items.malkuth;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.FDBossesServerScheduler;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_crush.MalkuthCrushAttack;
import com.finderfeed.fdbosses.content.util.Undismountable;
import com.finderfeed.fdbosses.init.*;
import com.finderfeed.fdlib.FDHelpers;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.FDTargetFinder;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

public class MalkuthFistChain extends FDEntity implements Undismountable {

    public static final EntityDataAccessor<Boolean> IS_HOOK = SynchedEntityData.defineId(MalkuthFistChain.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(MalkuthFistChain.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> HOOKED_TO = SynchedEntityData.defineId(MalkuthFistChain.class, EntityDataSerializers.INT);

    private UUID ownerPlayer;
    private UUID hookFlyingTo;

    private Vec3 currentPlayerHookPos = null;
    private int pullingPlayerTickerTime = -1;
    private int pullingPlayerTicker = -1;

    private Vec3 vectorBetween;

    private Direction cachedDirection;

    public Vec3 cachedDeltaMovement;


    public static void summon(Player owner, boolean isHook){

        MalkuthFistChain malkuthFistChain = new MalkuthFistChain(BossEntities.MALKUTH_FIST_CHAIN.get(), owner.level());
        malkuthFistChain.setPos(owner.getEyePosition());
        malkuthFistChain.getEntityData().set(IS_HOOK, isHook);
        malkuthFistChain.ownerPlayer = owner.getUUID();
        malkuthFistChain.setDeltaMovement(owner.getLookAngle().scale(3));
        owner.level().addFreshEntity(malkuthFistChain);

    }

    public MalkuthFistChain(EntityType<?> type, Level level) {
        super(type, level);
        this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.MALKUTH_FIST_FLYING).build());
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            if (ownerPlayer == null){
                this.setRemoved(RemovalReason.DISCARDED);
                this.onStopChaining();
                return;
            }
            this.checkOwnerValidity();

            var owner = this.getOwner();
            if (owner != null){
                this.getEntityData().set(OWNER_ID, owner.getId());
            }

            var hookedTo = this.getHookedTo();
            if (hookedTo != null){
                this.getEntityData().set(HOOKED_TO, hookedTo.getId());
            }

        }

        if (this.isHook()) {
            this.tickHook();
        }else{
            this.tickPullToBlocks();
        }

        var hookedTo = this.getHookedTo();
        if (hookedTo == null) {
            this.setPos(this.position().add(this.getDeltaMovement()));
        }else{
            Vec3 pos = this.getHookedToPos(hookedTo);
            this.setPos(pos.x,pos.y,pos.z);
        }
        this.tickPullingTime();
    }

    public Vec3 getHookedToPos(LivingEntity hookedTo){
        return hookedTo.getBoundingBox().getCenter();
    }

    public LivingEntity getHookedTo(){
        if (level() instanceof ServerLevel serverLevel){
            if (hookFlyingTo != null && serverLevel.getEntity(hookFlyingTo) instanceof LivingEntity livingEntity){
                return livingEntity;
            }
        }else{
            if (level().getEntity(this.getEntityData().get(HOOKED_TO)) instanceof LivingEntity livingEntity){
                return livingEntity;
            }

        }
        return null;
    }

    @Override
    public void setDeltaMovement(Vec3 vec) {
        super.setDeltaMovement(vec);
        if (!vec.equals(Vec3.ZERO)) {
            this.cachedDeltaMovement = vec;
        }
    }

    public void tickPullToBlocks(){
        if (!level().isClientSide) {
            Vec3 deltaMovement = this.getDeltaMovement();
            if (!deltaMovement.equals(Vec3.ZERO)) {

                if (tickCount > 2) {
                    level().playSound(null, this.getX(), this.getY(), this.getZ(), BossSounds.MALKUTH_CHAIN_PULL.get(), SoundSource.HOSTILE, 4f, 0.85f);
                }

                Vec3 start = this.position();
                Vec3 end = start.add(deltaMovement.scale(2));
                ClipContext clipContext = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
                var res = level().clip(clipContext);
                if (res.getType() != HitResult.Type.MISS) {
                    Vec3 location = res.getLocation();
                    this.teleportTo(location.x, location.y, location.z);
                    this.setDeltaMovement(Vec3.ZERO);
                    cachedDirection = res.getDirection();
                    this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.MALKUTH_FIST_GRAB).build());
                }
            }else{


                level().playSound(null,this.getX(),this.getY(),this.getZ(), BossSounds.MALKUTH_CHAIN_PULL.get(), SoundSource.HOSTILE, 4f, 0.9f);

                this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.MALKUTH_FIST_GRAB).build());

                var owner = this.getOwner();
                if (owner != null){
                    if (owner.getVehicle() != this){
                        if (!owner.startRiding(this,true)){
                            this.setRemoved(RemovalReason.DISCARDED);
                            this.onStopChaining();
                        }
                    }else{

                        if ((this.pullingPlayerTickerTime != -1 && this.pullingPlayerTicker > this.pullingPlayerTickerTime + 1)){
                            owner.stopRiding();


                            int cooldownIKnowYouAreLookingHereToChangeThisField = BossConfigs.BOSS_CONFIG.get().itemConfig.iceFireGauntletCooldown;
                            owner.getCooldowns().addCooldown(BossItems.MALKUTH_FIST.get(), cooldownIKnowYouAreLookingHereToChangeThisField);

                            this.setRemoved(RemovalReason.DISCARDED);
                            this.onStopChaining();

                            Direction direction = cachedDirection != null ? cachedDirection : Direction.UP;


                            BossUtil.malkuthPlayerFireballExplosionParticles((ServerLevel) level(), this.position(), MalkuthAttackType.FIRE);
                            PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                                    .frequency(50)
                                    .amplitude(1.5f)
                                    .inTime(0)
                                    .stayTime(0)
                                    .outTime(10)
                                    .build(),this.position(),40);

                            MalkuthCrushAttack.summon(level(), this.position().add(direction.getStepX() * 0.1f, direction.getStepY() * 0.1f, direction.getStepZ() * 0.1f), 0, direction, null);

                            level().playSound(null,this.getX(),this.getY(),this.getZ(), BossSounds.MALKUTH_FIREBALL_EXPLOSION.get(), SoundSource.HOSTILE, 3f, 1f);
                            level().playSound(null, this.position().x,this.position().y,this.position().z, BossSounds.ROCK_IMPACT.get(), SoundSource.PLAYERS, 3f, 0.8f);
                            level().playSound(null, this.position().x,this.position().y,this.position().z, BossSounds.MALKUTH_SWORD_EARTH_IMPACT.get(), SoundSource.PLAYERS, 3f, 0.8f);

                            if (cachedDirection != null){

                                Vec3 n = new Vec3(
                                        cachedDirection.getStepX(),
                                        cachedDirection.getStepY(),
                                        cachedDirection.getStepZ()
                                );

                                double offs = n.dot(new Vec3(0,-1,0));
                                offs = (offs + 1);

                                Vec3 tppos = this.position().add(n.scale(offs));

                                owner.teleportTo(tppos.x,tppos.y,tppos.z);

                            }


                            float damage = BossConfigs.BOSS_CONFIG.get().itemConfig.iceFireGauntletCrushDamage;

                            for (var entity : FDTargetFinder.getEntitiesInSphere(LivingEntity.class, level(), this.position(), 3)){
                                if (entity == owner) continue;

                                entity.setRemainingFireTicks(100);
                                entity.hurt(level().damageSources().playerAttack(owner), damage);

                                Vec3 between = entity.position().subtract(this.position());

                                Vec3 speed = between.normalize().scale(0.5).add(0,0.25,0);

                                if (entity instanceof ServerPlayer serverPlayer){
                                    FDLibCalls.setServerPlayerSpeed(serverPlayer, speed);
                                    serverPlayer.hasImpulse = true;
                                }else{
                                    entity.setDeltaMovement(speed);
                                }

                            }

                        }
                    }
                }
            }
        }

    }

    public void tickHook(){
        if (!level().isClientSide){

            if (hookFlyingTo == null) {

                if (tickCount > 2) {
                    level().playSound(null, this.getX(), this.getY(), this.getZ(), BossSounds.MALKUTH_CHAIN_PULL.get(), SoundSource.HOSTILE, 4f, 0.85f);
                }
                Vec3 deltaMovement = this.getDeltaMovement();
                Vec3 start = this.position().add(deltaMovement.reverse());
                Vec3 end = start.add(deltaMovement.scale(2));


                var entities = FDHelpers.traceEntities(level(), start,end, 0.15f, (livingEntity) -> {
                    return livingEntity instanceof LivingEntity && livingEntity != this.getOwner();
                });

                Entity entity;

                if (!entities.isEmpty() && (entity = entities.get(0)) != null) {
                    hookFlyingTo = entity.getUUID();
                    this.getEntityData().set(HOOKED_TO, entity.getId());
                }else{

                    ClipContext clipContext = new ClipContext(this.position(),end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
                    var res = level().clip(clipContext);
                    if (res.getType() != HitResult.Type.MISS){
                        this.setRemoved(RemovalReason.DISCARDED);
                        this.onStopChaining();
                    }
                }
            }else{

                this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.MALKUTH_FIST_GRAB).build());

                var entity = this.getEntityOnHook();
                var owner = this.getOwner();
                if (owner != null && entity != null && !entity.isDeadOrDying()) {
                    this.setDeltaMovement(Vec3.ZERO);
                    if (owner.getVehicle() == null) {

                        if (!owner.startRiding(this, true)){
                            this.setRemoved(RemovalReason.DISCARDED);
                            this.onStopChaining();
                        }

                    }else{
                        if (owner.getVehicle() == this) {

                            level().playSound(null,this.getX(),this.getY(),this.getZ(), BossSounds.MALKUTH_CHAIN_PULL.get(), SoundSource.HOSTILE, 4f, 0.9f);

                            if (this.pullingPlayerTicker <= pullingPlayerTickerTime / 2){
                                vectorBetween = entity.position().subtract(owner.position());
                            }

                            if ((this.pullingPlayerTickerTime != -1 && this.pullingPlayerTicker > this.pullingPlayerTickerTime + 1)) {
                                owner.stopRiding();

                                PacketDistributor.sendToPlayer((ServerPlayer) owner, new PlayerForceAttackEntityPacket(entity));

                                BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                                        .size(5f)
                                        .scalingOptions(1,0,2)
                                        .color(1f,0.6f,0.2f)
                                        .brightness(2)
                                        .build();
                                ((ServerLevel)level()).sendParticles(ballParticleOptions, entity.getX(),entity.getY() + entity.getBbHeight() / 2, entity.getZ(),1,0,0,0,0);

                                BallParticleOptions ballParticleOptions2 = BallParticleOptions.builder()
                                        .size(0.5f)
                                        .friction(0.9f)
                                        .scalingOptions(0,0,10)
                                        .color(1f,0.6f,0.2f)
                                        .brightness(2)
                                        .build();
                                BallParticleOptions ballParticleOptions3 = BallParticleOptions.builder()
                                        .size(0.5f)
                                        .friction(0.9f)
                                        .scalingOptions(0,0,10)
                                        .color(0.2f,0.6f,1f)
                                        .brightness(2)
                                        .build();
                                ((ServerLevel)level()).sendParticles(ballParticleOptions2, entity.getX(),entity.getY() + entity.getBbHeight() / 2, entity.getZ(),20,0.1,0.1,0.1,0.25);
                                ((ServerLevel)level()).sendParticles(ballParticleOptions3, entity.getX(),entity.getY() + entity.getBbHeight() / 2, entity.getZ(),20,0.1,0.1,0.1,0.25);




                                FDBossesServerScheduler.addDelayedAction(2,(server)->{
                                    if (vectorBetween != null) {
                                        Vec3 between = vectorBetween;
                                        Vec3 movement = between.multiply(-1, 0, -1)
                                                .normalize()
                                                .add(0,1,0)
                                                .scale(1.25);
                                        FDLibCalls.setServerPlayerSpeed((ServerPlayer) owner, movement);
                                        owner.hasImpulse = true;
                                        owner.setIgnoreFallDamageFromCurrentImpulse(true);
                                        owner.currentImpulseImpactPos = owner.position();
                                    }
                                });


                                level().playSound(null,this.getX(),this.getY(),this.getZ(), BossSounds.MALKUTH_SWORD_EARTH_IMPACT.get(), SoundSource.HOSTILE, 2f, 1f);

                                int cooldown = BossConfigs.BOSS_CONFIG.get().itemConfig.iceFireGauntletCooldown;

                                var item = owner.getUseItem();
                                if (owner.getUseItem().is(BossItems.MALKUTH_FIST.get())){
                                    var data = item.get(BossDataComponents.MALKUTH_FIST_COMPONENT);
                                    data.setCanSkipCooldown(true);
                                    data.setEntityHookCooldown(cooldown);
                                    item.set(BossDataComponents.MALKUTH_FIST_COMPONENT, new MalkuthFistDataComponent(data));
                                }


                                owner.getCooldowns().addCooldown(BossItems.MALKUTH_FIST.get(),cooldown);


                                this.setRemoved(RemovalReason.DISCARDED);
                                this.onStopChaining();
                            }
                        }else{
                            this.setRemoved(RemovalReason.DISCARDED);
                            this.onStopChaining();
                        }
                    }

                }else{
                    this.setRemoved(RemovalReason.DISCARDED);
                    this.onStopChaining();
                }
            }

        }else{

        }
    }


    private LivingEntity getEntityOnHook(){
        if (level() instanceof ServerLevel serverLevel) {
            if (hookFlyingTo != null && serverLevel.getEntity(hookFlyingTo) instanceof LivingEntity livingEntity) {
                return livingEntity;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    private void checkOwnerValidity(){
        var owner = this.getOwner();
        double checkDistance = this.getHookedTo() != null || this.getDeltaMovement().equals(Vec3.ZERO) ? 40 : 30;
        if (owner == null || this.distanceTo(owner) > checkDistance || !owner.getUseItem().is(BossItems.MALKUTH_FIST.get())){
            this.setRemoved(RemovalReason.DISCARDED);
            this.onStopChaining();
        }
    }

    public boolean isHook(){
        return this.getEntityData().get(IS_HOOK);
    }

    public Player getOwner(){
        if (!level().isClientSide){
            if (ownerPlayer != null){
                return (((ServerLevel)level()).getPlayerByUUID(ownerPlayer));
            }else{
                return null;
            }
        }else{
            if (level().getEntity(this.getEntityData().get(OWNER_ID)) instanceof Player player){
                return player;
            }else{
                return null;
            }
        }
    }

    private void onStopChaining(){
        var owner = this.getOwner();
        if (owner != null){
            if (owner.getUseItem().is(BossItems.MALKUTH_FIST.get())){
                owner.stopUsingItem();
            }
        }
    }


    @Override
    public Vec3 getPassengerRidingPosition(Entity entity) {

        if (currentPlayerHookPos == null){
            currentPlayerHookPos = entity.position().add(0,entity.getBbHeight() /2,0);
            double distance = currentPlayerHookPos.distanceTo(this.position());
            this.pullingPlayerTickerTime = (int) Math.ceil(distance/2);
            if (this.pullingPlayerTickerTime <= 0){
                this.pullingPlayerTickerTime = 1;
            }
            this.pullingPlayerTicker = 0;
        }

        Entity vehicle = this.getVehicle();

        Vec3 thisPos;
        if (vehicle != null){
            thisPos = this.position().add(0,-vehicle.getBbHeight() / 2,0);
        }else{
            thisPos = this.position();
        }

        float p = Mth.clamp(this.pullingPlayerTicker / (float) this.pullingPlayerTickerTime,0,1);

        Vec3 interpolated = FDMathUtil.interpolateVectors(currentPlayerHookPos, thisPos, FDEasings.easeIn(p));

        return interpolated;
    }

    private void tickPullingTime(){
        if (this.pullingPlayerTickerTime != -1){
            this.pullingPlayerTicker++;
        }
    }

    @Override
    public Vec3 getVehicleAttachmentPoint(Entity entity) {
        var bbHeight = entity.getBbHeight();
        return new Vec3(0,bbHeight / 2,0);
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }



    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(IS_HOOK, true);
        builder.define(OWNER_ID, -1);
        builder.define(HOOKED_TO, -1);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("ownerPlayer")) {
            this.ownerPlayer = tag.getUUID("ownerPlayer");
        }
        if (tag.contains("hookFlyingTo")){
            this.hookFlyingTo = tag.getUUID("hookFlyingTo");
        }
        this.getEntityData().set(IS_HOOK, tag.getBoolean("isHook"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (ownerPlayer != null){
            tag.putUUID("ownerPlayer", this.ownerPlayer);
        }
        if (hookFlyingTo != null){
            tag.putUUID("hookFlyingTo", this.hookFlyingTo);
        }
        tag.putBoolean("isHook", this.getEntityData().get(IS_HOOK));
    }

    @EventBusSubscriber(modid = FDBosses.MOD_ID)
    public static class Events {

        @SubscribeEvent
        public static void hurtEvent(LivingIncomingDamageEvent event){
            var entity = event.getEntity();
            if (entity instanceof ServerPlayer serverPlayer){
                if (serverPlayer.getVehicle() instanceof MalkuthFistChain malkuthFistChain) {
                    var source = event.getSource().getEntity();
                    if (source != null) {
                        var sourceBB = source.getBoundingBox().inflate(2);
                        if (sourceBB.intersects(serverPlayer.getBoundingBox())) {
                            event.setCanceled(true);
                            event.setInvulnerabilityTicks(20);
                        }
                    }else{
                        if (event.getSource().is(DamageTypes.IN_WALL)){
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }

    }

}
