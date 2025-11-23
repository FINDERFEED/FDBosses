package com.finderfeed.fdbosses.content.structures;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossStructures;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.ArrayList;
import java.util.Optional;

public class GeburahArenaStructure extends Structure {

    public static final MapCodec<GeburahArenaStructure> CODEC = RecordCodecBuilder.mapCodec(p->p.group(
            settingsCodec(p),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool)
    ).apply(p, GeburahArenaStructure::new));

    private final Holder<StructureTemplatePool> startPool;

    public GeburahArenaStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool) {
        super(settings);
        this.startPool = startPool;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext ctx) {


        var pos = this.getLowestYIn5by5BoxOffset7Blocks(ctx, Rotation.NONE);

        if (pos.getY() != -1){
            return Optional.empty();
        }


//        BlockPos blockpos = ctx.chunkPos().getMiddleBlockPosition(140);
        BlockPos blockpos = new BlockPos(pos.getX(), 140, pos.getZ());


        return JigsawPlacement.addPieces(
                ctx,
                this.startPool,
                Optional.empty(),
                20,
                blockpos,
                false,
                Optional.empty(),
                200,
                PoolAliasLookup.create(new ArrayList<>(), blockpos, ctx.seed()),
                DimensionPadding.ZERO,
                LiquidSettings.IGNORE_WATERLOGGING
        );
    }

    @Override
    public StructureType<?> type() {
        return BossStructures.GEBURAH_ARENA.get();
    }

}
