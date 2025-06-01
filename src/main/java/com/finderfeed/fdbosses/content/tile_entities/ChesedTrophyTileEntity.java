package com.finderfeed.fdbosses.content.tile_entities;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.arc_lightning.ArcLightningOptions;
import com.finderfeed.fdbosses.init.BossTileEntities;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.joml.Vector3f;

public class ChesedTrophyTileEntity extends TrophyBlockEntity {

    public ChesedTrophyTileEntity(BlockPos pos, BlockState state) {
        super(BossTileEntities.CHESED_TROPHY.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide){

            if (level.getGameTime() % 2 == 0){
                float rad = 0.5f;

                var bp = this.getBlockPos();

                float height = 0.4f;
                Vec3 basePos = new Vec3(bp.getX() + 0.5, bp.getY() + height,bp.getZ() + 0.5);
                float baseAngle = -(float) Math.toRadians(-this.getAngleFromState()) + FDMathUtil.FPI / 4;
                float randomRange = FDMathUtil.FPI * 2 - FDMathUtil.FPI / 2;
                for (int i = 0; i < 2;i++) {
                    var endOffset = new Vector3f(0, 0, rad).rotateY(baseAngle + randomRange * level.random.nextFloat()).add(0, -height, 0);
                    Vec3 end = basePos.add(endOffset.x,endOffset.y,endOffset.z);

                    level.addParticle(ArcLightningOptions.builder(BossParticles.ARC_LIGHTNING.get())
                                    .end(end.x, end.y, end.z)
                                    .lifetime(2)
                                    .color(1 + level.random.nextInt(40), 183 + level.random.nextInt(60), 165 + level.random.nextInt(60))
                                    .lightningSpread(0.05f)
                                    .width(0.05f)
                                    .segments(6)
                                    .circleOffset(level.random.nextFloat() * 0.5f - 0.5f)
                                    .build(),
                            true, basePos.x, basePos.y, basePos.z, 0, 0, 0
                    );
                }
            }

        }
    }

}
