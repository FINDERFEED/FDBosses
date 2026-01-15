package com.finderfeed.fdbosses.content.entities.geburah.respawn_point_setter_block;

import com.finderfeed.fdbosses.init.BossTileEntities;
import com.finderfeed.fdlib.data_structures.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class GeburahRespiteBlock extends GlazedTerracottaBlock implements EntityBlock {

    public static final String DATA_NAME = "geburahRespawnData";

    public GeburahRespiteBlock(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult p_60508_) {
        if (player instanceof ServerPlayer serverPlayer && hand == InteractionHand.MAIN_HAND){
            putSpecialRespawnPoint(serverPlayer, serverPlayer.level().dimension(), serverPlayer.getOnPos().above());
            serverPlayer.sendSystemMessage(Component.translatable("fdbosses.word.set_special_respawn_point"));
            serverPlayer.swing(InteractionHand.MAIN_HAND);
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, p_60508_);
    }

    public static CompoundTag getRespawnData(ServerPlayer serverPlayer){
        var perdata = serverPlayer.getPersistentData();
        if (perdata.contains(DATA_NAME)){
            return perdata.getCompound(DATA_NAME);
        }else{
            return null;
        }
    }

    public static void putRespawnData(ServerPlayer serverPlayer, CompoundTag tag){
        var perdata = serverPlayer.getPersistentData();
        perdata.put(DATA_NAME, tag);
    }

    public static void putSpecialRespawnPoint(ServerPlayer serverPlayer, ResourceKey<Level> dimension, BlockPos pos){
        var perdata = serverPlayer.getPersistentData();
        if (!perdata.contains(DATA_NAME)){
            CompoundTag tag = new CompoundTag();
            perdata.put(DATA_NAME, tag);
        }

        CompoundTag tag = perdata.getCompound(DATA_NAME);
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        tag.putString("dimension",dimension.location().toString());
    }

    public static Pair<ResourceKey<Level>, BlockPos> getSpecialRespawnPoint(ServerPlayer serverPlayer){
        var perdata = serverPlayer.getPersistentData();
        if (!perdata.contains(DATA_NAME)) return null;

        var data = perdata.getCompound(DATA_NAME);
        int x = data.getInt("x");
        int y = data.getInt("y");
        int z = data.getInt("z");
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(data.getString("dimension")));
        return new Pair<>(dimension, new BlockPos(x,y,z));
    }



    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BossTileEntities.GEBURAH_RESPITE_BLOCK.get().create(pos,state);
    }

}
