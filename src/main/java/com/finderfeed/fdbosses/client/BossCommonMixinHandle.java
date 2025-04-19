package com.finderfeed.fdbosses.client;

import com.finderfeed.fdbosses.content.entities.chesed_boss.kinetic_field.ChesedKineticFieldEntity;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public class BossCommonMixinHandle {


    public static void entityCollidersMixin(Entity entity, Level level, List<VoxelShape> shapes, AABB box, CallbackInfoReturnable<List<VoxelShape>> cir, ImmutableList.Builder<VoxelShape> builder){
        if (entity instanceof Player player || entity instanceof ThrownEnderpearl){
            AABB searchBox = new AABB(-20,-20,-20,20,20,20).move(entity.position());

            var list = level.getEntitiesOfClass(ChesedKineticFieldEntity.class,searchBox);
            for (ChesedKineticFieldEntity e : list){

                VoxelShape shape = e.getCollisionShape(entity);
                builder.add(shape);

            }

        }
    }

}
