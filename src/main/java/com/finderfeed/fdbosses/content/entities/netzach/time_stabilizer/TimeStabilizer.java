package com.finderfeed.fdbosses.content.entities.netzach.time_stabilizer;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class TimeStabilizer extends FDEntity implements AutoSerializable {

    public static final EntityDataAccessor<Boolean> DESTABILIZED = SynchedEntityData.defineId(TimeStabilizer.class, EntityDataSerializers.BOOLEAN);

    public static final int MAX_RECHARGE_TIME = 200;

    public static final int TERMINAL_DISTANCE = 5;

    private Player interactingPlayer = null;

    @SerializableField
    private int rechargeTime = MAX_RECHARGE_TIME;

    @SerializableField
    private float currentDisplacement = 0;

    @SerializableField
    private float targetDisplacement = 0;

    public TimeStabilizer(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!level().isClientSide){
            if (this.isDestabilized() && hand == InteractionHand.MAIN_HAND){
                if (interactingPlayer == null) {
                    PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenTimeStabilizerScreenPacket(this, currentDisplacement, targetDisplacement));
                    interactingPlayer = player;
                    return InteractionResult.CONSUME;
                }else{
                    player.sendSystemMessage(Component.translatable("fdbosses.word.already_being_interacted_with"));
                }
            }
        }
        return super.interact(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()){


            if (interactingPlayer != null){
                if (interactingPlayer.isRemoved() || interactingPlayer.isDeadOrDying() || !this.isDestabilized() || interactingPlayer.distanceTo(this) > TERMINAL_DISTANCE){
                    interactingPlayer = null;
                }
            }

            this.getEntityData().set(DESTABILIZED, this.isDestabilized());
            rechargeTime = Mth.clamp(rechargeTime - 1, 0, Integer.MAX_VALUE);
            if (rechargeTime == 1){
                this.targetDisplacement = this.currentDisplacement + BossUtil.randomPlusMinus() * 2160 + BossUtil.randomPlusMinus() * random.nextFloat() * 1080f;
            }

        }else{

            var animSystem = this.getAnimationSystem();

            if (this.isDestabilized()){
                level().addParticle(ParticleTypes.FLAME, this.getX() + 0.5, this.getY() + 1, this.getZ(), 0, 1, 0);

                animSystem.stopAnimation("gears");
                animSystem.stopAnimation("clock");

                animSystem.startAnimation("gears_broken", AnimationTicker.builder(BossAnims.TIME_STABILIZER_GEARS_BROKEN)
                        .setToNullTransitionTime(0)
                        .build());

                animSystem.startAnimation("clock_broken", AnimationTicker.builder(BossAnims.TIME_STABILIZER_CLOCK_BROKEN)
                        .setToNullTransitionTime(0)
                        .build());


            }else{


                animSystem.stopAnimation("gears_broken");
                animSystem.stopAnimation("clock_broken");


                animSystem.startAnimation("gears", AnimationTicker.builder(BossAnims.TIME_STABILIZER_GEARS)
                        .setToNullTransitionTime(0)
                        .build());

                animSystem.startAnimation("clock", AnimationTicker.builder(BossAnims.TIME_STABILIZER_CLOCK)
                        .setToNullTransitionTime(0)
                        .build());

            }

        }
    }

    public void setRechargeTime(int rechargeTime) {
        this.rechargeTime = Mth.clamp(rechargeTime, 0, Integer.MAX_VALUE);
        if (this.isDestabilized()){
            this.setTargetDisplacement();
        }
        this.getEntityData().set(DESTABILIZED, this.isDestabilized());
    }

    private void setTargetDisplacement(){
        this.targetDisplacement = this.currentDisplacement + BossUtil.randomPlusMinus() * 2160 + BossUtil.randomPlusMinus() * random.nextFloat() * 1080f;
    }

    public boolean isDestabilized(){
        if (level().isClientSide){
            return this.getEntityData().get(DESTABILIZED);
        }
        return rechargeTime == 0;
    }

    public void screenWasClosed(boolean solved, float currentDisplacement){
        this.interactingPlayer = null;
        this.currentDisplacement = currentDisplacement;
        if (solved){
            this.setRechargeTime(MAX_RECHARGE_TIME);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {
        p_326003_.define(DESTABILIZED, false);

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad("addData", tag);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave("addData", tag);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

}
