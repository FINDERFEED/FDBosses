package com.finderfeed.fdbosses.content.structures;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossStructures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.*;

import java.util.ArrayList;
import java.util.Optional;

public class MalkuthArenaStructure extends Structure {

    public static final Codec<MalkuthArenaStructure> CODEC = RecordCodecBuilder.create(p->p.group(
            settingsCodec(p),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool)
    ).apply(p, MalkuthArenaStructure::new));


    private final Holder<StructureTemplatePool> startPool;

    public MalkuthArenaStructure(StructureSettings structureSettings, Holder<StructureTemplatePool> startPool) {
        super(structureSettings);
        this.startPool = startPool;
    }


    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext ctx) {
        ChunkPos chunkpos = ctx.chunkPos();
        BlockPos blockpos = new BlockPos(chunkpos.getMinBlockX() + 8, -3, chunkpos.getMinBlockZ() + 8);
//        int maxDistanceFromCenter = 115;
        int maxDistanceFromCenter = 128;

        return JigsawPlacement.addPieces(
                ctx,
                this.startPool,
                Optional.of(FDBosses.location("malkuth_arena_part_1")),
                20,
                blockpos,
                false,
                Optional.of(Heightmap.Types.WORLD_SURFACE_WG),
                maxDistanceFromCenter
        );
//
//        return JigsawPlacement.addPieces(
//                ctx,
//                this.startPool,
//                Optional.of(FDBosses.location("malkuth_arena_part_1")),
//                20,
//                blockpos,
//                false,
//                Optional.of(Heightmap.Types.WORLD_SURFACE_WG),
//                maxDistanceFromCenter,
//                PoolAliasLookup.create(new ArrayList<>(), blockpos, ctx.seed()),
//                DimensionPadding.ZERO,
//                LiquidSettings.IGNORE_WATERLOGGING
//        );
    }

    @Override
    public StructureType<?> type() {
        return BossStructures.MALKUTH_ARENA.get();
    }


}
