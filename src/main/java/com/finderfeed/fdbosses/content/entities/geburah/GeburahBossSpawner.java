package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.content.entities.base.BossSpawnerContextAssignable;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.systems.particle.CircleParticleProcessor;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.client.particles.lightning_particle.LightningParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GeburahBossSpawner extends BossSpawnerEntity {

    public GeburahBossSpawner(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide){
            if (this.isActive()){

                Vec3 add = new Vec3(0.5 + 0.5 * random.nextFloat(),0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());

                Vec3 rnd = this.position().add(add);
                double l = add.multiply(1,0,1).length() * 1.5f; l *= l;

                level().addParticle(BallParticleOptions.builder()
                        .size(0.15f)
                        .color(100 + random.nextInt(50), 255, 255)
                        .scalingOptions(10,0,30)
                        .build(),true,rnd.x,rnd.y,rnd.z,0,(0.025f + random.nextFloat() * 0.025f) / Math.max(1,l),0);


            }
        }
    }

    @Override
    public EntityType<? extends BossSpawnerContextAssignable> getBossEntityType() {
        return BossEntities.GEBURAH.get();
    }

    @Override
    public Vec3 getPlayerItemsDropPosition(Vec3 deathPosition) {
        return deathPosition;
    }

    @Override
    public boolean canInteractWithBlockPos(BlockPos blockPos) {
        return true;
    }

    @Override
    public Component onArenaDestructionMessage() {
        return Component.translatable("fdbosses.word.tried_to_break_arena");
    }
}
