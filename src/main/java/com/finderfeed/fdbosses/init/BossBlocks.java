package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.blocks.NoEntitySpawnBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, FDBosses.MOD_ID);

    public static final Supplier<Block> NO_ENTITY_SPAWN_BLOCK = BLOCKS.register("no_entity_spawn_block",()->new NoEntitySpawnBlock());

}
