package com.finderfeed.fdbosses.content.entities.netzach.sector_attack;

import com.finderfeed.fdbosses.content.entities.FDOwnableEntity;
import com.finderfeed.fdlib.systems.shapes.FD2DShape;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class SectorAttack extends FDOwnableEntity {

    protected List<FD2DShape> currentAttackShapes = new ArrayList<>();

    protected List<FD2DShape> triangulated = new ArrayList<>();

    public SectorAttack(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide){

        }

    }




}
