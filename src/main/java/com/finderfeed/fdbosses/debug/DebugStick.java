package com.finderfeed.fdbosses.debug;

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

public class DebugStick extends Item {
    public DebugStick(Properties p_41383_) {
        super(p_41383_);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        if (!level.isClientSide){
//
//            for (int y = 0; y < 40;y++){
//
//                for (int z = 0; z < 100; z++){
//
//                    BlockPos pos = player.getOnPos().offset(0,y,z);
//                    BlockPos pos2 = player.getOnPos().offset(150,y,z);
//
//                    ClipContext ctx1 = new ClipContext(pos.getCenter(),pos.getCenter().add(200,0,0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
//                    ClipContext ctx2 = new ClipContext(pos2.getCenter(),pos2.getCenter().add(-200,0,0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
//
//
//                    var res1 = level.clip(ctx1);
//                    var res2 = level.clip(ctx2);
//
//                    if (res1.getType() == HitResult.Type.BLOCK){
//
//                        BlockPos resPos = res1.getBlockPos();
//
//                        if (!level.getBlockState(resPos).is(Blocks.BLACKSTONE)) {
//                            Direction direction = res1.getDirection();
//                            resPos = resPos.offset(direction.getNormal());
//                            level.setBlock(resPos,Blocks.BLACKSTONE.defaultBlockState(),Block.UPDATE_CLIENTS);
//                        }
//
//                    }
//                    if (res2.getType() == HitResult.Type.BLOCK){
//
//                        BlockPos resPos = res2.getBlockPos();
//
//                        if (!level.getBlockState(resPos).is(Blocks.BLACKSTONE)) {
//                            Direction direction = res2.getDirection();
//                            resPos = resPos.offset(direction.getNormal());
//                            level.setBlock(resPos,Blocks.BLACKSTONE.defaultBlockState(),Block.UPDATE_CLIENTS);
//                        }
//
//                    }
//
//                }
//
//            }
//
//
//
//            for (int y = 0; y < 40;y++){
//
//                for (int x = 0; x < 100; x++){
//
//                    BlockPos pos = player.getOnPos().offset(x,y,0);
//                    BlockPos pos2 = player.getOnPos().offset(x,y,150);
//
//                    ClipContext ctx1 = new ClipContext(pos.getCenter(),pos.getCenter().add(0,0,200), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
//                    ClipContext ctx2 = new ClipContext(pos2.getCenter(),pos2.getCenter().add(0,0,-200), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
//
//
//                    var res1 = level.clip(ctx1);
//                    var res2 = level.clip(ctx2);
//
//                    if (res1.getType() == HitResult.Type.BLOCK){
//
//                        BlockPos resPos = res1.getBlockPos();
//
//                        if (!level.getBlockState(resPos).is(Blocks.BLACKSTONE)) {
//                            Direction direction = res1.getDirection();
//                            resPos = resPos.offset(direction.getNormal());
//                            level.setBlock(resPos,Blocks.BLACKSTONE.defaultBlockState(),Block.UPDATE_CLIENTS);
//                        }
//
//                    }
//                    if (res2.getType() == HitResult.Type.BLOCK){
//
//                        BlockPos resPos = res2.getBlockPos();
//
//                        if (!level.getBlockState(resPos).is(Blocks.BLACKSTONE)) {
//                            Direction direction = res2.getDirection();
//                            resPos = resPos.offset(direction.getNormal());
//                            level.setBlock(resPos,Blocks.BLACKSTONE.defaultBlockState(),Block.UPDATE_CLIENTS);
//                        }
//
//                    }
//
//                }
//
//            }



//            if (true) return InteractionResultHolder.consume(player.getItemInHand(hand));
//
//            RandomSource random = level.random;
//
//            BlockPos base = player.blockPosition().below();
//
//            float maxrad = 40;
//            float radstart = 3.5f;
//            float seed = random.nextFloat() * 454353.342f;
//            System.out.println(seed);
//            float d = maxrad - radstart;
//            for (float rad = radstart; rad < maxrad;rad++){
//                float l = FDMathUtil.FPI * 2 * rad;
//                float step = 0.5f / l;
//                BlockPos prev = BlockPos.ZERO;
//                for (float i = 0; i <= l;i+=step){
//
//                    float p = i / l;
//
//                    Vec3 v = new Vec3(1,0,0).yRot(FDMathUtil.FPI * 2 * p);
//
//                    BlockPos pos = base.offset(
//                            new BlockPos(
//                                    (int) Math.floor(v.x * rad),
//                                    0,
//                                    (int) Math.floor(v.z * rad)
//                            )
//                    );
//
//                    if (!pos.equals(prev)){
//
//                        float bp = (rad - radstart) / d;
//                        float noiseFrequency = 1f;
//
//                        float borderOffset = (SimplexNoise.noise((float)v.x * noiseFrequency,seed,(float)v.z * noiseFrequency) + 1) / 2f;
//                        float sinborderOffset = (float) (Math.sin(p * FDMathUtil.FPI * 2 * 24 + seed) + 1) / 2f;
//
//                        float border = 0.4f - sinborderOffset * 0.05f;
//
////                        System.out.println(borderOffset);
//                        float sculkGenerationPercentRadius = 0.25f;
//
//                        if (bp < border){
//                           level.setBlock(pos,Blocks.DEEPSLATE.defaultBlockState(),Block.UPDATE_CLIENTS);
//                        }else{
//                            level.setBlock(pos,Blocks.BLACKSTONE.defaultBlockState(),Block.UPDATE_CLIENTS);
//                        }
//
//                        float sculkGenStart = border - sculkGenerationPercentRadius;
//                        float sculkGenEnd = border + sculkGenerationPercentRadius;
//
//                        if (bp > sculkGenStart && bp < sculkGenEnd){
//
//                            float percent = 1 - Math.abs((bp - sculkGenStart) / (sculkGenEnd - sculkGenStart) * 2 - 1);
//                            percent = FDEasings.easeOut(percent);
////                            percent = 1;
//
//                            if (random.nextFloat() < percent){
//                                level.setBlock(pos,Blocks.SCULK.defaultBlockState(),Block.UPDATE_CLIENTS);
//                            }
//
//                        }
//
//                        prev = pos;
//                    }
//
//                }
//
//            }





//            float border = 0.3f;
//
//            if (bp < border){
//                float deepslateChance = 1 - bp / border;
//                deepslateChance = FDEasings.easeInOut(FDEasings.easeOut(deepslateChance));
//                if (random.nextFloat() < deepslateChance){
//                    level.setBlock(pos,Blocks.DEEPSLATE.defaultBlockState(),Block.UPDATE_CLIENTS);
//                }else{
//                    level.setBlock(pos,Blocks.SCULK.defaultBlockState(),Block.UPDATE_CLIENTS);
//                }
//            }else{
//                float blackstoneChance = (bp - border) / (1 - border);
//                blackstoneChance = FDEasings.easeInOut(FDEasings.easeOut(blackstoneChance));
//                if (random.nextFloat() < blackstoneChance){
//                    level.setBlock(pos,Blocks.BLACKSTONE.defaultBlockState(),Block.UPDATE_CLIENTS);
//                }else{
//                    level.setBlock(pos,Blocks.SCULK.defaultBlockState(),Block.UPDATE_CLIENTS);
//                }
//            }
            BlockPos base = player.getOnPos();


            for (int x = 0; x < 200;x++){
                for (int y = 0; y < 200;y++){
                    boolean wasInterrupted = false;
                    for (int z = 0; z < 200;z++){
                        BlockPos setPos = base.offset(x,y,z);
                        BlockState state = level.getBlockState(setPos);
                        if (state.isAir() || state.is(Blocks.STRUCTURE_VOID)){
                            level.setBlock(setPos,Blocks.STRUCTURE_VOID.defaultBlockState(), Block.UPDATE_CLIENTS);
                        }else{
                            wasInterrupted = true;
                            break;
                        }
                    }
                    if (wasInterrupted) {
                        for (int z = 200; z > 0; z--) {
                            BlockPos setPos = base.offset(x, y, z);
                            BlockState state = level.getBlockState(setPos);
                            if (state.isAir()  || state.is(Blocks.STRUCTURE_VOID)) {
                                level.setBlock(setPos, Blocks.STRUCTURE_VOID.defaultBlockState(), Block.UPDATE_CLIENTS);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }



        }

        return super.use(level, player, hand);
    }
}
