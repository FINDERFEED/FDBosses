package com.finderfeed.fdbosses.content.entities.chesed_boss;

import com.finderfeed.fdbosses.content.entities.base.BossSpawnerContextAssignable;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.systems.particle.CircleParticleProcessor;
import com.finderfeed.fdlib.systems.particle.CompositeParticleProcessor;
import com.finderfeed.fdlib.systems.particle.SetParticleSpeedProcessor;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.client.particles.lightning_particle.LightningParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ChesedBossSpawner extends BossSpawnerEntity {

    public ChesedBossSpawner(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide){


            BlockPos pos = this.getOnPos().above();
            if (this.isActive()){
                BlockState state;
                if ((state = level().getBlockState(pos)).isAir() && !state.is(Blocks.LIGHT)){
                    level().setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL,15), Block.UPDATE_ALL);
                }
            }else{
                if (level().getBlockState(pos).getBlock() == Blocks.LIGHT){
                    level().setBlock(pos,Blocks.AIR.defaultBlockState(),Block.UPDATE_ALL);
                }
            }

        }else if (level().isClientSide && this.isActive()){

            Vec3 add = new Vec3(0.5 + 0.5 * random.nextFloat(),0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat()).add(0,0.75,0);
            float lightningAdditiveLength = 1 - random.nextFloat();
            Vec3 add2 = new Vec3(0.75 + lightningAdditiveLength * lightningAdditiveLength * 0.25f,0,0)
                    .yRot(FDMathUtil.FPI * 2 * random.nextFloat()).add(0,0.75 + random.nextFloat() * 0.25 - 0.125f + lightningAdditiveLength * 0.5f,0);

            Vec3 rnd = this.position().add(add);
            Vec3 rnd2 = this.position().add(add2);

            double l = add.multiply(1,0,1).length() * 1.5f; l *= l;

            level().addParticle(BallParticleOptions.builder()
                    .size(0.15f)
                    .color(100 + random.nextInt(50), 255, 255)
                    .scalingOptions(10,0,30)
                    .build(),true,rnd.x,rnd.y,rnd.z,0,(0.025f + random.nextFloat() * 0.025f) / Math.max(1,l),0);

            if (level().getGameTime() % 10 == 0) {
                level().addParticle(LightningParticleOptions.builder()
                        .color(60, 200 + random.nextInt(50), 255)
                        .lifetime(10)
                        .maxLightningSegments(3)
                        .randomRoll(true)
                        .build(), true, rnd2.x, rnd2.y, rnd2.z, 0, 0, 0);
            }

        }
    }

    @Override
    public EntityType<? extends BossSpawnerContextAssignable> getBossEntityType() {
        return BossEntities.CHESED.get();
    }

    @Override
    public void setActive(boolean state) {
        super.setActive(state);
    }

    @Override
    public boolean canInteractWithBlockPos(BlockPos blockPos) {

        Vec3 v = blockPos.getCenter();
        Vec3 pos = this.position();

        double yDiff = v.y - pos.y;

        var hdist = v.multiply(1,0,1).distanceTo(pos.multiply(1,0,1));


        if (hdist < ChesedEntity.ARENA_RADIUS && yDiff > -2 && yDiff < ChesedEntity.ARENA_HEIGHT){
            return false;
        }

        return true;
    }
}
