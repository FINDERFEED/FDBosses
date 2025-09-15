package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.tile_entities.ChesedTrophyTileEntity;
import com.finderfeed.fdbosses.content.tile_entities.MalkuthTrophyBlockEntity;
import com.finderfeed.fdbosses.content.tile_entities.TrophyBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossTileEntities {

    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, FDBosses.MOD_ID);

    public static final Supplier<BlockEntityType<? extends TrophyBlockEntity>> CHESED_TROPHY = TILE_ENTITIES.register("chesed_trophy_tile_entity",()->BlockEntityType.Builder.<ChesedTrophyTileEntity>of(
            ChesedTrophyTileEntity::new,BossBlocks.CHESED_TROPHY.get()
    ).build(null));

    public static final Supplier<BlockEntityType<? extends TrophyBlockEntity>> MALKUTH_TROPHY = TILE_ENTITIES.register("malkuth_trophy_tile_entity",()->BlockEntityType.Builder.<MalkuthTrophyBlockEntity>of(
            MalkuthTrophyBlockEntity::new,BossBlocks.MALKUTH_TROPHY.get()
    ).build(null));

}
