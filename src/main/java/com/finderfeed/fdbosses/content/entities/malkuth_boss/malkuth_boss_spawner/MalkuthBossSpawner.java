package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_boss_spawner;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerContextAssignable;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.init.BossEffects;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;

public class MalkuthBossSpawner extends BossSpawnerEntity {

    public MalkuthBossSpawner(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide){
            this.setActive(true);

            for (var player : BossTargetFinder.getEntitiesInArc(Player.class, level(), this.position().add(0,-2,0), new Vec2(0,-1),
                    FDMathUtil.FPI, MalkuthEntity.ENRAGE_HEIGHT + 2, MalkuthEntity.ENRAGE_RADIUS)){

                if (!this.isActive()) {
                    if (!player.hasEffect(BossEffects.MARK_OF_A_KNIGHT)) {
                        player.addEffect(new MobEffectInstance(BossEffects.MARK_OF_A_KNIGHT, -1, 0, true, false));
                    }
                }else{
                    if (player.hasEffect(BossEffects.MARK_OF_A_KNIGHT)) {
                        player.removeEffect(BossEffects.MARK_OF_A_KNIGHT);
                    }
                    if (player.hasEffect(BossEffects.MARK_OF_A_COWARD)) {
                        player.removeEffect(BossEffects.MARK_OF_A_COWARD);
                    }
                }

            }



        }

    }

    @Override
    public EntityType<? extends BossSpawnerContextAssignable> getBossEntityType() {
        return BossEntities.MALKUTH.get();
    }

    @Override
    public boolean canInteractWithBlockPos(BlockPos blockPos) {
        return true;
    }

    @Override
    public Component onArenaDestructionMessage() {
        return null;
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        return true;
    }
}
