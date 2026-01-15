package com.finderfeed.fdbosses.content.tile_entities;

import com.finderfeed.fdbosses.init.BossTileEntities;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class GeburahTrophyBlockEntity extends TrophyBlockEntity{

    public GeburahTrophyBlockEntity(BlockPos pos, BlockState state) {
        super(BossTileEntities.GEBURAH_TROPHY.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide){
            if (level.getGameTime() % 3 == 0) {
                BallParticleOptions ballParticle = BallParticleOptions.builder()
                        .color(1f,0.8f, 0.3f)
                        .scalingOptions(0, 0, 20)
                        .brightness(1)
                        .size(0.225f)
                        .build();
                Vec3 corePos = this.getBlockPos().getCenter().add(0,1.65,0);
                level.addParticle(ballParticle, true, corePos.x, corePos.y, corePos.z, 0, 0, 0);
            }
        }
    }

}
