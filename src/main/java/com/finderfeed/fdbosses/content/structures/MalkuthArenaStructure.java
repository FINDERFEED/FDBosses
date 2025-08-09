package com.finderfeed.fdbosses.content.structures;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossStructures;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.*;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MalkuthArenaStructure extends Structure {

    public static final MapCodec<MalkuthArenaStructure> CODEC = RecordCodecBuilder.mapCodec(p->p.group(
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
        BlockPos blockpos = new BlockPos(chunkpos.getMinBlockX() + 8, 4, chunkpos.getMinBlockZ() + 8);
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
                maxDistanceFromCenter,
                PoolAliasLookup.create(new ArrayList<>(), blockpos, ctx.seed()),
                DimensionPadding.ZERO,
                LiquidSettings.IGNORE_WATERLOGGING
        );
    }

    @Override
    public StructureType<?> type() {
        return BossStructures.MALKUTH_ARENA.get();
    }


}
