package com.finderfeed.fdbosses.content.entities.geburah.casts;

import com.finderfeed.fdbosses.content.entities.geburah.judgement_bird.JudgementBirdEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GeburahCastingCircleJudgementBird extends GeburahCastingCircle implements AutoSerializable {

    @SerializableField
    private Vec3 birdFlyTo;

    private AABB roostingBox;


    public static void summon(Level level, Vec3 pos, Vec3 castDirection, Vec3 birdFlyTo, AABB roostingBox){

        GeburahCastingCircleJudgementBird castCircle = new GeburahCastingCircleJudgementBird(BossEntities.GEBURAH_CASTING_CIRCLE_JUDGEMENT_BIRD.get(), level);
        castCircle.setPos(pos);
        castCircle.birdFlyTo = birdFlyTo;
        castCircle.setDirection(castDirection);
        castCircle.roostingBox = roostingBox;
        castCircle.setCastDuration(20);
        castCircle.getEntityData().set(COLOR, 0xffaaddff);
        level.addFreshEntity(castCircle);

    }

    public GeburahCastingCircleJudgementBird(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void cast() {
        if (roostingBox != null) {
            JudgementBirdEntity judgementBirdEntity = JudgementBirdEntity.summon(level(), this.position(), birdFlyTo, roostingBox);
            judgementBirdEntity.lookAt(EntityAnchorArgument.Anchor.FEET, birdFlyTo);
            judgementBirdEntity.setMoveTargetPos(birdFlyTo);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
        if (tag.contains("roostingBoxMinX")) {
            this.roostingBox = new AABB(
                    tag.getDouble("roostingBoxMinX"),
                    tag.getDouble("roostingBoxMinY"),
                    tag.getDouble("roostingBoxMinZ"),
                    tag.getDouble("roostingBoxMaxX"),
                    tag.getDouble("roostingBoxMaxY"),
                    tag.getDouble("roostingBoxMaxZ")
            );
        }
    }


    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
        if (this.roostingBox != null) {
            tag.putDouble("roostingBoxMinX", this.roostingBox.minX);
            tag.putDouble("roostingBoxMinY", this.roostingBox.minY);
            tag.putDouble("roostingBoxMinZ", this.roostingBox.minZ);
            tag.putDouble("roostingBoxMaxX", this.roostingBox.maxX);
            tag.putDouble("roostingBoxMaxY", this.roostingBox.maxY);
            tag.putDouble("roostingBoxMaxZ", this.roostingBox.maxZ);
        }
    }
}
