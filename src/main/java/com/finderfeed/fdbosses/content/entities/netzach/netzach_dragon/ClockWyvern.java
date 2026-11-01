package com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.math.EasingType;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
            int limit = 100;

            long repeatedTime = this.level().getGameTime() % limit;
            this.getAnimationSystem().startAnimation("IDLE", AnimationTicker.builder(BossAnims.CLOCK_WYVERN_SPINNING_GEARS)
                    .build());

            if (repeatedTime == 0){
                this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.CLOCK_WYVERN_SLAM_EARTH)
                        .setTransitionTime(5)
                        .nextAnimation(AnimationTicker.builder(BossAnims.CLOCK_WYVERN_IDLE)
                                        .setTransitionTime(5)
                                        .build())
                        .build());
            }else if (repeatedTime == 17){
                BossUtil.clockWyvernSlam((ServerLevel) level(), this.position());
                level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.MACE_SMASH_GROUND_HEAVY, SoundSource.HOSTILE, 4f, 1f);


                PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                        .frequency(10)
                        .amplitude(10f)
                        .inTime(0)
                        .stayTime(0)
                        .outTime(10)
                        .build(),this.position(),40);

            }

        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {

    }

}
