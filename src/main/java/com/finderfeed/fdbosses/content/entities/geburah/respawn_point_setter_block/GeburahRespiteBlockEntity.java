package com.finderfeed.fdbosses.content.entities.geburah.respawn_point_setter_block;

import com.finderfeed.fdbosses.init.BossTileEntities;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.tile.FDBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GeburahRespiteBlockEntity extends BlockEntity {

    public GeburahRespiteBlockEntity(BlockPos pos, BlockState state) {
        super(BossTileEntities.GEBURAH_RESPITE_BLOCK.get(), pos, state);
    }

}
