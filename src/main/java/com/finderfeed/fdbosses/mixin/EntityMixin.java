package com.finderfeed.fdbosses.mixin;

import com.finderfeed.fdbosses.client.BossCommonMixinHandle;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public class EntityMixin {




    @Inject(method = "collideBoundingBox",at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;addAll(Ljava/lang/Iterable;)Lcom/google/common/collect/ImmutableList$Builder;", ordinal = 1))
    private static void collectColliders(Entity entity, Vec3 smth, AABB box, Level level, List<VoxelShape> shapes, CallbackInfoReturnable<Vec3> cir, @Local ImmutableList.Builder<VoxelShape> builder){
        BossCommonMixinHandle.entityCollidersMixin(entity, level, shapes, box.expandTowards(smth), cir, builder);
    }

}
