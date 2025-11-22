package com.finderfeed.fdbosses.content.entities.geburah.respawn_point_setter_block;

import com.finderfeed.fdbosses.init.BossTileEntities;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.tile.FDEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class GeburahRespiteBlock extends GlazedTerracottaBlock implements EntityBlock {

    public GeburahRespiteBlock(Properties props) {
        super(props);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult p_60508_) {

        if (player instanceof ServerPlayer serverPlayer){
            serverPlayer.setRespawnPosition(serverPlayer.level().dimension(), serverPlayer.getOnPos(), serverPlayer.getYRot(), true,true);

            serverPlayer.swing(InteractionHand.MAIN_HAND);
            return InteractionResult.SUCCESS;
        }

        return super.useWithoutItem(state, level, pos, player, p_60508_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BossTileEntities.GEBURAH_RESPITE_BLOCK.get().create(pos,state);
    }

}
