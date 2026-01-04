package com.finderfeed.fdbosses.content.items.malkuth;

import com.finderfeed.fdbosses.FDBossesServerScheduler;
import com.finderfeed.fdbosses.content.entities.geburah.judgement_bird.JudgementBirdEntity;
import com.finderfeed.fdbosses.content.util.Undismountable;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.FDHelpers;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.util.FDUtil;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

public class MalkuthFistChain extends Entity implements Undismountable {

    public static final EntityDataAccessor<Boolean> IS_HOOK = SynchedEntityData.defineId(MalkuthFistChain.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(MalkuthFistChain.class, EntityDataSerializers.INT);

    private UUID ownerPlayer;
    private UUID hookFlyingTo;

    private Vec3 currentPlayerHookPos = null;
    private int pullingPlayerTickerTime = -1;
    private int pullingPlayerTicker = -1;

    private Vec3 vectorBetween;


    public static void summon(Player owner, boolean isHook){

        MalkuthFistChain malkuthFistChain = new MalkuthFistChain(BossEntities.MALKUTH_FIST_CHAIN.get(), owner.level());
        malkuthFistChain.setPos(owner.getEyePosition());
        malkuthFistChain.getEntityData().set(IS_HOOK, isHook);
        malkuthFistChain.ownerPlayer = owner.getUUID();
        malkuthFistChain.setDeltaMovement(owner.getLookAngle().scale(2));
        owner.level().addFreshEntity(malkuthFistChain);

    }

    public MalkuthFistChain(EntityType<?> type, Level level) {
        super(type, level);
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
            this.checkDistanceToOwner();

            var owner = this.getOwner();
            if (owner != null){
                this.getEntityData().set(OWNER_ID, owner.getId());
            }
        }

        if (this.isHook()) {
            this.tickHook();
        }

        this.setPos(this.position().add(this.getDeltaMovement()));
        this.tickPullingTime();
    }

    public void tickHook(){
        if (!level().isClientSide){

            if (hookFlyingTo == null) {
                Vec3 deltaMovement = this.getDeltaMovement();
                Vec3 start = this.position().add(deltaMovement.reverse());
                Vec3 end = start.add(deltaMovement.scale(2));


                var entities = FDHelpers.traceEntities(level(), start,end, 0.15f, (livingEntity) -> {
                    return livingEntity instanceof LivingEntity && livingEntity != this.getOwner();
                });

                Entity entity;

                if (!entities.isEmpty() && (entity = entities.get(0)) != null) {
                    hookFlyingTo = entity.getUUID();
                }else{
                    ClipContext clipContext = new ClipContext(start,end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
                    var res = level().clip(clipContext);
                    if (res.getType() != HitResult.Type.MISS){
                        this.setRemoved(RemovalReason.DISCARDED);
                        this.onStopChaining();
                    }
                }
            }else{
                var entity = this.getEntityOnHook();
                var owner = this.getOwner();
                if (owner != null && entity != null && !entity.isDeadOrDying()) {
                    this.setDeltaMovement(Vec3.ZERO);
                    if (!this.isPassenger()) {

                        if (!this.startRiding(entity, true)){
                            this.setRemoved(RemovalReason.DISCARDED);
                            this.onStopChaining();
                        }

                        if (owner.getVehicle() != this){
                            if (!owner.startRiding(this, true)){
                                this.setRemoved(RemovalReason.DISCARDED);
                                this.onStopChaining();
                            }
                        }

                    }else{
                        if (owner.getVehicle() == this) {


                            if (this.pullingPlayerTicker <= pullingPlayerTickerTime / 2){
                                vectorBetween = entity.position().subtract(owner.position());
                            }

                            if ((this.pullingPlayerTickerTime != -1 && this.pullingPlayerTicker > this.pullingPlayerTickerTime + 1)) {
                                owner.stopRiding();

                                PacketDistributor.sendToPlayer((ServerPlayer) owner, new PlayerForceAttackEntityPacket(entity));

                                FDBossesServerScheduler.addDelayedAction(2,(server)->{
                                    if (vectorBetween != null) {
                                        Vec3 between = vectorBetween;
                                        Vec3 movement = between.multiply(-1, 0, -1)
                                                .normalize()
                                                .add(0,0.5,0)
                                                .scale(1.25);
                                        FDLibCalls.setServerPlayerSpeed((ServerPlayer) owner, movement);
                                        owner.hasImpulse = true;
                                        owner.setIgnoreFallDamageFromCurrentImpulse(true);
                                        owner.currentImpulseImpactPos = owner.position();
                                    }
                                });


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

    private void checkDistanceToOwner(){
        var owner = this.getOwner();
        if (this.getPassengers().isEmpty() && owner == null || this.distanceTo(owner) > 30){
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
        System.out.println("Stopped chaining");
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

        Vec3 thisPos = this.position();

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
        return new Vec3(0,bbHeight - 0.1,0);
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }



    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(IS_HOOK, true);
        builder.define(OWNER_ID, -1);
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

}
