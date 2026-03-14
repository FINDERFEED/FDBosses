package com.finderfeed.fdbosses.content.entities.netzach.sector_attack;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.OwnableEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.shapes.FD2DShape;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectorAttack extends OwnableEntity implements AutoSerializable {

    public static final EntityDataAccessor<Boolean> FOLLOWING_OWNER = SynchedEntityData.defineId(SectorAttack.class, EntityDataSerializers.BOOLEAN);

    @SerializableField
    private String attackShapeId;

    @SerializableField
    private int timeUntilAttack;

    private int clientVisualsTick = 0;



    protected List<FD2DShape> triangulatedForRendering = new ArrayList<>();

    protected List<Vector2f> attackVisualOffsets = null;

    private Vec3 ownerPos;


    public static void summon(LivingEntity target, String shapeId, int attackTime){
        SectorAttack sectorAttack = new SectorAttack(BossEntities.SECTOR_ATTACK.get(), target.level());
        sectorAttack.setOwner(target);
        sectorAttack.attackShapeId = shapeId;
        sectorAttack.setPos(target.position());
        sectorAttack.timeUntilAttack = attackTime;
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

            if (timeUntilAttack == 0){
                this.setFollowingOwner(false);
            }

            timeUntilAttack = Mth.clamp(timeUntilAttack - 1, 0, Integer.MAX_VALUE);
        }

        if (this.isFollowingOwner()){
            var owner = this.getOwner();
            if (owner != null){
                this.ownerPos = owner.position();
            }
        }else{
            clientVisualsTick++;
        }

    }

    public List<Vector2f> getAttackVisualOffsets() {
        if (this.attackVisualOffsets == null){
            var shape = this.getAttackShape();
            if (shape == null){
                return new ArrayList<>();
            }

            this.attackVisualOffsets = new ArrayList<>();

            float minX = shape.getMinBoundX();
            float minZ = shape.getMinBoundZ();

            float maxX = shape.getMaxBoundX();
            float maxZ = shape.getMaxBoundZ();

            float minStep = 2.5f;
            float stepRandom = 1f;

            for (float x = minX; x <= maxX; x += minStep + random.nextFloat() * stepRandom){
                for (float z = minZ; z <= maxZ; z += minStep + random.nextFloat() * stepRandom){
                    Vector2f point = new Vector2f(x,z);

                    boolean inShape = false;

                    for (var s : shape.getShapes()){
                        if (BossUtil.isPointIn2dShape(s, point)){
                            inShape = true;
                            break;
                        }
                    }

                    if (inShape){
                        this.attackVisualOffsets.add(point);
                    }

                }
            }

        }
        return attackVisualOffsets;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (accessor == FOLLOWING_OWNER){
            var owner = this.getOwner();
            if (owner != null){
                this.ownerPos = owner.position();
            }
        }
    }

    public Vec3 getOwnerPos() {
        if (ownerPos == null){
            var owner = this.getOwner();
            if (owner != null){
                ownerPos = owner.position();
            }
        }
        return ownerPos;
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

    public void setFollowingOwner(boolean state){
        this.entityData.set(FOLLOWING_OWNER, state);
    }

    public boolean isFollowingOwner(){
        return this.entityData.get(FOLLOWING_OWNER);
    }

    public void sync(SectorAttackSyncPacket sectorAttackSyncPacket) {

        var shapeId = sectorAttackSyncPacket.shapeId;
        var sectorShape = ShapesRegistry.SHAPES.get(shapeId);
        triangulatedForRendering.clear();

        this.attackShapeId = shapeId;

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
        if (tag.contains("followingOwner")){
            this.setFollowingOwner(tag.getBoolean("followingOwner"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
        tag.putBoolean("followingOwner", this.isFollowingOwner());
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

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FOLLOWING_OWNER, true);
    }

    public static class ShapesRegistry {

        public static final String SIMPLE_CHECKERBOARD_2_ID = "SIMPLE_CHECKERBOARD";
        public static final String SIMPLE_TWO_SECTORS_ID = "SIMPLE_TWO_SECTORS";

        public static final HashMap<String, SectorAttackShape> SHAPES = new HashMap<>();

        public static final SectorAttackShape SIMPLE_CHECKERBOARD_2 = register(SIMPLE_CHECKERBOARD_2_ID, new SectorAttackShape()
                .addSimpleSquareCheckerboard(2f,7, 0)
        );

        public static final SectorAttackShape SIMPLE_TWO_SECTORS = register(SIMPLE_TWO_SECTORS_ID, new SectorAttackShape()
                .addSector(2,14,FDMathUtil.FPI / 4, 0)
                .addSector(2,14,FDMathUtil.FPI / 4, FDMathUtil.FPI)
        );

        public static final SectorAttackShape TEST = register("test", new SectorAttackShape()
                .addSector(2,14,FDMathUtil.FPI / 4, 0)
                .addSector(2,14,FDMathUtil.FPI / 4, FDMathUtil.FPI)
                .addTriangle(10,0,6,8,FDMathUtil.FPI / 2)
                .addSquare(15,0,4,-FDMathUtil.FPI / 2)
        );

        public static SectorAttackShape register(String name, SectorAttackShape shape){
            SHAPES.put(name, shape);
            return shape;
        }

    }

}
