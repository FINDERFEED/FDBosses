package com.finderfeed.fdbosses.mixin;

import com.finderfeed.fdbosses.BossMixinHandler;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
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

}
