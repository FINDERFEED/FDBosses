package com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon;

import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ClockWyvern extends FDEntity {

    public static FDModel clientModel;

    public ClockWyvern(EntityType<?> type, Level level) {
        super(type, level);
        if (clientModel == null){
            clientModel = new FDModel(BossModels.CLOCK_WYVERN.get());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.CLOCK_WYVERN_FLYING)
                    .setToNullTransitionTime(20)
                    .build());
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {

    }

}
