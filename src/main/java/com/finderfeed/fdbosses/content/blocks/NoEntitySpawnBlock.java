package com.finderfeed.fdbosses.content.blocks;

import com.finderfeed.fdbosses.BossClientPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.util.thread.EffectiveSide;

public class NoEntitySpawnBlock extends Block {

    public static final VoxelShape SHAPE = Block.box(0,0,0,16,2,16);

    public NoEntitySpawnBlock() {
        super(
                Properties.ofFullCopy(Blocks.BEDROCK)
                        .noCollission()
                        .isValidSpawn((state,getter,pos,type)->false)
                        .noOcclusion()
                        .dynamicShape()
        );
    }


    @Override
    protected VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        if (EffectiveSide.get().isClient()){
            Player player = BossClientPackets.getClientPlayer();
            if (player != null && player.level().isClientSide && player.isCreative()){
                return SHAPE;
            }
        }

        return Shapes.empty();
    }

    @Override
    protected RenderShape getRenderShape(BlockState p_60550_) {
        return RenderShape.INVISIBLE;
    }
}
