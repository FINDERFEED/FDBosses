package com.finderfeed.fdbosses.mixin;

import com.finderfeed.fdbosses.BossMixinHandler;
import com.finderfeed.fdbosses.content.structures.malkuth.MalkuthStructureBoundingBox;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Beardifier.class)
public class BeardifierMixin {

    @Inject(method = "forStructuresInChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/StructureManager;startsForStructure(Lnet/minecraft/world/level/ChunkPos;Ljava/util/function/Predicate;)Ljava/util/List;"))
    private static void forStructuresInChunk(StructureManager structureManager, ChunkPos chunkPos, CallbackInfoReturnable<Beardifier> cir,
                                             @Local(ordinal = 0) ObjectList<Beardifier.Rigid> beardifiers, @Local(ordinal = 1) ObjectList<JigsawJunction> jigsawJunctions){

        var starts = structureManager.startsForStructure(chunkPos, (structure)->true);

        for (var structureStart : starts){
            BossMixinHandler.addBeardifiersToStructureManually(structureManager, chunkPos, beardifiers, structureStart);
        }


    }

    @Inject(method = "compute",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/Beardifier$Rigid;terrainAdjustment()Lnet/minecraft/world/level/levelgen/structure/TerrainAdjustment;", ordinal = 1,
     shift = At.Shift.AFTER))
    public void compute(DensityFunction.FunctionContext ctx, CallbackInfoReturnable<Double> cir,
                        @Local Beardifier.Rigid beardifier,
                        @Local(ordinal = 0) LocalDoubleRef d0){

        int i = ctx.blockX();
        int j = ctx.blockY();
        int k = ctx.blockZ();


        BoundingBox boundingbox = beardifier.box();
        if (boundingbox instanceof MalkuthStructureBoundingBox) {
            int l = beardifier.groundLevelDelta();
            int i1 = Math.max(0, Math.max(boundingbox.minX() - i, i - boundingbox.maxX()));
            int j1 = Math.max(0, Math.max(boundingbox.minZ() - k, k - boundingbox.maxZ()));
            int k1 = boundingbox.minY() + l;
            int l1 = j - k1;

            int i2 = Math.max(0, Math.max(boundingbox.minY() - j, j - boundingbox.maxY()));

            double addition = getMyBuryContribution((double) i1 / 2.0, (double) i2 / 2.0, (double) j1 / 2.0) * 0.8;
            d0.set(d0.get() + addition);
        }

    }

    private static double getMyBuryContribution(double p_340947_, double p_340921_, double p_341266_) {
        double d0 = Mth.length(p_340947_, p_340921_, p_341266_);
        return Mth.clampedMap(d0, 0.0, 6.0, 1.0, 0.0);
    }

}
