package com.finderfeed.fdbosses.content.tile_entities;

import com.finderfeed.fdbosses.init.BossTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MalkuthTrophyBlockEntity extends TrophyBlockEntity {

    public MalkuthTrophyBlockEntity(BlockPos pos, BlockState state) {
        super(BossTileEntities.MALKUTH_TROPHY.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
    }

}
