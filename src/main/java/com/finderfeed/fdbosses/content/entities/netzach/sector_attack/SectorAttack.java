package com.finderfeed.fdbosses.content.entities.netzach.sector_attack;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.FDOwnableEntity;
import com.finderfeed.fdbosses.content.entities.OwnableEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.shapes.FD2DShape;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectorAttack extends OwnableEntity implements AutoSerializable {

    @SerializableField
    private String attackShapeId;

    protected List<FD2DShape> triangulatedForRendering = new ArrayList<>();

    public static void summon(LivingEntity target, String shapeId){
        SectorAttack sectorAttack = new SectorAttack(BossEntities.SECTOR_ATTACK.get(), target.level());
        sectorAttack.setOwner(target);
        sectorAttack.attackShapeId = shapeId;
        sectorAttack.setPos(target.position());
        target.level().addFreshEntity(sectorAttack);
    }

    public SectorAttack(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide){
            if (tickCount % 10 == 0 && !ShapesRegistry.SHAPES.containsKey(this.getAttackShapeId())){
                this.remove(RemovalReason.DISCARDED);
            }
        }

    }

    public String getAttackShapeId() {
        return attackShapeId;
    }

    public SectorAttackShape getAttackShape(){
        if (attackShapeId != null){
            return ShapesRegistry.SHAPES.get(attackShapeId);
        }
        return null;
    }

    public void sync(SectorAttackSyncPacket sectorAttackSyncPacket) {

        var shapeId = sectorAttackSyncPacket.shapeId;
        var sectorShape = ShapesRegistry.SHAPES.get(shapeId);
        triangulatedForRendering.clear();

        if (sectorShape == null) return;
        var shapes = sectorShape.getShapes();

        for (var shape : shapes){
            var splitShapes = BossUtil.splitShapeToTriangles(shape);
            this.triangulatedForRendering.addAll(splitShapes);
        }

    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (player == this.getOwner()) {
            PacketDistributor.sendToPlayer(player, new SectorAttackSyncPacket(this));
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer p_20174_) {
        super.stopSeenByPlayer(p_20174_);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
    }

    @Override
    public boolean syncsOwner() {
        return true;
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        return true;
    }

    public static class ShapesRegistry {

        public static final String SIMPLE_CHECKERBOARD_2_ID = "SIMPLE_CHECKERBOARD";
        public static final String SIMPLE_TWO_SECTORS_ID = "SIMPLE_TWO_SECTORS";

        public static final HashMap<String, SectorAttackShape> SHAPES = new HashMap<>();

        public static final SectorAttackShape SIMPLE_CHECKERBOARD_2 = register(SIMPLE_CHECKERBOARD_2_ID, new SectorAttackShape()
                .addSimpleSquareCheckerboard(2f,7, 0)
        );

        public static final SectorAttackShape SIMPLE_TWO_SECTORS = register(SIMPLE_TWO_SECTORS_ID, new SectorAttackShape()
                .addSector(14,FDMathUtil.FPI / 4, 0)
                .addSector(14,FDMathUtil.FPI / 4, FDMathUtil.FPI)
        );

        public static SectorAttackShape register(String name, SectorAttackShape shape){
            SHAPES.put(name, shape);
            return shape;
        }

    }

}
