package com.finderfeed.fdbosses.content.entities.chesed_boss.ray_reflector;

import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEffects;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ChesedRayReflector extends FDEntity {

    public static final String ACTIVE_LAYER = "ACTIVATION";

    public static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(ChesedRayReflector.class, EntityDataSerializers.BOOLEAN);

    private int activeTicker = 0;

    public ChesedRayReflector(EntityType<?> type, Level level) {
        super(type, level);
    }


    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            this.handleActivation(5);
            this.handleAnimation();
        }
    }

    private void handleAnimation(){
        var system = this.getSystem();
        int activationAnimLength = BossAnims.RAY_REFLECTOR_ACTIVATE.get().getAnimTime();
        if (this.isActive()){
            var animTicker = system.getTicker(ACTIVE_LAYER);
            if (animTicker != null){
                system.startAnimation(ACTIVE_LAYER, AnimationTicker.builder(BossAnims.RAY_REFLECTOR_ACTIVATE)
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
            if (!player.hasEffect(BossEffects.CHESED_ENERGIZED) || player.isSpectator()){
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

    public void setActive(boolean active){
        this.entityData.set(ACTIVE,active);
    }

    public boolean isActive(){
        return this.entityData.get(ACTIVE);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

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
