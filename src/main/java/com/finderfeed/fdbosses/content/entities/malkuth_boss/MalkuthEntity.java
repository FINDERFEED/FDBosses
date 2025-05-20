package com.finderfeed.fdbosses.content.entities.malkuth_boss;

import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDLivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class MalkuthEntity extends FDLivingEntity {
    public MalkuthEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }


}
