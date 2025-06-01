package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.blocks.NoEntitySpawnBlock;
import com.finderfeed.fdbosses.content.blocks.TrophyBlock;
import com.finderfeed.fdbosses.content.tile_entities.TrophyBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, FDBosses.MOD_ID);

    public static final Supplier<Block> NO_ENTITY_SPAWN_BLOCK = BLOCKS.register("no_entity_spawn_block",()->new NoEntitySpawnBlock());

    public static final Supplier<TrophyBlock> CHESED_TROPHY = BLOCKS.register("chesed_trophy",()->new TrophyBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F)
                    .instabreak()
                    .noOcclusion(),
            BossAnims.CHESED_IDLE,
            BossAnims.CHESED_ATTACK,
            (pos,state)-> BossTileEntities.CHESED_TROPHY.get().create(pos,state)
    ));

}
