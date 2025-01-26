package com.finderfeed.fdbosses.debug;

import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class DebugStick extends Item {
    public DebugStick(Properties p_41383_) {
        super(p_41383_);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        if (!level.isClientSide){

            float mrad = 38f;
            Random r = new Random(65464345);

//            for (int i = -50; i <= 50;i++){
//                for (int g = -50; g <= 50;g++){
//                    for (int k = -50; k <= 50;k++){
//                        BlockPos pos = player.getOnPos().offset(i,g,k);
//                        if (level.getBlockState(pos).isAir()){
//                            level.setBlock(pos,Blocks.STRUCTURE_VOID.defaultBlockState(),3);
//                        }
//                    }
//                }
//            }


            for (int y = 0; y < 50;y++) {
                for (float rad = mrad; rad > 0; rad -= 0.5f) {
                    float angle = 0.1f / rad;
                    for (float i = 0; i <= FDMathUtil.FPI * 2; i += angle) {

                        Vec3 v = new Vec3(rad, 0, 0).yRot(i);
                        Vec3 p = new Vec3(
                                Math.floor(player.getX()) + 0.5f,
                                player.getY() + y,
                                Math.floor(player.getZ()) + 0.5f
                        );
                        Vec3 pos = p.add(
                                v.x,
                                0,
                                v.z
                        );
                        BlockPos ppos = new BlockPos(
                                (int) Math.floor(pos.x),
                                (int) Math.floor(pos.y),
                                (int) Math.floor(pos.z)
                        );
                        if (level.getBlockState(ppos).is(Blocks.STRUCTURE_VOID)){
                            level.setBlock(ppos,Blocks.AIR.defaultBlockState(),3);
                        }

//                    if (r.nextFloat() > 0.25) {
//                    if (!level.getBlockState(ppos.below()).isAir()) {
//                        if (level.random.nextFloat() > 0.3) {
//                            level.setBlock(ppos, Blocks.DEEPSLATE.defaultBlockState(), 3);
//                        } else {
//                            level.setBlock(ppos, Blocks.SCULK.defaultBlockState(), 3);
//                        }
//                    }
//                    }
//                    if (!level.getBlockState(ppos).isAir()){
//                        for (Direction direction : Direction.values()){
//                            var nrm = direction.getNormal();
//                            BlockPos pos1 = ppos.offset(nrm);
//                                if (!level.getBlockState(pos1).isAir()){
//                                    level.setBlock(pos1,Blocks.AIR.defaultBlockState(),3);
//                                }
//                        }
//                    }
//                    }else{
//                        level.setBlock(ppos, Blocks.AIR.defaultBlockState(), 3);
//
//                    }
                    }
                }
            }

            //            ChesedBlockProjectile projectile = new ChesedBlockProjectile(BossEntities.BLOCK_PROJECTILE.get(),level);
//            projectile.setPos(player.position().add(player.getLookAngle()).add(0,2,0));
//            projectile.setDeltaMovement(player.getLookAngle().multiply(1,1,1));
//
//            projectile.setRotationSpeed(20);
//            projectile.setBlockState(Blocks.GRASS_BLOCK.defaultBlockState());
//            level.addFreshEntity(projectile);

//            FlyingBlockEntity block = new FlyingBlockEntity(FDEntities.FLYING_BLOCK.get(),level);
//            block.setPos(player.position().add(player.getLookAngle()).add(0,2,0));
//            block.setDeltaMovement(player.getLookAngle());
//            block.setRotationSpeed(20);
//            block.setBlockState(Blocks.STONE.defaultBlockState());
//            level.addFreshEntity(block);
        }

        return super.use(level, player, hand);
    }
}
