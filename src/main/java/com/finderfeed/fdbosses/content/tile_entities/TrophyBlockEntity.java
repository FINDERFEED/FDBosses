package com.finderfeed.fdbosses.content.tile_entities;

import com.finderfeed.fdbosses.content.blocks.TrophyBlock;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.tile.FDBlockEntity;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TrophyBlockEntity extends FDBlockEntity {

    public TrophyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public TrophyBlock getBlock(){
        return (TrophyBlock) this.getBlockState().getBlock();
    }

    public void onClick(Player player){
        TrophyBlock trophyBlock = this.getBlock();
        var clickAnim = trophyBlock.getClickAnimation();
        var idleAnim = trophyBlock.getIdleAnimation();
        if (clickAnim != null && idleAnim != null){
            this.getAnimationSystem().startAnimation("IDLE", AnimationTicker.builder(clickAnim)
                            .setLoopMode(Animation.LoopMode.ONCE)
                            .nextAnimation(AnimationTicker.builder(idleAnim)
                                    .setLoopMode(Animation.LoopMode.LOOP)
                                    .build())
                    .build());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide){
            TrophyBlock trophyBlock = this.getBlock();
            if (this.getAnimationSystem().getTicker("IDLE") == null) {
                this.getAnimationSystem().startAnimation("IDLE", AnimationTicker.builder(trophyBlock.getIdleAnimation())
                        .setLoopMode(Animation.LoopMode.LOOP)
                        .build());
            }
        }
    }

    public int getAngleFromState(){
        BlockState state = this.getBlockState();
        var facing = state.getValue(HorizontalDirectionalBlock.FACING);
        int angle = 0;
        switch (facing){
            case SOUTH -> {
                angle = 180;
            }
            case WEST -> {
                angle = 90;
            }
            case EAST -> {
                angle = 270;
            }
        }
        return angle;
    }
}
