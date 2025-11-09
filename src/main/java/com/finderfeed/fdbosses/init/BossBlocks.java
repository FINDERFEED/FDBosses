package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.blocks.NoEntitySpawnBlock;
import com.finderfeed.fdbosses.content.blocks.TrophyBlock;
import com.finderfeed.fdbosses.content.tile_entities.TrophyBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Random;
import java.util.function.Supplier;

public class BossBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, FDBosses.MOD_ID);

    public static final Supplier<Block> NO_ENTITY_SPAWN_BLOCK = BLOCKS.register("no_entity_spawn_block",()->new NoEntitySpawnBlock());

    public static final Supplier<TrophyBlock> CHESED_TROPHY = BLOCKS.register("chesed_trophy",()->new TrophyBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F)
                    .instabreak()
                    .noOcclusion(),
            BossAnims.CHESED_IDLE,
            null,
            (pos,state)-> BossTileEntities.CHESED_TROPHY.get().create(pos,state)
    ));

    public static final Supplier<TrophyBlock> MALKUTH_TROPHY = BLOCKS.register("malkuth_trophy",()->new TrophyBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F)
                    .instabreak()
                    .noOcclusion(),
            BossAnims.MALKUTH_SCREEN_IDLE,
            null,
            (pos,state)-> BossTileEntities.MALKUTH_TROPHY.get().create(pos,state)
    ));


    public static final Supplier<Block> JUSTICESTONE_BRICKS = BLOCKS.register("justicestone_bricks", ()->new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final Supplier<Block> CHISELED_JUSTICESTONE_BRICKS = BLOCKS.register("chiseled_justicestone_bricks", ()->new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final Supplier<Block> JUSTICESTONE_BEAM = BLOCKS.register("justicestone_beam", ()->new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final Supplier<Block> JUSTICESTONE_COLUMN = BLOCKS.register("justicestone_column", ()->new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final Supplier<Block> JUSTICESTONE_WALL = BLOCKS.register("justicestone_wall", ()->new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL).forceSolidOn()));
    public static final Supplier<Block> JUSTICESTONE_COLUMN_WALL = BLOCKS.register("justicestone_column_wall", ()->new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL).forceSolidOn()));
    public static final Supplier<Block> JUSTICESTONE_STAIRS = BLOCKS.register("justicestone_stairs", ()->new StairBlock(JUSTICESTONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_STAIRS)));
    public static final Supplier<Block> POLISHED_JUSTICESTONE = BLOCKS.register("polished_justicestone", ()->new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));


}
