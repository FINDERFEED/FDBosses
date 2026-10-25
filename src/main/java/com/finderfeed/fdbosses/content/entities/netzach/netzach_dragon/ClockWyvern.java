package com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon;

import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.math.EasingType;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ClockWyvern extends FDEntity {

    public static FDModel animatedModel;
    public static FDModel model;

    public ClockWyvern(EntityType<?> type, Level level) {
        super(type, level);
        if (animatedModel == null){
            animatedModel = new FDModel(BossModels.CLOCK_WYVERN.get());
            model = new FDModel(BossModels.CLOCK_WYVERN.get());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            int limit = 300;

            long repeatedTime = this.level().getGameTime() % limit;
            this.getAnimationSystem().startAnimation("IDLE", AnimationTicker.builder(BossAnims.CLOCK_WYVERN_SPINNING_GEARS)
                    .build());

            this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.CLOCK_WYVERN_BITE_EARTH)
                            .nextAnimation(AnimationTicker.builder(BossAnims.CLOCK_WYVERN_IDLE)
                                    .setTransitionTime(0)
                                    .build())
                            .setTransitionTime(0)
                    .build());

            if (true) return;

            if (repeatedTime < 30){
                this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.CLOCK_WYVERN_START_FLYING)
                                .setSpeed(1f)
                                .setInFadeTime(2)
                        .setOutFadeTime(20)
                        .build());
            }else if (repeatedTime < limit / 3){
                this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.CLOCK_WYVERN_FLYING)
                        .setSpeed(2.5f)
                        .setTransitionTime(15)
                        .build());
            }else if (repeatedTime < limit / 3 * 2){
                this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.CLOCK_WYVERN_FLYING_STATIONARY)
                        .setSpeed(2.5f)
                        .setInEasingType(EasingType.EASE_OUT)
                        .setOutFadeTime(20)
                        .setInFadeTime(15)
                        .build());
            }else{
                this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.CLOCK_WYVERN_IDLE)
                                .setInFadeTime(20)
                                .setOutFadeTime(2)
                        .build());
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {

    }

}
