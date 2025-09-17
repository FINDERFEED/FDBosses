package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthDamageSource;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthWeaknessHandler;
import com.finderfeed.fdbosses.content.structures.MalkuthArenaStructure;
import com.finderfeed.fdbosses.content.structures.malkuth.MalkuthStructureBoundingBox;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

public class BossMixinHandler {
//
//    public static void actuallyHurtPlayerMixin(DamageSource source, Player player){
//        LivingEntityAccessor accessor = (LivingEntityAccessor) player;
//        var damageContainers = accessor.getDamageContainers();
//        DamageContainer damageContainer = damageContainers.peek();
//        float newDamage = damageContainer.getNewDamage();
//
//        Level level = player.level();
//
//        if (!level.isClientSide && source instanceof MalkuthDamageSource damageSource) {
//
//            int malkuthDamageAmount = damageSource.getMalkuthAttackAmount();
//            MalkuthAttackType malkuthAttackType = damageSource.getMalkuthAttackType();
//
//            if (!MalkuthWeaknessHandler.isWeakTo(player, malkuthAttackType)) {
//                float damage = newDamage;
//                float reduction = 1 - BossConfigs.BOSS_CONFIG.get().malkuthConfig.nonWeakToDamageReduction / 100f;
//               damageContainer.setNewDamage(damage * reduction);
//            }
//
//            MalkuthWeaknessHandler.damageWeakness(malkuthAttackType, player, malkuthDamageAmount);
//
//        }
//
//    }

    public static void computeMixin(DensityFunction.FunctionContext p_208200_, CallbackInfoReturnable<Double> cir, LocalDoubleRef d0){

    }

    public static boolean addBeardifiersToStructureManually(StructureManager structureManager, ChunkPos chunkPos, ObjectList<Beardifier.Rigid> beardifiers, StructureStart structureStart){

        Structure structure = structureStart.getStructure();

        if (structure instanceof MalkuthArenaStructure malkuthArenaStructure){

            var bb = structureStart.getBoundingBox();

            var center = bb.getCenter();

            BlockPos startPos = new BlockPos(center.getX(),bb.minY(),center.getZ());

            int centerBBRadius = 60;
            int height = 40;



            BoundingBox centerBB = new MalkuthStructureBoundingBox(new BoundingBox(
                    -centerBBRadius,-height,-centerBBRadius,
                    centerBBRadius,-6,centerBBRadius
            ).moved(startPos.getX(),startPos.getY(),startPos.getZ()));


            if (boxIntersectsChunk(centerBB, chunkPos, 6)) {
                beardifiers.add(new Beardifier.Rigid(centerBB, TerrainAdjustment.NONE, 0));
            }

            int otherBoxRadius = 13;

            addMalkuthArenaBeardifiersInRadius(chunkPos, startPos, structureManager, beardifiers, centerBBRadius + otherBoxRadius - 1, otherBoxRadius, 6, 32, height);

            return true;
        }

        return false;
    }

    private static boolean boxIntersectsChunk(BoundingBox box, ChunkPos chunkPos, int bigger){
        return box.intersects(chunkPos.getMinBlockX() - bigger, chunkPos.getMinBlockZ() - bigger, chunkPos.getMaxBlockX() + bigger, chunkPos.getMaxBlockZ() + bigger);
    }

    private static void addMalkuthArenaBeardifiersInRadius(ChunkPos chunkPos, BlockPos startPos, StructureManager structureManager, ObjectList<Beardifier.Rigid> beardifiers, int startRadius, int boxRadius, int verticalOffset, int count, int height){

        float angle = 2 * FDMathUtil.FPI / count;

        for (int i = 0; i < count; i++){

            Vec3 offset = new Vec3(startRadius,0,0).yRot(angle * i);

            BlockPos pos = startPos.offset(
                    (int) Math.floor(offset.x),
                    (int) Math.floor(offset.y),
                    (int) Math.floor(offset.z)
            );

            BoundingBox box = new MalkuthStructureBoundingBox(new BoundingBox(-boxRadius, -height, -boxRadius, boxRadius, -verticalOffset, boxRadius)
                    .moved(pos.getX(), pos.getY(), pos.getZ()));

            if (boxIntersectsChunk(box, chunkPos, 6)) {
                beardifiers.add(new Beardifier.Rigid(box, TerrainAdjustment.NONE, 0));
            }

        }
    }

    public static Rotation getRotationForStructure(Optional<ResourceLocation> location){

        if (location.isPresent()){
            if (location.get().equals(FDBosses.location("malkuth_arena_part_1"))){
                return Rotation.NONE;
            }
        }

        return null;
    }



}
