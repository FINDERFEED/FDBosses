package com.finderfeed.fdbosses.debug;

import com.finderfeed.fdbosses.init.BossBlocks;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class DebugStick extends Item {
    public DebugStick(Properties p_41383_) {
        super(p_41383_);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        if (!level.isClientSide){

            BlockPos base = player.blockPosition();

            for (int x = 0; x < 200;x++){
                for (int z = 0; z < 200;z++){

                    BlockPos pos = base.offset(x,0,z);
                    BlockState state = level.getBlockState(pos);
                    BlockState stateb = level.getBlockState(pos.below());

                    if (state.isAir() && !stateb.isAir()){
                        level.setBlock(pos, BossBlocks.NO_ENTITY_SPAWN_BLOCK.get().defaultBlockState(),Block.UPDATE_CLIENTS);
                    }

                }
            }

//            for (int x = 0; x < 200;x++){
//                for (int y = 0; y < 200;y++){
//                    boolean wasInterrupted = false;
//                    for (int z = 0; z < 200;z++){
//                        BlockPos setPos = base.offset(x,y,z);
//                        BlockState state = level.getBlockState(setPos);
//                        if (state.isAir() || state.is(Blocks.STRUCTURE_VOID)){
//                            level.setBlock(setPos,Blocks.STRUCTURE_VOID.defaultBlockState(), Block.UPDATE_CLIENTS);
//                        }else{
//                            wasInterrupted = true;
//                            break;
//                        }
//                    }
//                    if (wasInterrupted) {
//                        for (int z = 200; z > 0; z--) {
//                            BlockPos setPos = base.offset(x, y, z);
//                            BlockState state = level.getBlockState(setPos);
//                            if (state.isAir()  || state.is(Blocks.STRUCTURE_VOID)) {
//                                level.setBlock(setPos, Blocks.STRUCTURE_VOID.defaultBlockState(), Block.UPDATE_CLIENTS);
//                            } else {
//                                break;
//                            }
//                        }
//                    }
//                }
//            }



        }

        return super.use(level, player, hand);
    }
}
