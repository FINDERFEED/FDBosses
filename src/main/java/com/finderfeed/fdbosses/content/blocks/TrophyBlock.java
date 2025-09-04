package com.finderfeed.fdbosses.content.blocks;

import com.finderfeed.fdbosses.content.tile_entities.TrophyBlockEntity;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.tile.FDEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class TrophyBlock extends FDEntityBlock {

    private Supplier<Animation> idleAnimation;
    private Supplier<Animation> clickAnimation;
    private BlockEntityType.BlockEntitySupplier<?> tileEntitySupplier;

    public TrophyBlock(Properties block, Supplier<Animation> idleAnimation,  Supplier<Animation> clickAnimation, BlockEntityType.BlockEntitySupplier<? extends TrophyBlockEntity> tileEntitySupplier) {
        super(block);
        this.idleAnimation = idleAnimation;
        this.clickAnimation = clickAnimation;
        this.tileEntitySupplier = tileEntitySupplier;
        this.registerDefaultState(this.getStateDefinition().any().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
    }

    public Supplier<Animation> getIdleAnimation() {
        return idleAnimation;
    }

    public Supplier<Animation> getClickAnimation() {
        return clickAnimation;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level instanceof ServerLevel serverLevel){
            BlockEntity block = serverLevel.getBlockEntity(pos);
            if (block instanceof TrophyBlockEntity trophy){
                trophy.onClick(player);
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HorizontalDirectionalBlock.FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockpos, BlockState state) {
        return tileEntitySupplier.create(blockpos,state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState p_60550_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, ctx.getHorizontalDirection());
    }
}
